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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.util.DirectoryManager;
import org.wikidata.wdtk.util.WebResourceFetcher;

/**
 * Class for representing incremental daily dump files as published by the
 * Wikimedia Foundation. The dump file and additional information about its
 * status is online and web access is needed to fetch this data on demand.
 * 
 * @author Markus Kroetzsch
 * 
 */
class WmfOnlineDailyDumpFile extends WmfDumpFile {

	static final Logger logger = LoggerFactory
			.getLogger(WmfOnlineDailyDumpFile.class);

	final WebResourceFetcher webResourceFetcher;
	final DirectoryManager dumpfileDirectoryManager;

	/**
	 * Constructor.
	 * 
	 * @param dateStamp
	 *            dump date in format YYYYMMDD
	 * @param projectName
	 *            project name string
	 * @param webResourceFetcher
	 *            object to use for accessing the web
	 * @param dumpfileDirectoryManager
	 *            the directory manager for the directory where dumps should be
	 *            downloaded to
	 */
	public WmfOnlineDailyDumpFile(String dateStamp, String projectName,
			WebResourceFetcher webResourceFetcher,
			DirectoryManager dumpfileDirectoryManager) {
		super(dateStamp, projectName);
		this.webResourceFetcher = webResourceFetcher;
		this.dumpfileDirectoryManager = dumpfileDirectoryManager;
	}

	@Override
	public DumpContentType getDumpContentType() {
		return DumpContentType.DAILY;
	}

	@Override
	public InputStream getDumpFileStream() throws IOException {
		prepareDumpFile();

		String fileName = WmfDumpFile.getDumpFileName(DumpContentType.DAILY,
				this.projectName, this.dateStamp);
		DirectoryManager dailyDirectoryManager = this.dumpfileDirectoryManager
				.getSubdirectoryManager(WmfDumpFile.getDumpFileDirectoryName(
						DumpContentType.DAILY, this.dateStamp));

		return dailyDirectoryManager.getInputStreamForFile(fileName,
				WmfDumpFile.getDumpFileCompressionType(DumpContentType.DAILY));
	}

	@Override
	public void prepareDumpFile() throws IOException {
		String fileName = WmfDumpFile.getDumpFileName(DumpContentType.DAILY,
				this.projectName, this.dateStamp);
		String urlString = getBaseUrl() + fileName;

		logger.info("Downloading daily dump file " + fileName + " from "
				+ urlString + " ...");

		if (this.getMaximalRevisionId() == -1L) {
			throw new IOException(
					"Failed to retrieve maximal revision id. Aborting dump retrieval.");
		}

		DirectoryManager dailyDirectoryManager = this.dumpfileDirectoryManager
				.getSubdirectoryManager(WmfDumpFile.getDumpFileDirectoryName(
						DumpContentType.DAILY, this.dateStamp));

		try (InputStream inputStream = webResourceFetcher
				.getInputStreamForUrl(urlString)) {
			dailyDirectoryManager.createFile(fileName, inputStream);
		}

		dailyDirectoryManager.createFile(WmfDumpFile.LOCAL_FILENAME_MAXREVID,
				this.getMaximalRevisionId().toString());

		logger.info("... Completed download of daily dump file " + fileName
				+ " from " + urlString);
	}

	@Override
	protected Long fetchMaximalRevisionId() {
		String inputLine;
		try (InputStream in = this.webResourceFetcher
				.getInputStreamForUrl(getBaseUrl() + "maxrevid.txt")) {
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
		boolean result;
		try (InputStream in = this.webResourceFetcher
				.getInputStreamForUrl(getBaseUrl() + "status.txt")) {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in, StandardCharsets.UTF_8));
			String inputLine = bufferedReader.readLine();
			bufferedReader.close();
			result = "done".equals(inputLine);
		} catch (IOException e) { // file not found or not readable
			result = false;
		}
		return result;
	}

	/**
	 * Returns the base URL under which the files for this dump are found.
	 * 
	 * @return base URL
	 */
	String getBaseUrl() {
		return WmfDumpFile.DUMP_SITE_BASE_URL
				+ WmfDumpFile.getDumpFileWebDirectory(DumpContentType.DAILY)
				+ this.projectName + "/" + this.dateStamp + "/";
	}

}