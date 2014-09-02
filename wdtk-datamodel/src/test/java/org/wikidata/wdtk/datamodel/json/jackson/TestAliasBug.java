package org.wikidata.wdtk.datamodel.json.jackson;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ItemDocumentImpl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This is a test for a bug in the JSON. Empty aliases are falsely serialized as
 * arrays. Once the bug is gone this class can safely be removed. Don't forget
 * to cleanup the affected implementations!
 * 
 * @author Fredo Erxleben
 *
 */
public class TestAliasBug extends JsonConversionTest {

	String buggedAliasesJson = "\"aliases\":[]";
	String buggedItemJson = "{" + itemTypeJson + "," + buggedAliasesJson + "}";

	@Test
	public void testAliasesToJava() {

		try {
			ItemDocumentImpl result = mapper.readValue(buggedItemJson,
					ItemDocumentImpl.class);

			assertNotNull(result);
			assert (result.getAliases().isEmpty());

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
