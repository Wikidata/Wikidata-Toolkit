package org.wikidata.wdtk.client;

/*
 * #%L
 * Wikidata Toolkit Command-line Tool
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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.varia.LevelRangeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.dumpfiles.DumpContentType;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.EntityTimerProcessor;
import org.wikidata.wdtk.dumpfiles.MwDumpFile;
import org.wikidata.wdtk.util.DirectoryManager;
import org.wikidata.wdtk.util.DirectoryManagerFactory;

/**
 * This class provides a Java command line client to process dump files.
 *
 * @author Michael Günther
 * @author Markus Kroetzsch
 */
public class Client {

	static final Logger logger = LoggerFactory.getLogger(Client.class);

	/**
	 * The pattern to use in log messages. One could insert the string
	 * "%c{1}:%L" to also show class name and line.
	 */
	static final String LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
	static ConsoleAppender errorAppender = null;
	static ConsoleAppender consoleAppender = null;

	private Sites sites = null;

	final DumpProcessingController dumpProcessingController;

	protected final ClientConfiguration clientConfiguration;

	/**
	 * Constructor.
	 *
	 * @param args
	 *            command line arguments to configure the conversion
	 */
	public Client(DumpProcessingController dumpProcessingController,
			String args[]) {
		initializeLogging();
		this.dumpProcessingController = dumpProcessingController;
		this.clientConfiguration = new ClientConfiguration(args);

		if (this.clientConfiguration.isQuiet()) {
			consoleAppender.setThreshold(Level.OFF);
		} else {
			consoleAppender.setThreshold(Level.INFO);
		}

	}

	/**
	 * Performs all actions that have been configured.
	 */
	public void performActions() {
		if (this.clientConfiguration.getActions().isEmpty()) {
			this.clientConfiguration.printHelp();
			return;
		}

		this.dumpProcessingController.setOfflineMode(this.clientConfiguration
				.getOfflineMode());

		if (this.clientConfiguration.getDumpDirectoryLocation() != null) {
			try {
				this.dumpProcessingController
						.setDownloadDirectory(this.clientConfiguration
								.getDumpDirectoryLocation());
			} catch (IOException e) {
				logger.error("Could not set download directory to "
						+ this.clientConfiguration.getDumpDirectoryLocation()
						+ ": " + e.getMessage());
				logger.error("Aborting");
				return;
			}
		}

		dumpProcessingController.setLanguageFilter(this.clientConfiguration
				.getFilterLanguages());
		dumpProcessingController.setSiteLinkFilter(this.clientConfiguration
				.getFilterSiteKeys());
		dumpProcessingController.setPropertyFilter(this.clientConfiguration
				.getFilterProperties());

		MwDumpFile dumpFile = this.clientConfiguration.getLocalDumpFile();

		if (dumpFile == null) {
			dumpFile = dumpProcessingController
					.getMostRecentDump(DumpContentType.JSON);
		} else {
			if (!dumpFile.isAvailable()) {
				logger.error("Dump file not found or not readable: "
						+ dumpFile.toString());
				return;
			}
		}

		this.clientConfiguration.setProjectName(dumpFile.getProjectName());
		this.clientConfiguration.setDateStamp(dumpFile.getDateStamp());

		boolean hasReadyProcessor = false;
		for (DumpProcessingAction props : this.clientConfiguration.getActions()) {

			if (!props.isReady()) {
				continue;
			}

			if (props.needsSites()) {
				prepareSites();
				if (this.sites == null) { // sites unavailable
					continue;
				}
				props.setSites(this.sites);
			}
			props.setDumpInformation(dumpFile.getProjectName(),
					dumpFile.getDateStamp());
			this.dumpProcessingController.registerEntityDocumentProcessor(
					props, null, true);
			hasReadyProcessor = true;
		}

		if (!hasReadyProcessor) {
			return; // silent; non-ready action should report its problem
					// directly
		}

		if (!this.clientConfiguration.isQuiet()) {
			EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(
					0);
			this.dumpProcessingController.registerEntityDocumentProcessor(
					entityTimerProcessor, null, true);
		}
		openActions();
		this.dumpProcessingController.processDump(dumpFile);
		closeActions();

		try {
			writeReport();
		} catch (IOException e) {
			logger.error("Could not print report file: " + e.getMessage());
		}

	}

	private void prepareSites() {
		if (this.sites == null) {
			try {
				sites = this.dumpProcessingController.getSitesInformation();
			} catch (IOException e) {
				logger.error("Failed to get sites information: "
						+ e.getMessage());
			}
		}
	}

	/**
	 * Sets up Log4J to write log messages to the console. Low-priority messages
	 * are logged to stdout while high-priority messages go to stderr.
	 */
	private void initializeLogging() {
		// Since logging is static, make sure this is done only once even if
		// multiple clients are created (e.g., during tests)
		if (consoleAppender != null) {
			return;
		}

		consoleAppender = new ConsoleAppender();
		consoleAppender.setLayout(new PatternLayout(LOG_PATTERN));
		consoleAppender.setThreshold(Level.INFO);
		LevelRangeFilter filter = new LevelRangeFilter();
		filter.setLevelMin(Level.TRACE);
		filter.setLevelMax(Level.INFO);
		consoleAppender.addFilter(filter);
		consoleAppender.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(consoleAppender);

		errorAppender = new ConsoleAppender();
		errorAppender.setLayout(new PatternLayout(LOG_PATTERN));
		errorAppender.setThreshold(Level.WARN);
		errorAppender.setTarget(ConsoleAppender.SYSTEM_ERR);
		errorAppender.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(errorAppender);
	}

	/**
	 * Starts the work of each configured action by calling its
	 * {@link DumpProcessingOutputAction#open()} method.
	 */
	private void openActions() {
		for (DumpProcessingAction action : this.clientConfiguration
				.getActions()) {
			action.open();
		}
	}

	/**
	 * Writes a report file including the results of the
	 * {@link DumpProcessingAction#getReport()} methods. If there is no report
	 * filename specified the reports will be logged.
	 *
	 * @throws IOException
	 */
	void writeReport() throws IOException {
		StringBuilder builder = new StringBuilder();
		for (DumpProcessingAction action : this.clientConfiguration
				.getActions()) {
			if (this.clientConfiguration.getReportFileName() != null) {
				builder.append(action.getActionName());
				builder.append(": ");
				if (action.isReady()) {
					builder.append(action.getReport());
				} else {
					builder.append("Action was not executed.");
				}
				builder.append(System.getProperty("line.separator"));
			} else {
				logger.info(action.getActionName() + ": " + action.getReport());
			}
		}
		if (this.clientConfiguration.getReportFileName() != null) {
			Path outputDirectory = Paths.get(
					this.clientConfiguration.getReportFileName()).getParent();
			if (outputDirectory == null) {
				outputDirectory = Paths.get(".");
			}
			DirectoryManager dm = DirectoryManagerFactory
					.createDirectoryManager(outputDirectory, false);
			OutputStream out = dm.getOutputStreamForFile(Paths
					.get(this.clientConfiguration.getReportFileName())
					.getFileName().toString());
			out.write(builder.toString().getBytes(StandardCharsets.UTF_8));
			out.close();
		}
	}

	/**
	 * Finishes the work of each configured action by calling its
	 * {@link DumpProcessingOutputAction#close()} method.
	 */
	private void closeActions() {
		for (DumpProcessingAction action : this.clientConfiguration
				.getActions()) {
			action.close();
		}

	}

	/**
	 * Launches the client with the specified parameters.
	 *
	 * @param args
	 *            command line parameters
	 * @throws ParseException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ParseException, IOException {
		Client client = new Client(
				new DumpProcessingController("wikidatawiki"), args);
		client.performActions();
	}
}
