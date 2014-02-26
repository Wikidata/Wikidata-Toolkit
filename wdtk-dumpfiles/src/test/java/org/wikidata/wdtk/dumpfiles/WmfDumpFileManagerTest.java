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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class WmfDumpFileManagerTest {

	MockWebResourceFetcher wrf;
	MockDirectoryManager dm;

	/**
	 * Helper class to test dump file processing capabilities.
	 * 
	 * @author Markus Kroetzsch
	 * 
	 */
	class TestDumpfileProcessor implements DumpFileProcessor {

		String result = "";

		@Override
		public void processDumpFileContents(BufferedReader bufferedReader,
				MediaWikiDumpFile dumpFile) {
			try {
				result = result
						+ MockStringContentFactory
								.getStringFromBufferedReader(bufferedReader)
						+ "\n";
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

	}

	@Before
	public void setUp() throws IOException {
		wrf = new MockWebResourceFetcher();
		dm = new MockDirectoryManager(Paths.get(System.getProperty("user.dir")));
	}

	/**
	 * Helper method to create mocked local dump files.
	 * 
	 * @param dateStamp
	 * @param dumpContentType
	 * @param isDone
	 */
	void setLocalDump(String dateStamp,
			MediaWikiDumpFile.DumpContentType dumpContentType, boolean isDone) {

		Path dumpFilePath = dm.directory.resolve("dumpfiles").resolve(
				"wikidatawiki");
		Path thisDumpPath = dumpFilePath.resolve(dumpContentType.toString()
				.toLowerCase() + "-" + dateStamp);
		dm.setFileContents(
				thisDumpPath.resolve("wikidatawiki-" + dateStamp
						+ WmfDumpFile.getDumpFilePostfix(dumpContentType)),
				"Contents of " + dumpContentType.toString().toLowerCase() + " "
						+ dateStamp);
		if (isDone) {
			dm.setFileContents(thisDumpPath.resolve("maxrevid.txt"),
					"123456789");
		}
	}

	@Test
	public void getAllDailyDumps() throws IOException {
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/",
				"/other-incr-wikidatawiki-index.html",
				MockWebResourceFetcher.TYPE_HTML);

		setLocalDump("20140220", MediaWikiDumpFile.DumpContentType.DAILY, true);
		setLocalDump("20140219", MediaWikiDumpFile.DumpContentType.CURRENT,
				true);
		setLocalDump("20140215", MediaWikiDumpFile.DumpContentType.DAILY, false);
		setLocalDump("20140205", MediaWikiDumpFile.DumpContentType.DAILY, true);
		setLocalDump("nodate", MediaWikiDumpFile.DumpContentType.DAILY, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, wrf);

		List<? extends MediaWikiDumpFile> dumpFiles = dumpFileManager
				.findAllDailyDumps();

		String[] dumpDates = { "20140221", "20140220", "20140219", "20140218",
				"20140217", "20140216", "20140215", "20140214", "20140213",
				"20140212", "20140211", "20140210", "20140209", "20140208",
				"20140205" };
		boolean[] dumpIsLocal = { false, true, false, false, false, false,
				false, false, false, false, false, false, false, false, true };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					MediaWikiDumpFile.DumpContentType.DAILY);
			assertEquals(dumpFiles.get(i).getDateStamp(), dumpDates[i]);
			if (dumpIsLocal[i]) {
				assertTrue(dumpFiles.get(i) instanceof WmfLocalDumpFile);
			} else {
				assertTrue(dumpFiles.get(i) instanceof WmfOnlineDailyDumpFile);
			}
		}
	}

	@Test
	public void getAllCurrentDumps() throws IOException {
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/wikidatawiki/",
				"/wikidatawiki-index.html", MockWebResourceFetcher.TYPE_HTML);

		setLocalDump("20140210", MediaWikiDumpFile.DumpContentType.CURRENT,
				false);
		setLocalDump("20140123", MediaWikiDumpFile.DumpContentType.CURRENT,
				true);
		setLocalDump("20140106", MediaWikiDumpFile.DumpContentType.DAILY, true);
		setLocalDump("20131201", MediaWikiDumpFile.DumpContentType.CURRENT,
				true);
		setLocalDump("nodate", MediaWikiDumpFile.DumpContentType.CURRENT, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, wrf);

		List<? extends MediaWikiDumpFile> dumpFiles = dumpFileManager
				.findAllCurrentDumps();

		String[] dumpDates = { "20140210", "20140123", "20140106", "20131221",
				"20131201" };
		boolean[] dumpIsLocal = { false, true, false, false, true };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					MediaWikiDumpFile.DumpContentType.CURRENT);
			assertEquals(dumpFiles.get(i).getDateStamp(), dumpDates[i]);
			if (dumpIsLocal[i]) {
				assertTrue(dumpFiles.get(i) instanceof WmfLocalDumpFile);
			} else {
				assertTrue(dumpFiles.get(i) instanceof WmfOnlineStandardDumpFile);
			}
		}
	}

	@Test
	public void getAllFullDumps() throws IOException {
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/wikidatawiki/",
				"/wikidatawiki-index.html", MockWebResourceFetcher.TYPE_HTML);

		setLocalDump("20140210", MediaWikiDumpFile.DumpContentType.FULL, false);
		setLocalDump("20140123", MediaWikiDumpFile.DumpContentType.FULL, true);
		setLocalDump("20140106", MediaWikiDumpFile.DumpContentType.CURRENT,
				true);
		setLocalDump("20131201", MediaWikiDumpFile.DumpContentType.FULL, true);
		setLocalDump("nodate", MediaWikiDumpFile.DumpContentType.FULL, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, wrf);

		List<? extends MediaWikiDumpFile> dumpFiles = dumpFileManager
				.findAllFullDumps();

		String[] dumpDates = { "20140210", "20140123", "20140106", "20131221",
				"20131201" };
		boolean[] dumpIsLocal = { false, true, false, false, true };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					MediaWikiDumpFile.DumpContentType.FULL);
			assertEquals(dumpFiles.get(i).getDateStamp(), dumpDates[i]);
			if (dumpIsLocal[i]) {
				assertTrue(dumpFiles.get(i) instanceof WmfLocalDumpFile);
			} else {
				assertTrue(dumpFiles.get(i) instanceof WmfOnlineStandardDumpFile);
			}
		}
	}

	@Test
	public void getAllDailyDumpsOffline() throws IOException {
		setLocalDump("20140220", MediaWikiDumpFile.DumpContentType.DAILY, true);
		setLocalDump("20140205", MediaWikiDumpFile.DumpContentType.DAILY, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, null);

		List<? extends MediaWikiDumpFile> dumpFiles = dumpFileManager
				.findAllDailyDumps();

		String[] dumpDates = { "20140220", "20140205" };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					MediaWikiDumpFile.DumpContentType.DAILY);
			assertEquals(dumpFiles.get(i).getDateStamp(), dumpDates[i]);
			assertTrue(dumpFiles.get(i) instanceof WmfLocalDumpFile);
		}
	}

	@Test
	public void getAllCurrentDumpsOffline() throws IOException {
		setLocalDump("20140220", MediaWikiDumpFile.DumpContentType.CURRENT,
				true);
		setLocalDump("20140205", MediaWikiDumpFile.DumpContentType.CURRENT,
				true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, null);

		List<? extends MediaWikiDumpFile> dumpFiles = dumpFileManager
				.findAllCurrentDumps();

		String[] dumpDates = { "20140220", "20140205" };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					MediaWikiDumpFile.DumpContentType.CURRENT);
			assertEquals(dumpFiles.get(i).getDateStamp(), dumpDates[i]);
			assertTrue(dumpFiles.get(i) instanceof WmfLocalDumpFile);
		}
	}

	@Test
	public void getAllFullDumpsOffline() throws IOException {
		setLocalDump("20140220", MediaWikiDumpFile.DumpContentType.FULL, true);
		setLocalDump("20140205", MediaWikiDumpFile.DumpContentType.FULL, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, null);

		List<? extends MediaWikiDumpFile> dumpFiles = dumpFileManager
				.findAllFullDumps();

		String[] dumpDates = { "20140220", "20140205" };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					MediaWikiDumpFile.DumpContentType.FULL);
			assertEquals(dumpFiles.get(i).getDateStamp(), dumpDates[i]);
			assertTrue(dumpFiles.get(i) instanceof WmfLocalDumpFile);
		}
	}

	@Test
	public void getAllRelevantDumps() throws IOException {
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/other/incr/wikidatawiki/",
				"/other-incr-wikidatawiki-index.html",
				MockWebResourceFetcher.TYPE_HTML);
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/wikidatawiki/",
				"/wikidatawiki-index.html", MockWebResourceFetcher.TYPE_HTML);

		setLocalDump("20140220", MediaWikiDumpFile.DumpContentType.DAILY, true);
		setLocalDump("20140219", MediaWikiDumpFile.DumpContentType.FULL, true);
		setLocalDump("20140205", MediaWikiDumpFile.DumpContentType.DAILY, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, wrf);

		List<? extends MediaWikiDumpFile> dumpFiles = dumpFileManager
				.findAllRelevantDumps(true);

		String[] dumpDates = { "20140221", "20140220", "20140219", "20140218",
				"20140217", "20140216", "20140215", "20140214", "20140213",
				"20140212", "20140211", "20140210" };
		boolean[] dumpIsLocal = { false, true, false, false, false, false,
				false, false, false, false, false, false };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			if (i == dumpFiles.size() - 1) {
				assertEquals(dumpFiles.get(i).getDumpContentType(),
						MediaWikiDumpFile.DumpContentType.CURRENT);
			} else {
				assertEquals(dumpFiles.get(i).getDumpContentType(),
						MediaWikiDumpFile.DumpContentType.DAILY);
			}
			assertEquals(dumpFiles.get(i).getDateStamp(), dumpDates[i]);
			if (dumpIsLocal[i]) {
				assertTrue(dumpFiles.get(i) instanceof WmfLocalDumpFile);
			} else {
				assertFalse(dumpFiles.get(i) instanceof WmfLocalDumpFile);
			}
		}
	}

	@Test
	public void getAllRelevantDumpsMainDumpMissing() throws IOException {
		setLocalDump("20140220", MediaWikiDumpFile.DumpContentType.DAILY, true);
		setLocalDump("20140210", MediaWikiDumpFile.DumpContentType.CURRENT,
				true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, wrf);

		List<? extends MediaWikiDumpFile> dumpFiles = dumpFileManager
				.findAllRelevantDumps(false);

		assertEquals(dumpFiles.size(), 1);
		assertEquals(dumpFiles.get(0).getDumpContentType(),
				MediaWikiDumpFile.DumpContentType.DAILY);
		assertEquals(dumpFiles.get(0).getDateStamp(), "20140220");
		assertTrue(dumpFiles.get(0) instanceof WmfLocalDumpFile);
	}

	@Test
	public void processAllRelevantDumps() throws IOException {

		setLocalDump("20140221", MediaWikiDumpFile.DumpContentType.DAILY, true);
		setLocalDump("20140220", MediaWikiDumpFile.DumpContentType.DAILY, true);
		setLocalDump("20140219", MediaWikiDumpFile.DumpContentType.CURRENT,
				true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, null);

		TestDumpfileProcessor dfp = new TestDumpfileProcessor();

		dumpFileManager.processAllRecentDumps(dfp, true);

		assertEquals(
				dfp.result,
				"Contents of daily 20140221\nContents of daily 20140220\nContents of current 20140219\n");
	}

}
