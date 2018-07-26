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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class ItemDocumentImplTest {

	private final ObjectMapper mapper = new DatamodelMapper("http://example.com/entity/");

	private final ItemIdValue iid = new ItemIdValueImpl("Q42", "http://example.com/entity/");
	private final Statement s = new StatementImpl("MyId", StatementRank.NORMAL,
			new SomeValueSnakImpl(new PropertyIdValueImpl("P42", "http://example.com/entity/")),
			Collections.emptyList(), Collections.emptyList(), iid);
	private final List<StatementGroup> statementGroups = Collections.singletonList(
			new StatementGroupImpl(Collections.singletonList(s))
	);
	private final MonolingualTextValue label = new TermImpl("en", "label");
	private final List<MonolingualTextValue> labelList = Collections.singletonList(label);
	private final MonolingualTextValue desc = new MonolingualTextValueImpl("des", "fr");
	private final List<MonolingualTextValue> descList = Collections.singletonList(desc);
	private final MonolingualTextValue alias = new MonolingualTextValueImpl("alias", "de");
	private final List<MonolingualTextValue> aliasList = Collections.singletonList(alias);
	private final List<SiteLink> sitelinks = Collections.singletonList(
			new SiteLinkImpl("Douglas Adams", "enwiki", Collections.emptyList())
	);

	private final ItemDocument ir1 = new ItemDocumentImpl(iid,
			labelList, descList, aliasList,
			statementGroups, sitelinks, 1234);
	private final ItemDocument ir2 = new ItemDocumentImpl(iid,
			labelList, descList, aliasList,
			statementGroups, sitelinks, 1234);

	private final String JSON_ITEM_LABEL = "{\"type\":\"item\",\"id\":\"Q42\",\"labels\":{\"en\":{\"language\":\"en\",\"value\":\"label\"}},\"descriptions\":{},\"aliases\":{},\"claims\":{},\"sitelinks\":{}}";
	private final String JSON_ITEM_DESCRIPTION = "{\"type\":\"item\",\"id\":\"Q42\",\"labels\":{},\"descriptions\":{\"fr\":{\"language\":\"fr\",\"value\":\"des\"}},\"aliases\":{},\"claims\":{},\"sitelinks\":{}}";
	private final String JSON_ITEM_ALIASES = "{\"type\":\"item\",\"id\":\"Q42\",\"labels\":{},\"descriptions\":{},\"aliases\":{\"de\":[{\"language\":\"de\",\"value\":\"alias\"}]},\"claims\":{},\"sitelinks\":{}}";
	private final String JSON_ITEM_STATEMENTS = "{\"type\":\"item\",\"id\":\"Q42\",\"labels\":{},\"descriptions\":{},\"aliases\":{},\"claims\":{\"P42\":[{\"rank\":\"normal\",\"id\":\"MyId\",\"mainsnak\":{\"property\":\"P42\",\"snaktype\":\"somevalue\"},\"type\":\"statement\"}]},\"sitelinks\":{}}";
	private final String JSON_ITEM_SITELINKS = "{\"type\":\"item\",\"id\":\"Q42\",\"labels\":{},\"descriptions\":{},\"aliases\":{},\"claims\":{},\"sitelinks\":{\"enwiki\":{\"title\":\"Douglas Adams\",\"site\":\"enwiki\",\"badges\":[]}}}";
	private final String JSON_ITEM_EMPTY_ARRAYS = "{\"type\":\"item\",\"id\":\"Q42\",\"labels\":[],\"descriptions\":[],\"aliases\":[],\"claims\":[],\"sitelinks\":[]}";

	@Test
	public void fieldsAreCorrect() {
		assertEquals(ir1.getItemId(), iid);
		assertEquals(ir1.getEntityId(), iid);
		assertEquals(ir1.getLabels(), Collections.singletonMap(label.getLanguageCode(), label));
		assertEquals(ir1.getDescriptions(), Collections.singletonMap(desc.getLanguageCode(), desc));
		assertEquals(
				ir1.getAliases(),
				Collections.singletonMap(alias.getLanguageCode(), Collections.singletonList(alias))
		);
		assertEquals(ir1.getStatementGroups(), statementGroups);
		assertEquals(new ArrayList<>(ir1.getSiteLinks().values()), sitelinks);
	}

	@Test
	public void findTerms() {
		assertEquals("label", ir1.findLabel("en"));
		assertNull( ir1.findLabel("ja"));
		assertEquals("des", ir1.findDescription("fr"));
		assertNull( ir1.findDescription("ja"));
	}

	@Test
	public void equalityBasedOnContent() {
		ItemDocument irDiffLabel = new ItemDocumentImpl(iid,
				Collections.emptyList(), descList, aliasList,
				statementGroups, sitelinks, 1234);
		ItemDocument irDiffDesc = new ItemDocumentImpl(iid,
				labelList, Collections.emptyList(), aliasList,
				statementGroups, sitelinks, 1234);
		ItemDocument irDiffAlias = new ItemDocumentImpl(iid,
				labelList, descList, Collections.emptyList(),
				statementGroups, sitelinks, 1234);
		ItemDocument irDiffStatementGroups = new ItemDocumentImpl(iid,
				labelList, descList, aliasList,
				Collections.emptyList(), sitelinks, 1234);
		ItemDocument irDiffSiteLinks = new ItemDocumentImpl(iid,
				labelList, descList, aliasList,
				statementGroups, Collections.emptyList(),
				1234);
		ItemDocument irDiffRevisions = new ItemDocumentImpl(iid,
				labelList, descList, aliasList,
				statementGroups, sitelinks, 1235);

		PropertyDocument pr = new PropertyDocumentImpl(
				new PropertyIdValueImpl("P42", "foo"),
				labelList, descList, aliasList,
				Collections.emptyList(),
				new DatatypeIdImpl(DatatypeIdValue.DT_STRING), 1234);

		// we need to use empty lists of Statement groups to test inequality
		// based on different item ids with all other data being equal
		ItemDocument irDiffItemIdValue = new ItemDocumentImpl(
				new ItemIdValueImpl("Q23", "http://example.org/"),
				labelList, descList, aliasList,
				Collections.emptyList(), sitelinks, 1234);

		assertEquals(ir1, ir1);
		assertEquals(ir1, ir2);
		assertNotEquals(ir1, irDiffLabel);
		assertNotEquals(ir1, irDiffDesc);
		assertNotEquals(ir1, irDiffAlias);
		assertNotEquals(ir1, irDiffStatementGroups);
		assertNotEquals(ir1, irDiffSiteLinks);
		assertNotEquals(ir1, irDiffRevisions);
		assertNotEquals(irDiffStatementGroups, irDiffItemIdValue);
		assertNotEquals(ir1, pr);
		assertNotEquals(ir1, null);
		assertNotEquals(ir1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(ir1.hashCode(), ir2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void idNotNull() {
		new ItemDocumentImpl(null,
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				statementGroups, sitelinks, 1234);
	}

	@Test
	public void labelsCanBeNull() {
		ItemDocument doc = new ItemDocumentImpl(iid, null,
				Collections.emptyList(),
				Collections.emptyList(),
				statementGroups, sitelinks, 1234);
		assertTrue(doc.getLabels().isEmpty());
	}

	@Test
	public void descriptionsNotNull() {
		ItemDocument doc = new ItemDocumentImpl(iid,
				Collections.emptyList(), null,
				Collections.emptyList(),
				statementGroups, sitelinks, 1234);
		assertTrue(doc.getDescriptions().isEmpty());
	}

	@Test
	public void aliasesCanBeNull() {
		ItemDocument doc =new ItemDocumentImpl(iid,
				Collections.emptyList(),
				Collections.emptyList(), null,
				statementGroups, sitelinks, 1234);
		assertTrue(doc.getAliases().isEmpty());
	}

	@Test
	public void statementGroupsCanBeNull() {
		ItemDocument doc = new ItemDocumentImpl(iid,
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(), null,
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
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				statementGroups2, sitelinks, 1234);
	}

	@Test(expected = NullPointerException.class)
	public void sitelinksNotNull() {
		new ItemDocumentImpl(iid,
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				statementGroups, null, 1234);
	}

	@Test
	public void iterateOverAllStatements() {
		Iterator<Statement> statements = ir1.getAllStatements();

		assertTrue(statements.hasNext());
		assertEquals(s, statements.next());
		assertFalse(statements.hasNext());
	}
	
	@Test
	public void testWithRevisionId() {
		assertEquals(1235L, ir1.withRevisionId(1235L).getRevisionId());
		assertEquals(ir1, ir1.withRevisionId(1325L).withRevisionId(ir1.getRevisionId()));
	}
	
	@Test
	public void testWithLabelInNewLanguage() {
		MonolingualTextValue newLabel = new MonolingualTextValueImpl(
				"Item Q42", "fr");
		ItemDocument withLabel = ir1.withLabel(newLabel);
		assertEquals("Item Q42", withLabel.findLabel("fr"));
	}
	
	@Test
	public void testWithDescriptionInNewLanguage() {
		MonolingualTextValue newDescription = new MonolingualTextValueImpl(
				"l'item 42 bien connu", "fr");
		ItemDocument withDescription = ir1.withDescription(newDescription);
		assertEquals("l'item 42 bien connu", withDescription.findDescription("fr"));
	}

	@Test
	public void testWithOverridenDescription() {
		MonolingualTextValue newDescription = new MonolingualTextValueImpl(
				"eine viel bessere Beschreibung", "de");
		ItemDocument withDescription = ir1.withDescription(newDescription);
		assertEquals("eine viel bessere Beschreibung", withDescription.findDescription("de"));
	}
	
	@Test
	public void testWithAliasInNewLanguage() {
		MonolingualTextValue newAlias = new MonolingualTextValueImpl(
				"Item42", "fr");
		ItemDocument withAliases = ir1.withAliases("fr", Collections.singletonList(newAlias));
		assertEquals(Collections.singletonList(newAlias), withAliases.getAliases().get("fr"));
	}

	@Test
	public void testWithOverridenAliases() {
		MonolingualTextValue newAlias = new MonolingualTextValueImpl(
				"A new alias of Q42", "en");

		ItemDocument withAlias = ir1.withAliases("en", Collections.singletonList(newAlias));
		assertEquals(Collections.singletonList(newAlias), withAlias.getAliases().get("en"));
	}
	
	@Test
	public void testAddStatement() {
		Statement fresh = new StatementImpl("MyFreshId", StatementRank.NORMAL,
				new SomeValueSnakImpl(new PropertyIdValueImpl("P29", "http://example.com/entity/")),
				Collections.emptyList(), Collections.emptyList(), iid);
		Claim claim = fresh.getClaim();
		assertFalse(ir1.hasStatementValue(
				claim.getMainSnak().getPropertyId(),
				claim.getValue()));
		ItemDocument withStatement = ir1.withStatement(fresh);
		assertTrue(withStatement.hasStatementValue(
				claim.getMainSnak().getPropertyId(),
				claim.getValue()));
	}
	
	@Test
	public void testDeleteStatements() {
		Statement toRemove = statementGroups.get(0).getStatements().get(0);
		ItemDocument withoutStatement = ir1.withoutStatementIds(Collections.singleton(toRemove.getStatementId()));
		assertNotEquals(withoutStatement, ir1);
	}

	@Test
	public void testLabelsToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(iid,
				labelList, Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), 0);
		JsonComparator.compareJsonStrings(JSON_ITEM_LABEL, mapper.writeValueAsString(document));
	}

	@Test
	public void testLabelToJava() throws IOException {
		ItemDocumentImpl document = new ItemDocumentImpl(iid,
				labelList, Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), 0);
		assertEquals(document, mapper.readValue(JSON_ITEM_LABEL, EntityDocumentImpl.class));
	}

	@Test
	public void testDescriptionsToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(iid,
				Collections.emptyList(), descList, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), 0);
		JsonComparator.compareJsonStrings(JSON_ITEM_DESCRIPTION, mapper.writeValueAsString(document));
	}

	@Test
	public void testDescriptionsToJava() throws IOException {
		ItemDocumentImpl document = new ItemDocumentImpl(iid,
				Collections.emptyList(), descList, Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), 0);
		assertEquals(document, mapper.readValue(JSON_ITEM_DESCRIPTION, EntityDocumentImpl.class));
	}

	@Test
	public void testAliasesToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(iid,
				Collections.emptyList(), Collections.emptyList(), aliasList,
				Collections.emptyList(), Collections.emptyList(), 0);
		JsonComparator.compareJsonStrings(JSON_ITEM_ALIASES, mapper.writeValueAsString(document));
	}

	@Test
	public void testAliasesToJava() throws IOException {
		ItemDocumentImpl document = new ItemDocumentImpl(iid,
				Collections.emptyList(), Collections.emptyList(), aliasList,
				Collections.emptyList(), Collections.emptyList(), 0);
		assertEquals(document, mapper.readValue(JSON_ITEM_ALIASES, ItemDocumentImpl.class));
	}

	@Test
	public void testStatementsToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(iid,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				statementGroups, Collections.emptyList(), 0);
		JsonComparator.compareJsonStrings(JSON_ITEM_STATEMENTS, mapper.writeValueAsString(document));
	}

	@Test
	public void testStatementsToJava() throws IOException {
		ItemDocumentImpl document = new ItemDocumentImpl(iid,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				statementGroups, Collections.emptyList(), 0);
		assertEquals(document, mapper.readValue(JSON_ITEM_STATEMENTS, ItemDocumentImpl.class));
	}

	@Test
	public void testSiteLinksToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(iid,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), sitelinks, 0);
		JsonComparator.compareJsonStrings(JSON_ITEM_SITELINKS, mapper.writeValueAsString(document));
	}

	@Test
	public void testSiteLinksToJava() throws IOException {
		ItemDocumentImpl document = new ItemDocumentImpl(iid,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), sitelinks, 0);
		assertEquals(document, mapper.readValue(JSON_ITEM_SITELINKS, ItemDocumentImpl.class));
	}

	/**
	 * Checks support of wrong serialization of empty object as empty array
	 */
	@Test
	public void testEmptyArraysForTerms() throws IOException {
		ItemDocumentImpl document = new ItemDocumentImpl(iid,
				Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
				Collections.emptyList(), Collections.emptyList(), 0);

		assertEquals(document, mapper.readerFor(ItemDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
				.readValue(JSON_ITEM_EMPTY_ARRAYS)
		);
	}

}
