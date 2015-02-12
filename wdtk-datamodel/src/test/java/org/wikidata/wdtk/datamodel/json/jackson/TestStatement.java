package org.wikidata.wdtk.datamodel.json.jackson;

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

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestStatement {

	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testEmptyStatementToJson() throws JsonProcessingException {
		JacksonStatement statement = JsonTestData.getTestNoValueStatement();

		String result = mapper.writeValueAsString(statement);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_NOVALUE_STATEMENT,
				result);
	}

	@Test
	public void testEmptyStatementToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonStatement result = mapper.readValue(
				JsonTestData.JSON_NOVALUE_STATEMENT, JacksonStatement.class);
		result.setSubject(JsonTestData.getEmtpyTestItemDocument().getEntityId());

		assertNotNull(result);
		assertEquals(JsonTestData.getTestNoValueStatement(), result);

	}

	@Test
	public void testEquality() {
		JacksonStatement correctStatement = new JacksonStatement(
				JsonTestData.TEST_STATEMENT_ID, JsonTestData.TEST_NOVALUE_SNAK);
		correctStatement.setSubject(JsonTestData.getEmtpyTestItemDocument()
				.getEntityId());
		JacksonStatement wrongId = new JacksonStatement("another id",
				JsonTestData.TEST_NOVALUE_SNAK);
		wrongId.setSubject(JsonTestData.getEmtpyTestItemDocument()
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
