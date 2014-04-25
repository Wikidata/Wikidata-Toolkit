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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.json.JsonSerializer;
import org.wikidata.wdtk.dumpfiles.MwDumpFileProcessor;
import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionDumpFileProcessor;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessorBroker;
import org.wikidata.wdtk.dumpfiles.StatisticsMwRevisionProcessor;
import org.wikidata.wdtk.dumpfiles.WikibaseRevisionProcessor;
import org.wikidata.wdtk.dumpfiles.WmfDumpFileManager;
import org.wikidata.wdtk.util.DirectoryManager;
import org.wikidata.wdtk.util.DirectoryManagerImpl;
import org.wikidata.wdtk.util.WebResourceFetcher;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;

/**
 * This class shows how convert data from wikidata.org to JSON, which follows
 * the JSON format as used in the Wikibase API. The compressed JSON data will be
 * written into a file named WikidataDump.json.bz2. You can find it in the
 * example directory after you ran the example code.
 * 
 * @author Michael Günther
 * 
 */
public class JsonSerializationExample {

	public static void main(String[] args) throws IOException {

		// Define where log messages go
		configureLogging();

		// Print information about this program
		printDocumentation();

		// Create object to get hold of Wikidata.org dumpfiles
		WmfDumpFileManager dumpFileManager = createDumpFileManager();

		// Write the output to a BZip2-compressed file
		BZip2CompressorOutputStream outputStream = new BZip2CompressorOutputStream(
				new FileOutputStream("WikidataDump.json.bz2"));

		// Create an object for managing the serialization process
		JsonSerializer jsonSerializer = new JsonSerializer(outputStream);

		// Set up processing pipeline with the JSON serializer
		MwDumpFileProcessor dumpFileProcessor = createDumpFileProcessor(jsonSerializer);

		// Set up the JSON serializer and write headers
		jsonSerializer.startSerialization();

		// Start processing (may trigger downloads where needed)
		dumpFileManager.processAllRecentRevisionDumps(dumpFileProcessor, true);

		// Finish the JSON serialization close the file stream
		jsonSerializer.finishSerialization();
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
	 * Create an object that handles the complete processing of MediaWiki
	 * dumpfiles. This processing consists of the following main steps:
	 * 
	 * <pre>
	 * XML dump file -> page revisions -> item documents -> json
	 * </pre>
	 * 
	 * The objects handling each step are of type {@link MwDumpFileProcessor},
	 * {@link MwRevisionProcessor}, and {@link JsonSerializer}. In each case,
	 * the object on the left calls the object on the right whenever new data is
	 * available. Therefore, the object on the right must be known to the object
	 * on the left, so we set up the objects in reverse order.
	 * 
	 * @param jsonSerializer
	 *            entity document processor that writes the Json serialization
	 * @return dump file processor
	 * @throws FileNotFoundException
	 */
	private static MwDumpFileProcessor createDumpFileProcessor(
			EntityDocumentProcessor jsonSerializer)
			throws FileNotFoundException {

		// Revision processor for extracting entity documents from revisions:
		// the documents are send to our serializer which generate the json
		// representation
		MwRevisionProcessor rpEntityJsonSerializer = new WikibaseRevisionProcessor(
				jsonSerializer);

		// Revision processor for general statistics and time keeping:
		MwRevisionProcessor rpRevisionStats = new StatisticsMwRevisionProcessor(
				"revision processing statistics", 10000);

		// Broker to distribute revisions to multiple subscribers:
		MwRevisionProcessorBroker rpBroker = new MwRevisionProcessorBroker();
		// Subscribe to the most recent revisions of type wikibase item:
		rpBroker.registerMwRevisionProcessor(rpEntityJsonSerializer,
				MwRevision.MODEL_WIKIBASE_ITEM, true);
		// Subscribe to all current revisions (null = no filter):
		rpBroker.registerMwRevisionProcessor(rpRevisionStats, null, true);

		// Object to parse XML dumps to send page revisions to our broker:
		return new MwRevisionDumpFileProcessor(rpBroker);
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
		System.out.println("*** Wikidata Toolkit: Json Serialization Example");
		System.out.println("*** ");
		System.out
				.println("*** This program will download dumps from Wikidata and serialize the data in a json format.");
		System.out.println("*** It will print progress json.");
		System.out
				.println("*** Downloading may take some time initially. After that, files");
		System.out
				.println("*** are stored on disk and are used until newer dumps are available.");
		System.out
				.println("*** You can delete files manually when no longer needed (see ");
		System.out
				.println("*** message below for the directory where dump files are found).");
		System.out
				.println("*** The json output will be stored in the directory of the example.");
		System.out
				.println("********************************************************************");
	}
}
