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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.helpers.StatementUpdateBuilder;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class StatementDocumentUpdateImplTest {

	private static final ItemIdValue JOHN = StatementUpdateImplTest.JOHN;
	private static final Statement JOHN_HAS_BROWN_HAIR = StatementUpdateImplTest.JOHN_HAS_BROWN_HAIR;
	private static final Collection<SiteLink> NO_SITELINKS = Collections.emptyList();
	private static final Collection<String> NO_REMOVED_SITELINKS = Collections.emptyList();

	private static StatementDocumentUpdate create(ItemIdValue entityId, long revisionId, StatementUpdate statements) {
		return new ItemUpdateImpl(entityId, revisionId, TermUpdate.NULL, TermUpdate.NULL, Collections.emptyMap(),
				statements, NO_SITELINKS, NO_REMOVED_SITELINKS);
	}

	@Test
	public void testFields() {
		StatementUpdate statements = StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build();
		StatementDocumentUpdate update = create(JOHN, 123, statements);
		assertEquals(JOHN, update.getEntityId());
		assertEquals(123, update.getBaseRevisionId());
		assertSame(statements, update.getStatements());
	}

	@Test
	public void testValidation() {
		assertThrows(NullPointerException.class, () -> create(JOHN, 0, null));
	}

	@Test
	public void testEmpty() {
		StatementUpdate statements = StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build();
		assertFalse(create(JOHN, 0, statements).isEmpty());
		assertTrue(create(JOHN, 0, StatementUpdate.NULL).isEmpty());
	}

	@Test
	public void testEquality() {
		StatementDocumentUpdate update = create(JOHN, 0,
				StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build());
		assertTrue(update.equals(update));
		assertTrue(update.equals(create(JOHN, 0,
				StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build())));
		assertFalse(update.equals(create(JOHN, 123,
				StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build())));
		assertFalse(update.equals(create(JOHN, 0, StatementUpdate.NULL)));
	}

	@Test
	public void testHashCode() {
		StatementDocumentUpdate update1 = create(JOHN, 123,
				StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build());
		StatementDocumentUpdate update2 = create(JOHN, 123,
				StatementUpdateBuilder.create().add(JOHN_HAS_BROWN_HAIR).build());
		assertEquals(update1.hashCode(), update2.hashCode());
	}

}
