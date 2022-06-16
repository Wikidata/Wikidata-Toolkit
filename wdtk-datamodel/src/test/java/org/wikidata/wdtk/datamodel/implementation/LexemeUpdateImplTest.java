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
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.containsInAnyOrder;
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
import java.util.List;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.FormUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.LexemeUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.SenseUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.TermUpdateBuilder;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeUpdate;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class LexemeUpdateImplTest {

	private static final LexemeIdValue L1 = Datamodel.makeWikidataLexemeIdValue("L1");
	private static final StatementUpdate STATEMENTS = StatementDocumentUpdateImplTest.STATEMENTS;
	private static final TermUpdate LEMMAS = TermUpdateBuilder.create().remove("en").build();
	private static final ItemIdValue Q1 = Datamodel.makeWikidataItemIdValue("Q1");
	private static final ItemIdValue Q2 = Datamodel.makeWikidataItemIdValue("Q2");
	private static final SenseIdValue S1 = Datamodel.makeWikidataSenseIdValue("L1-S1");
	private static final SenseIdValue S2 = Datamodel.makeWikidataSenseIdValue("L1-S2");
	private static final SenseIdValue S3 = Datamodel.makeWikidataSenseIdValue("L1-S3");
	private static final SenseDocument ADDED_SENSE = Datamodel.makeSenseDocument(
			SenseIdValue.NULL, Collections.emptyList(), Collections.emptyList());
	private static final List<SenseDocument> ADDED_SENSES = Arrays.asList(ADDED_SENSE);
	private static final SenseUpdate UPDATED_SENSE = SenseUpdateBuilder.forEntityId(S1)
			.updateStatements(STATEMENTS)
			.build();
	private static final SenseUpdate UPDATED_SENSE_REVISION = SenseUpdateBuilder.forBaseRevisionId(S1, 123)
			.append(UPDATED_SENSE)
			.build();
	private static final List<SenseUpdate> UPDATED_SENSES = Arrays.asList(UPDATED_SENSE);
	private static final List<SenseUpdate> UPDATED_SENSE_REVISIONS = Arrays.asList(UPDATED_SENSE_REVISION);
	private static final List<SenseIdValue> REMOVED_SENSES = Arrays.asList(S2);
	private static final FormIdValue F1 = Datamodel.makeWikidataFormIdValue("L1-F1");
	private static final FormIdValue F2 = Datamodel.makeWikidataFormIdValue("L1-F2");
	private static final FormIdValue F3 = Datamodel.makeWikidataFormIdValue("L1-F3");
	private static final FormDocument ADDED_FORM = Datamodel.makeFormDocument(
			FormIdValue.NULL, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
	private static final List<FormDocument> ADDED_FORMS = Arrays.asList(ADDED_FORM);
	private static final FormUpdate UPDATED_FORM = FormUpdateBuilder.forEntityId(F1)
			.updateStatements(STATEMENTS)
			.build();
	private static final FormUpdate UPDATED_FORM_REVISION = FormUpdateBuilder.forBaseRevisionId(F1, 123)
			.append(UPDATED_FORM)
			.build();
	private static final List<FormUpdate> UPDATED_FORMS = Arrays.asList(UPDATED_FORM);
	private static final List<FormUpdate> UPDATED_FORM_REVISIONS = Arrays.asList(UPDATED_FORM_REVISION);
	private static final List<FormIdValue> REMOVED_FORMS = Arrays.asList(F2);

	@Test
	public void testFields() {
		LexemeUpdate update = new LexemeUpdateImpl(L1, 123, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSE_REVISIONS, REMOVED_SENSES,
				ADDED_FORMS, UPDATED_FORM_REVISIONS, REMOVED_FORMS);
		assertEquals(L1, update.getEntityId());
		assertEquals(123, update.getBaseRevisionId());
		assertEquals(Q1, update.getLanguage().get());
		assertEquals(Q2, update.getLexicalCategory().get());
		assertSame(LEMMAS, update.getLemmas());
		assertSame(STATEMENTS, update.getStatements());
		assertEquals(ADDED_SENSES, update.getAddedSenses());
		assertThat(update.getUpdatedSenses().keySet(), containsInAnyOrder(S1));
		assertEquals(UPDATED_SENSE_REVISION, update.getUpdatedSenses().get(S1));
		assertThat(update.getRemovedSenses(), containsInAnyOrder(S2));
		assertEquals(ADDED_FORMS, update.getAddedForms());
		assertThat(update.getUpdatedForms().keySet(), containsInAnyOrder(F1));
		assertEquals(UPDATED_FORM_REVISION, update.getUpdatedForms().get(F1));
		assertThat(update.getRemovedForms(), containsInAnyOrder(F2));
		update = new LexemeUpdateImpl(L1, 123, null, null, LEMMAS, STATEMENTS,
				ADDED_SENSES, Arrays.asList(SenseUpdateBuilder.forBaseRevisionId(S1, 123).build()), REMOVED_SENSES,
				ADDED_FORMS, Arrays.asList(FormUpdateBuilder.forBaseRevisionId(F1, 123).build()), REMOVED_FORMS);
		assertFalse(update.getLanguage().isPresent());
		assertFalse(update.getLexicalCategory().isPresent());
		assertThat(update.getUpdatedSenses(), is(anEmptyMap()));
		assertThat(update.getUpdatedForms(), is(anEmptyMap()));
	}

	@Test
	public void testValidation() {
		// null parameter
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, null, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, null,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				null, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, null, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, null, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, null, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, null, REMOVED_FORMS));
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, null));
		// null item
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				Arrays.asList(ADDED_SENSE, null), UPDATED_SENSES, REMOVED_SENSES,
				ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, Arrays.asList(UPDATED_SENSE, null), REMOVED_SENSES,
				ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, Arrays.asList(S2, null),
				ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES,
				Arrays.asList(ADDED_FORM, null), UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES,
				ADDED_FORMS, Arrays.asList(UPDATED_FORM, null), REMOVED_FORMS));
		assertThrows(NullPointerException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES,
				ADDED_FORMS, UPDATED_FORMS, Arrays.asList(F2, null)));
		// placeholder ID
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, ItemIdValue.NULL, Q2, LEMMAS,
				STATEMENTS, ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, ItemIdValue.NULL, LEMMAS,
				STATEMENTS, ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, Arrays.asList(S2, SenseIdValue.NULL),
				ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES,
				ADDED_FORMS, UPDATED_FORMS, Arrays.asList(F2, FormIdValue.NULL)));
		// expected placeholder ID
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				Arrays.asList(ADDED_SENSE.withEntityId(S3)), UPDATED_SENSES, REMOVED_SENSES,
				ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES,
				Arrays.asList(ADDED_FORM.withEntityId(F3)), UPDATED_FORMS, REMOVED_FORMS));
		// unique IDs
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, Arrays.asList(UPDATED_SENSE, UPDATED_SENSE), REMOVED_SENSES,
				ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, Arrays.asList(S2, S2),
				ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, Arrays.asList(S1),
				ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES,
				ADDED_FORMS, Arrays.asList(UPDATED_FORM, UPDATED_FORM), REMOVED_FORMS));
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES,
				ADDED_FORMS, UPDATED_FORMS, Arrays.asList(F2, F2)));
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES,
				ADDED_FORMS, UPDATED_FORMS, Arrays.asList(F1)));
		// consistent revision
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSE_REVISIONS, REMOVED_SENSES,
				ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS));
		assertThrows(IllegalArgumentException.class, () -> new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES,
				ADDED_FORMS, UPDATED_FORM_REVISIONS, REMOVED_FORMS));
	}

	@Test
	public void testImmutability() {
		List<SenseDocument> addedSenses = new ArrayList<>(ADDED_SENSES);
		List<SenseUpdate> updatedSenses = new ArrayList<>(UPDATED_SENSE_REVISIONS);
		List<SenseIdValue> removedSenses = new ArrayList<>(REMOVED_SENSES);
		List<FormDocument> addedForms = new ArrayList<>(ADDED_FORMS);
		List<FormUpdate> updatedForms = new ArrayList<>(UPDATED_FORM_REVISIONS);
		List<FormIdValue> removedForms = new ArrayList<>(REMOVED_FORMS);
		LexemeUpdate update = new LexemeUpdateImpl(L1, 123, Q1, Q2, LEMMAS, STATEMENTS,
				addedSenses, updatedSenses, removedSenses, addedForms, updatedForms, removedForms);
		assertThrows(UnsupportedOperationException.class, () -> update.getAddedSenses().clear());
		assertThrows(UnsupportedOperationException.class, () -> update.getUpdatedSenses().clear());
		assertThrows(UnsupportedOperationException.class, () -> update.getRemovedSenses().clear());
		assertThrows(UnsupportedOperationException.class, () -> update.getAddedForms().clear());
		assertThrows(UnsupportedOperationException.class, () -> update.getUpdatedForms().clear());
		assertThrows(UnsupportedOperationException.class, () -> update.getRemovedForms().clear());
		addedSenses.clear();
		updatedSenses.clear();
		removedSenses.clear();
		addedForms.clear();
		updatedForms.clear();
		removedForms.clear();
		assertEquals(1, update.getAddedSenses().size());
		assertEquals(1, update.getUpdatedSenses().size());
		assertEquals(1, update.getRemovedSenses().size());
		assertEquals(1, update.getAddedForms().size());
		assertEquals(1, update.getUpdatedForms().size());
		assertEquals(1, update.getRemovedForms().size());
	}

	@Test
	public void testEmpty() {
		LexemeUpdate update = new LexemeUpdateImpl(L1, 0, null, null, TermUpdate.EMPTY, StatementUpdate.EMPTY,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
		assertTrue(update.isEmpty());
		update = new LexemeUpdateImpl(L1, 0, Q1, null, TermUpdate.EMPTY, StatementUpdate.EMPTY,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
		assertFalse(update.isEmpty());
		update = new LexemeUpdateImpl(L1, 0, null, Q2, TermUpdate.EMPTY, StatementUpdate.EMPTY,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
		assertFalse(update.isEmpty());
		update = new LexemeUpdateImpl(L1, 0, null, null, LEMMAS, StatementUpdate.EMPTY,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
		assertFalse(update.isEmpty());
		update = new LexemeUpdateImpl(L1, 0, null, null, TermUpdate.EMPTY, STATEMENTS,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
		assertFalse(update.isEmpty());
		update = new LexemeUpdateImpl(L1, 0, null, null, TermUpdate.EMPTY, StatementUpdate.EMPTY,
				ADDED_SENSES, Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
		assertFalse(update.isEmpty());
		update = new LexemeUpdateImpl(L1, 0, null, null, TermUpdate.EMPTY, StatementUpdate.EMPTY,
				Collections.emptyList(), UPDATED_SENSES, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
		assertFalse(update.isEmpty());
		update = new LexemeUpdateImpl(L1, 0, null, null, TermUpdate.EMPTY, StatementUpdate.EMPTY,
				Collections.emptyList(), Collections.emptyList(), REMOVED_SENSES,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
		assertFalse(update.isEmpty());
		update = new LexemeUpdateImpl(L1, 0, null, null, TermUpdate.EMPTY, StatementUpdate.EMPTY,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				ADDED_FORMS, Collections.emptyList(), Collections.emptyList());
		assertFalse(update.isEmpty());
		update = new LexemeUpdateImpl(L1, 0, null, null, TermUpdate.EMPTY, StatementUpdate.EMPTY,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), UPDATED_FORMS, Collections.emptyList());
		assertFalse(update.isEmpty());
		update = new LexemeUpdateImpl(L1, 0, null, null, TermUpdate.EMPTY, StatementUpdate.EMPTY,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), REMOVED_FORMS);
		assertFalse(update.isEmpty());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void testEquality() {
		LexemeUpdate update = new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS);
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
		assertTrue(update.equals(update));
		assertTrue(update.equals(new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS)));
		assertFalse(update.equals(new LexemeUpdateImpl(L1, 0, null, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS)));
		assertFalse(update.equals(new LexemeUpdateImpl(L1, 0, Q1, null, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS)));
		assertFalse(update.equals(new LexemeUpdateImpl(L1, 0, Q1, Q2, TermUpdate.EMPTY, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS)));
		assertFalse(update.equals(new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, StatementUpdate.EMPTY,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS)));
		assertFalse(update.equals(new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				Collections.emptyList(), UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS)));
		assertFalse(update.equals(new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, Collections.emptyList(), REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS)));
		assertFalse(update.equals(new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, Collections.emptyList(), ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS)));
		assertFalse(update.equals(new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, Collections.emptyList(), UPDATED_FORMS, REMOVED_FORMS)));
		assertFalse(update.equals(new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, Collections.emptyList(), REMOVED_FORMS)));
		assertFalse(update.equals(new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, Collections.emptyList())));
	}

	@Test
	public void testHashCode() {
		LexemeUpdate update1 = new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS);
		LexemeUpdate update2 = new LexemeUpdateImpl(L1, 0, Q1, Q2, LEMMAS, STATEMENTS,
				ADDED_SENSES, UPDATED_SENSES, REMOVED_SENSES, ADDED_FORMS, UPDATED_FORMS, REMOVED_FORMS);
		assertEquals(update1.hashCode(), update2.hashCode());
	}

	@Test
	public void testJson() {
		assertThat(
				new LexemeUpdateImpl(L1, 123, null, null, TermUpdate.EMPTY, StatementUpdate.EMPTY,
						Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
						Collections.emptyList(), Collections.emptyList(), Collections.emptyList()),
				producesJson("{}"));
		assertThat(LexemeUpdateBuilder.forEntityId(L1).setLanguage(Q1).build(), producesJson("{'language':'Q1'}"));
		assertThat(LexemeUpdateBuilder.forEntityId(L1).setLexicalCategory(Q2).build(),
				producesJson("{'lexicalCategory':'Q2'}"));
		assertThat(LexemeUpdateBuilder.forEntityId(L1).updateLemmas(LEMMAS).build(),
				producesJson("{'lemmas':" + toJson(LEMMAS) + "}"));
		assertThat(LexemeUpdateBuilder.forEntityId(L1).updateStatements(STATEMENTS).build(),
				producesJson("{'claims':" + toJson(STATEMENTS) + "}"));
		assertThat(LexemeUpdateBuilder.forEntityId(L1).addSense(ADDED_SENSE).build(),
				producesJson("{'senses':[{'add':''," + toJson(ADDED_SENSE).substring(1) + "]}"));
		assertThat(LexemeUpdateBuilder.forEntityId(L1).updateSense(UPDATED_SENSE).build(),
				producesJson("{'senses':[" + toJson(UPDATED_SENSE) + "]}"));
		assertThat(LexemeUpdateBuilder.forEntityId(L1).removeSense(S2).build(),
				producesJson("{'senses':[{'id':'L1-S2','remove':''}]}"));
		assertThat(LexemeUpdateBuilder.forEntityId(L1).addForm(ADDED_FORM).build(),
				producesJson("{'forms':[{'add':''," + toJson(ADDED_FORM).substring(1) + "]}"));
		assertThat(LexemeUpdateBuilder.forEntityId(L1).updateForm(UPDATED_FORM).build(),
				producesJson("{'forms':[" + toJson(UPDATED_FORM) + "]}"));
		assertThat(LexemeUpdateBuilder.forEntityId(L1).removeForm(F2).build(),
				producesJson("{'forms':[{'id':'L1-F2','remove':''}]}"));
	}

}
