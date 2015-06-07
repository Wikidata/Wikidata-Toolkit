package org.wikidata.wdtk.wikibaseapi;

import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class RecentChangeTest {
	@Test
	public void testEquals() {
		RecentChange rc1 = new RecentChange("a", new Date(), "b");
		RecentChange rc2 = new RecentChange("a", new Date(), "b");
		assertTrue(rc1.equals(rc2));
	}

	@Test
	public void testContainsSet() {
		RecentChange rc1 = new RecentChange("a", new Date(), "b");
		RecentChange rc2 = new RecentChange("a", new Date(), "b");
		Set<RecentChange> rcs = new TreeSet<>();
		rcs.add(rc1);
		assertTrue(rcs.contains(rc1));
		assertTrue(rcs.contains(rc2));
	}

}