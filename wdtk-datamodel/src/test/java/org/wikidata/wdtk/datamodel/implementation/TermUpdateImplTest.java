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

import static org.hamcrest.MatcherAssert.assertThat;
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

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.TermUpdateBuilder;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class TermUpdateImplTest {

	private static final MonolingualTextValue EN = Datamodel.makeMonolingualTextValue("hello", "en");
	private static final MonolingualTextValue EN2 = Datamodel.makeMonolingualTextValue("hi", "en");
	private static final MonolingualTextValue SK = Datamodel.makeMonolingualTextValue("ahoj", "sk");
	private static final MonolingualTextValue CS = Datamodel.makeMonolingualTextValue("nazdar", "cs");

	@Test
	public void testFields() {
		TermUpdate update = new TermUpdateImpl(Arrays.asList(EN, SK), Arrays.asList("de", "fr"));
		assertThat(update.getRemoved(), containsInAnyOrder("de", "fr"));
		assertThat(update.getModified().keySet(), containsInAnyOrder("sk", "en"));
		assertEquals(EN, update.getModified().get("en"));
		assertEquals(SK, update.getModified().get("sk"));
	}

	@Test
	public void testValidation() {
		assertThrows(NullPointerException.class, () -> new TermUpdateImpl(null, Collections.emptyList()));
		assertThrows(NullPointerException.class, () -> new TermUpdateImpl(Collections.emptyList(), null));
		assertThrows(NullPointerException.class,
				() -> new TermUpdateImpl(Arrays.asList(EN, null), Collections.emptyList()));
		assertThrows(NullPointerException.class,
				() -> new TermUpdateImpl(Collections.emptyList(), Arrays.asList("en", null)));
		assertThrows(IllegalArgumentException.class,
				() -> new TermUpdateImpl(Arrays.asList(EN, EN2), Collections.emptyList()));
		assertThrows(IllegalArgumentException.class,
				() -> new TermUpdateImpl(Arrays.asList(EN, EN), Collections.emptyList()));
		assertThrows(IllegalArgumentException.class,
				() -> new TermUpdateImpl(Collections.emptyList(), Arrays.asList("en", "")));
		assertThrows(IllegalArgumentException.class,
				() -> new TermUpdateImpl(Collections.emptyList(), Arrays.asList("en", "en")));
		assertThrows(IllegalArgumentException.class, () -> new TermUpdateImpl(Arrays.asList(EN), Arrays.asList("en")));
	}

	@Test
	public void testImmutability() {
		List<MonolingualTextValue> modified = new ArrayList<>();
		List<String> removed = new ArrayList<>();
		modified.add(EN);
		removed.add("sk");
		TermUpdate update = new TermUpdateImpl(modified, removed);
		assertThrows(UnsupportedOperationException.class, () -> update.getModified().put("cs", CS));
		assertThrows(UnsupportedOperationException.class, () -> update.getRemoved().add("fr"));
		modified.add(CS);
		removed.add("fr");
		assertEquals(1, update.getModified().size());
		assertEquals(1, update.getRemoved().size());
	}

	@Test
	public void testEmpty() {
		assertTrue(new TermUpdateImpl(Collections.emptyList(), Collections.emptyList()).isEmpty());
		assertFalse(new TermUpdateImpl(Arrays.asList(EN), Collections.emptyList()).isEmpty());
		assertFalse(new TermUpdateImpl(Collections.emptyList(), Arrays.asList("sk")).isEmpty());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void testEquality() {
		List<MonolingualTextValue> modified = Arrays.asList(EN);
		List<String> removed = Arrays.asList("sk");
		TermUpdate update = new TermUpdateImpl(modified, removed);
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
		assertTrue(update.equals(update));
		assertTrue(update.equals(new TermUpdateImpl(modified, removed)));
		assertFalse(update.equals(new TermUpdateImpl(Collections.emptyList(), removed)));
		assertFalse(update.equals(new TermUpdateImpl(modified, Collections.emptyList())));
	}

	@Test
	public void testHashCode() {
		TermUpdate update1 = new TermUpdateImpl(Arrays.asList(EN, SK), Arrays.asList("cs", "fr"));
		TermUpdate update2 = new TermUpdateImpl(Arrays.asList(EN, SK), Arrays.asList("cs", "fr"));
		assertEquals(update1.hashCode(), update2.hashCode());
	}

	@Test
	public void testJson() {
		assertThat(TermUpdateBuilder.create().build(), producesJson("{}"));
		assertThat(TermUpdateBuilder.create().put(EN).build(),
				producesJson("{'en':{'language':'en','value':'hello'}}"));
		assertThat(TermUpdateBuilder.create().remove("en").build(),
				producesJson("{'en':{'language':'en','remove':''}}"));
	}

}
