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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.json.JsonComparator;
import org.wikidata.wdtk.datamodel.implementation.json.JsonTestData;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class ItemIdValueImplTest {

	private final ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);

	private final ItemIdValueImpl item1 = new ItemIdValueImpl("Q42", "http://www.wikidata.org/entity/");
	private final ItemIdValueImpl item2 = new ItemIdValueImpl("Q42", "http://www.wikidata.org/entity/");
	private final ItemIdValueImpl item3 = new ItemIdValueImpl("Q57", "http://www.wikidata.org/entity/");
	private final ItemIdValueImpl item4 = new ItemIdValueImpl("Q42", "http://www.example.org/entity/");

	@Test
	public void entityTypeIsItem() {
		assertEquals(item1.getEntityType(), EntityIdValue.ET_ITEM);
	}

	@Test
	public void iriIsCorrect() {
		assertEquals(item1.getIri(), "http://www.wikidata.org/entity/Q42");
		assertEquals(item4.getIri(), "http://www.example.org/entity/Q42");
	}

	@Test
	public void siteIriIsCorrect() {
		assertEquals(item1.getSiteIri(), "http://www.wikidata.org/entity/");
	}

	@Test
	public void idIsCorrect() {
		assertEquals(item1.getId(), "Q42");
	}

	@Test
	public void equalityBasedOnContent() {
		assertEquals(item1, item1);
		assertEquals(item1, item2);
		assertThat(item1, not(equalTo(item3)));
		assertThat(item1, not(equalTo(item4)));
		assertThat(item1, not(equalTo(null)));
		assertFalse(item1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(item1.hashCode(), item2.hashCode());
	}

	@Test(expected = RuntimeException.class)
	public void idValidatedForFirstLetter() {
		new ItemIdValueImpl("P12345", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForNumber() {
		new ItemIdValueImpl("Q34d23", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForLength() {
		new ItemIdValueImpl("Q", "http://www.wikidata.org/entity/");
	}

	@Test(expected = RuntimeException.class)
	public void idNotNull() {
		new ItemIdValueImpl((String)null, "http://www.wikidata.org/entity/");
	}

	@Test(expected = NullPointerException.class)
	public void baseIriNotNull() {
		new ItemIdValueImpl("Q42", null);
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_ITEM_ID_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_ITEM_ID_VALUE,
				result);
	}

	@Test
	public void testToJava() throws IOException {
		assertItemIdValue(mapper.readValue(JsonTestData.JSON_ITEM_ID_VALUE,
				ValueImpl.class));
	}

	@Test
	public void testToJavaWithoutId() throws IOException {
		assertItemIdValue(mapper.readValue(JsonTestData.JSON_ITEM_ID_VALUE_WITHOUT_ID,
				ValueImpl.class));
	}

	@Test
	public void testToJavaWithoutNumericalId() throws IOException {
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

}
