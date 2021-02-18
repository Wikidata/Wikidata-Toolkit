/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2018 Wikidata Toolkit Developers
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

package org.wikidata.wdtk.datamodel.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TermImplTest {

	private final ObjectMapper mapper = new ObjectMapper();

	private final MonolingualTextValue mt1 = new TermImpl("en", "some string");
	private final MonolingualTextValue mt2 = new TermImpl("en", "some string");
	private final String JSON_TERM = "{\"language\":\"en\",\"value\":\"some string\"}";

	@Test
	public void dataIsCorrect() {
		assertEquals(mt1.getText(), "some string");
		assertEquals(mt1.getLanguageCode(), "en");
	}

	@Test
	public void equalityBasedOnContent() {
		MonolingualTextValue mtDiffString = new TermImpl(
				"another string", "en");
		MonolingualTextValue mtDiffLanguageCode = new TermImpl(
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

	@Test
	public void textNotNull() {
		assertThrows(NullPointerException.class, () -> new TermImpl("en", null));
	}

	@Test
	public void languageCodeNotNull() {
		assertThrows(NullPointerException.class, () -> new TermImpl(null, "some text"));
	}

	@Test
	public void testToJava() throws IOException {
		assertEquals(mt1, mapper.readValue(JSON_TERM, TermImpl.class));
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_TERM, mapper.writeValueAsString(mt1));
	}
}
