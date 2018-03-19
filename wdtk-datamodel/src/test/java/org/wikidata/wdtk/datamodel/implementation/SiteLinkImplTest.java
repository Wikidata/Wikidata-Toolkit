package org.wikidata.wdtk.datamodel.implementation;

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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.json.JsonComparator;
import org.wikidata.wdtk.datamodel.implementation.json.JsonTestData;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

public class SiteLinkImplTest {

	private final ObjectMapper mapper = new ObjectMapper();

	private final SiteLink s1 = new SiteLinkImpl("Dresden", "enwiki", Collections.emptyList());
	private final SiteLink s2 = new SiteLinkImpl("Dresden", "enwiki", Collections.emptyList());

	@Test
	public void fieldsIsCorrect() {
		assertEquals(s1.getPageTitle(), "Dresden");
		assertEquals(s1.getSiteKey(), "enwiki");
		assertEquals(s1.getBadges(), Collections. emptyList());
	}

	@Test
	public void equalityBasedOnContent() {
		SiteLink sDiffTitle = new SiteLinkImpl("Berlin", "enwiki",
				Collections.emptyList());
		SiteLink sDiffSiteKey = new SiteLinkImpl("Dresden", "dewiki",
				Collections.emptyList());
		SiteLink sDiffBadges = new SiteLinkImpl("Dresden", "enwiki",
				Collections.singletonList("some badge?"));

		assertEquals(s1, s1);
		assertEquals(s1, s2);
		assertNotEquals(s1, sDiffTitle);
		assertNotEquals(s1, sDiffSiteKey);
		assertNotEquals(s1, sDiffBadges);
		assertNotEquals(s1, null);
		assertNotEquals(s1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(s1.hashCode(), s2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void titleNotNull() {
		new SiteLinkImpl(null, "enwiki", Collections.emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void siteKeyNotNull() {
		new SiteLinkImpl("Dresden", null, Collections.emptyList());
	}

	@Test
	public void badgesCanBeNull() {
		SiteLink sitelink = new SiteLinkImpl("Dresden", "enwiki", null);
		assertEquals(sitelink.getBadges(), Collections.emptyList());
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		String result = mapper.writeValueAsString(JsonTestData.TEST_SITE_LINK);
		JsonComparator.compareJsonStrings(JsonTestData.JSON_SITE_LINK, result);
	}

	@Test
	public void testToJava() throws
			IOException {
		SiteLinkImpl result = mapper.readValue(JsonTestData.JSON_SITE_LINK,
				SiteLinkImpl.class);

		assertEquals("enwiki", result.getSiteKey());
		assertEquals("foobar", result.getPageTitle());
		assertTrue(result.getBadges().isEmpty());
	}

	@Test
	public void testEquals() {
		SiteLink match = JsonTestData.JACKSON_OBJECT_FACTORY.getSiteLink(
				"foobar", "enwiki", Collections.emptyList());
		SiteLink wrongLanguage = JsonTestData.JACKSON_OBJECT_FACTORY
				.getSiteLink("foobar", "dewiki",
						Collections.emptyList());
		SiteLink wrongValue = JsonTestData.JACKSON_OBJECT_FACTORY.getSiteLink(
				"barfoo", "enwiki", Collections.emptyList());

		assertEquals(JsonTestData.TEST_SITE_LINK, JsonTestData.TEST_SITE_LINK);
		assertEquals(JsonTestData.TEST_SITE_LINK, match);
		assertNotEquals(JsonTestData.TEST_SITE_LINK, wrongLanguage);
		assertNotEquals(JsonTestData.TEST_SITE_LINK, wrongValue);
	}
};
