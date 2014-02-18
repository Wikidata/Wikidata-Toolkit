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
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class PropertyDocumentImplTest {

	PropertyDocument pr1;
	PropertyDocument pr2;

	PropertyIdValue pid;
	Map<String, String> labels;
	Map<String, String> descriptions;
	Map<String, List<String>> aliases;
	DatatypeId datatypeId;

	@Before
	public void setUp() throws Exception {
		pid = new PropertyIdValueImpl("P42", "http://wikibase.org/entity/");
		labels = new HashMap<String, String>();
		labels.put("en", "Property 42");
		descriptions = new HashMap<String, String>();
		descriptions.put("de", "Dies ist Property 42.");
		aliases = new HashMap<String, List<String>>();
		aliases.put("en", Collections.<String> singletonList("An alias of P42"));
		datatypeId = new DatatypeIdImpl(DatatypeId.DT_ITEM);

		pr1 = new PropertyDocumentImpl(pid, labels, descriptions, aliases,
				datatypeId);
		pr2 = new PropertyDocumentImpl(pid, labels, descriptions, aliases,
				datatypeId);
	}

	@Test
	public void fieldsAreCorrect() {
		assertEquals(pr1.getPropertyId(), pid);
		assertEquals(pr1.getEntityId(), pid);
		assertEquals(pr1.getLabels(), labels);
		assertEquals(pr1.getDescriptions(), descriptions);
		assertEquals(pr1.getAliases(), aliases);
		assertEquals(pr1.getDatatype(), datatypeId);
	}

	@Test
	public void valueEqualityBasedOnContent() {
		PropertyDocument pr3 = new PropertyDocumentImpl(new PropertyIdValueImpl(
				"P43", "http://wikibase.org/entity/"), labels, descriptions,
				aliases, datatypeId);
		PropertyDocument pr4 = new PropertyDocumentImpl(pid,
				Collections.<String, String> emptyMap(), descriptions, aliases,
				datatypeId);
		PropertyDocument pr5 = new PropertyDocumentImpl(pid, labels,
				Collections.<String, String> emptyMap(), aliases, datatypeId);
		PropertyDocument pr6 = new PropertyDocumentImpl(pid, labels,
				descriptions, Collections.<String, List<String>> emptyMap(),
				datatypeId);
		PropertyDocument pr7 = new PropertyDocumentImpl(pid, labels,
				descriptions, aliases, new DatatypeIdImpl(DatatypeId.DT_STRING));

		ItemDocument ir = new ItemDocumentImpl(new ItemIdValueImpl("Q42", "foo"),
				labels, descriptions, aliases,
				Collections.<Statement> emptyList(),
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

}
