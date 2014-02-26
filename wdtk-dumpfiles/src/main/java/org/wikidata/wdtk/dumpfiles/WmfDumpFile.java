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

/**
 * Abstract base class for dump files provided by the Wikimedia Foundation.
 * 
 * @author Markus Kroetzsch
 * 
 */
public abstract class WmfDumpFile implements MediaWikiDumpFile {

	/**
	 * The default URL of the website to obtain the dump files from.
	 */
	static final String DUMP_SITE_BASE_URL = "http://dumps.wikimedia.org/";

	static final String POSTFIX_DAILY_DUMP_FILE = "-pages-meta-hist-incr.xml.bz2";
	static final String POSTFIX_CURRENT_DUMP_FILE = "-pages-meta-current.xml.bz2";
	static final String POSTFIX_FULL_DUMP_FILE = "-pages-meta-history.xml.bz2";

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

	/**
	 * Get the maximal revision id for this dump.
	 * 
	 * @return maximal revision id or -1 if it was not found
	 */
	protected abstract Long fetchMaximalRevisionId();

	/**
	 * Find out if the dump is ready.
	 * 
	 * @return true if the dump is done
	 */
	protected abstract boolean fetchIsDone();

	/**
	 * Return the ending used by the Wikimedia-provided dumpfile names of the
	 * given type.
	 * 
	 * @param dumpContentType
	 *            the type of dump
	 * @return postfix of the dumpfile name
	 * @throws IllegalArgumentException
	 *             if the given dump file type is not known
	 */
	public static String getDumpFilePostfix(
			MediaWikiDumpFile.DumpContentType dumpContentType) {
		switch (dumpContentType) {
		case DAILY:
			return WmfDumpFile.POSTFIX_DAILY_DUMP_FILE;
		case CURRENT:
			return WmfDumpFile.POSTFIX_CURRENT_DUMP_FILE;
		case FULL:
			return WmfDumpFile.POSTFIX_FULL_DUMP_FILE;
		default:
			throw new IllegalArgumentException("Unsupported dump type");
		}
	}

}