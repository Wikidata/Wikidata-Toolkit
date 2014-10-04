package org.wikidata.wdtk.datamodel.json.jackson;

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

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestPropertyDocument {

	JacksonPropertyDocument fullDocument;
	ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setupTestFullDocument() {
		fullDocument = new JacksonPropertyDocument();
		fullDocument.setJsonId(JsonTestData.getTestPropertyId().getId());
		fullDocument.setAliases(JsonTestData.getTestAliases());
		fullDocument.setDescriptions(JsonTestData.getTestMltvMap());
		fullDocument.setLabels(JsonTestData.getTestMltvMap());
		fullDocument.setJsonDatatype("quantity");
	}

	@Test
	public void testFullDocumentSetup() {
		assertNotNull(fullDocument.getAliases());
		assertNotNull(fullDocument.getDescriptions());
		assertNotNull(fullDocument.getLabels());
		assertNotNull(fullDocument.getJsonId());
		assertNotNull(fullDocument.getPropertyId());
		assertNotNull(fullDocument.getEntityId());
		assertNotNull(fullDocument.getJsonType());

		assertEquals(fullDocument.getPropertyId().getId(),
				fullDocument.getJsonId());
	}

	/**
	 * Tests the conversion of ItemDocuments containing labels from Pojo to Json
	 *
	 * @throws JsonProcessingException
	 */
	@Test
	public void testLabelsToJson() throws JsonProcessingException {
		JacksonItemDocument document = new JacksonItemDocument();
		document.setLabels(JsonTestData.getTestMltvMap());

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
	public void testLabelToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonItemDocument result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_LABEL, JacksonItemDocument.class);

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
		JacksonItemDocument document = new JacksonItemDocument();
		document.setDescriptions(JsonTestData.getTestMltvMap());

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
	public void testDescriptionsToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonItemDocument result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_DESCRIPTIONS,
				JacksonItemDocument.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestMltvMap(), result.getDescriptions());
	}

	@Test
	public void testAliasesToJson() throws JsonProcessingException {
		JacksonItemDocument document = new JacksonItemDocument();
		document.setAliases(JsonTestData.getTestAliases());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_ALIASES,
				result);
	}

	@Test
	public void testAliasesToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonItemDocument result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_ALIASES, JacksonItemDocument.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestAliases(), result.getAliases());
	}

	@Test
	public void testItemIdToJson() throws JsonProcessingException {
		JacksonItemDocument document = new JacksonItemDocument();
		document.setJsonId(JsonTestData.getTestItemId().getId());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_ITEMID,
				result);
	}

	@Test
	public void testItemIdToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonItemDocument result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_ITEMID, JacksonItemDocument.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestItemId(), result.getEntityId());
	}

	@Test
	public void testSiteLinksToJson() throws JsonProcessingException {
		JacksonItemDocument document = new JacksonItemDocument();
		document.setSiteLinks(JsonTestData.getTestSiteLinkMap());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_SITE_LINK,
				result);
	}

	@Test
	public void testSiteLinksToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonItemDocument result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_SITE_LINK, JacksonItemDocument.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestSiteLinkMap(), result.getSiteLinks());
	}

}
