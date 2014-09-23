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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestMonolingualTextValue extends JsonConversionTest {
	
	/**
	 * Tests the conversion of MonolingualTextValues from JSON to POJO
	 */
	@Test
	public void testMonolingualTextValueToJava(){
		
		try {
			JacksonMonolingualTextValue result = mapper.readValue(mltvJson, JacksonMonolingualTextValue.class);
			
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
	 * Tests the conversion of MonolingualTextValues from POJO to JSON
	 */
	@Test
	public void testMonolingualTextValueToJson(){
		
		try {
			String result = mapper.writeValueAsString(testMltv);
			JsonComparator.compareJsonStrings(mltvJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting Pojo to Json failed");
		}
	}
	
	@Test
	public void testEquals(){
		JacksonMonolingualTextValue match = new JacksonMonolingualTextValue("en", "foobar");
		JacksonMonolingualTextValue wrongLanguage = new JacksonMonolingualTextValue("de", "foobar");
		JacksonMonolingualTextValue wrongValue = new JacksonMonolingualTextValue("en", "barfoo");
		
		assertEquals(testMltv, testMltv);
		assertEquals(testMltv, match);
		assertFalse(testMltv.equals(wrongLanguage));
		assertFalse(testMltv.equals(wrongValue));
	}
}
