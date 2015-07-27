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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
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

	private String getLoginToken() throws IOException {
		String token = this.con.getLoginToken(this.username, this.password);
		return token;
	}

}
