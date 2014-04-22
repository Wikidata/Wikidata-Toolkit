package org.wikidata.wdtk.examples;

/*
 * #%L
 * Wikidata Toolkit Examples
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

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.MwDumpFile;
import org.wikidata.wdtk.dumpfiles.MwSitesDumpFileProcessor;
import org.wikidata.wdtk.dumpfiles.WmfDumpFileManager;
import org.wikidata.wdtk.util.DirectoryManager;
import org.wikidata.wdtk.util.DirectoryManagerImpl;
import org.wikidata.wdtk.util.WebResourceFetcher;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;

/**
 * This class demonstrates how to get access to information about sitelinks in
 * Wikidata.org. The data generally uses keys like "enwiki" to identify sites.
 * To find out what these keys mean, Wikidata Toolkit can be used to download
 * and process the dump of the MediaWiki sites table. The resulting
 * {@link Sites} object can be used to resolve links to other sites, and also
 * can be applied to {@link SiteLink} objects as found in the Wikidata data.
 * Other information obtained from the sites table includes the site language,
 * whether it is a MediaWiki site, and which group it has been assigned to. The
 * groups are used to define which sites can be used for entering site links in
 * Wikibase, but the sites table does not tell us which groups are currently
 * enabled for site links.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class SitelinksExample {

	public static void main(String[] args) throws IOException {

		// Define where log messages go
		configureLogging();

		// Print information about this program
		printDocumentation();

		// Create object to get hold of Wikidata.org dumpfiles
		WmfDumpFileManager dumpFileManager = createDumpFileManager();

		// Download the sites table dump and extract information
		Sites sites = getSitesInformation(dumpFileManager);

		// Access the data to find some information
		System.out
				.println("********************************************************************");
		System.out.println("*** Completed processing of sites table.");
		System.out.println("*** Examples:");
		System.out
				.println("*** URL of the page \"Dresden\" on German Wikipedia: "
						+ sites.getPageUrl("dewiki", "Dresden"));
		System.out
				.println("*** URL of the page \"ڈگلس ایڈم\" on Urdu Wikipedia: "
						+ sites.getPageUrl("urwiki", "ڈگلس ایڈم"));
		System.out
				.println("*** URL of the page \"Special:EntityData/Q1.json\" on Wikidata: "
						+ sites.getPageUrl("wikidatawiki",
								"Special:EntityData/Q1.json"));
		System.out
				.println("*** Main language of the site identified by \"frwikiquote\": "
						+ sites.getLanguageCode("frwikiquote"));
		System.out
				.println("*** Group of the site identified by \"zhwikivoyage\": "
						+ sites.getGroup("zhwikivoyage"));
		System.out
				.println("*** URL of the file \"api.php\" on English Wikipedia: "
						+ sites.getFileUrl("enwiki", "api.php"));
	}

	/**
	 * Creates an object that manages dumpfiles published by the Wikimedia
	 * Foundation. This object will check for available complete and incremental
	 * dump files, both online and in a local download directory. It provides
	 * direct access to the (decompressed) string content of these files.
	 * <p>
	 * The details in this method define which download directory is to be used,
	 * which Wikimedia project we are interested in (Wikidata), and that we want
	 * to allow online access (instead of using local files only).
	 * 
	 * @return dump file manager
	 * @throws IOException
	 *             if the download directory is not accessible
	 */
	private static WmfDumpFileManager createDumpFileManager()
			throws IOException {
		// The following can also be set to another directory:
		String downloadDirectory = System.getProperty("user.dir");
		DirectoryManager downloadDirectoryManager = new DirectoryManagerImpl(
				downloadDirectory);

		// The following can be set to null for offline operation:
		WebResourceFetcher webResourceFetcher = new WebResourceFetcherImpl();

		// The string "wikidatawiki" identifies Wikidata.org:
		return new WmfDumpFileManager("wikidatawiki", downloadDirectoryManager,
				webResourceFetcher);
	}

	/**
	 * Processes the most recent dump of the sites table to extract information
	 * about registered sites.
	 * 
	 * @param dumpFileManager
	 *            the dump file manager used to access the dump
	 * @return a Sites objects that contains the extracted information
	 * @throws IOException
	 */
	private static Sites getSitesInformation(WmfDumpFileManager dumpFileManager)
			throws IOException {
		// Get a handle for the most recent dump file of the sites table:
		MwDumpFile sitesTableDump = dumpFileManager
				.findMostRecentDump(DumpContentType.SITES);

		// Create a suitable processor for such dumps and process the file:
		MwSitesDumpFileProcessor sitesDumpFileProcessor = new MwSitesDumpFileProcessor();
		sitesDumpFileProcessor.processDumpFileContents(
				sitesTableDump.getDumpFileStream(), sitesTableDump);

		// Return the result:
		return sitesDumpFileProcessor.getSites();
	}

	/**
	 * Defines how messages should be logged. This method can be modified to
	 * restrict the logging messages that are shown on the console or to change
	 * their formatting. See the documentation of Log4J for details on how to do
	 * this.
	 */
	private static void configureLogging() {
		// Create the appender that will write log messages to the console.
		ConsoleAppender consoleAppender = new ConsoleAppender();
		// Define the pattern of log messages.
		// Insert the string "%c{1}:%L" to also show class name and line.
		String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
		consoleAppender.setLayout(new PatternLayout(pattern));
		// Change to Level.ERROR for fewer messages:
		consoleAppender.setThreshold(Level.INFO);

		consoleAppender.activateOptions();
		Logger.getRootLogger().addAppender(consoleAppender);
	}

	/**
	 * Print some basic documentation about this program.
	 */
	private static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: Sitelink Processing Example");
		System.out.println("*** ");
		System.out
				.println("*** This program will download and process site link information from");
		System.out
				.println("*** Wikidata. Downloaded files are stored on disk and are used until");
		System.out
				.println("*** newer dump are available. You can delete files manually when no");
		System.out
				.println("*** longer needed (see message below for the directory where files are found).");
		System.out
				.println("********************************************************************");
	}

}
