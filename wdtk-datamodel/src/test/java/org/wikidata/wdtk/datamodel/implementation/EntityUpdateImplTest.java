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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.PropertyUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.TermUpdateBuilder;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class EntityUpdateImplTest {

	private static final PropertyIdValue P1 = Datamodel.makeWikidataPropertyIdValue("P1");
	private static final PropertyIdValue P2 = Datamodel.makeWikidataPropertyIdValue("P2");
	private static final PropertyDocument PROPERTY = Datamodel.makePropertyDocument(
			P1, Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_ITEM));
	private static final TermUpdate NO_TERMS = TermUpdateBuilder.create().build();
	private static final StatementUpdate NO_STATEMENTS = StatementUpdateBuilder.create().build();
	private static final Map<String, List<MonolingualTextValue>> NO_ALIASES = Collections.emptyMap();

	@Test
	public void testFields() {
		EntityUpdate update = new PropertyUpdateImpl(P1, PROPERTY, NO_TERMS, NO_TERMS, NO_ALIASES, NO_STATEMENTS);
		assertEquals(P1, update.getEntityId());
		assertEquals(PROPERTY, update.getBaseRevision());
		update = new PropertyUpdateImpl(P1, null, NO_TERMS, NO_TERMS, NO_ALIASES, NO_STATEMENTS);
		assertNull(update.getBaseRevision());
	}

	@Test
	public void testValidation() {
		assertThrows(NullPointerException.class, () -> new PropertyUpdateImpl(
				null, PROPERTY, NO_TERMS, NO_TERMS, NO_ALIASES, NO_STATEMENTS));
		assertThrows(NullPointerException.class, () -> new PropertyUpdateImpl(
				null, null, NO_TERMS, NO_TERMS, NO_ALIASES, NO_STATEMENTS));
		assertThrows(IllegalArgumentException.class, () -> new PropertyUpdateImpl(
				P2, PROPERTY, NO_TERMS, NO_TERMS, NO_ALIASES, NO_STATEMENTS));
		assertThrows(IllegalArgumentException.class, () -> new PropertyUpdateImpl(
				PropertyIdValue.NULL, null, NO_TERMS, NO_TERMS, NO_ALIASES, NO_STATEMENTS));
		assertThrows(IllegalArgumentException.class, () -> new PropertyUpdateImpl(
				PropertyIdValue.NULL, PROPERTY.withEntityId(PropertyIdValue.NULL),
				NO_TERMS, NO_TERMS, NO_ALIASES, NO_STATEMENTS));
	}

	@Test
	public void testEquality() {
		EntityUpdate update1 = PropertyUpdateBuilder.forEntityId(P1).build();
		EntityUpdate update2 = PropertyUpdateBuilder.forBaseRevision(PROPERTY).build();
		assertEquals(update1, update1);
		assertEquals(update2, update2);
		assertEquals(update1, PropertyUpdateBuilder.forEntityId(P1).build());
		assertEquals(update2, PropertyUpdateBuilder.forBaseRevision(PROPERTY).build());
		assertNotEquals(update1, null);
		assertNotEquals(update1, this);
		assertNotEquals(update1, update2);
		assertNotEquals(update1, PropertyUpdateBuilder.forEntityId(P2).build());
		assertNotEquals(update2, PropertyUpdateBuilder.forBaseRevision(PROPERTY.withRevisionId(1234)).build());
	}

	@Test
	public void testHashCode() {
		assertEquals(
				PropertyUpdateBuilder.forBaseRevision(PROPERTY).build().hashCode(),
				PropertyUpdateBuilder.forBaseRevision(PROPERTY).build().hashCode());
	}

}
