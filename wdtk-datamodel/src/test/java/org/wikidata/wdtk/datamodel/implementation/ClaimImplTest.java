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
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class ClaimImplTest {

	private final EntityIdValue subject = new ItemIdValueImpl("Q42", "http://wikidata.org/entity/");
	private final ValueSnak mainSnak = new ValueSnakImpl(
			new PropertyIdValueImpl("P42", "http://wikidata.org/entity/"),
			subject
	);
	private final Claim c1 = new ClaimImpl(subject, mainSnak, Collections.emptyList());
	private final Claim c2 = new ClaimImpl(subject, mainSnak, Collections.emptyList());

	@Test
	public void gettersWorking() {
		assertEquals(c1.getSubject(), subject);
		assertEquals(c1.getMainSnak(), mainSnak);
		assertEquals(c1.getQualifiers(), Collections.emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void subjectNotNull() {
		new ClaimImpl(null, mainSnak, Collections.emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void mainSnakNotNull() {
		new ClaimImpl(subject, null, Collections.emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void qualifiersNotNull() {
		new ClaimImpl(subject, mainSnak, null);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(c1.hashCode(), c2.hashCode());
	}

	@Test
	public void equalityBasedOnContent() {
		Claim cDiffSubject, cDiffMainSnak, cDiffQualifiers;
		EntityIdValue subject2 = new ItemIdValueImpl("Q43",
				"http://wikidata.org/entity/");
		PropertyIdValue property = new PropertyIdValueImpl(
				"P43", "http://wikidata.org/entity/");
		ValueSnak mainSnak2 = new ValueSnakImpl(property, subject2);

		cDiffSubject = new ClaimImpl(subject2, mainSnak,
				Collections.emptyList());
		cDiffMainSnak = new ClaimImpl(subject, mainSnak2,
				Collections.emptyList());
		cDiffQualifiers = new ClaimImpl(subject, mainSnak,
				Collections.singletonList(new SnakGroupImpl(
						Collections. singletonList(mainSnak))));

		assertEquals(c1, c1);
		assertEquals(c1, c2);
		assertNotEquals(c1, cDiffSubject);
		assertNotEquals(c1, cDiffMainSnak);
		assertNotEquals(c1, cDiffQualifiers);
		assertNotEquals(c1, null);
		assertNotEquals(c1, this);
	}

	@Test
	public void accessSnakGroups() {
		EntityIdValue value1 = new ItemIdValueImpl("Q1",
				"http://wikidata.org/entity/");
		EntityIdValue value2 = new ItemIdValueImpl("Q2",
				"http://wikidata.org/entity/");
		PropertyIdValue property1 = new PropertyIdValueImpl("P1", "http://wikidata.org/entity/");
		PropertyIdValue property2 = new PropertyIdValueImpl("P2", "http://wikidata.org/entity/");
		Snak snak1 = new ValueSnakImpl(property1, value1);
		Snak snak2 = new ValueSnakImpl(property1, value2);
		Snak snak3 = new ValueSnakImpl(property2, value2);

		List<Snak> snakList1 = new ArrayList<>();
		snakList1.add(snak1);
		snakList1.add(snak2);

		SnakGroup snakGroup1 = new SnakGroupImpl(snakList1);
		SnakGroup snakGroup2 = new SnakGroupImpl(
				Collections.singletonList(snak3));
		List<SnakGroup> snakGroups = new ArrayList<>();
		snakGroups.add(snakGroup1);
		snakGroups.add(snakGroup2);

		Claim claim = new ClaimImpl(subject, mainSnak, snakGroups);

		Iterator<Snak> snaks = claim.getAllQualifiers();

		assertTrue(snaks.hasNext());
		assertEquals(snak1, snaks.next());
		assertTrue(snaks.hasNext());
		assertEquals(snak2, snaks.next());
		assertTrue(snaks.hasNext());
		assertEquals(snak3, snaks.next());
		assertFalse(snaks.hasNext());
	}
}
