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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.*;

public class SnakGroupTest {

	private SnakGroup sg1;
	private SnakGroup sg2;
	private Snak snak1;
	private Snak snak2;
	private PropertyIdValue property;

	@Before
	public void setUp() {
		EntityIdValue subject = new ItemIdValueImpl("Q42",
				"http://wikidata.org/entity/");
		property = new PropertyIdValueImpl("P42",
				"http://wikidata.org/entity/");

		snak1 = new ValueSnakImpl(property, subject);
		snak2 = new SomeValueSnakImpl(property);

		sg1 = new SnakGroupImpl(Collections.singletonList(snak1));
		sg2 = new SnakGroupImpl(Collections.singletonList(snak1));
	}

	@Test
	public void implementsCollection() {
		assertFalse(sg1.isEmpty());
		assertEquals(1, sg1.size());
		assertTrue(sg1.contains(snak1));
		assertFalse(sg1.contains(snak2));
		assertTrue(sg1.iterator().hasNext());
		assertEquals(sg1.iterator().next(), snak1);
		assertArrayEquals(new Snak[] {snak1}, sg1.toArray());
	}

	@Test
	public void snakListIsCorrect() {
		assertEquals(sg1.getSnaks(), Collections.singletonList(snak1));
	}

	@Test
	public void propertyIsCorrect() {
		assertEquals(sg1.getProperty(), property);
	}

	@Test
	public void equalityBasedOnContent() {
		List<Snak> snaks = new ArrayList<>();
		snaks.add(snak1);
		snaks.add(snak2);
		SnakGroup sg3 = new SnakGroupImpl(snaks);

		assertEquals(sg1, sg1);
		assertEquals(sg1, sg2);
		assertNotEquals(sg1, sg3);
		assertNotEquals(sg1, null);
		assertNotEquals(sg1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(sg1.hashCode(), sg2.hashCode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void snakListNotNull() {
		new SnakGroupImpl(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void snakListNotEmpty() {
		new SnakGroupImpl(Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void snakListRequiresSameProperty() {
		List<Snak> snaks = new ArrayList<>();

		snaks.add(snak1);

		PropertyIdValue property2 = new PropertyIdValueImpl("P23", "http://wikidata.org/entity/");
		Snak snak3 = new NoValueSnakImpl(property2);
		snaks.add(snak3);

		new SnakGroupImpl(snaks);
	}

}
