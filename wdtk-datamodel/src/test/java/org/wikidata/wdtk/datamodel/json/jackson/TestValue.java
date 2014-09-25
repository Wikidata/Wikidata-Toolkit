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

import java.io.IOException;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerTime;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValue;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueEntityId;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueGlobeCoordinates;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueMonolingualText;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueQuantity;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueString;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueTime;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestValue extends JsonConversionTest {

	@Test
	public void testStringValueToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(testStringValue);
		JsonComparator.compareJsonStrings(stringValueJson, result);
	}

	@Test
	public void testStringValueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(stringValueJson,
				JacksonValue.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueString);
		assertEquals(result.getType(), testStringValue.getType());
		assertEquals(((JacksonValueString) result).getValue(),
				testStringValue.getValue());
	}

	@Test
	public void testEntityIdValueToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(testEntityIdValue);
		JsonComparator.compareJsonStrings(entityIdValueJson, result);
	}

	@Test
	public void testEntityIdValueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(entityIdValueJson,
				JacksonValue.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueEntityId);
		assertEquals(result.getType(), testEntityIdValue.getType());
		assertEquals(((JacksonValueEntityId) result).getValue(),
				testEntityIdValue.getValue());
	}

	@Test
	public void testTimeValueToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(testTimeValue);
		JsonComparator.compareJsonStrings(timeValueJson, result);
	}

	@Test
	public void testTimeValueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(timeValueJson,
				JacksonValue.class);
		JacksonValueTime castedResult = (JacksonValueTime) result;

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueTime);
		assertEquals(result.getType(), testTimeValue.getType());
		assertEquals((castedResult).getValue(), testTimeValue.getValue());

		// test if every field contains the correct value
		assertEquals(castedResult.getSecond(), testTimeValue.getSecond());
		assertEquals(castedResult.getMinute(), testTimeValue.getMinute());
		assertEquals(castedResult.getHour(), testTimeValue.getHour());
		assertEquals(castedResult.getDay(), testTimeValue.getDay());
		assertEquals(castedResult.getMonth(), testTimeValue.getMonth());
		assertEquals(castedResult.getYear(), testTimeValue.getYear());

		assertEquals(castedResult.getAfterTolerance(),
				testTimeValue.getAfterTolerance());
		assertEquals(castedResult.getBeforeTolerance(),
				testTimeValue.getBeforeTolerance());
		assertEquals(castedResult.getPrecision(), testTimeValue.getPrecision());
		assertEquals(castedResult.getPreferredCalendarModel(),
				testTimeValue.getPreferredCalendarModel());
		assertEquals(castedResult.getTimezoneOffset(),
				testTimeValue.getTimezoneOffset());

		// test against the same time, created on a different way
		JacksonInnerTime otherTime = new JacksonInnerTime(2013, (byte) 10,
				(byte) 28, (byte) 0, (byte) 0, (byte) 0, 0, 0, 0, 11,
				"http://www.wikidata.org/entity/Q1985727");
		assertEquals(((JacksonValueTime) result).getValue(), otherTime);
	}

	@Test
	public void testGlobeCoordinateValueToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(testGlobeCoordinateValue);
		JsonComparator.compareJsonStrings(globeCoordinateValueJson, result);
	}

	@Test
	public void testGlobeCoordinateValueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(globeCoordinateValueJson,
				JacksonValue.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueGlobeCoordinates);
		assertEquals(result.getType(), testGlobeCoordinateValue.getType());
		assertEquals(((JacksonValueGlobeCoordinates) result).getValue(),
				testGlobeCoordinateValue.getValue());
	}

	@Test
	public void testQuantityValueToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(testQuantityValue);
		JsonComparator.compareJsonStrings(quantityValueJson, result);
	}

	@Test
	public void testQuantityValueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(quantityValueJson,
				JacksonValue.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueQuantity);
		assertEquals(result.getType(), testQuantityValue.getType());
		assertEquals(((JacksonValueQuantity) result).getValue(),
				testQuantityValue.getValue());
	}

	@Test
	public void testMltDatavalueToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(testMltDatavalue);
		JsonComparator.compareJsonStrings(mltDatavalueJson, result);
	}

	@Test
	public void testMltDatavalueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(mltDatavalueJson,
				JacksonValue.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueMonolingualText);
		assertEquals((result), testMltDatavalue);
	}

	@Test
	public void testMltDatavalueConstructor() {
		assertEquals(testMltDatavalue,
				new JacksonValueMonolingualText(testMltv));

	}
}
