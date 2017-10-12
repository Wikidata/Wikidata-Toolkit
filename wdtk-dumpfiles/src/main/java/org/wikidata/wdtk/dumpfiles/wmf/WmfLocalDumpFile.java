package org.wikidata.wdtk.dumpfiles.wmf;

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

import org.wikidata.wdtk.dumpfiles.DumpContentType;
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
				dumpFileName, WmfDumpFile.getDumpFileCompressionType(dumpFileName));
	}

	@Override
	public void prepareDumpFile() throws IOException {
		// nothing to do
	}

	@Override
	protected boolean fetchIsDone() {
		return this.localDumpfileDirectoryManager.hasFile(WmfDumpFile
				.getDumpFileName(this.dumpContentType, this.projectName,
						this.dateStamp));
	}

}
