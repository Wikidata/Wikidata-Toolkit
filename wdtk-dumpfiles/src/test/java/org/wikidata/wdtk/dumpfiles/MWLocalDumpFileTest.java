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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.testing.MockDirectoryManager;

public class MWLocalDumpFileTest {
	MockDirectoryManager dm;
	Path dmPath;

	@Before
	public void setUp() throws Exception {
		this.dmPath = Paths.get(System.getProperty("user.dir"));
		this.dm = new MockDirectoryManager(this.dmPath);
		this.dm.createFile("test.json.gz", "");
		this.dm.createFile("test.sql.gz", "");
		this.dm.createFile("test.xml.bz2", "");
		this.dm.createFile("current-dump.xml.bz2", "");
		this.dm.createFile("daily-dump.xml.bz2", "");
	}

	@Test
	public void missingDumpFile() throws IOException {
		MwLocalDumpFile df = new MwLocalDumpFile(dm,
				DumpContentType.DAILY, "empty-dump.xml",
				"date", "name");
		assertFalse(df.isAvailable());
	}

	@Test
	public void testGetters() {
		MwLocalDumpFile df = new MwLocalDumpFile(
				"./src/test/resources/empty-dump.xml",
				DumpContentType.SITES);

		assertEquals(df.getDateStamp(), "YYYYMMDD");
		assertEquals(df.getProjectName(), "LocalDumpFile");
		assertEquals(df.getDumpContentType(), DumpContentType.SITES);
	}

	@Test
	public void testInferJsonDump() throws IOException {
		MwLocalDumpFile df = new MwLocalDumpFile(dm, null,
				"test.json.gz", "YYYYMMDD", "name");
		
		df.prepareDumpFile();
		
		assertTrue(df.localDumpfileDirectoryManager.hasFile(df
				.getDumpFileName()));
		assertEquals(df.getDumpContentType(), DumpContentType.JSON);
	}
	
	@Test
	public void testInferSitesDump() throws IOException {
		MwLocalDumpFile df = new MwLocalDumpFile(dm, null,
				"test.sql.gz", "YYYYMMDD", "name");
		
		df.prepareDumpFile();
		
		assertTrue(df.localDumpfileDirectoryManager.hasFile(df
				.getDumpFileName()));
		assertEquals(df.getDumpContentType(), DumpContentType.SITES);
	}

	@Test
	public void testInferFullDump() throws IOException {
		MwLocalDumpFile df = new MwLocalDumpFile(dm, null,
				"test.xml.bz2", "YYYYMMDD", "name");

		df.prepareDumpFile();

		assertTrue(df.localDumpfileDirectoryManager.hasFile(df
				.getDumpFileName()));
		assertEquals(df.getDumpContentType(), DumpContentType.FULL);
	}

	@Test
	public void testInferDailyDump() throws IOException {
		MwLocalDumpFile df = new MwLocalDumpFile(dm, null,
				"daily-dump.xml.bz2", "YYYYMMDD", "name");

		df.prepareDumpFile();

		assertTrue(df.localDumpfileDirectoryManager.hasFile(df
				.getDumpFileName()));
		assertEquals(df.getDumpContentType(), DumpContentType.DAILY);
	}

	@Test
	public void testInferCurrentDump() throws IOException {
		MwLocalDumpFile df = new MwLocalDumpFile(dm, null,
				"current-dump.xml.bz2", "YYYYMMDD", "name");

		df.prepareDumpFile();

		assertTrue(df.localDumpfileDirectoryManager.hasFile(df
				.getDumpFileName()));
		assertEquals(df.getDumpContentType(), DumpContentType.CURRENT);
	}
}
