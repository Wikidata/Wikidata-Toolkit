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
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class MediaInfoUpdateImplTest {

	private static final MediaInfoIdValue M1 = Datamodel.makeWikimediaCommonsMediaInfoIdValue("M1");
	private static final StatementUpdate STATEMENTS = LabeledDocumentUpdateImplTest.STATEMENTS;
	private static final TermUpdate LABELS = LabeledDocumentUpdateImplTest.LABELS;

	@Test
	public void testFields() {
		MediaInfoUpdate update = new MediaInfoUpdateImpl(M1, 123, LABELS, STATEMENTS);
		assertEquals(M1, update.getEntityId());
		assertEquals(123, update.getBaseRevisionId());
		assertSame(STATEMENTS, update.getStatements());
		assertSame(LABELS, update.getLabels());
	}

	@Test
	public void testEmpty() {
		assertTrue(new MediaInfoUpdateImpl(M1, 123, TermUpdate.EMPTY, StatementUpdate.EMPTY).isEmpty());
		assertFalse(new MediaInfoUpdateImpl(M1, 123, LABELS, StatementUpdate.EMPTY).isEmpty());
	}

	@Test
	public void testEquality() {
		MediaInfoUpdate update = new MediaInfoUpdateImpl(M1, 123, LABELS, STATEMENTS);
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
		assertTrue(update.equals(update));
		assertTrue(update.equals(new MediaInfoUpdateImpl(M1, 123, LABELS, STATEMENTS)));
		assertFalse(update.equals(new MediaInfoUpdateImpl(M1, 123, TermUpdate.EMPTY, STATEMENTS)));
	}

	@Test
	public void testHashCode() {
		assertEquals(
				new MediaInfoUpdateImpl(M1, 123, LABELS, STATEMENTS).hashCode(),
				new MediaInfoUpdateImpl(M1, 123, LABELS, STATEMENTS).hashCode());
	}

}
