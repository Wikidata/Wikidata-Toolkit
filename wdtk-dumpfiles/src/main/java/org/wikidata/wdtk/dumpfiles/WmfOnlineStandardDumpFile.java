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

	final WebResourceFetcher webResourceFetcher;
	final DirectoryManager dumpfileDirectoryManager;
	final DumpContentType dumpContentType;

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
	public BufferedReader getDumpFileReader() throws IOException {
		String fileName = getFileName();
		String urlString = getBaseUrl() + fileName;

		if (this.getMaximalRevisionId() == -1) {
			throw new IOException(
					"Failed to retrieve maximal revision id. Aborting dump retrieval.");
		}

		String dumpLabel = this.dumpContentType.toString().toLowerCase();
		DirectoryManager thisDumpDirectoryManager = this.dumpfileDirectoryManager
				.getSubdirectoryManager(dumpLabel + "-" + this.dateStamp);

		try (InputStream inputStream = webResourceFetcher
				.getInputStreamForUrl(urlString)) {
			thisDumpDirectoryManager.createFile(fileName, inputStream);
		}

		thisDumpDirectoryManager.createFile("maxrevid.txt", this
				.getMaximalRevisionId().toString());

		return thisDumpDirectoryManager.getBufferedReaderForBz2File(fileName);
	}

	@Override
	protected Long fetchMaximalRevisionId() {
		Long maxRevId = -1L;
		String urlString = getBaseUrl();
		try (BufferedReader in = this.webResourceFetcher
				.getBufferedReaderForUrl(urlString)) {
			// Search for the line with the download link. The line just before
			// that contains the maximal revision id, formatted as
			// "[max 123456789]".
			String inputLine;
			String previousLine = "";
			String linePattern = getFileName();
			while (maxRevId < 0 && (inputLine = in.readLine()) != null) {
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
		} catch (IOException e) {
			// file not found or not readable; just fall through to return -1
		}
		return maxRevId;
	}

	// Old code below that extracts data from the site stats table.
	// It is unclear how this data is related to the dumps (which are generated
	// at different times)
	// protected Long fetchMaximalRevisionId() {
	// Long maxRevId = -1L;
	// String urlString = getBaseUrl() + this.projectName + "-" + dateStamp
	// + "-site_stats.sql.gz";
	// try (BufferedReader in = this.webResourceFetcher
	// .getBufferedReaderForGzipUrl(urlString)) {
	// String inputLine;
	// while (maxRevId < 0 && (inputLine = in.readLine()) != null) {
	// if (inputLine.startsWith("INSERT INTO `site_stats` VALUES (")) {
	// String[] values = inputLine.split(",", 4);
	// if (values.length == 4) {
	// try {
	// maxRevId = new Long(values[2]);
	// } catch (NumberFormatException e) {
	// // could not parse number; just continue and
	// // probably return -1
	// }
	// }
	// }
	// }
	// } catch (IOException e) {
	// // file not found or not readable; just fall through to return -1
	// }
	// return maxRevId;
	// }

	@Override
	protected boolean fetchIsDone() {
		boolean found = false;
		try (BufferedReader in = this.webResourceFetcher
				.getBufferedReaderForUrl(getBaseUrl() + this.projectName + "-"
						+ dateStamp + "-md5sums.txt")) {
			String inputLine;
			String filePostfix = WmfDumpFile
					.getDumpFilePostfix(this.dumpContentType);
			while (!found && (inputLine = in.readLine()) != null) {
				if (inputLine.endsWith(filePostfix)) {
					found = true;
				}
			}
		} catch (IOException e) {
			// file not found or not readable; just return false
		}
		return found;
	}

	/**
	 * Get the base URL under which the files for this dump are found.
	 * 
	 * @return base URL
	 */
	String getBaseUrl() {
		return WmfDumpFile.DUMP_SITE_BASE_URL + this.projectName + "/"
				+ this.dateStamp + "/";
	}

	/**
	 * Get the file name of the files for this dump.
	 * 
	 * @return file name
	 */
	String getFileName() {
		return this.projectName + "-" + this.dateStamp
				+ WmfDumpFile.getDumpFilePostfix(this.dumpContentType);
	}

}
