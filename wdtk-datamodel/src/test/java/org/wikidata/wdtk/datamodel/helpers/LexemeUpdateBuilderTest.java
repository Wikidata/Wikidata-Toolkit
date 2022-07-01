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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class LexemeUpdateBuilderTest {

	private static final LexemeIdValue L1 = EntityUpdateBuilderTest.L1;
	private static final Statement L1_DESCRIBES_SOMETHING = StatementBuilder
			.forSubjectAndProperty(L1, Datamodel.makeWikidataPropertyIdValue("P1"))
			.withValue(Datamodel.makeStringValue("something"))
			.build();
	private static final Statement L1_EVOKES_FEELING = StatementBuilder
			.forSubjectAndProperty(L1, Datamodel.makeWikidataPropertyIdValue("P2"))
			.withValue(Datamodel.makeStringValue("feeling"))
			.build();
	private static final MonolingualTextValue EN = TermUpdateBuilderTest.EN;
	private static final MonolingualTextValue SK = TermUpdateBuilderTest.SK;
	private static final ItemIdValue Q1 = EntityUpdateBuilderTest.Q1;
	private static final ItemIdValue Q2 = Datamodel.makeWikidataItemIdValue("Q2");
	private static final ItemIdValue Q3 = Datamodel.makeWikidataItemIdValue("Q3");
	private static final LexemeDocument LEXEME = Datamodel.makeLexemeDocument(L1, Q1, Q2, Arrays.asList(EN));
	private static final PropertyIdValue INSTANCE_OF = Datamodel.makeWikidataPropertyIdValue("P31");
	private static final ItemIdValue OBSOLETE = Datamodel.makeWikidataItemIdValue("Q11");
	private static final ItemIdValue RARE = Datamodel.makeWikidataItemIdValue("Q12");

	@Test
	public void testForEntityId() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(null));
		assertThrows(IllegalArgumentException.class, () -> LexemeUpdateBuilder.forEntityId(LexemeIdValue.NULL));
		LexemeUpdateBuilder.forEntityId(L1);
	}

	@Test
	public void testForBaseRevisionId() {
		assertEquals(123, LexemeUpdateBuilder.forBaseRevisionId(L1, 123).getBaseRevisionId());
	}

	@Test
	public void testForBaseRevision() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forBaseRevision(null));
		assertThrows(IllegalArgumentException.class,
				() -> LexemeUpdateBuilder.forBaseRevision(LEXEME.withEntityId(LexemeIdValue.NULL)));
		LexemeUpdateBuilder.forBaseRevision(LEXEME);
	}

	@Test
	public void testStatementUpdate() {
		LexemeUpdate update = LexemeUpdateBuilder.forEntityId(L1)
				.updateStatements(StatementUpdateBuilder.create().add(L1_DESCRIBES_SOMETHING).build())
				.build();
		assertThat(update.getStatements().getAdded(), containsInAnyOrder(L1_DESCRIBES_SOMETHING));
	}

	@Test
	public void testLanguageChange() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(L1).setLanguage(null));
		assertThrows(IllegalArgumentException.class,
				() -> LexemeUpdateBuilder.forEntityId(L1).setLanguage(ItemIdValue.NULL));
		assertEquals(Q3, LexemeUpdateBuilder.forEntityId(L1).setLanguage(Q3).build().getLanguage().get());
		// different value
		assertEquals(Q3, LexemeUpdateBuilder.forBaseRevision(LEXEME).setLanguage(Q3).build().getLanguage().get());
		// same value
		assertFalse(LexemeUpdateBuilder.forBaseRevision(LEXEME).setLanguage(Q2).build().getLanguage().isPresent());
		// restore previous value
		assertFalse(LexemeUpdateBuilder.forBaseRevision(LEXEME)
				.setLanguage(Q3)
				.setLanguage(Q2)
				.build()
				.getLanguage().isPresent());
	}

	@Test
	public void testLexicalCategoryChange() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(L1).setLexicalCategory(null));
		assertThrows(IllegalArgumentException.class,
				() -> LexemeUpdateBuilder.forEntityId(L1).setLexicalCategory(ItemIdValue.NULL));
		assertEquals(Q3, LexemeUpdateBuilder.forEntityId(L1).setLexicalCategory(Q3).build().getLexicalCategory().get());
		// different value
		assertEquals(Q3, LexemeUpdateBuilder.forBaseRevision(LEXEME)
				.setLexicalCategory(Q3)
				.build()
				.getLexicalCategory().get());
		// same value
		assertFalse(LexemeUpdateBuilder.forBaseRevision(LEXEME)
				.setLexicalCategory(Q1)
				.build()
				.getLexicalCategory().isPresent());
		// restore previous value
		assertFalse(LexemeUpdateBuilder.forBaseRevision(LEXEME)
				.setLexicalCategory(Q3)
				.setLexicalCategory(Q1)
				.build()
				.getLexicalCategory().isPresent());
	}

	@Test
	public void testBlindLemmaUpdate() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(L1).updateLemmas(null));
		LexemeUpdate update = LexemeUpdateBuilder.forEntityId(L1)
				.updateLemmas(TermUpdateBuilder.create().remove("en").build())
				.updateLemmas(TermUpdateBuilder.create().remove("sk").build())
				.build();
		assertThat(update.getLemmas().getRemoved(), containsInAnyOrder("en", "sk"));
	}

	@Test
	public void testBaseLemmaUpdate() {
		LexemeUpdate update = LexemeUpdateBuilder
				.forBaseRevision(LEXEME
						.withLemma(EN)
						.withLemma(SK))
				.updateLemmas(TermUpdateBuilder.create()
						.put(SK) // ignored
						.remove("en") // checked
						.build())
				.build();
		assertThat(update.getLemmas().getModified(), is(anEmptyMap()));
		assertThat(update.getLemmas().getRemoved(), containsInAnyOrder("en"));
	}

	private static FormDocument form(String representation) {
		return Datamodel.makeFormDocument(
				FormIdValue.NULL,
				Arrays.asList(Datamodel.makeMonolingualTextValue(representation, "en")),
				Collections.emptyList(),
				Collections.emptyList());
	}

	private static FormIdValue formId(int id) {
		return Datamodel.makeWikidataFormIdValue("L1-F" + id);
	}

	private static FormDocument form(int id, String representation) {
		return form(representation).withEntityId(formId(id));
	}

	private static FormUpdate formUpdate(int id, String representation) {
		return FormUpdateBuilder.forEntityId(formId(id))
				.updateRepresentations(TermUpdateBuilder.create()
						.put(Datamodel.makeMonolingualTextValue(representation, "en"))
						.build())
				.build();
	}

	private static FormUpdate withBase(LexemeDocument base, FormUpdate update) {
		return FormUpdateBuilder.forBaseRevision(base.getForm(update.getEntityId())).append(update).build();
	}

	private static FormUpdate formUpdate(int id, ItemIdValue... classes) {
		FormIdValue formId = formId(id);
		StatementUpdateBuilder statements = StatementUpdateBuilder.create();
		for (ItemIdValue clazz : classes) {
			statements.add(StatementBuilder.forSubjectAndProperty(formId, INSTANCE_OF)
					.withValue(clazz)
					.build());
		}
		return FormUpdateBuilder.forEntityId(formId)
				.updateStatements(statements.build())
				.build();
	}

	@Test
	public void testFormAddition() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(L1).addForm(null));
		LexemeUpdate update = LexemeUpdateBuilder.forEntityId(L1)
				.addForm(form("swim")) // simple case
				.addForm(form("swim")) // duplicates allowed
				.addForm(form(2, "swimming")) // strip ID
				.addForm(form("swam").withRevisionId(123)) // strip revision ID
				.build();
		assertEquals(Arrays.asList(form("swim"), form("swim"), form("swimming"), form("swam")), update.getAddedForms());
	}

	@Test
	public void testBlindFormUpdate() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(L1).updateForm(null));
		// cannot update removed form
		assertThrows(IllegalStateException.class, () -> LexemeUpdateBuilder.forEntityId(L1)
				.removeForm(formId(1))
				.updateForm(formUpdate(1, RARE)));
		LexemeUpdate update = LexemeUpdateBuilder.forEntityId(L1)
				.updateForm(formUpdate(1, OBSOLETE)) // simple case
				.updateForm(formUpdate(2, RARE))
				.updateForm(formUpdate(2, OBSOLETE)) // merge updates
				.updateForm(formUpdate(3)) // empty update
				.build();
		assertThat(update.getRemovedForms(), is(empty()));
		assertThat(update.getUpdatedForms().keySet(), containsInAnyOrder(formId(1), formId(2)));
		assertEquals(formUpdate(1, OBSOLETE), update.getUpdatedForms().get(formId(1)));
		assertEquals(formUpdate(2, RARE, OBSOLETE), update.getUpdatedForms().get(formId(2)));
		// synchronize revision IDs
		assertEquals(123, LexemeUpdateBuilder.forBaseRevisionId(L1, 123)
				.updateForm(formUpdate(1, OBSOLETE))
				.build()
				.getUpdatedForms()
				.get(formId(1))
				.getBaseRevisionId());
	}

	@Test
	public void testBaseFormUpdate() {
		assertThrows(IllegalArgumentException.class,
				() -> LexemeUpdateBuilder.forBaseRevision(LEXEME).updateForm(formUpdate(99, RARE)));
		LexemeDocument base = LEXEME
				.withForm(form(1, "swim"))
				.withForm(form(2, "swims"))
				.withForm(form(3, "swimming"))
				.withForm(form(4, "swam"));
		LexemeUpdate update = LexemeUpdateBuilder.forBaseRevision(base)
				.updateForm(formUpdate(1, "swims")) // simple case
				.updateForm(formUpdate(2, "swims")) // replace with the same
				.updateForm(formUpdate(3, "swam"))
				.updateForm(formUpdate(3, "swimming")) // revert previous update
				.updateForm(formUpdate(4, RARE))
				.updateForm(formUpdate(4, OBSOLETE)) // merge updates
				.build();
		assertThat(update.getRemovedForms(), is(empty()));
		assertThat(update.getUpdatedForms().keySet(), containsInAnyOrder(formId(1), formId(4)));
		assertEquals(withBase(base, formUpdate(1, "swims")), update.getUpdatedForms().get(formId(1)));
		assertEquals(withBase(base, formUpdate(4, RARE, OBSOLETE)), update.getUpdatedForms().get(formId(4)));
	}

	@Test
	public void testBlindFormRemoval() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(L1).removeForm(null));
		assertThrows(IllegalArgumentException.class,
				() -> LexemeUpdateBuilder.forEntityId(L1).removeForm(FormIdValue.NULL));
		LexemeUpdate update = LexemeUpdateBuilder.forEntityId(L1)
				.updateForm(formUpdate(2, RARE))
				.removeForm(formId(1)) // simple case
				.removeForm(formId(2)) // previously updated
				.removeForm(formId(3))
				.removeForm(formId(3)) // duplicate removal allowed
				.build();
		assertThat(update.getRemovedForms(), containsInAnyOrder(formId(1), formId(2), formId(3)));
		assertThat(update.getUpdatedForms(), is(anEmptyMap()));
	}

	@Test
	public void testBaseFormRemoval() {
		assertThrows(IllegalArgumentException.class,
				() -> LexemeUpdateBuilder.forBaseRevision(LEXEME).removeForm(formId(1)));
		LexemeUpdate update = LexemeUpdateBuilder
				.forBaseRevision(LEXEME
						.withForm(form(1, "swim"))
						.withForm(form(2, "swims")))
				.updateForm(formUpdate(2, RARE))
				.removeForm(formId(1)) // simple case
				.removeForm(formId(2)) // previously updated
				.build();
		assertThat(update.getRemovedForms(), containsInAnyOrder(formId(1), formId(2)));
		assertThat(update.getUpdatedForms(), is(anEmptyMap()));
	}

	private static SenseDocument sense(String gloss) {
		return Datamodel.makeSenseDocument(
				SenseIdValue.NULL,
				Arrays.asList(Datamodel.makeMonolingualTextValue(gloss, "en")),
				Collections.emptyList());
	}

	private static SenseIdValue senseId(int id) {
		return Datamodel.makeWikidataSenseIdValue("L1-S" + id);
	}

	private static SenseDocument sense(int id, String gloss) {
		return sense(gloss).withEntityId(senseId(id));
	}

	private static SenseUpdate senseUpdate(int id, String gloss) {
		return SenseUpdateBuilder.forEntityId(senseId(id))
				.updateGlosses(TermUpdateBuilder.create()
						.put(Datamodel.makeMonolingualTextValue(gloss, "en"))
						.build())
				.build();
	}

	private static SenseUpdate withBase(LexemeDocument base, SenseUpdate update) {
		return SenseUpdateBuilder.forBaseRevision(base.getSense(update.getEntityId())).append(update).build();
	}

	private static SenseUpdate senseUpdate(int id, ItemIdValue... classes) {
		SenseIdValue senseId = senseId(id);
		StatementUpdateBuilder statements = StatementUpdateBuilder.create();
		for (ItemIdValue clazz : classes) {
			statements.add(StatementBuilder.forSubjectAndProperty(senseId, INSTANCE_OF)
					.withValue(clazz)
					.build());
		}
		return SenseUpdateBuilder.forEntityId(senseId)
				.updateStatements(statements.build())
				.build();
	}

	@Test
	public void testSenseAddition() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(L1).addSense(null));
		LexemeUpdate update = LexemeUpdateBuilder.forEntityId(L1)
				.addSense(sense("move")) // simple case
				.addSense(sense("move")) // duplicates allowed
				.addSense(sense(2, "immerse")) // strip ID
				.addSense(sense("float").withRevisionId(123)) // strip revision ID
				.build();
		assertEquals(
				Arrays.asList(sense("move"), sense("move"), sense("immerse"), sense("float")),
				update.getAddedSenses());
	}

	@Test
	public void testBlindSenseUpdate() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(L1).updateSense(null));
		// cannot update removed form
		assertThrows(IllegalStateException.class, () -> LexemeUpdateBuilder.forEntityId(L1)
				.removeSense(senseId(1))
				.updateSense(senseUpdate(1, RARE)));
		LexemeUpdate update = LexemeUpdateBuilder.forEntityId(L1)
				.updateSense(senseUpdate(1, OBSOLETE)) // simple case
				.updateSense(senseUpdate(2, RARE))
				.updateSense(senseUpdate(2, OBSOLETE)) // merge updates
				.updateSense(senseUpdate(3)) // empty update
				.build();
		assertThat(update.getRemovedSenses(), is(empty()));
		assertThat(update.getUpdatedSenses().keySet(), containsInAnyOrder(senseId(1), senseId(2)));
		assertEquals(senseUpdate(1, OBSOLETE), update.getUpdatedSenses().get(senseId(1)));
		assertEquals(senseUpdate(2, RARE, OBSOLETE), update.getUpdatedSenses().get(senseId(2)));
		// synchronize revision IDs
		assertEquals(123, LexemeUpdateBuilder.forBaseRevisionId(L1, 123)
				.updateSense(senseUpdate(1, OBSOLETE))
				.build()
				.getUpdatedSenses()
				.get(senseId(1))
				.getBaseRevisionId());
	}

	@Test
	public void testBaseSenseUpdate() {
		assertThrows(IllegalArgumentException.class,
				() -> LexemeUpdateBuilder.forBaseRevision(LEXEME).updateSense(senseUpdate(99, RARE)));
		LexemeDocument base = LEXEME
				.withSense(sense(1, "move"))
				.withSense(sense(2, "immerse"))
				.withSense(sense(3, "traverse"))
				.withSense(sense(4, "float"));
		LexemeUpdate update = LexemeUpdateBuilder.forBaseRevision(base)
				.updateSense(senseUpdate(1, "move in water")) // simple case
				.updateSense(senseUpdate(2, "immerse")) // replace with the same
				.updateSense(senseUpdate(3, "traversal"))
				.updateSense(senseUpdate(3, "traverse")) // revert previous update
				.updateSense(senseUpdate(4, RARE))
				.updateSense(senseUpdate(4, OBSOLETE)) // merge updates
				.build();
		assertThat(update.getRemovedSenses(), is(empty()));
		assertThat(update.getUpdatedSenses().keySet(), containsInAnyOrder(senseId(1), senseId(4)));
		assertEquals(withBase(base, senseUpdate(1, "move in water")), update.getUpdatedSenses().get(senseId(1)));
		assertEquals(withBase(base, senseUpdate(4, RARE, OBSOLETE)), update.getUpdatedSenses().get(senseId(4)));
	}

	@Test
	public void testBlindSenseRemoval() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(L1).removeSense(null));
		assertThrows(IllegalArgumentException.class,
				() -> LexemeUpdateBuilder.forEntityId(L1).removeSense(SenseIdValue.NULL));
		LexemeUpdate update = LexemeUpdateBuilder.forEntityId(L1)
				.updateSense(senseUpdate(2, RARE))
				.removeSense(senseId(1)) // simple case
				.removeSense(senseId(2)) // previously updated
				.removeSense(senseId(3))
				.removeSense(senseId(3)) // duplicate removal allowed
				.build();
		assertThat(update.getRemovedSenses(), containsInAnyOrder(senseId(1), senseId(2), senseId(3)));
		assertThat(update.getUpdatedSenses(), is(anEmptyMap()));
	}

	@Test
	public void testBaseSenseRemoval() {
		assertThrows(IllegalArgumentException.class,
				() -> LexemeUpdateBuilder.forBaseRevision(LEXEME).removeSense(senseId(1)));
		LexemeUpdate update = LexemeUpdateBuilder
				.forBaseRevision(LEXEME
						.withSense(sense(1, "move"))
						.withSense(sense(2, "float")))
				.updateSense(senseUpdate(2, RARE))
				.removeSense(senseId(1)) // simple case
				.removeSense(senseId(2)) // previously updated
				.build();
		assertThat(update.getRemovedSenses(), containsInAnyOrder(senseId(1), senseId(2)));
		assertThat(update.getUpdatedSenses(), is(anEmptyMap()));
	}

	@Test
	public void testMerge() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(L1).append(null));
		LexemeUpdate update = LexemeUpdateBuilder.forEntityId(L1)
				.updateStatements(StatementUpdateBuilder.create().add(L1_DESCRIBES_SOMETHING).build())
				.updateLemmas(TermUpdateBuilder.create().remove("en").build())
				.addForm(form("swim"))
				.updateForm(formUpdate(2, RARE))
				.removeForm(formId(3))
				.addSense(sense("move"))
				.updateSense(senseUpdate(2, RARE))
				.removeSense(senseId(3))
				.append(LexemeUpdateBuilder.forEntityId(L1)
						.updateStatements(StatementUpdateBuilder.create().add(L1_EVOKES_FEELING).build())
						.updateLemmas(TermUpdateBuilder.create().remove("sk").build())
						.addForm(form("swims"))
						.updateForm(formUpdate(2, OBSOLETE))
						.removeForm(formId(4))
						.addSense(sense("float"))
						.updateSense(senseUpdate(2, OBSOLETE))
						.removeSense(senseId(4))
						.build())
				.build();
		assertThat(update.getStatements().getAdded(),
				containsInAnyOrder(L1_DESCRIBES_SOMETHING, L1_EVOKES_FEELING));
		assertThat(update.getLemmas().getRemoved(), containsInAnyOrder("en", "sk"));
		assertEquals(Arrays.asList(form("swim"), form("swims")), update.getAddedForms());
		assertThat(update.getUpdatedForms().keySet(), containsInAnyOrder(formId(2)));
		assertEquals(formUpdate(2, RARE, OBSOLETE), update.getUpdatedForms().get(formId(2)));
		assertThat(update.getRemovedForms(), containsInAnyOrder(formId(3), formId(4)));
		assertEquals(Arrays.asList(sense("move"), sense("float")), update.getAddedSenses());
		assertThat(update.getUpdatedSenses().keySet(), containsInAnyOrder(senseId(2)));
		assertEquals(senseUpdate(2, RARE, OBSOLETE), update.getUpdatedSenses().get(senseId(2)));
		assertThat(update.getRemovedSenses(), containsInAnyOrder(senseId(3), senseId(4)));
	}

}
