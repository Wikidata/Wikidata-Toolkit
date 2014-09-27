package org.wikidata.wdtk.clt;

import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;
import org.wikidata.wdtk.dumpfiles.StatisticsMwRevisionProcessor;

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
 * This class provides a Java command line client to generate dumps in various
 * data formats, such as JSON and RDF.
 * 
 * @author Michael Günther
 * 
 */
public class ConversionClient {

	final static String COMPRESS_BZ2 = ".bz2";
	final static String COMPRESS_GZIP = ".gz";
	final static String COMPRESS_NONE = "";

	private static Sites sites;
	private static DumpProcessingController dumpProcessingController;

	final ConversionProperties conversionProperties;
	final List<OutputConfiguration> configurations;

	/**
	 * True if any of the serializers want to put its output to stdout to
	 * prevent logging things to stdout.
	 */
	boolean useStdoutForOutput = false;

	/**
	 * True if any conversion format was specified.
	 */
	boolean convertSomething = false;

	static final Logger logger = LoggerFactory
			.getLogger(ConversionClient.class);

	/**
	 * Constructor.
	 * 
	 * @param args
	 *            command line arguments to configure the conversion
	 * @throws ParseException
	 * @throws IOException
	 */
	public ConversionClient(String args[]) throws ParseException, IOException {
		this.conversionProperties = new ConversionProperties();
		this.configurations = conversionProperties.handleArguments(args);

		// set flags (stdout and convertAnything)
		// System.out.println(this.configurations.get(0).getUseStdout());
		for (OutputConfiguration configuration : this.configurations) {
			if (configuration.getUseStdout()) {
				this.useStdoutForOutput = true;
			}
			if (configuration.getOutputFormat() != "none") {
				this.convertSomething = true;
			}
		}

	}

	/**
	 * Manages the serialization process. Therefore a
	 * {@link DumpProcessingController} and a serializer for the chosen output
	 * formats will be set up. After that the serialization process will be
	 * initiated.
	 * 
	 * @throws IOException
	 */
	public void convert() {

		if (!useStdoutForOutput) {
			// Define where log messages go
			configureLogging();
		}

		// Controller object for processing dumps:
		dumpProcessingController = new DumpProcessingController("wikidatawiki");

		// Initialize sites; needed to link to Wikipedia pages in RDF
		try {
			sites = dumpProcessingController.getSitesInformation();
		} catch (IOException e) {
			logger.error("Failed to get sites Information");
			return;
		}

		if (this.conversionProperties.getOfflineMode()) {
			dumpProcessingController.setOfflineMode(true);
		}

		for (OutputConfiguration props : configurations) {
			try {
				props.setupSerializer(dumpProcessingController, sites);
			} catch (IOException e) {
				logger.error("Could not setup " + props.getOutputFormat()
						+ " serializer");
			}
		}

		if (!useStdoutForOutput) {
			// General statistics and time keeping:
			MwRevisionProcessor rpRevisionStats = new StatisticsMwRevisionProcessor(
					"revision processing statistics", 10000);

			// Subscribe to all current revisions (null = no filter):
			dumpProcessingController.registerMwRevisionProcessor(
					rpRevisionStats, null, true);
		}

		if (this.conversionProperties.getDumplocation() != null) {
			try {
				dumpProcessingController
						.setDownloadDirectory(this.conversionProperties
								.getDumplocation());
			} catch (IOException e) {
				logger.error("Could not set download directory to "
						+ this.conversionProperties.getDumplocation());
				return;
			}
		}

		startSerializers();
		dumpProcessingController.processMostRecentMainDump();
		closeSerializers();
	}

	public boolean convertSomething() {
		return this.convertSomething;
	}

	public boolean useStdoutForOutput() {
		return this.useStdoutForOutput;
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
		org.apache.log4j.Logger.getRootLogger().addAppender(consoleAppender);
	}

	/**
	 * Starts the serializers. This includes the writing of headers if any (N3
	 * has no headers, but other formats have).
	 */
	private void startSerializers() {
		for (OutputConfiguration configuration : this.configurations) {
			configuration.startSerializer();
		}
	}

	/**
	 * Closes the serializers (and their output streams), and prints a short
	 * summary of the number of triples serialized by each.
	 */
	private void closeSerializers() {
		for (OutputConfiguration configuration : this.configurations) {
			configuration.closeSerializer();
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
		ConversionClient client = new ConversionClient(args);

		if (client.convertSomething()) {
			client.convert();
		}

	}
}
