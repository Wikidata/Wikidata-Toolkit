package org.wikidata.wdtk.datamodel.implementation.json;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.ItemIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.ValueImpl;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonInnerTime;
import org.wikidata.wdtk.datamodel.implementation.GlobeCoordinatesValueImpl;
import org.wikidata.wdtk.datamodel.implementation.MonolingualTextValueImpl;
import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.QuantityValueImpl;
import org.wikidata.wdtk.datamodel.implementation.StringValueImpl;
import org.wikidata.wdtk.datamodel.implementation.TimeValueImpl;

import java.io.IOException;

import static org.junit.Assert.*;

public class TestValue {

	ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);

	@Test
	public void testStringValueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_STRING_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_STRING_VALUE,
				result);
	}

	@Test
	public void testStringValueToJava() throws
			IOException {
		ValueImpl result = mapper.readValue(JsonTestData.JSON_STRING_VALUE,
				ValueImpl.class);

		assertTrue(result instanceof StringValueImpl);
		assertEquals(result.getType(), JsonTestData.TEST_STRING_VALUE.getType());
		assertEquals(((StringValueImpl) result).getValue(),
				JsonTestData.TEST_STRING_VALUE.getValue());
		assertEquals(JsonTestData.TEST_STRING_VALUE, result);
	}

	@Test
	public void testItemIdValueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_ITEM_ID_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_ITEM_ID_VALUE,
				result);
	}

	@Test
	public void testItemIdValueToJava() throws
			IOException {
		assertItemIdValue(mapper.readValue(JsonTestData.JSON_ITEM_ID_VALUE,
				ValueImpl.class));
	}

	@Test
	public void testItemIdValueToJavaWithoutId() throws
			IOException {
		assertItemIdValue(mapper.readValue(JsonTestData.JSON_ITEM_ID_VALUE_WITHOUT_ID,
				ValueImpl.class));
	}

	@Test
	public void testItemIdValueToJavaWithoutNumericalId() throws
			IOException {
		assertItemIdValue(mapper.readValue(JsonTestData.JSON_ITEM_ID_VALUE_WITHOUT_NUMERICAL_ID,
				ValueImpl.class));
	}

	private void assertItemIdValue(ValueImpl result) {
		assertTrue(result instanceof ItemIdValueImpl);

		assertEquals(result.getType(),
				JsonTestData.TEST_ITEM_ID_VALUE.getType());
		assertEquals(((ItemIdValueImpl) result).getValue(),
				JsonTestData.TEST_ITEM_ID_VALUE.getValue());
		assertEquals(JsonTestData.TEST_ITEM_ID_VALUE, result);
	}

	@Test
	public void testPropertyIdValueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_PROPERTY_ID_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_PROPERTY_ID_VALUE,
				result);
	}

	@Test
	public void testPropertyIdValueToJava() throws
			IOException {
		ValueImpl result = mapper.readValue(
				JsonTestData.JSON_PROPERTY_ID_VALUE, ValueImpl.class);

		assertTrue(result instanceof PropertyIdValueImpl);

		assertEquals(result.getType(),
				JsonTestData.TEST_PROPERTY_ID_VALUE.getType());
		assertEquals(((PropertyIdValueImpl) result).getValue(),
				JsonTestData.TEST_PROPERTY_ID_VALUE.getValue());
		assertEquals(JsonTestData.TEST_PROPERTY_ID_VALUE, result);
	}

	@Test
	public void testTimeValueToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(JsonTestData.TEST_TIME_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_TIME_VALUE, result);
	}

	@Test
	public void testTimeValueToJava() throws
			IOException {
		ValueImpl result = mapper.readValue(JsonTestData.JSON_TIME_VALUE,
				ValueImpl.class);
		TimeValueImpl castedResult = (TimeValueImpl) result;

		assertNotNull(result);
		assertTrue(result instanceof TimeValueImpl);
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
		assertEquals(((TimeValueImpl) result).getValue(), otherTime);
	}

	@Test
	public void testGlobeCoordinateValueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_GLOBE_COORDINATES_VALUE);
		JsonComparator.compareJsonStrings(
				JsonTestData.JSON_GLOBE_COORDINATES_VALUE, result);
	}

	@Test
	public void testGlobeCoordinateValueToJava() throws
			IOException {
		ValueImpl result = mapper.readValue(
				JsonTestData.JSON_GLOBE_COORDINATES_VALUE, ValueImpl.class);

		assertTrue(result instanceof GlobeCoordinatesValueImpl);
		assertEquals(result.getType(),
				JsonTestData.TEST_GLOBE_COORDINATES_VALUE.getType());
		assertEquals(((GlobeCoordinatesValueImpl) result).getValue(),
				JsonTestData.TEST_GLOBE_COORDINATES_VALUE.getValue());
		assertEquals(JsonTestData.TEST_GLOBE_COORDINATES_VALUE, result);
	}

	@Test
	public void testQuantityValueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_QUANTITY_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_QUANTITY_VALUE,
				result);
	}

	@Test
	public void testQuantityValueToJava() throws
			IOException {
		ValueImpl result = mapper.readValue(
				JsonTestData.JSON_QUANTITY_VALUE, ValueImpl.class);

		assertTrue(result instanceof QuantityValueImpl);
		assertEquals(result.getType(),
				JsonTestData.TEST_QUANTITY_VALUE.getType());
		assertEquals(((QuantityValueImpl) result).getValue(),
				JsonTestData.TEST_QUANTITY_VALUE.getValue());
		assertEquals(JsonTestData.TEST_QUANTITY_VALUE, result);
	}

	@Test
	public void testUnboundedQuantityValueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_UNBOUNDED_QUANTITY_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_UNBOUNDED_QUANTITY_VALUE,
				result);
	}

	@Test
	public void testUnboundedQuantityValueToJava() throws
			IOException {
		ValueImpl result = mapper.readValue(
				JsonTestData.JSON_UNBOUNDED_QUANTITY_VALUE, ValueImpl.class);

		assertTrue(result instanceof QuantityValueImpl);
		assertEquals(result.getType(),
				JsonTestData.TEST_UNBOUNDED_QUANTITY_VALUE.getType());
		assertEquals(((QuantityValueImpl) result).getValue(),
				JsonTestData.TEST_UNBOUNDED_QUANTITY_VALUE.getValue());
		assertEquals(JsonTestData.TEST_UNBOUNDED_QUANTITY_VALUE, result);
	}

	@Test
	public void testMltDatavalueToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_MONOLINGUAL_TEXT_VALUE);
		JsonComparator.compareJsonStrings(
				JsonTestData.JSON_MONOLINGUAL_TEXT_VALUE, result);
	}

	@Test
	public void testMltDatavalueToJava() throws
			IOException {
		ValueImpl result = mapper.readValue(
				JsonTestData.JSON_MONOLINGUAL_TEXT_VALUE, ValueImpl.class);

		assertTrue(result instanceof MonolingualTextValueImpl);
		assertEquals(JsonTestData.TEST_MONOLINGUAL_TEXT_VALUE, result);

	}

}
