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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;

public class SnakGroupTest {

	SnakGroup sg1;
	SnakGroup sg2;
	Snak snak1;
	Snak snak2;
	EntityIdValue subject;
	PropertyIdValue property;

	@Before
	public void setUp() throws Exception {
		subject = ItemIdValueImpl.create("Q42",
				"http://wikidata.org/entity/");
		property = PropertyIdValueImpl.create("P42",
				"http://wikidata.org/entity/");

		snak1 = new ValueSnakImpl(property, subject);
		snak2 = new SomeValueSnakImpl(property);

		sg1 = new SnakGroupImpl(Collections.<Snak> singletonList(snak1));
		sg2 = new SnakGroupImpl(Collections.<Snak> singletonList(snak1));
	}

	@Test
	public void snakListIsCorrect() {
		assertEquals(sg1.getSnaks(), Collections.<Snak> singletonList(snak1));
	}

	@Test
	public void propertyIsCorrect() {
		assertEquals(sg1.getProperty(), property);
	}

	@Test
	public void equalityBasedOnContent() {
		List<Snak> snaks = new ArrayList<Snak>();
		snaks.add(snak1);
		snaks.add(snak2);
		SnakGroup sg3 = new SnakGroupImpl(snaks);

		assertEquals(sg1, sg1);
		assertEquals(sg1, sg2);
		assertThat(sg1, not(equalTo(sg3)));
		assertThat(sg1, not(equalTo(null)));
		assertFalse(sg1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(sg1.hashCode(), sg2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void snakListNotNull() {
		new SnakGroupImpl(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void snakListNotEmpty() {
		new SnakGroupImpl(Collections.<Snak> emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void snakListRequiresSameProperty() {
		List<Snak> snaks = new ArrayList<Snak>();

		snaks.add(snak1);

		PropertyIdValue property2 = PropertyIdValueImpl
				.create("P23", "http://wikidata.org/entity/");
		Snak snak3 = new NoValueSnakImpl(property2);
		snaks.add(snak3);

		new SnakGroupImpl(snaks);
	}

}
