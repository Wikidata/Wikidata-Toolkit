package org.wikidata.wdtk.datamodel.json.jackson;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.EntityIdValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.GlobeCoordinateValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.MonolingualTextDatavalueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.QuantityValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.StringValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.Time;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.TimeValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.ValueImpl;

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
			TimeValueImpl castedResult = (TimeValueImpl)result;
			
			assertNotNull(result);
			assertTrue(result instanceof TimeValueImpl);
			assertEquals(result.getType(), testTimeValue.getType());
			assertEquals((castedResult).getValue(), testTimeValue.getValue());
			
			// test if every field contains the correct value
			assertEquals(castedResult.getSecond(), testTimeValue.getSecond());
			assertEquals(castedResult.getMinute(), testTimeValue.getMinute());
			assertEquals(castedResult.getHour(), testTimeValue.getHour());
			assertEquals(castedResult.getDay(), testTimeValue.getDay());
			assertEquals(castedResult.getMonth(), testTimeValue.getMonth());
			assertEquals(castedResult.getYear(), testTimeValue.getYear());
			
			assertEquals(castedResult.getAfterTolerance(), testTimeValue.getAfterTolerance());
			assertEquals(castedResult.getBeforeTolerance(), testTimeValue.getBeforeTolerance());
			assertEquals(castedResult.getPrecision(), testTimeValue.getPrecision());
			assertEquals(castedResult.getPreferredCalendarModel(), testTimeValue.getPreferredCalendarModel());
			assertEquals(castedResult.getTimezoneOffset(), testTimeValue.getTimezoneOffset());
			
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

	@Test
	public void testQuantityValueToJson(){
		
		try {
			String result = mapper.writeValueAsString(testQuantityValue);
			JsonComparator.compareJsonStrings(quantityValueJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting POJO to JSON failed");
		}
	}
	
	@Test
	public void testQuantityValueToJava(){
		
		try {
			ValueImpl result = mapper.readValue(quantityValueJson, ValueImpl.class);
			
			assertNotNull(result);
			assertTrue(result instanceof QuantityValueImpl);
			assertEquals(result.getType(), testQuantityValue.getType());
			assertEquals(((QuantityValueImpl)result).getValue(), testQuantityValue.getValue());
			
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
	public void testMltDatavalueToJson(){
		
		try {
			String result = mapper.writeValueAsString(testMltDatavalue);
			JsonComparator.compareJsonStrings(mltDatavalueJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting POJO to JSON failed");
		}
	}
	
	@Test
	public void testMltDatavalueToJava(){
		
		try {
			ValueImpl result = mapper.readValue(mltDatavalueJson, ValueImpl.class);
			
			assertNotNull(result);
			assertTrue(result instanceof MonolingualTextDatavalueImpl);
			assertEquals(((MonolingualTextDatavalueImpl)result), testMltDatavalue);
			
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
