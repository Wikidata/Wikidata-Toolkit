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
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityId;
import org.wikidata.wdtk.datamodel.interfaces.PropertyId;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

public class StatementImplTest {

	EntityId subject;
	Snak mainSnak;

	Statement s1;
	Statement s2;

	@Before
	public void setUp() throws Exception {
		subject = new ItemIdImpl("Q42", "http://wikidata.org/entity/");
		PropertyId property = new PropertyIdImpl("P42",
				"http://wikidata.org/entity/");
		mainSnak = new ValueSnakImpl(property, subject);

		s1 = new StatementImpl(subject, mainSnak,
				Collections.<Snak> emptyList(),
				Collections.<List<? extends Snak>> emptyList(),
				StatementRank.NORMAL);
		s2 = new StatementImpl(subject, mainSnak,
				Collections.<Snak> emptyList(),
				Collections.<List<? extends Snak>> emptyList(),
				StatementRank.NORMAL);
	}

	@Test(expected = NullPointerException.class)
	public void subjectNotNull() {
		new StatementImpl(null, mainSnak, Collections.<Snak> emptyList(),
				Collections.<List<? extends Snak>> emptyList(),
				StatementRank.NORMAL);
	}

	@Test(expected = NullPointerException.class)
	public void mainSnakNotNull() {
		new StatementImpl(subject, null, Collections.<Snak> emptyList(),
				Collections.<List<? extends Snak>> emptyList(),
				StatementRank.NORMAL);
	}

	@Test(expected = NullPointerException.class)
	public void qualifiersNotNull() {
		new StatementImpl(subject, mainSnak, null,
				Collections.<List<? extends Snak>> emptyList(),
				StatementRank.NORMAL);
	}

	@Test(expected = NullPointerException.class)
	public void referencesNotNull() {
		new StatementImpl(subject, mainSnak, Collections.<Snak> emptyList(),
				null, StatementRank.NORMAL);
	}

	@Test(expected = NullPointerException.class)
	public void rankNotNull() {
		new StatementImpl(subject, mainSnak, Collections.<Snak> emptyList(),
				Collections.<List<? extends Snak>> emptyList(), null);
	}

	@Test
	public void snakHashBasedOnContent() {
		assertEquals(s1.hashCode(), s2.hashCode());
	}

	@Test
	public void statementEqualityBasedOnContent() {
		Statement s3, s4, s5, s6, s7;
		EntityId subject2 = new ItemIdImpl("Q43", "http://wikidata.org/entity/");
		PropertyId property = new PropertyIdImpl("P43",
				"http://wikidata.org/entity/");
		Snak mainSnak2 = new ValueSnakImpl(property, subject2);

		s3 = new StatementImpl(subject2, mainSnak,
				Collections.<Snak> emptyList(),
				Collections.<List<? extends Snak>> emptyList(),
				StatementRank.NORMAL);
		s4 = new StatementImpl(subject, mainSnak2,
				Collections.<Snak> emptyList(),
				Collections.<List<? extends Snak>> emptyList(),
				StatementRank.NORMAL);
		s5 = new StatementImpl(subject, mainSnak,
				Collections.<Snak> singletonList(mainSnak),
				Collections.<List<? extends Snak>> emptyList(),
				StatementRank.NORMAL);
		s6 = new StatementImpl(subject, mainSnak,
				Collections.<Snak> emptyList(),
				Collections.<List<? extends Snak>> singletonList(Collections
						.<Snak> singletonList(mainSnak)), StatementRank.NORMAL);
		s7 = new StatementImpl(subject, mainSnak,
				Collections.<Snak> emptyList(),
				Collections.<List<? extends Snak>> emptyList(),
				StatementRank.PREFERRED);

		assertEquals(s1, s1);
		assertEquals(s1, s2);
		assertThat(s1, not(equalTo(s3)));
		assertThat(s1, not(equalTo(s4)));
		assertThat(s1, not(equalTo(s5)));
		assertThat(s1, not(equalTo(s6)));
		assertThat(s1, not(equalTo(s7)));
		assertThat(s1, not(equalTo(null)));
	}

}
