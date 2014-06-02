package org.wikidata.wdtk.dumpfiles;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
	static final String DUMP_SITE_BASE_URL = "http://dumps.wikimedia.org/";

	/**
	 * Hash map defining the relative Web directory of each type of dump.
	 */
	static final Map<DumpContentType, String> WEB_DIRECTORY = new HashMap<DumpContentType, String>();
	static {
		WmfDumpFile.WEB_DIRECTORY.put(DumpContentType.DAILY, "other/incr/");
		WmfDumpFile.WEB_DIRECTORY.put(DumpContentType.CURRENT, "");
		WmfDumpFile.WEB_DIRECTORY.put(DumpContentType.FULL, "");
		WmfDumpFile.WEB_DIRECTORY.put(DumpContentType.SITES, "");
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
	}

	/**
	 * Hash map defining the compression type of each type of dump.
	 */
	static final Map<DumpContentType, CompressionType> COMPRESSION_TYPE = new HashMap<DumpContentType, CompressionType>();
	static {
		WmfDumpFile.COMPRESSION_TYPE.put(DumpContentType.DAILY,
				CompressionType.BZ2);
		WmfDumpFile.COMPRESSION_TYPE.put(DumpContentType.CURRENT,
				CompressionType.BZ2);
		WmfDumpFile.COMPRESSION_TYPE.put(DumpContentType.FULL,
				CompressionType.BZ2);
		WmfDumpFile.COMPRESSION_TYPE.put(DumpContentType.SITES,
				CompressionType.GZIP);
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
	}

	/**
	 * The name of the file where a dump's maximal revision id should be stored
	 * locally.
	 */
	static final String LOCAL_FILENAME_MAXREVID = "maxrevid.txt";

	final String dateStamp;
	final String projectName;
	Long maxRevId;
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
	public Long getMaximalRevisionId() {
		if (this.maxRevId == null) {
			this.maxRevId = fetchMaximalRevisionId();
		}
		return this.maxRevId;
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
	 * Returns the maximal revision id for this dump.
	 * 
	 * @return maximal revision id or -1 if it was not found
	 */
	protected abstract Long fetchMaximalRevisionId();

	/**
	 * Finds out if the dump is ready.
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
	 * Returns the relative directory on the Web site where dumpfiles of the
	 * given type can be found.
	 * 
	 * @param dumpContentType
	 *            the type of dump
	 * @return relative web directory for the current dumpfiles
	 * @throws IllegalArgumentException
	 *             if the given dump file type is not known
	 */
	public static String getDumpFileWebDirectory(DumpContentType dumpContentType) {
		if (WmfDumpFile.WEB_DIRECTORY.containsKey(dumpContentType)) {
			return WmfDumpFile.WEB_DIRECTORY.get(dumpContentType);
		} else {
			throw new IllegalArgumentException("Unsupported dump type "
					+ dumpContentType);
		}
	}

	/**
	 * Returns the compression type of this kind of dump file.
	 * 
	 * @param dumpContentType
	 *            the type of dump
	 * @return compression type
	 * @throws IllegalArgumentException
	 *             if the given dump file type is not known
	 */
	public static CompressionType getDumpFileCompressionType(
			DumpContentType dumpContentType) {
		if (WmfDumpFile.COMPRESSION_TYPE.containsKey(dumpContentType)) {
			return WmfDumpFile.COMPRESSION_TYPE.get(dumpContentType);
		} else {
			throw new IllegalArgumentException("Unsupported dump type "
					+ dumpContentType);
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
	 *            the project name, e.g., wikidatawiki
	 * @param dateStamp
	 *            the date of the dump in format YYYYMMDD
	 * @return file name string
	 */
	public static String getDumpFileName(DumpContentType dumpContentType,
			String projectName, String dateStamp) {
		return projectName + "-" + dateStamp
				+ WmfDumpFile.getDumpFilePostfix(dumpContentType);
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