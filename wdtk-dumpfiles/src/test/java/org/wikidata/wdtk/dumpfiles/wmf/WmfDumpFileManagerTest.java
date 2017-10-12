package org.wikidata.wdtk.dumpfiles.wmf;

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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.MwDumpFile;
import org.wikidata.wdtk.dumpfiles.MwDumpFileProcessor;
import org.wikidata.wdtk.testing.MockDirectoryManager;
import org.wikidata.wdtk.testing.MockStringContentFactory;
import org.wikidata.wdtk.testing.MockWebResourceFetcher;

public class WmfDumpFileManagerTest {

	MockWebResourceFetcher wrf;
	MockDirectoryManager dm;
	Path dmPath;

	/**
	 * Helper class to test dump file processing capabilities.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	class TestDumpfileProcessor implements MwDumpFileProcessor {

		String result = "";

		@Override
		public void processDumpFileContents(InputStream inputStream,
				MwDumpFile dumpFile) {
			try {
				result = result
						+ MockStringContentFactory
								.getStringFromInputStream(inputStream) + "\n";
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

	}

	@Before
	public void setUp() throws IOException {
		this.wrf = new MockWebResourceFetcher();
		this.dmPath = Paths.get(System.getProperty("user.dir"));
		this.dm = new MockDirectoryManager(this.dmPath, true, false);
	}

	/**
	 * Helper method to create mocked local dump files.
	 *
	 * @param dateStamp
	 * @param dumpContentType
	 * @param isDone
	 * @throws IOException
	 */
	void setLocalDump(String dateStamp, DumpContentType dumpContentType,
			boolean isDone) throws IOException {

		Path dumpFilePath = this.dmPath.resolve("dumpfiles").resolve(
				"wikidatawiki");
		Path thisDumpPath = dumpFilePath.resolve(dumpContentType.toString()
				.toLowerCase() + "-" + dateStamp);
		dm.setDirectory(thisDumpPath);
		if (isDone) {
			Path filePath = thisDumpPath.resolve(WmfDumpFile.getDumpFileName(
					dumpContentType, "wikidatawiki", dateStamp));
			dm.setFileContents(filePath,
					"Contents of " + dumpContentType.toString().toLowerCase() + " " + dateStamp,
					WmfDumpFile.getDumpFileCompressionType(filePath.toString()));
		}
	}

	@Test
	public void getAllDailyDumps() throws IOException {
		wrf.setWebResourceContentsFromResource(
				"https://dumps.wikimedia.org/other/incr/wikidatawiki/",
				"/other-incr-wikidatawiki-index.html", this.getClass());

		setLocalDump("20140220", DumpContentType.DAILY, true);
		setLocalDump("20140219", DumpContentType.CURRENT, true);
		setLocalDump("20140215", DumpContentType.DAILY, false);
		setLocalDump("20140205", DumpContentType.DAILY, true);
		setLocalDump("nodate", DumpContentType.DAILY, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, wrf);

		List<? extends MwDumpFile> dumpFiles = dumpFileManager
				.findAllDumps(DumpContentType.DAILY);

		String[] dumpDates = { "20140221", "20140220", "20140219", "20140218",
				"20140217", "20140216", "20140215", "20140214", "20140213",
				"20140212", "20140211", "20140210", "20140209", "20140208",
				"20140205" };
		boolean[] dumpIsLocal = { false, true, false, false, false, false,
				false, false, false, false, false, false, false, false, true };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					DumpContentType.DAILY);
			assertEquals(dumpFiles.get(i).getDateStamp(), dumpDates[i]);
			if (dumpIsLocal[i]) {
				assertTrue(
						"Dumpfile " + dumpFiles.get(i) + " should be local.",
						dumpFiles.get(i) instanceof WmfLocalDumpFile);
			} else {
				assertTrue("Dumpfile " + dumpFiles.get(i)
						+ " should be online.",
						dumpFiles.get(i) instanceof WmfOnlineDailyDumpFile);
			}
		}
	}

	@Test
	public void getAllJsonDumps() throws IOException {
		wrf.setWebResourceContentsFromResource(
				"https://dumps.wikimedia.org/other/wikidata/",
				"/other-wikidata-index.html", this.getClass());

		setLocalDump("20141110", DumpContentType.JSON, true);
		setLocalDump("20150105", DumpContentType.CURRENT, true);
		setLocalDump("20141201", DumpContentType.JSON, true);
		setLocalDump("nodate", DumpContentType.JSON, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, wrf);

		List<? extends MwDumpFile> dumpFiles = dumpFileManager
				.findAllDumps(DumpContentType.JSON);

		String[] dumpDates = { "20150112", "20150105", "20141229", "20141222",
				"20141215", "20141210", "20141201", "20141124", "20141117",
				"20141110" };
		boolean[] dumpIsLocal = { false, false, false, false, false, false,
				true, false, false, true };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					DumpContentType.JSON);
			assertEquals(dumpFiles.get(i).getDateStamp(), dumpDates[i]);
			if (dumpIsLocal[i]) {
				assertTrue(
						"Dumpfile " + dumpFiles.get(i) + " should be local.",
						dumpFiles.get(i) instanceof WmfLocalDumpFile);
			} else {
				assertTrue("Dumpfile " + dumpFiles.get(i)
						+ " should be online.",
						dumpFiles.get(i) instanceof JsonOnlineDumpFile);
			}
		}
	}

	@Test
	public void getAllCurrentDumps() throws IOException {
		wrf.setWebResourceContentsFromResource(
				"https://dumps.wikimedia.org/wikidatawiki/",
				"/wikidatawiki-index-old.html", this.getClass());

		setLocalDump("20140210", DumpContentType.CURRENT, false);
		setLocalDump("20140123", DumpContentType.CURRENT, true);
		setLocalDump("20140106", DumpContentType.DAILY, true);
		setLocalDump("20131201", DumpContentType.CURRENT, true);
		setLocalDump("nodate", DumpContentType.CURRENT, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, wrf);

		List<? extends MwDumpFile> dumpFiles = dumpFileManager
				.findAllDumps(DumpContentType.CURRENT);

		String[] dumpDates = { "20140210", "20140123", "20140106", "20131221",
				"20131201" };
		boolean[] dumpIsLocal = { false, true, false, false, true };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					DumpContentType.CURRENT);
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
				"https://dumps.wikimedia.org/wikidatawiki/",
				"/wikidatawiki-index-old.html", this.getClass());

		setLocalDump("20140210", DumpContentType.FULL, false);
		setLocalDump("20140123", DumpContentType.FULL, true);
		setLocalDump("20140106", DumpContentType.CURRENT, true);
		setLocalDump("20131201", DumpContentType.FULL, true);
		setLocalDump("nodate", DumpContentType.FULL, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, wrf);

		List<? extends MwDumpFile> dumpFiles = dumpFileManager
				.findAllDumps(DumpContentType.FULL);

		String[] dumpDates = { "20140210", "20140123", "20140106", "20131221",
				"20131201" };
		boolean[] dumpIsLocal = { false, true, false, false, true };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					DumpContentType.FULL);
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
		setLocalDump("20140220", DumpContentType.DAILY, true);
		setLocalDump("20140205", DumpContentType.DAILY, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, null);

		List<? extends MwDumpFile> dumpFiles = dumpFileManager
				.findAllDumps(DumpContentType.DAILY);

		String[] dumpDates = { "20140220", "20140205" };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					DumpContentType.DAILY);
			assertEquals(dumpFiles.get(i).getDateStamp(), dumpDates[i]);
			assertTrue(dumpFiles.get(i) instanceof WmfLocalDumpFile);
		}
	}

	@Test
	public void getAllCurrentDumpsOffline() throws IOException {
		setLocalDump("20140220", DumpContentType.CURRENT, true);
		setLocalDump("20140205", DumpContentType.CURRENT, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, null);

		List<? extends MwDumpFile> dumpFiles = dumpFileManager
				.findAllDumps(DumpContentType.CURRENT);

		String[] dumpDates = { "20140220", "20140205" };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					DumpContentType.CURRENT);
			assertEquals(dumpFiles.get(i).getDateStamp(), dumpDates[i]);
			assertTrue(dumpFiles.get(i) instanceof WmfLocalDumpFile);
		}
	}

	@Test
	public void getAllFullDumpsOffline() throws IOException {
		setLocalDump("20140220", DumpContentType.FULL, true);
		setLocalDump("20140205", DumpContentType.FULL, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, null);

		List<? extends MwDumpFile> dumpFiles = dumpFileManager
				.findAllDumps(DumpContentType.FULL);

		String[] dumpDates = { "20140220", "20140205" };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			assertEquals(dumpFiles.get(i).getDumpContentType(),
					DumpContentType.FULL);
			assertEquals(dumpFiles.get(i).getDateStamp(), dumpDates[i]);
			assertTrue(dumpFiles.get(i) instanceof WmfLocalDumpFile);
		}
	}

	@Test
	public void getAllRelevantDumps() throws IOException {
		wrf.setWebResourceContentsFromResource(
				"https://dumps.wikimedia.org/other/incr/wikidatawiki/",
				"/other-incr-wikidatawiki-index.html", this.getClass());
		wrf.setWebResourceContentsFromResource(
				"https://dumps.wikimedia.org/wikidatawiki/",
				"/wikidatawiki-index-old.html", this.getClass());
		wrf.setWebResourceContentsFromResource(
				"https://dumps.wikimedia.org/wikidatawiki/20140210/wikidatawiki-20140210-md5sums.txt",
				"/wikidatawiki-20140210-md5sums.txt", this.getClass());

		setLocalDump("20140220", DumpContentType.DAILY, true);
		setLocalDump("20140219", DumpContentType.FULL, true);
		setLocalDump("20140205", DumpContentType.DAILY, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, wrf);

		List<? extends MwDumpFile> dumpFiles = dumpFileManager
				.findAllRelevantRevisionDumps(true);

		String[] dumpDates = { "20140221", "20140220", "20140219", "20140218",
				"20140217", "20140216", "20140215", "20140214", "20140213",
				"20140212", "20140211", "20140210" };
		boolean[] dumpIsLocal = { false, true, false, false, false, false,
				false, false, false, false, false, false };

		assertEquals(dumpFiles.size(), dumpDates.length);
		for (int i = 0; i < dumpFiles.size(); i++) {
			if (i == dumpFiles.size() - 1) {
				assertEquals(dumpFiles.get(i).getDumpContentType(),
						DumpContentType.CURRENT);
			} else {
				assertEquals(dumpFiles.get(i).getDumpContentType(),
						DumpContentType.DAILY);
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
		setLocalDump("20140220", DumpContentType.DAILY, true);
		setLocalDump("20140210", DumpContentType.CURRENT, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, wrf);

		List<? extends MwDumpFile> dumpFiles = dumpFileManager
				.findAllRelevantRevisionDumps(false);

		assertEquals(dumpFiles.size(), 1);
		assertEquals(dumpFiles.get(0).getDumpContentType(),
				DumpContentType.DAILY);
		assertEquals(dumpFiles.get(0).getDateStamp(), "20140220");
		assertTrue(dumpFiles.get(0) instanceof WmfLocalDumpFile);
	}

	@Test
	public void processAllRelevantDumps() throws IOException {

		setLocalDump("20140221", DumpContentType.DAILY, true);
		setLocalDump("20140220", DumpContentType.DAILY, true);
		setLocalDump("20140219", DumpContentType.CURRENT, true);

		WmfDumpFileManager dumpFileManager = new WmfDumpFileManager(
				"wikidatawiki", dm, null);

		TestDumpfileProcessor dfp = new TestDumpfileProcessor();

		for (MwDumpFile dumpFile : dumpFileManager
				.findAllRelevantRevisionDumps(true)) {
			dfp.processDumpFileContents(dumpFile.getDumpFileStream(), dumpFile);
		}

		assertEquals(
				dfp.result,
				"Contents of daily 20140221\nContents of daily 20140220\nContents of current 20140219\n");
	}

}
