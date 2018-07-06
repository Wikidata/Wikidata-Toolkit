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
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class SenseIdValueImplTest {

	private final ObjectMapper mapper = new DatamodelMapper("http://www.wikidata.org/entity/");

	private final SenseIdValueImpl sense1 = new SenseIdValueImpl("L42-S1", "http://www.wikidata.org/entity/");
	private final SenseIdValueImpl sense2 = new SenseIdValueImpl("L42-S1", "http://www.wikidata.org/entity/");
	private final SenseIdValueImpl sense3 = new SenseIdValueImpl("L57-S2", "http://www.wikidata.org/entity/");
	private final SenseIdValueImpl sense4 = new SenseIdValueImpl("L42-S1", "http://www.example.org/entity/");
	private final String JSON_SENSE_ID_VALUE = "{\"type\":\"wikibase-entityid\",\"value\":{\"entity-type\":\"sense\",\"id\":\"L42-S1\"}}";
	private final String JSON_SENSE_ID_VALUE_WITHOUT_TYPE = "{\"type\":\"wikibase-entityid\",\"value\":{\"id\":\"L42-S1\"}}";

	@Test
	public void entityTypeIsSense() {
		assertEquals(sense1.getEntityType(), EntityIdValue.ET_SENSE);
	}

	@Test
	public void iriIsCorrect() {
		assertEquals(sense1.getIri(), "http://www.wikidata.org/entity/L42-S1");
		assertEquals(sense4.getIri(), "http://www.example.org/entity/L42-S1");
	}

	@Test
	public void siteIriIsCorrect() {
		assertEquals(sense1.getSiteIri(), "http://www.wikidata.org/entity/");
	}

	@Test
	public void idIsCorrect() {
		assertEquals(sense1.getId(), "L42-S1");
	}

	@Test
	public void equalityBasedOnContent() {
		assertEquals(sense1, sense1);
		assertEquals(sense1, sense2);
		assertNotEquals(sense1, sense3);
		assertNotEquals(sense1, sense4);
		assertNotEquals(sense1, null);
		assertNotEquals(sense1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(sense1.hashCode(), sense2.hashCode());
	}

	@Test(expected = RuntimeException.class)
	public void idValidatedForFirstLetter() {
		new SenseIdValueImpl("Q12345", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForNumber() {
		new SenseIdValueImpl("L34d23", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForLength() {
		new SenseIdValueImpl("L", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForParts() {
		new SenseIdValueImpl("L21", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idNotNull() {
		new SenseIdValueImpl((String)null, "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void baseIriNotNull() {
		new SenseIdValueImpl("L42", null);
	}

	@Test
	public void lexemeIdIsCorrect() {
		assertEquals(sense1.getLexemeId(), new LexemeIdValueImpl("L42", "http://www.wikidata.org/entity/"));
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_SENSE_ID_VALUE, mapper.writeValueAsString(sense1));
	}

	@Test
	public void testToJava() throws IOException {
		assertEquals(sense1, mapper.readValue(JSON_SENSE_ID_VALUE, ValueImpl.class));
	}

	@Test
	public void testToJavaWithoutNumericalID() throws IOException {
		assertEquals(sense1, mapper.readValue(JSON_SENSE_ID_VALUE_WITHOUT_TYPE, ValueImpl.class));
	}
}
