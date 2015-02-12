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
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class ReferenceImplTest {

	Reference r1;
	Reference r2;
	SnakGroup snakGroup;
	ValueSnak valueSnak;

	@Before
	public void setUp() throws Exception {
		EntityIdValue subject = ItemIdValueImpl.create("Q42",
				"http://wikidata.org/entity/");
		PropertyIdValue property = PropertyIdValueImpl.create(
				"P42", "http://wikidata.org/entity/");
		valueSnak = new ValueSnakImpl(property, subject);
		snakGroup = new SnakGroupImpl(
				Collections.<Snak> singletonList(valueSnak));
		r1 = new ReferenceImpl(Collections.<SnakGroup> singletonList(snakGroup));
		r2 = new ReferenceImpl(Collections.<SnakGroup> singletonList(snakGroup));
	}

	@Test
	public void snakListIsCorrect() {
		assertEquals(r1.getSnakGroups(),
				Collections.<SnakGroup> singletonList(snakGroup));
	}

	@Test
	public void equalityBasedOnContent() {
		Reference r3 = new ReferenceImpl(Collections.<SnakGroup> emptyList());

		assertEquals(r1, r1);
		assertEquals(r1, r2);
		assertThat(r1, not(equalTo(r3)));
		assertThat(r1, not(equalTo(null)));
		assertFalse(r1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(r1.hashCode(), r2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void snakListNotNull() {
		new ReferenceImpl(null);
	}

	@Test
	public void iterateOverAllSnaks() {
		Iterator<Snak> snaks = r1.getAllSnaks();

		assertTrue(snaks.hasNext());
		assertEquals(valueSnak, snaks.next());
		assertFalse(snaks.hasNext());
	}

}
