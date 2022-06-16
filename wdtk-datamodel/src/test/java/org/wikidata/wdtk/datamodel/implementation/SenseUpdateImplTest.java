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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.wikidata.wdtk.datamodel.implementation.JsonTestUtils.producesJson;
import static org.wikidata.wdtk.datamodel.implementation.JsonTestUtils.toJson;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.SenseUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.TermUpdateBuilder;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

public class SenseUpdateImplTest {

	private static final SenseIdValue S1 = Datamodel.makeWikidataSenseIdValue("L1-S1");
	private static final StatementUpdate STATEMENTS = StatementDocumentUpdateImplTest.STATEMENTS;
	private static final TermUpdate GLOSSES = TermUpdateBuilder.create().remove("en").build();

	@Test
	public void testFields() {
		SenseUpdate update = new SenseUpdateImpl(S1, 123, GLOSSES, STATEMENTS);
		assertEquals(S1, update.getEntityId());
		assertEquals(123, update.getBaseRevisionId());
		assertSame(GLOSSES, update.getGlosses());
		assertSame(STATEMENTS, update.getStatements());
	}

	@Test
	public void testValidation() {
		assertThrows(NullPointerException.class, () -> new SenseUpdateImpl(S1, 0, null, StatementUpdate.EMPTY));
	}

	@Test
	public void testEmpty() {
		assertFalse(new SenseUpdateImpl(S1, 0, TermUpdate.EMPTY, STATEMENTS).isEmpty());
		assertFalse(new SenseUpdateImpl(S1, 0, GLOSSES, StatementUpdate.EMPTY).isEmpty());
		assertTrue(new SenseUpdateImpl(S1, 0, TermUpdate.EMPTY, StatementUpdate.EMPTY).isEmpty());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void testEquality() {
		SenseUpdate update = new SenseUpdateImpl(S1, 0, GLOSSES, STATEMENTS);
		assertFalse(update.equals(null));
		assertFalse(update.equals(this));
		assertTrue(update.equals(update));
		assertTrue(update.equals(
				new SenseUpdateImpl(S1, 0, TermUpdateBuilder.create().remove("en").build(), STATEMENTS)));
		assertFalse(update.equals(new SenseUpdateImpl(S1, 123, GLOSSES, StatementUpdate.EMPTY)));
		assertFalse(update.equals(new SenseUpdateImpl(S1, 123, TermUpdate.EMPTY, STATEMENTS)));
	}

	@Test
	public void testHashCode() {
		SenseUpdate update1 = new SenseUpdateImpl(S1, 123, GLOSSES, STATEMENTS);
		SenseUpdate update2 = new SenseUpdateImpl(S1, 123, TermUpdateBuilder.create().remove("en").build(), STATEMENTS);
		assertEquals(update1.hashCode(), update2.hashCode());
	}

	@Test
	public void testJson() {
		assertThat(new SenseUpdateImpl(S1, 123, TermUpdate.EMPTY, StatementUpdate.EMPTY), producesJson("{}"));
		assertThat(SenseUpdateBuilder.forEntityId(S1).updateGlosses(GLOSSES).build(),
				producesJson("{'glosses':" + toJson(GLOSSES) + "}"));
		assertThat(SenseUpdateBuilder.forEntityId(S1).updateStatements(STATEMENTS).build(),
				producesJson("{'claims':" + toJson(STATEMENTS) + "}"));
	}

}
