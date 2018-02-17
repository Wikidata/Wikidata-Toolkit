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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.interfaces.*;


public class PropertyDocumentImplTest {

	private final ObjectMapper mapper = new DatamodelMapper("http://example.com/entity/");

	private PropertyIdValue pid = new PropertyIdValueImpl("P2", "http://example.com/entity/");
	private final Statement s = new StatementImpl("MyId", StatementRank.NORMAL,
			new SomeValueSnakImpl(new PropertyIdValueImpl("P42", "http://example.com/entity/")),
			Collections.emptyList(), Collections.emptyList(), pid);
	private final List<StatementGroup> statementGroups = Collections.singletonList(
			new StatementGroupImpl(Collections.singletonList(s))
	);
	private final MonolingualTextValue label = new TermImpl("en", "label");
	private final List<MonolingualTextValue> labelList = Collections.singletonList(label);
	private final MonolingualTextValue desc = new TermImpl("fr", "des");
	private final List<MonolingualTextValue> descList = Collections.singletonList(desc);
	private final MonolingualTextValue alias = new TermImpl("de", "alias");
	private final List<MonolingualTextValue> aliasList = Collections.singletonList(alias);
	private DatatypeIdValue datatypeId = new DatatypeIdImpl(DatatypeIdValue.DT_ITEM);

	private final PropertyDocument pd1 = new PropertyDocumentImpl(pid, labelList, descList, aliasList,
			statementGroups, datatypeId, 1234);
	private final PropertyDocument pd2 = new PropertyDocumentImpl(pid, labelList, descList, aliasList,
			statementGroups, datatypeId, 1234);

	private final String JSON_PROPERTY = "{\"type\":\"property\",\"id\":\"P2\",\"labels\":{\"en\":{\"language\":\"en\",\"value\":\"label\"}},\"descriptions\":{\"fr\":{\"language\":\"fr\",\"value\":\"des\"}},\"aliases\":{\"de\":[{\"language\":\"de\",\"value\":\"alias\"}]},\"claims\":{\"P42\":[{\"rank\":\"normal\",\"id\":\"MyId\",\"mainsnak\":{\"property\":\"P42\",\"snaktype\":\"somevalue\"},\"type\":\"statement\"}]},\"datatype\":\"wikibase-item\",\"lastrevid\":1234}";

	@Test
	public void fieldsAreCorrect() {
		assertEquals(pd1.getPropertyId(), pid);
		assertEquals(pd1.getEntityId(), pid);
		assertEquals(pd1.getLabels(), Collections.singletonMap(label.getLanguageCode(), label));
		assertEquals(pd1.getDescriptions(), Collections.singletonMap(desc.getLanguageCode(), desc));
		assertEquals(
				pd1.getAliases(),
				Collections.singletonMap(alias.getLanguageCode(), Collections.singletonList(alias))
		);
		assertEquals(pd1.getStatementGroups(), statementGroups);
		assertEquals(pd1.getDatatype(), datatypeId);
	}

	@Test
	public void hasStatements() {
		assertTrue(pd1.hasStatement("P42"));
		assertFalse(pd1.hasStatement("P43"));
		assertTrue(pd1.hasStatement(new PropertyIdValueImpl("P42", "http://example.com/entity/")));
		assertFalse(pd1.hasStatement(Datamodel.makePropertyIdValue("P43", "http://example.com/entity/")));
	}

	@Test
	public void findTerms() {
		assertEquals("label", pd1.findLabel("en"));
		assertNull( pd1.findLabel("ja"));
		assertEquals("des", pd1.findDescription("fr"));
		assertNull( pd1.findDescription("ja"));
	}

	@Test
	public void equalityBasedOnContent() {
		PropertyDocument pdDiffLabel = new PropertyDocumentImpl(pid,
				Collections.emptyList(), descList, aliasList,
				statementGroups, datatypeId, 1234);
		PropertyDocument pdDiffDesc = new PropertyDocumentImpl(pid,
				labelList, Collections.emptyList(), aliasList,
				statementGroups, datatypeId, 1234);
		PropertyDocument pdDiffAlias = new PropertyDocumentImpl(pid,
				labelList, descList, Collections.emptyList(),
				statementGroups, datatypeId, 1234);
		PropertyDocument pdDiffStatementGroups = new PropertyDocumentImpl(pid,
				labelList, descList, aliasList,
				Collections.emptyList(), datatypeId, 1234);
		PropertyDocument pdDiffDatatype = new PropertyDocumentImpl(pid,
				labelList, descList, aliasList,
				statementGroups, new DatatypeIdImpl("string"), 1234);
		PropertyDocument pdDiffRevisions = new PropertyDocumentImpl(pid,
				labelList, descList, aliasList,
				statementGroups, datatypeId, 1235);
		ItemDocument id = new ItemDocumentImpl(new ItemIdValueImpl("Q42",
				"foo"), labelList, descList, aliasList,
				Collections.emptyList(), Collections.emptyList(), 1234);

		assertEquals(pd1, pd1);
		assertEquals(pd1, pd2);
		assertNotEquals(pd1, pdDiffLabel);
		assertNotEquals(pd1, pdDiffDesc);
		assertNotEquals(pd1, pdDiffAlias);
		assertNotEquals(pd1, pdDiffStatementGroups);
		assertNotEquals(pd1, pdDiffDatatype);
		assertNotEquals(pd1, pdDiffRevisions);
		assertNotEquals(pd1, id);
		assertNotEquals(pd1, null);
		assertNotEquals(pd1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(pd1.hashCode(), pd2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void idNotNull() {
		new PropertyDocumentImpl(null, labelList, descList, aliasList,
				statementGroups, datatypeId, 1234);
	}

	@Test
	public void labelsCanBeNull() {
		PropertyDocument doc = new PropertyDocumentImpl(pid, null, descList, aliasList,
				statementGroups, datatypeId, 1234);
		assertEquals(Collections.emptyMap(), doc.getLabels());
	}

	@Test
	public void descriptionsCanBeNull() {
		PropertyDocument doc = new PropertyDocumentImpl(pid, labelList, null, aliasList, statementGroups,
				datatypeId, 1234);
		assertEquals(Collections.emptyMap(), doc.getDescriptions());
	}

	@Test
	public void aliasesCanBeNull() {
		PropertyDocument doc = new PropertyDocumentImpl(pid, labelList, descList, null,
				statementGroups, datatypeId, 1234);
		assertEquals(Collections.emptyMap(), doc.getAliases());
	}

	@Test
	public void statementGroupsCanBeNull() {
		PropertyDocument doc = new PropertyDocumentImpl(pid, labelList, descList, aliasList, null,
				datatypeId, 1234);
		assertEquals(Collections.emptyList(), doc.getStatementGroups());
	}

	@Test(expected = NullPointerException.class)
	public void datatypeNotNull() {
		new PropertyDocumentImpl(pid, labelList, descList, aliasList,
				statementGroups, null, 1234);
	}

	@Test(expected = IllegalArgumentException.class)
	public void labelUniquePerLanguage() {
		List<MonolingualTextValue> labels2 = new ArrayList<>(labelList);
		labels2.add(new MonolingualTextValueImpl("Property 42 label duplicate", "en"));

		new PropertyDocumentImpl(pid, labels2, descList, aliasList,
				statementGroups, datatypeId, 1234);
	}

	@Test(expected = IllegalArgumentException.class)
	public void descriptionUniquePerLanguage() {
		List<MonolingualTextValue> descriptions2 = new ArrayList<>(descList);
		descriptions2.add(new MonolingualTextValueImpl("Duplicate desc P42", "fr"));

		new PropertyDocumentImpl(pid, labelList, descriptions2, aliasList,
				statementGroups, datatypeId, 1234);
	}
	
	@Test
	public void testWithRevisionId() {
		assertEquals(1235L, pd1.withRevisionId(1235L).getRevisionId());
		assertEquals(pd1, pd1.withRevisionId(1325L).withRevisionId(pd1.getRevisionId()));
	}
	
	@Test
	public void testWithLabelInNewLanguage() {
		MonolingualTextValue newLabel = new MonolingualTextValueImpl(
				"Propriété P42", "fr");
		PropertyDocument withLabel = pd1.withLabel(newLabel);
		assertEquals("Propriété P42", withLabel.findLabel("fr"));
		assertEquals("label", withLabel.findLabel("en"));
	}

	@Test
	public void testWithOverridenLabel() {
		MonolingualTextValue newLabel = new MonolingualTextValueImpl(
				"The P42 Property", "en");
		PropertyDocument withLabel = pd1.withLabel(newLabel);
		assertEquals("The P42 Property", withLabel.findLabel("en"));
	}
	
	@Test
	public void testWithIdenticalLabel() {
		MonolingualTextValue newLabel = new MonolingualTextValueImpl(
				"label", "en");
		PropertyDocument withLabel = pd1.withLabel(newLabel);
		assertEquals(withLabel, pd1);
	}
	
	@Test
	public void testWithDescriptionInNewLanguage() {
		MonolingualTextValue newDescription = new MonolingualTextValueImpl(
				"Beschreibung", "de");
		PropertyDocument withDescription = pd1.withDescription(newDescription);
		assertEquals("des", withDescription.findDescription("fr"));
		assertEquals("Beschreibung", withDescription.findDescription("de"));
	}

	@Test
	public void testPropertyToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_PROPERTY, mapper.writeValueAsString(pd1));
	}

	@Test
	public void testPropertyToJava() throws IOException {
		assertEquals(pd1, mapper.readValue(JSON_PROPERTY, PropertyDocumentImpl.class));
	}
        
        @Test
	public void testWithOverridenDescription() {
		MonolingualTextValue newDescription = new MonolingualTextValueImpl(
				"une meilleure description", "fr");
		PropertyDocument withDescription = pd1.withDescription(newDescription);
		assertEquals("une meilleure description", withDescription.findDescription("fr"));
	}
	
	@Test
	public void testWithIdenticalDescription() {
		MonolingualTextValue newDescription = new MonolingualTextValueImpl(
				"des", "fr");
		PropertyDocument withDescription = pd1.withDescription(newDescription);
		assertEquals(withDescription, pd1);
	}
	
	@Test
	public void testWithAliasInNewLanguage() {
		MonolingualTextValue newAlias = new MonolingualTextValueImpl(
				"Prop42", "fr");
		PropertyDocument withAliases = pd1.withAliases("fr", Collections.singletonList(newAlias));
		assertEquals(Collections.singletonList(newAlias), withAliases.getAliases().get("fr"));
	}

	@Test
	public void testWithOverridenAliases() {
		MonolingualTextValue newAlias = new MonolingualTextValueImpl(
				"A new alias of P42", "en");

		PropertyDocument withAlias = pd1.withAliases("en", Collections.singletonList(newAlias));
		assertEquals(Collections.singletonList(newAlias), withAlias.getAliases().get("en"));
	}
	
	@Test
	public void testAddStatement() {
		Statement fresh = new StatementImpl("MyFreshId", StatementRank.NORMAL,
				new SomeValueSnakImpl(new PropertyIdValueImpl("P29", "http://example.com/entity/")),
				Collections.emptyList(), Collections.emptyList(), pid);
		Claim claim = fresh.getClaim();
		assertFalse(pd1.hasStatementValue(
				claim.getMainSnak().getPropertyId(),
				claim.getValue()));
		PropertyDocument withStatement = pd1.withStatement(fresh);
		assertTrue(withStatement.hasStatementValue(
				claim.getMainSnak().getPropertyId(),
				claim.getValue()));
	}
	
	@Test
	public void testDeleteStatements() {
		Statement toRemove = statementGroups.get(0).getStatements().get(0);
		PropertyDocument withoutStatement = pd1.withoutStatementIds(Collections.singleton(toRemove.getStatementId()));
		assertNotEquals(withoutStatement, pd1);
	}
	
}
