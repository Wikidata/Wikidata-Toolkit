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
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.util.DirectoryManager;

/**
 * Class for representing dump files published by the Wikimedia Foundation, and
 * previously downloaded to a local directory.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class WmfLocalDumpFile extends WmfDumpFile {

	/**
	 * DirectoryManager for the directory of this local dumpfile.
	 */
	final DirectoryManager localDumpfileDirectoryManager;
	/**
	 * Type of this dumpfile.
	 */
	final DumpContentType dumpContentType;

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
			DumpContentType dumpContentType) {
		super(dateStamp, projectName);

		String subdirectoryName = WmfDumpFile.getDumpFileDirectoryName(
				dumpContentType, dateStamp);
		if (!dumpfileDirectoryManager.hasSubdirectory(subdirectoryName)) {
			throw new IllegalArgumentException(
					"There is no local dump file directory at the specified location.");
		}
		try {
			this.localDumpfileDirectoryManager = dumpfileDirectoryManager
					.getSubdirectoryManager(subdirectoryName);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Could not change to dump file directory", e);
		}

		this.dumpContentType = dumpContentType;
	}

	/**
	 * Returns the directory where this dump file data should be.
	 * 
	 * @return string representation of the directory of this dumpfile
	 */
	public String getDumpfileDirectory() {
		return this.localDumpfileDirectoryManager.toString();
	}

	@Override
	public DumpContentType getDumpContentType() {
		return this.dumpContentType;
	}

	@Override
	public InputStream getDumpFileStream() throws IOException {
		String dumpFileName = WmfDumpFile.getDumpFileName(this.dumpContentType,
				this.projectName, this.dateStamp);

		return this.localDumpfileDirectoryManager.getInputStreamForFile(
				dumpFileName,
				WmfDumpFile.getDumpFileCompressionType(this.dumpContentType));
	}

	@Override
	public void prepareDumpFile() throws IOException {
		// nothing to do
	}

	@Override
	protected Long fetchMaximalRevisionId() {
		if (!WmfDumpFile.isRevisionDumpFile(this.dumpContentType)) {
			return -1L;
		}

		String inputLine;
		try (InputStream in = this.localDumpfileDirectoryManager
				.getInputStreamForFile(WmfDumpFile.LOCAL_FILENAME_MAXREVID,
						CompressionType.NONE)) {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in, StandardCharsets.UTF_8));
			inputLine = bufferedReader.readLine();
			bufferedReader.close();
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
		return this.localDumpfileDirectoryManager.hasFile(WmfDumpFile
				.getDumpFileName(this.dumpContentType, this.projectName,
						this.dateStamp))
				&& (this.getMaximalRevisionId() >= 0 || !WmfDumpFile
						.isRevisionDumpFile(this.dumpContentType));
	}

}
