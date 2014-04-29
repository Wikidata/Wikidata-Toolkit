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

import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;
import org.wikidata.wdtk.dumpfiles.StatisticsMwRevisionProcessor;

/**
 * This class demonstrates how to write an application that downloads and
 * processes dumpfiles from Wikidata.org.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class DumpProcessingExample {

	public static void main(String[] args) throws IOException {

		// Define where log messages go
		ExampleHelpers.configureLogging();

		// Print information about this program
		printDocumentation();

		// Controller object for processing dumps:
		DumpProcessingController dumpProcessingController = new DumpProcessingController(
				"wikidatawiki");
		// // Work offline (using only previously fetched dumps):
		// dumpProcessingController.setOfflineMode(true);
		// // Use any download directory:
		// dumpProcessingController.setDownloadDirectory(System.getProperty("user.dir"));

		// Our local example class ItemStatisticsProcessor counts the number of
		// labels etc. in Wikibase item documents to print out some statistics:
		EntityDocumentProcessor edpItemStats = new ItemStatisticsProcessor();
		// Subscribe to the most recent entity documents of type wikibase item:
		dumpProcessingController.registerEntityDocumentProcessor(edpItemStats,
				MwRevision.MODEL_WIKIBASE_ITEM, true);

		// General statistics and time keeping:
		MwRevisionProcessor rpRevisionStats = new StatisticsMwRevisionProcessor(
				"revision processing statistics", 10000);
		// Subscribe to all current revisions (null = no filter):
		dumpProcessingController.registerMwRevisionProcessor(rpRevisionStats,
				null, true);

		// Start processing (may trigger downloads where needed)
		dumpProcessingController.processAllRecentRevisionDumps();

		// // Process just a recent daily dump for testing:
		// dumpProcessingController.processMostRecentDailyDump();
	}

	/**
	 * Print some basic documentation about this program.
	 */
	private static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: Dump Processing Example");
		System.out.println("*** ");
		System.out
				.println("*** This program will download and process dumps from Wikidata.");
		System.out
				.println("*** It will print progress information and some simple statistics.");
		System.out
				.println("*** Downloading may take some time initially. After that, files");
		System.out
				.println("*** are stored on disk and are used until newer dumps are available.");
		System.out
				.println("*** You can delete files manually when no longer needed (see ");
		System.out
				.println("*** message below for the directory where files are found).");
		System.out
				.println("********************************************************************");
	}

	/**
	 * A simple example class that processes EntityDocuments to compute basic
	 * statistics that are printed to the standard output. This could be
	 * replaced with any other class that processes entity documents in some
	 * way.
	 * 
	 * @author Markus Kroetzsch
	 * 
	 */
	static class ItemStatisticsProcessor implements EntityDocumentProcessor {

		long countItems = 0;
		long countLabels = 0;
		long countDescriptions = 0;
		long countAliases = 0;
		long countStatements = 0;
		long countSiteLinks = 0;

		@Override
		public void processItemDocument(ItemDocument itemDocument) {
			this.countItems++;
			this.countLabels += itemDocument.getLabels().size();
			this.countDescriptions += itemDocument.getDescriptions().size();
			for (String languageKey : itemDocument.getAliases().keySet()) {
				this.countAliases += itemDocument.getAliases().get(languageKey)
						.size();
			}
			for (StatementGroup sg : itemDocument.getStatementGroups()) {
				this.countStatements += sg.getStatements().size();
			}
			this.countSiteLinks += itemDocument.getSiteLinks().size();

			// print a report every 10000 items:
			if (this.countItems % 10000 == 0) {
				printReport();
			}
		}

		@Override
		public void processPropertyDocument(PropertyDocument propertyDocument) {
			// ignore properties
			// (in fact, the above code does not even register the processor for
			// receiving properties)
		}

		@Override
		public void finishProcessingEntityDocuments() {
			printReport(); // print a final report
		}

		/**
		 * Prints a report about the statistics gathered so far.
		 */
		private void printReport() {
			System.out.println("Processed " + this.countItems + " items:");
			System.out.println(" * Labels: " + this.countLabels);
			System.out.println(" * Descriptions: " + this.countDescriptions);
			System.out.println(" * Aliases: " + this.countAliases);
			System.out.println(" * Statements: " + this.countStatements);
			System.out.println(" * Site links: " + this.countSiteLinks);
		}

	}
}
