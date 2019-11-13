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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.wikibaseapi.apierrors.AssertUserFailedException;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

public class BasicApiConnectionTest {

	final ObjectMapper mapper = new ObjectMapper();

	MockBasicApiConnection con;

	private String LOGGED_IN_SERIALIZED_CONNECTION = "{\"loggedIn\":true,\"cookies\":{\"Path\":\"/\","+
			"\"GeoIP\":\"DE:13:Dresden:51.0500:13.7500:v4\",\" path\":\"/\",\" Domain\":\".wikidata.org\","+
			"\"testwikidatawikiSession\":\"c18ef92637227283bcda73bcf95cfaf5\",\" secure\":\"\","+
			"\"WMF-Last-Access\":\"18-Aug-2015\",\"Expires\":\"Sat, 19 Sep 2015 12:00:00 GMT\",\"HttpOnly\":\"\","+
			"\" Path\":\"/\",\" httponly\":\"\"},\"username\":\"username\",\"connectTimeout\":-1,\"readTimeout\":-1}";
	
	Set<String> split(String str, char ch) {
		Set<String> set = new TreeSet<>();
		StringTokenizer stok = new StringTokenizer(str, "" + ch);
		while (stok.hasMoreTokens()) {
			set.add(stok.nextToken().trim());
		}
		return set;
	}

	@Before
	public void setUp() throws Exception {
		this.con = new MockBasicApiConnection();
		Map<String, String> params = new HashMap<>();
		params.put("action", "query");
		params.put("meta", "tokens");
		params.put("type", "csrf");
		params.put("format", "json");
		params.put("assert", "user");
		this.con.setWebResourceFromPath(params, this.getClass(),
				"/query-csrf-token-loggedin-response.json", CompressionType.NONE);
		params.clear();
		params.put("action", "query");
		params.put("meta", "tokens");
		params.put("type", "login");
		params.put("format", "json");
		this.con.setWebResourceFromPath(params, this.getClass(),
				"/query-login-token.json", CompressionType.NONE);
		params.clear();
		params.put("action", "login");
		params.put("lgname", "username");
		params.put("lgpassword", "password");
		params.put("lgtoken", "b5780b6e2f27e20b450921d9461010b4");
		params.put("format", "json");
		this.con.setWebResourceFromPath(params, this.getClass(),
				"/loginSuccess.json", CompressionType.NONE);

		params.clear();
		params.put("action", "login");
		params.put("lgname", "username2");
		params.put("lgpassword", "password2");
		params.put("lgtoken", "anothertoken");
		params.put("format", "json");
		this.con.setWebResourceFromPath(params, this.getClass(),
				"/loginError.json", CompressionType.NONE);

		params.clear();
		params.put("action", "login");
		params.put("lgname", "username3");
		params.put("lgpassword", "password3");
		params.put("lgtoken", "b5780b6e2f27e20b450921d9461010b4");
		params.put("format", "json");
		this.con.setWebResourceFromPath(params, this.getClass(),
				"/loginError2.json", CompressionType.NONE);

		params.clear();
		params.put("action", "logout");
		params.put("assert", "user");
		params.put("format", "json");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		this.con.setWebResource(params, "{}");

		params.clear();
		params.put("action", "query");
		params.put("assert", "user");
		params.put("format", "json");
		this.con.setWebResourceFromPath(params, this.getClass(),
				"/assert-user-failed.json", CompressionType.NONE);
		params.clear();
	}

	@Test
	public void testGetToken() throws LoginFailedException, IOException, MediaWikiApiErrorException {
		this.con.login("username", "password");
		assertNotNull(this.con.getOrFetchToken("csrf"));
	}

	@Test(expected = IOException.class)
	public void testGetTokenWithoutLogin() throws IOException, MediaWikiApiErrorException {
		this.con.getOrFetchToken("csrf");
	}

	@Test
	public void testGetLoginToken() throws IOException, MediaWikiApiErrorException {
		assertNotNull(this.con.getOrFetchToken("login"));
	}

	@Test
	public void testConfirmLogin() throws LoginFailedException, IOException, MediaWikiApiErrorException {
		String token = this.con.getOrFetchToken("login");
		this.con.confirmLogin(token, "username", "password");
	}

	@Test
	public void testLogin() throws LoginFailedException {
		assertFalse(this.con.loggedIn);
		this.con.login("username", "password");
		assertEquals("username", this.con.getCurrentUser());
		assertEquals("password", this.con.password);
		assertTrue(this.con.isLoggedIn());
	}
		

	@Test
	public void testSerialize() throws LoginFailedException, IOException {
		
		Map<String, List<String>> headerFields = new HashMap<>();
		List<String> cookieList = testCookieList();
		headerFields.put("Set-Cookie", cookieList);
		
		con.login("username", "password");
		con.fillCookies(headerFields);
		
		
		assertTrue(this.con.isLoggedIn());
		String jsonSerialization = mapper.writeValueAsString(this.con);
		JsonNode tree1 = mapper.readTree(LOGGED_IN_SERIALIZED_CONNECTION);
		JsonNode tree2 = mapper.readTree(jsonSerialization);
		Assert.assertEquals(tree1, tree2);
	}
	
	@Test
	public void testDeserialize() throws IOException {
		ApiConnection newConn = mapper.readValue(LOGGED_IN_SERIALIZED_CONNECTION, ApiConnection.class);
		assertTrue(newConn.isLoggedIn());
		assertEquals("username", newConn.getCurrentUser());
	}

	@Test
	public void testLogout() throws IOException, LoginFailedException, MediaWikiApiErrorException {
		this.con.login("username", "password");
		this.con.logout();
		assertEquals("", this.con.username);
		assertEquals("", this.con.password);
		assertFalse(this.con.loggedIn);
	}

	@Test(expected = LoginFailedException.class)
	public void loginErrors() throws LoginFailedException {
		// This will fail because the token returned is not correct
		this.con.login("username2", "password2");
	}

	@Test(expected = LoginFailedException.class)
	public void loginUserErrors() throws LoginFailedException {
		// This will fail because the user is not known
		this.con.login("username3", "password3");
	}

	@Test(expected = LoginFailedException.class)
	public void loginIoErrors() throws LoginFailedException {
		// This will fail because the request will throw an IO exception
		this.con.login("notmocked", "notmocked");
	}

	@Test
	public void testGetQueryString() {
		Map<String, String> params = new HashMap<>();
		params.put("action", "login");
		params.put("lgname", "username");
		params.put("lgpassword", "password");
		params.put("lgtoken", "b5780b6e2f27e20b450921d9461010b4");
		params.put("format", "json");
		assertEquals(
				split("lgtoken=b5780b6e2f27e20b450921d9461010b4&lgpassword=password"
						+ "&action=login&lgname=username&format=json", '&'),
				split(con.getQueryString(params), '&'));
	}

	@Test
	public void testWarnings() throws IOException {
		JsonNode root;
		URL path = this.getClass().getResource("/warnings.json");
		root = mapper.readTree(path.openStream());
		List<String> warnings = con.getWarnings(root);
		List<String> expectedWarnings = Arrays
				.asList("[main]: Unrecognized parameter: 'rmparam'",
						"[query]: Unrecognized value for parameter 'list': raremodule",
						"[wbeditentity]: Your edit was patched into the latest version, overriding some of your own intermediate changes.",
						"[test]: Warning was not understood. Please report this to Wikidata Toolkit. JSON source: {\"unknown\":\"structure\"}");
		assertEquals(expectedWarnings, warnings);
	}

	@Test(expected = MediaWikiApiErrorException.class)
	public void testErrors() throws IOException,
			MediaWikiApiErrorException {
		JsonNode root;
		URL path = this.getClass().getResource("/error.json");
		root = mapper.readTree(path.openStream());
		con.checkErrors(root);
	}

	@Test
	public void testFillCookies() {
		Map<String, List<String>> headerFields = new HashMap<>();
		List<String> cookieList = testCookieList();
		headerFields.put("Set-Cookie", cookieList);
		con.fillCookies(headerFields);
		assertEquals(con.cookies.get(" Domain"), ".wikidata.org");
	}

	@Test
	public void testGetCookieString() {
		Map<String, List<String>> headerFields = new HashMap<>();
		List<String> cookieList = testCookieList();
		headerFields.put("Set-Cookie", cookieList);
		con.fillCookies(headerFields);
		assertEquals(
				split("HttpOnly;  httponly;  Path=/; GeoIP=DE:13:Dresden:51.0500:13.7500:v4;  "
						+ "Domain=.wikidata.org; Expires=Sat, 19 Sep 2015 12:00:00 GMT;  secure;  path=/; "
						+ "testwikidatawikiSession=c18ef92637227283bcda73bcf95cfaf5; WMF-Last-Access=18-Aug-2015; Path=/", ';'),
				split(con.getCookieString(), ';'));
	}

	@Test
	public void testSetupConnection() throws IOException {
		URL url = new URL("http://example.org/");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		con.setupConnection("POST", "", connection);
		assertEquals("POST",
				connection.getRequestMethod());
		assertEquals("application/x-www-form-urlencoded",
				connection.getRequestProperty("Content-Type"));

	}

	@Test
	public void testClearCookies() throws IOException, MediaWikiApiErrorException {
		con.cookies.put("Content", "some content");
		con.clearCookies();
		assertTrue(con.cookies.keySet().isEmpty());
	}

	@Test
	public void testGetWikidataApiConnection() {
		ApiConnection connection = BasicApiConnection.getWikidataApiConnection();
		assertEquals("https://www.wikidata.org/w/api.php",
				connection.apiBaseUrl);
	}

	@Test
	public void testGetTestWikidataApiConnection() {
		ApiConnection connection = BasicApiConnection.getTestWikidataApiConnection();
		assertEquals("https://test.wikidata.org/w/api.php",
				connection.apiBaseUrl);
	}

	@Test
	public void testErrorMessages() {
		ApiConnection connection = BasicApiConnection.getTestWikidataApiConnection();
		String[] knownErrors = { ApiConnection.LOGIN_WRONG_PASS,
				ApiConnection.LOGIN_WRONG_PLUGIN_PASS,
				ApiConnection.LOGIN_NOT_EXISTS, ApiConnection.LOGIN_BLOCKED,
				ApiConnection.LOGIN_EMPTY_PASS, ApiConnection.LOGIN_NO_NAME,
				ApiConnection.LOGIN_CREATE_BLOCKED,
				ApiConnection.LOGIN_ILLEGAL, ApiConnection.LOGIN_THROTTLED,
				ApiConnection.LOGIN_WRONG_TOKEN, ApiConnection.LOGIN_NEEDTOKEN };

		ArrayList<String> messages = new ArrayList<>();
		for (String error : knownErrors) {
			messages.add(connection.getLoginErrorMessage(error));
		}

		String unknownMessage = connection
				.getLoginErrorMessage("unkonwn error code");

		int i = 0;
		for (String message : messages) {
			assertNotEquals(unknownMessage, message);
			assertTrue(message.contains(knownErrors[i]));
			i++;
		}
	}
	
	@Test(expected = AssertUserFailedException.class)
	public void testCheckCredentials() throws IOException, MediaWikiApiErrorException, LoginFailedException {
		// we first login successfully
		this.con.login("username", "password");
		assertTrue(this.con.isLoggedIn());
		// after a while, the credentials expire
		this.con.checkCredentials();
	}
	
	/**
	 * For backwards compatibility: by defaults, no timeouts
	 * are set by us, we use HttpURLConnection's defaults.
	 * @throws IOException
	 */
	@Test
	public void testNoTimeouts() throws IOException {
		HttpURLConnection urlConn = mock(HttpURLConnection.class);
		
		this.con.setupConnection("GET", "foo=bar", urlConn);
		
		verify(urlConn, times(0)).setConnectTimeout(anyInt());
		verify(urlConn, times(0)).setReadTimeout(anyInt());
	}
	
	@Test
	public void testConnectTimeout() throws IOException {
		HttpURLConnection urlConn = mock(HttpURLConnection.class);
		
		this.con.setConnectTimeout(1000);
		this.con.setupConnection("GET", "foo=bar", urlConn);
		
		assertEquals(con.getConnectTimeout(), 1000);
		verify(urlConn, times(1)).setConnectTimeout(1000);
		verify(urlConn, times(0)).setReadTimeout(anyInt());
	}
	
	@Test
	public void testReadTimeout() throws IOException {
		HttpURLConnection urlConn = mock(HttpURLConnection.class);
		
		this.con.setReadTimeout(2000);
		this.con.setupConnection("GET", "foo=bar", urlConn);
		
		assertEquals(con.getReadTimeout(), 2000);
		verify(urlConn, times(0)).setConnectTimeout(anyInt());
		verify(urlConn, times(1)).setReadTimeout(2000);
	}

	private List<String> testCookieList() {
		List<String> cookieList = new ArrayList<>();
		cookieList
				.add("WMF-Last-Access=18-Aug-2015;Path=/;HttpOnly;Expires=Sat, 19 Sep 2015 12:00:00 GMT");
		cookieList
				.add("GeoIP=DE:13:Dresden:51.0500:13.7500:v4; Path=/; Domain=.wikidata.org");
		cookieList
				.add("testwikidatawikiSession=c18ef92637227283bcda73bcf95cfaf5; path=/; secure; httponly");
		return cookieList;
	}

}
