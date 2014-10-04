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

import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;

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
		ExampleHelpers.configureLogging();

		// Print information about this program
		printDocumentation();

		// Controller object for processing dumps:
		DumpProcessingController dumpProcessingController = new DumpProcessingController(
				"wikidatawiki");
		dumpProcessingController.setOfflineMode(ExampleHelpers.OFFLINE_MODE);

		// Download the sites table dump and extract information
		Sites sites = dumpProcessingController.getSitesInformation();

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
