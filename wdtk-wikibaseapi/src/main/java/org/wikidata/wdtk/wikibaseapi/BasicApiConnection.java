package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2018 Wikidata Toolkit Developers
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
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A connection to the MediaWiki API established via
 * standard login with username and password.
 *
 * @author Antonin Delpeuch
 *
 */
public class BasicApiConnection extends ApiConnection {
	
	/**
	 * Name of the HTTP parameter to submit a password to the API.
	 */
	protected final static String PARAM_LOGIN_USERNAME = "lgname";
	/**
	 * Name of the HTTP parameter to submit a password to the API.
	 */
	protected final static String PARAM_LOGIN_PASSWORD = "lgpassword";
	/**
	 * Name of the HTTP parameter to submit a login token to the API.
	 */
	protected final static String PARAM_LOGIN_TOKEN = "lgtoken";
	
	/**
	 * String value in the result field of the JSON response if the login was
	 * successful.
	 */
	protected final static String LOGIN_RESULT_SUCCESS = "Success";
	/**
	 * String value in the result field of the JSON response if the password was
	 * wrong.
	 */
	protected final static String LOGIN_WRONG_PASS = "WrongPass";
	/**
	 * String value in the result field of the JSON response if the password was
	 * rejected by an authentication plugin.
	 */
	protected final static String LOGIN_WRONG_PLUGIN_PASS = "WrongPluginPass";
	/**
	 * String value in the result field of the JSON response if no username was
	 * given.
	 */
	protected final static String LOGIN_NO_NAME = "NoName";
	/**
	 * String value in the result field of the JSON response if given username
	 * does not exist.
	 */
	protected final static String LOGIN_NOT_EXISTS = "NotExists";
	/**
	 * String value in the result field of the JSON response if the user name is
	 * illegal.
	 */
	protected final static String LOGIN_ILLEGAL = "Illegal";
	/**
	 * String value in the result field of the JSON response if there were too
	 * many logins in a short time.
	 */
	protected final static String LOGIN_THROTTLED = "Throttled";
	/**
	 * String value in the result field of the JSON response if password is
	 * empty.
	 */
	protected final static String LOGIN_EMPTY_PASS = "EmptyPass";
	/**
	 * String value in the result field of the JSON response if the wiki tried
	 * to automatically create a new account for you, but your IP address has
	 * been blocked from account creation.
	 */
	protected final static String LOGIN_CREATE_BLOCKED = "CreateBlocked";
	/**
	 * String value in the result field of the JSON response if the user is
	 * blocked.
	 */
	protected final static String LOGIN_BLOCKED = "Blocked";
	/**
	 * String value in the result field of the JSON response if token or session
	 * ID is missing.
	 */
	protected final static String LOGIN_NEEDTOKEN = "NeedToken";
	/**
	 * String value in the result field of the JSON response if token is wrong.
	 */
	protected final static String LOGIN_WRONG_TOKEN = "WrongToken";
	
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
	 * Password used to log in.
	 */
	@JsonIgnore
	String password = "";
	/**
	 * Map of cookies that are currently set.
	 */
	final Map<String, String> cookies;
	
	/**
	 * Creates an object to manage a connection to the Web API of a Wikibase
	 * site.
	 *
	 * @param apiBaseUrl
	 *            base URI to the API, e.g.,
	 *            "https://www.wikidata.org/w/api.php/"
	 */
	public BasicApiConnection(String apiBaseUrl) {
		super(apiBaseUrl);
		this.cookies = new HashMap<>();
	}
	
	/**
	 * Deserializes an existing BasicApiConnection from JSON.
	 * 
	 * @param apiBaseUrl
	 * 		base URL of the API to use, e.g. "https://www.wikidata.org/w/api.php/"
	 * @param cookies
	 * 		map of cookies used for this session
	 * @param loggedIn
	 * 		true if login succeeded.
	 */
	@JsonCreator
	protected BasicApiConnection(
			@JsonProperty("baseUrl") String apiBaseUrl,
			@JsonProperty("cookies") Map<String, String> cookies,
			@JsonProperty("username") String username,
			@JsonProperty("loggedIn") boolean loggedIn) {
		super(apiBaseUrl);
		this.username = username;
		this.cookies = cookies;
		this.loggedIn = loggedIn;
	}
	
	/**
	 * Creates an API connection to test.wikidata.org.
	 *
	 * @return {@link BasicApiConnection}
	 */
	public static BasicApiConnection getTestWikidataApiConnection() {
		return new BasicApiConnection(ApiConnection.URL_TEST_WIKIDATA_API);
	}
	
	/**
	 * Creates an API connection to wikidata.org.
	 *
	 * @return {@link BasicApiConnection}
	 */
	public static BasicApiConnection getWikidataApiConnection() {
		return new BasicApiConnection(ApiConnection.URL_WIKIDATA_API);
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
	 * Issues a Web API query to confirm that the previous login attempt was
	 * successful, and sets the internal state of the API connection accordingly
	 * in this case.
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
	protected void confirmLogin(String token, String username, String password)
			throws IOException, LoginFailedException, MediaWikiApiErrorException {
		Map<String, String> params = new HashMap<>();
		params.put(PARAM_ACTION, "login");
		params.put(PARAM_LOGIN_USERNAME, username);
		params.put(PARAM_LOGIN_PASSWORD, password);
		params.put(PARAM_LOGIN_TOKEN, token);

		JsonNode root = sendJsonRequest("POST", params);

		String result = root.path("login").path("result").textValue();
		if (LOGIN_RESULT_SUCCESS.equals(result)) {
			this.loggedIn = true;
			this.username = username;
			this.password = password;
		} else {
			String message = getLoginErrorMessage(result);
			logger.warn(message);
			if (LOGIN_WRONG_TOKEN.equals(result)) {
				throw new NeedLoginTokenException(message);
			} else {
				throw new LoginFailedException(message);
			}
		}
	}

	@Override
	@JsonProperty("loggedIn")
	public boolean isLoggedIn() {
		return super.isLoggedIn();
	}

	@Override
	@JsonProperty("username")
	public String getCurrentUser() {
		return super.getCurrentUser();
	}
	
	/**
	 * Returns the map of cookies currently used in this connection.
	 */
	@JsonProperty("cookies")
	public Map<String, String> getCookies() {
		return Collections.unmodifiableMap(this.cookies);
	}
	
	/**
	 * Clears the set of cookies. This will cause a logout.
	 *
	 * @throws IOException
	 */
	public void clearCookies() throws IOException {
		logout();
		this.cookies.clear();
	}
	
	/**
	 * Reads out the Set-Cookie Header Fields and fills the cookie map of the
	 * API connection with it.
	 */
	void fillCookies(Map<String, List<String>> headerFields) {
		List<String> headerCookies = headerFields
				.get(HEADER_FIELD_SET_COOKIE);
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
	 * @return cookie string
	 */
	protected String getCookieString() {
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
	 * Returns a user-readable message for a given API response.
	 *
	 * @param loginResult
	 *            a API login request result string other than
	 *            {@link #LOGIN_RESULT_SUCCESS}
	 * @return error message
	 */
	protected String getLoginErrorMessage(String loginResult) {
		switch (loginResult) {
		case LOGIN_WRONG_PASS:
			return loginResult + ": Wrong Password.";
		case LOGIN_WRONG_PLUGIN_PASS:
			return loginResult
					+ ": Wrong Password. An authentication plugin rejected the password.";
		case LOGIN_NOT_EXISTS:
			return loginResult + ": Username does not exist.";
		case LOGIN_BLOCKED:
			return loginResult + ": User is blocked.";
		case LOGIN_EMPTY_PASS:
			return loginResult + ": Password is empty.";
		case LOGIN_NO_NAME:
			return loginResult + ": No user name given.";
		case LOGIN_CREATE_BLOCKED:
			return loginResult
					+ ": The wiki tried to automatically create a new account for you, "
					+ "but your IP address has been blocked from account creation.";
		case LOGIN_ILLEGAL:
			return loginResult + ": Username is illegal.";
		case LOGIN_THROTTLED:
			return loginResult + ": Too many login attempts in a short time.";
		case LOGIN_WRONG_TOKEN:
			return loginResult + ": Token is wrong.";
		case LOGIN_NEEDTOKEN:
			return loginResult + ": Token or session ID is missing.";
		default:
			return "Login Error: " + loginResult;
		}
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
	@Override
	protected void setupConnection(String requestMethod, String queryString,
			HttpURLConnection connection) throws IOException {
		super.setupConnection(requestMethod, queryString, connection);
		connection.setRequestProperty(PARAM_COOKIE,
				getCookieString());
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
}
