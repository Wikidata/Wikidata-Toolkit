package org.wikidata.wdtk.datamodel.externalJsonImplementation;

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
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestMonolingualTextValue {

	ObjectMapper mapper = new ObjectMapper();
	String testJson = "{\"language\": \"en\", \"value\": \"foobar\"}";
	
	@Test
	public void testJsonToJava(){
		
		try {
			MonolingualTextValueImpl result = mapper.readValue(testJson, MonolingualTextValueImpl.class);
			
			assertEquals(result.getLanguageCode(), "en");
			assertEquals(result.getText(), "foobar");
			
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
	public void testJavaToJson(){
		
		MonolingualTextValueImpl pojo = new MonolingualTextValueImpl("en", "foobar");
		try {
			String result = mapper.writeValueAsString(pojo);
			// remove all whitespaces, they cause might the test to fail unjustified
			assertEquals(result.replaceAll("\\s+",""), testJson.replaceAll("\\s+",""));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting Pojo to Json failed");
		}
	}
	
	
}
