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

	MonolingualTextValue en = Datamodel.makeMonolingualTextValue("hello", "en");
	MonolingualTextValue en2 = Datamodel.makeMonolingualTextValue("hi", "en");
	MonolingualTextValue sk = Datamodel.makeMonolingualTextValue("ahoj", "sk");
	MonolingualTextValue cs = Datamodel.makeMonolingualTextValue("nazdar", "cs");
	MonolingualTextValue de = Datamodel.makeMonolingualTextValue("Hallo", "de");
	MonolingualTextValue de2 = Datamodel.makeMonolingualTextValue("Guten Tag", "de");
	MonolingualTextValue fr = Datamodel.makeMonolingualTextValue("Bonjour", "fr");

	@Test
	public void testCreate() {
		TermUpdate update = TermUpdateBuilder.create().build();
		assertThat(update.getModifiedTerms(), is(anEmptyMap()));
		assertThat(update.getRemovedTerms(), is(empty()));
	}

	@Test
	public void testForTerms() {
		assertThrows(NullPointerException.class, () -> TermUpdateBuilder.forTerms(null));
		assertThrows(NullPointerException.class, () -> TermUpdateBuilder.forTerms(Arrays.asList(sk, null)));
		assertThrows(IllegalArgumentException.class, () -> TermUpdateBuilder.forTerms(Arrays.asList(sk, sk)));
		TermUpdate update = TermUpdateBuilder.forTerms(Arrays.asList(sk, en)).build();
		assertThat(update.getModifiedTerms(), is(anEmptyMap()));
		assertThat(update.getRemovedTerms(), is(empty()));
	}

	@Test
	public void testBlindAssignment() {
		TermUpdateBuilder builder = TermUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.setTerm(null));
		builder.removeTerm("sk");
		builder.removeTerm("de");
		builder.setTerm(en); // simple case
		builder.setTerm(sk); // previously removed
		TermUpdate update = builder.build();
		assertThat(update.getRemovedTerms(), containsInAnyOrder("de"));
		assertThat(update.getModifiedTerms().keySet(), containsInAnyOrder("sk", "en"));
		assertEquals(en, update.getModifiedTerms().get("en"));
		assertEquals(sk, update.getModifiedTerms().get("sk"));
	}

	@Test
	public void testBlindRemoval() {
		TermUpdateBuilder builder = TermUpdateBuilder.create();
		assertThrows(NullPointerException.class, () -> builder.removeTerm(null));
		builder.setTerm(en);
		builder.setTerm(sk);
		builder.removeTerm("de"); // simple case
		builder.removeTerm("sk"); // previously assigned
		TermUpdate update = builder.build();
		assertThat(update.getRemovedTerms(), containsInAnyOrder("sk", "de"));
		assertThat(update.getModifiedTerms().keySet(), containsInAnyOrder("en"));
	}

	@Test
	public void testBaseAssignment() {
		TermUpdateBuilder builder = TermUpdateBuilder.forTerms(Arrays.asList(sk, en, de, cs));
		builder.removeTerm("sk");
		builder.removeTerm("de");
		builder.setTerm(fr); // new language key
		builder.setTerm(en2); // new value
		builder.setTerm(cs); // same value
		builder.setTerm(sk); // same value for previously removed
		builder.setTerm(de2); // new value for previously removed
		TermUpdate update = builder.build();
		assertThat(update.getRemovedTerms(), is(empty()));
		assertThat(update.getModifiedTerms().keySet(), containsInAnyOrder("en", "de", "fr"));
		assertEquals(fr, update.getModifiedTerms().get("fr"));
		assertEquals(en2, update.getModifiedTerms().get("en"));
		assertEquals(de2, update.getModifiedTerms().get("de"));
	}

	@Test
	public void testBaseRemoval() {
		TermUpdateBuilder builder = TermUpdateBuilder.forTerms(Arrays.asList(en, sk, cs));
		builder.setTerm(en2);
		builder.setTerm(de);
		builder.removeTerm("sk"); // simple case
		builder.removeTerm("fr"); // not found
		builder.removeTerm("en"); // previously modified
		builder.removeTerm("de"); // previously added
		TermUpdate update = builder.build();
		assertThat(update.getModifiedTerms(), anEmptyMap());
		assertThat(update.getRemovedTerms(), containsInAnyOrder("en", "sk"));
	}

}
