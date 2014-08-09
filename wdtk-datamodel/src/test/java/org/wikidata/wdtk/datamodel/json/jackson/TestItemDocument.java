package org.wikidata.wdtk.datamodel.json.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ItemDocumentImpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class TestItemDocument extends JsonConversionTest {
	
	// TODO test statements (JSON claim)
	
	ItemDocumentImpl fullDocument;
	
	@Before
	public void setupTestFullDocument(){
		fullDocument = new ItemDocumentImpl();
		fullDocument.setId(testItemId.getId());
		fullDocument.setAliases(testAliases);
		fullDocument.setDescriptions(testMltvMap);
		fullDocument.setLabels(testMltvMap);
	}
	
	@Test
	public void testFullDocumentSetup(){
		assertNotNull(fullDocument.getAliases());
		assertNotNull(fullDocument.getDescriptions());
		assertNotNull(fullDocument.getLabels());
		assertNotNull(fullDocument.getId());
		assertNotNull(fullDocument.getItemId());
		assertNotNull(fullDocument.getEntityId());
		
		assertEquals(fullDocument.getItemId().getId(), fullDocument.getId());
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
			JsonComparator.compareJsonStrings(wrappedLabelJson, result);
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
			ItemDocumentImpl result = mapper.readValue(wrappedLabelJson, ItemDocumentImpl.class);
			
			assertNotNull(result);
			assertEquals(testMltvMap, result.getLabels());
			
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
			JsonComparator.compareJsonStrings(wrappedDescriptionJson, result);
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
			ItemDocumentImpl result = mapper.readValue(wrappedDescriptionJson, ItemDocumentImpl.class);
			
			assertNotNull(result);
			assertEquals(testMltvMap, result.getDescriptions());
			
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
			JsonComparator.compareJsonStrings(wrappedAliasJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting Pojo to Json failed");
		}
	}
	
	@Test
	public void testAliasesToJava(){
		
		try {
			ItemDocumentImpl result = mapper.readValue(wrappedAliasJson, ItemDocumentImpl.class);
			
			assertNotNull(result);
			assertEquals(testAliases, result.getAliases());
			
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
	public void testItemIdToJson(){
		ItemDocumentImpl document = new ItemDocumentImpl();
		document.setId(testItemId.getId());
		
		try {
			String result = mapper.writeValueAsString(document);
			JsonComparator.compareJsonStrings(wrappedItemIdJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting Pojo to Json failed");
		}
	}
	
	@Test
	public void testItemIdToJava(){
		
		try {
			ItemDocumentImpl result = mapper.readValue(wrappedItemIdJson, ItemDocumentImpl.class);
			
			assertNotNull(result);
			assertEquals(testItemId, result.getEntityId());
			
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
	public void testSiteLinksToJson(){
		ItemDocumentImpl document = new ItemDocumentImpl();
		document.setSitelinks(testSiteLinkMap);
		
		try {
			String result = mapper.writeValueAsString(document);
			JsonComparator.compareJsonStrings(wrappedSiteLinkJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting Pojo to Json failed");
		}
	}
	
	@Test
	public void testSiteLinksToJava(){
		
		try {
			ItemDocumentImpl result = mapper.readValue(wrappedSiteLinkJson, ItemDocumentImpl.class);
			
			assertNotNull(result);
			assertEquals(testSiteLinkMap, result.getSiteLinks());
			
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
	public void testGenerationFromOtherItemDocument(){
		ItemDocumentImpl copy = new ItemDocumentImpl(fullDocument);
		assertEquals(fullDocument, copy);
	}
}
