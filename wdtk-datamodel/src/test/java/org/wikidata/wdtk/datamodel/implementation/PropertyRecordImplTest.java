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
import org.wikidata.wdtk.datamodel.interfaces.ItemRecord;
import org.wikidata.wdtk.datamodel.interfaces.PropertyId;
import org.wikidata.wdtk.datamodel.interfaces.PropertyRecord;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class PropertyRecordImplTest {

	PropertyRecord pr1;
	PropertyRecord pr2;

	PropertyId pid;
	Map<String, String> labels;
	Map<String, String> descriptions;
	Map<String, List<String>> aliases;
	DatatypeId datatypeId;

	@Before
	public void setUp() throws Exception {
		pid = new PropertyIdImpl("P42", "http://wikibase.org/entity/");
		labels = new HashMap<String, String>();
		labels.put("en", "Property 42");
		descriptions = new HashMap<String, String>();
		descriptions.put("de", "Dies ist Property 42.");
		aliases = new HashMap<String, List<String>>();
		aliases.put("en", Collections.<String> singletonList("An alias of P42"));
		datatypeId = new DatatypeIdImpl(DatatypeId.DT_ITEM);

		pr1 = new PropertyRecordImpl(pid, labels, descriptions, aliases,
				datatypeId);
		pr2 = new PropertyRecordImpl(pid, labels, descriptions, aliases,
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
	public void propertyRecordValueEqualityBasedOnContent() {
		PropertyRecord pr3 = new PropertyRecordImpl(new PropertyIdImpl("P43",
				"http://wikibase.org/entity/"), labels, descriptions, aliases,
				datatypeId);
		PropertyRecord pr4 = new PropertyRecordImpl(pid,
				Collections.<String, String> emptyMap(), descriptions, aliases,
				datatypeId);
		PropertyRecord pr5 = new PropertyRecordImpl(pid, labels,
				Collections.<String, String> emptyMap(), aliases, datatypeId);
		PropertyRecord pr6 = new PropertyRecordImpl(pid, labels, descriptions,
				Collections.<String, List<String>> emptyMap(), datatypeId);
		PropertyRecord pr7 = new PropertyRecordImpl(pid, labels, descriptions,
				aliases, new DatatypeIdImpl(DatatypeId.DT_STRING));

		ItemRecord ir = new ItemRecordImpl(new ItemIdImpl("Q42", "foo"),
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
	public void propertyRecordHashBasedOnContent() {
		assertEquals(pr1.hashCode(), pr2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void propertyRecordIdNotNull() {
		new PropertyRecordImpl(null, labels, descriptions, aliases, datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void propertyRecordLabelsNotNull() {
		new PropertyRecordImpl(pid, null, descriptions, aliases, datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void propertyRecordDescriptionsNotNull() {
		new PropertyRecordImpl(pid, labels, null, aliases, datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void propertyRecordAliasesNotNull() {
		new PropertyRecordImpl(pid, labels, descriptions, null, datatypeId);
	}

	@Test(expected = NullPointerException.class)
	public void propertyRecordDatatypeNotNull() {
		new PropertyRecordImpl(pid, labels, descriptions, aliases, null);
	}

}
