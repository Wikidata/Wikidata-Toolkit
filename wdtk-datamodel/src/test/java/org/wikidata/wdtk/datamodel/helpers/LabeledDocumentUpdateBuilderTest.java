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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LabeledDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.LabeledStatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class LabeledDocumentUpdateBuilderTest {

	private static final ItemIdValue Q1 = EntityUpdateBuilderTest.Q1;
	private static final PropertyIdValue P1 = EntityUpdateBuilderTest.P1;
	private static final MediaInfoIdValue M1 = EntityUpdateBuilderTest.M1;
	private static final LexemeIdValue L1 = EntityUpdateBuilderTest.L1;
	private static final ItemDocument ITEM = EntityUpdateBuilderTest.ITEM;
	private static final PropertyDocument PROPERTY = EntityUpdateBuilderTest.PROPERTY;
	private static final MediaInfoDocument MEDIA = EntityUpdateBuilderTest.MEDIA;
	private static final Statement JOHN_HAS_BROWN_HAIR = StatementUpdateBuilderTest.JOHN_HAS_BROWN_HAIR;
	private static final Statement JOHN_HAS_BLUE_EYES = StatementUpdateBuilderTest.JOHN_HAS_BLUE_EYES;
	private static final MonolingualTextValue EN = TermUpdateBuilderTest.EN;
	private static final MonolingualTextValue SK = TermUpdateBuilderTest.SK;

	@Test
	public void testForEntityId() {
		assertThrows(NullPointerException.class, () -> LabeledDocumentUpdateBuilder.forEntityId(null));
		assertThrows(IllegalArgumentException.class, () -> LabeledDocumentUpdateBuilder.forEntityId(ItemIdValue.NULL));
		assertThrows(IllegalArgumentException.class, () -> LabeledDocumentUpdateBuilder.forEntityId(L1));
		assertThat(LabeledDocumentUpdateBuilder.forEntityId(Q1), is(instanceOf(ItemUpdateBuilder.class)));
		assertThat(LabeledDocumentUpdateBuilder.forEntityId(P1), is(instanceOf(PropertyUpdateBuilder.class)));
		assertThat(LabeledDocumentUpdateBuilder.forEntityId(M1), is(instanceOf(MediaInfoUpdateBuilder.class)));
	}

	@Test
	public void testForBaseRevisionId() {
		assertEquals(123, LabeledDocumentUpdateBuilder.forBaseRevisionId(Q1, 123).getBaseRevisionId());
		assertEquals(123, LabeledDocumentUpdateBuilder.forBaseRevisionId(P1, 123).getBaseRevisionId());
		assertEquals(123, LabeledDocumentUpdateBuilder.forBaseRevisionId(M1, 123).getBaseRevisionId());
	}

	@Test
	public void testForBaseRevision() {
		assertThrows(NullPointerException.class, () -> LabeledDocumentUpdateBuilder.forBaseRevision(null));
		assertThrows(IllegalArgumentException.class,
				() -> LabeledDocumentUpdateBuilder.forBaseRevision(Datamodel.makeItemDocument(ItemIdValue.NULL)));
		assertThat(LabeledDocumentUpdateBuilder.forBaseRevision(ITEM), is(instanceOf(ItemUpdateBuilder.class)));
		assertThat(LabeledDocumentUpdateBuilder.forBaseRevision(PROPERTY), is(instanceOf(PropertyUpdateBuilder.class)));
		assertThat(LabeledDocumentUpdateBuilder.forBaseRevision(MEDIA), is(instanceOf(MediaInfoUpdateBuilder.class)));
	}

	@Test
	public void testStatementUpdate() {
		LabeledStatementDocumentUpdate update = LabeledDocumentUpdateBuilder.forEntityId(Q1)
				.updateStatements(StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build())
				.build();
		assertThat(update.getStatements().getAdded(), containsInAnyOrder(JOHN_HAS_BROWN_HAIR));
	}

	@Test
	public void testBlindLabelUpdate() {
		assertThrows(NullPointerException.class, () -> LabeledDocumentUpdateBuilder.forEntityId(Q1).updateLabels(null));
		LabeledDocumentUpdate update = LabeledDocumentUpdateBuilder.forEntityId(Q1)
				.updateLabels(TermUpdateBuilder.create().remove("en").build())
				.updateLabels(TermUpdateBuilder.create().remove("sk").build())
				.build();
		assertThat(update.getLabels().getRemoved(), containsInAnyOrder("en", "sk"));
	}

	@Test
	public void testBaseLabelUpdate() {
		LabeledDocumentUpdate update = LabeledDocumentUpdateBuilder
				.forBaseRevision(ITEM
						.withLabel(EN)
						.withLabel(SK))
				.updateLabels(TermUpdateBuilder.create()
						.put(SK) // ignored
						.remove("en") // checked
						.build())
				.build();
		assertThat(update.getLabels().getModified(), is(anEmptyMap()));
		assertThat(update.getLabels().getRemoved(), containsInAnyOrder("en"));
	}

	@Test
	public void testMerge() {
		assertThrows(NullPointerException.class, () -> LabeledDocumentUpdateBuilder.forEntityId(Q1).append(null));
		LabeledDocumentUpdateBuilder builder = LabeledDocumentUpdateBuilder.forEntityId(Q1)
				.updateStatements(StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build())
				.updateLabels(TermUpdateBuilder.create().remove("en").build());
		builder.append(LabeledDocumentUpdateBuilder.forEntityId(Q1)
				.updateStatements(StatementUpdateBuilder.create().add(JOHN_HAS_BLUE_EYES).build())
				.updateLabels(TermUpdateBuilder.create().remove("sk").build())
				.build());
		LabeledStatementDocumentUpdate update = builder.build();
		assertThat(update.getStatements().getAdded(),
				containsInAnyOrder(JOHN_HAS_BROWN_HAIR, JOHN_HAS_BLUE_EYES));
		assertThat(update.getLabels().getRemoved(), containsInAnyOrder("en", "sk"));
	}

}
