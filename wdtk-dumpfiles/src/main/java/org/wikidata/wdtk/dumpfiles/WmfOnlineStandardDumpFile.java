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
 * Class for representing dump files published by the Wikimedia Foundation in
 * the main common location of all dump files. This excludes incremental daily
 * dumps, which are found in another directory. The dump file and additional
 * information about its status is online and web access is needed to fetch this
 * data on demand.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class WmfOnlineStandardDumpFile extends WmfDumpFile {

	static final Logger logger = LoggerFactory
			.getLogger(WmfOnlineStandardDumpFile.class);

	final WebResourceFetcher webResourceFetcher;
	final DirectoryManager dumpfileDirectoryManager;
	final DumpContentType dumpContentType;

	/**
	 * Set to true when all required files have been downloaded successfully.
	 */
	boolean isPrepared = false;

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
	 * @param dumpContentType
	 *            the type of dump this represents
	 */
	public WmfOnlineStandardDumpFile(String dateStamp, String projectName,
			WebResourceFetcher webResourceFetcher,
			DirectoryManager dumpfileDirectoryManager,
			DumpContentType dumpContentType) {

		super(dateStamp, projectName);
		this.webResourceFetcher = webResourceFetcher;
		this.dumpfileDirectoryManager = dumpfileDirectoryManager;
		this.dumpContentType = dumpContentType;
	}

	@Override
	public DumpContentType getDumpContentType() {
		return this.dumpContentType;
	}

	@Override
	public InputStream getDumpFileStream() throws IOException {
		prepareDumpFile();

		String fileName = WmfDumpFile.getDumpFileName(this.dumpContentType,
				this.projectName, this.dateStamp);
		DirectoryManager thisDumpDirectoryManager = this.dumpfileDirectoryManager
				.getSubdirectoryManager(WmfDumpFile.getDumpFileDirectoryName(
						this.dumpContentType, this.dateStamp));

		return thisDumpDirectoryManager.getInputStreamForFile(fileName,
				WmfDumpFile.getDumpFileCompressionType(this.dumpContentType));
	}

	@Override
	public void prepareDumpFile() throws IOException {
		if (this.isPrepared) {
			return;
		}

		String fileName = WmfDumpFile.getDumpFileName(this.dumpContentType,
				this.projectName, this.dateStamp);
		String urlString = getBaseUrl() + fileName;

		logger.info("Downloading "
				+ this.dumpContentType.toString().toLowerCase() + " dump file "
				+ fileName + " from " + urlString + " ...");

		if (!isAvailable()) {
			throw new IOException(
					"Dump file not available (yet). Aborting dump retrieval.");
		}

		if (WmfDumpFile.isRevisionDumpFile(this.dumpContentType)
				&& this.getMaximalRevisionId() == -1) {
			throw new IOException(
					"Failed to retrieve maximal revision id. Aborting dump retrieval.");
		}

		DirectoryManager thisDumpDirectoryManager = this.dumpfileDirectoryManager
				.getSubdirectoryManager(WmfDumpFile.getDumpFileDirectoryName(
						this.dumpContentType, this.dateStamp));

		try (InputStream inputStream = webResourceFetcher
				.getInputStreamForUrl(urlString)) {
			thisDumpDirectoryManager.createFile(fileName, inputStream);
		}

		if (WmfDumpFile.isRevisionDumpFile(this.dumpContentType)) {
			thisDumpDirectoryManager.createFile(
					WmfDumpFile.LOCAL_FILENAME_MAXREVID, this
							.getMaximalRevisionId().toString());
		}

		this.isPrepared = true;

		logger.info("... completed download of "
				+ this.dumpContentType.toString().toLowerCase() + " dump file "
				+ fileName + " from " + urlString);

	}

	@Override
	protected Long fetchMaximalRevisionId() {
		if (!WmfDumpFile.isRevisionDumpFile(this.dumpContentType)) {
			return -1L;
		}

		Long maxRevId = -1L;
		String urlString = getBaseUrl();
		try (InputStream in = this.webResourceFetcher
				.getInputStreamForUrl(urlString)) {
			// Search for the line with the download link. The line just before
			// that contains the maximal revision id, formatted as
			// "[max 123456789]".
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in, StandardCharsets.UTF_8));
			String inputLine;
			String previousLine = "";
			String linePattern = WmfDumpFile.getDumpFileName(
					this.dumpContentType, this.projectName, this.dateStamp);
			while (maxRevId < 0
					&& (inputLine = bufferedReader.readLine()) != null) {
				if (inputLine.indexOf(linePattern) >= 0) {
					int startIndex = previousLine.lastIndexOf("[max ") + 5;
					int endIndex = previousLine.indexOf("]", startIndex);
					if (endIndex != -1) {
						try {
							maxRevId = new Long(previousLine.substring(
									startIndex, endIndex));
						} catch (NumberFormatException e) {
							// could not parse number; just continue and
							// probably return -1
						}
					}
				}
				previousLine = inputLine;
			}
			bufferedReader.close();
		} catch (IOException e) {
			// file not found or not readable; just fall through to return -1
		}
		return maxRevId;
	}

	@Override
	protected boolean fetchIsDone() {
		boolean found = false;
		try (InputStream in = this.webResourceFetcher
				.getInputStreamForUrl(getBaseUrl() + this.projectName + "-"
						+ dateStamp + "-md5sums.txt")) {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in, StandardCharsets.UTF_8));
			String inputLine;
			String filePostfix = WmfDumpFile
					.getDumpFilePostfix(this.dumpContentType);
			while (!found && (inputLine = bufferedReader.readLine()) != null) {
				if (inputLine.endsWith(filePostfix)) {
					found = true;
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			// file not found or not readable; just return false
		}
		return found;
	}

	/**
	 * Returns the base URL under which the files for this dump are found.
	 * 
	 * @return base URL
	 */
	String getBaseUrl() {
		return WmfDumpFile.DUMP_SITE_BASE_URL
				+ WmfDumpFile.getDumpFileWebDirectory(this.dumpContentType)
				+ this.projectName + "/" + this.dateStamp + "/";
	}

}
