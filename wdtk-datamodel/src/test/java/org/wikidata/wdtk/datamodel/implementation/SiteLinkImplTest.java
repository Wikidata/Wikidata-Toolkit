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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

public class SiteLinkImplTest {

	private final ObjectMapper mapper = new DatamodelMapper("http://example.com/entity/");

	private final List<ItemIdValue> badges = Arrays.asList(
			new ItemIdValueImpl("Q43", "http://example.com/entity/"),
			new ItemIdValueImpl("Q42", "http://example.com/entity/")
	);
	private final SiteLink s1 = new SiteLinkImpl("Dresden", "enwiki", badges);
	private final SiteLink s2 = new SiteLinkImpl("Dresden", "enwiki", badges);
	private final String JSON_SITE_LINK = "{\"site\":\"enwiki\", \"title\":\"Dresden\", \"badges\":[\"Q42\",\"Q43\"]}";

	@Test
	public void fieldsIsCorrect() {
		assertEquals(s1.getPageTitle(), "Dresden");
		assertEquals(s1.getSiteKey(), "enwiki");
		assertEquals(s1.getBadges(), badges);
	}

	@Test
	public void equalityBasedOnContent() {
		SiteLink sDiffTitle = new SiteLinkImpl("Berlin", "enwiki", badges);
		SiteLink sDiffSiteKey = new SiteLinkImpl("Dresden", "dewiki", badges);
		SiteLink sDiffBadges = new SiteLinkImpl("Dresden", "enwiki",
				Collections.emptyList());

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
		JsonComparator.compareJsonStrings(JSON_SITE_LINK, mapper.writeValueAsString(s1));
	}

	@Test
	public void testToJava() throws IOException {
		assertEquals(s1, mapper.readValue(JSON_SITE_LINK, SiteLinkImpl.class));
	}
}
