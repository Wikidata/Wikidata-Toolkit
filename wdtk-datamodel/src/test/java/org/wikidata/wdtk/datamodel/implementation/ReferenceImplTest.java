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

import java.util.Collections;
import java.util.Iterator;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class ReferenceImplTest {

	private final EntityIdValue subject = new ItemIdValueImpl("Q42",
			"http://wikidata.org/entity/");
	private final PropertyIdValue property = new PropertyIdValueImpl(
			"P42", "http://wikidata.org/entity/");
	private final ValueSnak valueSnak = new ValueSnakImpl(property, subject);
	private final SnakGroup snakGroup = new SnakGroupImpl(
			Collections. singletonList(valueSnak));
	private final Reference r1 = new ReferenceImpl(Collections.singletonList(snakGroup));
	private final Reference r2 = new ReferenceImpl(Collections.singletonList(snakGroup));

	@Test
	public void snakListIsCorrect() {
		assertEquals(r1.getSnakGroups(),
				Collections.singletonList(snakGroup));
	}

	@Test
	public void equalityBasedOnContent() {
		Reference r3 = new ReferenceImpl(Collections.emptyList());

		assertEquals(r1, r1);
		assertEquals(r1, r2);
		assertNotEquals(r1, r3);
		assertNotEquals(r1, null);
		assertNotEquals(r1, this);
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
