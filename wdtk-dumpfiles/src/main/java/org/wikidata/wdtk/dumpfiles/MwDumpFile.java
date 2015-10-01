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
import java.util.Comparator;

/**
 * Representation of MediaWiki dump files, which provides access to important
 * basic properties of dumps, and to the content of the dump itself.
 *
 * @author Markus Kroetzsch
 *
 */
public interface MwDumpFile {

	/**
	 * Comparator to sort dumps by date.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	class DateComparator implements Comparator<MwDumpFile> {
		@Override
		public int compare(MwDumpFile a, MwDumpFile b) {
			return a.getDateStamp().compareTo(b.getDateStamp());
		}
	}

	/**
	 * Checks if the dump is actually available. Should be called before
	 * {@link #getDumpFileReader()}. Depending on the type of dumpfile, this
	 * will trigger one or more checks to make sure that all relevant data can
	 * be accessed for this dump file. This is still no definite guarantee that
	 * the download will succeed, since there can always be IO errors anyway,
	 * but it helps to detect cases where the dump is clearly not in a usable
	 * state.
	 *
	 * @return true if the dump file is likely to be available
	 */
	boolean isAvailable();

	/**
	 * Returns the project name for this dump. Together with the dump content
	 * type and date stamp, this identifies the dump, and it is therefore always
	 * available.
	 *
	 * @return a project name string
	 */
	String getProjectName();

	/**
	 * Returns the date stamp for this dump. Together with the project name and
	 * dump content type, this identifies the dump, and it is therefore always
	 * available.
	 *
	 * @return a string that represents a date in format YYYYMMDD
	 */
	String getDateStamp();

	/**
	 * Returns information about the content of the dump. Together with the
	 * project name and date stamp, this identifies the dump, and it is
	 * therefore always available.
	 *
	 * @return the content type of this dump
	 */
	DumpContentType getDumpContentType();

	/**
	 * Returns an input stream that provides access to the (uncompressed) text
	 * content of the dump file.
	 * <p>
	 * It is important to close the stream after use.
	 *
	 * @return an input stream to read the dump file
	 * @throws IOException
	 *             if the dump file contents could not be accessed
	 */
	InputStream getDumpFileStream() throws IOException;

	/**
	 * Returns a buffered reader that provides access to the (uncompressed) text
	 * content of the dump file.
	 * <p>
	 * It is important to close the reader after use.
	 *
	 * @return a buffered reader to read the dump file
	 * @throws IOException
	 *             if the dump file contents could not be accessed
	 */
	BufferedReader getDumpFileReader() throws IOException;

	/**
	 * Prepares the dump file for access via {@link #getDumpFileStream()} or
	 * {@link #getDumpFileReader()}. In particular, this will download any
	 * remote files.
	 *
	 * @throws IOException
	 *             if there was a problem preparing the files
	 */
	void prepareDumpFile() throws IOException;
}
