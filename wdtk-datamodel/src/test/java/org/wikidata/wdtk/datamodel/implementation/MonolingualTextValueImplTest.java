package org.wikidata.wdtk.datamodel.implementation;

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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.json.JsonComparator;
import org.wikidata.wdtk.datamodel.implementation.json.JsonTestData;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

import java.io.IOException;

public class MonolingualTextValueImplTest {

	private final ObjectMapper mapper = new ObjectMapper();

	private final MonolingualTextValue mt1 = new MonolingualTextValueImpl("some string", "en");
	private final MonolingualTextValue mt2 = new MonolingualTextValueImpl("some string", "en");

	@Test
	public void dataIsCorrect() {
		assertEquals(mt1.getText(), "some string");
		assertEquals(mt1.getLanguageCode(), "en");
	}

	@Test
	public void equalityBasedOnContent() {
		MonolingualTextValue mtDiffString = new MonolingualTextValueImpl(
				"another string", "en");
		MonolingualTextValue mtDiffLanguageCode = new MonolingualTextValueImpl(
				"some string", "en-GB");

		assertEquals(mt1, mt1);
		assertEquals(mt1, mt2);
		assertThat(mt1, not(equalTo(mtDiffString)));
		assertThat(mt1, not(equalTo(mtDiffLanguageCode)));
		assertThat(mt1, not(equalTo(null)));
		assertFalse(mt1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(mt1.hashCode(), mt2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void textNotNull() {
		new MonolingualTextValueImpl(null, "en");
	}

	@Test(expected = NullPointerException.class)
	public void languageCodeNotNull() {
		new MonolingualTextValueImpl("some text", null);
	}

	/**
	 * Tests the conversion of MonolingualTextValues from JSON to POJO
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

	@Test
	public void testToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_MONOLINGUAL_TEXT_VALUE);
		JsonComparator.compareJsonStrings(
				JsonTestData.JSON_MONOLINGUAL_TEXT_VALUE, result);
	}

	@Test
	public void testToJava() throws
			IOException {
		ValueImpl result = mapper.readValue(
				JsonTestData.JSON_MONOLINGUAL_TEXT_VALUE, ValueImpl.class);

		assertTrue(result instanceof MonolingualTextValueImpl);
		assertEquals(JsonTestData.TEST_MONOLINGUAL_TEXT_VALUE, result);

	}
}
