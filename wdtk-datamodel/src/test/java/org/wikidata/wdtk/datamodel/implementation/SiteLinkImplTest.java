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
				"http://en.wikipedia.org/wiki/",
				Collections.<String> emptyList());
		s2 = new SiteLinkImpl("Dresden", "enwiki",
				"http://en.wikipedia.org/wiki/",
				Collections.<String> emptyList());
	}

	@Test
	public void siteLinkFieldsIsCorrect() {
		assertEquals(s1.getArticleTitle(), "Dresden");
		assertEquals(s1.getSiteKey(), "enwiki");
		assertEquals(s1.getBadges(), Collections.<String> emptyList());
	}

	@Test
	public void siteLinkEqualityBasedOnContent() {
		SiteLink s3 = new SiteLinkImpl("Berlin", "enwiki",
				"http://en.wikipedia.org/wiki/",
				Collections.<String> emptyList());
		SiteLink s4 = new SiteLinkImpl("Dresden", "dewiki",
				"http://en.wikipedia.org/wiki/",
				Collections.<String> emptyList());
		SiteLink s5 = new SiteLinkImpl("Dresden", "enwiki",
				"http://de.wikipedia.org/wiki/",
				Collections.<String> emptyList());
		SiteLink s6 = new SiteLinkImpl("Dresden", "enwiki",
				"http://en.wikipedia.org/wiki/",
				Collections.<String> singletonList("some badge?"));

		assertEquals(s1, s1);
		assertEquals(s1, s2);
		assertThat(s1, not(equalTo(s3)));
		assertThat(s1, not(equalTo(s4)));
		assertThat(s1, not(equalTo(s5)));
		assertThat(s1, not(equalTo(s6)));
		assertThat(s1, not(equalTo(null)));
		assertFalse(s1.equals(this));
	}

	@Test
	public void siteLinkHashBasedOnContent() {
		assertEquals(s1.hashCode(), s2.hashCode());
	}

	@Test
	public void siteLinkIri() {
		assertEquals(s1.getUrl(), "http://en.wikipedia.org/wiki/Dresden");

		SiteLink sSpecialChar = new SiteLinkImpl("&", "dewiki",
				"http://de.wikipedia.org/wiki/",
				Collections.<String> emptyList());
		assertEquals(sSpecialChar.getUrl(), "http://de.wikipedia.org/wiki/%26");
		SiteLink sSpecialChar2 = new SiteLinkImpl("Björk", "dewiki",
				"http://de.wikipedia.org/wiki/",
				Collections.<String> emptyList());
		assertEquals(sSpecialChar2.getUrl(),
				"http://de.wikipedia.org/wiki/Bj%C3%B6rk");
	}

	@Test(expected = NullPointerException.class)
	public void siteLinkTitleNotNull() {
		new SiteLinkImpl(null, "enwiki", "http://en.wikipedia.org/wiki/",
				Collections.<String> emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void siteLinkSiteKeyNotNull() {
		new SiteLinkImpl("Dresden", null, "http://en.wikipedia.org/wiki/",
				Collections.<String> emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void siteLinkBaseIriNotNull() {
		new SiteLinkImpl("Dresden", "enwiki", null,
				Collections.<String> emptyList());
	}

	@Test(expected = NullPointerException.class)
	public void siteLinkBadgesNotNull() {
		new SiteLinkImpl("Dresden", "enwiki", "http://en.wikipedia.org/wiki/",
				null);
	}

}
