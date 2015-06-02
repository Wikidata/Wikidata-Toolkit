package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.wikidata.wdtk.testing.MockWebResourceFetcher;

public class RecentChangesFetcherTest{
	@Test
	public void testConstructors() {
		RecentChangesFetcher rcf1 = new RecentChangesFetcher();
		RecentChangesFetcher rcf2 = new RecentChangesFetcher(
				"http://www.wikidata.org/w/api.php?action=feedrecentchanges&format=json&feedformat=rss");
		assertEquals(rcf1.rdfURL,
				"http://www.wikidata.org/w/api.php?action=feedrecentchanges&format=json&feedformat=rss");
		assertEquals(rcf2.rdfURL,
				"http://www.wikidata.org/w/api.php?action=feedrecentchanges&format=json&feedformat=rss");
	}
	
	@Test
	public void testGetRecentChanges() throws IOException {
		RecentChangesFetcher rcf = new RecentChangesFetcher();
		MockWebResourceFetcher wrf = new MockWebResourceFetcher();
		wrf.setWebResourceContentsFromResource(rcf.rdfURL,
				"/recentchanges.rdf", this.getClass());
		rcf.webResourceFetcher = wrf;
		Set<String> result = rcf.getRecentChanges();
		assertTrue(result.contains("Q1876457"));
		assertFalse(result.contains("Q1"));
		assertFalse(result.contains("Wikidata  - Recent changes [en]"));
	}

	@Test
	public void testParsePropertyName() {
		RecentChangesFetcher rcf = new RecentChangesFetcher();
		String itemString = " <title>Q5</title> ";
		String result = rcf.parsePropertyName(itemString);
		assertEquals(result, "Q5");
	}

	@Test
	public void testParseDate() {
		RecentChangesFetcher rcf = new RecentChangesFetcher();
		String itemString = " <pubDate>Tue, 02 Jun 2015 13:21:58 GMT</pubDate> ";
		Date result = rcf.parseTime(itemString);
		String resultString = new SimpleDateFormat(
				"dd.MM.yyyy HH:mm:ss", Locale.GERMANY)
				.format(result);
		assertEquals(resultString, "02.06.2015 15:21:58");
	}
}