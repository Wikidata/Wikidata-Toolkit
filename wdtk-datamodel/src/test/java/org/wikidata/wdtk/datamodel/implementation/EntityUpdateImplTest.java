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
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityUpdate;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class EntityUpdateImplTest {

	private static final PropertyIdValue P1 = Datamodel.makeWikidataPropertyIdValue("P1");
	private static final PropertyIdValue P2 = Datamodel.makeWikidataPropertyIdValue("P2");

	private static EntityUpdate create(PropertyIdValue entityId, long revisionId) {
		return new PropertyUpdateImpl(entityId, revisionId,
				TermUpdate.EMPTY, TermUpdate.EMPTY, Collections.emptyMap(), StatementUpdate.EMPTY);
	}

	@Test
	public void testFields() {
		EntityUpdate update = create(P1, 123);
		assertEquals(P1, update.getEntityId());
		assertEquals(123, update.getBaseRevisionId());
		update = create(P1, 0);
		assertEquals(0, update.getBaseRevisionId());
	}

	@Test
	public void testValidation() {
		assertThrows(NullPointerException.class, () -> create(null, 0));
		assertThrows(IllegalArgumentException.class, () -> create(PropertyIdValue.NULL, 0));
	}

	@Test
	public void testEquality() {
		EntityUpdate update = create(P1, 123);
		assertTrue(update.equals(update));
		assertTrue(update.equals(create(P1, 123)));
		assertFalse(update.equals(create(P2, 123)));
		assertFalse(update.equals(create(P1, 777)));
	}

	@Test
	public void testHashCode() {
		assertEquals(create(P1, 123).hashCode(), create(P1, 123).hashCode());
	}

}
