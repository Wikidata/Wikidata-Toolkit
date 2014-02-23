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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
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
	DirectoryManager getSubdirectoryManager(String subdirectoryName)
			throws IOException;

	/**
	 * Check if there is a subdirectory of the given name.
	 * 
	 * @param subdirectoryName
	 * @return true if the subdirectory exists
	 */
	boolean hasSubdirectory(String subdirectoryName);

	/**
	 * Check if there is a file of the given name.
	 * 
	 * @param fileName
	 * @return true if the file exists and is not a directory
	 */
	boolean hasFile(String fileName);

	/**
	 * Create a new file in the current directory, and fill it with the data
	 * from the given input stream.
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
	 * Create a new file in the current directory, and fill it with the given
	 * data. Should only be used for short pieces of data.
	 * 
	 * @param fileName
	 *            the name of the file
	 * @param fileContents
	 *            the data to write into the file
	 * @throws IOException
	 */
	void createFile(String fileName, String fileContents) throws IOException;

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
	BufferedReader getBufferedReaderForFile(String fileName) throws IOException;

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
	BufferedReader getBufferedReaderForBz2File(String fileName)
			throws IOException;

	/**
	 * Get a list of the names of all subdirectories of the base directory. The
	 * glob pattern can be used to filter the names; "*" should be used if no
	 * filtering is desired.
	 * 
	 * @param glob
	 *            pattern to filter directoy names
	 * @return
	 * @throws IOException
	 */
	List<String> getSubdirectories(String glob) throws IOException;

}