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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.JacksonNoValueSnak;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.JacksonSnak;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.JacksonSomeValueSnak;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.JacksonValueSnak;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestSnakJson extends JsonConversionTest{
	

	@Test
	public void testNoValueSnakToJava(){
		try {
			JacksonSnak result = mapper.readValue(noValueSnakJson, JacksonSnak.class);
			
			assertNotNull(result);
			assertTrue(result instanceof JacksonNoValueSnak);
			assertEquals(result, testNoValueSnak);
			
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
	public void testNoValueSnakToJson(){
		
		try {
			String result = mapper.writeValueAsString(testNoValueSnak);
			JsonComparator.compareJsonStrings(noValueSnakJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting POJO to JSON failed");
		}
	}
	
	@Test
	public void testSomeValueSnakToJava(){
		try {
			JacksonSnak result = mapper.readValue(someValueSnakJson, JacksonSnak.class);
			
			assertNotNull(result);
			assertTrue(result instanceof JacksonSomeValueSnak);
			assertEquals(result, testSomeValueSnak);
			
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
	public void testSomeValueSnakToJson(){
		
		try {
			String result = mapper.writeValueAsString(testSomeValueSnak);
			JsonComparator.compareJsonStrings(someValueSnakJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting POJO to JSON failed");
		}
	}
	
	@Test
	public void testCommonsValueSnakToJava(){
		try {
			JacksonSnak result = mapper.readValue(commonsValueSnakJson, JacksonSnak.class);
			
			assertNotNull(result);
			assertTrue(result instanceof JacksonValueSnak);
			assertEquals(result, testCommonsValueSnak);
			
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
	public void testCommonsValueSnakToJson(){
		
		try {
			String result = mapper.writeValueAsString(testCommonsValueSnak);
			JsonComparator.compareJsonStrings(commonsValueSnakJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting POJO to JSON failed");
		}
	}

}
