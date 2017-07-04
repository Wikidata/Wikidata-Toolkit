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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.testing.MockDirectoryManager;
import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.util.DirectoryManagerFactory;

public class MwLocalDumpFileTest {
	MockDirectoryManager dm;
	Path dmPath;

	@Before
	public void setUp() throws Exception {
		DirectoryManagerFactory
				.setDirectoryManagerClass(MockDirectoryManager.class);

		this.dmPath = Paths.get("/").toAbsolutePath();
		this.dm = new MockDirectoryManager(this.dmPath, true, true);
	}

	@Test
	public void missingDumpFile() throws IOException {
		MwLocalDumpFile df = new MwLocalDumpFile(
				"/non-existing-dump-file.json.gz");
		assertFalse(df.isAvailable());
	}

	@Test
	public void missingDumpFileDirectory() throws IOException {
		MwLocalDumpFile df = new MwLocalDumpFile(
				"/nonexisting-directory/non-existing-file.json.gz");
		assertFalse(df.isAvailable());
	}

	@Test
	public void testExplicitGetters() throws IOException {
		this.dm.setFileContents(this.dmPath
				.resolve("testdump-20150512.json.gz"), "");
		MwLocalDumpFile df = new MwLocalDumpFile(
				"/testdump-20150512.json.gz",
				DumpContentType.SITES, "20150815",
				"wikidatawiki");

		assertEquals("20150815", df.getDateStamp());
		assertEquals("wikidatawiki", df.getProjectName());
		assertEquals(DumpContentType.SITES, df.getDumpContentType());
		String toString = df.toString();

		assertEquals(this.dmPath.resolve("testdump-20150512.json.gz"),
				df.getPath());

		assertTrue(toString.contains("20150815"));
		assertTrue(toString.contains("wikidatawiki"));
		assertTrue(toString.toLowerCase().contains(
				DumpContentType.SITES.toString().toLowerCase()));
	}

	@Test
	public void testGuessJsonDumpAndDate() throws IOException {
		this.dm.setFileContents(this.dmPath
				.resolve("testdump-20150512.json.gz"), "");
		MwLocalDumpFile df = new MwLocalDumpFile(
				"/testdump-20150512.json.gz");
		assertTrue(df.isAvailable());
		assertEquals("20150512", df.getDateStamp());
		assertEquals("LOCAL", df.getProjectName());
		assertEquals(df.getDumpContentType(), DumpContentType.JSON);
	}

	@Test
	public void testJsonReader() throws IOException {
		this.dm.setFileContents(this.dmPath
				.resolve("testdump-20150512.json.gz"),
				"Test contents", CompressionType.GZIP);
		MwLocalDumpFile df = new MwLocalDumpFile(
				"/testdump-20150512.json.gz");
		BufferedReader br = df.getDumpFileReader();
		assertEquals("Test contents", br.readLine());
		assertTrue(br.readLine() == null);
	}

	@Test(expected = IOException.class)
	public void testUnavailableReader() throws IOException {
		MwLocalDumpFile df = new MwLocalDumpFile(
				"/testdump-20150512.json.gz");
		df.getDumpFileReader();
	}

	@Test
	public void testGuessSitesDump() throws IOException {
		this.dm.setFileContents(this.dmPath.resolve("test.sql.gz"), "");
		MwLocalDumpFile df = new MwLocalDumpFile("/test.sql.gz");
		assertTrue(df.isAvailable());
		assertEquals("YYYYMMDD", df.getDateStamp());
		assertEquals(df.getDumpContentType(), DumpContentType.SITES);
	}

	@Test
	public void testGuessFullDump() throws IOException {
		this.dm.setFileContents(this.dmPath.resolve("test.xml.bz2"), "");
		MwLocalDumpFile df = new MwLocalDumpFile("/test.xml.bz2");
		assertTrue(df.isAvailable());
		assertEquals(df.getDumpContentType(), DumpContentType.FULL);
	}

	@Test
	public void testGuessDailyDump() throws IOException {
		this.dm.setFileContents(
				this.dmPath.resolve("daily-dump.xml.bz2"), "");
		MwLocalDumpFile df = new MwLocalDumpFile("/daily-dump.xml.bz2");
		assertTrue(df.isAvailable());
		assertEquals(df.getDumpContentType(), DumpContentType.DAILY);
	}

	@Test
	public void testGuessCurrentDump() throws IOException {
		this.dm.setFileContents(
				this.dmPath.resolve("current-dump.xml.bz2"), "");
		MwLocalDumpFile df = new MwLocalDumpFile(
				"/current-dump.xml.bz2");
		assertTrue(df.isAvailable());
		assertEquals(df.getDumpContentType(), DumpContentType.CURRENT);
	}

	@Test
	public void testGuessUnknownDumpType() throws IOException {
		this.dm.setFileContents(this.dmPath.resolve("current-dump"), "");
		MwLocalDumpFile df = new MwLocalDumpFile("/current-dump");
		assertTrue(df.isAvailable());
		assertEquals(df.getDumpContentType(), DumpContentType.JSON);
	}

}
