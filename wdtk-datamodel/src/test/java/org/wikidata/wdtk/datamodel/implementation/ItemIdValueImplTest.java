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

public class ItemIdValueImplTest {

	private ItemIdValueImpl item1;
	private ItemIdValueImpl item2;
	private ItemIdValueImpl item3;
	private ItemIdValueImpl item4;

	@Before
	public void setUp() {
		item1 = ItemIdValueImpl
				.create("Q42", "http://www.wikidata.org/entity/");
		item2 = ItemIdValueImpl
				.create("Q42", "http://www.wikidata.org/entity/");
		item3 = ItemIdValueImpl
				.create("Q57", "http://www.wikidata.org/entity/");
		item4 = ItemIdValueImpl.create("Q42", "http://www.example.org/entity/");
	}

	@Test
	public void entityTypeIsItem() {
		assertEquals(item1.getEntityType(), EntityIdValue.ET_ITEM);
	}

	@Test
	public void iriIsCorrect() {
		assertEquals(item1.getIri(), "http://www.wikidata.org/entity/Q42");
		assertEquals(item4.getIri(), "http://www.example.org/entity/Q42");
	}

	@Test
	public void siteIriIsCorrect() {
		assertEquals(item1.getSiteIri(), "http://www.wikidata.org/entity/");
	}

	@Test
	public void idIsCorrect() {
		assertEquals(item1.getId(), "Q42");
	}

	@Test
	public void equalityBasedOnContent() {
		assertEquals(item1, item1);
		assertEquals(item1, item2);
		assertThat(item1, not(equalTo(item3)));
		assertThat(item1, not(equalTo(item4)));
		assertThat(item1, not(equalTo(null)));
		assertFalse(item1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(item1.hashCode(), item2.hashCode());
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForFirstLetter() {
		ItemIdValueImpl.create("P12345", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForNumber() {
		ItemIdValueImpl.create("Q34d23", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idValidatedForLength() {
		ItemIdValueImpl.create("Q", "http://www.wikidata.org/entity/");
	}

	@Test(expected = IllegalArgumentException.class)
	public void idNotNull() {
		ItemIdValueImpl.create(null, "http://www.wikidata.org/entity/");
	}

	@Test(expected = NullPointerException.class)
	public void baseIriNotNull() {
		ItemIdValueImpl.create("Q42", null);
	}

}
