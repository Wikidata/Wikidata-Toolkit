package org.wikidata.wdtk.dumpfiles;

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