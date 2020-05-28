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

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

/**
 * Class to read and write files from one directory. It is guaranteed that the
 * directory always exists (it is created if needed).
 *
 * @author Markus Kroetzsch
 *
 */
public class DirectoryManagerImpl implements DirectoryManager {

	/**
	 * The directory that this object is managing.
	 */
	final Path directory;

	/**
	 * If false, the directory manager will attempt to create directories when
	 * changing to a location that does not exist.
	 */
	final boolean readOnly;

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
	public DirectoryManagerImpl(Path baseDirectory, Boolean readOnly)
			throws IOException {
		this.directory = baseDirectory;
		this.readOnly = readOnly;
		createDirectory(this.directory);
	}

	@Override
	public String toString() {
		return this.directory.toString();
	}

	@Override
	public DirectoryManager getSubdirectoryManager(String subdirectoryName)
			throws IOException {
		return new DirectoryManagerImpl(directory.resolve(subdirectoryName),
				this.readOnly);
	}

	@Override
	public boolean hasSubdirectory(String subdirectoryName) {
		Path subdirectoryPath = this.directory.resolve(subdirectoryName);
		return Files.isDirectory(subdirectoryPath);
	}

	@Override
	public boolean hasFile(String fileName) {
		Path filePath = this.directory.resolve(fileName);
		return Files.isRegularFile(filePath) && !Files.isDirectory(filePath);
	}

	@Override
	public long createFile(String fileName, InputStream inputStream)
			throws IOException {
		long fileSize;
		Path filePath = this.directory.resolve(fileName);
		ensureWritePermission(filePath);

		try (ReadableByteChannel readableByteChannel = Channels
				.newChannel(inputStream);
				FileChannel fc = FileChannel
						.open(filePath, StandardOpenOption.WRITE,
								StandardOpenOption.CREATE_NEW)) {
			fileSize = fc.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		}
		return fileSize;
	}

	@Override
	public long createFileAtomic(String fileName, InputStream inputStream)
			throws IOException {
		long fileSize;
		Path filePath = this.directory.resolve(fileName);
		ensureWritePermission(filePath);

		Path fileTempPath = this.directory.resolve(fileName + ".part");

		try (ReadableByteChannel readableByteChannel = Channels
				.newChannel(inputStream);
				FileChannel fc = FileChannel.open(fileTempPath,
						StandardOpenOption.WRITE,
						StandardOpenOption.TRUNCATE_EXISTING,
						StandardOpenOption.CREATE)) {
			fileSize = fc.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		}

		Files.move(fileTempPath, filePath);

		return fileSize;
	}

	@Override
	public void createFile(String fileName, String fileContents)
			throws IOException {
		Path filePath = this.directory.resolve(fileName);
		ensureWritePermission(filePath);

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath,
				StandardCharsets.UTF_8, StandardOpenOption.WRITE,
				StandardOpenOption.CREATE_NEW)) {
			bufferedWriter.write(fileContents);
		}
	}

	@Override
	public OutputStream getOutputStreamForFile(String fileName)
			throws IOException {
		Path filePath = this.directory.resolve(fileName);
		ensureWritePermission(filePath);

		return Files.newOutputStream(filePath);
	}

	@Override
	public InputStream getInputStreamForFile(String fileName,
			CompressionType compressionType) throws IOException {
		Path filePath = this.directory.resolve(fileName);

		InputStream fileInputStream = Files.newInputStream(filePath,
				StandardOpenOption.READ);

		return getCompressorInputStream(fileInputStream, compressionType);
	}

	/**
	 * Returns an input stream that applies the required decompression to the
	 * given input stream.
	 *
	 * @param inputStream
	 *            the input stream with the (possibly compressed) data
	 * @param compressionType
	 *            the kind of compression
	 * @return an input stream with decompressed data
	 * @throws IOException
	 *             if there was a problem creating the decompression streams
	 */
	protected InputStream getCompressorInputStream(InputStream inputStream,
			CompressionType compressionType) throws IOException {
		switch (compressionType) {
		case NONE:
			return inputStream;
		case GZIP:
			return new GZIPInputStream(inputStream);
		case BZ2:
			return new BZip2CompressorInputStream(new BufferedInputStream(
					inputStream));
		default:
			throw new IllegalArgumentException("Unsupported compression type: "
					+ compressionType);
		}
	}

	@Override
	public List<String> getSubdirectories(String glob) throws IOException {
		List<String> result = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(
				this.directory, glob)) {
			for (Path entry : directoryStream) {
				if (Files.isDirectory(entry)) {
					result.add(entry.getFileName().toString());
				}
			}
		}
		return result;
	}

	/**
	 * Creates a directory at the given path if it does not exist yet and if the
	 * directory manager was not configured for read-only access.
	 *
	 * @param path
	 * @throws IOException
	 *             if it was not possible to create a directory at the given
	 *             path
	 */
	void createDirectory(Path path) throws IOException {
		if (Files.exists(path) && Files.isDirectory(path)) {
			return;
		}

		if (this.readOnly) {
			throw new FileNotFoundException(
					"The requested directory \""
							+ path.toString()
							+ "\" does not exist and we are in read-only mode, so it cannot be created.");
		}

		Files.createDirectory(path);
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
