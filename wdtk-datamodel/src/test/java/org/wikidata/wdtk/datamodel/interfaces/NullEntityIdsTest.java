package org.wikidata.wdtk.datamodel.interfaces;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NullEntityIdsTest {

	static class TestValueVisitor implements ValueVisitor<String> {

		@Override
		public String visit(EntityIdValue value) {
			return value.getId();
		}

		@Override
		public String visit(GlobeCoordinatesValue value) {
			return null;
		}

		@Override
		public String visit(MonolingualTextValue value) {
			return null;
		}

		@Override
		public String visit(QuantityValue value) {
			return null;
		}

		@Override
		public String visit(StringValue value) {
			return null;
		}

		@Override
		public String visit(TimeValue value) {
			return null;
		}

		@Override
		public String visit(UnsupportedValue value) {
			return null;
		}

	}

	@Test
	public void testNullItemId() {
		TestValueVisitor tvv = new TestValueVisitor();
		assertEquals("Q0", ItemIdValue.NULL.accept(tvv));
		assertEquals("Q0", ItemIdValue.NULL.getId());
		assertEquals("http://localhost/entity/", ItemIdValue.NULL.getSiteIri());
		assertEquals(EntityIdValue.ET_ITEM, ItemIdValue.NULL.getEntityType());
		assertEquals("http://localhost/entity/Q0", ItemIdValue.NULL.getIri());
	}

	@Test
	public void testNullPropertyId() {
		TestValueVisitor tvv = new TestValueVisitor();
		assertEquals("P0", PropertyIdValue.NULL.accept(tvv));
		assertEquals("P0", PropertyIdValue.NULL.getId());
		assertEquals("http://localhost/entity/",
				PropertyIdValue.NULL.getSiteIri());
		assertEquals(EntityIdValue.ET_PROPERTY,
				PropertyIdValue.NULL.getEntityType());
		assertEquals("http://localhost/entity/P0",
				PropertyIdValue.NULL.getIri());
	}

	@Test
	public void testNullLexemeId() {
		TestValueVisitor tvv = new TestValueVisitor();
		assertEquals("L0", LexemeIdValue.NULL.accept(tvv));
		assertEquals("L0", LexemeIdValue.NULL.getId());
		assertEquals("http://localhost/entity/", LexemeIdValue.NULL.getSiteIri());
		assertEquals(EntityIdValue.ET_LEXEME, LexemeIdValue.NULL.getEntityType());
		assertEquals("http://localhost/entity/L0", LexemeIdValue.NULL.getIri());
	}

}
