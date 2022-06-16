package org.wikidata.wdtk.datamodel.implementation;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2018 Wikidata Toolkit Developers
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

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.util.*;

import static org.junit.Assert.*;

public class TermedStatementDocumentImplTest {
	
	private Map<String, List<Statement>> initialStatements = null;
	private Statement statementA = null;
	private Statement statementEmptyId = null;
	private String statementIdA = "myIdA";
	private String statementIdB = "myIdB";

	@Before
	public void setUp() {
		ItemIdValue subject = new ItemIdValueImpl("Q42",
				"http://wikidata.org/entity/");
		PropertyIdValue property = new PropertyIdValueImpl("P42",
				"http://wikidata.org/entity/");
		Snak mainSnak = new ValueSnakImpl(property, subject);

		statementA = new StatementImpl(statementIdA, StatementRank.NORMAL, mainSnak,
				Collections.emptyList(), Collections.emptyList(), subject);
		Statement statementB = new StatementImpl(statementIdB, StatementRank.PREFERRED, mainSnak,
				Collections.emptyList(), Collections.emptyList(), subject);
		statementEmptyId = new StatementImpl("", StatementRank.NORMAL, mainSnak,
				Collections.emptyList(), Collections.emptyList(), subject);
		
		List<Statement> statements = Arrays.asList(statementA, statementB);
		initialStatements = new HashMap<>();
		initialStatements.put(property.getId(), statements);
	}
	
	@Test
	public void removeNoStatements() {
		Map<String, List<Statement>> removed = TermedStatementDocumentImpl.removeStatements(
				Collections.emptySet(), initialStatements);
		assertEquals(removed, initialStatements);
	}
	
	@Test
	public void removeAllStatements() {
		Set<String> toRemove = new HashSet<>();
		toRemove.add(statementIdA);
		toRemove.add(statementIdB);
		Map<String, List<Statement>> removed = TermedStatementDocumentImpl.removeStatements(toRemove, initialStatements);

		assertTrue(removed.isEmpty());
	}
	
	@Test
	public void addExistingStatement() {
		Map<String, List<Statement>> added = TermedStatementDocumentImpl.addStatementToGroups(statementA, initialStatements);
		assertEquals(initialStatements, added);
	}
	
	@Test
	public void addSameStatementWithoutId() {
		Map<String, List<Statement>> added = TermedStatementDocumentImpl.addStatementToGroups(statementEmptyId, initialStatements);
		assertNotEquals(initialStatements, added);
	}
}
