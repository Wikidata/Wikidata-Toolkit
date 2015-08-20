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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.util.CompressionType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiConnectionTest {

	final ObjectMapper mapper = new ObjectMapper();

	MockApiConnection con;

	String username = "username";
	String password = "password";

	@Before
	public void setUp() throws Exception {
		this.con = new MockApiConnection();
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "login");
		params.put("lgname", this.username);
		params.put("lgpassword", this.password);
		params.put("format", "json");
		this.con.setWebResourceFromPath(params, this.getClass(),
				"/loginNeedToken.json", CompressionType.NONE);
		params.clear();
		params.put("action", "login");
		params.put("lgname", username);
		params.put("lgpassword", password);
		params.put("lgtoken", "b5780b6e2f27e20b450921d9461010b4");
		params.put("format", "json");
		this.con.setWebResourceFromPath(params, this.getClass(),
				"/loginSuccess.json", CompressionType.NONE);
		params.clear();
		params.put("action", "login");
		params.put("lgname", username);
		params.put("lgpassword", password);
		params.put("lgtoken", "blah");
		params.put("format", "json");
		this.con.setWebResourceFromPath(params, this.getClass(),
				"/loginError.json", CompressionType.NONE);
		params.clear();
		params.put("action", "logout");
		params.put("format", "json");
		this.con.setWebResource(params, "{}");
	}

	@Test
	public void testGetLoginToken() throws IOException {
		assertTrue(getLoginToken() != null);
	}

	@Test
	public void testConfirmLogin() throws LoginFailedException, IOException {
		String token = getLoginToken();
		this.con.confirmLogin(token, this.username, this.password);
	}

	@Test
	public void testLogin() throws LoginFailedException {
		assertFalse(this.con.loggedIn);
		this.con.login(this.username, this.password);
		assertEquals(this.username, this.con.getCurrentUser());
		assertEquals(this.password, this.con.password);
		assertTrue(this.con.isLoggedIn());
	}

	@Test
	public void testLogout() throws IOException, LoginFailedException {
		this.con.login(this.username, this.password);
		this.con.logout();
		assertEquals("", this.con.username);
		assertEquals("", this.con.password);
		assertEquals(false, this.con.loggedIn);
	}

	@Test(expected = LoginFailedException.class)
	public void loginErrors() throws IOException, LoginFailedException {
		@SuppressWarnings("unused")
		String token = this.con.getLoginToken(this.username, this.password);
		this.con.confirmLogin("blah", this.username, this.password);
	}

	@Test
	public void testGetQueryString() throws UnsupportedEncodingException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "login");
		params.put("lgname", username);
		params.put("lgpassword", password);
		params.put("lgtoken", "b5780b6e2f27e20b450921d9461010b4");
		params.put("format", "json");
		assertEquals(
				"lgtoken=b5780b6e2f27e20b450921d9461010b4&lgpassword=password"
						+ "&action=login&lgname=username&format=json",
				con.getQueryString(params));
	}

	@Test
	public void testParseErrorsAndWarnings() throws JsonProcessingException,
			IOException {
		JsonNode root;
		URL path = this.getClass().getResource("/warnings.json");
		root = mapper.readTree(path.openStream());
		assertTrue(con.parseErrorsAndWarnings(root));

		path = this.getClass().getResource("/error.json");
		root = mapper.readTree(path.openStream());
		assertFalse(con.parseErrorsAndWarnings(root));

	}

	@Test
	public void testFillCookies() {
		Map<String, List<String>> headerFields = new HashMap<String, List<String>>();
		List<String> cookieList = testCookieList();
		headerFields.put("Set-Cookie", cookieList);
		con.fillCookies(headerFields);
		assertEquals(con.cookies.get(" Domain"), ".wikidata.org");

	}

	@Test
	public void testGetCookieString() {
		Map<String, List<String>> headerFields = new HashMap<String, List<String>>();
		List<String> cookieList = testCookieList();
		headerFields.put("Set-Cookie", cookieList);
		con.fillCookies(headerFields);
		assertEquals(
				"HttpOnly;  httponly;  Path=/; GeoIP=DE:13:Dresden:51.0500:13.7500:v4;  "
						+ "Domain=.wikidata.org; Expires=Sat, 19 Sep 2015 12:00:00 GMT;  secure;  path=/; "
						+ "testwikidatawikiSession=c18ef92637227283bcda73bcf95cfaf5; WMF-Last-Access=18-Aug-2015; Path=/",
				con.getCookieString());
	}

	@Test
	public void testSetupConnection() throws IOException {
		URL url = new URL("http://example.org/");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		con.setupConnection("POST", "", connection);
		assertEquals("application/x-www-form-urlencoded",
				connection.getRequestProperty("Content-Type"));

	}

	@Test
	public void testClearCookies() throws IOException {
		con.cookies.put("Content", "some content");
		con.clearCookies();
		assertTrue(con.cookies.keySet().isEmpty());
	}

	@Test
	public void testGetWikidataApiConnection() {
		ApiConnection connection = ApiConnection.getWikidataApiConnection();
		assertEquals("https://www.wikidata.org/w/api.php/",
				connection.apiBaseUrl);
	}

	@Test
	public void testGetTestWikidataApiConnection() {
		ApiConnection connection = ApiConnection.getTestWikidataApiConnection();
		assertEquals("https://test.wikidata.org/w/api.php",
				connection.apiBaseUrl);
	}

	private List<String> testCookieList() {
		List<String> cookieList = new ArrayList<String>();
		cookieList
				.add("WMF-Last-Access=18-Aug-2015;Path=/;HttpOnly;Expires=Sat, 19 Sep 2015 12:00:00 GMT");
		cookieList
				.add("GeoIP=DE:13:Dresden:51.0500:13.7500:v4; Path=/; Domain=.wikidata.org");
		cookieList
				.add("testwikidatawikiSession=c18ef92637227283bcda73bcf95cfaf5; path=/; secure; httponly");
		return cookieList;
	}

	private String getLoginToken() throws IOException {
		String token = this.con.getLoginToken(this.username, this.password);
		return token;
	}

}
