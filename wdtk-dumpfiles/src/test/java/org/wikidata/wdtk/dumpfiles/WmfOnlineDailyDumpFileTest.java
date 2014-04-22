package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.testing.MockDirectoryManager;
import org.wikidata.wdtk.testing.MockWebResourceFetcher;

public class WmfOnlineDailyDumpFileTest {

	MockWebResourceFetcher wrf;
	MockDirectoryManager dm;

	@Before
	public void setUp() throws IOException {
		dm = new MockDirectoryManager(Paths.get(System.getProperty("user.dir")));

		wrf = new MockWebResourceFetcher();
	}

	@Test
	public void validDumpProperties() throws IOException {
		String dateStamp = "20140220";
		String maxRevId = "110690987";
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/maxrevid.txt", maxRevId);
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/status.txt", "done");
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/wikidatawiki-" + dateStamp
						+ "-pages-meta-hist-incr.xml.bz2", "Line1");
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);

		BufferedReader br = dump.getDumpFileReader();

		assertEquals(br.readLine(), "Line1");
		assertEquals(br.readLine(), null);
		assertTrue(dump.isAvailable());
		assertTrue(dump.isAvailable()); // second time should use cached entry
		assertEquals(new Long(maxRevId), dump.getMaximalRevisionId());
		assertEquals(dateStamp, dump.getDateStamp());
		assertEquals("wikidatawiki", dump.getProjectName());
		assertEquals("wikidatawiki-daily-" + dateStamp, dump.toString());
		assertEquals(DumpContentType.DAILY, dump.getDumpContentType());
	}

	@Test
	public void missingDumpProperties() {
		String dateStamp = "20140220";
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);

		assertTrue(!dump.isAvailable());
		assertEquals(dump.getMaximalRevisionId(), new Long(-1));
		assertEquals(dateStamp, dump.getDateStamp());
	}

	@Test
	public void emptyDumpProperties() {
		String dateStamp = "20140220";
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/maxrevid.txt", "");
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/status.txt", "");
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);

		assertTrue(!dump.isAvailable());
		assertEquals(new Long(-1), dump.getMaximalRevisionId());
		assertEquals(dateStamp, dump.getDateStamp());
	}

	@Test
	public void malformedRevisionId() {
		String dateStamp = "20140220";
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/maxrevid.txt", "nan");

		assertEquals(dump.getMaximalRevisionId(), new Long(-1));
	}

	@Test
	public void inaccessibleRevisionId() {
		String dateStamp = "20140220";
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/maxrevid.txt", "1234567");
		wrf.setReturnFailingReaders(true);

		assertEquals(dump.getMaximalRevisionId(), new Long(-1));
	}

	@Test
	public void inaccessibleStatus() {
		String dateStamp = "20140220";
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/maxrevid.txt", "1234567");
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/status.txt", "done");
		wrf.setReturnFailingReaders(true);
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);

		assertTrue(!dump.isAvailable());
	}

	@Test(expected = IOException.class)
	public void downloadNoRevisionId() throws IOException {
		String dateStamp = "20140220";
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/wikidatawiki-" + dateStamp
						+ "-pages-meta-hist-incr.xml.bz2", "Line1");
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);
		dump.getDumpFileReader();
	}

	@Test(expected = IOException.class)
	public void downloadNoDumpFile() throws IOException {
		String dateStamp = "20140220";
		String maxRevId = "110690987";
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/maxrevid.txt", maxRevId);
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/"
						+ dateStamp + "/status.txt", "done");
		WmfOnlineDailyDumpFile dump = new WmfOnlineDailyDumpFile(dateStamp,
				"wikidatawiki", wrf, dm);
		dump.getDumpFileReader();
	}

}
