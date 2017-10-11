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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.util.DirectoryManager;
import org.wikidata.wdtk.util.WebResourceFetcher;

public class JsonOnlineDumpFile extends WmfDumpFile {

	static final Logger logger = LoggerFactory
			.getLogger(JsonOnlineDumpFile.class);

	final WebResourceFetcher webResourceFetcher;
	final DirectoryManager dumpfileDirectoryManager;

	private boolean isPrepared;

	/**
	 * Constructor. Currently only "wikidatawiki" is supported as a project
	 * name, since the dumps are placed under a non-systematic directory
	 * structure that must be hard-coded for each project.
	 *
	 * @param dateStamp
	 *            dump date in format YYYYMMDD
	 * @param projectName
	 *            project name string (e.g. "wikidatawiki")
	 * @param webResourceFetcher
	 *            object to use for accessing the web
	 * @param dumpfileDirectoryManager
	 *            the directory manager for the directory where dumps should be
	 *            downloaded to
	 */
	public JsonOnlineDumpFile(String dateStamp, String projectName,
			WebResourceFetcher webResourceFetcher,
			DirectoryManager dumpfileDirectoryManager) {
		super(dateStamp, projectName);
		this.webResourceFetcher = webResourceFetcher;
		this.dumpfileDirectoryManager = dumpfileDirectoryManager;
	}

	@Override
	public DumpContentType getDumpContentType() {
		return DumpContentType.JSON;
	}

	@Override
	public InputStream getDumpFileStream() throws IOException {
		prepareDumpFile();

		String fileName = WmfDumpFile.getDumpFileName(DumpContentType.JSON,
				this.projectName, this.dateStamp);
		DirectoryManager dailyDirectoryManager = this.dumpfileDirectoryManager
				.getSubdirectoryManager(WmfDumpFile.getDumpFileDirectoryName(
						DumpContentType.JSON, this.dateStamp));

		return dailyDirectoryManager.getInputStreamForFile(fileName, WmfDumpFile.getDumpFileCompressionType(fileName));
	}

	@Override
	public void prepareDumpFile() throws IOException {
		if (this.isPrepared) {
			return;
		}

		String fileName = WmfDumpFile.getDumpFileName(DumpContentType.JSON,
				this.projectName, this.dateStamp);
		String urlString = getBaseUrl() + fileName;

		logger.info("Downloading JSON dump file " + fileName + " from "
				+ urlString + " ...");

		if (!isAvailable()) {
			throw new IOException(
					"Dump file not available (yet). Aborting dump retrieval.");
		}

		DirectoryManager dailyDirectoryManager = this.dumpfileDirectoryManager
				.getSubdirectoryManager(WmfDumpFile.getDumpFileDirectoryName(
						DumpContentType.JSON, this.dateStamp));

		try (InputStream inputStream = webResourceFetcher
				.getInputStreamForUrl(urlString)) {
			dailyDirectoryManager.createFileAtomic(fileName, inputStream);
		}

		this.isPrepared = true;

		logger.info("... completed download of JSON dump file " + fileName
				+ " from " + urlString);
	}

	@Override
	protected boolean fetchIsDone() {
		// WMF provides no easy way to check this for these files;
		// so just assume it is done
		return true;
	}

	/**
	 * Returns the base URL under which the files for this dump are found.
	 *
	 * @return base URL
	 */
	String getBaseUrl() {
		return WmfDumpFile.getDumpFileWebDirectory(DumpContentType.JSON,
				this.projectName);
	}

}
