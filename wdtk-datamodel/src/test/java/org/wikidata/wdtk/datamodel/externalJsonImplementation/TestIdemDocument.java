package org.wikidata.wdtk.datamodel.externalJsonImplementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
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
	
	// wrapping into item document structure for dedicated tests
	static final String testLabelJson = "{\"labels\":{\"en\":" + TestMonolingualTextValue.testJson + "}}";
	static final String testDescriptionJson = "{\"descriptions\":{\"en\":" + TestMonolingualTextValue.testJson + "}}";
	static final String testAliasJson = "{ \"aliases\":{\"en\":[" + TestMonolingualTextValue.testJson + "]}}";
	
	// puzzle pieces for creation of the test object
	Map<String, MonolingualTextValueImpl> testMltvMap;
	Map<String, List<MonolingualTextValueImpl>> testAliases;
	
	@Before
	public void setupTestMltv(){
		testMltvMap = new HashMap<>();
		testMltvMap.put("en", TestMonolingualTextValue.testMltv);
	}

	@Before
	public void setupTestAliases(){
		testAliases = new HashMap<>();
		List<MonolingualTextValueImpl> aliases = new LinkedList<>();
		aliases.add(TestMonolingualTextValue.testMltv);
		testAliases.put("en", aliases);
	}
	
	/**
	 * Tests the conversion of ItemDocuments containing labels from Pojo to Json
	 */
	@Test
	public void testLabelsToJson(){
		ItemDocumentImpl document = new ItemDocumentImpl();
		document.setLabels(testMltvMap);
		
		try {
			String result = mapper.writeValueAsString(document);
			// remove all whitespaces, they cause might the test to fail unjustified
			assertEquals(testLabelJson.replaceAll("\\s+",""), result.replaceAll("\\s+",""));
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
		
		try {
			ItemDocumentImpl result = mapper.readValue(testLabelJson, ItemDocumentImpl.class);
			
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
		
		try {
			String result = mapper.writeValueAsString(document);
			// remove all whitespaces, they cause might the test to fail unjustified
			assertEquals(testDescriptionJson.replaceAll("\\s+",""), result.replaceAll("\\s+",""));
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
		
		try {
			ItemDocumentImpl result = mapper.readValue(testDescriptionJson, ItemDocumentImpl.class);
			
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

	@Test
	public void testAliasesToJson(){
		ItemDocumentImpl document = new ItemDocumentImpl();
		document.setAliases(testAliases);
		
		try {
			String result = mapper.writeValueAsString(document);
			// remove all whitespaces, they cause might the test to fail unjustified
			assertEquals(testAliasJson.replaceAll("\\s+",""), result.replaceAll("\\s+",""));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting Pojo to Json failed");
		}
	}
	
	@Test
	public void testAliasesToJava(){
		
		try {
			ItemDocumentImpl result = mapper.readValue(testAliasJson, ItemDocumentImpl.class);
			
			assertNotNull(result);
			assertEquals(testAliases, result.aliases);
			
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
}
