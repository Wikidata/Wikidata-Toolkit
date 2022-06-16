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
package org.wikidata.wdtk.datamodel.helpers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class TermUpdateBuilderTest {

	static final MonolingualTextValue EN = Datamodel.makeMonolingualTextValue("hello", "en");
	static final MonolingualTextValue EN2 = Datamodel.makeMonolingualTextValue("hi", "en");
	static final MonolingualTextValue SK = Datamodel.makeMonolingualTextValue("ahoj", "sk");
	static final MonolingualTextValue CS = Datamodel.makeMonolingualTextValue("nazdar", "cs");
	static final MonolingualTextValue DE = Datamodel.makeMonolingualTextValue("Hallo", "de");
	static final MonolingualTextValue DE2 = Datamodel.makeMonolingualTextValue("Guten Tag", "de");
	static final MonolingualTextValue FR = Datamodel.makeMonolingualTextValue("Bonjour", "fr");

	@Test
	public void testCreate() {
		TermUpdate update = TermUpdateBuilder.create().build();
		assertThat(update.getModified(), is(anEmptyMap()));
		assertThat(update.getRemoved(), is(empty()));
	}

	@Test
	public void testForTerms() {
		assertThrows(NullPointerException.class, () -> TermUpdateBuilder.forTerms(null));
		assertThrows(NullPointerException.class, () -> TermUpdateBuilder.forTerms(Arrays.asList(SK, null)));
		assertThrows(IllegalArgumentException.class, () -> TermUpdateBuilder.forTerms(Arrays.asList(SK, SK)));
		TermUpdate update = TermUpdateBuilder.forTerms(Arrays.asList(SK, EN)).build();
		assertThat(update.getModified(), is(anEmptyMap()));
		assertThat(update.getRemoved(), is(empty()));
	}

	@Test
	public void testBlindAssignment() {
		TermUpdateBuilder builder = TermUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.put(null));
		builder.remove("sk");
		builder.remove("de");
		builder.put(EN); // simple case
		builder.put(SK); // previously removed
		TermUpdate update = builder.build();
		assertThat(update.getRemoved(), containsInAnyOrder("de"));
		assertThat(update.getModified().keySet(), containsInAnyOrder("sk", "en"));
		assertEquals(EN, update.getModified().get("en"));
		assertEquals(SK, update.getModified().get("sk"));
	}

	@Test
	public void testBlindRemoval() {
		TermUpdateBuilder builder = TermUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.remove(null));
		assertThrows(IllegalArgumentException.class, () -> builder.remove(" "));
		builder.put(EN);
		builder.put(SK);
		builder.remove("de"); // simple case
		builder.remove("sk"); // previously assigned
		TermUpdate update = builder.build();
		assertThat(update.getRemoved(), containsInAnyOrder("sk", "de"));
		assertThat(update.getModified().keySet(), containsInAnyOrder("en"));
	}

	@Test
	public void testBaseAssignment() {
		TermUpdateBuilder builder = TermUpdateBuilder.forTerms(Arrays.asList(SK, EN, DE, CS));
		builder.remove("sk");
		builder.remove("de");
		builder.put(FR); // new language key
		builder.put(EN2); // new value
		builder.put(CS); // same value
		builder.put(SK); // same value for previously removed
		builder.put(DE2); // new value for previously removed
		TermUpdate update = builder.build();
		assertThat(update.getRemoved(), is(empty()));
		assertThat(update.getModified().keySet(), containsInAnyOrder("en", "de", "fr"));
		assertEquals(FR, update.getModified().get("fr"));
		assertEquals(EN2, update.getModified().get("en"));
		assertEquals(DE2, update.getModified().get("de"));
	}

	@Test
	public void testBaseRemoval() {
		TermUpdateBuilder builder = TermUpdateBuilder.forTerms(Arrays.asList(EN, SK, CS));
		builder.put(EN2);
		builder.put(DE);
		builder.remove("sk"); // simple case
		builder.remove("fr"); // not found
		builder.remove("en"); // previously modified
		builder.remove("de"); // previously added
		TermUpdate update = builder.build();
		assertThat(update.getModified(), anEmptyMap());
		assertThat(update.getRemoved(), containsInAnyOrder("en", "sk"));
	}

	@Test
	public void testMerge() {
		assertThrows(NullPointerException.class, () -> TermUpdateBuilder.create().append(null));
		TermUpdate update = TermUpdateBuilder.create()
				.put(EN) // prior assignment
				.remove("sk") // prior removal
				.append(TermUpdateBuilder.create()
						.put(DE) // another replacement
						.remove("cs") // another removal
						.build())
				.build();
		assertThat(update.getModified().values(), containsInAnyOrder(EN, DE));
		assertThat(update.getRemoved(), containsInAnyOrder("sk", "cs"));
	}

}
