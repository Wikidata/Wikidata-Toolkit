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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.json.JsonComparator;
import org.wikidata.wdtk.datamodel.implementation.json.JsonTestData;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;

import java.io.IOException;

public class PropertyIdValueImplTest {

	private final ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);

	private final PropertyIdValueImpl prop1 = new PropertyIdValueImpl("P42", "http://www.wikidata.org/entity/");
	private final PropertyIdValueImpl prop2 = new PropertyIdValueImpl("P42", "http://www.wikidata.org/entity/");
	private final PropertyIdValueImpl prop3 = new PropertyIdValueImpl("P57",	 "http://www.wikidata.org/entity/");
	private final PropertyIdValueImpl prop4 = new PropertyIdValueImpl("P42", "http://www.example.org/entity/");

	@Test
	public void entityTypeIsProperty() {
		assertEquals(prop1.getEntityType(), EntityIdValue.ET_PROPERTY);
	}

	@Test
	public void iriIsCorrect() {
		assertEquals(prop1.getIri(), "http://www.wikidata.org/entity/P42");
		assertEquals(prop4.getIri(), "http://www.example.org/entity/P42");
	}

	@Test
	public void idIsCorrect() {
		assertEquals(prop1.getId(), "P42");
	}

	@Test
	public void equalityBasedOnContent() {
		assertEquals(prop1, prop1);
		assertEquals(prop1, prop2);
		assertThat(prop1, not(equalTo(prop3)));
		assertThat(prop1, not(equalTo(prop4)));
		assertThat(prop1, not(equalTo(null)));
		assertFalse(prop1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(prop1.hashCode(), prop2.hashCode());
	}

	@Test(expected = RuntimeException.class)
	public void idValidatedForFirstLetter() {
		new PropertyIdValueImpl("Q12345", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForLength() {
		new ItemIdValueImpl("P", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForNumber() {
		new PropertyIdValueImpl("P34d23", "http://www.wikidata.org/entity/");
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		String result = mapper
				.writeValueAsString(JsonTestData.TEST_PROPERTY_ID_VALUE);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_PROPERTY_ID_VALUE,
				result);
	}

	@Test
	public void testToJava() throws
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
}
