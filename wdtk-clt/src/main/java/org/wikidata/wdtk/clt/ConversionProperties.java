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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
 * This class handles the program arguments from the conversion comandline tool.
 * 
 * @author Michael Günther
 * 
 */
public class ConversionProperties {

	Boolean configFile = false;
	Ini properties;
	Options options;
	String[] args;

	static final Logger logger = LoggerFactory
			.getLogger(ConversionProperties.class);

	public ConversionProperties(String[] args) {
		initOptions();
		this.args = args;
	}

	public Options getOptions() {
		return options;
	}

	/**
	 * Builds up a list of legal options and store them into the options
	 * objects.
	 */
	public void initOptions() {
		this.options = new Options();
		Option format = OptionBuilder.withArgName("file").hasArg()
				.withDescription("data format of the dump")
				.withLongOpt("format").create("f");
		Option destination = OptionBuilder
				.withArgName("path")
				.hasArg()
				.withDescription(
						"place the output into the directory located at <path>")
				.withLongOpt("destination").create("d");
		Option dumplocation = OptionBuilder.withArgName("path").hasArg()
				.withDescription("defines the location of the dumpfiles")
				.withLongOpt("dumplocation").create("l");
		Option config = OptionBuilder
				.withArgName("file")
				.hasArg()
				.withDescription(
						"defines config file for complexer conversation tasks")
				.withLongOpt("config").create("c");
		Option rdfdump = OptionBuilder.hasArgs()
				.withDescription("specifies the kind of rdf dump")
				.withLongOpt("rdfdump").create("r");
		Option compressionExtention = OptionBuilder
				.withArgName("compression extention")
				.hasArg()
				.withDescription(
						"Defines a compression format. Supported fromats: .bz2, .gz")
				.withLongOpt("compression").create("e");

		options.addOption(config);
		options.addOption(format);
		options.addOption(destination);
		options.addOption(dumplocation);
		options.addOption(
				"n",
				"offline",
				false,
				"execute conversion in offline mode - converter should use only previousely downloaded dumps");
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
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public List<ConversionConfiguration> handleArguments(String[] args)
			throws ParseException, IOException {
		List<ConversionConfiguration> configuration = new ArrayList<ConversionConfiguration>();
		ConversionConfiguration firstConfiguration = new ConversionConfiguration();
		configuration.add(firstConfiguration);
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = parser.parse(getOptions(), args);

		if (cmd.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ConversionClient", getOptions());
		}

		if (cmd.hasOption("s")) {
			firstConfiguration.setStdout(true);
		}

		if (cmd.hasOption("d")) {
			firstConfiguration.setOutputDestination(cmd.getOptionValue("d"));
		}

		if (cmd.hasOption("f")) {
			firstConfiguration.setOutputFormat(cmd.getOptionValue("f"));
		} else {
			logger.warn("No output format specified!");
		}

		if (cmd.hasOption("l")) {
			firstConfiguration.setDumplocation(cmd.getOptionValue("l"));
		}

		if (cmd.hasOption("n")) {
			firstConfiguration.setOfflineMode(true);
		}

		if (cmd.hasOption("e")) {
			firstConfiguration.setCompressionExtension(cmd.getOptionValue("e"));
		}

		if (cmd.hasOption("c")) {
			configuration.addAll(readOutConfigFile(cmd.getOptionValue("c")));
		}

		return configuration;

	}

	/**
	 * Returns a set of configuration property blocks stored in
	 * {@link ConversionConfiguration} objects.
	 * 
	 * @param path
	 *            filename and path of the configuration file
	 * @return
	 * @throws IOException
	 */
	public Set<ConversionConfiguration> readOutConfigFile(String path)
			throws IOException {
		Set<ConversionConfiguration> result = new HashSet<ConversionConfiguration>();
		FileReader reader = new FileReader(path);
		configFile = true;
		properties = new Ini(reader);
		for (Section section : properties.values()) {
			ConversionConfiguration configurationSection = new ConversionConfiguration();
			for (String key : section.keySet()) {
				if (key.toLowerCase().equals("offline")) {
					if (section.get(key).toLowerCase().equals("true")) {
						configurationSection.setOfflineMode(true);
					}
				}
				if (key.toLowerCase().equals("stdout")) {
					if (section.get(key).toLowerCase().equals("true")) {
						configurationSection.setStdout(true);
					}
					section.get(key);
				}
				if (key.toLowerCase().equals("format")) {
					configurationSection.setOutputFormat(section.get(key));
				} else {
					logger.warn("No output format specified in this section!");
				}
				if (key.toLowerCase().equals("destination")) {
					configurationSection.setOutputDestination(section.get(key));
				}
				if (key.toLowerCase().equals("dumplocation")) {
					configurationSection.setDumplocation(section.get(key));
				}
				if (key.toLowerCase().equals("rdfdump")) {
					configurationSection.setRdfdump(section.get(key));
				}
				if (key.toLowerCase().equals("compression")) {
					configurationSection.setCompressionExtension(section
							.get(key));
				}

			}
			result.add(configurationSection);
		}
		return result;
	}

	public List<ConversionConfiguration> getProperties() throws ParseException,
			IOException {
		return handleArguments(args);
	}
}
