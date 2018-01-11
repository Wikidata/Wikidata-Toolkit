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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
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
				WmfDumpFile.getDumpFileCompressionType(fileName));
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

		DirectoryManager thisDumpDirectoryManager = this.dumpfileDirectoryManager
				.getSubdirectoryManager(WmfDumpFile.getDumpFileDirectoryName(
						this.dumpContentType, this.dateStamp));

		long size;
		try (InputStream inputStream = webResourceFetcher
				.getInputStreamForUrl(urlString)) {
			size = thisDumpDirectoryManager.createFileAtomic(fileName,
					inputStream);
		}

		this.isPrepared = true;

		logger.info("... completed download of "
				+ this.dumpContentType.toString().toLowerCase() + " dump file "
				+ fileName + " from " + urlString + " (" + size + " bytes)");

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
		return WmfDumpFile.getDumpFileWebDirectory(this.dumpContentType,
				this.projectName) + this.dateStamp + "/";
	}

}
