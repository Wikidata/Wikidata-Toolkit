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
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

import java.io.IOException;

public class SnakImplTest {

	private final ObjectMapper mapper = new DatamodelMapper("http://example.com/entity/");

	private final PropertyIdValue p1 = new PropertyIdValueImpl("P42", "http://example.com/entity/");
	private final PropertyIdValue p2 = new PropertyIdValueImpl("P43", "http://example.com/entity/");
	private final ValueSnak vs1 = new ValueSnakImpl(p1, p1);
	private final ValueSnak vs2 = new ValueSnakImpl(p1, p1);
	private final ValueSnak vs3 = new ValueSnakImpl(p2, p1);
	private final ValueSnak vs4 = new ValueSnakImpl(p1, p2);
	private final ValueSnak vsmt1 = new ValueSnakImpl(p1, new TermImpl("en", "foo"));
	private final ValueSnak vsmt2 = new ValueSnakImpl(p1, new MonolingualTextValueImpl("foo", "en"));
	private final SomeValueSnak svs1 = new SomeValueSnakImpl(p1);
	private final SomeValueSnak svs2 = new SomeValueSnakImpl(p1);
	private final SomeValueSnak svs3 = new SomeValueSnakImpl(p2);
	private final NoValueSnak nvs1 = new NoValueSnakImpl(p1);
	private final NoValueSnak nvs2 = new NoValueSnakImpl(p1);
	private final NoValueSnak nvs3 = new NoValueSnakImpl(p2);
	private final String JSON_NOVALUE_SNAK = "{\"snaktype\":\"novalue\",\"property\":\"P42\"}";
	private final String JSON_SOMEVALUE_SNAK = "{\"snaktype\":\"somevalue\",\"property\":\"P42\"}";
	private final String JSON_VALUE_SNAK = "{\"snaktype\":\"value\",\"property\":\"P42\",\"datatype\":\"wikibase-property\",\"datavalue\":{\"value\":{\"id\":\"P42\",\"numeric-id\":42,\"entity-type\":\"property\"},\"type\":\"wikibase-entityid\"}}";
	private final String JSON_MONOLINGUAL_TEXT_VALUE_SNAK = "{\"snaktype\":\"value\",\"property\":\"P42\",\"datatype\":\"monolingualtext\",\"datavalue\":{\"value\":{\"language\":\"en\",\"text\":\"foo\"},\"type\":\"monolingualtext\"}}";
	private final String JSON_SNAK_UNKNOWN_ID = "{\"snaktype\":\"value\",\"property\":\"P42\",\"datatype\":\"wikibase-funkyid\",\"datavalue\":{\"value\":{\"id\":\"FUNKY42\",\"entity-type\":\"funky\"},\"type\":\"wikibase-entityid\"}}";
	private final String JSON_SNAK_UNKNOWN_DATAVALUE = "{\"snaktype\":\"value\",\"property\":\"P42\",\"datatype\":\"groovy\",\"datavalue\":{\"foo\":\"bar\",\"type\":\"groovyvalue\"}}";
	
	@Test
	public void fieldsAreCorrect() {
		assertEquals(vs1.getPropertyId(), p1);
		assertEquals(vs1.getValue(), p1);

		assertEquals(svs1.getPropertyId(), p1);
		assertNull(svs1.getValue());

		assertEquals(nvs1.getPropertyId(), p1);
		assertNull(nvs1.getValue());
	}

	@Test
	public void snakHashBasedOnContent() {
		assertEquals(vs1.hashCode(), vs2.hashCode());
		assertEquals(vsmt1.hashCode(), vsmt2.hashCode());
		assertEquals(svs1.hashCode(), svs2.hashCode());
		assertEquals(nvs1.hashCode(), nvs2.hashCode());
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
		assertEquals(vsmt1, vsmt2);
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
	public void testNoValueSnakToJava() throws IOException {
		assertEquals(nvs1, mapper.readValue(JSON_NOVALUE_SNAK, SnakImpl.class));
	}

	@Test
	public void testNoValueSnakToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_NOVALUE_SNAK, mapper.writeValueAsString(nvs1));
	}

	@Test
	public void testSomeValueSnakToJava() throws IOException {
		assertEquals(svs1, mapper.readValue(JSON_SOMEVALUE_SNAK, SnakImpl.class));
	}

	@Test
	public void testSomeValueSnakToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_SOMEVALUE_SNAK, mapper.writeValueAsString(svs1));

	}

	@Test
	public void testValueSnakToJava() throws IOException {
		assertEquals(vs1, mapper.readValue(JSON_VALUE_SNAK, SnakImpl.class));
	}

	@Test
	public void testValueSnakToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_VALUE_SNAK, mapper.writeValueAsString(vs1));
	}

	@Test
	public void testMonolingualTextValueSnakToJava() throws IOException {
		assertEquals(vsmt1, mapper.readValue(JSON_MONOLINGUAL_TEXT_VALUE_SNAK, SnakImpl.class));
		assertEquals(vsmt2, mapper.readValue(JSON_MONOLINGUAL_TEXT_VALUE_SNAK, SnakImpl.class));
	}

	@Test
	public void testMonolingualTextValueSnakToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_MONOLINGUAL_TEXT_VALUE_SNAK, mapper.writeValueAsString(vsmt1));
		JsonComparator.compareJsonStrings(JSON_MONOLINGUAL_TEXT_VALUE_SNAK, mapper.writeValueAsString(vsmt2));
	}
	
	@Test
	public void testDeserializeUnknownIdSnak() throws IOException {
		// We only require deserialization not to fail here
		mapper.readValue(JSON_SNAK_UNKNOWN_ID, SnakImpl.class);
	}
	
	@Test
	public void testDeserializeUnknownDatavalueSnak() throws IOException {
		// We only require deserialization not to fail here
		mapper.readValue(JSON_SNAK_UNKNOWN_DATAVALUE, SnakImpl.class);
	}
}
