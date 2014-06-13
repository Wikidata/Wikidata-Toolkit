package org.wikidata.wdtk.datamodel.externalJsonImplementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestIdemDocument {
	
	ObjectMapper mapper = new ObjectMapper();
	
	// puzzle pieces for creation of the test object
	Map<String, MonolingualTextValueImpl> testMltvMap;
	Map<String, List<MonolingualTextValueImpl>> testAliases;
	
	String testMltvJson = "{\"en\":{\"language\":\"en\", \"value\":\"fooLabel\"}}";
	
	@Before
	public void setupTestMltv(){
		testMltvMap = new HashMap<>();
		MonolingualTextValueImpl fooLabel =  new MonolingualTextValueImpl("en", "fooLabel");
		testMltvMap.put("en", fooLabel);
	}

	public void setupTestAliases(){
		// TODO
	}
	
	/**
	 * Tests the conversion of ItemDocuments containing labels from Pojo to Json
	 */
	@Test
	public void testLabelsToJson(){
		ItemDocumentImpl document = new ItemDocumentImpl();
		document.setLabels(testMltvMap);
		// wrap up the label json in the ItemDocument description
		String expected = "{\"labels\":" + testMltvJson + "}";
		
		try {
			String result = mapper.writeValueAsString(document);
			// remove all whitespaces, they cause might the test to fail unjustified
			assertEquals(expected.replaceAll("\\s+",""), result.replaceAll("\\s+",""));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting Pojo to Json failed");
		}
	}
	
	/**
	 * Tests the conversion of ItemDocuments containing labels from Json to Pojo
	 */
	@Test
	public void testLabelToJava(){
		
		// wrap up the label json in the ItemDocument description
		String testJson = "{\"labels\":" + testMltvJson + "}";
		
		try {
			ItemDocumentImpl result = mapper.readValue(testJson, ItemDocumentImpl.class);
			
			assertNotNull(result);
			assertEquals(testMltvMap, result.labels);
			
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
	 * Tests the conversion of ItemDocuments containing descriptions from Pojo to Json
	 */
	@Test
	public void testDescriptionsToJson(){
		ItemDocumentImpl document = new ItemDocumentImpl();
		document.setDescriptions(testMltvMap);
		// wrap up the label json in the ItemDocument description
		String expected = "{\"descriptions\":" + testMltvJson + "}";
		
		try {
			String result = mapper.writeValueAsString(document);
			// remove all whitespaces, they cause might the test to fail unjustified
			assertEquals(expected.replaceAll("\\s+",""), result.replaceAll("\\s+",""));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting Pojo to Json failed");
		}
	}

	/**
	 * Tests the conversion of ItemDocuments containing descriptions from Json to Pojo
	 */
	@Test
	public void testDescriptionsToJava(){
		
		// wrap up the label json in the ItemDocument description
		String testJson = "{\"descriptions\":" + testMltvJson + "}";
		
		try {
			ItemDocumentImpl result = mapper.readValue(testJson, ItemDocumentImpl.class);
			
			assertNotNull(result);
			assertEquals(testMltvMap, result.descriptions);
			
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

	public void testAliasesToJson(){
		ItemDocumentImpl document = new ItemDocumentImpl();
		
	}
}
