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
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A connection to the MediaWiki API established via
 * standard login with username and password.
 * 
 * Users should migrate to this class rather than
 * instantiating {@class ApiConnection} directly
 * as ApiConnection will become an interface.
 * 
 * @author Antonin Delpeuch
 *
 */
public class BasicApiConnection extends ApiConnection {

	public BasicApiConnection(String apiBaseUrl) {
		super(apiBaseUrl);
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
	private BasicApiConnection(
			@JsonProperty("baseUrl") String apiBaseUrl,
			@JsonProperty("cookies") Map<String, String> cookies,
			@JsonProperty("username") String username,
			@JsonProperty("loggedIn") boolean loggedIn,
			@JsonProperty("tokens") Map<String, String> tokens) {
		super(apiBaseUrl, cookies, username, loggedIn, tokens);
	}
	
	/**
	 * Creates an API connection to wikidata.org.
	 *
	 * @deprecated to be migrated to {@class PasswordApiConnection}
	 * @return {@link BasicApiConnection}
	 */
	public static BasicApiConnection getWikidataApiConnection() {
		return new BasicApiConnection(ApiConnection.URL_WIKIDATA_API);
	}

	/**
	 * Creates an API connection to test.wikidata.org.
	 *
	 * @deprecated to be migrated to {@class PasswordApiConnection}
	 * @return {@link BasicApiConnection}
	 */
	public static BasicApiConnection getTestWikidataApiConnection() {
		return new BasicApiConnection(ApiConnection.URL_TEST_WIKIDATA_API);
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
	@Override
	public void login(String username, String password)
			throws LoginFailedException {
		super.login(username, password);
	}
	
	/**
	 * To be migrated from {@class ApiConnection}
	 */
	@Override
	void confirmLogin(String token, String username, String password)
			throws IOException, LoginFailedException, MediaWikiApiErrorException {
		super.confirmLogin(token, username, password);
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
	 * Reads out the Set-Cookie Header Fields and fills the cookie map of the
	 * API connection with it.
	 * 
	 * To be migrated from {@class ApiConnection}
	 */
	@Override
	void fillCookies(Map<String, List<String>> headerFields) {
		super.fillCookies(headerFields);
	}
	
	/**
	 * To be migrated from {@class ApiConnection}
	 */
	@Override
	String getCookieString() {
		return super.getCookieString();
	}
	
	/**
	 * Returns a user-readable message for a given API response.
	 *
	 * @deprecated to be migrated to {@class PasswordApiConnection}
	 * @param loginResult
	 *            a API login request result string other than
	 *            {@link #LOGIN_RESULT_SUCCESS}
	 * @return error message
	 */
	@Override
	String getLoginErrorMessage(String loginResult) {
		return super.getLoginErrorMessage(loginResult);
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
	void setupConnection(String requestMethod, String queryString,
			HttpURLConnection connection) throws IOException {
		super.setupConnection(requestMethod, queryString, connection);
		connection.setRequestProperty(ApiConnection.PARAM_COOKIE,
				getCookieString());
	}
	
}
