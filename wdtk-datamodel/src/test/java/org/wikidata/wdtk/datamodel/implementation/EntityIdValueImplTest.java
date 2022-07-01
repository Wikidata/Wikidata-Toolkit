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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntityIdValueImplTest {

	@Test
	public void testFromIdItem() {
		assertEquals(new ItemIdValueImpl("Q42", "http://foo/"), EntityIdValueImpl.fromId("Q42", "http://foo/"));
	}

	@Test
	public void testFromIdProperty() {
		assertEquals(new PropertyIdValueImpl("P42", "http://foo/"), EntityIdValueImpl.fromId("P42", "http://foo/"));
	}

	@Test
	public void testFromIdLexeme() {
		assertEquals(new LexemeIdValueImpl("L42", "http://foo/"), EntityIdValueImpl.fromId("L42", "http://foo/"));
	}

	@Test
	public void testFromIdForm() {
		assertEquals(new FormIdValueImpl("L42-F1", "http://foo/"), EntityIdValueImpl.fromId("L42-F1", "http://foo/"));
	}

	@Test
	public void testFromIdSense() {
		assertEquals(new SenseIdValueImpl("L42-S1", "http://foo/"), EntityIdValueImpl.fromId("L42-S1", "http://foo/"));
	}

	@Test
	public void testFromIdMediaInfo() {
		assertEquals(new MediaInfoIdValueImpl("M42", "http://foo/"), EntityIdValueImpl.fromId("M42", "http://foo/"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromIdFailure() {
		EntityIdValueImpl.fromId("L42-P1", "http://foo/");
	}

}
