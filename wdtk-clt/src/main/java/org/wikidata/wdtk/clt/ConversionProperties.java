package org.wikidata.wdtk.clt;

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

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the program arguments from the conversion command line
 * tool.
 * 
 * @author Michael Günther
 * 
 */
public class ConversionProperties {

	static final Logger logger = LoggerFactory
			.getLogger(ConversionProperties.class);

	final static Options options = new Options();
	static {
		initOptions();
	}

	// some global configuration parameters
	Boolean offlineMode = false;
	String dumplocation = null;

	Boolean useStdOut = false;

	/**
	 * Constructor.
	 */
	public ConversionProperties() {
	}

	/**
	 * Builds up a list of legal options and store them into the options
	 * objects.
	 */
	@SuppressWarnings("static-access")
	private static void initOptions() {
		Option format = OptionBuilder
				.withArgName("format")
				.hasArg()
				.withDescription(
						"data format of the dump (\"json\" or \"rdf\")")
				.withLongOpt("format").create("f");
		Option destination = OptionBuilder
				.withArgName("path")
				.hasArg()
				.withDescription(
						"place the output into the directory located at <path>")
				.withLongOpt("destination").create("d");
		Option dumplocation = OptionBuilder.withArgName("path").hasArg()
				.withDescription("define the location of the dumpfiles")
				.withLongOpt("dumplocation").create("l");
		Option config = OptionBuilder
				.withArgName("file")
				.hasArg()
				.withDescription(
						"set a config file <file> for more complex conversion tasks")
				.withLongOpt("config").create("c");
		Option rdfdump = OptionBuilder.hasArgs()
				.withDescription("specifies the kind of RDF dump")
				.withLongOpt("rdfdump").create("r");
		Option compressionExtention = OptionBuilder
				.withArgName("compression extension")
				.hasArg()
				.withDescription(
						"define a compression format. Supported formats: .bz2, .gz")
				.withLongOpt("compression").create("e");

		options.addOption(config);
		options.addOption(format);
		options.addOption(destination);
		options.addOption(dumplocation);
		options.addOption(compressionExtention);
		options.addOption(rdfdump);
		options.addOption(
				"n",
				"offline",
				false,
				"execute conversion in offline mode - converter should use only previously downloaded dumps");
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
	 * @return list of {@link OutputConfiguration}
	 * @throws ParseException
	 * @throws IOException
	 */
	public List<OutputConfiguration> handleArguments(String[] args)
			throws ParseException, IOException {
		List<OutputConfiguration> configuration = new ArrayList<OutputConfiguration>();
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = parser.parse(options, args);

		// print help text
		if ((cmd.hasOption("h")) || (args.length == 0)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ConversionClient", options);
		}

		handleGeneralArguments(cmd);

		// check if there is some kind of output specified
		if (cmd.hasOption("f")) {
			configuration.add(handleOutputArguments(cmd));
		}

		if (cmd.hasOption("c")) {
			List<OutputConfiguration> configFile = readConfigFile(cmd
					.getOptionValue("c"));
			configuration.addAll(configFile);
		}

		return configuration;

	}

	/**
	 * 
	 * Reads the properties defined in a configuration file. Returns a set of
	 * configuration property blocks stored in {@link OutputConfiguration}
	 * objects and call
	 * {@link ConversionProperties#handleGeneralArguments(CommandLine)} to
	 * interpreted the properties from the general section. The first element of
	 * the list contains general information (about all dumps).
	 * 
	 * @param path
	 *            filename and path of the configuration file
	 * @return the list with configurations for all output dumps
	 * @throws IOException
	 */
	public List<OutputConfiguration> readConfigFile(String path)
			throws IOException {
		List<OutputConfiguration> result = new ArrayList<OutputConfiguration>();

		FileReader reader = new FileReader(path);
		Ini ini = new Ini(reader);

		for (Section section : ini.values()) {
			if (section.getName().toLowerCase().equals("general")) {
				handleGeneralArguments(section);
			} else {
				result.add(handleOutputArguments(section));
			}
		}
		return result;
	}

	public String getDumplocation() {
		return dumplocation;
	}

	public void setDumplocation(String dumplocation) {
		this.dumplocation = dumplocation;
	}

	public Boolean getOfflineMode() {
		return offlineMode;
	}

	public void setOfflineMode(Boolean offlineMode) {
		this.offlineMode = offlineMode;
	}

	/**
	 * Analyses the content of the general section of an ini configuration file
	 * and fills out the class arguments with this data.
	 * 
	 * @param section
	 *            {@link Section} with name "general"
	 */
	private void handleGeneralArguments(Section section) {
		for (String key : section.keySet()) {
			switch (key.toLowerCase()) {
			case "offline":
				if (section.get(key).toLowerCase().equals("true")) {
					this.offlineMode = true;
				}
				break;
			case "dumplocation":
				this.dumplocation = section.get(key);
				break;
			default:
				logger.warn("Unrecognized option: " + key);
			}
		}
	}

	/**
	 * Analyses the comandline arguments which are relevant for the
	 * serialization process in general. It fills out the class arguments with
	 * this data.
	 * 
	 * @param cmd
	 *            {@link CommandLine} objects; contains the command line
	 *            arguments parsed by a {@link CommandLineParser}
	 */
	private void handleGeneralArguments(CommandLine cmd) {
		if (cmd.hasOption("l")) {
			this.dumplocation = cmd.getOptionValue("l");
		}

		if (cmd.hasOption("n")) {
			this.offlineMode = true;
		}
	}

	/**
	 * Analyses the arguments content of the general section of an ini
	 * configuration file and fills out the class arguments of an new
	 * {@link OutputConfiguration} with this data.
	 * 
	 * @param cmd
	 *            {@link CommandLine} objects; contains the command line
	 *            arguments parsed by a {@link CommandLineParser}
	 * @return {@link OutputConfiguration} containing the output parameters
	 */
	private OutputConfiguration handleOutputArguments(CommandLine cmd) {

		OutputConfiguration result;
		switch (cmd.getOptionValue("f").toLowerCase()) {
		case "rdf":
			RdfConfiguration rdfConfiguration = new RdfConfiguration(this);
			if (cmd.hasOption("r")) {
				rdfConfiguration.setRdfdump(cmd.getOptionValue("r"));
			} else {
				logger.warn("No kind of rdf-dump set!");
			}
			result = rdfConfiguration;
			break;
		case "json":
			result = new JsonConfiguration(this);
			break;
		default:
			logger.warn("No output format specified!");
			return null;
		}

		if (cmd.hasOption("s")) {
			if (useStdOut == true) {
				logger.warn("Multiple serializer using stdout as output destination!");
			}
			result.setUseStdout(true);
			useStdOut = true;
		}

		if (cmd.hasOption("d")) {
			result.outputDestination = cmd.getOptionValue("d");
		}

		if (cmd.hasOption("e")) {
			result.compressionExtension = cmd.getOptionValue("e");
		}

		return result;
	}

	/**
	 * Analyses the content of a section of an ini configuration file (not the
	 * "general" section) and fills out the class arguments of an new
	 * {@link OutputConfiguration} with this data.
	 * 
	 * @param section
	 *            {@link Section} with name "general"
	 * @return {@link OutputConfiguration} containing the output parameters
	 */
	private OutputConfiguration handleOutputArguments(Section section) {
		OutputConfiguration result;
		switch (section.get("format").toLowerCase()) {
		case "rdf":
			RdfConfiguration rdfConfiguration = new RdfConfiguration(this);
			rdfConfiguration.setRdfdump(section.get("rdfdump").toLowerCase());
			result = rdfConfiguration;
			break;
		case "json":
			result = new JsonConfiguration(this);
			break;
		default:
			logger.warn("Unrecognized format: "
					+ section.get("format").toLowerCase());
			return null;
		}

		for (String key : section.keySet()) {
			switch (key.toLowerCase()) {
			case "stdout":
				if (section.get(key).toLowerCase().equals("true")) {
					if (useStdOut == true) {
						logger.warn("Multiple serializer using stdout as output destination!");
					}
					result.setUseStdout(true);
					this.useStdOut = true;
				}
				section.get(key);
				break;
			case "destination":
				result.setOutputDestination(section.get(key));
				break;
			case "rdfdump":
				// do nothing (see above)
				break;
			case "compression":
				result.setCompressionExtension(section.get(key));
				break;
			default:
				logger.warn("Unrecognized option: " + key);
			}
		}
		return result;
	}

}
