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

package org.wikidata.wdtk.wikibaseapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public class WbGetEntitiesActionTest {

	MockBasicApiConnection con;
	WbGetEntitiesAction action;

	@BeforeEach
	public void setUp() throws Exception {

		this.con = new MockBasicApiConnection();
		Map<String, String> params = new HashMap<>();
		params.put("action", "wbgetentities");
		params.put("format", "json");
		params.put("ids", "Q32063953");
		this.con.setWebResourceFromPath(params, getClass(),
				"/wbgetentities-Q32063953.json", CompressionType.NONE);
		params.put("ids", "Q6|Q42|P31");
		this.con.setWebResourceFromPath(params, getClass(),
				"/wbgetentities-Q6-Q42-P31.json", CompressionType.NONE);
		params.put("props",
				"datatype|labels|aliases|descriptions|claims|sitelinks");
		this.con.setWebResourceFromPath(params, getClass(),
				"/wbgetentities-Q6-Q42-P31.json", CompressionType.NONE);
		params.put("languages", "en");
		params.put("sitefilter", "enwiki");
		this.con.setWebResourceFromPath(params, getClass(),
				"/wbgetentities-Q6-Q42-P31.json", CompressionType.NONE);
		
		params.clear();
		params.put("action", "wbgetentities");
		params.put("format", "json");
		params.put("ids", "M91629437");
		this.con.setWebResourceFromPath(params, getClass(),
				"/wbgetentities-missing-mid.json", CompressionType.NONE);

		this.action = new WbGetEntitiesAction(this.con, Datamodel.SITE_WIKIDATA);

	}

	@Test
	public void testWbGetEntitiesWithProps() throws MediaWikiApiErrorException, IOException {
		WbGetEntitiesActionData properties = new WbGetEntitiesActionData();
		properties.ids = "Q6|Q42|P31";
		properties.props = "datatype|labels|aliases|descriptions|claims|sitelinks";
		Map<String, EntityDocument> result1 = action.wbGetEntities(properties);
		Map<String, EntityDocument> result2 = action.wbGetEntities(
				properties.ids, null, null, properties.props, null, null);

		assertTrue(result1.containsKey("Q42"));
		assertEquals(result1, result2);
	}

	@Test
	public void testWbGetEntitiesNoProps() throws MediaWikiApiErrorException, IOException {
		WbGetEntitiesActionData properties = new WbGetEntitiesActionData();
		properties.ids = "Q6|Q42|P31";
		Map<String, EntityDocument> result1 = action.wbGetEntities(properties);
		Map<String, EntityDocument> result2 = action.wbGetEntities(
				properties.ids, null, null, properties.props, null, null);

		assertTrue(result1.containsKey("Q42"));
		assertEquals(result1, result2);
	}
	
	@Test
	public void testWbGetEntitiesRedirected() throws MediaWikiApiErrorException, IOException {
		WbGetEntitiesActionData properties = new WbGetEntitiesActionData();
		properties.ids = "Q32063953";
		Map<String, EntityDocument> result = action.wbGetEntities(properties);
		
		assertTrue(result.containsKey("Q32063953"));
	}

	@Test
	public void testWbGetEntitiesPropsFilters()
			throws MediaWikiApiErrorException, IOException {
		WbGetEntitiesActionData properties = new WbGetEntitiesActionData();
		properties.ids = "Q6|Q42|P31";
		properties.props = "datatype|labels|aliases|descriptions|claims|sitelinks";
		properties.languages = "en";
		properties.sitefilter = "enwiki";
		Map<String, EntityDocument> result1 = action.wbGetEntities(properties);
		Map<String, EntityDocument> result2 = action.wbGetEntities(
				properties.ids, null, null, properties.props, null, null);

		assertTrue(result1.containsKey("Q42"));
		assertEquals(result1, result2);
	}

	@Test
	public void testWbGetEntitiesIoError() throws MediaWikiApiErrorException, IOException {
		WbGetEntitiesActionData properties = new WbGetEntitiesActionData();
		properties.ids = "Q6|Q42|notmocked";
		assertThrows(IOException.class, () -> action.wbGetEntities(properties));
	}

	@Test
	public void testIdsAndTitles() throws MediaWikiApiErrorException, IOException {
		assertThrows(IllegalArgumentException.class, () -> action.wbGetEntities("Q42", null, "Tim Berners Lee", null, null, null));
	}

	@Test
	public void testIdsAndSites() throws MediaWikiApiErrorException, IOException {
		assertThrows(IllegalArgumentException.class, () -> action.wbGetEntities("Q42", "enwiki", null, null, null, null));
	}

	@Test
	public void testTitlesNoSites() throws MediaWikiApiErrorException, IOException {
		assertThrows(IllegalArgumentException.class, () -> action.wbGetEntities(null, null, "Tim Berners Lee", null, null, null));
	}

	@Test
	public void testNoTitlesOrIds() throws MediaWikiApiErrorException, IOException {
		assertThrows(IllegalArgumentException.class, () -> action.wbGetEntities(null, "enwiki", null, null, null, null));
	}
	
	// for https://github.com/Wikidata/Wikidata-Toolkit/issues/643
	@Test
	public void testMissingMid() throws MediaWikiApiErrorException, IOException {
		action.wbGetEntities("M91629437", null, null, null, null, null);
	}

}
