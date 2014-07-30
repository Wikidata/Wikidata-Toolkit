package org.wikidata.wdtk.datamodel.json.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.EntityIdValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.GlobeCoordinateValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.StringValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.Time;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.ValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.TimeValueImpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestValue extends JsonConversionTest {

	@Test
	public void testStringValueToJson(){
		
		try {
			String result = mapper.writeValueAsString(testStringValue);
			JsonComparator.compareJsonStrings(stringValueJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting POJO to JSON failed");
		}
	}
	
	@Test
	public void testStringValueToJava(){
		
		try {
			ValueImpl result = mapper.readValue(stringValueJson, ValueImpl.class);
			
			assertNotNull(result);
			assertTrue(result instanceof StringValueImpl);
			assertEquals(result.getType(), testStringValue.getType());
			assertEquals(((StringValueImpl)result).getValue(), testStringValue.getValue());
			
		} catch (JsonParseException e) {
			e.printStackTrace();
			fail("Parsing failed");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			fail("Json mapping failed");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IO failed");
		}
	}
	
	@Test
	public void testEntityIdValueToJson(){
		
		try {
			String result = mapper.writeValueAsString(testEntityIdValue);
			JsonComparator.compareJsonStrings(entityIdValueJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting POJO to JSON failed");
		}
	}
	
	@Test
	public void testEntityIdValueToJava(){
		
		try {
			ValueImpl result = mapper.readValue(entityIdValueJson, ValueImpl.class);
			
			assertNotNull(result);
			assertTrue(result instanceof EntityIdValueImpl);
			assertEquals(result.getType(), testEntityIdValue.getType());
			assertEquals(((EntityIdValueImpl)result).getValue(), testEntityIdValue.getValue());
			
		} catch (JsonParseException e) {
			e.printStackTrace();
			fail("Parsing failed");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			fail("Json mapping failed");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IO failed");
		}
	}
	
	@Test
	public void testTimeValueToJson(){
		
		try {
			String result = mapper.writeValueAsString(testTimeValue);
			JsonComparator.compareJsonStrings(timeValueJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting POJO to JSON failed");
		}
	}
	
	@Test
	public void testTimeValueToJava(){
		
		try {
			ValueImpl result = mapper.readValue(timeValueJson, ValueImpl.class);
			
			assertNotNull(result);
			assertTrue(result instanceof TimeValueImpl);
			assertEquals(result.getType(), testTimeValue.getType());
			assertEquals(((TimeValueImpl)result).getValue(), testTimeValue.getValue());
			
			// test against the same time, created on a different way
			Time otherTime = new Time(2013, (byte)10, (byte)28, (byte)0, (byte)0, (byte)0, 0, 0, 0, 11, "http://www.wikidata.org/entity/Q1985727");
			assertEquals(((TimeValueImpl)result).getValue(), otherTime);
			
		} catch (JsonParseException e) {
			e.printStackTrace();
			fail("Parsing failed");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			fail("Json mapping failed");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IO failed");
		}
	}
	
	@Test
	public void testGlobeCoordinateValueToJson(){
		
		try {
			String result = mapper.writeValueAsString(testGlobeCoordinateValue);
			JsonComparator.compareJsonStrings(globeCoordinateValueJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting POJO to JSON failed");
		}
	}
	
	@Test
	public void testGlobeCoordinateValueToJava(){
		
		try {
			ValueImpl result = mapper.readValue(globeCoordinateValueJson, ValueImpl.class);
			
			assertNotNull(result);
			assertTrue(result instanceof GlobeCoordinateValueImpl);
			assertEquals(result.getType(), testGlobeCoordinateValue.getType());
			assertEquals(((GlobeCoordinateValueImpl)result).getValue(), testGlobeCoordinateValue.getValue());
			
		} catch (JsonParseException e) {
			e.printStackTrace();
			fail("Parsing failed");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			fail("Json mapping failed");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IO failed");
		}
	}


}
