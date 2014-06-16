package org.wikidata.wdtk.datamodel.externalJsonImplementation;

import java.io.IOException;

import org.junit.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonComparator {

	private static ObjectMapper mapper = new ObjectMapper();
	
	public static void compareJsonStrings(String string1, String string2){
		
		try {
			JsonNode tree1 = mapper.readTree(string1);
			JsonNode tree2 = mapper.readTree(string2);
			Assert.assertEquals(tree1, tree2);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
