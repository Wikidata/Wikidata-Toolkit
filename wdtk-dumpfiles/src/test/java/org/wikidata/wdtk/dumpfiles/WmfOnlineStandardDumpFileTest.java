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

public class WmfOnlineStandardDumpFileTest {

	MockWebResourceFetcher wrf;
	MockDirectoryManager dm;

	@Before
	public void setUp() throws IOException {
		dm = new MockDirectoryManager(Paths.get(System.getProperty("user.dir")));

		wrf = new MockWebResourceFetcher();
	}

	@Test
	public void validCurrentDumpProperties() throws IOException {
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/",
				"/wikidatawiki-20140210-index.html", this.getClass());
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/wikidatawiki-20140210-pages-meta-current.xml.bz2",
				"Line1");
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/wikidatawiki-20140210-md5sums.txt",
				"/wikidatawiki-20140210-md5sums.txt", this.getClass());
		MwDumpFile dump = new WmfOnlineStandardDumpFile("20140210",
				"wikidatawiki", wrf, dm, DumpContentType.CURRENT);

		BufferedReader br = dump.getDumpFileReader();

		assertEquals(br.readLine(), "Line1");
		assertEquals(br.readLine(), null);
		assertTrue(dump.isAvailable());
		assertEquals(new Long(108891795), dump.getMaximalRevisionId());
		assertEquals("20140210", dump.getDateStamp());
		assertEquals(DumpContentType.CURRENT, dump.getDumpContentType());
	}

	@Test
	public void missingFullDumpProperties() {
		MwDumpFile dump = new WmfOnlineStandardDumpFile("20140210",
				"wikidatawiki", wrf, dm, DumpContentType.FULL);

		assertTrue(!dump.isAvailable());
		assertEquals(dump.getMaximalRevisionId(), new Long(-1));
		assertEquals("20140210", dump.getDateStamp());
	}

	@Test
	public void inaccessibleCurrentDumpProperties() throws IOException {
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/",
				"/wikidatawiki-20140210-index.html", this.getClass());
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/wikidatawiki-20140210-pages-meta-current.xml.bz2",
				"Line1");
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/wikidatawiki-20140210-md5sums.txt",
				"/wikidatawiki-20140210-md5sums.txt", this.getClass());
		wrf.setReturnFailingReaders(true);

		MwDumpFile dump = new WmfOnlineStandardDumpFile("20140210",
				"wikidatawiki", wrf, dm, DumpContentType.CURRENT);

		assertTrue(!dump.isAvailable());
		assertEquals(new Long(-1), dump.getMaximalRevisionId());
	}

	@Test
	public void emptyFullDumpIsDone() throws IOException {
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/",
				"/wikidatawiki-20140210-index.html", this.getClass());
		MwDumpFile dump = new WmfOnlineStandardDumpFile("20140210",
				"wikidatawiki", wrf, dm, DumpContentType.FULL);

		assertTrue(!dump.isAvailable());
		assertEquals(new Long(108995868), dump.getMaximalRevisionId());
		assertEquals("20140210", dump.getDateStamp());
		assertEquals(DumpContentType.FULL, dump.getDumpContentType());
	}

	@Test
	public void emptyFullDumpRevisionId() throws IOException {
		MwDumpFile dump = new WmfOnlineStandardDumpFile("20140210",
				"wikidatawiki", wrf, dm, DumpContentType.FULL);

		assertEquals(new Long(-1), dump.getMaximalRevisionId());
	}

	@Test(expected = IOException.class)
	public void downloadNoRevisionId() throws IOException {
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/wikidatawiki-20140210-pages-meta-current.xml.bz2",
				"Line1");
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/wikidatawiki-20140210-md5sums.txt",
				"/wikidatawiki-20140210-md5sums.txt", this.getClass());
		MwDumpFile dump = new WmfOnlineStandardDumpFile("20140210",
				"wikidatawiki", wrf, dm, DumpContentType.FULL);
		dump.getDumpFileReader();
	}

	@Test(expected = IOException.class)
	public void downloadNoMd5sum() throws IOException {
		wrf.setWebResourceContents(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/wikidatawiki-20140210-pages-meta-current.xml.bz2",
				"Line1");
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/",
				"/wikidatawiki-20140210-index.html", this.getClass());
		MwDumpFile dump = new WmfOnlineStandardDumpFile("20140210",
				"wikidatawiki", wrf, dm, DumpContentType.FULL);
		dump.getDumpFileReader();
	}

	@Test(expected = IOException.class)
	public void downloadNoDumpFile() throws IOException {
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/",
				"/wikidatawiki-20140210-index.html", this.getClass());
		wrf.setWebResourceContentsFromResource(
				"http://dumps.wikimedia.org/wikidatawiki/20140210/wikidatawiki-20140210-md5sums.txt",
				"/wikidatawiki-20140210-md5sums.txt", this.getClass());
		MwDumpFile dump = new WmfOnlineStandardDumpFile("20140210",
				"wikidatawiki", wrf, dm, DumpContentType.CURRENT);
		dump.getDumpFileReader();
	}

}
