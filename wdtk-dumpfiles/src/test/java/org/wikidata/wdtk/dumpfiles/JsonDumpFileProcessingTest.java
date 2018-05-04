package org.wikidata.wdtk.dumpfiles;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.dumpfiles.wmf.WmfDumpFile;
import org.wikidata.wdtk.testing.MockDirectoryManager;
import org.wikidata.wdtk.testing.MockStringContentFactory;
import org.wikidata.wdtk.util.Timer;

public class JsonDumpFileProcessingTest {

	/**
	 * Test class that delays processing to provoke a timeout.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	private class SlowDocumentProcessor implements EntityDocumentProcessor {

		int itemCount = 0;

		@Override
		public void processItemDocument(ItemDocument itemDocument) {
			itemCount++;
			if (itemCount == 1) {
				Timer timer = Timer.getNamedTimer("delayTimer");
				timer.start();
				boolean busywait = true;
				while (busywait) {
					timer.stop();
					if (timer.getTotalCpuTime() > 1000000000) {
						busywait = false;
					}
					timer.start();
				}
			}
		}

	}

	@Test
	public void testRegularJsonProcessing() throws IOException {
		Path dmPath = Paths.get(System.getProperty("user.dir"));
		MockDirectoryManager dm = new MockDirectoryManager(dmPath, true, true);
		setLocalJsonDumpFile("mock-dump-for-testing.json", "20150223", dm);

		DumpProcessingController dpc = new DumpProcessingController(
				"wikidatawiki");
		dpc.downloadDirectoryManager = dm;
		dpc.setOfflineMode(true);

		EntityTimerProcessor timer = new EntityTimerProcessor(0);
		dpc.registerEntityDocumentProcessor(timer, null, true);

		timer.open();
		dpc.processMostRecentJsonDump();
		timer.close();

		assertEquals(3, timer.entityCount);
	}

	@Test
	public void testBuggyJsonProcessing() throws IOException {
		Path dmPath = Paths.get(System.getProperty("user.dir"));
		MockDirectoryManager dm = new MockDirectoryManager(dmPath, true, true);
		setLocalJsonDumpFile("mock-dump-with-bugs.json", "20150223", dm);

		DumpProcessingController dpc = new DumpProcessingController(
				"wikidatawiki");
		dpc.downloadDirectoryManager = dm;
		dpc.setOfflineMode(true);

		EntityTimerProcessor timer = new EntityTimerProcessor(0);
		dpc.registerEntityDocumentProcessor(timer, null, true);

		timer.open();
		dpc.processMostRecentJsonDump();
		timer.close();

		assertTrue(timer.entityCount >= 3);
	}

	@Test(expected = EntityTimerProcessor.TimeoutException.class)
	public void testTimeout() throws IOException {
		Path dmPath = Paths.get(System.getProperty("user.dir"));
		MockDirectoryManager dm = new MockDirectoryManager(dmPath, true, true);
		setLocalJsonDumpFile("mock-dump-for-long-testing.json", "20150223", dm);

		DumpProcessingController dpc = new DumpProcessingController(
				"wikidatawiki");
		dpc.downloadDirectoryManager = dm;
		dpc.setOfflineMode(true);

		EntityTimerProcessor timer = new EntityTimerProcessor(1);
		timer.setReportInterval(1);
		dpc.registerEntityDocumentProcessor(timer, null, true);
		dpc.registerEntityDocumentProcessor(new SlowDocumentProcessor(), null,
				true);

		timer.open();
		dpc.processMostRecentJsonDump();
		timer.close();
	}

	@Test
	public void testNonTimeout() throws IOException {
		Path dmPath = Paths.get(System.getProperty("user.dir"));
		MockDirectoryManager dm = new MockDirectoryManager(dmPath, true, true);
		setLocalJsonDumpFile("mock-dump-for-long-testing.json", "20150223", dm);

		DumpProcessingController dpc = new DumpProcessingController(
				"wikidatawiki");
		dpc.downloadDirectoryManager = dm;
		dpc.setOfflineMode(true);

		EntityTimerProcessor timer = new EntityTimerProcessor(0);
		dpc.registerEntityDocumentProcessor(timer, null, true);
		dpc.registerEntityDocumentProcessor(new SlowDocumentProcessor(), null,
				true);

		timer.open();
		dpc.processMostRecentJsonDump();
		timer.close();

		assertEquals(101, timer.entityCount);
	}

	private void setLocalJsonDumpFile(String fileName, String dateStamp,
			MockDirectoryManager dm) throws IOException {

		DumpContentType dumpContentType = DumpContentType.JSON;
		URL resourceUrl = MwDumpFileProcessingTest.class.getResource("/"
				+ fileName);
		Path dmPath = Paths.get(System.getProperty("user.dir"));
		Path dumpFilePath = dmPath.resolve("dumpfiles").resolve("wikidatawiki");
		Path thisDumpPath = dumpFilePath.resolve(dumpContentType.toString()
				.toLowerCase() + "-" + dateStamp);
		Path filePath = thisDumpPath.resolve(dateStamp + WmfDumpFile.getDumpFilePostfix(dumpContentType));
		dm.setFileContents(filePath, MockStringContentFactory.getStringFromUrl(resourceUrl),
				WmfDumpFile.getDumpFileCompressionType(filePath.toString()));
	}

}
