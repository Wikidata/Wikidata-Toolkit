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
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	 * Constructor
	 * 
	 * @param baseDirectory
	 *            the directory where the file manager should point initially;
	 *            will be created if not existing
	 * @throws IOException
	 *             if there was a problem creating the directory
	 */
	public DirectoryManagerImpl(String baseDirectory) throws IOException {
		this(Paths.get(baseDirectory));
	}

	/**
	 * Constructor
	 * 
	 * @param baseDirectory
	 *            the directory where the file manager should point initially;
	 *            will be created if not existing
	 * @throws IOException
	 *             if there was a problem creating the directory
	 */
	public DirectoryManagerImpl(Path baseDirectory) throws IOException {
		this.directory = baseDirectory;
		createDirectory(this.directory);
	}

	@Override
	public String toString() {
		return this.directory.toString();
	}

	@Override
	public DirectoryManager getSubdirectoryManager(String subdirectoryName)
			throws IOException {
		return new DirectoryManagerImpl(directory.resolve(subdirectoryName));
	}

	@Override
	public boolean hasSubdirectory(String subdirectoryName) {
		Path subdirectoryPath = this.directory.resolve(subdirectoryName);
		return Files.isDirectory(subdirectoryPath);
	}

	@Override
	public boolean hasFile(String fileName) {
		Path filePath = this.directory.resolve(fileName);
		return Files.isRegularFile(filePath);
	}

	@Override
	public long createFile(String fileName, InputStream inputStream)
			throws IOException {
		long fileSize;
		Path filePath = this.directory.resolve(fileName);
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
	public void createFile(String fileName, String fileContents)
			throws IOException {
		Path filePath = this.directory.resolve(fileName);
		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath,
				StandardCharsets.UTF_8, StandardOpenOption.WRITE,
				StandardOpenOption.CREATE_NEW)) {
			bufferedWriter.write(fileContents);
		}
	}

	@Override
	public InputStream getInputStreamForFile(String fileName,
			CompressionType compressionType) throws IOException {
		Path filePath = this.directory.resolve(fileName);
		InputStream fileInputStream = Files.newInputStream(filePath,
				StandardOpenOption.READ);
		switch (compressionType) {
		case NONE:
			return fileInputStream;
		case GZIP:
			return new GZIPInputStream(fileInputStream);
		case BZ2:
			return new BZip2CompressorInputStream(new BufferedInputStream(
					fileInputStream));
		default:
			throw new IllegalArgumentException("Unsupported compresion type: "
					+ compressionType);
		}

	}

	@Override
	public List<String> getSubdirectories(String glob) throws IOException {
		List<String> result = new ArrayList<String>();
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
	 * Create a directory at the given path if it does not exist yet.
	 * 
	 * @param path
	 * @throws IOException
	 *             if it was not possible to create a directory at the given
	 *             path
	 */
	void createDirectory(Path path) throws IOException {
		try {
			Files.createDirectory(path);
		} catch (FileAlreadyExistsException e) {
			if (Files.isDirectory(path)) {
				// fine, then we don't need to create it
			} else {
				throw e;
			}
		}
	}
}
