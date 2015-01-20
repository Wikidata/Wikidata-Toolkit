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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

public class PropertyDocumentImplTest {

	PropertyDocument pd1;
	PropertyDocument pd2;

	PropertyIdValue pid;
	Map<String, MonolingualTextValue> labelMap;
	Map<String, MonolingualTextValue> descriptionMap;
	Map<String, List<MonolingualTextValue>> aliasMap;
	List<MonolingualTextValue> labels;
	List<MonolingualTextValue> descriptions;
	List<MonolingualTextValue> aliases;
	List<StatementGroup> statementGroups;
	DatatypeIdValue datatypeId;

	@Before
	public void setUp() throws Exception {
		pid = DataObjectFactoryImplTest.getTestPropertyIdValue(2);

		labelMap = new HashMap<String, MonolingualTextValue>();
		labelMap.put("en", new MonolingualTextValueImpl("Property 42", "en"));
		labels = new ArrayList<MonolingualTextValue>();
		labels.add(new MonolingualTextValueImpl("Property 42", "en"));

		descriptionMap = new HashMap<String, MonolingualTextValue>();
		descriptionMap.put("de", new MonolingualTextValueImpl(
				"Dies ist Property 42.", "de"));
		descriptions = new ArrayList<MonolingualTextValue>();
		descriptions.add(new MonolingualTextValueImpl("Dies ist Property 42.",
				"de"));

		MonolingualTextValue alias1 = new MonolingualTextValueImpl(
				"An alias of P42", "en");
		MonolingualTextValue alias2 = new MonolingualTextValueImpl(
				"Ein Alias von P42", "de");
		MonolingualTextValue alias3 = new MonolingualTextValueImpl(
				"Another alias of P42", "en");
		aliasMap = new HashMap<String, List<MonolingualTextValue>>();
		List<MonolingualTextValue> enAliases = new ArrayList<MonolingualTextValue>();
		enAliases.add(alias1);
		enAliases.add(alias3);
		aliasMap.put("en", enAliases);
		aliasMap.put("de",
				Collections.<MonolingualTextValue> singletonList(alias2));
		aliases = new ArrayList<MonolingualTextValue>();
		aliases.add(alias1);
		aliases.add(alias2);
		aliases.add(alias3);

		statementGroups = DataObjectFactoryImplTest.getTestStatementGroups(2,
				10, 3, EntityIdValue.ET_PROPERTY);

		datatypeId = new DatatypeIdImpl(DatatypeIdValue.DT_ITEM);

		pd1 = new PropertyDocumentImpl(pid, labels, descriptions, aliases,
				statementGroups, datatypeId);
		pd2 = new PropertyDocumentImpl(pid, labels, descriptions, aliases,
				statementGroups, datatypeId);
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
	public void equalityBasedOnContent() {
		PropertyDocument pdDiffSubject = new PropertyDocumentImpl(
				DataObjectFactoryImplTest.getTestPropertyIdValue(3), labels,
				descriptions, aliases,
				DataObjectFactoryImplTest.getTestStatementGroups(3, 10, 3,
						EntityIdValue.ET_PROPERTY), datatypeId);
		PropertyDocument pdDiffLabels = new PropertyDocumentImpl(pid,
				Collections.<MonolingualTextValue> emptyList(), descriptions,
				aliases, statementGroups, datatypeId);
		PropertyDocument pdDiffDescriptions = new PropertyDocumentImpl(pid,
				labels, Collections.<MonolingualTextValue> emptyList(),
				aliases, statementGroups, datatypeId);
		PropertyDocument pdDiffAliases = new PropertyDocumentImpl(pid, labels,
				descriptions, Collections.<MonolingualTextValue> emptyList(),
				statementGroups, datatypeId);
		PropertyDocument pdDiffStatements = new PropertyDocumentImpl(pid,
				labels, descriptions, aliases,
				Collections.<StatementGroup> emptyList(), datatypeId);
		PropertyDocument pdDiffDatatype = new PropertyDocumentImpl(pid, labels,
				descriptions, aliases, statementGroups, new DatatypeIdImpl(
						DatatypeIdValue.DT_STRING));

		ItemDocument id = new ItemDocumentImpl(
				ItemIdValueImpl.create("Q42", "foo"), labels,
				descriptions, aliases,
				Collections.<StatementGroup> emptyList(),
				Collections.<String, SiteLink> emptyMap());

		assertEquals(pd1, pd1);
		assertEquals(pd1, pd2);
		assertThat(pd1, not(equalTo(pdDiffSubject)));
		assertThat(pd1, not(equalTo(pdDiffLabels)));
		assertThat(pd1, not(equalTo(pdDiffDescriptions)));
		assertThat(pd1, not(equalTo(pdDiffAliases)));
		assertThat(pd1, not(equalTo(pdDiffStatements)));
		assertThat(pd1, not(equalTo(pdDiffDatatype)));
		assertFalse(pd1.equals(id));
		assertThat(pd1, not(equalTo(null)));
		assertFalse(pd1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(pd1.hashCode(), pd2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void idNotNull() {
		new PropertyDocumentImpl(null, labels, descriptions, aliases,
				statementGroups, datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void labelsNotNull() {
		new PropertyDocumentImpl(pid, null, descriptions, aliases,
				statementGroups, datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void descriptionsNotNull() {
		new PropertyDocumentImpl(pid, labels, null, aliases, statementGroups,
				datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void aliasesNotNull() {
		new PropertyDocumentImpl(pid, labels, descriptions, null,
				statementGroups, datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void statementGroupsNotNull() {
		new PropertyDocumentImpl(pid, labels, descriptions, aliases, null,
				datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void datatypeNotNull() {
		new PropertyDocumentImpl(pid, labels, descriptions, aliases,
				statementGroups, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void labelUniquePerLanguage() {
		List<MonolingualTextValue> labels2 = new ArrayList<MonolingualTextValue>(
				labels);
		labels2.add(new MonolingualTextValueImpl("Property 42 label duplicate",
				"en"));

		new PropertyDocumentImpl(pid, labels2, descriptions, aliases,
				statementGroups, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void descriptionUniquePerLanguage() {
		List<MonolingualTextValue> descriptions2 = new ArrayList<MonolingualTextValue>(
				descriptions);
		descriptions2.add(new MonolingualTextValueImpl(
				"Noch eine Beschreibung fuer P42", "de"));

		new PropertyDocumentImpl(pid, labels, descriptions2, aliases,
				statementGroups, null);
	}

}
