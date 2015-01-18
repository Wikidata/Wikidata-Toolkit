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
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueGlobeCoordinates;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueItemId;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueMonolingualText;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValuePropertyId;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueQuantity;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueString;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueTime;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestValue {

	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testStringValueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_STRING_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_STRING_VALUE,
				result);
	}

	@Test
	public void testStringValueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(JsonTestData.JSON_STRING_VALUE,
				JacksonValue.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueString);
		assertEquals(result.getType(), JsonTestData.TEST_STRING_VALUE.getType());
		assertEquals(((JacksonValueString) result).getValue(),
				JsonTestData.TEST_STRING_VALUE.getValue());
	}

	@Test
	public void testItemIdValueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_ITEM_ID_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_ITEM_ID_VALUE,
				result);
	}

	@Test
	public void testItemIdValueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(JsonTestData.JSON_ITEM_ID_VALUE,
				JacksonValue.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueItemId);
		assertEquals(result.getType(),
				JsonTestData.TEST_ITEM_ID_VALUE.getType());
		assertEquals(((JacksonValueItemId) result).getValue(),
				JsonTestData.TEST_ITEM_ID_VALUE.getValue());
	}

	@Test
	public void testPropertyIdValueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_PROPERTY_ID_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_PROPERTY_ID_VALUE,
				result);
	}

	@Test
	public void testPropertyIdValueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(
				JsonTestData.JSON_PROPERTY_ID_VALUE, JacksonValue.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValuePropertyId);
		assertEquals(result.getType(),
				JsonTestData.TEST_PROPERTY_ID_VALUE.getType());
		assertEquals(((JacksonValuePropertyId) result).getValue(),
				JsonTestData.TEST_PROPERTY_ID_VALUE.getValue());
	}

	@Test
	public void testTimeValueToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(JsonTestData.TEST_TIME_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_TIME_VALUE, result);
	}

	@Test
	public void testTimeValueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(JsonTestData.JSON_TIME_VALUE,
				JacksonValue.class);
		JacksonValueTime castedResult = (JacksonValueTime) result;

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueTime);
		assertEquals(result.getType(), JsonTestData.TEST_TIME_VALUE.getType());

		assertEquals(castedResult.getValue(),
				JsonTestData.TEST_TIME_VALUE.getValue());

		// test if every field contains the correct value
		assertEquals(castedResult.getSecond(),
				JsonTestData.TEST_TIME_VALUE.getSecond());
		assertEquals(castedResult.getMinute(),
				JsonTestData.TEST_TIME_VALUE.getMinute());
		assertEquals(castedResult.getHour(),
				JsonTestData.TEST_TIME_VALUE.getHour());
		assertEquals(castedResult.getDay(),
				JsonTestData.TEST_TIME_VALUE.getDay());
		assertEquals(castedResult.getMonth(),
				JsonTestData.TEST_TIME_VALUE.getMonth());
		assertEquals(castedResult.getYear(),
				JsonTestData.TEST_TIME_VALUE.getYear());

		assertEquals(castedResult.getAfterTolerance(),
				JsonTestData.TEST_TIME_VALUE.getAfterTolerance());
		assertEquals(castedResult.getBeforeTolerance(),
				JsonTestData.TEST_TIME_VALUE.getBeforeTolerance());
		assertEquals(castedResult.getPrecision(),
				JsonTestData.TEST_TIME_VALUE.getPrecision());
		assertEquals(castedResult.getPreferredCalendarModel(),
				JsonTestData.TEST_TIME_VALUE.getPreferredCalendarModel());
		assertEquals(castedResult.getTimezoneOffset(),
				JsonTestData.TEST_TIME_VALUE.getTimezoneOffset());

		// test against the same time, created on a different way
		JacksonInnerTime otherTime = new JacksonInnerTime(2013, (byte) 10,
				(byte) 28, (byte) 0, (byte) 0, (byte) 0, 0, 0, 0, 11,
				"http://www.wikidata.org/entity/Q1985727");
		assertEquals(((JacksonValueTime) result).getValue(), otherTime);
	}

	@Test
	public void testGlobeCoordinateValueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_GLOBE_COORDINATES_VALUE);
		JsonComparator.compareJsonStrings(
				JsonTestData.JSON_GLOBE_COORDINATES_VALUE, result);
	}

	@Test
	public void testGlobeCoordinateValueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(
				JsonTestData.JSON_GLOBE_COORDINATES_VALUE, JacksonValue.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueGlobeCoordinates);
		assertEquals(result.getType(),
				JsonTestData.TEST_GLOBE_COORDINATES_VALUE.getType());
		assertEquals(((JacksonValueGlobeCoordinates) result).getValue(),
				JsonTestData.TEST_GLOBE_COORDINATES_VALUE.getValue());
	}

	@Test
	public void testQuantityValueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_QUANTITY_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_QUANTITY_VALUE,
				result);
	}

	@Test
	public void testQuantityValueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(
				JsonTestData.JSON_QUANTITY_VALUE, JacksonValue.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueQuantity);
		assertEquals(result.getType(),
				JsonTestData.TEST_QUANTITY_VALUE.getType());
		assertEquals(((JacksonValueQuantity) result).getValue(),
				JsonTestData.TEST_QUANTITY_VALUE.getValue());
	}

	@Test
	public void testMltDatavalueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_MONOLINGUAL_TEXT_VALUE);
		JsonComparator.compareJsonStrings(
				JsonTestData.JSON_MONOLINGUAL_TEXT_VALUE, result);
	}

	@Test
	public void testMltDatavalueToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonValue result = mapper.readValue(
				JsonTestData.JSON_MONOLINGUAL_TEXT_VALUE, JacksonValue.class);

		assertNotNull(result);
		assertTrue(result instanceof JacksonValueMonolingualText);
		assertEquals((result), JsonTestData.TEST_MONOLINGUAL_TEXT_VALUE);
	}

}
