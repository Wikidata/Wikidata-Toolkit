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
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestSnakJson {

	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testNoValueSnakToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonSnak result = mapper.readValue(JsonTestData.JSON_NOVALUE_SNAK,
				JacksonSnak.class);
		result.setSiteIri(Datamodel.SITE_WIKIDATA);

		assertNotNull(result);
		assertTrue(result instanceof JacksonNoValueSnak);
		assertEquals(result, JsonTestData.TEST_NOVALUE_SNAK);
	}

	@Test
	public void testNoValueSnakToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_NOVALUE_SNAK);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_NOVALUE_SNAK,
				result);
	}

	@Test
	public void testSomeValueSnakToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonSnak result = mapper.readValue(JsonTestData.JSON_SOMEVALUE_SNAK,
				JacksonSnak.class);
		result.setSiteIri(Datamodel.SITE_WIKIDATA);

		assertNotNull(result);
		assertTrue(result instanceof JacksonSomeValueSnak);
		assertEquals(result, JsonTestData.TEST_SOMEVALUE_SNAK);
	}

	@Test
	public void testSomeValueSnakToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_SOMEVALUE_SNAK);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_SOMEVALUE_SNAK,
				result);
	}

	@Test
	public void testCommonsValueSnakToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonSnak result = mapper.readValue(
				JsonTestData.JSON_VALUE_SNAK_STRING, JacksonSnak.class);
		result.setSiteIri(Datamodel.SITE_WIKIDATA);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueSnak);
		assertEquals(result, JsonTestData.TEST_STRING_VALUE_SNAK);
	}

	@Test
	public void testCommonsValueSnakToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_STRING_VALUE_SNAK);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_VALUE_SNAK_STRING,
				result);
	}

}
