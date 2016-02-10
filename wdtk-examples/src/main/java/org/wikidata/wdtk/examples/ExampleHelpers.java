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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor;
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor.TimeoutException;
import org.wikidata.wdtk.dumpfiles.MwDumpFile;

/**
 * Class for sharing code that is used in many examples. It contains several
 * static final members that can be modified to change the behaviour of example
 * programs, such as whether to use {@link ExampleHelpers#OFFLINE_MODE} or not.
 *
 * @author Markus Kroetzsch
 *
 */
public class ExampleHelpers {

	/**
	 * If set to true, all example programs will run in offline mode. Only data
	 * dumps that have been downloaded in previous runs will be used.
	 */
	public static final boolean OFFLINE_MODE = false;

	/**
	 * Enum to say which dumps should be downloaded and processed. Used as
	 * possible values of {@link ExampleHelpers#DUMP_FILE_MODE}.
	 */
	public enum DumpProcessingMode {
		JSON, CURRENT_REVS, ALL_REVS, CURRENT_REVS_WITH_DAILIES, ALL_REVS_WITH_DAILIES, JUST_ONE_DAILY_FOR_TEST
	}

	/**
	 * Defines which dumps will be downloaded and processed in all examples.
	 */
	public static final DumpProcessingMode DUMP_FILE_MODE = DumpProcessingMode.JSON;

	/**
	 * The directory where to place files created by the example applications.
	 */
	public static final String EXAMPLE_OUTPUT_DIRECTORY = "results";

	/**
	 * Timeout to abort processing after a short while or 0 to disable timeout.
	 * If set, then the processing will cleanly exit after about this many
	 * seconds, as if the dump file would have ended there. This is useful for
	 * testing (and in particular better than just aborting the program) since
	 * it allows for final processing and proper closing to happen without
	 * having to wait for a whole dump file to process.
	 */
	public static final int TIMEOUT_SEC = 0;

	/**
	 * Identifier of the dump file that was processed last. This can be used to
	 * name files generated while processing a dump file.
	 */
	private static String lastDumpFileName = "";

	/**
	 * Defines how messages should be logged. This method can be modified to
	 * restrict the logging messages that are shown on the console or to change
	 * their formatting. See the documentation of Log4J for details on how to do
	 * this.
	 */
	public static void configureLogging() {
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
	 * Processes all entities in a Wikidata dump using the given entity
	 * processor. By default, the most recent JSON dump will be used. In offline
	 * mode, only the most recent previously downloaded file is considered.
	 *
	 * @param entityDocumentProcessor
	 *            the object to use for processing entities in this dump
	 */
	public static void processEntitiesFromWikidataDump(
			EntityDocumentProcessor entityDocumentProcessor) {

		// Controller object for processing dumps:
		DumpProcessingController dumpProcessingController = new DumpProcessingController(
				"wikidatawiki");
		dumpProcessingController.setOfflineMode(OFFLINE_MODE);

		// // Optional: Use another download directory:
		// dumpProcessingController.setDownloadDirectory(System.getProperty("user.dir"));

		// Should we process historic revisions or only current ones?
		boolean onlyCurrentRevisions;
		switch (DUMP_FILE_MODE) {
		case ALL_REVS:
		case ALL_REVS_WITH_DAILIES:
			onlyCurrentRevisions = false;
			break;
		case CURRENT_REVS:
		case CURRENT_REVS_WITH_DAILIES:
		case JSON:
		case JUST_ONE_DAILY_FOR_TEST:
		default:
			onlyCurrentRevisions = true;
		}

		// Subscribe to the most recent entity documents of type wikibase item:
		dumpProcessingController.registerEntityDocumentProcessor(
				entityDocumentProcessor, null, onlyCurrentRevisions);

		// Also add a timer that reports some basic progress information:
		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(
				TIMEOUT_SEC);
		dumpProcessingController.registerEntityDocumentProcessor(
				entityTimerProcessor, null, onlyCurrentRevisions);

		MwDumpFile dumpFile = null;
		try {
			// Start processing (may trigger downloads where needed):
			switch (DUMP_FILE_MODE) {
			case ALL_REVS:
			case CURRENT_REVS:
				dumpFile = dumpProcessingController
						.getMostRecentDump(DumpContentType.FULL);
				break;
			case ALL_REVS_WITH_DAILIES:
			case CURRENT_REVS_WITH_DAILIES:
				MwDumpFile fullDumpFile = dumpProcessingController
						.getMostRecentDump(DumpContentType.FULL);
				MwDumpFile incrDumpFile = dumpProcessingController
						.getMostRecentDump(DumpContentType.DAILY);
				lastDumpFileName = fullDumpFile.getProjectName() + "-"
						+ incrDumpFile.getDateStamp() + "."
						+ fullDumpFile.getDateStamp();
				dumpProcessingController.processAllRecentRevisionDumps();
				break;
			case JSON:
				dumpFile = dumpProcessingController
						.getMostRecentDump(DumpContentType.JSON);
				break;
			case JUST_ONE_DAILY_FOR_TEST:
				dumpFile = dumpProcessingController
						.getMostRecentDump(DumpContentType.DAILY);
				break;
			default:
				throw new RuntimeException("Unsupported dump processing type "
						+ DUMP_FILE_MODE);
			}

			if (dumpFile != null) {
				lastDumpFileName = dumpFile.getProjectName() + "-"
						+ dumpFile.getDateStamp();
				dumpProcessingController.processDump(dumpFile);
			}
		} catch (TimeoutException e) {
			// The timer caused a time out. Continue and finish normally.
		}

		// Print final timer results:
		entityTimerProcessor.close();
	}

	/**
	 * Opens a new FileOutputStream for a file of the given name in the example
	 * output directory ({@link ExampleHelpers#EXAMPLE_OUTPUT_DIRECTORY}). Any
	 * file of this name that exists already will be replaced. The caller is
	 * responsible for eventually closing the stream.
	 *
	 * @param filename
	 *            the name of the file to write to
	 * @return FileOutputStream for the file
	 * @throws IOException
	 *             if the file or example output directory could not be created
	 */
	public static FileOutputStream openExampleFileOuputStream(String filename)
			throws IOException {
		Path directoryPath;
		if ("".equals(lastDumpFileName)) {
			directoryPath = Paths.get(EXAMPLE_OUTPUT_DIRECTORY);
		} else {
			directoryPath = Paths.get(EXAMPLE_OUTPUT_DIRECTORY);
			createDirectory(directoryPath);
			directoryPath = directoryPath.resolve(
					lastDumpFileName);
		}

		createDirectory(directoryPath);
		Path filePath = directoryPath.resolve(filename);
		return new FileOutputStream(filePath.toFile());
	}

	/**
	 * Returns the name of the dump file that was last processed. This can be
	 * used to name files generated from this dump. The result might be the
	 * empty string if no file has been processed yet.
	 */
	public static String getLastDumpFileName() {
		return lastDumpFileName;
	}

	/**
	 * Create a directory at the given path if it does not exist yet.
	 *
	 * @param path
	 *            the path to the directory
	 * @throws IOException
	 *             if it was not possible to create a directory at the given
	 *             path
	 */
	private static void createDirectory(Path path) throws IOException {
		try {
			Files.createDirectory(path);
		} catch (FileAlreadyExistsException e) {
			if (!Files.isDirectory(path)) {
				throw e;
			}
		}
	}
}
