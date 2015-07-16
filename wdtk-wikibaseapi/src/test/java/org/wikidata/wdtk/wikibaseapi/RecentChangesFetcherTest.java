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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;
import org.wikidata.wdtk.testing.MockWebResourceFetcher;

public class RecentChangesFetcherTest{
	private String dateLine = "			<pubDate>Tue, 02 Jun 2015 13:22:02 GMT</pubDate>			<dc:creator>Superzerocool</dc:creator>			<comments>http://www.wikidata.org/wiki/Talk:Q1876457</comments>		</item>";
	private String titleLine = "			<title>Q1876457</title>";

	private String urlForFrom = "https://www.wikidata.org/w/api.php?action=feedrecentchanges&format=json&feedformat=rss&from=20150611154713";
	
	@Test
	public void testConstructors() {
		RecentChangesFetcher rcf1 = new RecentChangesFetcher();
		RecentChangesFetcher rcf2 = new RecentChangesFetcher(
				"https://www.wikidata.org/w/api.php");
		assertEquals(rcf1.rssUrl,
				"https://www.wikidata.org/w/api.php?action=feedrecentchanges&format=json&feedformat=rss");
		assertEquals(rcf2.rssUrl,
				"https://www.wikidata.org/w/api.php?action=feedrecentchanges&format=json&feedformat=rss");
	}
	
	@Test
	public void testGetRecentChanges() throws IOException, ParseException {
		RecentChangesFetcher rcf = new RecentChangesFetcher();
		MockWebResourceFetcher wrf = new MockWebResourceFetcher();
		wrf.setWebResourceContentsFromResource(rcf.rssUrl,
				"/recentchanges.xml", this.getClass());
		rcf.webResourceFetcher = wrf;
		Set<RecentChange> result = rcf.getRecentChanges();
		RecentChange rc1 = new RecentChange(
				"Q1876457",
				new SimpleDateFormat("dd.MM.yyyy HH:mm:ss Z",
						Locale.ENGLISH)
						.parse("02.06.2015 13:22:02 GMT"),
				"Superzerocool");
		assertTrue(result.contains(rc1));
		RecentChange rc2 = new RecentChange("", new Date(), "");
		RecentChange rc3 = new RecentChange("Q1", new Date(), "");
		RecentChange rc4 = new RecentChange(
				"Wikidata  - Recent changes [en]", new Date(),
				"");

		assertFalse(result.contains(rc2));
		assertFalse(result.contains(rc3));
		assertFalse(result.contains(rc4));
	}
	
	@Test
	public void testGetRecentChangesForUrl() throws IOException,
			ParseException {
		RecentChangesFetcher rcf = new RecentChangesFetcher();
		MockWebResourceFetcher wrf = new MockWebResourceFetcher();
		wrf.setWebResourceContentsFromResource(urlForFrom,
				"/recentchanges.xml", this.getClass());
		rcf.webResourceFetcher = wrf;
		Date date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
				.parse("11.06.2015 15:47:13");
		Set<RecentChange> result = rcf.getRecentChanges(date);
		assertFalse(result.isEmpty());
	}

	@Test
	public void testGetRecentChangesWithFromParamenter() throws IOException {
		RecentChangesFetcher rcf = new RecentChangesFetcher();
		MockWebResourceFetcher wrf = new MockWebResourceFetcher();
		wrf.setWebResourceContentsFromResource(rcf.rssUrl,
				"/recentchanges.xml", this.getClass());
		rcf.webResourceFetcher = wrf;
	}

	@Test
	public void testParsePropertyName() {
		RecentChangesFetcher rcf = new RecentChangesFetcher();
		String result = rcf.parsePropertyNameFromItemString(titleLine);
		assertEquals(result, "Q1876457");
	}

	@Test
	public void testParseDate() throws ParseException {
		RecentChangesFetcher rcf = new RecentChangesFetcher();
		Date result = rcf.parseTimeFromItemString(dateLine);
		Date actualDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss Z",
				Locale.ENGLISH)
				.parse("02.06.2015 13:22:02 GMT");
		assertEquals(actualDate.compareTo(result), 0);

	}

	@Test
	public void testParseAuthor() {
		RecentChangesFetcher rcf = new RecentChangesFetcher();
		String result = rcf.parseAuthorFromItemString(dateLine);
		assertEquals(result, "Superzerocool");
	}

	@Test
	public void testBuildUrl() throws ParseException {
		RecentChangesFetcher rcf = new RecentChangesFetcher();
		Date date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss")
				.parse("11.06.2015 15:47:13");
		String result = rcf.buildUrl(date);
		assertEquals(urlForFrom, result);
	}
}
