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
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class StatementDocumentUpdateImplTest {

	private static final ItemIdValue JOHN = StatementUpdateImplTest.JOHN;
	static final StatementUpdate STATEMENTS = StatementUpdateBuilder.create().remove("ID123").build();
	private static final Collection<SiteLink> NO_SITELINKS = Collections.emptyList();
	private static final Collection<String> NO_REMOVED_SITELINKS = Collections.emptyList();

	private static StatementDocumentUpdate create(ItemIdValue entityId, long revisionId, StatementUpdate statements) {
		return new ItemUpdateImpl(entityId, revisionId, TermUpdate.EMPTY, TermUpdate.EMPTY, Collections.emptyMap(),
				statements, NO_SITELINKS, NO_REMOVED_SITELINKS);
	}

	@Test
	public void testFields() {
		StatementDocumentUpdate update = create(JOHN, 123, STATEMENTS);
		assertEquals(JOHN, update.getEntityId());
		assertEquals(123, update.getBaseRevisionId());
		assertSame(STATEMENTS, update.getStatements());
	}

	@Test
	public void testValidation() {
		assertThrows(NullPointerException.class, () -> create(JOHN, 0, null));
		assertThrows(IllegalArgumentException.class, () -> create(JOHN, 0,
				StatementUpdateBuilder.create().add(StatementUpdateImplTest.RITA_HAS_BROWN_HAIR).build()));
		assertThrows(IllegalArgumentException.class,
				() -> create(JOHN, 0, StatementUpdateBuilder.create()
						.replace(StatementUpdateImplTest.RITA_HAS_BROWN_HAIR.withStatementId("ID99")).build()));
	}

	@Test
	public void testEmpty() {
		assertFalse(create(JOHN, 0, STATEMENTS).isEmpty());
		assertTrue(create(JOHN, 0, StatementUpdate.EMPTY).isEmpty());
	}

	@Test
	public void testEquality() {
		StatementDocumentUpdate update = create(JOHN, 0, STATEMENTS);
		assertTrue(update.equals(update));
		assertTrue(update.equals(create(JOHN, 0, StatementUpdateBuilder.create().remove("ID123").build())));
		assertFalse(update.equals(create(JOHN, 123, STATEMENTS)));
		assertFalse(update.equals(create(JOHN, 0, StatementUpdate.EMPTY)));
	}

	@Test
	public void testHashCode() {
		StatementDocumentUpdate update1 = create(JOHN, 123, STATEMENTS);
		StatementDocumentUpdate update2 = create(JOHN, 123, StatementUpdateBuilder.create().remove("ID123").build());
		assertEquals(update1.hashCode(), update2.hashCode());
	}

}
