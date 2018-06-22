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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.wikibaseapi.apierrors.MaxlagErrorException;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import org.wikidata.wdtk.wikibaseapi.apierrors.TokenErrorException;

import com.fasterxml.jackson.databind.JsonNode;

public class WbEditingActionTest {

	@Test(expected = IOException.class)
	public void testOffineErrors() throws IOException,
			MediaWikiApiErrorException {
		MockBasicApiConnection con = new MockBasicApiConnection();
		WbEditingAction weea = new WbEditingAction(con,
				Datamodel.SITE_WIKIDATA);

		EntityDocument result = weea.wbEditEntity("Q42", null, null, null,
				"{}", true, false, 0, null);
		assertEquals(null, result);
	}

	@Test(expected = TokenErrorException.class)
	public void testApiErrorGettingToken() throws IOException,
			MediaWikiApiErrorException {
		MockBasicApiConnection con = new MockBasicApiConnection();
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "query");
		params.put("meta", "tokens");
		params.put("type", "csrf");
		params.put("format", "json");
		// This error makes no sense for this action, but that does not matter
		// here:
		con.setWebResourceFromPath(params, this.getClass(),
				"/error-badtoken.json", CompressionType.NONE);

		params.clear();
		params.put("action", "wbeditentity");
		params.put("id", "Q42");
		params.put("token", null);
		params.put("format", "json");
		params.put("data", "{}");
		params.put("maxlag", "5");
		con.setWebResourceFromPath(params, this.getClass(),
				"/error-badtoken.json", CompressionType.NONE);

		WbEditingAction weea = new WbEditingAction(con,
				Datamodel.SITE_WIKIDATA);
		weea.wbEditEntity("Q42", null, null, null, "{}", false, false, 0, null);
	}

	@Test(expected = TokenErrorException.class)
	public void testNoTokenInReponse() throws IOException,
			MediaWikiApiErrorException {
		MockBasicApiConnection con = new MockBasicApiConnection();
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "query");
		params.put("meta", "tokens");
		params.put("format", "json");
		// This error makes no sense for this action, but that does not matter
		// here:
		con.setWebResource(params, "{}");

		params.clear();
		params.put("action", "wbeditentity");
		params.put("id", "Q42");
		params.put("token", null);
		params.put("format", "json");
		params.put("data", "{}");
		params.put("maxlag", "5");
		con.setWebResourceFromPath(params, this.getClass(),
				"/error-badtoken.json", CompressionType.NONE);

		WbEditingAction weea = new WbEditingAction(con,
				Datamodel.SITE_WIKIDATA);

		weea.wbEditEntity("Q42", null, null, null, "{}", false, false, 0, null);
	}

	@Test(expected = MaxlagErrorException.class)
	public void testApiErrorMaxLag() throws IOException,
			MediaWikiApiErrorException {
		MockBasicApiConnection con = new MockBasicApiConnection();
		Map<String, String> params = new HashMap<String, String>();
		params.put("action", "query");
		params.put("meta", "tokens");
		params.put("type", "csrf");
		params.put("format", "json");
		con.setWebResourceFromPath(params, this.getClass(),
				"/query-csrf-token-loggedin-response.json",
				CompressionType.NONE);

		params.clear();
		params.put("action", "wbeditentity");
		params.put("id", "Q42");
		params.put("token", "42307b93c79b0cb558d2dfb4c3c92e0955e06041+\\");
		params.put("format", "json");
		params.put("data", "{}");
		params.put("maxlag", "5");
		con.setWebResourceFromPath(params, this.getClass(),
				"/error-maxlag.json", CompressionType.NONE);

		WbEditingAction weea = new WbEditingAction(con,
				Datamodel.SITE_WIKIDATA);
		WbEditingAction.MAXLAG_SLEEP_TIME = 0; // speed up the test ...
		weea.wbEditEntity("Q42", null, null, null, "{}", false, false, 0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIdAndSite() throws IOException, MediaWikiApiErrorException {
		WbEditingAction weea = new WbEditingAction(
				new MockBasicApiConnection(), Datamodel.SITE_WIKIDATA);
		weea.wbEditEntity("Q1234", "enwiki", null, null, "{}", false, false, 0,
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIdAndTitle() throws IOException, MediaWikiApiErrorException {
		WbEditingAction weea = new WbEditingAction(
				new MockBasicApiConnection(), Datamodel.SITE_WIKIDATA);
		weea.wbEditEntity("Q1234", null, "Title", null, "{}", false, false, 0,
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testTitleNoSite() throws IOException,
			MediaWikiApiErrorException {
		WbEditingAction weea = new WbEditingAction(
				new MockBasicApiConnection(), Datamodel.SITE_WIKIDATA);
		weea.wbEditEntity(null, null, "Title", null, "{}", false, false, 0,
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNewAndId() throws IOException, MediaWikiApiErrorException {
		WbEditingAction weea = new WbEditingAction(
				new MockBasicApiConnection(), Datamodel.SITE_WIKIDATA);
		weea.wbEditEntity("Q1234", null, null, "item", "{}", false, false, 0,
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNewAndSite() throws IOException, MediaWikiApiErrorException {
		WbEditingAction weea = new WbEditingAction(
				new MockBasicApiConnection(), Datamodel.SITE_WIKIDATA);
		weea.wbEditEntity(null, "enwiki", null, "item", "{}", false, false, 0,
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNewAndTitle() throws IOException,
			MediaWikiApiErrorException {
		WbEditingAction weea = new WbEditingAction(
				new MockBasicApiConnection(), Datamodel.SITE_WIKIDATA);
		weea.wbEditEntity(null, null, "Title", "item", "{}", false, false, 0,
				null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoTask() throws IOException, MediaWikiApiErrorException {
		WbEditingAction weea = new WbEditingAction(
				new MockBasicApiConnection(), Datamodel.SITE_WIKIDATA);
		weea.wbEditEntity(null, null, null, null, "{}", false, false, 0, null);
	}

}
