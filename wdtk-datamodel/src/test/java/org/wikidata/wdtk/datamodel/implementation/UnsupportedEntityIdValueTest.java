package org.wikidata.wdtk.datamodel.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.UnsupportedEntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UnsupportedEntityIdValueTest {
	private final ObjectMapper mapper = new DatamodelMapper("http://www.wikidata.org/entity/");

	private final String JSON_UNSUPPORTED_VALUE_1 = "{\"type\":\"wikibase-entityid\",\"value\":{\"entity-type\":\"funky\",\"id\":\"Z343\"}}";
	private final String JSON_UNSUPPORTED_VALUE_2 = "{\"type\":\"wikibase-entityid\",\"value\":{\"entity-type\":\"shiny\",\"id\":\"P8989\",\"foo\":\"bar\"}}";
	
	private UnsupportedEntityIdValue firstValue, secondValue;
	
	@Before
	public void deserializeFirstValue() throws JsonParseException, JsonMappingException, IOException {
		firstValue = mapper.readValue(JSON_UNSUPPORTED_VALUE_1, UnsupportedEntityIdValueImpl.class);
		secondValue = mapper.readValue(JSON_UNSUPPORTED_VALUE_2, UnsupportedEntityIdValueImpl.class);
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
		assertEquals("funky", firstValue.getEntityTypeString());
		assertEquals("shiny", secondValue.getEntityTypeString());
	}
}
