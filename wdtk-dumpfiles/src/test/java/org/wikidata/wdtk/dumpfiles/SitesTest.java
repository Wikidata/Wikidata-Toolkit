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
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.implementation.SitesImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.dumpfiles.wmf.WmfDumpFile;
import org.wikidata.wdtk.testing.MockDirectoryManager;
import org.wikidata.wdtk.testing.MockStringContentFactory;

public class SitesTest {

	MockDirectoryManager dm;
	Path dmPath;
	DumpProcessingController dpc;

	@Before
	public void setUp() throws IOException {
		this.dmPath = Paths.get(System.getProperty("user.dir"));
		this.dm = new MockDirectoryManager(this.dmPath, true, true);

		this.dpc = new DumpProcessingController("wikidatawiki");
		this.dpc.downloadDirectoryManager = this.dm;
	}

	@Test
	public void getSiteInformation() throws IOException {
		Path dumpFilePath = this.dmPath.resolve("dumpfiles").resolve(
				"wikidatawiki");
		Path thisDumpPath = dumpFilePath.resolve(DumpContentType.SITES
				.toString().toLowerCase() + "-" + "20140420");
		dm.setDirectory(dumpFilePath);
		dm.setDirectory(thisDumpPath);

		URL resourceUrl = this.getClass().getResource(
				"/wikidatawiki-20140420-sites.sql");
		Path filePath = thisDumpPath.resolve("wikidatawiki-" + "20140420"
				+ WmfDumpFile.getDumpFilePostfix(DumpContentType.SITES));
		dm.setFileContents(filePath, MockStringContentFactory.getStringFromUrl(resourceUrl),
				WmfDumpFile.getDumpFileCompressionType(filePath.toString()));

		this.dpc.setOfflineMode(true);

		DataObjectFactory factory = new DataObjectFactoryImpl();
		SiteLink siteLink = factory.getSiteLink("Douglas Adams", "dewiki",
				Collections.<String> emptyList());

		Sites sites = this.dpc.getSitesInformation();

		assertEquals("en", sites.getLanguageCode("enwikivoyage"));
		// Test sites with protocol-relative URLs:
		assertEquals(SitesImpl.DEFAULT_PROTOCOL_PREFIX
				+ "//de.wikipedia.org/wiki/Douglas_Adams",
				sites.getSiteLinkUrl(siteLink));
		assertEquals(
				SitesImpl.DEFAULT_PROTOCOL_PREFIX
						+ "//ar.wikipedia.org/wiki/%D8%AF%D9%88%D8%BA%D9%84%D8%A7%D8%B3_%D8%A2%D8%AF%D9%85%D8%B2",
				sites.getPageUrl("arwiki", "دوغلاس_آدمز"));
		assertEquals(SitesImpl.DEFAULT_PROTOCOL_PREFIX
				+ "//en.wikipedia.org/w/api.php",
				sites.getFileUrl("enwiki", "api.php"));

		// Site with explicit http URL:
		assertEquals("http://aa.wikipedia.org/wiki/Test",
				sites.getPageUrl("aawiki", "Test"));

	}

}
