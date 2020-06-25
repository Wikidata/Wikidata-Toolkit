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
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.*;
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
	 * Password used to log in.
	 */
	@JsonIgnore
	String password = "";

	/**
	 * Used for managing and serializing/deserializing cookies.
	 */
	private final CookieManager cookieManager;

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
		cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
	}

	/**
	 * Deserializes an existing BasicApiConnection from JSON.
	 *
	 * @param apiBaseUrl     base URL of the API to use, e.g. "https://www.wikidata.org/w/api.php/"
	 * @param cookies        map of cookies used for this session
	 * @param username       name of the current user
	 * @param loggedIn       true if login succeeded.
	 * @param tokens         map of tokens used for this session
	 * @param connectTimeout the maximum time to wait for when establishing a connection, in milliseconds
	 * @param readTimeout    the maximum time to wait for a server response once the connection was established, in milliseconds
	 */
	@JsonCreator
	protected BasicApiConnection(
			@JsonProperty("baseUrl") String apiBaseUrl,
			@JsonProperty("cookies") List<HttpCookieWrapper> cookies,
			@JsonProperty("username") String username,
			@JsonProperty("loggedIn") boolean loggedIn,
			@JsonProperty("tokens") Map<String, String> tokens,
			@JsonProperty("connectTimeout") int connectTimeout,
			@JsonProperty("readTimeout") int readTimeout) {
		super(apiBaseUrl, tokens);
		this.username = username;
		this.loggedIn = loggedIn;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;

		cookieManager = new CookieManager();
		cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
		CookieStore cookieStore = cookieManager.getCookieStore();
		// We only deal with apiBaseUrl here.
		URI uri = URI.create(apiBaseUrl);
		cookies.stream().map(HttpCookieWrapper::toHttpCookie)
				.forEach(cookie -> cookieStore.add(uri, cookie));
	}

	@Override
	protected OkHttpClient.Builder getClientBuilder() {
		return new OkHttpClient.Builder()
				.cookieJar(new JavaNetCookieJar(cookieManager));
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
	 * Creates an API connection to commons.wikimedia.org.
	 *
	 * @return {@link BasicApiConnection}
	 */
	public static BasicApiConnection getWikimediaCommonsApiConnection() {
		return new BasicApiConnection(ApiConnection.URL_WIKIMEDIA_COMMONS_API);
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
			String token = getOrFetchToken("login");
			try {
				this.confirmLogin(token, username, password);
			} catch (NeedLoginTokenException e) { // try once more
				clearToken("login");
				token = getOrFetchToken("login");
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

	/**
	 * Returns the map of cookies currently used in this connection.
	 */
	@JsonProperty("cookies")
	public List<HttpCookie> getCookies() {
		return cookieManager.getCookieStore().getCookies();
	}

	/**
	 * Clears the set of cookies. This will cause a logout.
	 *
	 * @throws IOException
	 */
	public void clearCookies() throws IOException, MediaWikiApiErrorException {
		logout();
		cookieManager.getCookieStore().removeAll();
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
	 * Logs the current user out.
	 *
	 * @throws IOException
	 */
	public void logout() throws IOException, MediaWikiApiErrorException {
		if (this.loggedIn) {
			Map<String, String> params = new HashMap<>();
			params.put("action", "logout");
			params.put("token", getOrFetchToken("csrf"));
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

	/**
	 * Wrapper for {@link HttpCookie}.
	 *
	 * Used for json deserialization.
	 *
	 * Since {@link HttpCookie} is final, we can't extend it here.
	 */
	protected static class HttpCookieWrapper {

		private HttpCookie httpCookie;

		@JsonCreator
		public HttpCookieWrapper(@JsonProperty("name") String name,
		                         @JsonProperty("value") String value,
		                         @JsonProperty("comment") String comment,
		                         @JsonProperty("commentURL") String commentURL,
		                         @JsonProperty("domain") String domain,
		                         @JsonProperty("maxAge") int maxAge,
		                         @JsonProperty("path") String path,
		                         @JsonProperty("portlist") String portlist,
		                         @JsonProperty("secure") boolean secure,
		                         @JsonProperty("httpOnly") boolean httpOnly,
		                         @JsonProperty("version") int version,
		                         @JsonProperty("discard") boolean discard) {
			httpCookie = new HttpCookie(name, value);
			httpCookie.setComment(comment);
			httpCookie.setCommentURL(commentURL);
			httpCookie.setDomain(domain);
			httpCookie.setMaxAge(maxAge);
			httpCookie.setPath(path);
			httpCookie.setPortlist(portlist);
			httpCookie.setSecure(secure);
			httpCookie.setHttpOnly(httpOnly);
			httpCookie.setVersion(version);
			httpCookie.setDiscard(discard);
		}

		public HttpCookie toHttpCookie() {
			return httpCookie;
		}
	}
}
