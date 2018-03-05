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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

public class ItemDocumentImplTest {

	ItemDocument ir1;
	ItemDocument ir2;

	Statement s;

	ItemIdValue iid;
	List<StatementGroup> statementGroups;
	List<SiteLink> sitelinks;

	@Before
	public void setUp() throws Exception {
		iid = new ItemIdValueImpl("Q42", "http://wikibase.org/entity/");

		s = new StatementImpl("MyId", StatementRank.NORMAL,
				new SomeValueSnakImpl(new PropertyIdValueImpl("P42", "http://wikibase.org/entity/")),
				Collections.emptyList(), Collections.emptyList(), iid);
		StatementGroup sg = new StatementGroupImpl(Collections.singletonList(s));
		statementGroups = Collections.singletonList(sg);

		SiteLink sl = new SiteLinkImpl("Douglas Adams", "enwiki",
				Collections. emptyList());
		sitelinks = Collections.singletonList(sl);

		ir1 = new ItemDocumentImpl(iid,
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(),
				statementGroups, sitelinks, 1234);
		ir2 = new ItemDocumentImpl(iid,
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(),
				statementGroups, sitelinks, 1234);
	}

	@Test
	public void fieldsAreCorrect() {
		assertEquals(ir1.getItemId(), iid);
		assertEquals(ir1.getEntityId(), iid);
		assertEquals(ir1.getStatementGroups(), statementGroups);
		assertEquals(new ArrayList<>(ir1.getSiteLinks().values()), sitelinks);
	}

	@Test
	public void equalityBasedOnContent() {
		ItemDocument irDiffStatementGroups = new ItemDocumentImpl(iid,
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(), sitelinks, 1234);
		ItemDocument irDiffSiteLinks = new ItemDocumentImpl(iid,
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(),
				statementGroups, Collections. emptyList(),
				1234);
		ItemDocument irDiffRevisions = new ItemDocumentImpl(iid,
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(),
				statementGroups, sitelinks, 1235);

		PropertyDocument pr = new PropertyDocumentImpl(
				new PropertyIdValueImpl("P42", "foo"),
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(), new DatatypeIdImpl(
						DatatypeIdValue.DT_STRING), 1234);

		// we need to use empty lists of Statement groups to test inequality
		// based on different item ids with all other data being equal
		ItemDocument irDiffItemIdValue = new ItemDocumentImpl(
				new ItemIdValueImpl("Q23", "http://example.org/"),
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(), sitelinks, 1234);

		assertEquals(ir1, ir1);
		assertEquals(ir1, ir2);
		assertThat(ir1, not(equalTo(irDiffStatementGroups)));
		assertThat(ir1, not(equalTo(irDiffSiteLinks)));
		assertThat(ir1, not(equalTo(irDiffRevisions)));
		assertThat(irDiffStatementGroups, not(equalTo(irDiffItemIdValue)));
		assertFalse(ir1.equals(pr));
		assertThat(ir1, not(equalTo(null)));
		assertFalse(ir1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(ir1.hashCode(), ir2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void idNotNull() {
		new ItemDocumentImpl(null,
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(),
				statementGroups, sitelinks, 1234);
	}

	@Test
	public void labelsCanBeNull() {
		ItemDocument doc = new ItemDocumentImpl(iid, null,
				Collections. emptyList(),
				Collections. emptyList(),
				statementGroups, sitelinks, 1234);
		assertTrue(doc.getLabels().isEmpty());
	}

	@Test
	public void descriptionsNotNull() {
		ItemDocument doc = new ItemDocumentImpl(iid,
				Collections. emptyList(), null,
				Collections. emptyList(),
				statementGroups, sitelinks, 1234);
		assertTrue(doc.getDescriptions().isEmpty());
	}

	@Test
	public void aliasesCanBeNull() {
		ItemDocument doc =new ItemDocumentImpl(iid,
				Collections. emptyList(),
				Collections. emptyList(), null,
				statementGroups, sitelinks, 1234);
		assertTrue(doc.getAliases().isEmpty());
	}

	@Test
	public void statementGroupsCanBeNull() {
		ItemDocument doc = new ItemDocumentImpl(iid,
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(), null,
				sitelinks, 1234);
		assertTrue(doc.getStatementGroups().isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void statementGroupsUseSameSubject() {
		ItemIdValue iid2 = new ItemIdValueImpl("Q23", "http://example.org/");
		Statement s2 = new StatementImpl("MyId", StatementRank.NORMAL,
				new SomeValueSnakImpl(new PropertyIdValueImpl("P42", "http://wikibase.org/entity/")),
				Collections.emptyList(),  Collections.emptyList(), iid2);
		StatementGroup sg2 = new StatementGroupImpl(Collections.singletonList(s2));

		List<StatementGroup> statementGroups2 = new ArrayList<>();
		statementGroups2.add(statementGroups.get(0));
		statementGroups2.add(sg2);

		new ItemDocumentImpl(iid,
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(),
				statementGroups2, sitelinks, 1234);
	}

	@Test(expected = NullPointerException.class)
	public void sitelinksNotNull() {
		new ItemDocumentImpl(iid,
				Collections. emptyList(),
				Collections. emptyList(),
				Collections. emptyList(),
				statementGroups, null, 1234);
	}

	@Test
	public void iterateOverAllStatements() {
		Iterator<Statement> statements = ir1.getAllStatements();

		assertTrue(statements.hasNext());
		assertEquals(s, statements.next());
		assertFalse(statements.hasNext());
	}

}
