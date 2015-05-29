package org.wikidata.wdtk.wikibaseapi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RecentChangesFetcherTest{
	@Test
	public void testCreation(){
		RecentChangesFetcher rcf = new RecentChangesFetcher();
		assertEquals(rcf.rdfURL,"http://www.wikidata.org/w/api.php?action=feedrecentchanges&format=json&feedformat=rss");
	}
}