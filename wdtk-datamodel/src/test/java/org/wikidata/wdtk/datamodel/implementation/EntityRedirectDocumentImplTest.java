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

package org.wikidata.wdtk.datamodel.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.interfaces.EntityRedirectDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EntityRedirectDocumentImplTest {

	private final ObjectMapper mapper = new DatamodelMapper("http://example.com/entity/");

	private final ItemIdValue entityItemId = new ItemIdValueImpl("Q1", "http://example.com/entity/");
	private final ItemIdValue targetItemId = new ItemIdValueImpl("Q2", "http://example.com/entity/");
	private final EntityRedirectDocument itemRedirect = new EntityRedirectDocumentImpl(entityItemId, targetItemId, 0);
	private final EntityRedirectDocument itemRedirect2 = new EntityRedirectDocumentImpl(entityItemId, targetItemId, 0);
	private final EntityRedirectDocument lexemeRedirect = new EntityRedirectDocumentImpl(
			new LexemeIdValueImpl("L1", "http://example.com/entity/"),
			new LexemeIdValueImpl("L2", "http://example.com/entity/"),
			0
	);

	private final String JSON_ITEM_REDIRECT = "{\"entity\":\"Q1\",\"redirect\":\"Q2\"}";

	@Test
	public void fieldsAreCorrect() {
		assertEquals(itemRedirect.getEntityId(), entityItemId);
		assertEquals(itemRedirect.getTargetId(), targetItemId);
	}

	@Test
	public void equalityBasedOnContent() {
		EntityRedirectDocumentImpl diffEntity = new EntityRedirectDocumentImpl(targetItemId, targetItemId, 0);
		EntityRedirectDocumentImpl diffTarget = new EntityRedirectDocumentImpl(entityItemId, entityItemId, 0);
		EntityRedirectDocumentImpl diffRevisionId = new EntityRedirectDocumentImpl(entityItemId, targetItemId, 1);

		assertEquals(itemRedirect, itemRedirect2);
		assertNotEquals(itemRedirect, lexemeRedirect);
		assertNotEquals(itemRedirect, diffEntity);
		assertNotEquals(itemRedirect, diffTarget);
		assertNotEquals(itemRedirect, diffRevisionId);
		assertNotEquals(itemRedirect, null);
		assertNotEquals(itemRedirect, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(itemRedirect.hashCode(), itemRedirect2.hashCode());
	}

	@Test
	public void idNotNull() {
		assertThrows(NullPointerException.class, () -> new EntityRedirectDocumentImpl(null, targetItemId, 0));
	}

	@Test
	public void targetNotNull() {
		assertThrows(NullPointerException.class, () -> new EntityRedirectDocumentImpl(entityItemId, null, 0));
	}

	@Test
	public void entityTypeEquality() {
		assertThrows(IllegalArgumentException.class, () -> new EntityRedirectDocumentImpl(entityItemId, new LexemeIdValueImpl("L1", "http://example.com/entity/"), 0));
	}

	@Test
	public void testRedirectToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_ITEM_REDIRECT, mapper.writeValueAsString(itemRedirect));
	}

	@Test
	public void testLexemeToJava() throws IOException {
		assertEquals(itemRedirect, mapper.readValue(JSON_ITEM_REDIRECT, EntityRedirectDocumentImpl.class));
	}
}
