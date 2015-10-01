package org.wikidata.wdtk.testing;

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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.util.DirectoryManager;

public class MockDirectoryManagerTest {

	MockDirectoryManager mdm;
	Path basePath;

	@Before
	public void setUp() throws Exception {
		basePath = Paths.get(System.getProperty("user.dir"));
		mdm = new MockDirectoryManager(basePath, true, false);
		mdm.setDirectory(basePath.resolve("dir1").resolve("subdir"));
		mdm.setFileContents(basePath.resolve("dir2").resolve("test.txt"),
				"Test contents");
		mdm.setFileContents(
				basePath.resolve("anotherdir").resolve("test.txt.bz2"),
				"Test BZ2 contents\nMore contents", CompressionType.BZ2);
		mdm.setFileContents(
				basePath.resolve("anotherdir").resolve("test.txt.gz"),
				"Test GZIP contents", CompressionType.GZIP);
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
		assertEquals(expectedDirs, mdmDirs);
	}

	@Test
	public void readFile() throws IOException {
		DirectoryManager submdm = mdm.getSubdirectoryManager("dir2");
		String content = MockStringContentFactory
				.getStringFromInputStream(submdm.getInputStreamForFile(
						"test.txt", CompressionType.NONE));
		assertEquals("Test contents", content);
	}

	@Test
	public void readBz2File() throws IOException {
		DirectoryManager submdm = mdm.getSubdirectoryManager("anotherdir");
		String content = MockStringContentFactory
				.getStringFromInputStream(submdm.getInputStreamForFile(
						"test.txt.bz2", CompressionType.BZ2));
		assertEquals("Test BZ2 contents\nMore contents", content);
	}

	@Test
	public void readGzipFile() throws IOException {
		DirectoryManager submdm = mdm.getSubdirectoryManager("anotherdir");
		String content = MockStringContentFactory
				.getStringFromInputStream(submdm.getInputStreamForFile(
						"test.txt.gz", CompressionType.GZIP));
		assertEquals("Test GZIP contents", content);
	}

	@Test
	public void createFileFromInputStream() throws IOException {
		InputStream inputStream = MockStringContentFactory
				.newMockInputStream("New stream contents\nMultiple lines");
		mdm.createFile("newfile.txt", inputStream);
		String content = MockStringContentFactory.getStringFromInputStream(mdm
				.getInputStreamForFile("newfile.txt", CompressionType.NONE));
		assertEquals("New stream contents\nMultiple lines", content);
	}

	@Test
	public void createAtomicFileFromInputStream() throws IOException {
		InputStream inputStream = MockStringContentFactory
				.newMockInputStream("New stream contents\nMultiple lines");
		mdm.createFileAtomic("newfile.txt", inputStream);
		String content = MockStringContentFactory.getStringFromInputStream(mdm
				.getInputStreamForFile("newfile.txt", CompressionType.NONE));
		assertEquals("New stream contents\nMultiple lines", content);
	}

	@Test
	public void createFileFromString() throws IOException {
		mdm.createFile("newfile.txt", "New contents");
		String content = MockStringContentFactory.getStringFromInputStream(mdm
				.getInputStreamForFile("newfile.txt", CompressionType.NONE));
		assertTrue(Arrays.equals(MockDirectoryManager
				.getMockedFileContents(mdm.directory.resolve("newfile.txt")),
				content.getBytes(StandardCharsets.UTF_8)));
		assertEquals("New contents", content);
	}

	@Test
	public void createFileUsingOutputstream() throws IOException {
		OutputStream out = mdm.getOutputStreamForFile("newfile.txt");

		BufferedWriter ow = new BufferedWriter(new OutputStreamWriter(out));
		ow.write("New contents");
		ow.close();

		String content = MockStringContentFactory.getStringFromInputStream(mdm
				.getInputStreamForFile("newfile.txt", CompressionType.NONE));
		assertEquals("New contents", content);
	}

	@Test
	public void readFileFails() throws IOException {
		mdm.setReturnFailingReaders(true);
		DirectoryManager submdm = mdm.getSubdirectoryManager("dir2");
		InputStream in = submdm.getInputStreamForFile("test.txt",
				CompressionType.NONE);
		// We do not use @Test(expected = IOException.class) in order to check
		// if the exception is really thrown at the right moment.
		boolean exception = false;
		try {
			MockStringContentFactory.getStringFromInputStream(in);
		} catch (IOException e) {
			exception = true;
		}
		assertTrue(exception);
	}

	@Test(expected = FileAlreadyExistsException.class)
	public void createFileConflict() throws IOException {
		DirectoryManager submdm = mdm.getSubdirectoryManager("dir2");
		submdm.createFile("test.txt", "New contents");
	}

	@Test(expected = FileNotFoundException.class)
	public void fileNotFound() throws IOException {
		mdm.getInputStreamForFile("test.txt", CompressionType.NONE);
	}

	@Test(expected = IOException.class)
	public void createFileFromStringReadOnly() throws IOException {
		DirectoryManager mdmReadOnly = new MockDirectoryManager(basePath,
				false, true);
		mdmReadOnly.createFile("newfile.txt", "New contents");
	}

	@Test(expected = IOException.class)
	public void createFileFromInputStreamReadOnly() throws IOException {
		DirectoryManager mdmReadOnly = new MockDirectoryManager(basePath,
				false, true);
		mdmReadOnly.createFile("newfile.txt",
				MockStringContentFactory.newMockInputStream("content"));
	}

	@Test(expected = IOException.class)
	public void createFileFromInputStreamAtomicReadOnly() throws IOException {
		DirectoryManager mdmReadOnly = new MockDirectoryManager(basePath,
				false, true);
		mdmReadOnly.createFileAtomic("newfile.txt",
				MockStringContentFactory.newMockInputStream("content"));
	}

	@Test(expected = IOException.class)
	public void getOutputStreamReadOnly() throws IOException {
		DirectoryManager mdmReadOnly = new MockDirectoryManager(basePath,
				false, true);
		mdmReadOnly.getOutputStreamForFile("newfile.txt");
	}

	@Test(expected = IOException.class)
	public void openInNonexistingDirectoryReadOnly() throws IOException {
		DirectoryManager mdmReadOnly = new MockDirectoryManager(basePath,
				false, true);
		mdmReadOnly.getSubdirectoryManager("doesNotExist");
	}

}
