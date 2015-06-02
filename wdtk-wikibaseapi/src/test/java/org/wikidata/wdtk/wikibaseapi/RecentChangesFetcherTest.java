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
import java.util.Set;

import org.junit.Test;
import org.wikidata.wdtk.testing.MockWebResourceFetcher;

public class RecentChangesFetcherTest{
	@Test
	public void testCreation(){
		RecentChangesFetcher rcf = new RecentChangesFetcher();
		assertEquals(rcf.rdfURL,"http://www.wikidata.org/w/api.php?action=feedrecentchanges&format=json&feedformat=rss");
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
	}
}