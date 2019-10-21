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
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LexemeIdValueImplTest {

	private final ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);

	private final LexemeIdValueImpl lexeme1 = new LexemeIdValueImpl("L42", "http://www.wikidata.org/entity/");
	private final LexemeIdValueImpl lexeme2 = new LexemeIdValueImpl("L42", "http://www.wikidata.org/entity/");
	private final LexemeIdValueImpl lexeme3 = new LexemeIdValueImpl("L57", "http://www.wikidata.org/entity/");
	private final LexemeIdValueImpl lexeme4 = new LexemeIdValueImpl("L42", "http://www.example.org/entity/");
	private final String JSON_LEXEME_ID_VALUE = "{\"type\":\"wikibase-entityid\",\"value\":{\"entity-type\":\"lexeme\",\"numeric-id\":42,\"id\":\"L42\"}}";
	private final String JSON_LEXEME_ID_VALUE_WITHOUT_NUMERICAL_ID = "{\"type\":\"wikibase-entityid\",\"value\":{\"id\":\"L42\"}}";

	@Test
	public void entityTypeIsLexeme() {
		assertEquals(lexeme1.getEntityType(), EntityIdValue.ET_LEXEME);
	}

	@Test
	public void iriIsCorrect() {
		assertEquals(lexeme1.getIri(), "http://www.wikidata.org/entity/L42");
		assertEquals(lexeme4.getIri(), "http://www.example.org/entity/L42");
	}

	@Test
	public void siteIriIsCorrect() {
		assertEquals(lexeme1.getSiteIri(), "http://www.wikidata.org/entity/");
	}

	@Test
	public void idIsCorrect() {
		assertEquals(lexeme1.getId(), "L42");
	}

	@Test
	public void equalityBasedOnContent() {
		assertEquals(lexeme1, lexeme1);
		assertEquals(lexeme1, lexeme2);
		assertNotEquals(lexeme1, lexeme3);
		assertNotEquals(lexeme1, lexeme4);
		assertNotEquals(lexeme1, null);
		assertNotEquals(lexeme1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(lexeme1.hashCode(), lexeme2.hashCode());
	}

	@Test(expected = RuntimeException.class)
	public void idValidatedForFirstLetter() {
		new LexemeIdValueImpl("Q12345", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForNumber() {
		new LexemeIdValueImpl("L34d23", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForLength() {
		new LexemeIdValueImpl("L", "http://www.wikidata.org/entity/");
	}

	@Test(expected = RuntimeException.class)
	public void idNotNull() {
		new LexemeIdValueImpl((String)null, "http://www.wikidata.org/entity/");
	}

	@Test(expected = NullPointerException.class)
	public void baseIriNotNull() {
		new LexemeIdValueImpl("L42", null);
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_LEXEME_ID_VALUE, mapper.writeValueAsString(lexeme1));
	}

	@Test
	public void testToJava() throws IOException {
		assertEquals(lexeme1, mapper.readValue(JSON_LEXEME_ID_VALUE, ValueImpl.class));
	}

	@Test
	public void testToJavaWithoutNumericalID() throws IOException {
		assertEquals(lexeme1, mapper.readValue(JSON_LEXEME_ID_VALUE_WITHOUT_NUMERICAL_ID, ValueImpl.class));
	}

}
