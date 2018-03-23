package org.wikidata.wdtk.datamodel.implementation;

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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

public class StatementGroupTest {

	private StatementGroup sg1;
	private StatementGroup sg2;
	private Statement statement1;
	private Statement statement2;
	private EntityIdValue subject;
	private PropertyIdValue property;

	@Before
	public void setUp() throws Exception {
		subject = new ItemIdValueImpl("Q42",
				"http://wikidata.org/entity/");
		property = new PropertyIdValueImpl("P42",
				"http://wikidata.org/entity/");
		Snak mainSnak = new ValueSnakImpl(property, subject);

		statement1 = new StatementImpl("MyId", StatementRank.NORMAL, mainSnak,
				Collections.emptyList(), Collections.emptyList(), subject);
		statement2 = new StatementImpl("MyId", StatementRank.PREFERRED, mainSnak,
				Collections.emptyList(), Collections.emptyList(), subject);

		sg1 = new StatementGroupImpl(
				Collections.singletonList(statement1));
		sg2 = new StatementGroupImpl(
				Collections.singletonList(statement1));
	}

	@Test
	public void statementListIsCorrect() {
		assertEquals(sg1.getStatements(),
				Collections.singletonList(statement1));
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
	public void size() {
		assertEquals(sg1.size(), 1);
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

	@Test(expected = NullPointerException.class)
	public void statementListNotNull() {
		new StatementGroupImpl(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void statementListNotEmpty() {
		new StatementGroupImpl(Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void statementListRequiresSameSubject() {
		List<Statement> statements = new ArrayList<>();

		statements.add(statement1);

		EntityIdValue subject2 = new ItemIdValueImpl("Q23",
				"http://wikidata.org/entity/");
		Snak mainSnak = new NoValueSnakImpl(property);
		Statement s2 = new StatementImpl("MyId", StatementRank.NORMAL, mainSnak,
				Collections.emptyList(),  Collections.emptyList(), subject2);
		statements.add(s2);

		new StatementGroupImpl(statements);
	}

	@Test(expected = IllegalArgumentException.class)
	public void statementListRequiresSameProperty() {
		List<Statement> statements = new ArrayList<>();

		statements.add(statement1);

		PropertyIdValue property2 = new PropertyIdValueImpl("P23", "http://wikidata.org/entity/");
		Snak mainSnak = new NoValueSnakImpl(property2);
		Statement s2 = new StatementImpl("MyId", StatementRank.NORMAL, mainSnak,
			Collections.emptyList(), Collections.emptyList(), subject);
		statements.add(s2);

		new StatementGroupImpl(statements);
	}

}
