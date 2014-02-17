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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeId;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemId;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

public class ItemDocumentImplTest {

	ItemDocument ir1;
	ItemDocument ir2;

	ItemId iid;
	Map<String, String> labels;
	Map<String, String> descriptions;
	Map<String, List<String>> aliases;
	List<Statement> statements;
	Map<String, SiteLink> sitelinks;

	@Before
	public void setUp() throws Exception {
		iid = new ItemIdImpl("Q42", "http://wikibase.org/entity/");
		labels = new HashMap<String, String>();
		labels.put("en", "Property 42");
		descriptions = new HashMap<String, String>();
		descriptions.put("de", "Dies ist Property 42.");
		aliases = new HashMap<String, List<String>>();
		aliases.put("en", Collections.<String> singletonList("An alias of P42"));

		Statement s = new StatementImpl(iid, new SomeValueSnakImpl(
				new PropertyIdImpl("P42", "http://wikibase.org/entity/")),
				Collections.<Snak> emptyList(),
				Collections.<List<? extends Snak>> emptyList(),
				StatementRank.NORMAL);
		statements = Collections.singletonList(s);
		SiteLink sl = new SiteLinkImpl("Douglas Adams", "enwiki",
				"http://en.wikipedia.org/wiki/",
				Collections.<String> emptyList());
		sitelinks = Collections.singletonMap("enwiki", sl);

		ir1 = new ItemDocumentImpl(iid, labels, descriptions, aliases,
				statements, sitelinks);
		ir2 = new ItemDocumentImpl(iid, labels, descriptions, aliases,
				statements, sitelinks);
	}

	@Test
	public void fieldsAreCorrect() {
		assertEquals(ir1.getItemId(), iid);
		assertEquals(ir1.getEntityId(), iid);
		assertEquals(ir1.getLabels(), labels);
		assertEquals(ir1.getDescriptions(), descriptions);
		assertEquals(ir1.getAliases(), aliases);
		assertEquals(ir1.getStatements(), statements);
		assertEquals(ir1.getSiteLinks(), sitelinks);
	}

	@Test
	public void valueEqualityBasedOnContent() {
		ItemDocument ir3 = new ItemDocumentImpl(new ItemIdImpl("Q43",
				"something"), labels, descriptions, aliases, statements,
				sitelinks);
		ItemDocument ir4 = new ItemDocumentImpl(iid,
				Collections.<String, String> emptyMap(), descriptions, aliases,
				statements, sitelinks);
		ItemDocument ir5 = new ItemDocumentImpl(iid, labels,
				Collections.<String, String> emptyMap(), aliases, statements,
				sitelinks);
		ItemDocument ir6 = new ItemDocumentImpl(iid, labels, descriptions,
				Collections.<String, List<String>> emptyMap(), statements,
				sitelinks);
		ItemDocument ir7 = new ItemDocumentImpl(iid, labels, descriptions,
				aliases, Collections.<Statement> emptyList(), sitelinks);
		ItemDocument ir8 = new ItemDocumentImpl(iid, labels, descriptions,
				aliases, statements, Collections.<String, SiteLink> emptyMap());

		PropertyDocument pr = new PropertyDocumentImpl(new PropertyIdImpl(
				"P42", "foo"), labels, descriptions, aliases,
				new DatatypeIdImpl(DatatypeId.DT_STRING));

		assertEquals(ir1, ir1);
		assertEquals(ir1, ir2);
		assertThat(ir1, not(equalTo(ir3)));
		assertThat(ir1, not(equalTo(ir4)));
		assertThat(ir1, not(equalTo(ir5)));
		assertThat(ir1, not(equalTo(ir6)));
		assertThat(ir1, not(equalTo(ir7)));
		assertThat(ir1, not(equalTo(ir8)));
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
		new ItemDocumentImpl(null, labels, descriptions, aliases, statements,
				sitelinks);
	}

	@Test(expected = NullPointerException.class)
	public void labelsNotNull() {
		new ItemDocumentImpl(iid, null, descriptions, aliases, statements,
				sitelinks);
	}

	@Test(expected = NullPointerException.class)
	public void descriptionsNotNull() {
		new ItemDocumentImpl(iid, labels, null, aliases, statements, sitelinks);
	}

	@Test(expected = NullPointerException.class)
	public void aliasesNotNull() {
		new ItemDocumentImpl(iid, labels, descriptions, null, statements,
				sitelinks);
	}

	@Test(expected = NullPointerException.class)
	public void statementsNotNull() {
		new ItemDocumentImpl(iid, labels, descriptions, aliases, null,
				sitelinks);
	}

	@Test(expected = NullPointerException.class)
	public void sitelinksNotNull() {
		new ItemDocumentImpl(iid, labels, descriptions, aliases, statements,
				null);
	}

}
