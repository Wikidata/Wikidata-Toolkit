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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Interface for classes that read and write files from one directory. Allows
 * for mock implementations to test functionality without actually writing
 * files.
 *
 * @author Markus Kroetzsch
 *
 */
public interface DirectoryManager {

	/**
	 * Returns a new directory manager for the subdirectory of the given name.
	 * If the subdirectory does not exist yet, it will be created. If this is
	 * not desired, its existence can be checked with
	 * {@link #hasSubdirectory(String)} first (ignoring the fact that there
	 * might be race conditions when accessing the file system).
	 *
	 * @param subdirectoryName
	 *            the string name of the subdirectory
	 * @throws IOException
	 *             if directory could not be created
	 * @return DirectoryManager for subdirectory
	 */
	DirectoryManager getSubdirectoryManager(String subdirectoryName)
			throws IOException;

	/**
	 * Checks if there is a subdirectory of the given name.
	 *
	 * @param subdirectoryName
	 *            the name of the subdirectory
	 * @return true if the subdirectory exists
	 */
	boolean hasSubdirectory(String subdirectoryName);

	/**
	 * Checks if there is a file of the given name.
	 *
	 * @param fileName
	 *            the name of the file
	 * @return true if the file exists and is not a directory
	 */
	boolean hasFile(String fileName);

	/**
	 * Creates a new file in the current directory, and fill it with the data
	 * from the given input stream. If the stream encodes a string, then it
	 * should generally be encoded in UTF-8, since access methods assume this.
	 *
	 * @param fileName
	 *            the name of the file
	 * @param inputStream
	 *            the input stream from which to load the file
	 * @return size of the new file in bytes
	 * @throws IOException
	 */
	long createFile(String fileName, InputStream inputStream)
			throws IOException;

	/**
	 * Creates a new file in the current directory, and fill it with the data
	 * from the given input stream. This is done by first writing the data to a
	 * temporary file that uses a suffix to the file name, and then moving the
	 * completed file to the new location. This should be used when fetching
	 * larger files through from slow stream (e.g., a download) to prevent
	 * partially finished file downloads lying around if the program is
	 * terminated during download. The temporary file will still be lying
	 * around, but it will not be mistaken for the completed download by any
	 * other parts of the program.
	 * <p>
	 * If the stream encodes a string, then it should generally be encoded in
	 * UTF-8, since access methods assume this.
	 *
	 * @param fileName
	 *            the name of the file
	 * @param inputStream
	 *            the input stream from which to load the file
	 * @return size of the new file in bytes
	 * @throws IOException
	 */
	long createFileAtomic(String fileName, InputStream inputStream)
			throws IOException;

	/**
	 * Creates a new file in the current directory, and fill it with the given
	 * data, encoded in UTF-8. Should only be used for short pieces of data.
	 *
	 * @param fileName
	 *            the name of the file
	 * @param fileContents
	 *            the data to write into the file
	 * @throws IOException
	 */
	void createFile(String fileName, String fileContents) throws IOException;

	/**
	 * Opens and returns an output stream that can be used to write to the file
	 * of the given name within the current directory. The stream is owned by
	 * the caller and must be closed after use. If the file already exists, it
	 * will be truncated at this operation.
	 *
	 * @param fileName
	 *            the name of the file
	 * @return the stream to write to
	 * @throws IOException
	 */
	OutputStream getOutputStreamForFile(String fileName) throws IOException;

	/**
	 * Returns an input stream to access file of the given name within the
	 * current directory, possibly uncompressing it if required.
	 * <p>
	 * It is important to close the stream after using it to free memory.
	 *
	 * @param fileName
	 *            the name of the file
	 * @param compressionType
	 *            for types other than {@link CompressionType#NONE}, the file
	 *            will be uncompressed appropriately and the returned input
	 *            stream will provide access to the uncompressed content
	 * @return an InputStream to fetch data from the file
	 * @throws IOException
	 */
	InputStream getInputStreamForFile(String fileName,
			CompressionType compressionType) throws IOException;

	/**
	 * Returns a list of the names of all subdirectories of the base directory.
	 * The glob pattern can be used to filter the names; "*" should be used if
	 * no filtering is desired.
	 *
	 * @param glob
	 *            pattern to filter directoy names
	 * @return list of subdirectory names
	 * @throws IOException
	 */
	List<String> getSubdirectories(String glob) throws IOException;

}
