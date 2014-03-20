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
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.util.DirectoryManager;
import org.wikidata.wdtk.util.WebResourceFetcher;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;

/**
 * Class for providing access to available dumpfiles provided by the Wikimedia
 * Foundation. The preferred access point for this class if
 * {@link #processAllRecentDumps(MwDumpFileProcessor, boolean)}, since this
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
	 * Processes all relevant dumps in order. For further details on the
	 * parameters, see {@link #findAllRelevantDumps(boolean)}.
	 * 
	 * @param preferCurrent
	 *            should dumps with current revisions be preferred?
	 * @return most recent main dump or null if no such dump exists
	 */
	public void processAllRecentDumps(MwDumpFileProcessor dumpFileProcessor,
			boolean preferCurrent) {

		for (MwDumpFile dumpFile : findAllRelevantDumps(preferCurrent)) {
			try (InputStream inputStream = dumpFile.getDumpFileStream()) {
				logger.info("Processing dump file " + dumpFile.toString());
				dumpFileProcessor
						.processDumpFileContents(inputStream, dumpFile);
			} catch (FileAlreadyExistsException e) {
				logger.error("Dump file "
						+ dumpFile.toString()
						+ " could not be processed since file "
						+ e.getFile()
						+ " already exists. Try deleting the file or dumpfile directory to attempt a new download.");
			} catch (IOException e) {
				logger.error("Dump file " + dumpFile.toString()
						+ " could not be processed: " + e.toString());
			}
		}
	}

	/**
	 * Finds all dump files, online or locally, that are relevant to obtain the
	 * most current state of the data.
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
	public List<MwDumpFile> findAllRelevantDumps(boolean preferCurrent) {
		MwDumpFile mainDump = findMostRecentMainDump(preferCurrent);
		if (mainDump == null) {
			return findAllDailyDumps();
		}

		List<MwDumpFile> result = new ArrayList<MwDumpFile>();

		for (MwDumpFile dumpFile : findAllDailyDumps()) {
			if (dumpFile.getDateStamp().compareTo(mainDump.getDateStamp()) > 0) {
				result.add(dumpFile);
			}
		}

		result.add(mainDump);

		return result;
	}

	/**
	 * Finds the most recent main dump (non-incremental dump). For further
	 * details on the parameters, see {@link #findAllRelevantDumps(boolean)}.
	 * 
	 * @param preferCurrent
	 *            should dumps with current revisions be preferred?
	 * @return most recent main dump or null if no such dump exists
	 */
	public MwDumpFile findMostRecentMainDump(boolean preferCurrent) {
		List<MwDumpFile> mainDumps;
		if (preferCurrent) {
			mainDumps = findAllCurrentDumps();
		} else {
			mainDumps = findAllFullDumps();
		}

		if (mainDumps.size() == 0) {
			return null;
		} else {
			return mainDumps.get(0);
		}

	}

	/**
	 * Returns a list of all daily dump files available either online or
	 * locally. For dumps available both online and locally, the local version
	 * is included. The list is order with most recent dump date first.
	 * 
	 * @return a list of daily dump files
	 */
	public List<MwDumpFile> findAllDailyDumps() {
		List<MwDumpFile> localDumps = findDumpsLocally(DumpContentType.DAILY);
		if (this.webResourceFetcher != null) {
			List<MwDumpFile> onlineDumps = findDailyDumpsOnline();
			return mergeDumpLists(localDumps, onlineDumps);
		} else {
			return localDumps;
		}
	}

	/**
	 * Returns a list of all current dump files available either online or
	 * locally. For dumps available both online and locally, the local version
	 * is included. The list is order with most recent dump date first.
	 * 
	 * @return a list of current dump files
	 */
	public List<MwDumpFile> findAllCurrentDumps() {
		List<MwDumpFile> localDumps = findDumpsLocally(DumpContentType.CURRENT);
		if (this.webResourceFetcher != null) {
			List<MwDumpFile> onlineDumps = findCurrentDumpsOnline();
			return mergeDumpLists(localDumps, onlineDumps);
		} else {
			return localDumps;
		}
	}

	/**
	 * Finds a list of all full dump files available either online or locally.
	 * For dumps available both online and locally, the local version is
	 * included. The list is order with most recent dump date first.
	 * 
	 * @return a list of full dump files
	 */
	public List<MwDumpFile> findAllFullDumps() {
		List<MwDumpFile> localDumps = findDumpsLocally(DumpContentType.FULL);
		if (this.webResourceFetcher != null) {
			List<MwDumpFile> onlineDumps = findFullDumpsOnline();
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
			return Collections.<MwDumpFile> emptyList();
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

		return result;
	}

	/**
	 * Finds out which daily dump files are available for download. The result
	 * is a list of objects that describe the available dump files, in
	 * descending order by their date. Not all of the dumps included might be
	 * actually available.
	 * 
	 * @return list of objects that provide information on available daily dumps
	 */
	List<MwDumpFile> findDailyDumpsOnline() {
		List<String> dumpFileDates = findDumpDatesOnline(WmfDumpFile.DAILY_WEB_DIRECTORY);

		List<MwDumpFile> result = new ArrayList<MwDumpFile>();

		for (String dateStamp : dumpFileDates) {
			result.add(new WmfOnlineDailyDumpFile(dateStamp, this.projectName,
					this.webResourceFetcher, this.dumpfileDirectoryManager));
		}

		return result;
	}

	/**
	 * Finds out which current version dump files are available for download.
	 * The result is a list of objects that describe the available dump files,
	 * in descending order by their date. Not all of the dumps included might be
	 * actually available.
	 * 
	 * @return list of objects that provide information on available current
	 *         dumps
	 */
	List<MwDumpFile> findCurrentDumpsOnline() {
		List<String> dumpFileDates = findDumpDatesOnline("");

		List<MwDumpFile> result = new ArrayList<MwDumpFile>();

		for (String dateStamp : dumpFileDates) {
			result.add(new WmfOnlineStandardDumpFile(dateStamp,
					this.projectName, this.webResourceFetcher,
					this.dumpfileDirectoryManager, DumpContentType.CURRENT));
		}

		return result;
	}

	/**
	 * Finds out which full dump files are available for download. The result is
	 * a list of objects that describe the available dump files, in descending
	 * order by their date. Not all of the dumps included might be actually
	 * available.
	 * 
	 * @return list of objects that provide information on available full dumps
	 */
	List<MwDumpFile> findFullDumpsOnline() {
		List<String> dumpFileDates = findDumpDatesOnline("");

		List<MwDumpFile> result = new ArrayList<MwDumpFile>();

		for (String dateStamp : dumpFileDates) {
			result.add(new WmfOnlineStandardDumpFile(dateStamp,
					this.projectName, this.webResourceFetcher,
					this.dumpfileDirectoryManager, DumpContentType.FULL));
		}

		return result;
	}

	/**
	 * Finds out which dump files are available for download in a given
	 * directory. The result is a list of YYYYMMDD date stamps, ordered newest
	 * to oldest. The list is based on the directories found at the target
	 * location, without considering whether or not each dump is actually
	 * available.
	 * 
	 * @param relativeDirectory
	 *            string of the relative directory to look for dump files, e.g.,
	 *            "other/incr/" for daily dumps or the empty string for main
	 *            dumps
	 * @return list of date stamps
	 */
	List<String> findDumpDatesOnline(String relativeDirectory) {
		List<String> result = new ArrayList<String>();
		try (InputStream in = this.webResourceFetcher
				.getInputStreamForUrl(WmfDumpFile.DUMP_SITE_BASE_URL
						+ relativeDirectory + this.projectName + "/")) {

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(in, StandardCharsets.UTF_8));
			String inputLine;
			while ((inputLine = bufferedReader.readLine()) != null) {
				if (inputLine.startsWith("<tr><td class=\"n\">")) {
					String dateStamp = inputLine.substring(27, 35);
					if (dateStamp
							.matches(WmfDumpFileManager.DATE_STAMP_PATTERN)) {
						result.add(dateStamp);
					}
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
