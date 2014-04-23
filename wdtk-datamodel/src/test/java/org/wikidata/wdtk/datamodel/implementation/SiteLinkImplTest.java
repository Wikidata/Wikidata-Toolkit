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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

public class SiteLinkImplTest {

	SiteLink s1;
	SiteLink s2;

	@Before
	public void setUp() throws Exception {
		s1 = new SiteLinkImpl("Dresden", "enwiki",
				Collections.<String> emptyList());
		s2 = new SiteLinkImpl("Dresden", "enwiki",
				Collections.<String> emptyList());
	}

	@Test
	public void fieldsIsCorrect() {
		assertEquals(s1.getPageTitle(), "Dresden");
		assertEquals(s1.getSiteKey(), "enwiki");
		assertEquals(s1.getBadges(), Collections.<String> emptyList());
	}

	@Test
	public void equalityBasedOnContent() {
		SiteLink sDiffTitle = new SiteLinkImpl("Berlin", "enwiki",
				Collections.<String> emptyList());
		SiteLink sDiffSiteKey = new SiteLinkImpl("Dresden", "dewiki",
				Collections.<String> emptyList());
		SiteLink sDiffBadges = new SiteLinkImpl("Dresden", "enwiki",
				Collections.<String> singletonList("some badge?"));

		assertEquals(s1, s1);
		assertEquals(s1, s2);
		assertThat(s1, not(equalTo(sDiffTitle)));
		assertThat(s1, not(equalTo(sDiffSiteKey)));
		assertThat(s1, not(equalTo(sDiffBadges)));
		assertThat(s1, not(equalTo(null)));
		assertFalse(s1.equals(this));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(s1.hashCode(), s2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void titleNotNull() {
		new SiteLinkImpl(null, "enwiki", Collections.<String> emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void siteKeyNotNull() {
		new SiteLinkImpl("Dresden", null, Collections.<String> emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void badgesNotNull() {
		new SiteLinkImpl("Dresden", "enwiki", null);
	}

}
