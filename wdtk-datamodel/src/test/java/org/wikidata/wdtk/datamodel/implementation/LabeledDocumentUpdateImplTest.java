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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.StatementUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.TermUpdateBuilder;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LabeledStatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class LabeledDocumentUpdateImplTest {

	static final ItemIdValue JOHN = StatementUpdateImplTest.JOHN;
	private static final Statement JOHN_HAS_BROWN_HAIR = StatementUpdateImplTest.JOHN_HAS_BROWN_HAIR;
	private static final Collection<SiteLink> NO_SITELINKS = Collections.emptyList();
	private static final Collection<String> NO_REMOVED_SITELINKS = Collections.emptyList();
	private static final StatementUpdate STATEMENTS = StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build();
	static final TermUpdate LABELS = TermUpdateBuilder.create().remove("en").build();

	private static LabeledStatementDocumentUpdate create(
			ItemIdValue entityId, long revisionId, StatementUpdate statements, TermUpdate labels) {
		return new ItemUpdateImpl(entityId, revisionId, labels, TermUpdate.EMPTY, Collections.emptyMap(),
				statements, NO_SITELINKS, NO_REMOVED_SITELINKS);
	}

	@Test
	public void testFields() {
		LabeledStatementDocumentUpdate update = create(JOHN, 123, STATEMENTS, LABELS);
		assertEquals(JOHN, update.getEntityId());
		assertEquals(123, update.getBaseRevisionId());
		assertSame(STATEMENTS, update.getStatements());
		assertSame(LABELS, update.getLabels());
	}

	@Test
	public void testValidation() {
		assertThrows(NullPointerException.class, () -> create(JOHN, 0, StatementUpdate.EMPTY, null));
	}

	@Test
	public void testEmpty() {
		assertFalse(create(JOHN, 0, STATEMENTS, TermUpdate.EMPTY).isEmpty());
		assertFalse(create(JOHN, 0, StatementUpdate.EMPTY, LABELS).isEmpty());
		assertTrue(create(JOHN, 0, StatementUpdate.EMPTY, TermUpdate.EMPTY).isEmpty());
	}

	@Test
	public void testEquality() {
		LabeledStatementDocumentUpdate update = create(JOHN, 0, STATEMENTS, LABELS);
		assertTrue(update.equals(update));
		assertTrue(update.equals(create(JOHN, 0, STATEMENTS, TermUpdateBuilder.create().remove("en").build())));
		assertFalse(update.equals(create(JOHN, 123, StatementUpdate.EMPTY, LABELS)));
		assertFalse(update.equals(create(JOHN, 123, STATEMENTS, TermUpdate.EMPTY)));
	}

	@Test
	public void testHashCode() {
		LabeledStatementDocumentUpdate update1 = create(JOHN, 123, STATEMENTS, LABELS);
		LabeledStatementDocumentUpdate update2 = create(JOHN, 123, STATEMENTS,
				TermUpdateBuilder.create().remove("en").build());
		assertEquals(update1.hashCode(), update2.hashCode());
	}

}
