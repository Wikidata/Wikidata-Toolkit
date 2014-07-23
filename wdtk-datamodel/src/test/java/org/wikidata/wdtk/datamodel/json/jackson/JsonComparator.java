package org.wikidata.wdtk.datamodel.json.jackson;

import java.io.IOException;

import org.junit.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A helper class for comparing JSON objects to each other.
 * 
 * @author fredo
 *
 */
public class JsonComparator {

	private static ObjectMapper mapper = new ObjectMapper();

	/**
	 * Compares two JSON objects represented by Strings to each other. Both
	 * Strings are supposed to be valid JSON. From the given Strings the JSON
	 * tree is build and both trees are compared.
	 * 
	 * @param string1
	 * @param string2
	 */
	public static void compareJsonStrings(String string1, String string2) {

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
