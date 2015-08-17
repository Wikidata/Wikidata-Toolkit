package org.wikidata.wdtk.examples;

/*
 * #%L
 * Wikidata Toolkit Examples
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;

/**
 * This class illustrates how to process local dumpfiles. It uses
 * {@link EntityTimerProcessor} to process a dump.
 *
 * @author Markus Damm
 *
 */
public class LocalDumpFileExample {

	/**
	 * Path to the dump that should be processed
	 */
	private final static String DUMP_FILE = "./src/resources/sample-dump-20150815.json.gz";

	public static void main(String[] args) throws IOException {
		ExampleHelpers.configureLogging();
		LocalDumpFileExample.printDocumentation();

		DumpProcessingController dumpProcessingController = new DumpProcessingController(
				"wikidatawiki");
		// Note that the project name "wikidatawiki" is only for online access;
		// not relevant here.

		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(
				entityTimerProcessor, null, true);

		// Select local file (meta-data will be guessed):
		System.out.println();
		System.out
				.println("Processing a local dump file giving only its location");
		System.out
				.println("(meta-data like the date is guessed from the file name):");
		MwLocalDumpFile mwDumpFile = new MwLocalDumpFile(DUMP_FILE);
		dumpProcessingController.processDump(mwDumpFile);

		// Select local file and set meta-data:
		System.out.println();
		System.out
				.println("Processing a local dump file with all meta-data set:");
		mwDumpFile = new MwLocalDumpFile(DUMP_FILE, DumpContentType.JSON,
				"20150815", "wikidatawiki");
		dumpProcessingController.processDump(mwDumpFile);

		entityTimerProcessor.close();
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: LocalDumpFileExample");
		System.out.println("*** ");
		System.out
				.println("*** This program illustrates how to process local dumps.");
		System.out
				.println("*** It uses an EntityTimerProcesses which counts processed items");
		System.out.println("*** and elapsed time.");
		System.out.println("*** ");
		System.out.println("*** See source code for further details.");
		System.out
				.println("********************************************************************");
	}
}
