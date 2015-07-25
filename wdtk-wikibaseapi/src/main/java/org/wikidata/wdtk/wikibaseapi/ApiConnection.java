package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class to build up and hold a connection to a Wikibase API, managing cookies
 * and login.
 * 
 * @author Michael Guenther
 * 
 */
public class ApiConnection {

	static final Logger logger = LoggerFactory.getLogger(ApiConnection.class);

	final static String wikidataApiUrl = "https://www.wikidata.org/w/api.php/";
	final static String testWikidataApiUrl = "https://test.wikidata.org/w/api.php";

	final static String PARAM_COOKIE = "Cookie";
	final static String HEADER_FIELD_SET_COOKIE = "Set-Cookie";

	/**
	 * String value in the result fied of the json response if the login was
	 * succesfull.
	 */
	final static String LOGIN_RESULT_SUCCESS = "Success";
	/**
	 * Wrong Password.
	 */
	final static String LOGIN_WRONG_PASS = "WrongPass";
	/**
	 * Wrong Password. An authentication plugin rejected the password.
	 */
	final static String LOGIN_WRONG_PLUGIN_PASS = "WrongPluginPass";
	/**
	 * No Username.
	 */
	final static String LOGIN_No_Name = "NoName";
	/**
	 * Username does not exist.
	 */
	final static String LOGIN_NOT_EXISTS = "NotExists";
	/**
	 * Username is illegal.
	 */
	final static String LOGIN_ILLEGAL = "Illegal";
	/**
	 * There were too many logins in a short time.
	 */
	final static String LOGIN_THROTTLED = "Throttled";
	/**
	 * Password is empty
	 */
	final static String LOGIN_EMPTY_PASS = "EmptyPass";
	/**
	 * The wiki tried to automatically create a new account for you, but your IP
	 * address has been blocked from account creation.
	 */
	final static String LOGIN_CREATE_BLOCKED = "CreateBlocked";
	/**
	 * The User is blocked.
	 */
	final static String LOGIN_BLOCKED = "Blocked";
	/**
	 * Token or Session ID is missing.
	 */
	final static String LOGIN_NEEDTOKEN = "NeedToken";
	/**
	 * Token is wrong.
	 */
	final static String LOGIN_WRONG_TOKEN = "WrongToken";

	/**
	 * base URL to the Wikibase API.
	 */
	final String apiBaseUrl;

	// login data
	boolean loggedIn = false;
	String username = "";
	String password = "";

	final Map<String, String> cookies = new HashMap<String, String>();

	/**
	 * Mapper object used for deserializing JSON data.
	 */
	final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Creates an object to manage an connection to a Wikibase API and send http
	 * requests.
	 * 
	 * @param apiBaseUrl
	 *            base URI to the API - please use https URLs!
	 */
	public ApiConnection(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
	}

	/**
	 * Creates an api connection to wikidata.org.
	 * 
	 * @return {@link ApiConnection}
	 */
	public static ApiConnection getWikidataApiConnection() {
		return new ApiConnection(ApiConnection.wikidataApiUrl);
	}

	/**
	 * Creates an api connection to test.wikidata.org.
	 * 
	 * @return {@link ApiConnection}
	 */
	public static ApiConnection getTestWikidataApiConnection() {
		return new ApiConnection(ApiConnection.testWikidataApiUrl);
	}

	/**
	 * Log the user in the API given a username and a password
	 * 
	 * @param username
	 * @param password
	 * @return true if the login was successful
	 * @throws IOException
	 * @throws LoginFailedException
	 */
	public void login(String username, String password)
			throws LoginFailedException {
		String token;
		try {
			token = this.getLoginToken(username, password);
			try {
				this.confirmLogin(token, username, password);
			} catch (NeedTokenException e) {
				token = this.getLoginToken(username, password);
				this.confirmLogin(token, username, password);
			}
		} catch (IOException e1) {
			throw new LoginFailedException(e1.getMessage());
		}
	}

	/**
	 * Returns login Token from an api login query with the given username and
	 * password.
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws IOException
	 */
	String getLoginToken(String username, String password) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "login");
		params.put("lgname", username);
		params.put("lgpassword", password);
		params.put("format", "json");
		String tokenResponse = this.getRequestResultAsString(this.sendRequest(
				"POST", params));
		JsonNode root = this.mapper.readTree(tokenResponse);
		String token = root.get("login").get("token").textValue();

		return token;
	}

	/**
	 * Returns true if the query to confirm the login was successful.
	 * 
	 * @param token
	 * @param username
	 * @param password
	 * @return
	 * @throws IOException
	 * @throws LoginFailedException
	 */
	void confirmLogin(String token, String username, String password)
			throws IOException, LoginFailedException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "login");
		params.put("lgname", username);
		params.put("lgpassword", password);
		params.put("lgtoken", token);
		params.put("format", "json");
		String json = this
				.getRequestResultAsString(sendRequest("POST", params));
		JsonNode root = this.mapper.readTree(json);
		String result = root.get("login").get("result").textValue();
		if (result.equals(ApiConnection.LOGIN_RESULT_SUCCESS)) {
			this.loggedIn = true;
			this.username = username;
			this.password = password;
			return;
		} else {
			String message;
			switch (result) {
			case ApiConnection.LOGIN_WRONG_PASS:
				message = result + ": Wrong Password.";
				logger.warn(message);
				throw new LoginFailedException(message);
			case ApiConnection.LOGIN_WRONG_PLUGIN_PASS:
				message = result
						+ ": Wrong Password. An authentication plugin rejected the password.";
				logger.warn(message);
				throw new LoginFailedException(message);
			case ApiConnection.LOGIN_NOT_EXISTS:
				message = result + ": Username does not exist.";
				logger.warn(message);
				throw new LoginFailedException(message);
			case ApiConnection.LOGIN_BLOCKED:
				message = result + ": User is blocked.";
				logger.warn(message);
				throw new LoginFailedException(message);
			case ApiConnection.LOGIN_EMPTY_PASS:
				message = result + ": Password is empty.";
				logger.warn(message);
				throw new LoginFailedException(message);
			case ApiConnection.LOGIN_CREATE_BLOCKED:
				message = result
						+ ": The wiki tried to automatically create a new account for you,"
						+ "but your IP address has been blocked from account creation.";
				logger.warn(message);
				throw new LoginFailedException(message);
			case ApiConnection.LOGIN_ILLEGAL:
				message = result + ": Usernmame is illegal.";
				logger.warn(message);
				throw new LoginFailedException(message);
			case ApiConnection.LOGIN_THROTTLED:
				message = result + ": Too many logins in a short time.";
				logger.warn(message);
				throw new LoginFailedException(message);
			case ApiConnection.LOGIN_WRONG_TOKEN:
				message = result + ": Token is wrong.";
				logger.warn(message);
				throw new LoginFailedException(message);
			case ApiConnection.LOGIN_NEEDTOKEN:
				message = result + ": Token or Session ID is missing.";
				logger.warn(message);
				throw new NeedTokenException();
			default:
				message = "Login Error: " + result;
				logger.warn(message);
				throw new LoginFailedException(message);
			}
		}
	}

	/**
	 * Logs the current user out.
	 * 
	 * @throws IOException
	 */
	public void Logout() throws IOException {
		if (this.loggedIn) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("action", "logout");
			params.put("format", "json"); // reduce the output
			this.sendRequest("POST", params);
			this.loggedIn = false;
			this.username = "";
			this.password = "";
		}
	}

	/**
	 * Sets a cookie for the next requests.
	 * 
	 * @param key
	 * @param Value
	 */
	public void setCookie(String key, String value) {
		this.cookies.put(key, value);
	}

	/**
	 * Reads out the Set-Cookie Header Fields and fills the cookie map of the
	 * api connection with it.
	 * 
	 * @param con
	 */
	void fillCookies(HttpURLConnection con) {
		this.cookies.clear();
		String headerName;
		for (int i = 1; (headerName = con.getHeaderFieldKey(i)) != null; i++) {
			if (headerName.equals(ApiConnection.HEADER_FIELD_SET_COOKIE)) {
				String[] cookieResponse = con.getHeaderField(i).split(
						";\\p{Space}??");
				for (String cookieLine : cookieResponse) {
					String[] entry = cookieLine.split("=");
					if (entry.length == 2)
						this.cookies.put(entry[0], entry[1]);
					if (entry.length == 1)
						this.cookies.put(entry[0], "");
				}
			}
		}
	}

	/**
	 * Sets the cookie request parameter from the cooie map of the api
	 * connection. This should be done before sending requests to the api.
	 * 
	 * @param con
	 */
	void setCookies(HttpURLConnection con) {
		String result = "";
		for (String key : this.cookies.keySet()) {
			if (this.cookies.get(key) != "") {
				result += key + "=" + this.cookies.get(key) + "; ";
			} else {
				result += key + "; ";
			}

		}
		if (result != "") {
			result = result.substring(0, result.length() - 2);
		}
		con.setRequestProperty(ApiConnection.PARAM_COOKIE, result);

	}

	/**
	 * Clears the set of cookies. This will cause a Logout.
	 * 
	 * @throws IOException
	 */
	public void clearCookies() throws IOException {
		this.Logout();
		this.cookies.clear();

	}

	/**
	 * Returns the query string of a URL from a parameter list.
	 * 
	 * @param params
	 *            Map with parameters
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getQueryString(Map<String, String> params)
			throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder();
		for (String key : params.keySet()) {
			builder.append(URLEncoder.encode(key, "UTF-8"));
			builder.append("=");
			builder.append(URLEncoder.encode(params.get(key), "UTF-8"));
			builder.append("&");
		}
		String result = builder.toString();
		return result.substring(0, result.length() - 1);
	}

	/**
	 * This method send a request to the api with the given parameters and the
	 * given request method to the api and returns the result string. It
	 * automatically fill the cookie map with cookies in the result header after
	 * the request.
	 * 
	 * @param requestMethod
	 *            either POST or GET
	 * @param parameters
	 *            Maps parameter keys to values. Out of this map the function
	 *            will create a query string for the request.
	 * @return api result
	 * @throws IOException
	 */
	public InputStream sendRequest(String requestMethod,
			Map<String, String> parameters) throws IOException {
		String queryString = getQueryString(parameters);
		URL url = new URL(this.apiBaseUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(requestMethod);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length",
				String.valueOf(queryString.length()));
		this.setCookies(connection);
		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		writer.write(queryString);
		writer.flush();
		writer.close();
		int rc = connection.getResponseCode();
		if (rc != 200) {
			logger.warn("Error: Getting Response Code " + rc);
		}
		InputStream iStream = connection.getInputStream();
		this.fillCookies(connection);
		return iStream;
	}

	/**
	 * Halndles errors and warnings. Note that there is at most one error
	 * message in the json result even if there are more errors in the request.
	 * Returns true if there are no errors.
	 * 
	 * @param root
	 *            root node of the json result
	 * @return
	 */
	public boolean parseErrorsAndWarnings(JsonNode root) {
		if (root.has("error")) {
			JsonNode errorNode = root.path("error");
			logger.error("Error when reading data from API: "
					+ errorNode.path("info").asText("DESCRIPTION MISSING")
					+ " ["
					+ errorNode.path("code").asText("UNKNOWN ERROR CODE") + "]");
			return false;
		}
		if (root.has("warning")) {
			JsonNode warningNode = root.path("warning");
			Iterator<Map.Entry<String, JsonNode>> iter = warningNode.fields();
			while (iter.hasNext()) {
				Map.Entry<String, JsonNode> node = iter.next();
				if (node.getKey().equals("main")) {
					logger.warn("Warning when reading data from API: "
							+ node.getValue().path("*")
									.asText("DESCRIPTION MISSING"));
				} else {
					logger.warn("Warning when reading data from API "
							+ node.getKey()
							+ " :"
							+ node.getValue().path("*")
									.asText("DESCRIPTION MISSING"));
				}

			}
		}
		return true;
	}

	/**
	 * Returns the content of an {@link InputStream} without line endings.
	 * 
	 * @param iStream
	 * @return String result
	 * @throws IOException
	 */
	private String getRequestResultAsString(InputStream iStream)
			throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				iStream));
		String result = "";
		for (String line; (line = reader.readLine()) != null;) {
			result = result.concat(line);
		}
		return result;
	}

}
