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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.util.DirectoryManager;

/**
 * Mock implementation of {@link DirectoryManager} that simulates file access
 * without touching the file system.
 *
 * @author Markus Kroetzsch
 *
 */
public class MockDirectoryManager implements DirectoryManager {

	/**
	 * Mock files with this content are interpreted as directories.
	 */
	static final String DIRECTORY_MARKER = "DIRECTORY";

	/**
	 * The mocked file system. This is static so that it can be accessed after a
	 * test even if the directory manager that was used is created internally.
	 */
	static HashMap<Path, String> files = new HashMap<Path, String>();

	final Path directory;
	boolean returnFailingReaders;

	/**
	 * Creates a new object and clears all previously stored files.
	 *
	 * @param directory
	 *            initial directory that is managed
	 * @throws IOException
	 */
	public MockDirectoryManager(Path directory) throws IOException {

		this(directory, true);
	}

	/**
	 * Creates a new object and clears all previously stored if requested.
	 *
	 * @param directory
	 *            initial directory that is managed
	 * @param resetFileSystem
	 *            if true, the previously mocked files will be cleared
	 * @throws IOException
	 */
	public MockDirectoryManager(Path directory, boolean resetFileSystem)
			throws IOException {
		this.directory = directory;

		if (resetFileSystem) {
			files = new HashMap<Path, String>();
		}

		if (files.containsKey(directory)
				&& !files.get(directory).equals(
						MockDirectoryManager.DIRECTORY_MARKER)) {
			throw new IOException("Could not create mock working directory.");
		}
		setDirectory(directory);
	}

	/**
	 * When set to true, every operation that returns reader objects to access
	 * some file will return objects that fail with exceptions when trying to
	 * read the file. This can be used to simulate problems like insufficient
	 * access rights or files becoming inaccessible after being opened.
	 * <p>
	 * The property is inherited by any submanagers that are created by this
	 * object.
	 *
	 * @param returnFailingReaders
	 *            whether read operations should fail
	 */
	public void setReturnFailingReaders(boolean returnFailingReaders) {
		this.returnFailingReaders = returnFailingReaders;
	}

	@Override
	public String toString() {
		return "[mocked directory] " + this.directory.toString();
	}

	/**
	 * Set the contents of the file at the given path and create all parent
	 * directories in our mocked view of the file system.
	 *
	 * @param path
	 * @param contents
	 */
	public void setFileContents(Path path, String contents) {
		files.put(path, contents);
		Path parent = path.getParent();
		if (parent != null) {
			setFileContents(parent, MockDirectoryManager.DIRECTORY_MARKER);
		}
	}

	/**
	 * Create the given directory and all parent directories in our mocked view
	 * of the file system.
	 *
	 * @param path
	 */
	public void setDirectory(Path path) {
		setFileContents(path, MockDirectoryManager.DIRECTORY_MARKER);
	}

	@Override
	public DirectoryManager getSubdirectoryManager(String subdirectoryName)
			throws IOException {
		MockDirectoryManager result = new MockDirectoryManager(
				directory.resolve(subdirectoryName), false);
		result.setReturnFailingReaders(this.returnFailingReaders);
		return result;
	}

	@Override
	public boolean hasSubdirectory(String subdirectoryName) {
		Path directoryPath = this.directory.resolve(subdirectoryName);
		return MockDirectoryManager.DIRECTORY_MARKER.equals(files
				.get(directoryPath));
	}

	@Override
	public boolean hasFile(String fileName) {
		Path filePath = this.directory.resolve(fileName);
		return files.containsKey(filePath)
				&& !files.get(filePath).equals(
						MockDirectoryManager.DIRECTORY_MARKER);
	}

	@Override
	public long createFile(String fileName, InputStream inputStream)
			throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));
		StringBuilder contentsBuilder = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			contentsBuilder.append(line).append("\n");
		}
		createFile(fileName, contentsBuilder.toString());

		return contentsBuilder.toString().getBytes(StandardCharsets.UTF_8).length;
	}

	@Override
	public long createFileAtomic(String fileName, InputStream inputStream)
			throws IOException {
		return createFile(fileName, inputStream);
	}

	@Override
	public void createFile(String fileName, String fileContents)
			throws IOException {
		if (this.hasFile(fileName)) {
			throw new FileAlreadyExistsException("File exists");
		}
		Path filePath = this.directory.resolve(fileName);
		files.put(filePath, fileContents);
	}

	@Override
	public OutputStream getOutputStreamForFile(String fileName)
			throws IOException {
		Path filePath = this.directory.resolve(fileName);
		return new MockOutputStream(filePath);
	}

	@Override
	public InputStream getInputStreamForFile(String fileName,
			CompressionType compressionType) throws IOException {
		if (compressionType == CompressionType.GZIP
				&& !fileName.endsWith(".gz")) {
			throw new IllegalArgumentException(
					"Can only read gz files with this compression type");
		} else if (compressionType == CompressionType.BZ2
				&& !fileName.endsWith(".bz2")) {
			throw new IllegalArgumentException(
					"Can only read bz2 files with this compression type");
		} else if (compressionType == CompressionType.NONE
				&& (fileName.endsWith(".bz2") || fileName.endsWith(".gz"))) {
			throw new IllegalArgumentException(
					"Cannot read compressed files with this compression type");
		}

		return getInputStreamForMockFile(fileName);
	}

	/**
	 * Get an input stream for the mocked contents of the given file, or throw
	 * an exception if the file does not exist.
	 *
	 * @param fileName
	 * @return input stream for file
	 * @throws FileNotFoundException
	 */
	InputStream getInputStreamForMockFile(String fileName)
			throws FileNotFoundException {
		if (!hasFile(fileName)) {
			throw new FileNotFoundException();
		}

		if (this.returnFailingReaders) {
			return MockStringContentFactory.getFailingInputStream();
		} else {
			Path filePath = this.directory.resolve(fileName);
			return MockStringContentFactory.newMockInputStream(files
					.get(filePath));
		}
	}

	@Override
	public List<String> getSubdirectories(String glob) throws IOException {
		List<String> result = new ArrayList<String>();
		PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(
				"glob:" + glob);
		for (Path path : files.keySet()) {
			if (!this.directory.equals(path.getParent())) {
				continue;
			}
			if (pathMatcher.matches(path.getFileName())) {
				result.add(path.getFileName().toString());
			}
		}
		return result;
	}

	/**
	 * Returns the string contents of the mocked file for the given path. If the
	 * file is not mocked, null is returned. If the file is a mocked directory,
	 * {@link MockDirectoryManager#DIRECTORY_MARKER} is returned.
	 *
	 * @param filePath
	 *            the path of the mocked file
	 * @return string contents of mocked file
	 */
	public static String getMockedFileContents(Path filePath) {
		return files.get(filePath);
	}
}
