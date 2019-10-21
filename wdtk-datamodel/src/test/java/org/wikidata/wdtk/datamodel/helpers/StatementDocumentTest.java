package org.wikidata.wdtk.datamodel.helpers;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import static org.junit.Assert.*;

public class StatementDocumentTest {
	private static ItemIdValue Q1 = Datamodel.makeWikidataItemIdValue("Q1");
	private static ItemIdValue Q2 = Datamodel.makeWikidataItemIdValue("Q2");
	private static PropertyIdValue P1 = Datamodel.makeWikidataPropertyIdValue("P1");
	private static PropertyIdValue P2 = Datamodel.makeWikidataPropertyIdValue("P2");
	private static PropertyIdValue P3 = Datamodel.makeWikidataPropertyIdValue("P3");

	@Test
	public void testFindStatement() {
		Statement s1 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q1).build();
		Statement s2 = StatementBuilder.forSubjectAndProperty(Q1, P1)
				.withValue(Q2).build();
		Statement s3 = StatementBuilder.forSubjectAndProperty(Q1, P2)
				.withValue(Q1).build();
		ItemDocument id = ItemDocumentBuilder.forItemId(Q1).withStatement(s1)
				.withStatement(s2).withStatement(s3).build();

		assertTrue(id.hasStatement(P1));
		assertTrue(id.hasStatement("P1"));
		assertNull(id.findStatement(P1));
		assertNull(id.findStatement("P1"));
		assertTrue(id.hasStatement(P2));
		assertTrue(id.hasStatement("P2"));
		assertEquals(s3, id.findStatement(P2));
		assertEquals(s3, id.findStatement("P2"));
		assertFalse(id.hasStatement(P3));
		assertFalse(id.hasStatement("P3"));
		assertNull(id.findStatement(P3));
		assertNull(id.findStatement("P3"));
	}
}
