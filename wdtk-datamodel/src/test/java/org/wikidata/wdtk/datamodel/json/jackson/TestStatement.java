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

public class TestStatement extends JsonConversionTest {

	@Test
	public void testEmptyStatementToJson() throws JsonProcessingException {
		JacksonStatement statement = testEmptyStatement;

		String result = mapper.writeValueAsString(statement);
		JsonComparator.compareJsonStrings(emptyStatementJson, result);
	}

	@Test
	public void testEmptyStatementToJava() throws JsonParseException,
			JsonMappingException, IOException {
		JacksonStatement result = mapper.readValue(emptyStatementJson,
				JacksonStatement.class);
		result.setSubject(testItemId);

		assertNotNull(result);
		assertEquals(testEmptyStatement, result);

	}

	@Test
	public void testEquality() {
		JacksonStatement correctStatement = new JacksonStatement(statementId,
				testNoValueSnak);
		correctStatement.setSubject(testItemId);
		JacksonStatement wrongId = new JacksonStatement(" " + statementId,
				testNoValueSnak);
		wrongId.setSubject(testItemId);

		assertEquals(testEmptyStatement, testEmptyStatement);
		assertEquals(testEmptyStatement, correctStatement);
		assertFalse(testEmptyStatement.equals(wrongId));
	}

	@Test
	public void testToString() {
		assertNotNull(testEmptyStatement.toString());
	}

	@Test
	public void testHashCode() {
		assertNotNull(testEmptyStatement.hashCode());
	}
}
