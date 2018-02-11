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
import static org.junit.Assert.assertFalse;

import java.io.IOException;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.TermImpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestMonolingualTextValue {

	ObjectMapper mapper = new ObjectMapper();

	/**
	 * Tests the conversion of MonolingualTextValues from JSON to POJO
	 *
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test
	public void testMonolingualTextValueToJava() throws
			IOException {
		TermImpl result = mapper.readValue(
				JsonTestData.JSON_TERM_MLTV,
				TermImpl.class);

		assertEquals("en", result.getLanguageCode());
		assertEquals("foobar", result.getText());
	}

	/**
	 * Tests the conversion of MonolingualTextValues from POJO to JSON
	 *
	 * @throws JsonProcessingException
	 */
	@Test
	public void testMonolingualTextValueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_MLTV_TERM_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_TERM_MLTV,
				result);
	}

	@Test
	public void testEquals() {
		TermImpl match = new TermImpl(
				"en", "foobar");
		TermImpl wrongLanguage = new TermImpl(
				"de", "foobar");
		TermImpl wrongValue = new TermImpl(
				"en", "barfoo");

		assertEquals(JsonTestData.TEST_MLTV_TERM_VALUE,
				JsonTestData.TEST_MLTV_TERM_VALUE);
		assertEquals(JsonTestData.TEST_MLTV_TERM_VALUE, match);
		assertFalse(JsonTestData.TEST_MLTV_TERM_VALUE
				.equals(wrongLanguage));
		assertFalse(JsonTestData.TEST_MLTV_TERM_VALUE.equals(wrongValue));
	}
}
