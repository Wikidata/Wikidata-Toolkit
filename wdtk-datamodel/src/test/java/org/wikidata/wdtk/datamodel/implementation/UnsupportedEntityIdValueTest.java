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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.UnsupportedEntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UnsupportedEntityIdValueTest {
	private final ObjectMapper mapper = new DatamodelMapper("http://www.wikidata.org/entity/");

	private final String JSON_UNSUPPORTED_VALUE_1 = "{\"type\":\"wikibase-entityid\",\"value\":{\"entity-type\":\"funky\",\"id\":\"Z343\"}}";
	private final String JSON_UNSUPPORTED_VALUE_2 = "{\"type\":\"wikibase-entityid\",\"value\":{\"entity-type\":\"shiny\",\"id\":\"R8989\",\"foo\":\"bar\"}}";
	private final String JSON_UNSUPPORTED_VALUE_NO_TYPE = "{\"type\":\"wikibase-entityid\",\"value\":{\"id\":\"Z343\"}}";
	
	private UnsupportedEntityIdValue firstValue, secondValue, noType;
	
	@Before
	public void deserializeValues() throws IOException {
		firstValue = mapper.readValue(JSON_UNSUPPORTED_VALUE_1, UnsupportedEntityIdValueImpl.class);
		secondValue = mapper.readValue(JSON_UNSUPPORTED_VALUE_2, UnsupportedEntityIdValueImpl.class);
		noType = mapper.readValue(JSON_UNSUPPORTED_VALUE_NO_TYPE, UnsupportedEntityIdValueImpl.class);
	}
	
	@Test
	public void testEquals() throws IOException {
		Value otherValue = mapper.readValue(JSON_UNSUPPORTED_VALUE_1, ValueImpl.class);
		assertEquals(firstValue, otherValue);
		assertNotEquals(secondValue, otherValue);
		assertNotEquals(firstValue, noType);
		assertNotEquals(noType, secondValue);
	}
	
	@Test
	public void testHash() throws IOException {
		Value otherValue = mapper.readValue(JSON_UNSUPPORTED_VALUE_2, ValueImpl.class);
		assertEquals(secondValue.hashCode(), otherValue.hashCode());
	}
	
	@Test
	public void testSerialize() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_UNSUPPORTED_VALUE_1, mapper.writeValueAsString(firstValue));
		JsonComparator.compareJsonStrings(JSON_UNSUPPORTED_VALUE_2, mapper.writeValueAsString(secondValue));
		JsonComparator.compareJsonStrings(JSON_UNSUPPORTED_VALUE_NO_TYPE, mapper.writeValueAsString(noType));
	}
	
	@Test
	public void testToString() {
		assertEquals(ToString.toString(firstValue), firstValue.toString());
		assertEquals(ToString.toString(secondValue), secondValue.toString());
	}
	
	@Test
	public void testGetTypeString() {
		assertEquals("funky", firstValue.getEntityTypeJsonString());
		assertEquals("shiny", secondValue.getEntityTypeJsonString());
	}
	
	@Test
	public void testGetIri() {
		assertEquals("http://www.wikidata.org/entity/Z343", firstValue.getIri());
		assertEquals("http://www.wikidata.org/entity/R8989", secondValue.getIri());
		assertEquals("http://www.wikidata.org/entity/Z343", noType.getIri());
	}
	
	@Test
	public void testGetId() {
		assertEquals("Z343", firstValue.getId());
		assertEquals("R8989", secondValue.getId());
		assertEquals("Z343", noType.getId());
	}
	
	@Test
	public void testGetEntityType() {
		assertEquals("http://www.wikidata.org/ontology#Funky", firstValue.getEntityType());
		assertEquals("http://www.wikidata.org/ontology#Shiny", secondValue.getEntityType());
		assertEquals(EntityIdValue.ET_UNSUPPORTED, noType.getEntityType());
	}
	
	@Test
	public void testGetEntityTypeString() {
		assertEquals("funky", firstValue.getEntityTypeJsonString());
		assertEquals("shiny", secondValue.getEntityTypeJsonString());
		assertNull(noType.getEntityTypeJsonString());
	}
}
