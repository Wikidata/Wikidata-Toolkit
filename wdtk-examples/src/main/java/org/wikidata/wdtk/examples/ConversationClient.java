package org.wikidata.wdtk.examples;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * This class provides a java comand line client to generate dumps in various
 * data formats like json and rdf.
 * 
 * @author Michael Günther
 * 
 */
public class ConversationClient {

	Options options;

	String outputFormat;
	String outputDestination;
	Boolean stdout = false;

	static final Logger logger = LoggerFactory
			.getLogger(ConversationClient.class);

	public Options getOptions() {
		return options;
	}

	public ConversationClient() {
		// create Options object
		options = new Options();

	}

	/**
	 * Builds up a list of legal options for apache commons cli
	 */
	public void addOptions() {
		Option format = OptionBuilder.withArgName("file").hasArg()
				.withDescription("data format of the dump")
				.withLongOpt("format").create("f");
		Option destination = OptionBuilder
				.withArgName("path")
				.hasArg()
				.withDescription(
						"place the output into the directory located at <path>")
				.withLongOpt("destination").create("d");

		options.addOption(format);
		options.addOption(destination);
		options.addOption("h", "help", false, "print this message");

		options.addOption("s", "stdout", false, "write output to stdout");
	}

	/**
	 * This function interprets the arguments of the main function. By doing
	 * this it will set flags for the dump generation. See in the help text for
	 * more specific information about the options.
	 * 
	 * @param args
	 *            array of arguments from the main function.
	 * @throws ParseException
	 */
	public void handleArguments(String[] args) throws ParseException {
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = parser.parse(getOptions(), args);

		if (cmd.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ConversationClient", getOptions());
		}

		if (cmd.hasOption("s")) {
			stdout = true;
		}

		if (cmd.hasOption("d")) {
			outputDestination = cmd.getOptionValue("d");
		}

		if (cmd.hasOption("f")) {
			outputFormat = cmd.getOptionValue("f");
		} else {
			logger.warn("No output format specified!");
		}
	}

	public static void main(String[] args) throws ParseException {
		ConversationClient client = new ConversationClient();
		client.addOptions();
		client.handleArguments(args);
	}
}
