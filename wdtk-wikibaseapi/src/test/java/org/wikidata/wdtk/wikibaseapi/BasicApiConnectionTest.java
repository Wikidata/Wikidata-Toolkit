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

import java.io.IOException;
import java.net.HttpCookie;
import java.net.URL;
import java.util.*;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.*;
import org.wikidata.wdtk.testing.MockStringContentFactory;
import org.wikidata.wdtk.wikibaseapi.apierrors.AssertUserFailedException;
import org.wikidata.wdtk.wikibaseapi.apierrors.MaxlagErrorException;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

public class BasicApiConnectionTest {

	private final ObjectMapper mapper = new ObjectMapper();

	private static MockWebServer server;
	private BasicApiConnection connection;

	private String LOGGED_IN_SERIALIZED_CONNECTION = "{\"baseUrl\":\"" + server.url("/w/api.php") + "\",\"cookies\":[{\"name\":\"GeoIP\",\"value\":\"DE:13:Dresden:51.0500:13.7500:v4\",\"comment\":null,\"commentURL\":null,\"domain\":\"domain comparison should be skipped\",\"maxAge\":-1,\"path\":\"/\",\"portlist\":null,\"secure\":false,\"httpOnly\":false,\"version\":0,\"discard\":false},{\"name\":\"testwikidatawikiSession\",\"value\":\"c18ef92637227283bcda73bcf95cfaf5\",\"comment\":null,\"commentURL\":null,\"domain\":\"domain comparison should be skipped\",\"maxAge\":-1,\"path\":\"/\",\"portlist\":null,\"secure\":true,\"httpOnly\":true,\"version\":0,\"discard\":false}],\"username\":\"username\",\"loggedIn\":true,\"tokens\":{\"login\":\"b5780b6e2f27e20b450921d9461010b4\"},\"connectTimeout\":5000,\"readTimeout\":6000}";

	Set<String> split(String str, char ch) {
		Set<String> set = new TreeSet<>();
		StringTokenizer stok = new StringTokenizer(str, "" + ch);
		while (stok.hasMoreTokens()) {
			set.add(stok.nextToken().trim());
		}
		return set;
	}

	private static MockResponse makeJsonResponseFrom(String path) throws IOException {
		String body = MockStringContentFactory.getStringFromUrl(BasicApiConnectionTest.class.getResource(path));
		return new MockResponse()
				.addHeader("Content-Type", "application/json; charset=utf-8")
				.addHeader("Set-Cookie", "WMF-Last-Access=18-Aug-2015;Path=/;HttpOnly;Expires=Sat, 19 Sep 2015 12:00:00 GMT")
				.addHeader("Set-Cookie", "GeoIP=DE:13:Dresden:51.0500:13.7500:v4; Path=/; Domain=" + server.getHostName())
				.addHeader("Set-Cookie", "testwikidatawikiSession=c18ef92637227283bcda73bcf95cfaf5; path=/; secure; httponly")
				.setBody(body);
	}

	@BeforeClass
	public static void init() throws Exception {
		Dispatcher dispatcher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if ("/w/api.php?languages=fr&format=json&action=wbgetentities&ids=Q8&sitefilter=enwiki&props=info".equals(request.getPath())) {
					return new MockResponse()
							.setHeader("Content-Type", "application/json; charset=utf-8")
							.setBody("{\"entities\":{\"Q8\":{\"pageid\":134,\"ns\":0,\"title\":\"Q8\",\"lastrevid\":1174289176,\"modified\":\"2020-05-05T12:39:07Z\",\"type\":\"item\",\"id\":\"Q8\"}},\"success\":1}\n");
				}
				try {
					switch (request.getBody().readUtf8()) {
						case "meta=tokens&format=json&action=query&type=login":
							return makeJsonResponseFrom("/query-login-token.json");
						case "lgtoken=b5780b6e2f27e20b450921d9461010b4&lgpassword=password&format=json&action=login&lgname=username":
							return makeJsonResponseFrom("/loginSuccess.json");
						case "lgtoken=b5780b6e2f27e20b450921d9461010b4&lgpassword=password1&format=json&action=login&lgname=username1":
							return makeJsonResponseFrom("/loginError.json");
						case "meta=tokens&assert=user&format=json&action=query&type=csrf":
							return makeJsonResponseFrom("/query-csrf-token-loggedin-response.json");
						case "assert=user&format=json&action=logout&token=42307b93c79b0cb558d2dfb4c3c92e0955e06041%2B%5C":
							return new MockResponse().setHeader("Content-Type", "application/json; charset=utf-8").setBody("{}");
						case "assert=user&format=json&action=query":
							return makeJsonResponseFrom("/assert-user-failed.json");
					}
				}catch (Exception e) {
					return new MockResponse().setResponseCode(404);
				}
				return new MockResponse().setResponseCode(404);
			}
		};

		server = new MockWebServer();
		server.setDispatcher(dispatcher);
		server.start();
	}

	@AfterClass
	public static void finish() throws IOException {
		server.shutdown();
	}

	@Before
	public void setUp() {
		connection = new BasicApiConnection(server.url("/w/api.php").toString());
	}

	@Test
	public void testGetToken() throws LoginFailedException, IOException, MediaWikiApiErrorException, InterruptedException {
		connection.login("username", "password");
		assertNotNull(connection.getOrFetchToken("csrf"));
	}

	@Test
	public void testGetLoginToken() throws IOException, MediaWikiApiErrorException, InterruptedException, LoginFailedException {
		assertNotNull(connection.getOrFetchToken("login"));
	}

	@Test
	public void testConfirmLogin() throws LoginFailedException, IOException, MediaWikiApiErrorException {
		String token = connection.getOrFetchToken("login");
		connection.confirmLogin(token, "username", "password");
	}

	@Test
	public void testLogin() throws LoginFailedException {
		assertFalse(connection.loggedIn);
		connection.login("username", "password");
		assertEquals("username", connection.getCurrentUser());
		assertEquals("password", connection.password);
		assertTrue(connection.isLoggedIn());
	}


	@Test
	public void testSerialize() throws LoginFailedException, IOException {
		connection.login("username", "password");
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(6000);
		assertTrue(connection.isLoggedIn());
		String jsonSerialization = mapper.writeValueAsString(connection);
		// We skip comparing the cookie domains here, since they depend on
		// the mocked web server's host, which is system dependent.
		jsonSerialization = jsonSerialization.replaceAll("\"domain\":\"[^\"]*\"", "\"domain\":\"domain comparison should be skipped\"");
		assertEquals(LOGGED_IN_SERIALIZED_CONNECTION, jsonSerialization);
	}

	@Test
	public void testDeserialize() throws IOException {
		BasicApiConnection newConnection = mapper.readValue(LOGGED_IN_SERIALIZED_CONNECTION, BasicApiConnection.class);
		assertTrue(newConnection.isLoggedIn());
		assertEquals("username", newConnection.getCurrentUser());
		assertEquals(5000, newConnection.getConnectTimeout());
		assertEquals(6000, newConnection.getReadTimeout());
		assertEquals(server.url("/w/api.php").toString(), newConnection.getApiBaseUrl());
		List<HttpCookie> cookies = newConnection.getCookies();
		for (HttpCookie cookie : cookies) {
			if (cookie.getName().equals("GeoIP")) {
				assertEquals("DE:13:Dresden:51.0500:13.7500:v4", cookie.getValue());
			} else {
				assertEquals("testwikidatawikiSession", cookie.getName());
				assertEquals("c18ef92637227283bcda73bcf95cfaf5", cookie.getValue());
			}
		}
		Map<String, String> tokens = newConnection.getTokens();
		assertEquals("b5780b6e2f27e20b450921d9461010b4", tokens.get("login"));
		assertNull(tokens.get("csrf"));
	}

	@Test
	public void testLogout() throws IOException, LoginFailedException, MediaWikiApiErrorException {
		connection.login("username", "password");
		connection.logout();
		assertEquals("", connection.username);
		assertEquals("", connection.password);
		assertFalse(connection.loggedIn);
	}

	@Test(expected = LoginFailedException.class)
	public void loginUserErrors() throws LoginFailedException {
		// This will fail because the user is not known
		connection.login("username1", "password1");
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
				split(connection.getQueryString(params), '&'));
	}

	@Test
	public void testWarnings() throws IOException {
		JsonNode root;
		URL path = this.getClass().getResource("/warnings.json");
		root = mapper.readTree(path.openStream());
		List<String> warnings = connection.getWarnings(root);
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
		connection.checkErrors(root);
	}
	
	@Test
	public void testMaxlagError() throws IOException, MediaWikiApiErrorException {
		JsonNode root;
		URL path = this.getClass().getResource("/error-maxlag-full.json");
		root = mapper.readTree(path.openStream());
		try {
			connection.checkErrors(root);
		} catch(MaxlagErrorException e) {
			assertEquals(3.45, e.getLag(), 0.001);
		}
	}

	@Test
	public void testClearCookies() throws LoginFailedException, IOException, MediaWikiApiErrorException {
		connection.login("username", "password");
		assertFalse(connection.getCookies().isEmpty());
		connection.clearCookies();
		assertTrue(connection.getCookies().isEmpty());
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
		BasicApiConnection connection = BasicApiConnection.getTestWikidataApiConnection();
		String[] knownErrors = { BasicApiConnection.LOGIN_WRONG_PASS,
				BasicApiConnection.LOGIN_WRONG_PLUGIN_PASS,
				BasicApiConnection.LOGIN_NOT_EXISTS, BasicApiConnection.LOGIN_BLOCKED,
				BasicApiConnection.LOGIN_EMPTY_PASS, BasicApiConnection.LOGIN_NO_NAME,
				BasicApiConnection.LOGIN_CREATE_BLOCKED,
				BasicApiConnection.LOGIN_ILLEGAL, BasicApiConnection.LOGIN_THROTTLED,
				BasicApiConnection.LOGIN_WRONG_TOKEN, BasicApiConnection.LOGIN_NEEDTOKEN };

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
		connection.login("username", "password");
		assertTrue(connection.isLoggedIn());
		// after a while, the credentials expire
		connection.checkCredentials();
	}

	/**
	 * For backwards compatibility: by defaults, no timeouts
	 * are set by us, we use HttpURLConnection's defaults.
	 * @throws IOException
	 */
	@Test
	public void testNoTimeouts() throws IOException {
		assertEquals(-1, connection.getConnectTimeout());
		assertEquals(-1, connection.getReadTimeout());
	}

	@Test
	public void testConnectTimeout() throws IOException {
		connection.setConnectTimeout(5000);
		assertEquals(5000, connection.getConnectTimeout());
	}

	@Test
	public void testReadTimeout() throws IOException {
		connection.setReadTimeout(5000);
		assertEquals(5000, connection.getReadTimeout());
	}

	@Test
	public void testTimeouts() {
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		assertEquals(5000, connection.getConnectTimeout());
		assertEquals(5000, connection.getReadTimeout());
	}

	@Test
	public void testGetMethod() throws IOException, MediaWikiApiErrorException {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("action", "wbgetentities");
		parameters.put("languages", "fr");
		parameters.put("ids", "Q8");
		parameters.put("sitefilter", "enwiki");
		parameters.put("props", "info");
		JsonNode root = connection.sendJsonRequest("GET", parameters);
		assertEquals("{\"entities\":{\"Q8\":{\"pageid\":134,\"ns\":0,\"title\":\"Q8\",\"lastrevid\":1174289176,\"modified\":\"2020-05-05T12:39:07Z\",\"type\":\"item\",\"id\":\"Q8\"}},\"success\":1}", mapper.writeValueAsString(root));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUnsupportedMethod() throws IOException, MediaWikiApiErrorException {
		connection.sendJsonRequest("PUT", new HashMap<>());
	}
}
