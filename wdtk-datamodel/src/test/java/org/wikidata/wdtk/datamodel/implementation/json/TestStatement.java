package org.wikidata.wdtk.datamodel.implementation.json;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImplTest;
import org.wikidata.wdtk.datamodel.implementation.StatementImpl;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonPreStatement;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestStatement {

	ObjectMapper mapper = new DatamodelMapper(Datamodel.SITE_WIKIDATA);

	@Test
	public void testEmptyStatementToJson() throws JsonProcessingException {
		JacksonPreStatement statement = JsonTestData.getTestNoValueStatement();

		String result = mapper.writeValueAsString(statement);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_NOVALUE_STATEMENT,
				result);
	}
	
	@Test
	public void testFullStatementToJson() throws IOException {
		Statement statement = DataObjectFactoryImplTest.getTestStatement(2, 3, 4, EntityIdValue.ET_ITEM);
		ObjectMapper mapper = new DatamodelMapper(statement.getClaim().getSubject().getSiteIri());
		String json = mapper.writeValueAsString(statement);
		JacksonPreStatement deserialized = mapper.readValue(json, JacksonPreStatement.class);
		assertEquals(statement, deserialized.withSubject(statement.getClaim().getSubject()));
	}

	@Test
	public void testEmptyStatementNoIdToJson() throws JsonProcessingException {
		JacksonPreStatement statement = JsonTestData.getTestNoValueNoIdStatement();

		String result = mapper.writeValueAsString(statement);
		JsonComparator.compareJsonStrings(
				JsonTestData.JSON_NOVALUE_NOID_STATEMENT, result);
	}

	@Test
	public void testEmptyStatementToJava() throws
			IOException {
		StatementImpl result = mapper.readValue(
				JsonTestData.JSON_NOVALUE_STATEMENT, JacksonPreStatement.class).withSubject(
			JsonTestData.getEmptyTestItemDocument().getEntityId());

		assertNotNull(result);
		assertNull(result.getValue());
		assertNull(result.getClaim().getValue());
		assertEquals(JsonTestData.getTestNoValueStatement(), result);
	}

	@Test
	public void testEquality() {
		StatementImpl correctStatement = new StatementImpl(
				JsonTestData.TEST_STATEMENT_ID, JsonTestData.TEST_NOVALUE_SNAK,
				JsonTestData.getEmptyTestItemDocument()
				.getEntityId());
		StatementImpl wrongId = new StatementImpl("another id",
				JsonTestData.TEST_NOVALUE_SNAK,
				JsonTestData.getEmptyTestItemDocument()
				.getEntityId());

		assertEquals(JsonTestData.getTestNoValueStatement(),
				JsonTestData.getTestNoValueStatement());
		assertEquals(JsonTestData.getTestNoValueStatement(), correctStatement);
		assertFalse(JsonTestData.getTestNoValueStatement().equals(wrongId));
	}

	@Test
	public void testToString() {
		assertNotNull(JsonTestData.getTestNoValueStatement().toString());
	}

	@Test
	public void testHashCode() {
		assertNotNull(JsonTestData.getTestNoValueStatement().hashCode());
	}
}
