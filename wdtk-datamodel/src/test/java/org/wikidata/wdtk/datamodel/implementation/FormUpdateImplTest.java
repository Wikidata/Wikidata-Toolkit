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
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.wikidata.wdtk.datamodel.implementation.JsonTestUtils.producesJson;
import static org.wikidata.wdtk.datamodel.implementation.JsonTestUtils.toJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.FormUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.TermUpdateBuilder;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class FormUpdateImplTest {

	private static final FormIdValue F1 = Datamodel.makeWikidataFormIdValue("L1-F1");
	private static final StatementUpdate STATEMENTS = StatementDocumentUpdateImplTest.STATEMENTS;
	private static final TermUpdate REPRESENTATIONS = TermUpdateBuilder.create().remove("en").build();
	private static final ItemIdValue FEATURE1 = Datamodel.makeWikidataItemIdValue("Q1");
	private static final ItemIdValue FEATURE2 = Datamodel.makeWikidataItemIdValue("Q2");
	private static final List<ItemIdValue> FEATURES = Arrays.asList(FEATURE1);

	@Test
	public void testFields() {
		FormUpdate update = new FormUpdateImpl(F1, 123, REPRESENTATIONS, FEATURES, STATEMENTS);
		assertEquals(F1, update.getEntityId());
		assertEquals(123, update.getBaseRevisionId());
		assertSame(REPRESENTATIONS, update.getRepresentations());
		assertEquals(new HashSet<>(FEATURES), update.getGrammaticalFeatures().get());
		assertSame(STATEMENTS, update.getStatements());
		update = new FormUpdateImpl(F1, 123, REPRESENTATIONS, null, STATEMENTS);
		assertFalse(update.getGrammaticalFeatures().isPresent());
		update = new FormUpdateImpl(F1, 123, REPRESENTATIONS, Collections.emptyList(), STATEMENTS);
		assertThat(update.getGrammaticalFeatures().get(), is(empty()));
	}

	@Test
	public void testValidation() {
		assertThrows(NullPointerException.class,
				() -> new FormUpdateImpl(F1, 0, null, null, StatementUpdate.EMPTY));
		assertThrows(NullPointerException.class, () -> new FormUpdateImpl(
				F1, 0, TermUpdate.EMPTY, Arrays.asList(FEATURE1, null), StatementUpdate.EMPTY));
		assertThrows(IllegalArgumentException.class, () -> new FormUpdateImpl(
				F1, 0, TermUpdate.EMPTY, Arrays.asList(ItemIdValue.NULL), StatementUpdate.EMPTY));
		assertThrows(IllegalArgumentException.class, () -> new FormUpdateImpl(
				F1, 0, TermUpdate.EMPTY, Arrays.asList(FEATURE1, FEATURE1), StatementUpdate.EMPTY));
	}

	@Test
	public void testImmutability() {
		List<ItemIdValue> features = new ArrayList<>();
		features.add(FEATURE1);
		FormUpdate update = new FormUpdateImpl(F1, 123, REPRESENTATIONS, features, STATEMENTS);
		assertThrows(UnsupportedOperationException.class, () -> update.getGrammaticalFeatures().get().add(FEATURE2));
		features.add(FEATURE2);
		assertEquals(1, update.getGrammaticalFeatures().get().size());
	}

	@Test
	public void testEmpty() {
		assertFalse(new FormUpdateImpl(F1, 0, REPRESENTATIONS, null, StatementUpdate.EMPTY).isEmpty());
		assertFalse(
				new FormUpdateImpl(F1, 0, TermUpdate.EMPTY, Collections.emptyList(), StatementUpdate.EMPTY).isEmpty());
		assertFalse(new FormUpdateImpl(F1, 0, TermUpdate.EMPTY, FEATURES, StatementUpdate.EMPTY).isEmpty());
		assertFalse(new FormUpdateImpl(F1, 0, TermUpdate.EMPTY, null, STATEMENTS).isEmpty());
		assertTrue(new FormUpdateImpl(F1, 0, TermUpdate.EMPTY, null, StatementUpdate.EMPTY).isEmpty());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void testEquality() {
		FormUpdate update = new FormUpdateImpl(F1, 0, REPRESENTATIONS, FEATURES, STATEMENTS);
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
		assertTrue(update.equals(update));
		assertTrue(update.equals(new FormUpdateImpl(F1, 0, TermUpdateBuilder.create().remove("en").build(),
				Arrays.asList(Datamodel.makeWikidataItemIdValue("Q1")), STATEMENTS)));
		assertFalse(update.equals(new FormUpdateImpl(F1, 123, REPRESENTATIONS, FEATURES, StatementUpdate.EMPTY)));
		assertFalse(update.equals(new FormUpdateImpl(F1, 123, TermUpdate.EMPTY, FEATURES, STATEMENTS)));
		assertFalse(update.equals(new FormUpdateImpl(F1, 123, REPRESENTATIONS, null, STATEMENTS)));
		assertFalse(update.equals(new FormUpdateImpl(F1, 123, REPRESENTATIONS, Collections.emptyList(), STATEMENTS)));
		assertFalse(new FormUpdateImpl(F1, 123, REPRESENTATIONS, null, STATEMENTS).equals(
				new FormUpdateImpl(F1, 123, REPRESENTATIONS, Collections.emptyList(), STATEMENTS)));
	}

	@Test
	public void testHashCode() {
		FormUpdate update1 = new FormUpdateImpl(F1, 123, REPRESENTATIONS, FEATURES, STATEMENTS);
		FormUpdate update2 = new FormUpdateImpl(F1, 123, TermUpdateBuilder.create().remove("en").build(),
				Arrays.asList(Datamodel.makeWikidataItemIdValue("Q1")), STATEMENTS);
		assertEquals(update1.hashCode(), update2.hashCode());
	}

	@Test
	public void testJson() {
		assertThat(new FormUpdateImpl(F1, 123, TermUpdate.EMPTY, null, StatementUpdate.EMPTY), producesJson("{}"));
		assertThat(FormUpdateBuilder.forEntityId(F1).updateRepresentations(REPRESENTATIONS).build(),
				producesJson("{'representations':" + toJson(REPRESENTATIONS) + "}"));
		assertThat(FormUpdateBuilder.forEntityId(F1).setGrammaticalFeatures(FEATURES).build(),
				producesJson("{'grammaticalFeatures':['Q1']}"));
		assertThat(FormUpdateBuilder.forEntityId(F1).setGrammaticalFeatures(Collections.emptyList()).build(),
				producesJson("{'grammaticalFeatures':[]}"));
		assertThat(FormUpdateBuilder.forEntityId(F1).updateStatements(STATEMENTS).build(),
				producesJson("{'claims':" + toJson(STATEMENTS) + "}"));
	}

}
