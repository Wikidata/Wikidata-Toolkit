package org.wikidata.wdtk.dumpfiles;

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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

/**
 * Class to read and write files from one directory. It is guaranteed that the
 * directory always exists (it is created if needed).
 * 
 * @author Markus Kroetzsch
 * 
 */
public class DirectoryManager {

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
	public DirectoryManager(String baseDirectory) throws IOException {
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
	public DirectoryManager(Path baseDirectory) throws IOException {
		this.directory = baseDirectory;
		createDirectory(this.directory);
	}

	@Override
	public String toString() {
		return this.directory.toString();
	}

	/**
	 * Return a new directory manager for the subdirectory of the given name. If
	 * the subdirectory does not exist yet, it will be created. If this is not
	 * desired, its existence can be checked with
	 * {@link #hasSubdirectory(String)} first (ignoring the fact that there
	 * might be race conditions when accessing the file system).
	 * 
	 * @param subdirectoryName
	 *            the string name of the subdirectory
	 * @throws IOException
	 *             if directory could not be created
	 */
	public DirectoryManager getSubdirectoryManager(String subdirectoryName)
			throws IOException {
		return new DirectoryManager(directory.resolve(subdirectoryName));
	}

	/**
	 * Check if there is a subdirectory of the given name.
	 * 
	 * @param subdirectoryName
	 * @return true if the subdirectory exists
	 */
	public boolean hasSubdirectory(String subdirectoryName) {
		Path subdirectoryPath = this.directory.resolve(subdirectoryName);
		return Files.isDirectory(subdirectoryPath);
	}

	/**
	 * Check if there is a file of the given name.
	 * 
	 * @param fileName
	 * @return true if the file exists and is not a directory
	 */
	public boolean hasFile(String fileName) {
		Path filePath = this.directory.resolve(fileName);
		return Files.isRegularFile(filePath);
	}

	/**
	 * Create a new file in the current directory, and fill it with the data
	 * from the given readable byte channel.
	 * 
	 * @param fileName
	 *            the name of the file
	 * @param readableByteChannel
	 *            the channel from which to load the file
	 * @return size of the new file in bytes
	 * @throws IOException
	 */
	public long createFile(String fileName,
			ReadableByteChannel readableByteChannel) throws IOException {
		long fileSize;
		Path filePath = this.directory.resolve(fileName);
		try (FileChannel fc = FileChannel.open(filePath,
				StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
			fileSize = fc.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
		}
		return fileSize;
	}

	/**
	 * Create a new file in the current directory, and fill it with the given
	 * data. Should only be used for short pieces of data.
	 * 
	 * @param fileName
	 *            the name of the file
	 * @param fileContents
	 *            the data to write into the file
	 * @throws IOException
	 */
	public void createFile(String fileName, String fileContents)
			throws IOException {
		Path filePath = this.directory.resolve(fileName);
		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath,
				StandardCharsets.UTF_8, StandardOpenOption.WRITE,
				StandardOpenOption.CREATE_NEW)) {
			bufferedWriter.write(fileContents);
		}
	}

	/**
	 * Get a buffered reader to access the file of the given name within the
	 * current directory.
	 * <p>
	 * It is important to close the reader after using it to free memory.
	 * 
	 * @param fileName
	 *            the name of the file
	 * @return a BufferedReader to fetch data from the file
	 * @throws IOException
	 */
	public BufferedReader getBufferedReaderForFile(String fileName)
			throws IOException {
		Path filePath = this.directory.resolve(fileName);
		return new BufferedReader(new InputStreamReader(Files.newInputStream(
				filePath, StandardOpenOption.READ)));
	}

	/**
	 * Get a buffered reader to access the BZIP2-compressed file of the given
	 * name within the current directory.
	 * <p>
	 * It is important to close the reader after using it to free memory.
	 * 
	 * @param fileName
	 *            the name of the file
	 * @return a BufferedReader to fetch data from the file
	 * @throws IOException
	 */
	public BufferedReader getBufferedReaderForBz2File(String fileName)
			throws IOException {
		Path filePath = this.directory.resolve(fileName);
		return new BufferedReader(new InputStreamReader(
				new BZip2CompressorInputStream(
						new BufferedInputStream(Files.newInputStream(filePath,
								StandardOpenOption.READ)))));
	}

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
