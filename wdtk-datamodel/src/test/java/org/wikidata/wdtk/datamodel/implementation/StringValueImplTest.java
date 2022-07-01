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
import org.wikidata.wdtk.datamodel.interfaces.StringValue;

import java.io.IOException;

public class StringValueImplTest {

	private final ObjectMapper mapper = new ObjectMapper();

	private final StringValue s1 = new StringValueImpl("some string");
	private final StringValue s2 = new StringValueImpl("some string");
	private final String JSON_STRING_VALUE = "{\"type\":\"string\",\"value\":\"some string\"}";

	@Test
	public void stringIsCorrect() {
		assertEquals(s1.getString(), "some string");
	}

	@Test
	public void equalityBasedOnContent() {
		StringValue s3 = new StringValueImpl("another string");

		assertEquals(s1, s1);
		assertEquals(s1, s2);
		assertNotEquals(s1, s3);
		assertNotEquals(s1, null);
		assertNotEquals(s1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(s1.hashCode(), s2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void stringNotNull() {
		new StringValueImpl(null);
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_STRING_VALUE, mapper.writeValueAsString(s1));
	}

	@Test
	public void testToJava() throws IOException {
		assertEquals(s1, mapper.readValue(JSON_STRING_VALUE, ValueImpl.class));
	}
}
