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
		this.dmPath = Paths.get(System.getProperty("user.dir"))
				.resolve("dumpfiles").resolve("wikidatawiki");
		this.dm = new MockDirectoryManager(this.dmPath);
	}

	@Test
	public void missingDumpFile() throws IOException {
		MwLocalDumpFile df = new MwLocalDumpFile(dm,
				DumpContentType.DAILY, "empty-dump.xml",
				"date", "name");
		assertFalse(df.isAvailable());
	}

	@Test
	public void testGetters() throws IOException {
		MwLocalDumpFile df = new MwLocalDumpFile(
				System.getProperty("user.dir"),
				DumpContentType.DAILY, "empty-dump.xml");
		assertEquals(df.getDateStamp(), "LocalDate");
		assertEquals(df.getProjectName(), "LocalDumpFile");
		assertEquals(df.getDumpContentType(), DumpContentType.DAILY);
	}
}
