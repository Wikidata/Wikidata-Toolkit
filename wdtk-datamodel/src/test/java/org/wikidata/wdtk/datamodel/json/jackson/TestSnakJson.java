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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestSnakJson extends JsonConversionTest {

	@Test
	public void testNoValueSnakToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonSnak result = mapper.readValue(noValueSnakJson,
				JacksonSnak.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonNoValueSnak);
		assertEquals(result, testNoValueSnak);
	}

	@Test
	public void testNoValueSnakToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(testNoValueSnak);
		JsonComparator.compareJsonStrings(noValueSnakJson, result);
	}

	@Test
	public void testSomeValueSnakToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonSnak result = mapper.readValue(someValueSnakJson,
				JacksonSnak.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonSomeValueSnak);
		assertEquals(result, testSomeValueSnak);
	}

	@Test
	public void testSomeValueSnakToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(testSomeValueSnak);
		JsonComparator.compareJsonStrings(someValueSnakJson, result);
	}

	@Test
	public void testCommonsValueSnakToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonSnak result = mapper.readValue(commonsValueSnakJson,
				JacksonSnak.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueSnak);
		assertEquals(result, testCommonsValueSnak);
	}

	@Test
	public void testCommonsValueSnakToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(testCommonsValueSnak);
		JsonComparator.compareJsonStrings(commonsValueSnakJson, result);
	}

}
