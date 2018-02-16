package org.wikidata.wdtk.datamodel.implementation.json;

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

import java.io.IOException;
import java.util.Collections;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.SiteLinkImpl;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class tests the correct working of the SiteLinkImpl per se (i.e. not
 * being used in a context like items).
 *
 * @author Fredo Erxleben
 *
 */
public class TestSiteLink {

	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testSiteLinkToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(JsonTestData.TEST_SITE_LINK);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_SITE_LINK, result);
	}

	@Test
	public void testSiteLinkToJava() throws
			IOException {
		SiteLinkImpl result = mapper.readValue(JsonTestData.JSON_SITE_LINK,
				SiteLinkImpl.class);

		assertEquals("enwiki", result.getSiteKey());
		assertEquals("foobar", result.getPageTitle());
		assert (result.getBadges().isEmpty());
	}

	@Test
	public void testEquals() {
		SiteLink match = JsonTestData.JACKSON_OBJECT_FACTORY.getSiteLink(
				"foobar", "enwiki", Collections.<String> emptyList());
		SiteLink wrongLanguage = JsonTestData.JACKSON_OBJECT_FACTORY
				.getSiteLink("foobar", "dewiki",
						Collections.<String> emptyList());
		SiteLink wrongValue = JsonTestData.JACKSON_OBJECT_FACTORY.getSiteLink(
				"barfoo", "enwiki", Collections.<String> emptyList());

		assertEquals(JsonTestData.TEST_SITE_LINK, JsonTestData.TEST_SITE_LINK);
		assertEquals(JsonTestData.TEST_SITE_LINK, match);
		assertFalse(JsonTestData.TEST_SITE_LINK.equals(wrongLanguage));
		assertFalse(JsonTestData.TEST_SITE_LINK.equals(wrongValue));
	}
}
