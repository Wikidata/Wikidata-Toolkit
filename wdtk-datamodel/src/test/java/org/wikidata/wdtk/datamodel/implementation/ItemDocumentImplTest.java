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
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

public class ItemDocumentImplTest {

	ItemDocument ir1;
	ItemDocument ir2;

	Statement s;

	ItemIdValue iid;
	List<StatementGroup> statementGroups;
	Map<String, SiteLink> sitelinks;

	@Before
	public void setUp() throws Exception {
		iid = ItemIdValueImpl.create("Q42",
				"http://wikibase.org/entity/");

		Claim c = new ClaimImpl(iid, new SomeValueSnakImpl(
				PropertyIdValueImpl.create("P42",
						"http://wikibase.org/entity/")),
				Collections.<SnakGroup> emptyList());
		s = new StatementImpl(c, Collections.<Reference> emptyList(),
				StatementRank.NORMAL, "MyId");
		StatementGroup sg = new StatementGroupImpl(Collections.singletonList(s));
		statementGroups = Collections.singletonList(sg);

		SiteLink sl = new SiteLinkImpl("Douglas Adams", "enwiki",
				Collections.<String> emptyList());
		sitelinks = Collections.singletonMap("enwiki", sl);

		ir1 = new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, sitelinks);
		ir2 = new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, sitelinks);
	}

	@Test
	public void fieldsAreCorrect() {
		assertEquals(ir1.getItemId(), iid);
		assertEquals(ir1.getEntityId(), iid);
		assertEquals(ir1.getStatementGroups(), statementGroups);
		assertEquals(ir1.getSiteLinks(), sitelinks);
	}

	@Test
	public void equalityBasedOnContent() {
		ItemDocument irDiffStatementGroups = new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(), sitelinks);
		ItemDocument irDiffSiteLinks = new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, Collections.<String, SiteLink> emptyMap());

		PropertyDocument pr = new PropertyDocumentImpl(
				PropertyIdValueImpl.create("P42", "foo"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(), new DatatypeIdImpl(
						DatatypeIdValue.DT_STRING));

		// we need to use empty lists of Statement groups to test inequality
		// based on different item ids with all other data being equal
		ItemDocument irDiffItemIdValue = new ItemDocumentImpl(
				ItemIdValueImpl.create("Q23",
						"http://example.org/"),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<StatementGroup> emptyList(), sitelinks);

		assertEquals(ir1, ir1);
		assertEquals(ir1, ir2);
		assertThat(ir1, not(equalTo(irDiffStatementGroups)));
		assertThat(ir1, not(equalTo(irDiffSiteLinks)));
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
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, sitelinks);
	}

	@Test(expected = NullPointerException.class)
	public void labelsNotNull() {
		new ItemDocumentImpl(iid, null,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, sitelinks);
	}

	@Test(expected = NullPointerException.class)
	public void descriptionsNotNull() {
		new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(), null,
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, sitelinks);
	}

	@Test(expected = NullPointerException.class)
	public void aliasesNotNull() {
		new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(), null,
				statementGroups, sitelinks);
	}

	@Test(expected = NullPointerException.class)
	public void statementGroupsNotNull() {
		new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(), null, sitelinks);
	}

	@Test(expected = IllegalArgumentException.class)
	public void statementGroupsUseSameSubject() {
		ItemIdValue iid2 = ItemIdValueImpl.create("Q23",
				"http://example.org/");
		Claim c2 = new ClaimImpl(iid2, new SomeValueSnakImpl(
				PropertyIdValueImpl.create("P42",
						"http://wikibase.org/entity/")),
				Collections.<SnakGroup> emptyList());
		Statement s2 = new StatementImpl(c2,
				Collections.<Reference> emptyList(), StatementRank.NORMAL,
				"MyId");
		StatementGroup sg2 = new StatementGroupImpl(
				Collections.singletonList(s2));

		List<StatementGroup> statementGroups2 = new ArrayList<StatementGroup>();
		statementGroups2.add(statementGroups.get(0));
		statementGroups2.add(sg2);

		new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups2, sitelinks);
	}

	@Test(expected = NullPointerException.class)
	public void sitelinksNotNull() {
		new ItemDocumentImpl(iid,
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				Collections.<MonolingualTextValue> emptyList(),
				statementGroups, null);
	}

	@Test
	public void iterateOverAllStatements() {
		Iterator<Statement> statements = ir1.getAllStatements();

		assertTrue(statements.hasNext());
		assertEquals(s, statements.next());
		assertFalse(statements.hasNext());
	}

}
