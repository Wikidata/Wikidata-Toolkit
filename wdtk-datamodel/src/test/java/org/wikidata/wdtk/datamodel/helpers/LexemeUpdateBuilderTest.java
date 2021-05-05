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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class LexemeUpdateBuilderTest {

	private static final LexemeIdValue L1 = EntityUpdateBuilderTest.L1;
	private static final Statement JOHN_HAS_BROWN_HAIR = StatementUpdateBuilderTest.JOHN_HAS_BROWN_HAIR;
	private static final Statement JOHN_HAS_BLUE_EYES = StatementUpdateBuilderTest.JOHN_HAS_BLUE_EYES;
	private static final MonolingualTextValue EN = TermUpdateBuilderTest.EN;
	private static final MonolingualTextValue SK = TermUpdateBuilderTest.SK;
	private static final ItemIdValue Q1 = EntityUpdateBuilderTest.Q1;
	private static final ItemIdValue Q2 = Datamodel.makeWikidataItemIdValue("Q2");
	private static final ItemIdValue Q3 = Datamodel.makeWikidataItemIdValue("Q3");
	private static final LexemeDocument LEXEME = Datamodel.makeLexemeDocument(L1, Q1, Q2, Arrays.asList(EN));

	@Test
	public void testForEntityId() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(null));
		assertThrows(IllegalArgumentException.class, () -> LexemeUpdateBuilder.forEntityId(LexemeIdValue.NULL));
		LexemeUpdateBuilder.forEntityId(L1);
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
				.updateStatements(StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build())
				.build();
		assertThat(update.getStatements().getAdded(), containsInAnyOrder(JOHN_HAS_BROWN_HAIR));
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

	@Test
	public void testMerge() {
		assertThrows(NullPointerException.class, () -> LexemeUpdateBuilder.forEntityId(L1).append(null));
		LexemeUpdate update = LexemeUpdateBuilder.forEntityId(L1)
				.updateStatements(StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build())
				.updateLemmas(TermUpdateBuilder.create().remove("en").build())
				.append(LexemeUpdateBuilder.forEntityId(L1)
						.updateStatements(StatementUpdateBuilder.create().add(JOHN_HAS_BLUE_EYES).build())
						.updateLemmas(TermUpdateBuilder.create().remove("sk").build())
						.build())
				.build();
		assertThat(update.getStatements().getAdded(),
				containsInAnyOrder(JOHN_HAS_BROWN_HAIR, JOHN_HAS_BLUE_EYES));
		assertThat(update.getLemmas().getRemoved(), containsInAnyOrder("en", "sk"));
	}

}
