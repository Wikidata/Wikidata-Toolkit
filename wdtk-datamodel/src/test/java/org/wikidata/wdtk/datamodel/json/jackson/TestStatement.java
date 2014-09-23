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
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestStatement extends JsonConversionTest {
	
	@Test
	public void testEmptyStatementToJson(){
		JacksonStatement statement = testEmptyStatement;
		
		try {
			String result = mapper.writeValueAsString(statement);
			JsonComparator.compareJsonStrings(emptyStatementJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting Pojo to Json failed");
		}
	}
	
	@Test
	public void testEmptyStatementToJava(){
		try {
			JacksonStatement result = mapper.readValue(emptyStatementJson, JacksonStatement.class);
			// inject claim since this is not provided on statement level in the JSON
			JacksonClaim claim = new JacksonClaim(result, new JacksonPropertyId(propertyId));
			result.setClaim(claim);
			
			assertNotNull(result);
			assertEquals(testEmptyStatement, result);
			
		} catch (JsonParseException e) {
			e.printStackTrace();
			fail("Parsing failed");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			fail("Json mapping failed");
		} catch (IOException e) {
			e.printStackTrace();
			fail("IO failed");
		}
	}
	
	@Test
	public void testEquality(){
		JacksonStatement correctStatement = new JacksonStatement(statementId, testNoValueSnak);
		correctStatement.setClaim(testClaim);
		
		assertEquals(testEmptyStatement, testEmptyStatement);
		assertEquals(testEmptyStatement, correctStatement);
		
		JacksonStatement wrongId = new JacksonStatement(" " + statementId, testNoValueSnak);
		wrongId.setClaim(testClaim);
		assertFalse(testEmptyStatement.equals(wrongId));
	}
	
	@Test
	public void testToString(){
		assertNotNull(testEmptyStatement.toString());
	}
	
	@Test
	public void testHashCode(){
		assertNotNull(testEmptyStatement.hashCode());
	}
}
