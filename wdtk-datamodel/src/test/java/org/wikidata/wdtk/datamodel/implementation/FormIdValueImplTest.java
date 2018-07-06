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

public class FormIdValueImplTest {

	private final ObjectMapper mapper = new DatamodelMapper("http://www.wikidata.org/entity/");

	private final FormIdValueImpl form1 = new FormIdValueImpl("L42-F1", "http://www.wikidata.org/entity/");
	private final FormIdValueImpl form2 = new FormIdValueImpl("L42-F1", "http://www.wikidata.org/entity/");
	private final FormIdValueImpl form3 = new FormIdValueImpl("L57-F2", "http://www.wikidata.org/entity/");
	private final FormIdValueImpl form4 = new FormIdValueImpl("L42-F1", "http://www.example.org/entity/");
	private final String JSON_FORM_ID_VALUE = "{\"type\":\"wikibase-entityid\",\"value\":{\"entity-type\":\"form\",\"id\":\"L42-F1\"}}";
	private final String JSON_FORM_ID_VALUE_WITHOUT_TYPE = "{\"type\":\"wikibase-entityid\",\"value\":{\"id\":\"L42-F1\"}}";

	@Test
	public void entityTypeIsForm() {
		assertEquals(form1.getEntityType(), EntityIdValue.ET_FORM);
	}

	@Test
	public void iriIsCorrect() {
		assertEquals(form1.getIri(), "http://www.wikidata.org/entity/L42-F1");
		assertEquals(form4.getIri(), "http://www.example.org/entity/L42-F1");
	}

	@Test
	public void siteIriIsCorrect() {
		assertEquals(form1.getSiteIri(), "http://www.wikidata.org/entity/");
	}

	@Test
	public void idIsCorrect() {
		assertEquals(form1.getId(), "L42-F1");
	}

	@Test
	public void equalityBasedOnContent() {
		assertEquals(form1, form1);
		assertEquals(form1, form2);
		assertNotEquals(form1, form3);
		assertNotEquals(form1, form4);
		assertNotEquals(form1, null);
		assertNotEquals(form1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(form1.hashCode(), form2.hashCode());
	}

	@Test(expected = RuntimeException.class)
	public void idValidatedForFirstLetter() {
		new FormIdValueImpl("Q12345", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForNumber() {
		new FormIdValueImpl("L34d23", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForLength() {
		new FormIdValueImpl("L", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForParts() {
		new FormIdValueImpl("L21", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idNotNull() {
		new FormIdValueImpl((String)null, "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void baseIriNotNull() {
		new FormIdValueImpl("L42", null);
	}

	@Test
	public void lexemeIdIsCorrect() {
		assertEquals(form1.getLexemeId(), new LexemeIdValueImpl("L42", "http://www.wikidata.org/entity/"));
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_FORM_ID_VALUE, mapper.writeValueAsString(form1));
	}

	@Test
	public void testToJava() throws IOException {
		assertEquals(form1, mapper.readValue(JSON_FORM_ID_VALUE, ValueImpl.class));
	}

	@Test
	public void testToJavaWithoutNumericalID() throws IOException {
		assertEquals(form1, mapper.readValue(JSON_FORM_ID_VALUE_WITHOUT_TYPE, ValueImpl.class));
	}
}
