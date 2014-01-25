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

import org.wikidata.wdtk.datamodel.interfaces.EntityId;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;


public class PropertyIdImplTest {

	private PropertyIdImpl prop1;
	private PropertyIdImpl prop2;
	private PropertyIdImpl prop3;
	private PropertyIdImpl prop4;

	@Before
	public void setUp() {
		prop1 = new PropertyIdImpl("P42", "http://www.wikidata.org/entity/");
		prop2 = new PropertyIdImpl("P42", "http://www.wikidata.org/entity/");
		prop3 = new PropertyIdImpl("P57", "http://www.wikidata.org/entity/");
		prop4 = new PropertyIdImpl("P42", "http://www.example.org/entity/");
	}

	@Test
	public void entityTypeIsProperty() {
		assertEquals(prop1.getEntityType(), EntityId.EntityType.PROPERTY);
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
	public void propertyEqualityBasedOnContent() {
		assertEquals(prop1, prop2);
		assertThat(prop1, not(equalTo(prop3)));
		assertThat(prop1, not(equalTo(prop4)));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void propertyIdValidatedForFirstLetter() {
		new PropertyIdImpl("Q12345", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void itemIdValidatedForNumber() {
		new PropertyIdImpl("P34d23", "http://www.wikidata.org/entity/");
	}

}
