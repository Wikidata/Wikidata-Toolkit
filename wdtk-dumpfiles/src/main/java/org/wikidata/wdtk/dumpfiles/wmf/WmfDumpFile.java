package org.wikidata.wdtk.dumpfiles.wmf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.MwDumpFile;
import org.wikidata.wdtk.util.CompressionType;

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

/**
 * Abstract base class for dump files provided by the Wikimedia Foundation.
 *
 * @author Markus Kroetzsch
 *
 */
public abstract class WmfDumpFile implements MwDumpFile {

	/**
	 * The default URL of the website to obtain the dump files from.
	 */
	protected static final String DUMP_SITE_BASE_URL = "https://dumps.wikimedia.org/";

	/**
	 * Hash map defining the relative Web directory of each type of dump.
	 */
	static final Map<DumpContentType, String> WEB_DIRECTORY = new HashMap<DumpContentType, String>();
	static {
		WmfDumpFile.WEB_DIRECTORY.put(DumpContentType.DAILY, "other/incr/");
		WmfDumpFile.WEB_DIRECTORY.put(DumpContentType.CURRENT, "");
		WmfDumpFile.WEB_DIRECTORY.put(DumpContentType.FULL, "");
		WmfDumpFile.WEB_DIRECTORY.put(DumpContentType.SITES, "");
		WmfDumpFile.WEB_DIRECTORY.put(DumpContentType.JSON, "other/");
	}

	/**
	 * Hash map defining file name ending of each type of dump.
	 */
	static final Map<DumpContentType, String> POSTFIXES = new HashMap<DumpContentType, String>();
	static {
		WmfDumpFile.POSTFIXES.put(DumpContentType.DAILY,
				"-pages-meta-hist-incr.xml.bz2");
		WmfDumpFile.POSTFIXES.put(DumpContentType.CURRENT,
				"-pages-meta-current.xml.bz2");
		WmfDumpFile.POSTFIXES.put(DumpContentType.FULL,
				"-pages-meta-history.xml.bz2");
		WmfDumpFile.POSTFIXES.put(DumpContentType.SITES, "-sites.sql.gz");
		WmfDumpFile.POSTFIXES.put(DumpContentType.JSON, ".json.gz");
	}

	/**
	 * Hash map defining whether a certain type of dump is a dump of page
	 * revisions or not. Dumps with page revisions have a maximal revision id,
	 * while other dump files do not.
	 */
	static final Map<DumpContentType, Boolean> REVISION_DUMP = new HashMap<DumpContentType, Boolean>();
	static {
		WmfDumpFile.REVISION_DUMP.put(DumpContentType.DAILY, true);
		WmfDumpFile.REVISION_DUMP.put(DumpContentType.CURRENT, true);
		WmfDumpFile.REVISION_DUMP.put(DumpContentType.FULL, true);
		WmfDumpFile.REVISION_DUMP.put(DumpContentType.SITES, false);
		WmfDumpFile.REVISION_DUMP.put(DumpContentType.JSON, false);
	}

	protected final String dateStamp;
	protected final String projectName;
	Boolean isDone;

	public WmfDumpFile(String dateStamp, String projectName) {
		this.dateStamp = dateStamp;
		this.projectName = projectName;
	}

	@Override
	public String getProjectName() {
		return this.projectName;
	}

	@Override
	public String getDateStamp() {
		return this.dateStamp;
	}

	@Override
	public boolean isAvailable() {
		if (isDone == null) {
			isDone = fetchIsDone();
		}
		return isDone;
	}

	@Override
	public String toString() {
		return this.projectName + "-"
				+ getDumpContentType().toString().toLowerCase() + "-"
				+ this.dateStamp;
	}

	@Override
	public BufferedReader getDumpFileReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getDumpFileStream(),
				StandardCharsets.UTF_8));
	}

	/**
	 * Finds out if the dump is ready. For online dumps, this should return true
	 * if the file can be fetched from the Web. For local dumps, this should
	 * return true if the file is complete and not corrupted. For some types of
	 * dumps, there are ways of checking this easily (i.e., without reading the
	 * full file). If this is not possible, then the method should just return
	 * "true."
	 *
	 * @return true if the dump is done
	 */
	protected abstract boolean fetchIsDone();

	/**
	 * Returns the ending used by the Wikimedia-provided dumpfile names of the
	 * given type.
	 *
	 * @param dumpContentType
	 *            the type of dump
	 * @return postfix of the dumpfile name
	 * @throws IllegalArgumentException
	 *             if the given dump file type is not known
	 */
	public static String getDumpFilePostfix(DumpContentType dumpContentType) {
		if (WmfDumpFile.POSTFIXES.containsKey(dumpContentType)) {
			return WmfDumpFile.POSTFIXES.get(dumpContentType);
		} else {
			throw new IllegalArgumentException("Unsupported dump type "
					+ dumpContentType);
		}
	}

	/**
	 * Returns the absolute directory on the Web site where dumpfiles of the
	 * given type can be found.
	 *
	 * @param dumpContentType
	 *            the type of dump
	 * @return relative web directory for the current dumpfiles
	 * @throws IllegalArgumentException
	 *             if the given dump file type is not known
	 */
	public static String getDumpFileWebDirectory(
			DumpContentType dumpContentType, String projectName) {
		if (dumpContentType == DumpContentType.JSON) {
			if ("wikidatawiki".equals(projectName)) {
				return WmfDumpFile.DUMP_SITE_BASE_URL
						+ WmfDumpFile.WEB_DIRECTORY.get(dumpContentType)
						+ "wikidata" + "/";
			} else {
				throw new RuntimeException(
						"Wikimedia Foundation uses non-systematic directory names for this type of dump file."
								+ " I don't know where to find dumps of project "
								+ projectName);
			}
		} else if (WmfDumpFile.WEB_DIRECTORY.containsKey(dumpContentType)) {
			return WmfDumpFile.DUMP_SITE_BASE_URL
					+ WmfDumpFile.WEB_DIRECTORY.get(dumpContentType)
					+ projectName + "/";
		} else {
			throw new IllegalArgumentException("Unsupported dump type "
					+ dumpContentType);
		}
	}

	/**
	 * Returns the compression type of this kind of dump file using file suffixes
	 *
	 * @param fileName the name of the file
	 * @return compression type
	 * @throws IllegalArgumentException
	 *             if the given dump file type is not known
	 */
	public static CompressionType getDumpFileCompressionType(String fileName) {
		if (fileName.endsWith(".gz")) {
			return CompressionType.GZIP;
		} else if (fileName.endsWith(".bz2")) {
			return CompressionType.BZ2;
		} else {
			return CompressionType.NONE;
		}
	}

	/**
	 * Returns the name of the directory where the dumpfile of the given type
	 * and date should be stored.
	 *
	 * @param dumpContentType
	 *            the type of the dump
	 * @param dateStamp
	 *            the date of the dump in format YYYYMMDD
	 * @return the local directory name for the dumpfile
	 */
	public static String getDumpFileDirectoryName(
			DumpContentType dumpContentType, String dateStamp) {
		return dumpContentType.toString().toLowerCase() + "-" + dateStamp;
	}

	/**
	 * Extracts the date stamp from a dumpfile directory name in the form that
	 * is created by {@link #getDumpFileDirectoryName(DumpContentType, String)}.
	 * It is not checked that the given directory name has the right format; if
	 * it has not, the result will not be a date stamp but some other string.
	 *
	 * @param dumpContentType
	 * @param directoryName
	 * @return the date stamp
	 */
	public static String getDateStampFromDumpFileDirectoryName(
			DumpContentType dumpContentType, String directoryName) {
		int prefixLength = dumpContentType.toString().length() + 1;
		return directoryName.substring(prefixLength);
	}

	/**
	 * Returns the name under which this dump file. This is the name used online
	 * and also locally when downloading the file.
	 *
	 * @param dumpContentType
	 *            the type of the dump
	 * @param projectName
	 *            the project name, e.g. "wikidatawiki"
	 * @param dateStamp
	 *            the date of the dump in format YYYYMMDD
	 * @return file name string
	 */
	public static String getDumpFileName(DumpContentType dumpContentType,
			String projectName, String dateStamp) {
		if (dumpContentType == DumpContentType.JSON) {
			return dateStamp + WmfDumpFile.getDumpFilePostfix(dumpContentType);
		} else {
			return projectName + "-" + dateStamp
					+ WmfDumpFile.getDumpFilePostfix(dumpContentType);
		}
	}

	/**
	 * Returns true if the given dump file type contains page revisions and
	 * false if it does not. Dumps that do not contain pages are for auxiliary
	 * information such as linked sites.
	 *
	 * @param dumpContentType
	 *            the type of dump
	 * @return true if the dumpfile contains revisions
	 * @throws IllegalArgumentException
	 *             if the given dump file type is not known
	 */
	public static boolean isRevisionDumpFile(DumpContentType dumpContentType) {
		if (WmfDumpFile.REVISION_DUMP.containsKey(dumpContentType)) {
			return WmfDumpFile.REVISION_DUMP.get(dumpContentType);
		} else {
			throw new IllegalArgumentException("Unsupported dump type "
					+ dumpContentType);
		}
	}

}
