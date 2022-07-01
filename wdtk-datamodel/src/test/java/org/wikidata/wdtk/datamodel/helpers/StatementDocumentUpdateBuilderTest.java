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
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocumentUpdate;

public class StatementDocumentUpdateBuilderTest {

	private static final ItemIdValue Q1 = EntityUpdateBuilderTest.Q1;
	private static final PropertyIdValue P1 = EntityUpdateBuilderTest.P1;
	private static final MediaInfoIdValue M1 = EntityUpdateBuilderTest.M1;
	private static final LexemeIdValue L1 = EntityUpdateBuilderTest.L1;
	private static final FormIdValue F1 = EntityUpdateBuilderTest.F1;
	private static final SenseIdValue S1 = EntityUpdateBuilderTest.S1;
	private static final ItemDocument ITEM = EntityUpdateBuilderTest.ITEM;
	private static final PropertyDocument PROPERTY = EntityUpdateBuilderTest.PROPERTY;
	private static final MediaInfoDocument MEDIA = EntityUpdateBuilderTest.MEDIA;
	private static final LexemeDocument LEXEME = EntityUpdateBuilderTest.LEXEME;
	private static final FormDocument FORM = EntityUpdateBuilderTest.FORM;
	private static final SenseDocument SENSE = EntityUpdateBuilderTest.SENSE;
	private static final EntityIdValue JOHN = StatementUpdateBuilderTest.JOHN;
	private static final EntityIdValue RITA = StatementUpdateBuilderTest.RITA;
	private static final Statement JOHN_ALREADY_HAS_BROWN_HAIR = StatementUpdateBuilderTest.JOHN_ALREADY_HAS_BROWN_HAIR;
	private static final Statement JOHN_ALREADY_HAS_BLUE_EYES = StatementUpdateBuilderTest.JOHN_ALREADY_HAS_BLUE_EYES;

	@Test
	public void testForEntityId() {
		assertThrows(NullPointerException.class, () -> StatementDocumentUpdateBuilder.forEntityId(null));
		assertThrows(IllegalArgumentException.class,
				() -> StatementDocumentUpdateBuilder.forEntityId(ItemIdValue.NULL));
		assertThat(StatementDocumentUpdateBuilder.forEntityId(Q1), is(instanceOf(ItemUpdateBuilder.class)));
		assertThat(StatementDocumentUpdateBuilder.forEntityId(P1), is(instanceOf(PropertyUpdateBuilder.class)));
		assertThat(StatementDocumentUpdateBuilder.forEntityId(M1), is(instanceOf(MediaInfoUpdateBuilder.class)));
		assertThat(StatementDocumentUpdateBuilder.forEntityId(L1), is(instanceOf(LexemeUpdateBuilder.class)));
		assertThat(StatementDocumentUpdateBuilder.forEntityId(F1), is(instanceOf(FormUpdateBuilder.class)));
		assertThat(StatementDocumentUpdateBuilder.forEntityId(S1), is(instanceOf(SenseUpdateBuilder.class)));
	}

	@Test
	public void testForBaseRevisionId() {
		assertEquals(123, StatementDocumentUpdateBuilder.forBaseRevisionId(Q1, 123).getBaseRevisionId());
		assertEquals(123, StatementDocumentUpdateBuilder.forBaseRevisionId(P1, 123).getBaseRevisionId());
		assertEquals(123, StatementDocumentUpdateBuilder.forBaseRevisionId(M1, 123).getBaseRevisionId());
		assertEquals(123, StatementDocumentUpdateBuilder.forBaseRevisionId(L1, 123).getBaseRevisionId());
		assertEquals(123, StatementDocumentUpdateBuilder.forBaseRevisionId(F1, 123).getBaseRevisionId());
		assertEquals(123, StatementDocumentUpdateBuilder.forBaseRevisionId(S1, 123).getBaseRevisionId());
	}

	@Test
	public void testForBaseRevision() {
		assertThrows(NullPointerException.class, () -> StatementDocumentUpdateBuilder.forBaseRevision(null));
		assertThrows(IllegalArgumentException.class,
				() -> StatementDocumentUpdateBuilder.forBaseRevision(Datamodel.makeItemDocument(ItemIdValue.NULL)));
		assertThat(StatementDocumentUpdateBuilder.forBaseRevision(ITEM), is(instanceOf(ItemUpdateBuilder.class)));
		assertThat(StatementDocumentUpdateBuilder.forBaseRevision(PROPERTY),
				is(instanceOf(PropertyUpdateBuilder.class)));
		assertThat(StatementDocumentUpdateBuilder.forBaseRevision(MEDIA), is(instanceOf(MediaInfoUpdateBuilder.class)));
		assertThat(StatementDocumentUpdateBuilder.forBaseRevision(LEXEME), is(instanceOf(LexemeUpdateBuilder.class)));
		assertThat(StatementDocumentUpdateBuilder.forBaseRevision(FORM), is(instanceOf(FormUpdateBuilder.class)));
		assertThat(StatementDocumentUpdateBuilder.forBaseRevision(SENSE), is(instanceOf(SenseUpdateBuilder.class)));
	}

	@Test
	public void testBlindStatementUpdate() {
		assertThrows(NullPointerException.class,
				() -> StatementDocumentUpdateBuilder.forEntityId(Q1).updateStatements(null));
		assertThrows(IllegalArgumentException.class, () -> StatementDocumentUpdateBuilder.forEntityId(RITA)
				.updateStatements(StatementUpdateBuilder.create().replace(JOHN_ALREADY_HAS_BROWN_HAIR).build()));
		StatementDocumentUpdate update = StatementDocumentUpdateBuilder.forEntityId(JOHN)
				.updateStatements(StatementUpdateBuilder.create().replace(JOHN_ALREADY_HAS_BROWN_HAIR).build())
				.updateStatements(StatementUpdateBuilder.create().replace(JOHN_ALREADY_HAS_BLUE_EYES).build())
				.build();
		assertThat(update.getStatements().getReplaced().values(),
				containsInAnyOrder(JOHN_ALREADY_HAS_BROWN_HAIR, JOHN_ALREADY_HAS_BLUE_EYES));
	}

	@Test
	public void testBaseStatementUpdate() {
		StatementDocumentUpdate update = StatementDocumentUpdateBuilder
				.forBaseRevision(ITEM
						.withStatement(JOHN_ALREADY_HAS_BROWN_HAIR)
						.withStatement(JOHN_ALREADY_HAS_BLUE_EYES))
				.updateStatements(StatementUpdateBuilder.create()
						.replace(JOHN_ALREADY_HAS_BROWN_HAIR) // ignored
						.remove(JOHN_ALREADY_HAS_BLUE_EYES.getStatementId()) // checked
						.build())
				.build();
		assertThat(update.getStatements().getReplaced(), is(anEmptyMap()));
		assertThat(update.getStatements().getRemoved(),
				containsInAnyOrder(JOHN_ALREADY_HAS_BLUE_EYES.getStatementId()));
	}

	@Test
	public void testMerge() {
		assertThrows(NullPointerException.class, () -> StatementDocumentUpdateBuilder.forEntityId(Q1).append(null));
		StatementDocumentUpdateBuilder builder = StatementDocumentUpdateBuilder.forEntityId(JOHN)
				.updateStatements(StatementUpdateBuilder.create().replace(JOHN_ALREADY_HAS_BROWN_HAIR).build());
		builder.append(StatementDocumentUpdateBuilder.forEntityId(JOHN)
				.updateStatements(StatementUpdateBuilder.create().replace(JOHN_ALREADY_HAS_BLUE_EYES).build())
				.build());
		StatementDocumentUpdate update = builder.build();
		assertThat(update.getStatements().getReplaced().values(),
				containsInAnyOrder(JOHN_ALREADY_HAS_BROWN_HAIR, JOHN_ALREADY_HAS_BLUE_EYES));
	}

}
