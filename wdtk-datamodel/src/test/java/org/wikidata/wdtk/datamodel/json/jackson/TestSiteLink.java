package org.wikidata.wdtk.datamodel.json.jackson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * This class tests the correct working of the SiteLinkImpl per se (i.e. not
 * being used in a context like items).
 * 
 * @author Fredo Erxleben
 *
 */
public class TestSiteLink extends JsonConversionTest {

	@Test
	public void testSiteLinkToJson() {

		try {
			String result = mapper.writeValueAsString(testSiteLink);
			JsonComparator.compareJsonStrings(siteLinkJson, result);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail("Converting Pojo to Json failed");
		}
	}

	public void testSiteLinkToJava() {
		try {
			SiteLinkImpl result = mapper.readValue(siteLinkJson,
					SiteLinkImpl.class);

			assertEquals("enwiki", result.getSiteKey());
			assertEquals("foobar", result.getPageTitle());
			assert (result.badges.isEmpty());

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
	public void testEquals() {
		SiteLinkImpl match = new SiteLinkImpl("enwiki", "foobar");
		SiteLinkImpl wrongLanguage = new SiteLinkImpl("dewiki", "foobar");
		SiteLinkImpl wrongValue = new SiteLinkImpl("enwiki", "barfoo");

		assertEquals(testSiteLink, testSiteLink);
		assertEquals(testSiteLink, match);
		assertFalse(testSiteLink.equals(wrongLanguage));
		assertFalse(testSiteLink.equals(wrongValue));
	}
}
