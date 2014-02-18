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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class StatementImplTest {

	EntityIdValue subject;
	ValueSnak mainSnak;

	Statement s1;
	Statement s2;

	@Before
	public void setUp() throws Exception {
		subject = new ItemIdValueImpl("Q42", "http://wikidata.org/entity/");
		PropertyIdValue property = new PropertyIdValueImpl("P42",
				"http://wikidata.org/entity/");
		mainSnak = new ValueSnakImpl(property, subject);

		s1 = new StatementImpl(subject, mainSnak,
				Collections.<Snak> emptyList(),
				Collections.<Reference> emptyList(), StatementRank.NORMAL);
		s2 = new StatementImpl(subject, mainSnak,
				Collections.<Snak> emptyList(),
				Collections.<Reference> emptyList(), StatementRank.NORMAL);
	}

	@Test
	public void gettersWorking() {
		assertEquals(s1.getSubject(), subject);
		assertEquals(s1.getMainSnak(), mainSnak);
		assertEquals(s1.getQualifiers(), Collections.<Snak> emptyList());
		assertEquals(s1.getReferences(),
				Collections.<List<? extends Snak>> emptyList());
		assertEquals(s1.getRank(), StatementRank.NORMAL);
	}

	@Test(expected = NullPointerException.class)
	public void subjectNotNull() {
		new StatementImpl(null, mainSnak, Collections.<Snak> emptyList(),
				Collections.<Reference> emptyList(), StatementRank.NORMAL);
	}

	@Test(expected = NullPointerException.class)
	public void mainSnakNotNull() {
		new StatementImpl(subject, null, Collections.<Snak> emptyList(),
				Collections.<Reference> emptyList(), StatementRank.NORMAL);
	}

	@Test(expected = NullPointerException.class)
	public void qualifiersNotNull() {
		new StatementImpl(subject, mainSnak, null,
				Collections.<Reference> emptyList(), StatementRank.NORMAL);
	}

	@Test(expected = NullPointerException.class)
	public void referencesNotNull() {
		new StatementImpl(subject, mainSnak, Collections.<Snak> emptyList(),
				null, StatementRank.NORMAL);
	}

	@Test(expected = NullPointerException.class)
	public void rankNotNull() {
		new StatementImpl(subject, mainSnak, Collections.<Snak> emptyList(),
				Collections.<Reference> emptyList(), null);
	}

	@Test
	public void snakHashBasedOnContent() {
		assertEquals(s1.hashCode(), s2.hashCode());
	}

	@Test
	public void statementEqualityBasedOnContent() {
		Statement s3, s4, s5, s6, s7;
		EntityIdValue subject2 = new ItemIdValueImpl("Q43",
				"http://wikidata.org/entity/");
		PropertyIdValue property = new PropertyIdValueImpl("P43",
				"http://wikidata.org/entity/");
		ValueSnak mainSnak2 = new ValueSnakImpl(property, subject2);

		s3 = new StatementImpl(subject2, mainSnak,
				Collections.<Snak> emptyList(),
				Collections.<Reference> emptyList(), StatementRank.NORMAL);
		s4 = new StatementImpl(subject, mainSnak2,
				Collections.<Snak> emptyList(),
				Collections.<Reference> emptyList(), StatementRank.NORMAL);
		s5 = new StatementImpl(subject, mainSnak,
				Collections.<Snak> singletonList(mainSnak),
				Collections.<Reference> emptyList(), StatementRank.NORMAL);
		s6 = new StatementImpl(subject, mainSnak,
				Collections.<Snak> emptyList(),
				Collections.<Reference> singletonList(new ReferenceImpl(
						Collections.<ValueSnak> singletonList(mainSnak))),
				StatementRank.NORMAL);
		s7 = new StatementImpl(subject, mainSnak,
				Collections.<Snak> emptyList(),
				Collections.<Reference> emptyList(), StatementRank.PREFERRED);

		assertEquals(s1, s1);
		assertEquals(s1, s2);
		assertThat(s1, not(equalTo(s3)));
		assertThat(s1, not(equalTo(s4)));
		assertThat(s1, not(equalTo(s5)));
		assertThat(s1, not(equalTo(s6)));
		assertThat(s1, not(equalTo(s7)));
		assertThat(s1, not(equalTo(null)));
		assertFalse(s1.equals(this));
	}

}
