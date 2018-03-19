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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.implementation.json.JsonTestData;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

public class PropertyDocumentImplTest {

	private PropertyDocument pd1;
	private PropertyDocument pd2;

	private PropertyIdValue pid;
	private Map<String, MonolingualTextValue> labelMap;
	private Map<String, MonolingualTextValue> descriptionMap;
	private Map<String, List<MonolingualTextValue>> aliasMap;
	private List<MonolingualTextValue> labels;
	private List<MonolingualTextValue> descriptions;
	private List<MonolingualTextValue> aliases;
	private List<StatementGroup> statementGroups;
	private DatatypeIdValue datatypeId;

	@Before
	public void setUp() throws Exception {
		pid = DataObjectFactoryImplTest.getTestPropertyIdValue(2);

		labelMap = new HashMap<>();
		labelMap.put("en", new MonolingualTextValueImpl("Property 42", "en"));
		labels = new ArrayList<>();
		labels.add(new MonolingualTextValueImpl("Property 42", "en"));

		descriptionMap = new HashMap<>();
		descriptionMap.put("de", new MonolingualTextValueImpl(
				"Dies ist Property 42.", "de"));
		descriptions = new ArrayList<>();
		descriptions.add(new MonolingualTextValueImpl("Dies ist Property 42.",
				"de"));

		MonolingualTextValue alias1 = new MonolingualTextValueImpl(
				"An alias of P42", "en");
		MonolingualTextValue alias2 = new MonolingualTextValueImpl(
				"Ein Alias von P42", "de");
		MonolingualTextValue alias3 = new MonolingualTextValueImpl(
				"Another alias of P42", "en");
		aliasMap = new HashMap<>();
		List<MonolingualTextValue> enAliases = new ArrayList<>();
		enAliases.add(alias1);
		enAliases.add(alias3);
		aliasMap.put("en", enAliases);
		aliasMap.put("de",
				Collections.singletonList(alias2));
		aliases = new ArrayList<>();
		aliases.add(alias1);
		aliases.add(alias2);
		aliases.add(alias3);

		statementGroups = DataObjectFactoryImplTest.getTestStatementGroups(2,
				10, 3, EntityIdValue.ET_PROPERTY);

		datatypeId = new DatatypeIdImpl(DatatypeIdValue.DT_ITEM);

		pd1 = new PropertyDocumentImpl(pid, labels, descriptions, aliases,
				statementGroups, datatypeId, 1234);
		pd2 = new PropertyDocumentImpl(pid, labels, descriptions, aliases,
				statementGroups, datatypeId, 1234);
	}

	@Test
	public void fieldsAreCorrect() {
		assertEquals(pd1.getPropertyId(), pid);
		assertEquals(pd1.getEntityId(), pid);
		assertEquals(pd1.getLabels(), labelMap);
		assertEquals(pd1.getDescriptions(), descriptionMap);
		assertEquals(pd1.getAliases(), aliasMap);
		assertEquals(pd1.getDatatype(), datatypeId);
	}

	@Test
	public void hasStatements() {
		assertTrue(pd1.hasStatement("P411"));
		assertFalse(pd1.hasStatement("P1234"));
		assertTrue(pd1.hasStatement(Datamodel.makePropertyIdValue("P411",
				"foo:")));
		assertFalse(pd1.hasStatement(Datamodel.makePropertyIdValue("P411",
				"bar:")));
	}

	@Test
	public void findTerms() {
		assertEquals("Property 42", pd1.findLabel("en"));
		assertEquals(null, pd1.findLabel("ja"));
		assertEquals("Dies ist Property 42.", pd1.findDescription("de"));
		assertEquals(null, pd1.findDescription("ja"));
	}

	@Test
	public void equalityBasedOnContent() {
		PropertyDocument pdDiffSubject = new PropertyDocumentImpl(
				DataObjectFactoryImplTest.getTestPropertyIdValue(3), labels,
				descriptions, aliases,
				DataObjectFactoryImplTest.getTestStatementGroups(3, 10, 3,
						EntityIdValue.ET_PROPERTY), datatypeId, 1234);
		PropertyDocument pdDiffLabels = new PropertyDocumentImpl(pid,
				Collections. emptyList(), descriptions,
				aliases, statementGroups, datatypeId, 1234);
		PropertyDocument pdDiffDescriptions = new PropertyDocumentImpl(pid,
				labels, Collections. emptyList(),
				aliases, statementGroups, datatypeId, 1234);
		PropertyDocument pdDiffAliases = new PropertyDocumentImpl(pid, labels,
				descriptions, Collections. emptyList(),
				statementGroups, datatypeId, 1234);
		PropertyDocument pdDiffStatements = new PropertyDocumentImpl(pid,
				labels, descriptions, aliases,
				Collections. emptyList(), datatypeId, 1234);
		PropertyDocument pdDiffDatatype = new PropertyDocumentImpl(pid, labels,
				descriptions, aliases, statementGroups, new DatatypeIdImpl(
						DatatypeIdValue.DT_STRING), 1234);

		ItemDocument id = new ItemDocumentImpl(new ItemIdValueImpl("Q42",
				"foo"), labels, descriptions, aliases,
				Collections. emptyList(),
				Collections. emptyList(), 1234);

		assertEquals(pd1, pd1);
		assertEquals(pd1, pd2);
		assertNotEquals(pd1, pdDiffSubject);
		assertNotEquals(pd1, pdDiffLabels);
		assertNotEquals(pd1, pdDiffDescriptions);
		assertNotEquals(pd1, pdDiffAliases);
		assertNotEquals(pd1, pdDiffStatements);
		assertNotEquals(pd1, pdDiffDatatype);
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
		new PropertyDocumentImpl(null, labels, descriptions, aliases,
				statementGroups, datatypeId, 1234);
	}

	@Test
	public void labelsCanBeNull() {
		PropertyDocument doc = new PropertyDocumentImpl(pid, null, descriptions, aliases,
				statementGroups, datatypeId, 1234);
		assertEquals(Collections.emptyMap(), doc.getLabels());
	}

	@Test
	public void descriptionsCanBeNull() {
		PropertyDocument doc = new PropertyDocumentImpl(pid, labels, null, aliases, statementGroups,
				datatypeId, 1234);
		assertEquals(Collections.emptyMap(), doc.getDescriptions());
	}

	@Test
	public void aliasesCanBeNull() {
		PropertyDocument doc = new PropertyDocumentImpl(pid, labels, descriptions, null,
				statementGroups, datatypeId, 1234);
		assertEquals(Collections.emptyMap(), doc.getAliases());
	}

	@Test
	public void statementGroupsCanBeNull() {
		PropertyDocument doc = new PropertyDocumentImpl(pid, labels, descriptions, aliases, null,
				datatypeId, 1234);
		assertEquals(Collections.emptyList(), doc.getStatementGroups());
	}

	@Test(expected = NullPointerException.class)
	public void datatypeNotNull() {
		new PropertyDocumentImpl(pid, labels, descriptions, aliases,
				statementGroups, null, 1234);
	}

	@Test(expected = IllegalArgumentException.class)
	public void labelUniquePerLanguage() {
		List<MonolingualTextValue> labels2 = new ArrayList<>(
				labels);
		labels2.add(new MonolingualTextValueImpl("Property 42 label duplicate",
				"en"));

		new PropertyDocumentImpl(pid, labels2, descriptions, aliases,
				statementGroups, null, 1234);
	}

	@Test(expected = IllegalArgumentException.class)
	public void descriptionUniquePerLanguage() {
		List<MonolingualTextValue> descriptions2 = new ArrayList<>(
				descriptions);
		descriptions2.add(new MonolingualTextValueImpl(
				"Noch eine Beschreibung fuer P42", "de"));

		new PropertyDocumentImpl(pid, labels, descriptions2, aliases,
				statementGroups, null, 1234);
	}

	@Test
	public void testFullDocumentSetup() {
		PropertyDocumentImpl fullDocument = new PropertyDocumentImpl(
				JsonTestData.getTestPropertyId().getId(),
				JsonTestData.getTestMltvMap(),
				JsonTestData.getTestMltvMap(),
				JsonTestData.getTestAliases(),
				Collections.emptyMap(),
				"quantity",
				0, JsonTestData.getTestItemId().getSiteIri());

		assertEquals(fullDocument.getAliases(), JsonTestData.getTestAliases());
		assertEquals(fullDocument.getDescriptions(),
				JsonTestData.getTestMltvMap());
		assertEquals(fullDocument.getLabels(), JsonTestData.getTestMltvMap());
		assertEquals(fullDocument.getPropertyId(),
				JsonTestData.getTestPropertyId());
		assertEquals(fullDocument.getEntityId(),
				JsonTestData.getTestPropertyId());
		assertEquals(fullDocument.getDatatype(),
				Datamodel.makeDatatypeIdValue(DatatypeIdValue.DT_QUANTITY));
		assertEquals(fullDocument.getPropertyId().getId(),
				fullDocument.getJsonId());
	}
}
