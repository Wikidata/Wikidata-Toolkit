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
 * Class for representing dump files that contain all revisions of each page, as
 * published by the Wikimedia Foundation. The dump file and additional
 * information about its status is online and web access is needed to fetch this
 * data on demand.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class WmfOnlineFullDumpFile extends WmfOnlineStandardDumpFile {

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
	public WmfOnlineFullDumpFile(String dateStamp, String projectName,
			WebResourceFetcher webResourceFetcher,
			DirectoryManager dumpfileDirectoryManager) {
		super(dateStamp, projectName, webResourceFetcher,
				dumpfileDirectoryManager,
				MediaWikiDumpFile.DumpContentType.FULL,
				WmfDumpFile.POSTFIX_FULL_DUMP_FILE);
	}

}
