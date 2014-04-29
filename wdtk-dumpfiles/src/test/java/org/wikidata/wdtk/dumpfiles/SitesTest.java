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
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.testing.MockDirectoryManager;
import org.wikidata.wdtk.testing.MockStringContentFactory;

public class SitesTest {

	MockDirectoryManager dm;
	Path dmPath;
	DumpProcessingController dpc;

	@Before
	public void setUp() throws IOException {
		this.dmPath = Paths.get(System.getProperty("user.dir"));
		this.dm = new MockDirectoryManager(this.dmPath);

		this.dpc = new DumpProcessingController("wikidatawiki");
		this.dpc.downloadDirectoryManager = this.dm;
	}

	@Test
	public void getSiteInformation() throws IOException {
		Path dumpFilePath = this.dmPath.resolve("dumpfiles").resolve(
				"wikidatawiki");
		Path thisDumpPath = dumpFilePath.resolve(DumpContentType.SITES
				.toString().toLowerCase() + "-" + "20140420");

		URL resourceUrl = this.getClass().getResource(
				"/wikidatawiki-20140420-sites.sql");
		dm.setFileContents(thisDumpPath.resolve("wikidatawiki-" + "20140420"
				+ WmfDumpFile.getDumpFilePostfix(DumpContentType.SITES)),
				MockStringContentFactory.getStringFromUrl(resourceUrl));

		this.dpc.setOfflineMode(true);

		DataObjectFactory factory = new DataObjectFactoryImpl();
		SiteLink siteLink = factory.getSiteLink("Douglas Adams", "dewiki",
				Collections.<String> emptyList());

		Sites sites = this.dpc.getSitesInformation();

		assertEquals(sites.getLanguageCode("enwikivoyage"), "en");
		assertEquals(sites.getSiteLinkUrl(siteLink),
				"http://de.wikipedia.org/wiki/Douglas_Adams");
		assertEquals(
				sites.getPageUrl("arwiki", "دوغلاس_آدمز"),
				"http://ar.wikipedia.org/wiki/%D8%AF%D9%88%D8%BA%D9%84%D8%A7%D8%B3_%D8%A2%D8%AF%D9%85%D8%B2");
		assertEquals(sites.getFileUrl("enwiki", "api.php"),
				"http://en.wikipedia.org/w/api.php");

	}

}
