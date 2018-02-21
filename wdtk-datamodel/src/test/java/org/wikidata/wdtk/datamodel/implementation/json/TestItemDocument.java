package org.wikidata.wdtk.datamodel.implementation.json;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
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
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.ItemDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonPreStatement;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestItemDocument {

	// TODO test statements (JSON claim)

	ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);

	/**
	 * Tests the conversion of ItemDocuments containing labels from Pojo to Json
	 *
	 * @throws JsonProcessingException
	 */
	@Test
	public void testLabelsToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(
				JsonTestData.getTestItemId().getId(),
				JsonTestData.getTestMltvMap(),
				Collections.<String, MonolingualTextValue>emptyMap(),
				Collections.<String, List<MonolingualTextValue>>emptyMap(),
				Collections.<String, List<JacksonPreStatement>>emptyMap(),
				Collections.<String, SiteLink>emptyMap(),
				0, JsonTestData.getTestItemId().getSiteIri());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_LABEL,
				result);
	}

	/**
	 * Tests the conversion of ItemDocuments containing labels from Json to Pojo
	 *
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test
	public void testLabelToJava() throws
			IOException {
		ItemDocumentImpl result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_LABEL, ItemDocumentImpl.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestMltvMap(), result.getLabels());
	}

	/**
	 * Tests the conversion of ItemDocuments containing descriptions from Pojo
	 * to Json
	 *
	 * @throws JsonProcessingException
	 */
	@Test
	public void testDescriptionsToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(
				JsonTestData.getTestItemId().getId(),
				Collections.<String, MonolingualTextValue>emptyMap(),
				JsonTestData.getTestMltvMap(),
				Collections.<String, List<MonolingualTextValue>>emptyMap(),
				Collections.<String, List<JacksonPreStatement>>emptyMap(),
				Collections.<String, SiteLink>emptyMap(),
				0, JsonTestData.getTestItemId().getSiteIri());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(
				JsonTestData.JSON_WRAPPED_DESCRIPTIONS, result);
	}

	/**
	 * Tests the conversion of ItemDocuments containing descriptions from Json
	 * to Pojo
	 *
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test
	public void testDescriptionsToJava() throws
			IOException {
		ItemDocumentImpl result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_DESCRIPTIONS,
				ItemDocumentImpl.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestMltvMap(), result.getDescriptions());
	}

	@Test
	public void testAliasesToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(
				JsonTestData.getTestItemId().getId(),
				Collections.<String, MonolingualTextValue>emptyMap(),
				Collections.<String, MonolingualTextValue>emptyMap(),
				JsonTestData.getTestAliases(),
				Collections.<String, List<JacksonPreStatement>>emptyMap(),
				Collections.<String, SiteLink>emptyMap(),
				0, JsonTestData.getTestItemId().getSiteIri());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_ALIASES,
				result);
	}

	@Test
	public void testAliasesToJava() throws
			IOException {

		ItemDocumentImpl result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_ALIASES, ItemDocumentImpl.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestAliases(), result.getAliases());
	}

	@Test
	public void testItemIdToJson() throws JsonProcessingException {
		ItemDocumentImpl document = JsonTestData.getEmptyTestItemDocument();

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_ITEMID,
				result);
	}

	@Test
	public void testEmptyItemIdToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(
				ItemIdValue.NULL.getId(),
				Collections.<String, MonolingualTextValue>emptyMap(),
				Collections.<String, MonolingualTextValue>emptyMap(),
				Collections.<String, List<MonolingualTextValue>>emptyMap(),
				Collections.<String, List<JacksonPreStatement>>emptyMap(),
				Collections.<String, SiteLink>emptyMap(),
				0, ItemIdValue.NULL.getSiteIri());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_NOITEMID,
				result);
	}

	@Test
	public void testItemIdToJava() throws
			IOException {

		ItemDocumentImpl result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_ITEMID, ItemDocumentImpl.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestItemId(), result.getEntityId());
	}

	@Test
	public void testSiteLinksToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(
				JsonTestData.getTestItemId().getId(),
				Collections.<String, MonolingualTextValue>emptyMap(),
				Collections.<String, MonolingualTextValue>emptyMap(),
				Collections.<String, List<MonolingualTextValue>>emptyMap(),
				Collections.<String, List<JacksonPreStatement>>emptyMap(),
				JsonTestData.getTestSiteLinkMap(),
				0, JsonTestData.getTestItemId().getSiteIri());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_SITE_LINK,
				result);
	}

	@Test
	public void testSiteLinksToJava() throws
			IOException {
		ItemDocumentImpl result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_SITE_LINK, ItemDocumentImpl.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestSiteLinkMap(), result.getSiteLinks());
	}

	@Test
	public void testEmptyArraysForTerms() throws IOException {
		ItemDocumentImpl result = mapper.reader(ItemDocumentImpl.class)
			.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
			.readValue(JsonTestData.JSON_EMPTY_ARRAY_AS_CONTAINER);

		assertNotNull(result);
		assertNotNull(result.getLabels());
		assertNotNull(result.getDescriptions());
		assertNotNull(result.getAliases());
		assertNotNull(result.getAllStatements());
		assertNotNull(result.getSiteLinks());
	}

	@Test
	public void testGenerationFromOtherItemDocument() {
		ItemDocumentImpl fullDocument = new ItemDocumentImpl(
				JsonTestData.getTestItemId().getId(),
				JsonTestData.getTestMltvMap(),
				JsonTestData.getTestMltvMap(),
				JsonTestData.getTestAliases(),
				Collections.<String, List<JacksonPreStatement>>emptyMap(),
				JsonTestData.getTestSiteLinkMap(),
				0, JsonTestData.getTestItemId().getSiteIri());

		assertEquals(fullDocument.getAliases(), JsonTestData.getTestAliases());
		assertEquals(fullDocument.getDescriptions(),
				JsonTestData.getTestMltvMap());
		assertEquals(fullDocument.getLabels(), JsonTestData.getTestMltvMap());
		assertEquals(fullDocument.getItemId(), JsonTestData.getTestItemId());
		assertEquals(fullDocument.getEntityId(), JsonTestData.getTestItemId());
		assertEquals(fullDocument.getItemId().getId(), fullDocument.getJsonId());

		DatamodelConverter converter = new DatamodelConverter(
				new DataObjectFactoryImpl());
		ItemDocument copy = converter.copy(fullDocument);

		assertEquals(fullDocument, copy);
	}
}
