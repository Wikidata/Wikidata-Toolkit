package org.wikidata.wdtk.datamodel.implementation;

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
		assertEquals("funky", firstValue.getTypeString());
		assertEquals("shiny", secondValue.getTypeString());
	}
}
