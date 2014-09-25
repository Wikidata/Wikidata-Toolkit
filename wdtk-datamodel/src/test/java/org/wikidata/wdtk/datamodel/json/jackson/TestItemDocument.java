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

public class TestItemDocument extends JsonConversionTest {

	// TODO test statements (JSON claim)

	JacksonItemDocument fullDocument;

	@Before
	public void setupTestFullDocument() {
		fullDocument = new JacksonItemDocument();
		fullDocument.setJsonId(testItemId.getId());
		fullDocument.setAliases(testAliases);
		fullDocument.setDescriptions(testMltvMap);
		fullDocument.setLabels(testMltvMap);
	}

	@Test
	public void testFullDocumentSetup() {
		assertNotNull(fullDocument.getAliases());
		assertNotNull(fullDocument.getDescriptions());
		assertNotNull(fullDocument.getLabels());
		assertNotNull(fullDocument.getJsonId());
		assertNotNull(fullDocument.getItemId());
		assertNotNull(fullDocument.getEntityId());

		assertEquals(fullDocument.getItemId().getId(), fullDocument.getJsonId());
	}

	/**
	 * Tests the conversion of ItemDocuments containing labels from Pojo to Json
	 *
	 * @throws JsonProcessingException
	 */
	@Test
	public void testLabelsToJson() throws JsonProcessingException {
		JacksonItemDocument document = new JacksonItemDocument();
		document.setLabels(testMltvMap);

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(wrappedLabelJson, result);
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
		JacksonItemDocument result = mapper.readValue(wrappedLabelJson,
				JacksonItemDocument.class);

		assertNotNull(result);
		assertEquals(testMltvMap, result.getLabels());
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
		document.setDescriptions(testMltvMap);

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(wrappedDescriptionJson, result);
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
		JacksonItemDocument result = mapper.readValue(wrappedDescriptionJson,
				JacksonItemDocument.class);

		assertNotNull(result);
		assertEquals(testMltvMap, result.getDescriptions());
	}

	@Test
	public void testAliasesToJson() throws JsonProcessingException {
		JacksonItemDocument document = new JacksonItemDocument();
		document.setAliases(testAliases);

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(wrappedAliasJson, result);
	}

	@Test
	public void testAliasesToJava() throws JsonParseException,
			JsonMappingException, IOException {

		JacksonItemDocument result = mapper.readValue(wrappedAliasJson,
				JacksonItemDocument.class);

		assertNotNull(result);
		assertEquals(testAliases, result.getAliases());
	}

	@Test
	public void testItemIdToJson() throws JsonProcessingException {
		JacksonItemDocument document = new JacksonItemDocument();
		document.setJsonId(testItemId.getId());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(wrappedItemIdJson, result);
	}

	@Test
	public void testItemIdToJava() throws JsonParseException,
			JsonMappingException, IOException {

		JacksonItemDocument result = mapper.readValue(wrappedItemIdJson,
				JacksonItemDocument.class);

		assertNotNull(result);
		assertEquals(testItemId, result.getEntityId());
	}

	@Test
	public void testSiteLinksToJson() throws JsonProcessingException {
		JacksonItemDocument document = new JacksonItemDocument();
		document.setSiteLinks(testSiteLinkMap);

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(wrappedSiteLinkJson, result);
	}

	@Test
	public void testSiteLinksToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonItemDocument result = mapper.readValue(wrappedSiteLinkJson,
				JacksonItemDocument.class);

		assertNotNull(result);
		assertEquals(testSiteLinkMap, result.getSiteLinks());
	}

	@Test
	public void testGenerationFromOtherItemDocument() {
		JacksonItemDocument copy = new JacksonItemDocument(fullDocument);
		assertEquals(fullDocument, copy);
	}
}
