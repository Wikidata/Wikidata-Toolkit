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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.testing.MockWebResourceFetcher;

public class WikibaseDataFetcherTest {

	@Test
	public void testWbGetEntities() throws IOException {
		List<String> entityIds = Arrays.asList("Q6", "Q42", "P31");

		WikibaseDataFetcher wdf = new WikibaseDataFetcher();

		MockWebResourceFetcher wrf = new MockWebResourceFetcher();
		wrf.setWebResourceContentsFromResource(
				wdf.getWbGetEntitiesUrl(entityIds),
				"/wbgetentities-Q6-Q42-P31.json", this.getClass());

		wdf.webResourceFetcher = wrf;

		Map<String, EntityDocument> results = wdf.getEntityDocuments("Q6",
				"Q42", "P31");

		assertEquals(2, results.size());
		assertFalse(results.containsKey("Q6"));
		assertTrue(results.containsKey("Q42"));
		assertTrue(results.containsKey("P31"));
	}

	@Test
	public void testGetEntityDocument() throws IOException {
		List<String> entityIds = Arrays.asList("Q42");

		WikibaseDataFetcher wdf = new WikibaseDataFetcher();

		// We use the mock answer as for a multi request; no problem
		MockWebResourceFetcher wrf = new MockWebResourceFetcher();
		wrf.setWebResourceContentsFromResource(
				wdf.getWbGetEntitiesUrl(entityIds),
				"/wbgetentities-Q6-Q42-P31.json", this.getClass());

		wdf.webResourceFetcher = wrf;

		EntityDocument result = wdf.getEntityDocument("Q42");

		assertTrue(result != null);
	}

	@Test
	public void testGetMissingEntityDocument() throws IOException {
		List<String> entityIds = Arrays.asList("Q6");

		WikibaseDataFetcher wdf = new WikibaseDataFetcher();

		// We use the mock answer as for a multi request; no problem
		MockWebResourceFetcher wrf = new MockWebResourceFetcher();
		wrf.setWebResourceContentsFromResource(
				wdf.getWbGetEntitiesUrl(entityIds),
				"/wbgetentities-Q6-Q42-P31.json", this.getClass());

		wdf.webResourceFetcher = wrf;

		EntityDocument result = wdf.getEntityDocument("Q6");

		assertTrue(result == null);
	}

	@Test
	public void testWbGetEntitiesError() throws IOException {
		List<String> entityIds = Arrays.asList("bogus");
		WikibaseDataFetcher wdf = new WikibaseDataFetcher();

		MockWebResourceFetcher wrf = new MockWebResourceFetcher();
		wrf.setWebResourceContentsFromResource(
				wdf.getWbGetEntitiesUrl(entityIds),
				"/wbgetentities-bogus.json", this.getClass());

		wdf.webResourceFetcher = wrf;

		Map<String, EntityDocument> results = wdf.getEntityDocuments("bogus");

		assertEquals(0, results.size());
	}

	@Test
	public void testWbGetEntitiesEmpty() throws IOException {
		MockWebResourceFetcher wrf = new MockWebResourceFetcher();

		WikibaseDataFetcher wdf = new WikibaseDataFetcher();
		wdf.webResourceFetcher = wrf;

		Map<String, EntityDocument> results = wdf
				.getEntityDocuments(Collections.<String> emptyList());

		assertEquals(0, results.size());
	}

	@Test
	public void testWbGetEntitiesNoWebAccess() throws IOException {
		MockWebResourceFetcher wrf = new MockWebResourceFetcher();
		wrf.setReturnFailingReaders(true);

		WikibaseDataFetcher wdf = new WikibaseDataFetcher();
		wdf.webResourceFetcher = wrf;

		Map<String, EntityDocument> results = wdf.getEntityDocuments("Q6",
				"Q42", "P31");

		// No data mocked, no results (but also no exception thrown)
		assertEquals(0, results.size());
	}

	@Test
	public void testWbGetEntitiesApiUrlError() throws IOException {
		MockWebResourceFetcher wrf = new MockWebResourceFetcher();

		WikibaseDataFetcher wdf = new WikibaseDataFetcher("invalid URL",
				Datamodel.SITE_WIKIDATA);
		wdf.webResourceFetcher = wrf;

		Map<String, EntityDocument> results = wdf.getEntityDocuments("Q6",
				"Q42", "P31");

		assertEquals(0, results.size());
	}

	@Test
	public void testWbGetEntitiesUrl() throws IOException {
		List<String> entityIds = Arrays.asList("Q6", "Q42", "P31");
		WikibaseDataFetcher wdf = new WikibaseDataFetcher();
		assertEquals(
				"https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&props=datatype%7Clabels%7Caliases%7Cdescriptions%7Cclaims%7Csitelinks&ids=Q6%7CQ42%7CP31",
				wdf.getWbGetEntitiesUrl(entityIds));
	}

	@Test
	public void testWbGetEntitiesUrlTitle() throws IOException {
		List<String> titles = Collections
				.<String> singletonList("Douglas Adams");
		String siteKey = "enwiki";
		WikibaseDataFetcher wdf = new WikibaseDataFetcher();
		assertEquals(
				"https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&props=datatype%7Clabels%7Caliases%7Cdescriptions%7Cclaims%7Csitelinks&sites=enwiki&titles=Douglas+Adams",
				wdf.getWbGetEntitiesUrl(siteKey, titles));
	}

	@Test
	public void testWbGetEntitiesTitle() throws IOException {
		WikibaseDataFetcher wdf = new WikibaseDataFetcher();

		MockWebResourceFetcher wrf = new MockWebResourceFetcher();
		wrf.setWebResourceContentsFromResource(
				wdf.getWbGetEntitiesUrl("enwiki",
						Collections.<String> singletonList("Douglas Adams")),
				"/wbgetentities-Douglas-Adams.json", this.getClass());

		wdf.webResourceFetcher = wrf;

		EntityDocument result = wdf.getEntityDocumentByTitle("enwiki",
				"Douglas Adams");

		assertEquals("Q42", result.getEntityId().getId());
	}

	@Test
	public void testWbGetEntitiesTitleEmpty() throws IOException {
		WikibaseDataFetcher wdf = new WikibaseDataFetcher();

		MockWebResourceFetcher wrf = new MockWebResourceFetcher();
		wrf.setWebResourceContentsFromResource(
				wdf.getWbGetEntitiesUrl("dewiki",
						Collections.<String> singletonList("1234567890")),
				"/wbgetentities-1234567890-missing.json", this.getClass());

		wdf.webResourceFetcher = wrf;

		EntityDocument result = wdf.getEntityDocumentByTitle("dewiki",
				"1234567890");

		assertEquals(null, result);
	}

	@Test
	public void testWbGetEntitiesUrlFilterAll() throws IOException {
		List<String> entityIds = Arrays.asList("Q6", "Q42", "P31");
		WikibaseDataFetcher wdf = new WikibaseDataFetcher();
		wdf.getFilter().setLanguageFilter(Collections.<String> emptySet());
		wdf.getFilter().setPropertyFilter(
				Collections.<PropertyIdValue> emptySet());
		wdf.getFilter().setSiteLinkFilter(Collections.<String> emptySet());
		assertEquals(
				"https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&props=datatype&ids=Q6%7CQ42%7CP31",
				wdf.getWbGetEntitiesUrl(entityIds));
	}

	@Test
	public void testWbGetEntitiesUrlFilterSome() throws IOException {
		List<String> entityIds = Arrays.asList("Q6", "Q42", "P31");
		WikibaseDataFetcher wdf = new WikibaseDataFetcher();
		wdf.getFilter().setLanguageFilter(Collections.<String> singleton("zh"));
		wdf.getFilter().setSiteLinkFilter(
				Collections.<String> singleton("dewiki"));
		assertEquals(
				"https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&props=datatype%7Clabels%7Caliases%7Cdescriptions%7Cclaims%7Csitelinks&languages=zh&sitefilter=dewiki&ids=Q6%7CQ42%7CP31",
				wdf.getWbGetEntitiesUrl(entityIds));
	}
}
