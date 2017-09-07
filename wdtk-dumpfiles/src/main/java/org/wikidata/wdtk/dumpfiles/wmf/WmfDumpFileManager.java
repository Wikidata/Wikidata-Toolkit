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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwDumpFile;
import org.wikidata.wdtk.util.DirectoryManager;
import org.wikidata.wdtk.util.WebResourceFetcher;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;

/**
 * Class for providing access to available dumpfiles provided by the Wikimedia
 * Foundation. The preferred access point for this class is
 * {@link DumpProcessingController#processAllRecentRevisionDumps()}, since this
 * method takes care of freeing resources and might also provide parallelized
 * downloading/processing in the future.
 * <p>
 * Typically, the Web will be accessed to find information about dumps available
 * online. This Web access is mediated by a {@link WebResourceFetcherImpl}
 * object, provided upon construction. If null is given instead, the class will
 * operate in offline mode, using only previously downloaded files.
 * <p>
 * The location of the Wikimedia download site is currently hardwired, since the
 * extraction methods used to get the data are highly specific to the format of
 * files on this site. Other sites (if any) would most likely need different
 * methods.
 *
 * @author Markus Kroetzsch
 *
 */
public class WmfDumpFileManager {

	static final Logger logger = LoggerFactory
			.getLogger(WmfDumpFileManager.class);

	/**
	 * The regular expression that a date stamp should match.
	 */
	static final String DATE_STAMP_PATTERN = "\\d\\d\\d\\d\\d\\d\\d\\d";

	/**
	 * The name of the directory where downloaded dump files are stored.
	 */
	public static final String DOWNLOAD_DIRECTORY_NAME = "dumpfiles";

	final String projectName;
	final DirectoryManager dumpfileDirectoryManager;
	final WebResourceFetcher webResourceFetcher;

	/**
	 * Constructor.
	 *
	 * @param projectName
	 *            name of the project to obtain dumps for as used in the folder
	 *            structure of the dump site, e.g., "wikidatawiki"
	 * @param downloadDirectoryManager
	 *            the directory manager for the directory where the download
	 *            directory for dump files should be; it will be created if
	 *            needed
	 * @param webResourceFetcher
	 *            the web resource fetcher to access web resources or null if no
	 *            web access should happen
	 * @throws IOException
	 *             if it was not possible to access the directory for managing
	 *             dumpfiles
	 */
	public WmfDumpFileManager(String projectName,
			DirectoryManager downloadDirectoryManager,
			WebResourceFetcher webResourceFetcher) throws IOException {
		this.projectName = projectName;
		this.dumpfileDirectoryManager = downloadDirectoryManager
				.getSubdirectoryManager(
						WmfDumpFileManager.DOWNLOAD_DIRECTORY_NAME)
				.getSubdirectoryManager(projectName);
		this.webResourceFetcher = webResourceFetcher;

		WmfDumpFileManager.logger.info("Using download directory "
				+ this.dumpfileDirectoryManager.toString());
	}

	/**
	 * Finds all page revision dump files, online or locally, that are relevant
	 * to obtain the most current state of the data. Revision dump files are
	 * dumps that contain page revisions in MediaWiki's XML format.
	 * <p>
	 * If the parameter <b>preferCurrent</b> is true, then dumps that contain
	 * only the current versions of all files will be preferred if available
	 * anywhere, even over previously downloaded dump files that contain all
	 * versions. However, dump files may still contain non-current revisions,
	 * and when processing multiple dumps there might even be overlaps (one
	 * revision occurring in multiple dumps).
	 * <p>
	 * The result is ordered with the most recent dump first. If a dump file A
	 * contains revisions of a page P, and Rmax is the maximal revision of P in
	 * A, then every dump file that comes after A should contain only revisions
	 * of P that are smaller than or equal to Rmax. In other words, the maximal
	 * revision found in the first file that contains P at all should also be
	 * the maximal revision overall.
	 *
	 * @param preferCurrent
	 *            should dumps with current revisions be preferred?
	 * @return an ordered list of all dump files that match the given criteria
	 */
	public List<MwDumpFile> findAllRelevantRevisionDumps(boolean preferCurrent) {
		MwDumpFile mainDump;
		if (preferCurrent) {
			mainDump = findMostRecentDump(DumpContentType.CURRENT);
		} else {
			mainDump = findMostRecentDump(DumpContentType.FULL);
		}
		if (mainDump == null) {
			return findAllDumps(DumpContentType.DAILY);
		}

		List<MwDumpFile> result = new ArrayList<MwDumpFile>();

		for (MwDumpFile dumpFile : findAllDumps(DumpContentType.DAILY)) {
			if (dumpFile.getDateStamp().compareTo(mainDump.getDateStamp()) > 0) {
				result.add(dumpFile);
			}
		}

		result.add(mainDump);

		if (logger.isInfoEnabled()) {
			StringBuilder logMessage = new StringBuilder();
			logMessage.append("Found ")
					.append(result.size())
					.append(" relevant dumps to process:");
			for (MwDumpFile dumpFile : result) {
				logMessage.append("\n * ").append(dumpFile.toString());
			}
			logger.info(logMessage.toString());
		}

		return result;
	}

	/**
	 * Finds the most recent dump of the given type that is actually available.
	 *
	 * @param dumpContentType
	 *            the type of the dump to look for
	 * @return most recent main dump or null if no such dump exists
	 */
	public MwDumpFile findMostRecentDump(DumpContentType dumpContentType) {
		List<MwDumpFile> dumps = findAllDumps(dumpContentType);

		for (MwDumpFile dump : dumps) {
			if (dump.isAvailable()) {
				return dump;
			}
		}
		return null;
	}

	/**
	 * Returns a list of all dump files of the given type available either
	 * online or locally. For dumps available both online and locally, the local
	 * version is included. The list is ordered with most recent dump date
	 * first. Online dumps found by this method might not be available yet (if
	 * their directory has been created online but the file was not uploaded or
	 * completely written yet).
	 *
	 * @return a list of dump files of the given type
	 */
	public List<MwDumpFile> findAllDumps(DumpContentType dumpContentType) {
		List<MwDumpFile> localDumps = findDumpsLocally(dumpContentType);
		if (this.webResourceFetcher != null) {
			List<MwDumpFile> onlineDumps = findDumpsOnline(dumpContentType);
			return mergeDumpLists(localDumps, onlineDumps);
		} else {
			return localDumps;
		}
	}

	/**
	 * Merges a list of local and online dumps. For dumps available both online
	 * and locally, only the local version is included. The list is order with
	 * most recent dump date first.
	 *
	 * @return a merged list of dump files
	 */
	List<MwDumpFile> mergeDumpLists(List<MwDumpFile> localDumps,
			List<MwDumpFile> onlineDumps) {
		List<MwDumpFile> result = new ArrayList<MwDumpFile>(localDumps);

		HashSet<String> localDateStamps = new HashSet<String>();
		for (MwDumpFile dumpFile : localDumps) {
			localDateStamps.add(dumpFile.getDateStamp());
		}
		for (MwDumpFile dumpFile : onlineDumps) {
			if (!localDateStamps.contains(dumpFile.getDateStamp())) {
				result.add(dumpFile);
			}
		}
		Collections.sort(result,
				Collections.reverseOrder(new MwDumpFile.DateComparator()));
		return result;
	}

	/**
	 * Finds out which dump files of the given type have been downloaded
	 * already. The result is a list of objects that describe the available dump
	 * files, in descending order by their date. Not all of the dumps included
	 * might be actually available.
	 *
	 * @param dumpContentType
	 *            the type of dump to consider
	 * @return list of objects that provide information on available dumps
	 */
	List<MwDumpFile> findDumpsLocally(DumpContentType dumpContentType) {

		String directoryPattern = WmfDumpFile.getDumpFileDirectoryName(
				dumpContentType, "*");

		List<String> dumpFileDirectories;
		try {
			dumpFileDirectories = this.dumpfileDirectoryManager
					.getSubdirectories(directoryPattern);
		} catch (IOException e) {
			logger.error("Unable to access dump directory: " + e.toString());
			return Collections.emptyList();
		}

		List<MwDumpFile> result = new ArrayList<MwDumpFile>();

		for (String directory : dumpFileDirectories) {
			String dateStamp = WmfDumpFile
					.getDateStampFromDumpFileDirectoryName(dumpContentType,
							directory);
			if (dateStamp.matches(WmfDumpFileManager.DATE_STAMP_PATTERN)) {
				WmfLocalDumpFile dumpFile = new WmfLocalDumpFile(dateStamp,
						this.projectName, dumpfileDirectoryManager,
						dumpContentType);
				if (dumpFile.isAvailable()) {
					result.add(dumpFile);
				} else {
					logger.error("Incomplete local dump file data. Maybe delete "
							+ dumpFile.getDumpfileDirectory()
							+ " to attempt fresh download.");
				}
			} // else: silently ignore directories that don't match
		}

		Collections.sort(result,
				Collections.reverseOrder(new MwDumpFile.DateComparator()));

		logger.info("Found " + result.size() + " local dumps of type "
				+ dumpContentType + ": " + result);

		return result;
	}

	/**
	 * Finds out which dump files of the given type are available for download.
	 * The result is a list of objects that describe the available dump files,
	 * in descending order by their date. Not all of the dumps included might be
	 * actually available.
	 *
	 * @return list of objects that provide information on available full dumps
	 */
	List<MwDumpFile> findDumpsOnline(DumpContentType dumpContentType) {
		List<String> dumpFileDates = findDumpDatesOnline(dumpContentType);

		List<MwDumpFile> result = new ArrayList<MwDumpFile>();

		for (String dateStamp : dumpFileDates) {
			if (dumpContentType == DumpContentType.DAILY) {
				result.add(new WmfOnlineDailyDumpFile(dateStamp,
						this.projectName, this.webResourceFetcher,
						this.dumpfileDirectoryManager));
			} else if (dumpContentType == DumpContentType.JSON) {
				result.add(new JsonOnlineDumpFile(dateStamp, this.projectName,
						this.webResourceFetcher, this.dumpfileDirectoryManager));
			} else {
				result.add(new WmfOnlineStandardDumpFile(dateStamp,
						this.projectName, this.webResourceFetcher,
						this.dumpfileDirectoryManager, dumpContentType));
			}
		}

		logger.info("Found " + result.size() + " online dumps of type "
				+ dumpContentType + ": " + result);

		return result;
	}

	/**
	 * Finds out which dump files are available for download in a given
	 * directory. The result is a list of YYYYMMDD date stamps, ordered newest
	 * to oldest. The list is based on the directories or files found at the
	 * target location, without considering whether or not each dump is actually
	 * available.
	 * <p>
	 * The implementation is rather uniform since all cases supported thus far
	 * use directory/file names that start with a date stamp. If the date would
	 * occur elsewhere or in another form, then more work would be needed.
	 *
	 * @param dumpContentType
	 *            the type of dump to consider
	 * @return list of date stamps
	 */
	List<String> findDumpDatesOnline(DumpContentType dumpContentType) {
		List<String> result = new ArrayList<String>();
		try (InputStream in = this.webResourceFetcher
				.getInputStreamForUrl(WmfDumpFile.getDumpFileWebDirectory(
						dumpContentType, this.projectName))) {

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in, StandardCharsets.UTF_8));
			String inputLine;
			while ((inputLine = bufferedReader.readLine()) != null) {
				String dateStamp = "";
				if (inputLine.startsWith("<tr><td class=\"n\">")) {
					// old format of HTML file lists
					dateStamp = inputLine.substring(27, 35);
				} else if (inputLine.startsWith("<a href=")) {
					// new Jan 2015 of HTML file lists
					dateStamp = inputLine.substring(9, 17);
				}
				if (dateStamp.matches(WmfDumpFileManager.DATE_STAMP_PATTERN)) {
					result.add(dateStamp);
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			logger.error("Failed to fetch available dump dates online.");
		}

		Collections.sort(result, Collections.reverseOrder());
		return result;
	}
}
