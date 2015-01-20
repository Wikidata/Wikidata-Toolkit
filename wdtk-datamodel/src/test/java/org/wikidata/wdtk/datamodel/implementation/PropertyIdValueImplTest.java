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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;

public class PropertyIdValueImplTest {

	private PropertyIdValueImpl prop1;
	private PropertyIdValueImpl prop2;
	private PropertyIdValueImpl prop3;
	private PropertyIdValueImpl prop4;

	@Before
	public void setUp() {
		prop1 = PropertyIdValueImpl.create("P42",
				"http://www.wikidata.org/entity/");
		prop2 = PropertyIdValueImpl.create("P42",
				"http://www.wikidata.org/entity/");
		prop3 = PropertyIdValueImpl.create("P57",
				"http://www.wikidata.org/entity/");
		prop4 = PropertyIdValueImpl.create("P42",
				"http://www.example.org/entity/");
	}

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

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForFirstLetter() {
		PropertyIdValueImpl.create("Q12345", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForLength() {
		ItemIdValueImpl.create("P", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForNumber() {
		PropertyIdValueImpl.create("P34d23", "http://www.wikidata.org/entity/");
	}

}
