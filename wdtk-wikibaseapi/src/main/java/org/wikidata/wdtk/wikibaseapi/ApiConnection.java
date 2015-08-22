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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

	/**
	 * URL of the API of wikidata.org.
	 */
	public final static String URL_WIKIDATA_API = "https://www.wikidata.org/w/api.php/";
	/**
	 * URL of the API of test.wikidata.org.
	 */
	public final static String URL_TEST_WIKIDATA_API = "https://test.wikidata.org/w/api.php";

	final static String PARAM_COOKIE = "Cookie";
	final static String HEADER_FIELD_SET_COOKIE = "Set-Cookie";

	/**
	 * String value in the result field of the JSON response if the login was
	 * successful.
	 */
	final static String LOGIN_RESULT_SUCCESS = "Success";
	/**
	 * String value in the result field of the JSON response if the password was
	 * wrong.
	 */
	final static String LOGIN_WRONG_PASS = "WrongPass";
	/**
	 * String value in the result field of the JSON response if the password was
	 * rejected by an authentication plugin.
	 */
	final static String LOGIN_WRONG_PLUGIN_PASS = "WrongPluginPass";
	/**
	 * String value in the result field of the JSON response if no username was
	 * given.
	 */
	final static String LOGIN_NO_NAME = "NoName";
	/**
	 * String value in the result field of the JSON response if given username
	 * does not exist.
	 */
	final static String LOGIN_NOT_EXISTS = "NotExists";
	/**
	 * Username is illegal.
	 */
	final static String LOGIN_ILLEGAL = "Illegal";
	/**
	 * String value in the result field of the JSON response if there were too
	 * many logins in a short time.
	 */
	final static String LOGIN_THROTTLED = "Throttled";
	/**
	 * String value in the result field of the JSON response if password is
	 * empty.
	 */
	final static String LOGIN_EMPTY_PASS = "EmptyPass";
	/**
	 * String value in the result field of the JSON response if the wiki tried
	 * to automatically create a new account for you, but your IP address has
	 * been blocked from account creation.
	 */
	final static String LOGIN_CREATE_BLOCKED = "CreateBlocked";
	/**
	 * String value in the result field of the JSON response if the user is
	 * blocked.
	 */
	final static String LOGIN_BLOCKED = "Blocked";
	/**
	 * String value in the result field of the JSON response if token or session
	 * ID is missing.
	 */
	final static String LOGIN_NEEDTOKEN = "NeedToken";
	/**
	 * String value in the result field of the JSON response if token is wrong.
	 */
	final static String LOGIN_WRONG_TOKEN = "WrongToken";

	/**
	 * URL to access the Wikibase API.
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
	 * Creates an object to manage a connection to the Web API of a Wikibase
	 * site.
	 *
	 * @param apiBaseUrl
	 *            base URI to the API, e.g.,
	 *            "https://www.wikidata.org/w/api.php/"
	 */
	public ApiConnection(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
	}

	/**
	 * Creates an API connection to wikidata.org.
	 *
	 * @return {@link ApiConnection}
	 */
	public static ApiConnection getWikidataApiConnection() {
		return new ApiConnection(ApiConnection.URL_WIKIDATA_API);
	}

	/**
	 * Creates an API connection to test.wikidata.org.
	 *
	 * @return {@link ApiConnection}
	 */
	public static ApiConnection getTestWikidataApiConnection() {
		return new ApiConnection(ApiConnection.URL_TEST_WIKIDATA_API);
	}

	/**
	 * Logs in using the specified user credentials. After successful login, the
	 * API connection remains in a logged in state, and future actions will be
	 * run as a logged in user.
	 *
	 * @param username
	 *            the name of the user to log in
	 * @param password
	 *            the password of the user
	 * @return true if the login was successful
	 * @throws LoginFailedException
	 *             if the login failed for some reason
	 */
	public void login(String username, String password)
			throws LoginFailedException {
		try {
			String token = this.getLoginToken(username, password);
			try {
				this.confirmLogin(token, username, password);
			} catch (NeedTokenException e) { // try once more
				token = this.getLoginToken(username, password);
				this.confirmLogin(token, username, password);
			}
		} catch (IOException e1) {
			throw new LoginFailedException(e1.getMessage());
		}
	}

	/**
	 * Returns true if a user is logged in.
	 *
	 * @return true if the connection is in a logged in state
	 */
	public boolean isLoggedIn() {
		return this.loggedIn;
	}

	/**
	 * Returns the username of the user who is currently logged in. If there is
	 * no user logged in the result is an empty string.
	 *
	 * @return name of the logged in user
	 */
	public String getCurrentUser() {
		return this.username;
	}

	/**
	 * Returns login token from an API login query with the given username and
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

		JsonNode root = this.mapper.readTree(sendRequest("POST", params));
		String token = root.get("login").get("token").textValue();

		return token;
	}

	/**
	 * Issues a Web API query to confirm that the previous login attempt was
	 * successful, and sets the internal state of the API connection accordingly
	 * in this case.
	 *
	 * @param token
	 *            the token string acquired from a previous call to
	 *            {@link #getLoginToken(String, String)}
	 * @param username
	 *            the name of the user that was logged in
	 * @param password
	 *            the password used to log in
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

		JsonNode root = this.mapper.readTree(sendRequest("POST", params));

		String result = root.get("login").get("result").textValue();
		if (result.equals(ApiConnection.LOGIN_RESULT_SUCCESS)) {
			this.loggedIn = true;
			this.username = username;
			this.password = password;
			return;
		} else {
			String message = getLoginErrorMessage(result);
			logger.warn(message);
			throw new LoginFailedException(message);
		}
	}

	/**
	 * Returns a user-readable message for a given API response.
	 *
	 * @param loginResult
	 *            a API login request result string other than
	 *            {@link #LOGIN_RESULT_SUCCESS}
	 * @return error message
	 */
	String getLoginErrorMessage(String loginResult) {
		switch (loginResult) {
		case ApiConnection.LOGIN_WRONG_PASS:
			return loginResult + ": Wrong Password.";
		case ApiConnection.LOGIN_WRONG_PLUGIN_PASS:
			return loginResult
					+ ": Wrong Password. An authentication plugin rejected the password.";
		case ApiConnection.LOGIN_NOT_EXISTS:
			return loginResult + ": Username does not exist.";
		case ApiConnection.LOGIN_BLOCKED:
			return loginResult + ": User is blocked.";
		case ApiConnection.LOGIN_EMPTY_PASS:
			return loginResult + ": Password is empty.";
		case ApiConnection.LOGIN_NO_NAME:
			return loginResult + ": No user name given.";
		case ApiConnection.LOGIN_CREATE_BLOCKED:
			return loginResult
					+ ": The wiki tried to automatically create a new account for you,"
					+ "but your IP address has been blocked from account creation.";
		case ApiConnection.LOGIN_ILLEGAL:
			return loginResult + ": Username is illegal.";
		case ApiConnection.LOGIN_THROTTLED:
			return loginResult + ": Too many login attempts in a short time.";
		case ApiConnection.LOGIN_WRONG_TOKEN:
			return loginResult + ": Token is wrong.";
		case ApiConnection.LOGIN_NEEDTOKEN:
			return loginResult + ": Token or session ID is missing.";
		default:
			return "Login Error: " + loginResult;
		}
	}

	/**
	 * Logs the current user out.
	 *
	 * @throws IOException
	 */
	public void logout() throws IOException {
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
	 * Reads out the Set-Cookie Header Fields and fills the cookie map of the
	 * API connection with it.
	 *
	 * @param con
	 */
	void fillCookies(Map<String, List<String>> headerFields) {
		List<String> cookieList = headerFields
				.get(ApiConnection.HEADER_FIELD_SET_COOKIE);
		for (int i = 0; i < cookieList.size(); i++) {
			String[] cookieResponse = cookieList.get(i).split(";\\p{Space}??");
			for (String cookieLine : cookieResponse) {
				String[] entry = cookieLine.split("=");
				if (entry.length == 2)
					this.cookies.put(entry[0], entry[1]);
				if (entry.length == 1)
					this.cookies.put(entry[0], "");
			}
		}
	}

	/**
	 * Returns the string representation of the currently stored cookies. This
	 * data is added to the connection before making requests.
	 */
	String getCookieString() {
		StringBuilder result = new StringBuilder();
		boolean isFirst = true;
		for (Entry<String, String> entry : this.cookies.entrySet()) {
			if (isFirst) {
				isFirst = false;
			} else {
				result.append("; ");
			}
			result.append(entry.getKey());
			if (!"".equals(entry.getValue())) {
				result.append("=").append(entry.getValue());
			}
		}
		return result.toString();
	}

	/**
	 * Clears the set of cookies. This will cause a logout.
	 *
	 * @throws IOException
	 */
	public void clearCookies() throws IOException {
		this.logout();
		this.cookies.clear();

	}

	/**
	 * Returns the query string of a URL from a parameter list.
	 *
	 * @param params
	 *            Map with parameters
	 * @return query string
	 */
	String getQueryString(Map<String, String> params) {
		StringBuilder builder = new StringBuilder();
		try {
			for (String key : params.keySet()) {
				builder.append(URLEncoder.encode(key, "UTF-8"));
				builder.append("=");
				builder.append(URLEncoder.encode(params.get(key), "UTF-8"));
				builder.append("&");
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(
					"Your Java version does not support UTF-8 encoding.");
		}
		String result = builder.toString();
		return result.substring(0, result.length() - 1);
	}

	/**
	 * Sends a request to the API with the given parameters and the given
	 * request method and returns the result string. It automatically fills the
	 * cookie map with cookies in the result header after the request.
	 *
	 * @param requestMethod
	 *            either POST or GET
	 * @param parameters
	 *            Maps parameter keys to values. Out of this map the function
	 *            will create a query string for the request.
	 * @return API result
	 * @throws IOException
	 */
	public InputStream sendRequest(String requestMethod,
			Map<String, String> parameters) throws IOException {
		String queryString = getQueryString(parameters);
		URL url = new URL(this.apiBaseUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		setupConnection(requestMethod, queryString, connection);
		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		writer.write(queryString);
		writer.flush();
		writer.close();

		int rc = connection.getResponseCode();
		if (rc != 200) {
			logger.warn("Error: API request returned response code " + rc);
		}

		InputStream iStream = connection.getInputStream();
		fillCookies(connection.getHeaderFields());
		return iStream;
	}

	/**
	 * Handles errors and warnings that are returned in an API response. Note
	 * that there is at most one error message in the JSON result even if there
	 * are more errors in the request. Returns true if there are no errors.
	 *
	 * @param root
	 *            root node of the JSON result
	 * @return true if there are no errors
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

		if (root.has("warnings")) {
			JsonNode warningNode = root.path("warnings");
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
	 * Configures an {@link HttpURLConnection} object to send requests. Takes
	 * the request method (either "POST" or "GET") and query string.
	 *
	 * @param requestMethod
	 * @param queryString
	 * @throws IOException
	 */
	void setupConnection(String requestMethod, String queryString,
			HttpURLConnection connection) throws IOException {
		connection.setRequestMethod(requestMethod);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length",
				String.valueOf(queryString.length()));
		connection.setRequestProperty(ApiConnection.PARAM_COOKIE,
				getCookieString());
	}

}
