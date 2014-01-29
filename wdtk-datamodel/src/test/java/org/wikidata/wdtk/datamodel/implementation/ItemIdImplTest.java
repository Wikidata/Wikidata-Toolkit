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
import org.wikidata.wdtk.datamodel.implementation.ItemIdImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityId;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

public class ItemIdImplTest {

	private ItemIdImpl item1;
	private ItemIdImpl item2;
	private ItemIdImpl item3;
	private ItemIdImpl item4;

	@Before
	public void setUp() {
		item1 = new ItemIdImpl("Q42", "http://www.wikidata.org/entity/");
		item2 = new ItemIdImpl("Q42", "http://www.wikidata.org/entity/");
		item3 = new ItemIdImpl("Q57", "http://www.wikidata.org/entity/");
		item4 = new ItemIdImpl("Q42", "http://www.example.org/entity/");
	}

	@Test
	public void entityTypeIsItem() {
		assertEquals(item1.getEntityType(), EntityId.EntityType.ITEM);
	}

	@Test
	public void iriIsCorrect() {
		assertEquals(item1.getIri(), "http://www.wikidata.org/entity/Q42");
		assertEquals(item4.getIri(), "http://www.example.org/entity/Q42");
	}

	@Test
	public void idIsCorrect() {
		assertEquals(item1.getId(), "Q42");
	}

	@Test
	public void itemEqualityBasedOnContent() {
		assertEquals(item1, item1);
		assertEquals(item1, item2);
		assertThat(item1, not(equalTo(item3)));
		assertThat(item1, not(equalTo(item4)));
		assertThat(item1, not(equalTo(null)));
		assertFalse(item1.equals(this));
	}

	@Test
	public void itemHashBasedOnContent() {
		assertEquals(item1.hashCode(), item2.hashCode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void itemIdValidatedForFirstLetter() {
		new ItemIdImpl("P12345", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void itemIdValidatedForNumber() {
		new ItemIdImpl("Q34d23", "http://www.wikidata.org/entity/");
	}

	@Test(expected = NullPointerException.class)
	public void idNotNull() {
		new ItemIdImpl(null, "http://www.wikidata.org/entity/");
	}

	@Test(expected = NullPointerException.class)
	public void baseIriNotNull() {
		new ItemIdImpl("Q42", null);
	}

}
