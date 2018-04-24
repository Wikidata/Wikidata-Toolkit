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

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class FormIdValueImplTest {

	private final FormIdValueImpl lexeme1 = new FormIdValueImpl("L42-F1", "http://www.wikidata.org/entity/");
	private final FormIdValueImpl lexeme2 = new FormIdValueImpl("L42-F1", "http://www.wikidata.org/entity/");
	private final FormIdValueImpl lexeme3 = new FormIdValueImpl("L57-F2", "http://www.wikidata.org/entity/");
	private final FormIdValueImpl lexeme4 = new FormIdValueImpl("L42-F1", "http://www.example.org/entity/");

	@Test
	public void entityTypeIsForm() {
		assertEquals(lexeme1.getEntityType(), EntityIdValue.ET_FORM);
	}

	@Test
	public void iriIsCorrect() {
		assertEquals(lexeme1.getIri(), "http://www.wikidata.org/entity/L42-F1");
		assertEquals(lexeme4.getIri(), "http://www.example.org/entity/L42-F1");
	}

	@Test
	public void siteIriIsCorrect() {
		assertEquals(lexeme1.getSiteIri(), "http://www.wikidata.org/entity/");
	}

	@Test
	public void idIsCorrect() {
		assertEquals(lexeme1.getId(), "L42-F1");
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

}
