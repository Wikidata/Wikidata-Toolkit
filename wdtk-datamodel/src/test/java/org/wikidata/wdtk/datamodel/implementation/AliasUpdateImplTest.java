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
package org.wikidata.wdtk.datamodel.implementation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.wikidata.wdtk.datamodel.implementation.JsonTestUtils.producesJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.AliasUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.AliasUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

public class AliasUpdateImplTest {

	private static final MonolingualTextValue EN = Datamodel.makeMonolingualTextValue("hello", "en");
	private static final MonolingualTextValue EN2 = Datamodel.makeMonolingualTextValue("hi", "en");
	private static final MonolingualTextValue EN3 = Datamodel.makeMonolingualTextValue("hey", "en");
	private static final MonolingualTextValue EN4 = Datamodel.makeMonolingualTextValue("howdy", "en");
	private static final MonolingualTextValue SK = Datamodel.makeMonolingualTextValue("ahoj", "sk");

	@Test
	public void testFields() {
		AliasUpdate empty = new AliasUpdateImpl(null, Collections.emptyList(), Collections.emptyList());
		assertFalse(empty.getLanguageCode().isPresent());
		assertFalse(empty.getRecreated().isPresent());
		assertThat(empty.getAdded(), is(Matchers.empty()));
		assertThat(empty.getRemoved(), is(Matchers.empty()));
		AliasUpdate cleared = new AliasUpdateImpl(Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList());
		assertFalse(cleared.getLanguageCode().isPresent());
		assertThat(cleared.getRecreated().get(), is(Matchers.empty()));
		assertThat(cleared.getAdded(), is(Matchers.empty()));
		assertThat(cleared.getRemoved(), is(Matchers.empty()));
		AliasUpdate recreated = new AliasUpdateImpl(Arrays.asList(EN, EN2), Collections.emptyList(),
				Collections.emptyList());
		assertEquals("en", recreated.getLanguageCode().get());
		assertEquals(Arrays.asList(EN, EN2), recreated.getRecreated().get());
		assertThat(recreated.getAdded(), is(Matchers.empty()));
		assertThat(recreated.getRemoved(), is(Matchers.empty()));
		AliasUpdate incremental = new AliasUpdateImpl(null, Arrays.asList(EN, EN2), Arrays.asList(EN3));
		assertEquals("en", incremental.getLanguageCode().get());
		assertFalse(incremental.getRecreated().isPresent());
		assertThat(incremental.getAdded(), contains(EN, EN2));
		assertThat(incremental.getRemoved(), containsInAnyOrder(EN3));
	}

	@Test
	public void testValidation() {
		assertThrows(NullPointerException.class, () -> new AliasUpdateImpl(null, null, Collections.emptyList()));
		assertThrows(NullPointerException.class, () -> new AliasUpdateImpl(null, Collections.emptyList(), null));
		assertThrows(NullPointerException.class,
				() -> new AliasUpdateImpl(Arrays.asList(EN, null), Collections.emptyList(), Collections.emptyList()));
		assertThrows(NullPointerException.class,
				() -> new AliasUpdateImpl(null, Arrays.asList(EN, null), Collections.emptyList()));
		assertThrows(NullPointerException.class,
				() -> new AliasUpdateImpl(null, Collections.emptyList(), Arrays.asList(EN, null)));
		assertThrows(IllegalArgumentException.class,
				() -> new AliasUpdateImpl(Collections.emptyList(), Arrays.asList(EN), Collections.emptyList()));
		assertThrows(IllegalArgumentException.class,
				() -> new AliasUpdateImpl(Collections.emptyList(), Collections.emptyList(), Arrays.asList(EN)));
		assertThrows(IllegalArgumentException.class,
				() -> new AliasUpdateImpl(Arrays.asList(EN, SK), Collections.emptyList(), Collections.emptyList()));
		assertThrows(IllegalArgumentException.class,
				() -> new AliasUpdateImpl(null, Arrays.asList(EN, SK), Collections.emptyList()));
		assertThrows(IllegalArgumentException.class,
				() -> new AliasUpdateImpl(null, Collections.emptyList(), Arrays.asList(EN, SK)));
		assertThrows(IllegalArgumentException.class,
				() -> new AliasUpdateImpl(null, Arrays.asList(EN), Arrays.asList(SK)));
		assertThrows(IllegalArgumentException.class,
				() -> new AliasUpdateImpl(Arrays.asList(EN, EN), Collections.emptyList(), Collections.emptyList()));
		assertThrows(IllegalArgumentException.class,
				() -> new AliasUpdateImpl(null, Arrays.asList(EN, EN), Collections.emptyList()));
		assertThrows(IllegalArgumentException.class,
				() -> new AliasUpdateImpl(null, Collections.emptyList(), Arrays.asList(EN, EN)));
		assertThrows(IllegalArgumentException.class,
				() -> new AliasUpdateImpl(null, Arrays.asList(EN), Arrays.asList(EN)));
	}

	@Test
	public void testImmutability() {
		List<MonolingualTextValue> recreated = new ArrayList<>();
		List<MonolingualTextValue> added = new ArrayList<>();
		List<MonolingualTextValue> removed = new ArrayList<>();
		recreated.add(EN);
		added.add(EN);
		removed.add(EN2);
		AliasUpdate update1 = new AliasUpdateImpl(recreated, Collections.emptyList(), Collections.emptyList());
		assertThrows(UnsupportedOperationException.class, () -> update1.getRecreated().get().add(EN4));
		assertThrows(UnsupportedOperationException.class, () -> update1.getAdded().add(EN4));
		assertThrows(UnsupportedOperationException.class, () -> update1.getRemoved().add(EN4));
		AliasUpdate update2 = new AliasUpdateImpl(null, added, removed);
		assertThrows(UnsupportedOperationException.class, () -> update2.getAdded().add(EN4));
		assertThrows(UnsupportedOperationException.class, () -> update2.getRemoved().add(EN4));
		recreated.add(EN2);
		added.add(EN3);
		removed.add(EN4);
		assertEquals(1, update1.getRecreated().get().size());
		assertEquals(1, update2.getAdded().size());
		assertEquals(1, update2.getRemoved().size());
	}

	@Test
	public void testEmpty() {
		assertTrue(new AliasUpdateImpl(null, Collections.emptyList(), Collections.emptyList()).isEmpty());
		assertFalse(new AliasUpdateImpl(null, Arrays.asList(EN), Collections.emptyList()).isEmpty());
		assertFalse(new AliasUpdateImpl(null, Collections.emptyList(), Arrays.asList(EN)).isEmpty());
		assertFalse(new AliasUpdateImpl(Arrays.asList(EN), Collections.emptyList(), Collections.emptyList()).isEmpty());
		assertFalse(new AliasUpdateImpl(Collections.emptyList(), Collections.emptyList(), Collections.emptyList())
				.isEmpty());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void testEquality() {
		List<MonolingualTextValue> recreated = Arrays.asList(EN);
		List<MonolingualTextValue> added = Arrays.asList(EN2);
		List<MonolingualTextValue> removed = Arrays.asList(EN3);
		AliasUpdate update1 = new AliasUpdateImpl(recreated, Collections.emptyList(), Collections.emptyList());
		AliasUpdate update2 = new AliasUpdateImpl(null, added, removed);
		assertFalse(update1.equals(null));
		assertFalse(update2.equals(null));
		assertFalse(update1.equals(this));
		assertFalse(update2.equals(this));
		assertTrue(update1.equals(update1));
		assertTrue(update2.equals(update2));
		assertFalse(update1.equals(update2));
		assertTrue(update1.equals(new AliasUpdateImpl(recreated, Collections.emptyList(), Collections.emptyList())));
		assertTrue(update2.equals(new AliasUpdateImpl(null, added, removed)));
		assertFalse(update1.equals(
				new AliasUpdateImpl(Arrays.asList(EN2), Collections.emptyList(), Collections.emptyList())));
		assertFalse(update2.equals(new AliasUpdateImpl(null, Arrays.asList(EN4), removed)));
		assertFalse(update2.equals(new AliasUpdateImpl(null, added, Arrays.asList(EN4))));
	}

	@Test
	public void testHashCode() {
		AliasUpdate update1a = new AliasUpdateImpl(Arrays.asList(EN), Collections.emptyList(), Collections.emptyList());
		AliasUpdate update1b = new AliasUpdateImpl(Arrays.asList(EN), Collections.emptyList(), Collections.emptyList());
		AliasUpdate update2a = new AliasUpdateImpl(null, Arrays.asList(EN), Arrays.asList(EN2));
		AliasUpdate update2b = new AliasUpdateImpl(null, Arrays.asList(EN), Arrays.asList(EN2));
		assertEquals(update1a.hashCode(), update1b.hashCode());
		assertEquals(update2a.hashCode(), update2b.hashCode());
	}

	@Test
	public void testJson() {
		assertThat(AliasUpdateBuilder.create().build(), producesJson("null"));
		assertThat(AliasUpdateBuilder.create().recreate(Collections.emptyList()).build(), producesJson("[]"));
		assertThat(AliasUpdateBuilder.create().recreate(Arrays.asList(EN, EN2)).build(),
				producesJson("[{'language':'en','value':'hello'},{'language':'en','value':'hi'}]"));
		assertThat(AliasUpdateBuilder.create().add(EN).build(),
				producesJson("[{'add':'','language':'en','value':'hello'}]"));
		assertThat(AliasUpdateBuilder.create().remove(EN).build(),
				producesJson("[{'language':'en','remove':'','value':'hello'}]"));
	}

}
