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

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.dumpfiles.wmf.WmfDumpFile;
import org.wikidata.wdtk.dumpfiles.wmf.WmfLocalDumpFile;
import org.wikidata.wdtk.testing.MockDirectoryManager;
import org.wikidata.wdtk.testing.MockStringContentFactory;
import org.wikidata.wdtk.util.DirectoryManagerFactory;

public class MwDumpFileProcessingTest {

	/**
	 * Helper class that stores all information passed to it for later testing.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	class TestMwRevisionProcessor implements MwRevisionProcessor {

		final List<MwRevision> revisions = new ArrayList<>();
		String siteName;
		String baseUrl;
		Map<Integer, String> namespaces;

		@Override
		public void startRevisionProcessing(String siteName, String baseUrl,
				Map<Integer, String> namespaces) {
			this.siteName = siteName;
			this.baseUrl = baseUrl;
			this.namespaces = namespaces;
		}

		@Override
		public void processRevision(MwRevision mwRevision) {
			this.revisions.add(new MwRevisionImpl(mwRevision));
		}

		@Override
		public void finishRevisionProcessing() {
		}

	}

	/**
	 * Helper class that counts how many items it gets.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	class TestEntityDocumentProcessor implements EntityDocumentProcessor {

		int itemCount = 0;
		int propCount = 0;

		@Override
		public void processItemDocument(ItemDocument itemDocument) {
			this.itemCount++;
		}

		@Override
		public void processPropertyDocument(PropertyDocument propertyDocument) {
			this.propCount++;
		}

	}

	@Before
	public void configureDirectoryManager() {
		DirectoryManagerFactory
				.setDirectoryManagerClass(MockDirectoryManager.class);
	}

	/**
	 * Generates a simple item revision for testing purposes.
	 *
	 * @param number
	 */
	private MwRevision getItemRevision(int number) {
		MwRevisionImpl result = new MwRevisionImpl();
		result.prefixedTitle = "Q1";
		result.namespace = 0;
		result.pageId = 32;
		result.revisionId = number;
		result.parentRevisionId = number - 1;
		result.timeStamp = "2014-02-19T23:34:1" + (number % 10) + "Z";
		result.format = "application/json";
		result.model = MwRevision.MODEL_WIKIBASE_ITEM;
		result.comment = "Test comment " + number;
		result.text = "{\"id\":\"Q1\",\"type\":\"item\",\"labels\":{\"en\":{\"language\":\"en\",\"value\":\"Revision "
				+ number + "\"}}}";
		result.contributor = "127.0.0." + (number % 256);
		result.contributorId = -1;
		return result;
	}

	/**
	 * Generates a simple property revision for testing purposes.
	 *
	 * @param number
	 */
	private MwRevision getPropertyRevision(int number) {
		MwRevisionImpl result = new MwRevisionImpl();
		result.prefixedTitle = "Property:P1";
		result.namespace = 120;
		result.pageId = 12345;
		result.revisionId = number + 10000;
		result.parentRevisionId = number + 9999;
		result.timeStamp = "2014-02-19T23:34:1" + (number % 10) + "Z";
		result.format = "application/json";
		result.model = MwRevision.MODEL_WIKIBASE_PROPERTY;
		result.comment = "Test comment " + (number + 10000);
		result.text = "{\"id\":\"P1\",\"type\":\"property\",\"labels\":{\"en\":{\"language\":\"en\",\"value\":\"Revision "
				+ (number + 10000) + "\"}},\"datatype\":\"wikibase-item\"}";
		result.contributor = "127.0.0." + (number % 256);
		result.contributorId = -1;
		return result;
	}

	/**
	 * Generates a simple lexeme revision for testing purposes.
	 *
	 * @param number
	 */
	private MwRevision getLexemeRevision(int number) {
		MwRevisionImpl result = new MwRevisionImpl();
		result.prefixedTitle = "Lexeme:L1";
		result.namespace = 122;
		result.pageId = 1235667;
		result.revisionId = number + 100000;
		result.parentRevisionId = number + 8888;
		result.timeStamp = "2014-02-19T23:34:1" + (number % 10) + "Z";
		result.format = "application/json";
		result.model = MwRevision.MODEL_WIKIBASE_LEXEME;
		result.comment = "Test comment " + (number + 10000);
		result.text = "{\"type\":\"lexeme\",\"id\":\"L1\",\"lexicalCategory\":\"Q" + number / 2 + "\",\"language\":\"Q" + number + "\"}";
		result.contributor = "127.0.0." + (number % 256);
		result.contributorId = -1;
		return result;
	}

	/**
	 * Generates a simple page revision for testing purposes.
	 *
	 * @param number
	 */
	private MwRevision getPageRevision(int number) {
		MwRevisionImpl result = new MwRevisionImpl();
		result.prefixedTitle = "Wikidata:Contact the development team";
		result.namespace = 4;
		result.pageId = 181;
		result.revisionId = 110689110 + number;
		result.parentRevisionId = 110689109 + number;
		result.timeStamp = "2014-02-20T23:34:1" + number + "Z";
		result.format = "text/x-wiki";
		result.model = MwRevision.MODEL_WIKITEXT;
		result.comment = "Test comment " + number;
		result.text = "Test wikitext " + number + "\nLine 2\nLine 3";
		result.contributor = "User " + number;
		result.contributorId = 1000 + number;
		return result;
	}

	/**
	 * Assert that two revisions are equal. Better than using equals() since it
	 * generates more useful error reports.
	 *
	 * @param rev1
	 * @param rev2
	 */
	private void assertEqualRevisions(MwRevision rev1, MwRevision rev2,
			String test) {
		assertEquals("[" + test + "] Revision prefixed titles do not match:",
				rev1.getPrefixedTitle(), rev2.getPrefixedTitle());
		assertEquals("[" + test + "] Revision namespaces do not match:",
				rev1.getNamespace(), rev2.getNamespace());
		assertEquals("[" + test + "] Revision page ids do not match:",
				rev1.getPageId(), rev2.getPageId());
		assertEquals("[" + test + "] Revision ids do not match:",
				rev1.getRevisionId(), rev2.getRevisionId());
		assertEquals("[" + test + "] Revision parent ids do not match:",
				rev1.getParentRevisionId(), rev2.getParentRevisionId());
		assertEquals("[" + test + "] Revision timestamps do not match:",
				rev1.getTimeStamp(), rev2.getTimeStamp());
		assertEquals("[" + test + "] Revision formats do not match:",
				rev1.getFormat(), rev2.getFormat());
		assertEquals("[" + test + "] Revision models do not match:",
				rev1.getModel(), rev2.getModel());
		assertEquals("[" + test + "] Revision comments do not match:",
				rev1.getComment(), rev2.getComment());
		assertEquals("[" + test + "] Revision texts do not match:",
				rev1.getText(), rev2.getText());
		assertEquals("[" + test + "] Revision contributors do not match:",
				rev1.getContributor(), rev2.getContributor());
		assertEquals("[" + test + "] Revision contributor ids do not match:",
				rev1.getContributorId(), rev2.getContributorId());
	}

	/**
	 * Assert that two lists contain the same revisions in the same order.
	 *
	 * @param list1
	 * @param list2
	 */
	private void assertEqualRevisionLists(List<MwRevision> list1,
			List<MwRevision> list2, String test) {
		assertEquals("[" + test + "] Size of revision lists does not match:",
				list1.size(), list2.size());
		for (int i = 0; i < list1.size(); i++) {
			assertEqualRevisions(list1.get(i), list2.get(i), test + "-item" + i);
		}
	}

	@Test
	public void testIncompleteDumpFile() throws IOException {
		URL resourceUrl = MwDumpFileProcessingTest.class
				.getResource("/mock-dump-incomplete-revision.xml");
		MwDumpFile mockDumpFile = Mockito.mock(WmfLocalDumpFile.class);

		MwRevisionProcessorBroker mwrpBroker = new MwRevisionProcessorBroker();

		TestMwRevisionProcessor tmrpAll = new TestMwRevisionProcessor();
		mwrpBroker.registerMwRevisionProcessor(tmrpAll, null, false);

		MwRevisionDumpFileProcessor mwdfp = new MwRevisionDumpFileProcessor(
				mwrpBroker);
		mwdfp.processDumpFileContents(resourceUrl.openStream(), mockDumpFile);

		List<MwRevision> revisionsAll = new ArrayList<>();
		revisionsAll.add(getItemRevision(4));

		assertEqualRevisionLists(revisionsAll, tmrpAll.revisions,
				"all-incomplete");
	}

	@Test
	public void testBuggyDumpFile() throws IOException {
		URL resourceUrl = MwDumpFileProcessingTest.class
				.getResource("/mock-dump-with-bugs.xml");
		MwDumpFile mockDumpFile = Mockito.mock(WmfLocalDumpFile.class);

		MwRevisionProcessorBroker mwrpBroker = new MwRevisionProcessorBroker();

		TestMwRevisionProcessor tmrpAll = new TestMwRevisionProcessor();
		mwrpBroker.registerMwRevisionProcessor(tmrpAll, null, false);

		MwRevisionDumpFileProcessor mwdfp = new MwRevisionDumpFileProcessor(
				mwrpBroker);
		mwdfp.processDumpFileContents(resourceUrl.openStream(), mockDumpFile);

		List<MwRevision> revisionsAll = new ArrayList<>();
		revisionsAll.add(getItemRevision(4));
		revisionsAll.add(getItemRevision(5));
		revisionsAll.add(getPageRevision(1));
		revisionsAll.add(getPageRevision(2));

		assertEqualRevisionLists(revisionsAll, tmrpAll.revisions,
				"all-incomplete");
	}

	private void setLocalDumpFile(String dateStamp,
			DumpContentType dumpContentType, MockDirectoryManager dm)
			throws IOException {
		URL resourceUrl = MwDumpFileProcessingTest.class
				.getResource("/mock-dump-for-testing.xml");
		Path dmPath = Paths.get(System.getProperty("user.dir"));
		Path dumpFilePath = dmPath.resolve("dumpfiles").resolve("wikidatawiki");
		Path thisDumpPath = dumpFilePath.resolve(dumpContentType.toString()
				.toLowerCase() + "-" + dateStamp);
		Path filePath = thisDumpPath.resolve("wikidatawiki-" + dateStamp
				+ WmfDumpFile.getDumpFilePostfix(dumpContentType));
		dm.setFileContents(filePath, MockStringContentFactory.getStringFromUrl(resourceUrl),
				WmfDumpFile.getDumpFileCompressionType(filePath.toString()));
	}

	/**
	 * Creates a mocked local dump file with three pages, each with three
	 * revisions starting from the given baseId (plus some offset per page).
	 *
	 * @param dateStamp
	 * @param baseId
	 * @param dumpContentType
	 * @param dm
	 * @throws IOException
	 */
	private void mockLocalDumpFile(String dateStamp, int baseId,
			DumpContentType dumpContentType, MockDirectoryManager dm)
			throws IOException {
		Path dmPath = Paths.get(System.getProperty("user.dir"));
		Path dumpFilePath = dmPath.resolve("dumpfiles").resolve("wikidatawiki");

		dm.setDirectory(dmPath);
		dm.setDirectory(dmPath.resolve("dumpfiles"));
		dm.setDirectory(dumpFilePath);

		Path thisDumpPath = dumpFilePath.resolve(dumpContentType.toString()
				.toLowerCase() + "-" + dateStamp);

		URL resourceUrl = MwDumpFileProcessingTest.class
				.getResource("/mock-dump-header.xml");
		String dumpContents = MockStringContentFactory
				.getStringFromUrl(resourceUrl);
		for (int pageId = baseId; pageId < baseId + 3; pageId++) {
			dumpContents += "  <page>\n";
			dumpContents += "    <title>Q" + pageId + "</title>\n";
			dumpContents += "    <ns>0</ns>\n";
			dumpContents += "    <id>" + (pageId + 1000) + "</id>\n";
			for (int revId = pageId * 1000 + baseId + 1; revId < pageId * 1000
					+ baseId + 4; revId++) {
				dumpContents += "    <revision>\n";
				dumpContents += "      <id>" + revId + "</id>\n";
				dumpContents += "      <parentid>" + (revId - 1)
						+ "</parentid>\n";
				dumpContents += "      <timestamp>2014-02-19T23:34:0"
						+ (revId % 10) + "</timestamp>\n";
				dumpContents += "      <contributor>";
				dumpContents += "        <ip>127.0.0." + (revId % 256)
						+ "</ip>\n";
				dumpContents += "      </contributor>\n";
				dumpContents += "      <comment>Test comment " + revId
						+ "</comment>\n";
				dumpContents += "      <text xml:space=\"preserve\">{&quot;label&quot;:{&quot;en&quot;:&quot;Revision "
						+ revId + "&quot;}}</text>\n";
				dumpContents += "      <sha1>ignored</sha1>";
				dumpContents += "      <model>wikibase-item</model>";
				dumpContents += "      <format>application/json</format>";
				dumpContents += "    </revision>\n";
			}
			dumpContents += "  </page>\n";
		}
		dumpContents += "</mediawiki>\n";

		Path filePath = thisDumpPath.resolve("wikidatawiki-" + dateStamp
				+ WmfDumpFile.getDumpFilePostfix(dumpContentType));
		dm.setFileContents(filePath, dumpContents, WmfDumpFile.getDumpFileCompressionType(filePath.toString()));
	}

	@Test
	public void testMwDailyDumpFileProcessing() throws IOException {
		Path dmPath = Paths.get(System.getProperty("user.dir"));
		MockDirectoryManager dm = new MockDirectoryManager(dmPath, true);
		setLocalDumpFile("20140420", DumpContentType.DAILY, dm);

		DumpProcessingController dpc = new DumpProcessingController(
				"wikidatawiki");
		dpc.downloadDirectoryManager = dm;
		dpc.setOfflineMode(true);

		StatisticsMwRevisionProcessor mwrpAllStats = new StatisticsMwRevisionProcessor(
				"all", 2);
		dpc.registerMwRevisionProcessor(mwrpAllStats, null, false);

		TestMwRevisionProcessor tmrpAll = new TestMwRevisionProcessor();
		dpc.registerMwRevisionProcessor(tmrpAll, null, false);
		TestMwRevisionProcessor tmrpAllCurrent = new TestMwRevisionProcessor();
		dpc.registerMwRevisionProcessor(tmrpAllCurrent, null, true);
		TestMwRevisionProcessor tmrpAllItems = new TestMwRevisionProcessor();
		dpc.registerMwRevisionProcessor(tmrpAllItems,
				MwRevision.MODEL_WIKIBASE_ITEM, false);
		TestEntityDocumentProcessor edpCurrentCounter = new TestEntityDocumentProcessor();
		dpc.registerEntityDocumentProcessor(edpCurrentCounter,
				MwRevision.MODEL_WIKIBASE_ITEM, true);
		dpc.registerEntityDocumentProcessor(edpCurrentCounter,
				MwRevision.MODEL_WIKIBASE_PROPERTY, true);
		TestEntityDocumentProcessor edpAllCounter = new TestEntityDocumentProcessor();
		dpc.registerEntityDocumentProcessor(edpAllCounter,
				MwRevision.MODEL_WIKIBASE_ITEM, false);
		dpc.registerEntityDocumentProcessor(edpAllCounter,
				MwRevision.MODEL_WIKIBASE_PROPERTY, false);
		TestMwRevisionProcessor tmrpAllProperties = new TestMwRevisionProcessor();
		dpc.registerMwRevisionProcessor(tmrpAllProperties,
				MwRevision.MODEL_WIKIBASE_PROPERTY, false);
		TestMwRevisionProcessor tmrpAllLexemes = new TestMwRevisionProcessor();
		dpc.registerMwRevisionProcessor(tmrpAllLexemes,
				MwRevision.MODEL_WIKIBASE_LEXEME, false);

		dpc.processDump(dpc.getMostRecentDump(DumpContentType.DAILY));

		List<MwRevision> revisionsAllItems = new ArrayList<>();
		revisionsAllItems.add(getItemRevision(4));
		revisionsAllItems.add(getItemRevision(5));
		revisionsAllItems.add(getItemRevision(3));
		revisionsAllItems.add(getItemRevision(2));

		List<MwRevision> revisionsAllProperties = new ArrayList<>();
		revisionsAllProperties.add(getPropertyRevision(4));
		revisionsAllProperties.add(getPropertyRevision(5));

		List<MwRevision> revisionsAllLexemes = new ArrayList<>();
		revisionsAllLexemes.add(getLexemeRevision(9));
		revisionsAllLexemes.add(getLexemeRevision(10));

		List<MwRevision> revisionsAll = new ArrayList<>(revisionsAllItems);
		revisionsAll.add(getPageRevision(1));
		revisionsAll.add(getPageRevision(2));
		revisionsAll.addAll(revisionsAllProperties);
		revisionsAll.addAll(revisionsAllLexemes);

		List<MwRevision> revisionsAllCurrent = new ArrayList<>();
		revisionsAllCurrent.add(getItemRevision(5));
		revisionsAllCurrent.add(getPageRevision(2));
		revisionsAllCurrent.add(getPropertyRevision(5));
		revisionsAllCurrent.add(getLexemeRevision(10));

		assertEquals("Wikidata Toolkit Test", tmrpAll.siteName);
		assertEquals(revisionsAll.size(), mwrpAllStats.getTotalRevisionCount());
		assertEquals(revisionsAll.size(),
				mwrpAllStats.getCurrentRevisionCount());
		assertEqualRevisionLists(revisionsAll, tmrpAll.revisions, "all");
		assertEqualRevisionLists(revisionsAllItems, tmrpAllItems.revisions,
				"allitems");
		assertEqualRevisionLists(revisionsAllCurrent, tmrpAllCurrent.revisions,
				"allcurrent");
		assertEqualRevisionLists(revisionsAllProperties,
				tmrpAllProperties.revisions, "allproperties");
		assertEqualRevisionLists(revisionsAllLexemes,
				tmrpAllLexemes.revisions, "alllexemes");

		assertEquals(revisionsAllItems.size(), edpAllCounter.itemCount);
		assertEquals(revisionsAllProperties.size(), edpAllCounter.propCount);
		assertEquals(1, edpCurrentCounter.itemCount);
		assertEquals(1, edpCurrentCounter.propCount);
	}

	@Test
	public void testMwRecentCurrentDumpFileProcessing() throws IOException {
		Path dmPath = Paths.get(System.getProperty("user.dir"));
		MockDirectoryManager dm = new MockDirectoryManager(dmPath, true);
		mockLocalDumpFile("20140420", 4, DumpContentType.DAILY, dm);
		mockLocalDumpFile("20140419", 3, DumpContentType.DAILY, dm);
		mockLocalDumpFile("20140418", 2, DumpContentType.DAILY, dm);
		mockLocalDumpFile("20140417", 1, DumpContentType.DAILY, dm);
		mockLocalDumpFile("20140418", 2, DumpContentType.CURRENT, dm);

		DumpProcessingController dpc = new DumpProcessingController(
				"wikidatawiki");
		dpc.downloadDirectoryManager = dm;
		dpc.setOfflineMode(true);

		StatisticsMwRevisionProcessor mwrpStats = new StatisticsMwRevisionProcessor(
				"stats", 2);
		dpc.registerMwRevisionProcessor(mwrpStats, null, true);

		dpc.processAllRecentRevisionDumps();

		assertEquals(5, mwrpStats.getTotalRevisionCount());
		assertEquals(1, mwrpStats.getCurrentRevisionCount());
	}

	@Test
	public void testMwRecentFullDumpFileProcessing() throws IOException {
		Path dmPath = Paths.get(System.getProperty("user.dir"));
		MockDirectoryManager dm = new MockDirectoryManager(dmPath, true, true);
		mockLocalDumpFile("20140420", 4, DumpContentType.DAILY, dm);
		mockLocalDumpFile("20140419", 3, DumpContentType.DAILY, dm);
		mockLocalDumpFile("20140418", 2, DumpContentType.DAILY, dm);
		mockLocalDumpFile("20140417", 1, DumpContentType.DAILY, dm);
		mockLocalDumpFile("20140418", 2, DumpContentType.FULL, dm);

		DumpProcessingController dpc = new DumpProcessingController(
				"wikidatawiki");
		dpc.downloadDirectoryManager = dm;
		dpc.setOfflineMode(true);

		StatisticsMwRevisionProcessor mwrpStats = new StatisticsMwRevisionProcessor(
				"stats", 2);
		dpc.registerMwRevisionProcessor(mwrpStats, null, false);

		dpc.processAllRecentRevisionDumps();

		assertEquals(19, mwrpStats.getTotalRevisionCount());
		assertEquals(5, mwrpStats.getCurrentRevisionCount());
	}

	@Test
	public void testMwMostRecentFullDumpFileProcessing() throws IOException {
		Path dmPath = Paths.get(System.getProperty("user.dir"));
		MockDirectoryManager dm = new MockDirectoryManager(dmPath, true);
		mockLocalDumpFile("20140418", 2, DumpContentType.FULL, dm);

		DumpProcessingController dpc = new DumpProcessingController(
				"wikidatawiki");
		dpc.downloadDirectoryManager = dm;
		dpc.setOfflineMode(true);

		StatisticsMwRevisionProcessor mwrpStats = new StatisticsMwRevisionProcessor(
				"stats", 2);
		dpc.registerMwRevisionProcessor(mwrpStats, null, false);

		dpc.processMostRecentMainDump();

		assertEquals(9, mwrpStats.getTotalRevisionCount());
		assertEquals(9, mwrpStats.getCurrentRevisionCount());
	}

}
