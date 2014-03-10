package org.wikidata.wdtk.util;

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

public class MockDirectoryManagerTest {

	MockDirectoryManager mdm;

	@Before
	public void setUp() throws Exception {
		Path basePath = Paths.get(System.getProperty("user.dir"));
		mdm = new MockDirectoryManager(basePath);
		mdm.setDirectory(basePath.resolve("dir1").resolve("subdir"));
		mdm.setFileContents(basePath.resolve("dir2").resolve("test.txt"),
				"Test contents");
		mdm.setFileContents(
				basePath.resolve("anotherdir").resolve("test.txt.bz2"),
				"Test BZ2 contents\nMore contents");
	}

	@Test
	public void newSubdirectoryManager() throws IOException {
		mdm.getSubdirectoryManager("newdir");
		assertTrue(mdm.hasSubdirectory("newdir"));
	}

	@Test(expected = IOException.class)
	public void subdirectoryManagerConflict() throws IOException {
		DirectoryManager submdm = mdm.getSubdirectoryManager("dir2");
		submdm.getSubdirectoryManager("test.txt");
	}

	@Test
	public void hasSubdirectory() throws IOException {
		DirectoryManager submdm = mdm.getSubdirectoryManager("dir1");
		assertTrue(submdm.hasSubdirectory("subdir"));
		assertFalse(submdm.hasFile("subdir"));
		assertFalse(mdm.hasSubdirectory("dir"));
	}

	@Test
	public void hasFile() throws IOException {
		DirectoryManager submdm = mdm.getSubdirectoryManager("dir2");
		assertTrue(submdm.hasFile("test.txt"));
		assertFalse(mdm.hasSubdirectory("test.txt"));
	}

	@Test
	public void getSubdirectories() throws IOException {
		HashSet<String> mdmDirs = new HashSet<String>(
				mdm.getSubdirectories("dir*"));
		HashSet<String> expectedDirs = new HashSet<String>();
		expectedDirs.add("dir1");
		expectedDirs.add("dir2");
		assertEquals(mdmDirs, expectedDirs);
	}

	@Test
	public void readFile() throws IOException {
		DirectoryManager submdm = mdm.getSubdirectoryManager("dir2");
		String content = MockStringContentFactory
				.getStringFromInputStream(submdm
						.getInputStreamForFile("test.txt"));
		assertEquals(content, "Test contents");
	}

	@Test
	public void readBz2File() throws IOException {
		DirectoryManager submdm = mdm.getSubdirectoryManager("anotherdir");
		String content = MockStringContentFactory
				.getStringFromInputStream(submdm
						.getInputStreamForBz2File("test.txt.bz2"));
		assertEquals(content, "Test BZ2 contents\nMore contents");
	}

	@Test
	public void createFileFromInputStream() throws IOException {
		InputStream inputStream = MockStringContentFactory
				.newMockInputStream("New stream contents\nMultiple lines");
		mdm.createFile("newfile.txt", inputStream);
		String content = MockStringContentFactory.getStringFromInputStream(mdm
				.getInputStreamForFile("newfile.txt"));
		assertEquals(content, "New stream contents\nMultiple lines");
	}

	@Test
	public void createFileFromString() throws IOException {
		mdm.createFile("newfile.txt", "New contents");
		String content = MockStringContentFactory.getStringFromInputStream(mdm
				.getInputStreamForFile("newfile.txt"));
		assertEquals(content, "New contents");
	}

	@Test(expected = FileAlreadyExistsException.class)
	public void createFileConflict() throws IOException {
		DirectoryManager submdm = mdm.getSubdirectoryManager("dir2");
		submdm.createFile("test.txt", "New contents");
	}

	@Test(expected = FileNotFoundException.class)
	public void fileNotFound() throws IOException {
		mdm.getInputStreamForFile("test.txt");
	}

	@Test(expected = IllegalArgumentException.class)
	public void readOnlyNonBz2Files() throws IOException {
		DirectoryManager submdm = mdm.getSubdirectoryManager("dir2");
		submdm.getInputStreamForBz2File("test.txt");
	}

	@Test(expected = IllegalArgumentException.class)
	public void bunzipOnlyBz2Files() throws IOException {
		DirectoryManager submdm = mdm.getSubdirectoryManager("anotherdir");
		submdm.getInputStreamForFile("test.txt.bz2");
	}
}
