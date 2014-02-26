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

/**
 * Class for representing dump files published by the Wikimedia Foundation, and
 * previously downloaded to a local directory.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class WmfLocalDumpFile extends WmfDumpFile {

	final DirectoryManager thisDumpfileDirectoryManager;
	final MediaWikiDumpFile.DumpContentType dumpContentType;

	/**
	 * Constructor.
	 * 
	 * @param dateStamp
	 *            dump date in format YYYYMMDD
	 * @param projectName
	 *            project name string
	 * @param dumpfileDirectoryManager
	 *            the directory manager for the directory where dumps should be
	 *            downloaded to
	 * @param dumpContentType
	 *            the type of dump this represents
	 */
	public WmfLocalDumpFile(String dateStamp, String projectName,
			DirectoryManager dumpfileDirectoryManager,
			MediaWikiDumpFile.DumpContentType dumpContentType) {
		super(dateStamp, projectName);

		String subdirectoryName = dumpContentType.toString().toLowerCase()
				+ "-" + dateStamp;
		if (!dumpfileDirectoryManager.hasSubdirectory(subdirectoryName)) {
			throw new IllegalArgumentException(
					"There is no local dump file directory at the specified location.");
		}
		try {
			this.thisDumpfileDirectoryManager = dumpfileDirectoryManager
					.getSubdirectoryManager(subdirectoryName);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Could not change to dump file directory", e);
		}

		this.dumpContentType = dumpContentType;
	}

	/**
	 * Get the directory where this dump file data should be.
	 * 
	 * @return
	 */
	public String getDumpfileDirectory() {
		return this.thisDumpfileDirectoryManager.toString();
	}

	@Override
	public DumpContentType getDumpContentType() {
		return this.dumpContentType;
	}

	@Override
	public BufferedReader getDumpFileReader() throws IOException {
		return this.thisDumpfileDirectoryManager
				.getBufferedReaderForBz2File(getFileName());
	}

	@Override
	protected Long fetchMaximalRevisionId() {
		String inputLine;
		try (BufferedReader in = this.thisDumpfileDirectoryManager
				.getBufferedReaderForFile("maxrevid.txt")) {
			inputLine = in.readLine();
		} catch (IOException e) {
			return -1L;
		}

		if (inputLine != null) {
			try {
				return new Long(inputLine);
			} catch (NumberFormatException e) {
				// fall through
			}
		}
		return -1L;
	}

	@Override
	protected boolean fetchIsDone() {
		return this.thisDumpfileDirectoryManager.hasFile(getFileName())
				&& this.getMaximalRevisionId() >= 0;
	}

	/**
	 * Get the file name of this dump file.
	 * 
	 * @return
	 */
	String getFileName() {
		return this.projectName + "-" + this.dateStamp
				+ WmfDumpFile.getDumpFilePostfix(this.dumpContentType);
	}

}
