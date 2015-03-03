package org.wikidata.wdtk.client;

import java.io.IOException;

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
	 * @throws ParseException
	 * @throws IOException
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

		if (this.clientConfiguration.getDumpLocation() != null) {
			try {
				this.dumpProcessingController
						.setDownloadDirectory(this.clientConfiguration
								.getDumpLocation());
			} catch (IOException e) {
				logger.error("Could not set download directory to "
						+ this.clientConfiguration.getDumpLocation() + ": "
						+ e.getMessage());
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

		MwDumpFile dumpFile = dumpProcessingController
				.getMostRecentDump(DumpContentType.JSON);

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
	}

	private void prepareSites() {
		if (this.sites == null) {
			try {
				sites = this.dumpProcessingController.getSitesInformation();
			} catch (IOException e) {
				logger.error("Failed to get sites information.");
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
