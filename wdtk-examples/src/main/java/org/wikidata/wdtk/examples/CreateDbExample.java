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

import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;
import org.wikidata.wdtk.dumpfiles.StatisticsMwRevisionProcessor;
import org.wikidata.wdtk.storage.wdtkbindings.DumpImportEntityDocumentProcessor;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkDatabaseManager;

/**
 * This example utility program processes the most recent Wikidata dump to
 * create a database file in a more efficient binary format that can be used in
 * other WDTK programs.
 *
 * @author Markus Kroetzsch
 */
public class CreateDbExample {

	/**
	 * The name of your database file. Change as appropriate.
	 */
	final static String dbName = "wdtkDatabase";
	final static WdtkDatabaseManager wdtkDatabaseManager = new WdtkDatabaseManager(
			dbName);

	/**
	 * If true, then only previously downloaded files are considered.
	 */
	final static boolean offlineMode = false;

	public static void main(String[] args) throws IOException {

		// Define where log messages go
		ExampleHelpers.configureLogging();

		// Print information about this program
		printDocumentation();

		// Controller object for processing dumps:
		DumpProcessingController dumpProcessingController = new DumpProcessingController(
				"wikidatawiki");
		dumpProcessingController.setOfflineMode(offlineMode);

		DumpImportEntityDocumentProcessor edpDumpImport = new DumpImportEntityDocumentProcessor(
				wdtkDatabaseManager);
		dumpProcessingController.registerEntityDocumentProcessor(edpDumpImport,
				MwRevision.MODEL_WIKIBASE_ITEM, true);
		dumpProcessingController.registerEntityDocumentProcessor(edpDumpImport,
				MwRevision.MODEL_WIKIBASE_PROPERTY, true);

		// General statistics and time keeping:
		MwRevisionProcessor rpRevisionStats = new StatisticsMwRevisionProcessor(
				"revision processing statistics", 10000);
		// Subscribe to all current revisions (null = no filter):
		dumpProcessingController.registerMwRevisionProcessor(rpRevisionStats,
				null, true);

		// Start processing (may trigger downloads where needed):

		// Process just the most recent main dump:
		dumpProcessingController.processMostRecentMainDump();
		// Process all recent dumps (including daily dumps as far as available)
		// dumpProcessingController.processAllRecentRevisionDumps();

		// Close the database properly:
		edpDumpImport.close();
	}

	/**
	 * Print some basic documentation about this program.
	 */
	private static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: Create WDTK Database");
		System.out.println("*** ");
		System.out
				.println("*** This program will download and process dumps from Wikidata");
		System.out
				.println("*** to import the data into a local binary database file for faster processing.");
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

}
