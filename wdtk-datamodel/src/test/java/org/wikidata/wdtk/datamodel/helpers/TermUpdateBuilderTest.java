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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
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
		assertThat(update.getModifiedTerms(), is(anEmptyMap()));
		assertThat(update.getRemovedTerms(), is(empty()));
	}

	@Test
	public void testForTerms() {
		assertThrows(NullPointerException.class, () -> TermUpdateBuilder.forTerms(null));
		assertThrows(NullPointerException.class, () -> TermUpdateBuilder.forTerms(Arrays.asList(SK, null)));
		assertThrows(IllegalArgumentException.class, () -> TermUpdateBuilder.forTerms(Arrays.asList(SK, SK)));
		TermUpdate update = TermUpdateBuilder.forTerms(Arrays.asList(SK, EN)).build();
		assertThat(update.getModifiedTerms(), is(anEmptyMap()));
		assertThat(update.getRemovedTerms(), is(empty()));
	}

	@Test
	public void testBlindAssignment() {
		TermUpdateBuilder builder = TermUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.setTerm(null));
		builder.removeTerm("sk");
		builder.removeTerm("de");
		builder.setTerm(EN); // simple case
		builder.setTerm(SK); // previously removed
		TermUpdate update = builder.build();
		assertThat(update.getRemovedTerms(), containsInAnyOrder("de"));
		assertThat(update.getModifiedTerms().keySet(), containsInAnyOrder("sk", "en"));
		assertEquals(EN, update.getModifiedTerms().get("en"));
		assertEquals(SK, update.getModifiedTerms().get("sk"));
	}

	@Test
	public void testBlindRemoval() {
		TermUpdateBuilder builder = TermUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.removeTerm(null));
		assertThrows(IllegalArgumentException.class, () -> builder.removeTerm(" "));
		builder.setTerm(EN);
		builder.setTerm(SK);
		builder.removeTerm("de"); // simple case
		builder.removeTerm("sk"); // previously assigned
		TermUpdate update = builder.build();
		assertThat(update.getRemovedTerms(), containsInAnyOrder("sk", "de"));
		assertThat(update.getModifiedTerms().keySet(), containsInAnyOrder("en"));
	}

	@Test
	public void testBaseAssignment() {
		TermUpdateBuilder builder = TermUpdateBuilder.forTerms(Arrays.asList(SK, EN, DE, CS));
		builder.removeTerm("sk");
		builder.removeTerm("de");
		builder.setTerm(FR); // new language key
		builder.setTerm(EN2); // new value
		builder.setTerm(CS); // same value
		builder.setTerm(SK); // same value for previously removed
		builder.setTerm(DE2); // new value for previously removed
		TermUpdate update = builder.build();
		assertThat(update.getRemovedTerms(), is(empty()));
		assertThat(update.getModifiedTerms().keySet(), containsInAnyOrder("en", "de", "fr"));
		assertEquals(FR, update.getModifiedTerms().get("fr"));
		assertEquals(EN2, update.getModifiedTerms().get("en"));
		assertEquals(DE2, update.getModifiedTerms().get("de"));
	}

	@Test
	public void testBaseRemoval() {
		TermUpdateBuilder builder = TermUpdateBuilder.forTerms(Arrays.asList(EN, SK, CS));
		builder.setTerm(EN2);
		builder.setTerm(DE);
		builder.removeTerm("sk"); // simple case
		builder.removeTerm("fr"); // not found
		builder.removeTerm("en"); // previously modified
		builder.removeTerm("de"); // previously added
		TermUpdate update = builder.build();
		assertThat(update.getModifiedTerms(), anEmptyMap());
		assertThat(update.getRemovedTerms(), containsInAnyOrder("en", "sk"));
	}

	@Test
	public void testMerge() {
		assertThrows(NullPointerException.class, () -> TermUpdateBuilder.create().apply(null));
		TermUpdate update = TermUpdateBuilder.create()
				.setTerm(EN) // prior assignment
				.removeTerm("sk") // prior removal
				.apply(TermUpdateBuilder.create()
						.setTerm(DE) // another replacement
						.removeTerm("cs") // another removal
						.build())
				.build();
		assertThat(update.getModifiedTerms().values(), containsInAnyOrder(EN, DE));
		assertThat(update.getRemovedTerms(), containsInAnyOrder("sk", "cs"));
	}

}
