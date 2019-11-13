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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Class to build up and hold a connection to a Wikibase API, managing cookies
 * and login.
 * 
 * This should no longer be instantiated directly: please use one of the subclasses
 * {@class BasicApiConnection} and {@class OAuthApiConnection} instead. This
 * class will become an interface in a future release.
 *
 * @author Michael Guenther
 * @author Antonin Delpeuch
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiConnection {

	static final Logger logger = LoggerFactory.getLogger(ApiConnection.class);

	/**
	 * URL of the API of wikidata.org.
	 */
	public final static String URL_WIKIDATA_API = "https://www.wikidata.org/w/api.php";
	/**
	 * URL of the API of test.wikidata.org.
	 */
	public final static String URL_TEST_WIKIDATA_API = "https://test.wikidata.org/w/api.php";

	/**
	 * Name of the HTTP parameter to submit an action to the API.
	 */
	public final static String PARAM_ACTION = "action";
	/**
	 * Name of the HTTP parameter to submit a password to the API.
	 */
	public final static String PARAM_LOGIN_USERNAME = "lgname";
	/**
	 * Name of the HTTP parameter to submit a password to the API.
	 */
	public final static String PARAM_LOGIN_PASSWORD = "lgpassword";
	/**
	 * Name of the HTTP parameter to submit a login token to the API.
	 */
	public final static String PARAM_LOGIN_TOKEN = "lgtoken";
	/**
	 * Name of the HTTP parameter to submit the requested result format to the
	 * API.
	 */
	public final static String PARAM_FORMAT = "format";
	/**
	 * Name of the HTTP parameter to submit cookies to the API.
	 */
	public final static String PARAM_COOKIE = "Cookie";

	/**
	 * Name of the HTTP response header field that provides us with cookies we
	 * should set.
	 */
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
	 * String value in the result field of the JSON response if the user name is
	 * illegal.
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
	 * MediaWiki assert= parameter to ensure we are editting while logged in.
	 */
	private static final String ASSERT_PARAMETER = "assert";

	/**
	 * URL to access the Wikibase API.
	 */
	final String apiBaseUrl;

	/**
	 * True after successful login.
	 */
	boolean loggedIn = false;
	/**
	 * User name used to log in.
	 */
	String username = "";
	
	/**
	 * Password used to log in.
	 */
	@JsonIgnore
	String password = "";
	/**
	 * Map of cookies that are currently set.
	 */
	final Map<String, String> cookies;

	/**
	 * Map of requested tokens.
	 */
	final Map<String, String> tokens;
	
	/**
	 * Maximum time to wait for when establishing a connection, in milliseconds.
	 * For negative values, no timeout is set.
	 */
	int connectTimeout = -1;
	
	/**
	 * Maximum time to wait for a server response once the connection was established.
	 * For negative values, no timeout is set.
	 */
	int readTimeout = -1;

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
	@Deprecated
	public ApiConnection(String apiBaseUrl) {
		this.apiBaseUrl = apiBaseUrl;
		this.cookies = new HashMap<>();
		this.tokens = new HashMap<>();
	}
	
	/**
	 * Deserializes an existing ApiConnection from JSON.
	 * 
	 * @param apiBaseUrl
	 * 		base URL of the API to use, e.g. "https://www.wikidata.org/w/api.php/"
	 * @param cookies
	 * 		map of cookies used for this session
	 * @param loggedIn
	 * 		true if login succeeded.
	 * @param tokens
	 * 		map of tokens used for this session
	 */
	@JsonCreator
	@Deprecated
	protected ApiConnection(
			@JsonProperty("baseUrl") String apiBaseUrl,
			@JsonProperty("cookies") Map<String, String> cookies,
			@JsonProperty("username") String username,
			@JsonProperty("loggedIn") boolean loggedIn,
			@JsonProperty("tokens") Map<String, String> tokens) {
		this.apiBaseUrl = apiBaseUrl;
		this.username = username;
		this.cookies = cookies;
		this.loggedIn = loggedIn;
		this.tokens = tokens;
	}

	/**
	 * Creates an API connection to wikidata.org.
	 *
	 * @deprecated to be migrated to {@class BasicApiConnection}
	 * @return {@link BasicApiConnection}
	 */
	@Deprecated
	public static BasicApiConnection getWikidataApiConnection() {
		return new BasicApiConnection(ApiConnection.URL_WIKIDATA_API);
	}

	/**
	 * Creates an API connection to test.wikidata.org.
	 *
	 * @deprecated to be migrated to {@class BasicApiConnection}
	 * @return {@link BasicApiConnection}
	 */
	@Deprecated
	public static BasicApiConnection getTestWikidataApiConnection() {
		return new BasicApiConnection(ApiConnection.URL_TEST_WIKIDATA_API);
	}

	/**
	 * Builds a string that serializes a list of objects separated by the pipe
	 * character. The toString methods are used to turn objects into strings.
	 * This operation is commonly used to build parameter lists for API
	 * requests.
	 *
	 * @param objects
	 *            the objects to implode
	 * @return string of imploded objects
	 */
	public static String implodeObjects(Iterable<?> objects) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Object o : objects) {
			if (first) {
				first = false;
			} else {
				builder.append("|");
			}
			builder.append(o.toString());
		}
		return builder.toString();
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
	 * @throws LoginFailedException
	 *             if the login failed for some reason
	 */
	@Deprecated
	public void login(String username, String password)
			throws LoginFailedException {
		try {
			String token = fetchToken("login");
			try {
				this.confirmLogin(token, username, password);
			} catch (NeedLoginTokenException e) { // try once more
				token = fetchToken("login");
				this.confirmLogin(token, username, password);
			}
		} catch (IOException | MediaWikiApiErrorException e1) {
			throw new LoginFailedException(e1.getMessage(), e1);
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
	 * Logs the current user out.
	 *
	 * @throws IOException
	 */
	public void logout() throws IOException {
		if (this.loggedIn) {
			Map<String, String> params = new HashMap<>();
			params.put("action", "logout");
			params.put("format", "json"); // reduce the output
			params.put("token", getOrFetchToken("csrf"));
			try {
				sendJsonRequest("POST", params);
			} catch (MediaWikiApiErrorException e) {
				throw new IOException(e.getMessage(), e); //TODO: we should throw a better exception
			}

			this.loggedIn = false;
			this.username = "";
			this.password = "";
		}
	}

	/**
	 * Clears the set of cookies. This will cause a logout.
	 *
	 * @throws IOException
	 */
	public void clearCookies() throws IOException {
		logout();
		this.cookies.clear();
		this.tokens.clear();
	}

	/**
	 * Return a token of given type.
	 * @param tokenType The kind of token to retrieve like "csrf" or "login"
	 * @return can return null if token can not be retrieved
	 */
	String getOrFetchToken(String tokenType) throws IOException {
		if (tokens.containsKey(tokenType)) {
			return tokens.get(tokenType);
		}
		String value = fetchToken(tokenType);
		tokens.put(tokenType, value);
		// TODO if fetchToken raises an exception, we could try to recover here:
		// (1) Check if we are still logged in; maybe log in again
		// (2) If there is another error, maybe just run the operation again
		return value;
	}

	/**
	 * Remove fetched value of given token.
	 */
	void clearToken(String tokenType) {
		tokens.remove(tokenType);
	}

	/**
	 * Executes a API query action to get a new token.
	 * The method only executes the action, without doing any
	 * checks first. If errors occur, they are logged and null is returned.
	 *
	 * @param tokenType The kind of token to retrieve like "csrf" or "login"
	 * @return newly retrieved token
	 * @throws IOException if no token could be retrieved
	 */
	private String fetchToken(String tokenType) throws IOException {
		Map<String, String> params = new HashMap<>();
		params.put(ApiConnection.PARAM_ACTION, "query");
		params.put("meta", "tokens");
		params.put("type", tokenType);

		try {
			JsonNode root = this.sendJsonRequest("POST", params);
			return root.path("query").path("tokens").path(tokenType + "token").textValue();
		} catch (MediaWikiApiErrorException e) {
			throw new IOException("Error when trying to fetch token: " + e.toString());
		}
	}

	/**
	 * Sends a request to the API with the given parameters and the given
	 * request method and returns the result JSON tree. It automatically fills the
	 * cookie map with cookies in the result header after the request.
	 * It logs the request warnings and adds makes sure that "format": "json"
	 * parameter is set.
	 *
	 * @param requestMethod
	 *            either POST or GET
	 * @param parameters
	 *            Maps parameter keys to values. Out of this map the function
	 *            will create a query string for the request.
	 * @return API result
	 * @throws IOException
	 * @throws MediaWikiApiErrorException if the API returns an error
	 */
	public JsonNode sendJsonRequest(String requestMethod, Map<String,String> parameters) throws IOException, MediaWikiApiErrorException {
		parameters.put(ApiConnection.PARAM_FORMAT, "json");
		if (loggedIn) {
			parameters.put(ApiConnection.ASSERT_PARAMETER, "user");
		}
		try (InputStream response = sendRequest(requestMethod, parameters)) {
			JsonNode root = this.mapper.readTree(response);
			this.checkErrors(root);
			this.logWarnings(root);
			return root;
		}
	}

	/**
	 * Sends a request to the API with the given parameters and the given
	 * request method and returns the result string. It automatically fills the
	 * cookie map with cookies in the result header after the request.
	 *
	 * Warning: You probably want to use ApiConnection.sendJsonRequest
	 * that execute the request using JSON content format,
	 * throws the errors and logs the warnings.
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
		HttpURLConnection connection = (HttpURLConnection) WebResourceFetcherImpl
				.getUrlConnection(url);

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
	 * @deprecated Use ApiConnection.sendJsonRequest that executes this method
	 *
	 * Checks if an API response contains an error and throws a suitable
	 * exception in this case.
	 *
	 * @param root
	 *            root node of the JSON result
	 * @throws MediaWikiApiErrorException
	 */
	public void checkErrors(JsonNode root) throws MediaWikiApiErrorException {
		if (root.has("error")) {
			JsonNode errorNode = root.path("error");
			MediaWikiApiErrorHandler.throwMediaWikiApiErrorException(errorNode
					.path("code").asText("UNKNOWN"), errorNode.path("info")
					.asText("No details provided"));
		}
	}

	/**
	 * @deprecated Use ApiConnection.sendJsonRequest that executes this method
	 *
	 * Extracts and logs any warnings that are returned in an API response.
	 *
	 * @param root
	 *            root node of the JSON result
	 */
	public void logWarnings(JsonNode root) {
		for (String warning : getWarnings(root)) {
			logger.warn("API warning " + warning);
		}
	}
	
	/**
	 * Checks that the credentials are still valid for the
	 * user currently logged in. This can fail if (for instance)
	 * the cookies expired, or were invalidated by a logout from
	 * a different client.
	 * 
	 * This method queries the API and throws {@class AssertUserFailedException}
	 * if the check failed. This does not update the state of the connection
	 * object.
	 * @throws MediaWikiApiErrorException 
	 * @throws IOException 
	 */
	public void checkCredentials() throws IOException, MediaWikiApiErrorException {
		Map<String,String> parameters = new HashMap<>();
		parameters.put("action", "query");
		sendJsonRequest("POST", parameters);
	}

	/**
	 * Extracts warnings that are returned in an API response.
	 *
	 * @param root
	 *            root node of the JSON result
	 */
	List<String> getWarnings(JsonNode root) {
		ArrayList<String> warnings = new ArrayList<>();

		if (root.has("warnings")) {
			JsonNode warningNode = root.path("warnings");
			Iterator<Map.Entry<String, JsonNode>> moduleIterator = warningNode
					.fields();
			while (moduleIterator.hasNext()) {
				Map.Entry<String, JsonNode> moduleNode = moduleIterator.next();
				Iterator<JsonNode> moduleOutputIterator = moduleNode.getValue()
						.elements();
				while (moduleOutputIterator.hasNext()) {
					JsonNode moduleOutputNode = moduleOutputIterator.next();
					if (moduleOutputNode.isTextual()) {
						warnings.add("[" + moduleNode.getKey() + "]: "
								+ moduleOutputNode.textValue());
					} else if (moduleOutputNode.isArray()) {
						Iterator<JsonNode> messageIterator = moduleOutputNode
								.elements();
						while (messageIterator.hasNext()) {
							JsonNode messageNode = messageIterator.next();
							warnings.add("["
									+ moduleNode.getKey()
									+ "]: "
									+ messageNode.path("html").path("*")
											.asText(messageNode.toString()));
						}
					} else {
						warnings.add("["
								+ moduleNode.getKey()
								+ "]: "
								+ "Warning was not understood. Please report this to Wikidata Toolkit. JSON source: "
								+ moduleOutputNode.toString());
					}
				}

			}
		}

		return warnings;
	}

	/**
	 * Issues a Web API query to confirm that the previous login attempt was
	 * successful, and sets the internal state of the API connection accordingly
	 * in this case.
	 * 
	 * @deprecated because it will be migrated to {@class BasicApiConnection}.
	 *
	 * @param token
	 *            the login token string
	 * @param username
	 *            the name of the user that was logged in
	 * @param password
	 *            the password used to log in
	 * @throws IOException
	 * @throws LoginFailedException
	 */
	@Deprecated
	void confirmLogin(String token, String username, String password)
			throws IOException, LoginFailedException, MediaWikiApiErrorException {
		Map<String, String> params = new HashMap<>();
		params.put(ApiConnection.PARAM_ACTION, "login");
		params.put(ApiConnection.PARAM_LOGIN_USERNAME, username);
		params.put(ApiConnection.PARAM_LOGIN_PASSWORD, password);
		params.put(ApiConnection.PARAM_LOGIN_TOKEN, token);

		JsonNode root = sendJsonRequest("POST", params);

		String result = root.path("login").path("result").textValue();
		if (ApiConnection.LOGIN_RESULT_SUCCESS.equals(result)) {
			this.loggedIn = true;
			this.username = username;
			this.password = password;
		} else {
			String message = getLoginErrorMessage(result);
			logger.warn(message);
			if (ApiConnection.LOGIN_WRONG_TOKEN.equals(result)) {
				throw new NeedLoginTokenException(message);
			} else {
				throw new LoginFailedException(message);
			}
		}
	}

	/**
	 * Returns a user-readable message for a given API response.
	 *
	 * @deprecated to be migrated to {@class BasicApiConnection}
	 * @param loginResult
	 *            a API login request result string other than
	 *            {@link #LOGIN_RESULT_SUCCESS}
	 * @return error message
	 */
	@Deprecated
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
					+ ": The wiki tried to automatically create a new account for you, "
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
	 * Reads out the Set-Cookie Header Fields and fills the cookie map of the
	 * API connection with it.
	 * 
	 * @deprecated to be migrated to {@class BasicApiConnection}
	 */
	@Deprecated
	void fillCookies(Map<String, List<String>> headerFields) {
		List<String> headerCookies = headerFields
				.get(ApiConnection.HEADER_FIELD_SET_COOKIE);
		if (headerCookies != null) {
			for (String cookie : headerCookies) {
				String[] cookieResponse = cookie.split(";\\p{Space}??");
				for (String cookieLine : cookieResponse) {
					String[] entry = cookieLine.split("=");
					if (entry.length == 2) {
						this.cookies.put(entry[0], entry[1]);
					}
					if (entry.length == 1) {
						this.cookies.put(entry[0], "");
					}
				}
			}
		}
	}

	/**
	 * Returns the string representation of the currently stored cookies. This
	 * data is added to the connection before making requests.
	 * 
	 * @deprecated to be migrated to {@class BasicApiConnection}
	 * @return cookie string
	 */
	@Deprecated
	String getCookieString() {
		StringBuilder result = new StringBuilder();
		boolean first = true;
		for (Entry<String, String> entry : this.cookies.entrySet()) {
			if (first) {
				first = false;
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
	 * Returns the query string of a URL from a parameter list.
	 *
	 * @param params
	 *            Map with parameters
	 * @return query string
	 */
	String getQueryString(Map<String, String> params) {
		StringBuilder builder = new StringBuilder();
		try {
			boolean first = true;
			for (Map.Entry<String,String> entry : params.entrySet()) {
				if (first) {
					first = false;
				} else {
					builder.append("&");
				}
				builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				builder.append("=");
				builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(
					"Your Java version does not support UTF-8 encoding.");
		}

		return builder.toString();
	}

	/**
	 * Configures a given {@link HttpURLConnection} object to send requests.
	 * Takes the request method (either "POST" or "GET") and query string.
	 *
	 * @param requestMethod
	 *            either "POST" or "GET"
	 * @param queryString
	 *            the query string to submit
	 * @param connection
	 *            the connection to configure
	 * @throws IOException
	 *             if the given protocol is not valid
	 */
	void setupConnection(String requestMethod, String queryString,
			HttpURLConnection connection) throws IOException {
		connection.setRequestMethod(requestMethod);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		if(connectTimeout >= 0) {
			connection.setConnectTimeout(connectTimeout);
		}
		if(readTimeout >= 0) {
			connection.setReadTimeout(readTimeout);
		}
		connection.setRequestProperty(ApiConnection.PARAM_COOKIE,
				getCookieString());
	}
	
	/**
	 * Maximum time to wait for when establishing a connection, in milliseconds.
	 * For negative values, no timeout is set, which is the default behaviour (for
	 * backwards compatibility).
	 * 
	 * @see HttpURLConnection.getConnectionTimeout
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}
	
	/**
	 * Sets the maximum time to wait for when establishing a connection, in milliseconds.
	 * For negative values, no timeout is set.
	 * 
	 * @see HttpURLConnection.setConnectionTimeout
	 */
	public void setConnectTimeout(int timeout) {
		connectTimeout = timeout;
	}
	
	/**
	 * Maximum time to wait for a server response once the connection was established.
	 * For negative values, no timeout is set, which is the default behaviour (for backwards
	 * compatibility).
	 * 
	 * @see HttpURLConnection.getReadTimeout
	 */
	public int getReadTimeout() {
		return readTimeout;
	}
	
	/**
	 * Sets the maximum time to wait for a server response once the connection was established.
	 * For negative values, no timeout is set.
	 * 
	 * @see HttpURLConnection.setReadTimeout
	 */
	public void setReadTimeout(int timeout) {
		readTimeout = timeout;
	}

}
