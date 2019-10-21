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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
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
	static final String DIRECTORY_MARKER_STRING = "DIRECTORY";

	/**
	 * Mock files with this content are interpreted as directories.
	 */
	static final byte[] DIRECTORY_MARKER = DIRECTORY_MARKER_STRING
			.getBytes(StandardCharsets.UTF_8);

	/**
	 * The mocked file system. This is static so that it can be accessed after a
	 * test even if the directory manager that was used is created internally.
	 */
	public static HashMap<Path, byte[]> files = new HashMap<>();

	final Path directory;

	/**
	 * If true, all read methods will return objects that will throw exceptions
	 * when trying to get any data. Used for testing exception handling.
	 */
	boolean returnFailingReaders;

	/**
	 * If false, the directory manager will attempt to create directories when
	 * changing to a location that does not exist.
	 */
	final boolean readOnly;

	/**
	 * Creates a new object but retains all previously stored files.
	 *
	 * @param directory
	 *            initial directory that is managed
	 * @param readOnly
	 *            if false, the directory manager will attempt to create
	 *            directories when changing to a location that does not exist
	 * @throws IOException
	 */
	public MockDirectoryManager(Path directory, Boolean readOnly)
			throws IOException {
		this(directory, false, readOnly);
	}

	/**
	 * Constructor
	 *
	 * @param baseDirectory
	 *            the directory where the file manager should point initially;
	 *            will be created if not existing
	 * @param readOnly
	 *            if false, the directory manager will attempt to create
	 *            directories when changing to a location that does not exist
	 * @throws IOException
	 *             if there was a problem creating the directory
	 */
	public MockDirectoryManager(String baseDirectory, Boolean readOnly)
			throws IOException {
		this(Paths.get(baseDirectory), readOnly);
	}

	/**
	 * Creates a new object and clears all previously stored mock files if
	 * requested.
	 *
	 * @param directory
	 *            initial directory that is managed
	 * @param resetFileSystem
	 *            if true, the previously mocked files will be cleared; in this
	 *            case, the starting directory will be created, however (even in
	 *            read-only mode)
	 * @param readOnly
	 *            if false, the directory manager will attempt to create
	 *            directories when changing to a location that does not exist
	 * @throws IOException
	 */
	public MockDirectoryManager(Path directory, boolean resetFileSystem,
			boolean readOnly) throws IOException {
		this.directory = directory;
		this.readOnly = readOnly;

		if (resetFileSystem) {
			files = new HashMap<>();
			setDirectory(directory);
		}

		if (files.containsKey(directory)) {
			if (!Arrays.equals(files.get(directory), DIRECTORY_MARKER)) {
				throw new IOException(
						"Could not create mock working directory.");
			} // else: directory exists, nothing to do
		} else {
			ensureWritePermission(directory);
			setDirectory(directory);
		}
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
	 * Sets the contents of the file at the given path and creates all parent
	 * directories in our mocked view of the file system.
	 * <p>
	 * This method is used for mocking and is always successful, even if the
	 * object is in read-only mode otherwise.
	 *
	 * @param path
	 * @param contents
	 * @throws IOException
	 */
	public void setFileContents(Path path, String contents) throws IOException {
		setFileContents(path, contents, CompressionType.NONE);
	}

	/**
	 * Sets the contents of the file at the given path and creates all parent
	 * directories in our mocked view of the file system. If a compression is
	 * chosen, the file contents is the compressed version of the given
	 * contents. Strings are encoded as UTF8.
	 * <p>
	 * This method is used for mocking and is always successful, even if the
	 * object is in read-only mode otherwise.
	 *
	 * @param path
	 * @param contents
	 * @param compressionType
	 * @throws IOException
	 */
	public void setFileContents(Path path, String contents,
			CompressionType compressionType) throws IOException {
		files.put(path, MockStringContentFactory.getBytesFromString(contents,
				compressionType));
		Path parent = path.getParent();
		if (parent != null) {
			setFileContents(parent, DIRECTORY_MARKER_STRING);
		}
	}

	/**
	 * Create the given directory and all parent directories in our mocked view
	 * of the file system.
	 * <p>
	 * This method is used for mocking and is always successful, even if the
	 * object is in read-only mode otherwise.
	 *
	 * @param path
	 * @throws IOException
	 */
	public void setDirectory(Path path) throws IOException {
		setFileContents(path, DIRECTORY_MARKER_STRING);
	}

	@Override
	public DirectoryManager getSubdirectoryManager(String subdirectoryName)
			throws IOException {
		MockDirectoryManager result = new MockDirectoryManager(
				directory.resolve(subdirectoryName), false, this.readOnly);
		result.setReturnFailingReaders(this.returnFailingReaders);
		return result;
	}

	@Override
	public boolean hasSubdirectory(String subdirectoryName) {
		Path directoryPath = this.directory.resolve(subdirectoryName);
		return Arrays.equals(DIRECTORY_MARKER, files.get(directoryPath));
	}

	@Override
	public boolean hasFile(String fileName) {
		Path filePath = this.directory.resolve(fileName);
		return files.containsKey(filePath)
				&& !Arrays.equals(files.get(filePath), DIRECTORY_MARKER);
	}

	@Override
	public long createFile(String fileName, InputStream inputStream)
			throws IOException {

		Path filePath = this.directory.resolve(fileName);
		ensureWritePermission(filePath);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int nextByte;
		while ((nextByte = inputStream.read()) >= 0) {
			out.write(nextByte);
		}
		out.close();

		files.put(filePath, out.toByteArray());

		return out.size();
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
		ensureWritePermission(filePath);

		files.put(filePath, fileContents.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public OutputStream getOutputStreamForFile(String fileName)
			throws IOException {
		Path filePath = this.directory.resolve(fileName);
		ensureWritePermission(filePath);

		return new MockOutputStream(filePath);
	}

	@Override
	public InputStream getInputStreamForFile(String fileName,
			CompressionType compressionType) throws IOException {
		if (compressionType == CompressionType.GZIP) {
			return new GZIPInputStream(getInputStreamForMockFile(fileName));
		} else if (compressionType == CompressionType.BZ2) {
			return new BZip2CompressorInputStream(
					getInputStreamForMockFile(fileName));
		} else {
			return getInputStreamForMockFile(fileName);
		}
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
			throw new FileNotFoundException("Could not find file \"" + fileName
					+ "\" in current directory \"" + this.directory.toString()
					+ "\"");
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
	public List<String> getSubdirectories(String glob) {
		List<String> result = new ArrayList<>();
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
	 * Returns the byte contents of the mocked file for the given path. If the
	 * file is not mocked, null is returned. If the file is a mocked directory,
	 * the bytes of {@link MockDirectoryManager#DIRECTORY_MARKER} are returned.
	 *
	 * @param filePath
	 *            the path of the mocked file
	 * @return byte contents of mocked file
	 */
	public static byte[] getMockedFileContents(Path filePath) {
		return files.get(filePath);
	}

	/**
	 * Throws an exception if the object is in read-only mode. The file path is
	 * only needed for the error message. A detailed check for writability is
	 * not performed (if there is a specific problem for this one path, e.g.,
	 * due to missing permissions, an exception will be created in due course
	 * anyway).
	 *
	 * @param writeFilePath
	 *            the name of the file we would like to write to
	 * @throws IOException
	 *             if in read-only mode
	 */
	void ensureWritePermission(Path writeFilePath) throws IOException {
		if (this.readOnly) {
			throw new IOException("Cannot write to \""
					+ writeFilePath.toString()
					+ "\" since we are in read-only mode.");
		}
	}
}
