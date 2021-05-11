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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

public class StatementGroupTest {


	private EntityIdValue subject = new ItemIdValueImpl("Q42", "http://wikidata.org/entity/");
	private PropertyIdValue property = new PropertyIdValueImpl("P42", "http://wikidata.org/entity/");
	private Snak mainSnak = new ValueSnakImpl(property, subject);
	private Statement statement1 = new StatementImpl("MyId", StatementRank.NORMAL, mainSnak,
			Collections.emptyList(), Collections.emptyList(), subject);
	private Statement statement2 = new StatementImpl("MyId", StatementRank.PREFERRED, mainSnak,
			Collections.emptyList(), Collections.emptyList(), subject);
	private Statement statementEmptyId = new StatementImpl("", StatementRank.NORMAL, mainSnak,
			Collections.emptyList(), Collections.emptyList(), subject);
	private Statement statementDeprecrated = new StatementImpl("DepId", StatementRank.DEPRECATED, mainSnak,
			Collections.emptyList(), Collections.emptyList(), subject);
	private StatementGroup sg1 = new StatementGroupImpl(Collections.singletonList(statement1));
	private StatementGroup sg2 = new StatementGroupImpl(Collections.singletonList(statement1));

	@Test
	public void implementsCollection() {
		assertFalse(sg1.isEmpty());
		assertEquals(1, sg1.size());
		assertTrue(sg1.contains(statement1));
		assertFalse(sg1.contains(statement2));
		assertTrue(sg1.iterator().hasNext());
		assertEquals(sg1.iterator().next(), statement1);
		assertArrayEquals(new Statement[] {statement1}, sg1.toArray());
	}

	@Test
	public void statementListIsCorrect() {
		assertEquals(sg1.getStatements(), Collections.singletonList(statement1));
	}

	@Test
	public void getBestStatementsWithPreferred() {
		assertEquals(
				new StatementGroupImpl(Collections.singletonList(statement2)),
				new StatementGroupImpl(Arrays.asList(statement1, statement2)).getBestStatements()
		);
	}

	@Test
	public void getBestStatementsWithoutPreferred() {
		assertEquals(
				new StatementGroupImpl(Collections.singletonList(statement1)),
				new StatementGroupImpl(Collections.singletonList(statement1)).getBestStatements()
		);
	}

	@Test
	public void getBestStatementsEmpty() {
		assertNull(
				new StatementGroupImpl(Collections.singletonList(statementDeprecrated)).getBestStatements()
		);
	}

	@Test
	public void propertyIsCorrect() {
		assertEquals(sg1.getProperty(), property);
	}

	@Test
	public void subjectIsCorrect() {
		assertEquals(sg1.getSubject(), subject);
	}

	@Test
	public void equalityBasedOnContent() {
		List<Statement> statements = new ArrayList<>();
		statements.add(statement1);
		statements.add(statement2);
		StatementGroup sg3 = new StatementGroupImpl(statements);

		assertEquals(sg1, sg1);
		assertEquals(sg1, sg2);
		assertNotEquals(sg1, sg3);
		assertNotEquals(sg1, null);
		assertNotEquals(sg1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(sg1.hashCode(), sg2.hashCode());
	}

	@Test
	public void statementListNotNull() {
		assertThrows(NullPointerException.class, () -> new StatementGroupImpl(null));
	}

	@Test
	public void statementListNotEmpty() {
		assertThrows(IllegalArgumentException.class, () -> new StatementGroupImpl(Collections.emptyList()));
	}

	@Test
	public void statementListRequiresSameSubject() {
		List<Statement> statements = new ArrayList<>();

		statements.add(statement1);

		EntityIdValue subject2 = new ItemIdValueImpl("Q23",
				"http://wikidata.org/entity/");
		Snak mainSnak = new NoValueSnakImpl(property);
		Statement s2 = new StatementImpl("MyId", StatementRank.NORMAL, mainSnak,
				Collections.emptyList(),  Collections.emptyList(), subject2);
		statements.add(s2);

		assertThrows(IllegalArgumentException.class, () -> new StatementGroupImpl(statements));
	}

	@Test
	public void statementListRequiresSameProperty() {
		List<Statement> statements = new ArrayList<>();

		statements.add(statement1);

		PropertyIdValue property2 = new PropertyIdValueImpl("P23", "http://wikidata.org/entity/");
		Snak mainSnak = new NoValueSnakImpl(property2);
		Statement s2 = new StatementImpl("MyId", StatementRank.NORMAL, mainSnak,
			Collections.emptyList(), Collections.emptyList(), subject);
		statements.add(s2);

		assertThrows(IllegalArgumentException.class, () -> new StatementGroupImpl(statements));
	}

	@Test
	public void addSameStatementToGroup() {
		StatementGroup added = sg1.withStatement(statement1);
		assertEquals(sg1, added);
	}

	@Test
	public void addStatementWithMatchingId() {
		StatementGroup added = sg1.withStatement(statement2);
		
		assertEquals(new StatementGroupImpl(Collections.singletonList(statement2)), added);
	}

	@Test
	public void addStatementEmptyId() {
		StatementGroup initial = new StatementGroupImpl(Collections.singletonList(statementEmptyId));
		StatementGroup added = initial.withStatement(statementEmptyId);
		
		assertEquals(new StatementGroupImpl(Arrays.asList(statementEmptyId, statementEmptyId)), added);
	}
}
