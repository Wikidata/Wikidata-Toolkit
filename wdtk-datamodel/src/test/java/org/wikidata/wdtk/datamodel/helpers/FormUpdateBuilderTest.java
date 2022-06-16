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
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class FormUpdateBuilderTest {

	private static final FormIdValue F1 = EntityUpdateBuilderTest.F1;
	private static final FormDocument FORM = EntityUpdateBuilderTest.FORM;
	private static final Statement F1_DESCRIBES_SOMETHING = StatementBuilder
			.forSubjectAndProperty(F1, Datamodel.makeWikidataPropertyIdValue("P1"))
			.withValue(Datamodel.makeStringValue("something"))
			.build();
	private static final Statement F1_EVOKES_FEELING = StatementBuilder
			.forSubjectAndProperty(F1, Datamodel.makeWikidataPropertyIdValue("P2"))
			.withValue(Datamodel.makeStringValue("feeling"))
			.build();
	private static final MonolingualTextValue EN = TermUpdateBuilderTest.EN;
	private static final MonolingualTextValue SK = TermUpdateBuilderTest.SK;
	private static final ItemIdValue Q1 = EntityUpdateBuilderTest.Q1;
	private static final ItemIdValue Q2 = Datamodel.makeWikidataItemIdValue("Q2");
	private static final ItemIdValue Q3 = Datamodel.makeWikidataItemIdValue("Q3");

	@Test
	public void testForEntityId() {
		assertThrows(NullPointerException.class, () -> FormUpdateBuilder.forEntityId(null));
		assertThrows(IllegalArgumentException.class, () -> FormUpdateBuilder.forEntityId(FormIdValue.NULL));
		FormUpdateBuilder.forEntityId(F1);
	}

	@Test
	public void testForBaseRevisionId() {
		assertEquals(123, FormUpdateBuilder.forBaseRevisionId(F1, 123).getBaseRevisionId());
	}

	@Test
	public void testForBaseRevision() {
		assertThrows(NullPointerException.class, () -> FormUpdateBuilder.forBaseRevision(null));
		assertThrows(IllegalArgumentException.class,
				() -> FormUpdateBuilder.forBaseRevision(FORM.withEntityId(FormIdValue.NULL)));
		FormUpdateBuilder.forBaseRevision(FORM);
	}

	@Test
	public void testStatementUpdate() {
		FormUpdate update = FormUpdateBuilder.forEntityId(F1)
				.updateStatements(StatementUpdateBuilder.create().add(F1_DESCRIBES_SOMETHING).build())
				.build();
		assertThat(update.getStatements().getAdded(), containsInAnyOrder(F1_DESCRIBES_SOMETHING));
	}

	@Test
	public void testBlindRepresentationUpdate() {
		assertThrows(NullPointerException.class, () -> FormUpdateBuilder.forEntityId(F1).updateRepresentations(null));
		FormUpdate update = FormUpdateBuilder.forEntityId(F1)
				.updateRepresentations(TermUpdateBuilder.create().remove("en").build())
				.updateRepresentations(TermUpdateBuilder.create().remove("sk").build())
				.build();
		assertThat(update.getRepresentations().getRemoved(), containsInAnyOrder("en", "sk"));
	}

	@Test
	public void testBaseRepresentationUpdate() {
		FormUpdate update = FormUpdateBuilder
				.forBaseRevision(FORM
						.withRepresentation(EN)
						.withRepresentation(SK))
				.updateRepresentations(TermUpdateBuilder.create()
						.put(SK) // ignored
						.remove("en") // checked
						.build())
				.build();
		assertThat(update.getRepresentations().getModified(), is(anEmptyMap()));
		assertThat(update.getRepresentations().getRemoved(), containsInAnyOrder("en"));
	}

	@Test
	public void testBlindFeatureChange() {
		FormUpdateBuilder builder = FormUpdateBuilder.forEntityId(F1);
		assertThrows(NullPointerException.class, () -> builder.setGrammaticalFeatures(null));
		assertThrows(NullPointerException.class, () -> builder.setGrammaticalFeatures(Arrays.asList(Q1, null)));
		assertThrows(IllegalArgumentException.class,
				() -> builder.setGrammaticalFeatures(Arrays.asList(ItemIdValue.NULL)));
		assertThrows(IllegalArgumentException.class, () -> builder.setGrammaticalFeatures(Arrays.asList(Q1, Q1)));
		assertFalse(builder.build().getGrammaticalFeatures().isPresent());
		FormUpdate update = builder.setGrammaticalFeatures(Arrays.asList(Q1, Q2)).build();
		assertThat(update.getGrammaticalFeatures().get(), containsInAnyOrder(Q1, Q2));
	}

	@Test
	public void testBaseFeatureChange() {
		FormDocument base = FORM
				.withGrammaticalFeature(Q1)
				.withGrammaticalFeature(Q2);
		assertFalse(FormUpdateBuilder.forBaseRevision(base).build().getGrammaticalFeatures().isPresent());
		assertFalse(FormUpdateBuilder.forBaseRevision(base)
				.setGrammaticalFeatures(Arrays.asList(Q1, Q2))
				.build()
				.getGrammaticalFeatures().isPresent());
		FormUpdate update = FormUpdateBuilder.forBaseRevision(base)
				.setGrammaticalFeatures(Arrays.asList(Q2, Q3))
				.build();
		assertThat(update.getGrammaticalFeatures().get(), containsInAnyOrder(Q2, Q3));
	}

	@Test
	public void testMerge() {
		assertThrows(NullPointerException.class, () -> FormUpdateBuilder.forEntityId(F1).append(null));
		FormUpdate update = FormUpdateBuilder.forEntityId(F1)
				.updateStatements(StatementUpdateBuilder.create().add(F1_DESCRIBES_SOMETHING).build())
				.updateRepresentations(TermUpdateBuilder.create().remove("en").build())
				.append(FormUpdateBuilder.forEntityId(F1)
						.updateStatements(StatementUpdateBuilder.create().add(F1_EVOKES_FEELING).build())
						.updateRepresentations(TermUpdateBuilder.create().remove("sk").build())
						.build())
				.build();
		assertThat(update.getStatements().getAdded(),
				containsInAnyOrder(F1_DESCRIBES_SOMETHING, F1_EVOKES_FEELING));
		assertThat(update.getRepresentations().getRemoved(), containsInAnyOrder("en", "sk"));
	}

}
