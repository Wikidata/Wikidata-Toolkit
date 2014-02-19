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
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

public class PropertyDocumentImplTest {

	PropertyDocument pr1;
	PropertyDocument pr2;

	PropertyIdValue pid;
	Map<String, MonolingualTextValue> labelMap;
	Map<String, MonolingualTextValue> descriptionMap;
	Map<String, List<MonolingualTextValue>> aliasMap;
	List<MonolingualTextValue> labels;
	List<MonolingualTextValue> descriptions;
	List<MonolingualTextValue> aliases;
	DatatypeIdValue datatypeId;

	@Before
	public void setUp() throws Exception {
		pid = new PropertyIdValueImpl("P42", "http://wikibase.org/entity/");

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

		datatypeId = new DatatypeIdImpl(DatatypeIdValue.DT_ITEM);

		pr1 = new PropertyDocumentImpl(pid, labels, descriptions, aliases,
				datatypeId);
		pr2 = new PropertyDocumentImpl(pid, labels, descriptions, aliases,
				datatypeId);
	}

	@Test
	public void fieldsAreCorrect() {
		assertEquals(pr1.getPropertyId(), pid);
		assertEquals(pr1.getEntityId(), pid);
		assertEquals(pr1.getLabels(), labelMap);
		assertEquals(pr1.getDescriptions(), descriptionMap);
		assertEquals(pr1.getAliases(), aliasMap);
		assertEquals(pr1.getDatatype(), datatypeId);
	}

	@Test
	public void valueEqualityBasedOnContent() {
		PropertyDocument pr3 = new PropertyDocumentImpl(
				new PropertyIdValueImpl("P43", "http://wikibase.org/entity/"),
				labels, descriptions, aliases, datatypeId);
		PropertyDocument pr4 = new PropertyDocumentImpl(pid,
				Collections.<MonolingualTextValue> emptyList(), descriptions,
				aliases, datatypeId);
		PropertyDocument pr5 = new PropertyDocumentImpl(pid, labels,
				Collections.<MonolingualTextValue> emptyList(), aliases,
				datatypeId);
		PropertyDocument pr6 = new PropertyDocumentImpl(pid, labels,
				descriptions, Collections.<MonolingualTextValue> emptyList(),
				datatypeId);
		PropertyDocument pr7 = new PropertyDocumentImpl(pid, labels,
				descriptions, aliases, new DatatypeIdImpl(
						DatatypeIdValue.DT_STRING));

		ItemDocument ir = new ItemDocumentImpl(
				new ItemIdValueImpl("Q42", "foo"), labels, descriptions,
				aliases, Collections.<StatementGroup> emptyList(),
				Collections.<String, SiteLink> emptyMap());

		assertEquals(pr1, pr1);
		assertEquals(pr1, pr2);
		assertThat(pr1, not(equalTo(pr3)));
		assertThat(pr1, not(equalTo(pr4)));
		assertThat(pr1, not(equalTo(pr5)));
		assertThat(pr1, not(equalTo(pr6)));
		assertThat(pr1, not(equalTo(pr7)));
		assertFalse(pr1.equals(ir));
		assertThat(pr1, not(equalTo(null)));
		assertFalse(pr1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(pr1.hashCode(), pr2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void idNotNull() {
		new PropertyDocumentImpl(null, labels, descriptions, aliases,
				datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void labelsNotNull() {
		new PropertyDocumentImpl(pid, null, descriptions, aliases, datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void descriptionsNotNull() {
		new PropertyDocumentImpl(pid, labels, null, aliases, datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void aliasesNotNull() {
		new PropertyDocumentImpl(pid, labels, descriptions, null, datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void datatypeNotNull() {
		new PropertyDocumentImpl(pid, labels, descriptions, aliases, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void labelUniquePerLanguage() {
		List<MonolingualTextValue> labels2 = new ArrayList<MonolingualTextValue>(
				labels);
		labels2.add(new MonolingualTextValueImpl("Property 42 label duplicate",
				"en"));

		new PropertyDocumentImpl(pid, labels2, descriptions, aliases, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void descriptionUniquePerLanguage() {
		List<MonolingualTextValue> descriptions2 = new ArrayList<MonolingualTextValue>(
				descriptions);
		descriptions2.add(new MonolingualTextValueImpl(
				"Noch eine Beschreibung fuer P42", "de"));

		new PropertyDocumentImpl(pid, labels, descriptions2, aliases, null);
	}

}
