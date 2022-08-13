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

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;

public class DatatypeIdImplTest {

	private final DatatypeIdImpl d1 = new DatatypeIdImpl(DatatypeIdValue.DT_ITEM);
	private final DatatypeIdImpl d2 = new DatatypeIdImpl("http://wikiba.se/ontology#WikibaseItem");
	private final DatatypeIdImpl d3 = new DatatypeIdImpl(DatatypeIdValue.DT_TIME);
	private final DatatypeIdImpl d4 = new DatatypeIdImpl("http://wikiba.se/ontology#SomeUnknownDatatype", "some-unknownDatatype");

	@Test(expected = NullPointerException.class)
	public void datatypeIdNotNull() {
		new DatatypeIdImpl((String) null);
	}

	@Test
	public void equalityBasedOnContent() {
		assertEquals(d1, d1);
		assertEquals(d1, d2);
		assertNotEquals(d1, d3);
		assertNotEquals(d1, null);
		assertNotEquals(d1, new StringValueImpl("foo"));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(d1.hashCode(), d2.hashCode());
	}

	@Test
	public void doNotChokeOnUnknownDatatypes() {
		// for issue https://github.com/Wikidata/Wikidata-Toolkit/issues/716
		assertEquals("some-unknownDatatype", d4.getJsonString());
		assertEquals("http://wikiba.se/ontology#SomeUnknownDatatype", d4.getIri());
	}

	@Test
	public void testDeserializeUnknownJsonDatatype() {
		// for issue https://github.com/Wikidata/Wikidata-Toolkit/issues/716
		assertEquals("http://wikiba.se/ontology#LocalMedia", DatatypeIdImpl.getDatatypeIriFromJsonDatatype("localMedia"));
	}

}
