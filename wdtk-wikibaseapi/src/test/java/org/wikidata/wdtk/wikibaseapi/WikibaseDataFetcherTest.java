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
import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import org.wikidata.wdtk.wikibaseapi.apierrors.NoSuchEntityErrorException;

public class WikibaseDataFetcherTest {

	MockBasicApiConnection con;
	WikibaseDataFetcher wdf;

	@Before
	public void setUp() throws Exception {
		con = new MockBasicApiConnection();
		wdf = new WikibaseDataFetcher(con, Datamodel.SITE_WIKIDATA);
	}

	@Test
	public void testWbGetEntities() throws IOException,
			MediaWikiApiErrorException {
		Map<String, String> parameters = new HashMap<String, String>();
		setStandardParameters(parameters);
		parameters.put("ids", "Q6|Q42|P31");
		con.setWebResourceFromPath(parameters, this.getClass(),
				"/wbgetentities-Q6-Q42-P31.json", CompressionType.NONE);

		Map<String, EntityDocument> results = wdf.getEntityDocuments("Q6",
				"Q42", "P31");

		assertEquals(2, results.size());
		assertFalse(results.containsKey("Q6"));
		assertTrue(results.containsKey("Q42"));
		assertTrue(results.containsKey("P31"));
	}

	@Test
	public void testGetEntityDocument() throws IOException,
			MediaWikiApiErrorException {
		// We use the mock answer as for a multi request; no problem
		Map<String, String> parameters = new HashMap<String, String>();
		setStandardParameters(parameters);
		parameters.put("ids", "Q42");
		con.setWebResourceFromPath(parameters, this.getClass(),
				"/wbgetentities-Q6-Q42-P31.json", CompressionType.NONE);

		EntityDocument result = wdf.getEntityDocument("Q42");
		assertTrue(result != null);
	}

	@Test
	public void testGetMissingEntityDocument() throws IOException,
			MediaWikiApiErrorException {
		// List<String> entityIds = Arrays.asList("Q6");
		Map<String, String> parameters = new HashMap<String, String>();
		setStandardParameters(parameters);
		parameters.put("ids", "Q6");
		// We use the mock answer as for a multi request; no problem
		con.setWebResourceFromPath(parameters, getClass(),
				"/wbgetentities-Q6-Q42-P31.json", CompressionType.NONE);
		EntityDocument result = wdf.getEntityDocument("Q6");

		assertEquals(null, result);
	}

	@Test(expected = NoSuchEntityErrorException.class)
	public void testWbGetEntitiesError() throws IOException,
			MediaWikiApiErrorException {
		Map<String, String> parameters = new HashMap<String, String>();
		setStandardParameters(parameters);
		parameters.put("ids", "bogus");
		// We use the mock answer as for a multi request; no problem
		con.setWebResourceFromPath(parameters, getClass(),
				"/wbgetentities-bogus.json", CompressionType.NONE);
		wdf.getEntityDocuments("bogus");
	}

	@Test
	public void testWbGetEntitiesEmpty() throws IOException,
			MediaWikiApiErrorException {

		Map<String, EntityDocument> results = wdf
				.getEntityDocuments(Collections.<String> emptyList());

		assertEquals(0, results.size());
	}

	@Test
	public void testWbGetEntitiesTitle() throws IOException,
			MediaWikiApiErrorException {
		Map<String, String> parameters = new HashMap<String, String>();
		this.setStandardParameters(parameters);
		parameters.put("titles", "Douglas Adams");
		parameters.put("sites", "enwiki");
		con.setWebResourceFromPath(parameters, getClass(),
				"/wbgetentities-Douglas-Adams.json", CompressionType.NONE);

		EntityDocument result = wdf.getEntityDocumentByTitle("enwiki",
				"Douglas Adams");

		assertEquals("Q42", result.getEntityId().getId());
	}

	@Test
	public void testWbGetEntitiesTitleEmpty() throws IOException,
			MediaWikiApiErrorException {
		Map<String, String> parameters = new HashMap<String, String>();
		this.setStandardParameters(parameters);
		parameters.put("titles", "1234567890");
		parameters.put("sites", "dewiki");
		con.setWebResourceFromPath(parameters, getClass(),
				"/wbgetentities-1234567890-missing.json", CompressionType.NONE);

		EntityDocument result = wdf.getEntityDocumentByTitle("dewiki",
				"1234567890");

		assertEquals(null, result);
	}

	@Test
	public void testWikidataDataFetcher() throws IOException {
		WikibaseDataFetcher wbdf = WikibaseDataFetcher.getWikidataDataFetcher();

		assertEquals(Datamodel.SITE_WIKIDATA, wbdf.siteIri);
		assertEquals(ApiConnection.URL_WIKIDATA_API,
				wbdf.wbGetEntitiesAction.connection.apiBaseUrl);
	}

	@Test
	public void testWbGetEntitesSplitted() throws IOException,
			MediaWikiApiErrorException {
		List<String> entityIds = Arrays.asList("Q6", "Q42", "P31", "Q1");

		Map<String, String> parameters1 = new HashMap<String, String>();
		setStandardParameters(parameters1);
		parameters1.put("ids", "Q6|Q42|P31");

		Map<String, String> parameters2 = new HashMap<String, String>();
		setStandardParameters(parameters2);
		parameters2.put("ids", "Q1");

		con.setWebResourceFromPath(parameters1, this.getClass(),
				"/wbgetentities-Q6-Q42-P31.json", CompressionType.NONE);
		con.setWebResourceFromPath(parameters2, this.getClass(),
				"/wbgetentities-Q1.json", CompressionType.NONE);

		wdf.maxListSize = 3;

		Map<String, EntityDocument> results = wdf.getEntityDocuments(entityIds);

		assertEquals(3, results.size());
		assertFalse(results.containsKey("Q6"));
		assertTrue(results.containsKey("Q1"));
		assertTrue(results.containsKey("P31"));
		assertTrue(results.containsKey("Q42"));
	}

	@Test
	public void testGetEntitiesTitleSplitted() throws IOException,
			MediaWikiApiErrorException {
		Map<String, String> parameters1 = new HashMap<String, String>();
		this.setStandardParameters(parameters1);
		parameters1.put("titles", "Douglas Adams");
		parameters1.put("sites", "enwiki");
		con.setWebResourceFromPath(parameters1, getClass(),
				"/wbgetentities-Douglas-Adams.json", CompressionType.NONE);

		Map<String, String> parameters2 = new HashMap<String, String>();
		this.setStandardParameters(parameters2);
		parameters2.put("titles", "Oliver Kahn");
		parameters2.put("sites", "enwiki");
		con.setWebResourceFromPath(parameters2, getClass(),
				"/wbgetentites-Oliver-Kahn.json", CompressionType.NONE);

		wdf.maxListSize = 1;

		Map<String, EntityDocument> result = wdf.getEntityDocumentsByTitle(
				"enwiki", "Oliver Kahn", "Douglas Adams");

		assertEquals(2, result.keySet().size());
		assertEquals("Q42", result.get("Douglas Adams").getEntityId().getId());
		assertEquals("Q131261", result.get("Oliver Kahn").getEntityId().getId());
	}
  
	private void setStandardParameters(Map<String, String> parameters) {
		parameters.put("action", "wbgetentities");
		parameters.put("format", "json");
		parameters.put("props",
				"info|datatype|labels|aliases|descriptions|claims|sitelinks");
	}

	public void testWbSearchEntities() throws IOException, MediaWikiApiErrorException {
		Map<String, String> parameters = new HashMap<String, String>();
		setStandardSearchParameters(parameters);
		parameters.put("search", "abc");
		parameters.put("language", "en");
		con.setWebResourceFromPath(parameters, this.getClass(),
				"/wbsearchentities-abc.json", CompressionType.NONE);

		List<WbSearchEntitiesResult> results = wdf.searchEntities("abc");

		assertEquals(7, results.size());
		List<String> expectedIds = new ArrayList<>();
		expectedIds.add("Q169889");
		expectedIds.add("Q286874");
		expectedIds.add("Q781365");
		expectedIds.add("Q287076");
		expectedIds.add("Q304330");
		expectedIds.add("Q1057802");
		expectedIds.add("Q26298");
		List<String> actualIds = new ArrayList<>();
		for (WbSearchEntitiesResult result: results) {
			actualIds.add(result.getEntityId());
		}
		assertEquals(expectedIds, actualIds);
	}

	private void setStandardSearchParameters(Map<String, String> parameters) {
		parameters.put("action", "wbsearchentities");
		parameters.put("format", "json");
	}
}
