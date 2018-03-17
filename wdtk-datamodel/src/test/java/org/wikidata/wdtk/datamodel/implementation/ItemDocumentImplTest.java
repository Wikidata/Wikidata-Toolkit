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
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.json.JsonComparator;
import org.wikidata.wdtk.datamodel.implementation.json.JsonTestData;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

public class ItemDocumentImplTest {

	private ItemDocument ir1;
	private ItemDocument ir2;

	private Statement s;

	private ItemIdValue iid;
	private List<StatementGroup> statementGroups;
	private List<SiteLink> sitelinks;

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

	ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);

	/**
	 * Tests the conversion of ItemDocuments containing labels from Pojo to Json
	 */
	@Test
	public void testLabelsToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(
				JsonTestData.getTestItemId().getId(),
				JsonTestData.getTestMltvMap(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				0, JsonTestData.getTestItemId().getSiteIri());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_LABEL,
				result);
	}

	/**
	 * Tests the conversion of ItemDocuments containing labels from Json to Pojo
	 */
	@Test
	public void testLabelToJava() throws
			IOException {
		ItemDocumentImpl result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_LABEL, ItemDocumentImpl.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestMltvMap(), result.getLabels());
	}

	/**
	 * Tests the conversion of ItemDocuments containing descriptions from Pojo
	 * to Json
	 */
	@Test
	public void testDescriptionsToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(
				JsonTestData.getTestItemId().getId(),
				Collections.emptyMap(),
				JsonTestData.getTestMltvMap(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				0, JsonTestData.getTestItemId().getSiteIri());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(
				JsonTestData.JSON_WRAPPED_DESCRIPTIONS, result);
	}

	/**
	 * Tests the conversion of ItemDocuments containing descriptions from Json
	 * to Pojo
	 */
	@Test
	public void testDescriptionsToJava() throws
			IOException {
		ItemDocumentImpl result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_DESCRIPTIONS,
				ItemDocumentImpl.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestMltvMap(), result.getDescriptions());
	}

	@Test
	public void testAliasesToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(
				JsonTestData.getTestItemId().getId(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				JsonTestData.getTestAliases(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				0, JsonTestData.getTestItemId().getSiteIri());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_ALIASES,
				result);
	}

	@Test
	public void testAliasesToJava() throws
			IOException {

		ItemDocumentImpl result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_ALIASES, ItemDocumentImpl.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestAliases(), result.getAliases());
	}

	@Test
	public void testItemIdToJson() throws JsonProcessingException {
		ItemDocumentImpl document = JsonTestData.getEmptyTestItemDocument();

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_ITEMID,
				result);
	}

	@Test
	public void testEmptyItemIdToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(
				ItemIdValue.NULL.getId(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				0, ItemIdValue.NULL.getSiteIri());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_NOITEMID,
				result);
	}

	@Test
	public void testItemIdToJava() throws
			IOException {

		ItemDocumentImpl result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_ITEMID, ItemDocumentImpl.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestItemId(), result.getEntityId());
	}

	@Test
	public void testSiteLinksToJson() throws JsonProcessingException {
		ItemDocumentImpl document = new ItemDocumentImpl(
				JsonTestData.getTestItemId().getId(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				Collections.emptyMap(),
				JsonTestData.getTestSiteLinkMap(),
				0, JsonTestData.getTestItemId().getSiteIri());

		String result = mapper.writeValueAsString(document);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_WRAPPED_SITE_LINK,
				result);
	}

	@Test
	public void testSiteLinksToJava() throws
			IOException {
		ItemDocumentImpl result = mapper.readValue(
				JsonTestData.JSON_WRAPPED_SITE_LINK, ItemDocumentImpl.class);

		assertNotNull(result);
		assertEquals(JsonTestData.getTestSiteLinkMap(), result.getSiteLinks());
	}

	@Test
	public void testEmptyArraysForTerms() throws IOException {
		ItemDocumentImpl result = mapper.readerFor(ItemDocumentImpl.class)
				.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
				.readValue(JsonTestData.JSON_EMPTY_ARRAY_AS_CONTAINER);

		assertNotNull(result);
		assertNotNull(result.getLabels());
		assertNotNull(result.getDescriptions());
		assertNotNull(result.getAliases());
		assertNotNull(result.getAllStatements());
		assertNotNull(result.getSiteLinks());
	}

	@Test
	public void testGenerationFromOtherItemDocument() {
		ItemDocumentImpl fullDocument = new ItemDocumentImpl(
				JsonTestData.getTestItemId().getId(),
				JsonTestData.getTestMltvMap(),
				JsonTestData.getTestMltvMap(),
				JsonTestData.getTestAliases(),
				Collections.emptyMap(),
				JsonTestData.getTestSiteLinkMap(),
				0, JsonTestData.getTestItemId().getSiteIri());

		assertEquals(fullDocument.getAliases(), JsonTestData.getTestAliases());
		assertEquals(fullDocument.getDescriptions(),
				JsonTestData.getTestMltvMap());
		assertEquals(fullDocument.getLabels(), JsonTestData.getTestMltvMap());
		assertEquals(fullDocument.getItemId(), JsonTestData.getTestItemId());
		assertEquals(fullDocument.getEntityId(), JsonTestData.getTestItemId());
		assertEquals(fullDocument.getItemId().getId(), fullDocument.getJsonId());

		DatamodelConverter converter = new DatamodelConverter(
				new DataObjectFactoryImpl());
		ItemDocument copy = converter.copy(fullDocument);

		assertEquals(fullDocument, copy);
	}

}
