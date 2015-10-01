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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.testing.MockDirectoryManager;

public class WmfLocalDumpFileTest {

	MockDirectoryManager dm;
	Path dmPath;

	@Before
	public void setUp() throws Exception {
		this.dmPath = Paths.get(System.getProperty("user.dir"))
				.resolve("dumpfiles").resolve("wikidatawiki");
		this.dm = new MockDirectoryManager(this.dmPath, true, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void directoryDoesNotExist() {
		new WmfLocalDumpFile("20140220", "wikidatawiki", dm,
				DumpContentType.DAILY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void directoryNotReadable() throws IOException {
		MockDirectoryManager dm = Mockito.mock(MockDirectoryManager.class);
		Mockito.when(dm.hasSubdirectory("daily-20140220")).thenReturn(true);
		Mockito.doThrow(new IOException()).when(dm)
				.getSubdirectoryManager("daily-20140220");

		new WmfLocalDumpFile("20140220", "wikidatawiki", dm,
				DumpContentType.DAILY);
	}

	@Test
	public void missingDumpFile() throws IOException {
		Path thisDumpPath = this.dmPath.resolve("daily-20140220");
		dm.setDirectory(thisDumpPath);
		WmfLocalDumpFile dumpFile = new WmfLocalDumpFile("20140220",
				"wikidatawiki", dm, DumpContentType.DAILY);
		assertEquals(dumpFile.isAvailable(), false);
	}

}
