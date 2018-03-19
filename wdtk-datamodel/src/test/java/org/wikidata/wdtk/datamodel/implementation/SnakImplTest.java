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
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.json.JsonComparator;
import org.wikidata.wdtk.datamodel.implementation.json.JsonTestData;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

import java.io.IOException;

public class SnakImplTest {

	private final ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);

	private final PropertyIdValue p1 = new PropertyIdValueImpl("P42", "http://example.com/entity/");
	private final PropertyIdValue p2 = new PropertyIdValueImpl("P43", "http://example.com/entity/");
	private final ValueSnak vs1 = new ValueSnakImpl(p1, p1);
	private final ValueSnak vs2 = new ValueSnakImpl(p1, p1);
	private final ValueSnak vs3 = new ValueSnakImpl(p2, p1);
	private final ValueSnak vs4 = new ValueSnakImpl(p1, p2);
	private final SomeValueSnak svs1 = new SomeValueSnakImpl(p1);
	private final SomeValueSnak svs2 = new SomeValueSnakImpl(p1);
	private final SomeValueSnak svs3 = new SomeValueSnakImpl(p2);
	private final NoValueSnak nvs1 = new NoValueSnakImpl(p1);
	private final NoValueSnak nvs2 = new NoValueSnakImpl(p1);
	private final NoValueSnak nvs3 = new NoValueSnakImpl(p2);

	@Test
	public void snakHashBasedOnContent() {
		assertEquals(vs1.hashCode(), vs2.hashCode());
		assertEquals(svs1.hashCode(), svs2.hashCode());
		assertEquals(nvs1.hashCode(), nvs2.hashCode());
	}

	@Test
	public void snaksWithoutValues() {
		assertEquals(svs1.getValue(), null);
		assertEquals(nvs1.getValue(), null);
	}

	@Test
	public void snakEqualityBasedOnType() {
		assertNotEquals(svs1, nvs1);
		assertNotEquals(nvs1, svs1);
		assertNotEquals(vs1, svs1);
	}

	@Test
	public void valueSnakEqualityBasedOnContent() {
		assertEquals(vs1, vs1);
		assertEquals(vs1, vs2);
		assertNotEquals(vs1, vs3);
		assertNotEquals(vs1, vs4);
		assertNotEquals(vs1, null);
	}

	@Test
	public void someValueSnakEqualityBasedOnContent() {
		assertEquals(svs1, svs1);
		assertEquals(svs1, svs2);
		assertNotEquals(svs1, svs3);
		assertNotEquals(svs1, null);
	}

	@Test
	public void noValueSnakEqualityBasedOnContent() {
		assertEquals(nvs1, nvs1);
		assertEquals(nvs1, nvs2);
		assertNotEquals(nvs1, nvs3);
		assertNotEquals(nvs1, null);
	}

	@Test(expected = NullPointerException.class)
	public void snakPropertyNotNull() {
		new SomeValueSnakImpl(null);
	}

	@Test(expected = NullPointerException.class)
	public void snakValueNotNull() {
		new ValueSnakImpl(new PropertyIdValueImpl("P42",
				"http://example.com/entity/"), null);
	}

	@Test
	public void testNoValueSnakToJava() throws
			IOException {
		SnakImpl result = mapper.readValue(JsonTestData.JSON_NOVALUE_SNAK,
				SnakImpl.class);

		assertNotNull(result);
		assertNull(result.getValue());
		assertTrue(result instanceof NoValueSnakImpl);
		assertEquals(result, JsonTestData.TEST_NOVALUE_SNAK);
	}

	@Test
	public void testNoValueSnakToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_NOVALUE_SNAK);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_NOVALUE_SNAK,
				result);
	}

	@Test
	public void testSomeValueSnakToJava() throws
			IOException {
		SnakImpl result = mapper.readValue(JsonTestData.JSON_SOMEVALUE_SNAK,
				SnakImpl.class);

		assertNotNull(result);
		assertNull(result.getValue());
		assertTrue(result instanceof SomeValueSnakImpl);
		assertEquals(result, JsonTestData.TEST_SOMEVALUE_SNAK);
	}

	@Test
	public void testSomeValueSnakToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_SOMEVALUE_SNAK);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_SOMEVALUE_SNAK,
				result);
	}

	@Test
	public void testCommonsValueSnakToJava() throws
			IOException {
		SnakImpl result = mapper.readValue(
				JsonTestData.JSON_VALUE_SNAK_STRING, SnakImpl.class);

		assertNotNull(result);
		assertTrue(result instanceof ValueSnakImpl);
		assertEquals(result, JsonTestData.TEST_STRING_VALUE_SNAK);
	}

	@Test
	public void testCommonsValueSnakToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_STRING_VALUE_SNAK);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_VALUE_SNAK_STRING,
				result);
	}

	@Test
	public void testCommonsValueSnakToJavaWithHash() throws
			IOException {
		SnakImpl result = mapper.readValue(
				JsonTestData.JSON_VALUE_SNAK_STRING_HASH, SnakImpl.class);

		assertNotNull(result);
		assertTrue(result instanceof ValueSnakImpl);
		assertEquals(result, JsonTestData.TEST_STRING_VALUE_SNAK);
	}
	
}
