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

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.AliasUpdate;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class PropertyUpdateImplTest {

	private static final PropertyIdValue P1 = Datamodel.makeWikidataPropertyIdValue("P1");
	private static final StatementUpdate STATEMENTS = LabeledDocumentUpdateImplTest.STATEMENTS;
	private static final TermUpdate LABELS = LabeledDocumentUpdateImplTest.LABELS;
	private static final TermUpdate DESCRIPTIONS = TermedDocumentUpdateImplTest.DESCRIPTIONS;
	private static final Map<String, AliasUpdate> ALIASES = TermedDocumentUpdateImplTest.ALIASES;

	@Test
	public void testFields() {
		PropertyUpdate update = new PropertyUpdateImpl(P1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS);
		assertEquals(P1, update.getEntityId());
		assertEquals(123, update.getBaseRevisionId());
		assertSame(LABELS, update.getLabels());
		assertSame(DESCRIPTIONS, update.getDescriptions());
		assertEquals(ALIASES, update.getAliases());
		assertSame(STATEMENTS, update.getStatements());
	}

	@Test
	public void testEmpty() {
		PropertyUpdate empty = new PropertyUpdateImpl(P1, 123, TermUpdate.EMPTY, TermUpdate.EMPTY,
				Collections.emptyMap(), StatementUpdate.EMPTY);
		assertTrue(empty.isEmpty());
		PropertyUpdate nonempty = new PropertyUpdateImpl(P1, 123, TermUpdate.EMPTY, DESCRIPTIONS,
				Collections.emptyMap(), StatementUpdate.EMPTY);
		assertFalse(nonempty.isEmpty());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void testEquality() {
		PropertyUpdate update = new PropertyUpdateImpl(P1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS);
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
		assertTrue(update.equals(update));
		assertTrue(update.equals(new PropertyUpdateImpl(P1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS)));
		assertFalse(update.equals(new PropertyUpdateImpl(P1, 123, LABELS, TermUpdate.EMPTY, ALIASES, STATEMENTS)));
	}

	@Test
	public void testHashCode() {
		assertEquals(
				new PropertyUpdateImpl(P1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS).hashCode(),
				new PropertyUpdateImpl(P1, 123, LABELS, DESCRIPTIONS, ALIASES, STATEMENTS).hashCode());
	}

}
