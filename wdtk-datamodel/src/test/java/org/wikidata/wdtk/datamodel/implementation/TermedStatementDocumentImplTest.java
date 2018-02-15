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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.StatementImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

public class TermedStatementDocumentImplTest {
	
	private Map<String, List<Statement>> initialStatements = null;
	private Statement statementA = null;
	private Statement statementEmptyId = null;
	private String statementIdA = "myIdA";
	private String statementIdB = "myIdB";
	private Set<String> initialStatementIds = null;
	
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
		initialStatementIds = new HashSet<>();
		initialStatementIds.add(statementIdA);
		initialStatementIds.add(statementIdB);
	}
	
	@Test
	public void removeNoStatements() {
		Map<String, List<Statement>> removed = TermedStatementDocumentImpl.removeStatements(
				Collections.emptySet(), initialStatements);
		assertEquals(removed, initialStatements);
	}
	
	@Test
	public void removeAllStatements() {
		Map<String, List<Statement>> removed = TermedStatementDocumentImpl.removeStatements(
				Arrays.asList(statementIdA, statementIdB).stream().collect(Collectors.toSet()), initialStatements);

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
