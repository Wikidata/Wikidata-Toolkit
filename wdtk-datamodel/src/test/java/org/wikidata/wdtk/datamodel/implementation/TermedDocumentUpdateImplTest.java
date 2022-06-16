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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anEmptyMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.AliasUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.TermUpdateBuilder;
import org.wikidata.wdtk.datamodel.interfaces.AliasUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocumentUpdate;

public class TermedDocumentUpdateImplTest {

	private static final ItemIdValue JOHN = StatementUpdateImplTest.JOHN;
	private static final StatementUpdate STATEMENTS = StatementDocumentUpdateImplTest.STATEMENTS;
	private static final MonolingualTextValue EN = Datamodel.makeMonolingualTextValue("hello", "en");
	private static final MonolingualTextValue SK = Datamodel.makeMonolingualTextValue("ahoj", "sk");
	private static final TermUpdate LABELS = TermUpdateBuilder.create().remove("de").build();
	static final TermUpdate DESCRIPTIONS = TermUpdateBuilder.create().remove("en").build();
	static final AliasUpdate ALIAS = AliasUpdateBuilder.create().add(EN).build();
	static final Map<String, AliasUpdate> ALIASES = new HashMap<>();

	static {
		ALIASES.put("en", ALIAS);
	}

	private static TermedStatementDocumentUpdate create(
			ItemIdValue entityId,
			long revisionId,
			StatementUpdate statements,
			TermUpdate labels,
			TermUpdate descriptions,
			Map<String, AliasUpdate> aliases) {
		return new ItemUpdateImpl(entityId, revisionId, labels, descriptions, aliases, statements,
				Collections.emptyList(), Collections.emptyList());
	}

	@Test
	public void testFields() {
		TermedStatementDocumentUpdate update = create(JOHN, 123, STATEMENTS, LABELS, DESCRIPTIONS, ALIASES);
		assertEquals(JOHN, update.getEntityId());
		assertEquals(123, update.getBaseRevisionId());
		assertSame(STATEMENTS, update.getStatements());
		assertSame(LABELS, update.getLabels());
		assertSame(DESCRIPTIONS, update.getDescriptions());
		assertEquals(ALIASES, update.getAliases());
	}

	@Test
	public void testValidation() {
		assertThrows(NullPointerException.class,
				() -> create(JOHN, 123, StatementUpdate.EMPTY, TermUpdate.EMPTY, null, ALIASES));
		assertThrows(NullPointerException.class,
				() -> create(JOHN, 123, StatementUpdate.EMPTY, TermUpdate.EMPTY, TermUpdate.EMPTY, null));
		Map<String, AliasUpdate> aliases = new HashMap<>();
		aliases.put(null, AliasUpdate.EMPTY);
		assertThrows(NullPointerException.class,
				() -> create(JOHN, 123, StatementUpdate.EMPTY, TermUpdate.EMPTY, TermUpdate.EMPTY, aliases));
		aliases.clear();
		aliases.put("en", null);
		assertThrows(NullPointerException.class,
				() -> create(JOHN, 123, StatementUpdate.EMPTY, TermUpdate.EMPTY, TermUpdate.EMPTY, aliases));
		aliases.clear();
		aliases.put(" ", AliasUpdate.EMPTY);
		assertThrows(IllegalArgumentException.class,
				() -> create(JOHN, 123, StatementUpdate.EMPTY, TermUpdate.EMPTY, TermUpdate.EMPTY, aliases));
		aliases.clear();
		aliases.put("de", AliasUpdateBuilder.create().add(EN).build());
		assertThrows(IllegalArgumentException.class,
				() -> create(JOHN, 123, StatementUpdate.EMPTY, TermUpdate.EMPTY, TermUpdate.EMPTY, aliases));
		aliases.clear();
		aliases.put("en", AliasUpdate.EMPTY);
		assertThat(
				create(JOHN, 123, StatementUpdate.EMPTY, TermUpdate.EMPTY, TermUpdate.EMPTY, aliases).getAliases(),
				is(anEmptyMap()));
	}

	@Test
	public void testImmutability() {
		Map<String, AliasUpdate> aliases = new HashMap<>();
		aliases.put("en", AliasUpdateBuilder.create().add(EN).build());
		TermedStatementDocumentUpdate update = create(
				JOHN, 0, StatementUpdate.EMPTY, TermUpdate.EMPTY, TermUpdate.EMPTY, aliases);
		assertThrows(UnsupportedOperationException.class, () -> update.getAliases().remove("en"));
		aliases.put("sk", AliasUpdateBuilder.create().add(SK).build());
		assertEquals(1, update.getAliases().size());
	}

	@Test
	public void testEmpty() {
		assertTrue(create(JOHN, 0, StatementUpdate.EMPTY, TermUpdate.EMPTY, TermUpdate.EMPTY, Collections.emptyMap())
				.isEmpty());
		assertFalse(create(JOHN, 0, StatementUpdate.EMPTY, LABELS, TermUpdate.EMPTY, Collections.emptyMap()).isEmpty());
		assertFalse(create(JOHN, 0, StatementUpdate.EMPTY, TermUpdate.EMPTY, DESCRIPTIONS, Collections.emptyMap())
				.isEmpty());
		assertFalse(create(JOHN, 0, StatementUpdate.EMPTY, TermUpdate.EMPTY, TermUpdate.EMPTY, ALIASES).isEmpty());
	}

	@Test
	public void testEquality() {
		TermedStatementDocumentUpdate update = create(JOHN, 0, STATEMENTS, LABELS, DESCRIPTIONS, ALIASES);
		assertTrue(update.equals(update));
		assertTrue(update.equals(create(JOHN, 0, STATEMENTS, LABELS, DESCRIPTIONS, ALIASES)));
		assertFalse(update.equals(create(JOHN, 0, STATEMENTS, TermUpdate.EMPTY, DESCRIPTIONS, ALIASES)));
		assertFalse(update.equals(create(JOHN, 0, STATEMENTS, LABELS, TermUpdate.EMPTY, ALIASES)));
		assertFalse(update.equals(create(JOHN, 0, STATEMENTS, LABELS, DESCRIPTIONS, Collections.emptyMap())));
	}

	@Test
	public void testHashCode() {
		TermedStatementDocumentUpdate update1 = create(JOHN, 123, STATEMENTS, LABELS, DESCRIPTIONS, ALIASES);
		TermedStatementDocumentUpdate update2 = create(JOHN, 123, STATEMENTS, LABELS, DESCRIPTIONS, ALIASES);
		assertEquals(update1.hashCode(), update2.hashCode());
	}

}
