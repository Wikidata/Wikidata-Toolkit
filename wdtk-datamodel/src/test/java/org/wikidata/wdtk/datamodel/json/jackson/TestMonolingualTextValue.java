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
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.json.jackson.MonolingualTextValueImpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestMonolingualTextValue {

	ObjectMapper mapper = new ObjectMapper();
	static final String testMltvJson = "{\"language\": \"en\", \"value\": \"foobar\"}";
	static final MonolingualTextValueImpl testMltv = new MonolingualTextValueImpl("en", "foobar");

	/**
	 * Tests the conversion of MonolingualTextValues from Json to Pojo
	 */
	@Test
	public void testMonolingualTextValueToJava(){
		
		try {
			MonolingualTextValueImpl result = mapper.readValue(testMltvJson, MonolingualTextValueImpl.class);
			
			assertEquals("en", result.getLanguageCode());
			assertEquals("foobar", result.getText());
			
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
	
	/**
	 * Tests the conversion of MonolingualTextValues from Pojo to Json
	 */
	@Test
	public void testMonolingualTextValueToJson(){
		
		try {
			String result = mapper.writeValueAsString(testMltv);
			JsonComparator.compareJsonStrings(testMltvJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting Pojo to Json failed");
		}
	}
	
	@Test
	public void testEquals(){
		MonolingualTextValueImpl match = new MonolingualTextValueImpl("en", "foobar");
		MonolingualTextValueImpl wrongLanguage = new MonolingualTextValueImpl("de", "foobar");
		MonolingualTextValueImpl wrongValue = new MonolingualTextValueImpl("en", "barfoo");
		
		assertEquals(testMltv, testMltv);
		assertEquals(testMltv, match);
		assertFalse(testMltv.equals(wrongLanguage));
		assertFalse(testMltv.equals(wrongValue));
	}
}
