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

import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

import java.io.IOException;

public class MonolingualTextValueImplTest {

	private final ObjectMapper mapper = new ObjectMapper();

	private final MonolingualTextValue mt1 = new MonolingualTextValueImpl("some string", "en");
	private final MonolingualTextValue mt2 = new MonolingualTextValueImpl("some string", "en");
	private final String JSON_MONOLINGUAL_TEXT_VALUE = "{\"value\":{\"language\":\"en\",\"text\":\"some string\"},\"type\":\"monolingualtext\"}";

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
		assertNotEquals(mt1, mtDiffString);
		assertNotEquals(mt1, mtDiffLanguageCode);
		assertNotEquals(mt1, null);
		assertNotEquals(mt1, this);
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

	@Test
	public void testToJava() throws IOException {
		assertEquals(mt1, mapper.readValue(JSON_MONOLINGUAL_TEXT_VALUE, MonolingualTextValueImpl.class));
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_MONOLINGUAL_TEXT_VALUE, mapper.writeValueAsString(mt1));
	}
}
