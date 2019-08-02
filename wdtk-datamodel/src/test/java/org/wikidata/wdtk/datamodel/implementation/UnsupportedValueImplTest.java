package org.wikidata.wdtk.datamodel.implementation;

/*-
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2019 Wikidata Toolkit Developers
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

import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.UnsupportedValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;

import static org.junit.Assert.*;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UnsupportedValueImplTest {
	private final ObjectMapper mapper = new ObjectMapper();

	private final String JSON_UNSUPPORTED_VALUE_1 = "{\"type\":\"funky\",\"value\":\"groovy\"}";
	private final String JSON_UNSUPPORTED_VALUE_2 = "{\"type\":\"shiny\",\"number\":42}";
	
	private UnsupportedValue firstValue, secondValue;
	
	@Before
	public void deserializeFirstValue() throws JsonParseException, JsonMappingException, IOException {
		firstValue = mapper.readValue(JSON_UNSUPPORTED_VALUE_1, UnsupportedValueImpl.class);
		secondValue = mapper.readValue(JSON_UNSUPPORTED_VALUE_2, UnsupportedValueImpl.class);
	}
	
	@Test
	public void testEquals() throws JsonParseException, JsonMappingException, IOException {
		Value otherValue = mapper.readValue(JSON_UNSUPPORTED_VALUE_1, ValueImpl.class);
		assertEquals(firstValue, otherValue);
		assertNotEquals(secondValue, otherValue);
	}
	
	@Test
	public void testHash() throws JsonParseException, JsonMappingException, IOException {
		Value otherValue = mapper.readValue(JSON_UNSUPPORTED_VALUE_2, ValueImpl.class);
		assertEquals(secondValue.hashCode(), otherValue.hashCode());
	}
	
	@Test
	public void testSerialize() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_UNSUPPORTED_VALUE_1, mapper.writeValueAsString(firstValue));
		JsonComparator.compareJsonStrings(JSON_UNSUPPORTED_VALUE_2, mapper.writeValueAsString(secondValue));
	}
	
	@Test
	public void testToString() {
		assertEquals(ToString.toString(firstValue), firstValue.toString());
		assertEquals(ToString.toString(secondValue), secondValue.toString());
	}
	
	@Test
	public void testGetTypeString() {
		assertEquals("funky", firstValue.getTypeJsonString());
		assertEquals("shiny", secondValue.getTypeJsonString());
	}
}
