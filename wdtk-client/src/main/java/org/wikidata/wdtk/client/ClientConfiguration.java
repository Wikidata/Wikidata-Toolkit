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

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.MwLocalDumpFile;

/**
 * This class handles the program arguments from the conversion command line
 * tool.
 *
 * @author Michael Günther
 * @author Markus Kroetzsch
 */
public class ClientConfiguration {

	static final Logger logger = LoggerFactory
			.getLogger(ClientConfiguration.class);

	/**
	 * Short command-line alternative to {@link #OPTION_HELP}.
	 */
	public static final String CMD_OPTION_HELP = "h";
	/**
	 * Short command-line alternative to {@link #OPTION_CONFIG_FILE}.
	 */
	public static final String CMD_OPTION_CONFIG_FILE = "c";
	/**
	 * Short command-line alternative to {@link #OPTION_DUMP_LOCATION}.
	 */
	public static final String CMD_OPTION_DUMP_LOCATION = "d";
	/**
	 * Short command-line alternative to {@link #OPTION_QUIET}.
	 */
	public static final String CMD_OPTION_QUIET = "q";
	/**
	 * Short command-line alternative to {@link #OPTION_OFFLINE_MODE}.
	 */
	public static final String CMD_OPTION_OFFLINE_MODE = "n";
	/**
	 * Short command-line alternative to {@link #OPTION_ACTION}.
	 */
	public static final String CMD_OPTION_ACTION = "a";
	/**
	 * Short command-line alternative to {@link #OPTION_OUTPUT_DESTINATION}.
	 */
	public static final String CMD_OPTION_OUTPUT_DESTINATION = "o";
	/**
	 * Short command-line alternative to {@link #OPTION_OUTPUT_COMPRESSION}.
	 */
	public static final String CMD_OPTION_OUTPUT_COMPRESSION = "z";
	/**
	 * Short command-line alternative to {@link #OPTION_OUTPUT_STDOUT}.
	 */
	public static final String CMD_OPTION_OUTPUT_STDOUT = "s";
	/**
	 * Short command-line alternative to {@link #OPTION_CREATE_REPORT}.
	 */
	public static final String CMD_OPTION_CREATE_REPORT = "r";
	/**
	 * Short command-line alternative to {@link #OPTION_LOCAL_DUMPFILE}.
	 */
	public static final String CMD_OPTION_LOCAL_DUMPFILE = "i";

	/**
	 * Name of the long command line option for printing the help text.
	 */
	public static final String OPTION_HELP = "help";
	/**
	 * Name of the long command line option for specifying the location of the
	 * configuration file.
	 */
	public static final String OPTION_CONFIG_FILE = "config";
	/**
	 * Name of the long command line option and configuration file field for
	 * specifying the base location of the dump files to use.
	 */
	public static final String OPTION_DUMP_LOCATION = "dumps";
	/**
	 * Name of the long command line option and configuration file field for
	 * switching to offline mode.
	 */
	public static final String OPTION_OFFLINE_MODE = "offline";
	/**
	 * Name of the long command line option and configuration file field for
	 * requesting that no messages are logged to stdout.
	 */
	public static final String OPTION_QUIET = "quiet";
	/**
	 * Name of the long command line option for selecting an action that should
	 * be performed. Available actions are registered in the field
	 * {@link #KNOWN_ACTIONS}.
	 */
	public static final String OPTION_ACTION = "action";
	/**
	 * Name of the long command line option and configuration file field for
	 * specifying the language filters.
	 */
	public static final String OPTION_FILTER_LANGUAGES = "fLang";
	/**
	 * Name of the long command line option and configuration file field for
	 * specifying the site link filters.
	 */
	public static final String OPTION_FILTER_SITES = "fSite";
	/**
	 * Name of the long command line option and configuration file field for
	 * specifying the property filters.
	 */
	public static final String OPTION_FILTER_PROPERTIES = "fProp";
	/**
	 * Name of the long command line option to create a report file about the
	 * files produced by DumpProcessingOutputActions.
	 */
	public static final String OPTION_CREATE_REPORT = "report";
	/**
	 * Name of the long command line option and configuration file field for
	 * defining the destination (usually output file name) of actions that
	 * produce output.
	 */
	public static final String OPTION_OUTPUT_DESTINATION = DumpProcessingOutputAction.OPTION_DESTINATION;
	/**
	 * Name of the long command line option and configuration file field for
	 * specifying the compression to use for the output.
	 */
	public static final String OPTION_OUTPUT_COMPRESSION = DumpProcessingOutputAction.OPTION_COMPRESSION;
	/**
	 * Name of the long command line option and configuration file field for
	 * directing output to stdout.
	 */
	public static final String OPTION_OUTPUT_STDOUT = DumpProcessingOutputAction.OPTION_USE_STDOUT;
	/**
	 * Name of the long command line option and configuration file field for
	 * specifying the tasks for RDF serialization.
	 */
	public static final String OPTION_OUTPUT_RDF_TYPE = "rdftasks";
	/**
	 * Name of the long command line option and configuration file field for
	 * defining the path to a local dump file.
	 */
	public static final String OPTION_LOCAL_DUMPFILE = "input";

	static final Map<String, Class<? extends DumpProcessingAction>> KNOWN_ACTIONS = new HashMap<>();
	static {
		KNOWN_ACTIONS.put("rdf", RdfSerializationAction.class);
		KNOWN_ACTIONS.put("json", JsonSerializationAction.class);
		KNOWN_ACTIONS.put("sqid", SchemaUsageAnalyzer.class);
	}

	/**
	 * Available command-line options.
	 */
	static final Options options = new Options();
	static {
		initOptions();
	}

	/**
	 * List of actions that were configured to be performed.
	 */
	final List<DumpProcessingAction> actions;

	/**
	 * Global configuration that defines if the operations should avoid all
	 * internet access.
	 */
	boolean offlineMode = false;

	/**
	 * String representation of the directory where the dump files should be
	 * sought.
	 */
	String dumpDirectoryLocation = null;

	/**
	 * Location of a dump file that should be processed.
	 */
	String inputDumpLocation = null;

	/**
	 * String representation of the path where the final report should be
	 * stored.
	 */
	String reportFilename = null;

	/**
	 * True if no status messages should be written to stdout.
	 */
	boolean quiet = false;

	/**
	 * Set language codes to use as a filter, or null if no filter should be
	 * used.
	 */
	Set<String> filterLanguages = null;

	/**
	 * Set site keys to use as a filter, or null if no filter should be used.
	 */
	Set<String> filterSites = null;

	/**
	 * Set property ids to use as a filter, or null if no filter should be used.
	 */
	Set<PropertyIdValue> filterProperties = null;

	/**
	 * Date stamp of the dump to be processed.
	 */
	String dateStamp = "UNKNOWN";
	/**
	 * String name of the site that the processed dump file comes from.
	 */
	String project = "UNKNOWN";

	/**
	 * Constructs a new object for the given arguments.
	 *
	 * @param args
	 *            command-line arguments
	 */
	public ClientConfiguration(String[] args) {
		this.actions = handleArguments(args);
	}

	/**
	 * Inserts the information about the dateStamp of a dump and the project
	 * name into a pattern.
	 *
	 * @param pattern
	 *            String with wildcards
	 * @param dateStamp
	 * @param project
	 * @return String with injected information.
	 */
	public static String insertDumpInformation(String pattern,
			String dateStamp, String project) {
		if (pattern == null) {
			return null;
		} else {
			return pattern.replace("{DATE}", dateStamp).replace("{PROJECT}",
					project);
		}
	}

	/**
	 * Returns the list of actions defined for this object.
	 *
	 * @return list of actions
	 */
	public List<DumpProcessingAction> getActions() {
		return this.actions;
	}

	/**
	 * Returns the location of the directory where downloaded dump files are
	 * stored, or null if the default is to be used.
	 *
	 * @return string path to dump file directory
	 */
	public String getDumpDirectoryLocation() {
		return this.dumpDirectoryLocation;
	}

	/**
	 * Returns true if all operations should be performed in offline mode,
	 * without accessing the Internet.
	 *
	 * @return true if in offline mode
	 */
	public boolean getOfflineMode() {
		return this.offlineMode;
	}

	/**
	 * Returns true if the application should not write anything to stdout. This
	 * can be set explicitly or indirectly if one of the actions wants to write
	 * to stdout.
	 *
	 * @return true if the application should not log messages to stdout
	 */
	public boolean isQuiet() {
		return this.quiet;
	}

	/**
	 * Returns the output destination where a report file should be created. If
	 * the client should not create such a file the function will return null.
	 *
	 * @return report filename
	 */
	public String getReportFileName() {
		return this.insertDumpInformation(this.reportFilename);
	}

	/**
	 * Returns a set of language codes that should be used as a filter, or null
	 * if no filter is set. An empty set means that all languages should be
	 * filtered.
	 *
	 * @return language filter
	 */
	public Set<String> getFilterLanguages() {
		return this.filterLanguages;
	}

	/**
	 * Returns a set of site keys that should be used as a filter for site
	 * links, or null if no filter is set. An empty set means that all site
	 * links should be filtered.
	 *
	 * @return site key filter
	 */
	public Set<String> getFilterSiteKeys() {
		return this.filterSites;
	}

	/**
	 * Returns a set of property ids that should be used as a filter for
	 * statements, or null if no filter is set. An empty set means that all
	 * statements should be filtered.
	 *
	 * @return property filter
	 */
	public Set<PropertyIdValue> getFilterProperties() {
		return this.filterProperties;
	}

	/**
	 * Returns the string path to a dump file that should be processed.
	 *
	 * @return string path to file
	 */
	public String getInputDumpLocation() {
		return this.inputDumpLocation;
	}

	/**
	 * Returns a local dump file that was previously downloaded and should be
	 * locally processed, or null if no local dump file is set.
	 * <p>
	 * The dump file object will be created based on the current configuration.
	 *
	 * @return dump file that should be locally processed
	 */
	public MwLocalDumpFile getLocalDumpFile() {
		if (this.inputDumpLocation != null) {
			return new MwLocalDumpFile(this.inputDumpLocation);
		} else {
			return null;
		}
	}

	/**
	 * Sets the project name of the dump file.
	 *
	 * @param project
	 */
	public void setProjectName(String project) {
		this.project = project;
	}

	/**
	 * Sets the date stamp of the dump file.
	 *
	 * @param dateStamp
	 */
	public void setDateStamp(String dateStamp) {
		this.dateStamp = dateStamp;
	}

	/**
	 * Returns the project name according to the dump file.
	 *
	 * @return project name
	 */
	public String getProjectName() {
		return this.project;
	}

	/**
	 * Returns the date stamp of the dump file.
	 *
	 * @return date stamp
	 */
	public String getDateStamp() {
		return this.dateStamp;
	}

	/**
	 * Prints a help text to the console.
	 */
	public void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("wdtk-client", options);
	}

	public String insertDumpInformation(String pattern) {
		return ClientConfiguration.insertDumpInformation(pattern,
				this.dateStamp, this.project);
	}

	/**
	 * This function interprets the arguments of the main function. By doing
	 * this it will set flags for the dump generation. See in the help text for
	 * more specific information about the options.
	 *
	 * @param args
	 *            array of arguments from the main function.
	 * @return list of {@link DumpProcessingOutputAction}
	 */
	private List<DumpProcessingAction> handleArguments(String[] args) {
		CommandLine cmd;
		CommandLineParser parser = new GnuParser();

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			logger.error("Failed to parse arguments: " + e.getMessage());
			return Collections.emptyList();
		}

		// Stop processing if a help text is to be printed:
		if ((cmd.hasOption(CMD_OPTION_HELP)) || (args.length == 0)) {
			return Collections.emptyList();
		}

		List<DumpProcessingAction> configuration = new ArrayList<>();

		handleGlobalArguments(cmd);

		if (cmd.hasOption(CMD_OPTION_ACTION)) {
			DumpProcessingAction action = handleActionArguments(cmd);
			if (action != null) {
				configuration.add(action);
			}
		}

		if (cmd.hasOption(CMD_OPTION_CONFIG_FILE)) {
			try {
				List<DumpProcessingAction> configFile = readConfigFile(cmd
						.getOptionValue(CMD_OPTION_CONFIG_FILE));
				configuration.addAll(configFile);
			} catch (IOException e) {
				logger.error("Failed to read configuration file \""
						+ cmd.getOptionValue(CMD_OPTION_CONFIG_FILE) + "\": "
						+ e.toString());
			}

		}

		return configuration;

	}

	/**
	 *
	 * Reads the properties defined in a configuration file. Returns a set of
	 * configuration property blocks stored in {@link DumpProcessingAction}
	 * objects and call
	 * {@link ClientConfiguration#handleGlobalArguments(CommandLine)} to
	 * interpreted the properties from the general section. The first element of
	 * the list contains general information (about all dumps).
	 *
	 * @param path
	 *            filename and path of the configuration file
	 * @return the list with configurations for all output dumps
	 * @throws IOException
	 */
	private List<DumpProcessingAction> readConfigFile(String path)
			throws IOException {
		List<DumpProcessingAction> result = new ArrayList<>();

		try(FileReader reader = new FileReader(path)) {
			Ini ini = new Ini(reader);

			for (Section section : ini.values()) {
				if (section.getName().toLowerCase().equals("general")) {
					handleGlobalArguments(section);
				} else {
					DumpProcessingAction action = handleActionArguments(section);
					if (action != null) {
						action.setActionName(section.getName());
						result.add(action);
					}
				}
			}
			return result;
		}
	}

	/**
	 * Analyses the command-line arguments which are relevant for the
	 * serialization process in general. It fills out the class arguments with
	 * this data.
	 *
	 * @param cmd
	 *            {@link CommandLine} objects; contains the command line
	 *            arguments parsed by a {@link CommandLineParser}
	 */
	private void handleGlobalArguments(CommandLine cmd) {
		if (cmd.hasOption(CMD_OPTION_DUMP_LOCATION)) {
			this.dumpDirectoryLocation = cmd
					.getOptionValue(CMD_OPTION_DUMP_LOCATION);
		}

		if (cmd.hasOption(CMD_OPTION_OFFLINE_MODE)) {
			this.offlineMode = true;
		}

		if (cmd.hasOption(CMD_OPTION_QUIET)) {
			this.quiet = true;
		}

		if (cmd.hasOption(CMD_OPTION_CREATE_REPORT)) {
			this.reportFilename = cmd.getOptionValue(CMD_OPTION_CREATE_REPORT);
		}

		if (cmd.hasOption(OPTION_FILTER_LANGUAGES)) {
			setLanguageFilters(cmd.getOptionValue(OPTION_FILTER_LANGUAGES));
		}

		if (cmd.hasOption(OPTION_FILTER_SITES)) {
			setSiteFilters(cmd.getOptionValue(OPTION_FILTER_SITES));
		}

		if (cmd.hasOption(OPTION_FILTER_PROPERTIES)) {
			setPropertyFilters(cmd.getOptionValue(OPTION_FILTER_PROPERTIES));
		}

		if (cmd.hasOption(CMD_OPTION_LOCAL_DUMPFILE)) {
			this.inputDumpLocation = cmd.getOptionValue(OPTION_LOCAL_DUMPFILE);
		}
	}

	/**
	 * Analyses the content of the general section of an ini configuration file
	 * and fills out the class arguments with this data.
	 *
	 * @param section
	 *            {@link Section} with name "general"
	 */
	private void handleGlobalArguments(Section section) {
		for (String key : section.keySet()) {
			switch (key) {
			case OPTION_OFFLINE_MODE:
				if (section.get(key).toLowerCase().equals("true")) {
					this.offlineMode = true;
				}
				break;
			case OPTION_QUIET:
				if (section.get(key).toLowerCase().equals("true")) {
					this.quiet = true;
				}
				break;
			case OPTION_CREATE_REPORT:
				this.reportFilename = section.get(key);
				break;
			case OPTION_DUMP_LOCATION:
				this.dumpDirectoryLocation = section.get(key);
				break;
			case OPTION_FILTER_LANGUAGES:
				setLanguageFilters(section.get(key));
				break;
			case OPTION_FILTER_SITES:
				setSiteFilters(section.get(key));
				break;
			case OPTION_FILTER_PROPERTIES:
				setPropertyFilters(section.get(key));
				break;
			case OPTION_LOCAL_DUMPFILE:
				this.inputDumpLocation = section.get(key);
				break;
			default:
				logger.warn("Unrecognized option: " + key);
			}
		}
	}

	/**
	 * Analyses the command-line arguments which are relevant for the specific
	 * action that is to be executed, and returns a corresponding
	 * {@link DumpProcessingAction} object.
	 *
	 * @param cmd
	 *            {@link CommandLine} objects; contains the command line
	 *            arguments parsed by a {@link CommandLineParser}
	 * @return {@link DumpProcessingAction} for the given arguments
	 */
	private DumpProcessingAction handleActionArguments(CommandLine cmd) {

		DumpProcessingAction result = makeDumpProcessingAction(cmd
				.getOptionValue(CMD_OPTION_ACTION).toLowerCase());
		if (result == null) {
			return null;
		}

		for (Option option : cmd.getOptions()) {
			result.setOption(option.getLongOpt(), option.getValue());
		}

		checkDuplicateStdOutOutput(result);

		return result;
	}

	/**
	 * Analyses the content of a section of an ini configuration file (not the
	 * "general" section) and fills out the class arguments of an new
	 * {@link DumpProcessingAction} with this data.
	 *
	 * @param section
	 *            {@link Section} with name "general"
	 * @return {@link DumpProcessingAction} containing the output parameters
	 */
	private DumpProcessingAction handleActionArguments(Section section) {
		DumpProcessingAction result = makeDumpProcessingAction(section.get(
				OPTION_ACTION).toLowerCase());
		if (result == null) {
			return null;
		}

		for (String key : section.keySet()) {
			if (!result.setOption(key.toLowerCase(), section.get(key))
					&& !OPTION_ACTION.equals(key.toLowerCase())) {
				logger.warn("Unrecognized option: " + key);
			}
		}

		checkDuplicateStdOutOutput(result);

		return result;
	}

	/**
	 * Checks if a newly created action wants to write output to stdout, and
	 * logs a warning if other actions are doing the same.
	 *
	 * @param newAction
	 *            the new action to be checked
	 */
	private void checkDuplicateStdOutOutput(DumpProcessingAction newAction) {
		if (newAction.useStdOut()) {
			if (this.quiet) {
				logger.warn("Multiple actions are using stdout as output destination.");
			}
			this.quiet = true;
		}
	}

	/**
	 * Creates a {@link DumpProcessingAction} object for the action of the given
	 * name. The operation may fail if the name is not associated with any
	 * action, or if the associated action class cannot be instantiated.
	 *
	 * @param name
	 *            of the action
	 * @return the {@link DumpProcessingAction} or null if creation failed
	 */
	private DumpProcessingAction makeDumpProcessingAction(String name) {
		if (!KNOWN_ACTIONS.containsKey(name)) {
			logger.error("Unknown action \"" + name + "\".");
			return null;
		}

		try {
			Constructor<? extends DumpProcessingAction> constructor = KNOWN_ACTIONS
					.get(name).getConstructor();
			return constructor.newInstance();
		} catch (NoSuchMethodException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			logger.error("Class \"" + KNOWN_ACTIONS.get(name)
					+ "\" that was registered to handle action \"" + name
					+ "\" does not have an appropriate constructor.");
			return null;
		} catch (InstantiationException | InvocationTargetException e) {
			logger.error("Error when trying to instantiate handler for action \""
					+ name + "\":" + e.getMessage());
			return null;
		}

	}

	/**
	 * Sets the set of language filters based on the given string.
	 *
	 * @param filters
	 *            comma-separates list of language codes, or "-" to filter all
	 *            languages
	 */
	private void setLanguageFilters(String filters) {
		this.filterLanguages = new HashSet<>();
		if (!"-".equals(filters)) {
			Collections.addAll(this.filterLanguages, filters.split(","));
		}
	}

	/**
	 * Sets the set of site filters based on the given string.
	 *
	 * @param filters
	 *            comma-separates list of site keys, or "-" to filter all site
	 *            links
	 */
	private void setSiteFilters(String filters) {
		this.filterSites = new HashSet<>();
		if (!"-".equals(filters)) {
			Collections.addAll(this.filterSites, filters.split(","));
		}
	}

	/**
	 * Sets the set of property filters based on the given string.
	 *
	 * @param filters
	 *            comma-separates list of property ids, or "-" to filter all
	 *            statements
	 */
	private void setPropertyFilters(String filters) {
		this.filterProperties = new HashSet<>();
		if (!"-".equals(filters)) {
			for (String pid : filters.split(",")) {
				this.filterProperties.add(Datamodel
						.makeWikidataPropertyIdValue(pid));
			}
		}
	}

	/**
	 * Builds a list of legal options and store them into the options objects.
	 */
	@SuppressWarnings("static-access")
	private static void initOptions() {

		List<String> actions = new ArrayList<>(KNOWN_ACTIONS.keySet());
		Collections.sort(actions);
		Option action = OptionBuilder
				.hasArg()
				.withArgName("action")
				.withDescription(
						"define the action that should be performed; avaible actions: "
								+ actions).withLongOpt(OPTION_ACTION)
				.create(CMD_OPTION_ACTION);

		Option destination = OptionBuilder.withArgName("path").hasArg()
				.withDescription("place the output into the file at <path>")
				.withLongOpt(OPTION_OUTPUT_DESTINATION)
				.create(CMD_OPTION_OUTPUT_DESTINATION);

		Option dumplocation = OptionBuilder
				.hasArg()
				.withArgName("path")
				.withDescription(
						"set the directory where downloaded dump files are stored")
				.withLongOpt(OPTION_DUMP_LOCATION)
				.create(CMD_OPTION_DUMP_LOCATION);

		Option config = OptionBuilder
				.hasArg()
				.withArgName("file")
				.withDescription(
						"set a config file; use this to define multiple actions for a single run")
				.withLongOpt(OPTION_CONFIG_FILE).create(CMD_OPTION_CONFIG_FILE);

		Option rdfdump = OptionBuilder
				.hasArgs()
				.withArgName("task")
				.withDescription(
						"specify which data to include in RDF dump (use with action \"rdf\"); run with options \"-a rdf -n\" for help")
				.withLongOpt(OPTION_OUTPUT_RDF_TYPE).create();

		Option filterLanguages = OptionBuilder
				.hasArgs()
				.withArgName("languages")
				.withDescription(
						"specifies a list of language codes; if given, only terms in languages in this list will be processed; the value \"-\" denotes the empty list (no terms are processed)")
				.withLongOpt(OPTION_FILTER_LANGUAGES).create();

		Option filterSites = OptionBuilder
				.hasArgs()
				.withArgName("sites")
				.withDescription(
						"specifies a list of site keys; if given, only site links to sites in this list will be processed; the value \"-\" denotes the empty list (no site links are processed)")
				.withLongOpt(OPTION_FILTER_SITES).create();

		Option filterProperties = OptionBuilder
				.hasArgs()
				.withArgName("ids")
				.withDescription(
						"specifies a list of property ids; if given, only statements for properties in this list will be processed; the value \"-\" denotes the empty list (no statements are processed)")
				.withLongOpt(OPTION_FILTER_PROPERTIES).create();

		Option compressionExtention = OptionBuilder
				.hasArg()
				.withArgName("type")
				.withDescription(
						"define a compression format to be used for the output; possible values: "
								+ DumpProcessingOutputAction.COMPRESS_GZIP
								+ ", "
								+ DumpProcessingOutputAction.COMPRESS_BZ2)
				.withLongOpt(OPTION_OUTPUT_COMPRESSION)
				.create(CMD_OPTION_OUTPUT_COMPRESSION);

		Option report = OptionBuilder
				.hasArg()
				.withArgName("path")
				.withDescription(
						"specifies a path to print a final report after dump generations")
				.withLongOpt(OPTION_CREATE_REPORT)
				.create(CMD_OPTION_CREATE_REPORT);

		Option localDump = OptionBuilder
				.hasArg()
				.withArgName("path")
				.withDescription(
						"select a dump file for processing; if omitted, then the latest dump from Wikidata will be used (and possibly downloaded)")
				.withLongOpt(OPTION_LOCAL_DUMPFILE)
				.create(CMD_OPTION_LOCAL_DUMPFILE);

		options.addOption(config);
		options.addOption(action);
		options.addOption(
				CMD_OPTION_QUIET,
				OPTION_QUIET,
				false,
				"perform all actions quietly, without printing status messages to the console; errors/warnings are still printed to stderr");
		options.addOption(destination);
		options.addOption(dumplocation);
		options.addOption(filterLanguages);
		options.addOption(filterSites);
		options.addOption(filterProperties);
		options.addOption(compressionExtention);
		options.addOption(report);
		options.addOption(localDump);
		options.addOption(rdfdump);
		options.addOption(CMD_OPTION_OFFLINE_MODE, OPTION_OFFLINE_MODE, false,
				"execute all operations in offline mode, especially do not download new dumps");
		options.addOption(CMD_OPTION_HELP, OPTION_HELP, false,
				"print this message");

		options.addOption(CMD_OPTION_OUTPUT_STDOUT, OPTION_OUTPUT_STDOUT,
				false, "write output to stdout");
	}

}
