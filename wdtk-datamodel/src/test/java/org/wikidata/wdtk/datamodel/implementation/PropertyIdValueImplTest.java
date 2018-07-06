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
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;

import java.io.IOException;

public class PropertyIdValueImplTest {

	private final ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);

	private final PropertyIdValueImpl prop1 = new PropertyIdValueImpl("P42", "http://www.wikidata.org/entity/");
	private final PropertyIdValueImpl prop2 = new PropertyIdValueImpl("P42", "http://www.wikidata.org/entity/");
	private final PropertyIdValueImpl prop3 = new PropertyIdValueImpl("P57",	 "http://www.wikidata.org/entity/");
	private final PropertyIdValueImpl prop4 = new PropertyIdValueImpl("P42", "http://www.example.org/entity/");
	private final String JSON_PROPERTY_ID_VALUE = "{\"type\":\"wikibase-entityid\",\"value\":{\"entity-type\":\"property\",\"numeric-id\":42,\"id\":\"P42\"}}";
	private final String JSON_PROPERTY_ID_VALUE_WITHOUT_NUMERICAL_ID = "{\"type\":\"wikibase-entityid\",\"value\":{\"id\":\"P42\"}}";

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
		assertNotEquals(prop1, prop3);
		assertNotEquals(prop1, prop4);
		assertNotEquals(prop1, null);
		assertNotEquals(prop1, this);
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
		JsonComparator.compareJsonStrings(JSON_PROPERTY_ID_VALUE, mapper.writeValueAsString(prop1));
	}

	@Test
	public void testToJava() throws
			IOException {
		assertEquals(prop1, mapper.readValue(JSON_PROPERTY_ID_VALUE, ValueImpl.class));
	}

	@Test
	public void testToJavaWithoutNumericalID() throws IOException {
		assertEquals(prop1, mapper.readValue(JSON_PROPERTY_ID_VALUE_WITHOUT_NUMERICAL_ID, ValueImpl.class));
	}
}
