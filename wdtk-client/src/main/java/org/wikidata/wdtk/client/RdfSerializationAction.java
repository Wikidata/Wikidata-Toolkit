package org.wikidata.wdtk.client;

/*
 * #%L
 * Wikidata Toolkit Command-line Tool
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.rdf.PropertyRegister;
import org.wikidata.wdtk.rdf.RdfSerializer;

/**
 * This class represents an action of generating an RDF dump from data. It
 * provides the additional option
 * {@link RdfSerializationAction#OPTION_RDF_TASKS}, which is required for
 * generating any output.
 *
 * @author Markus Kroetzsch
 *
 */
public class RdfSerializationAction extends DumpProcessingOutputAction {

	static final Logger logger = LoggerFactory
			.getLogger(DumpProcessingOutputAction.class);

	/**
	 * Name of the option that defines the tasks of the RDF serializer. The
	 * value of this option should be a space-free, comma-separated list of task
	 * names. Tasks define the data that should be processed ("items",
	 * "properties", "entities") and what information should be written for each
	 * ("statements", "sitelinks", "datatypes", "labels", "desciptions",
	 * "aliases", "terms" meaning "labels,descriptions,aliases", "alldata"
	 * meaning "statements,terms,sitelinks,datatypes","simplestatements").
	 */
	public static final String OPTION_RDF_TASKS = "rdftasks";

	public static final Map<String, Integer> KNOWN_TASKS = new HashMap<>();
	static {
		KNOWN_TASKS.put("items", RdfSerializer.TASK_ITEMS);
		KNOWN_TASKS.put("properties", RdfSerializer.TASK_PROPERTIES);
		KNOWN_TASKS.put("entities", RdfSerializer.TASK_ALL_ENTITIES);
		KNOWN_TASKS.put("alldata", RdfSerializer.TASK_ALL_EXACT_DATA);
		KNOWN_TASKS.put("statements", RdfSerializer.TASK_STATEMENTS);
		KNOWN_TASKS.put("sitelinks", RdfSerializer.TASK_SITELINKS);
		KNOWN_TASKS.put("datatypes", RdfSerializer.TASK_DATATYPES);
		KNOWN_TASKS.put("labels", RdfSerializer.TASK_LABELS);
		KNOWN_TASKS.put("descriptions", RdfSerializer.TASK_DESCRIPTIONS);
		KNOWN_TASKS.put("aliases", RdfSerializer.TASK_ALIASES);
		KNOWN_TASKS.put("terms", RdfSerializer.TASK_TERMS);
		KNOWN_TASKS
				.put("interpropertylinks", RdfSerializer.TASK_PROPERTY_LINKS);
		KNOWN_TASKS.put("simplestatements",
				RdfSerializer.TASK_SIMPLE_STATEMENTS);
	}

	public static final Map<String, String> TASK_HELP = new HashMap<>();
	static {
		TASK_HELP.put("items", "consider items when exporting data");
		TASK_HELP.put("properties", "consider properties when exporting data");
		TASK_HELP.put("entities", "consider all entities when exporting data");
		TASK_HELP.put("alldata",
				"export all data available for the considered entities");
		TASK_HELP.put("statements",
				"export statements for the considered entities");
		TASK_HELP.put("sitelinks",
				"export site links for the considered entities");
		TASK_HELP.put("datatypes",
				"export datatypes for the considered entities");
		TASK_HELP.put("labels", "export labels for the considered entities");
		TASK_HELP.put("descriptions",
				"export descriptions for the considered entities");
		TASK_HELP.put("aliases", "export labels for the considered entities");
		TASK_HELP.put("terms", "shortcut for labels,descriptions,aliases");
		TASK_HELP
				.put("simplestatements",
						"export unqualified statements without references as single triples");
		TASK_HELP
				.put("interpropertylinks",
						"export triples which connect wikidata properties and there respective rdf properties in different contexts");
	}

	/**
	 * The base file name that will be used by default. File endings for
	 * indicating compression will be appended where required.
	 */
	public final static String DEFAULT_FILE_NAME = "{PROJECT}-{DATE}.rdf";

	/**
	 * default action name is used to separate different
	 * DumpProcessingOutputActions from each other.
	 */
	public final static String DEFAULT_ACTION_NAME = "RdfSerializationAction";

	/**
	 * Internal serializer object that will actually write the RDF output.
	 */
	RdfSerializer serializer;

	/**
	 * A string to identify the overall task to be executed. This is used to
	 * create the default output file name.
	 */
	String taskName = "";

	/**
	 * Integer that holds the serialization task flags.
	 */
	int tasks = 0;

	public RdfSerializationAction() {
		this.outputDestination = DEFAULT_FILE_NAME;
	}

	@Override
	public boolean setOption(String option, String value) {
		if (super.setOption(option, value)) {
			return true;
		}

		switch (option) {
		case OPTION_RDF_TASKS:
			setTasks(value);
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean needsSites() {
		return this.tasks != 0; // no need for sites when just printing help
	}

	@Override
	public boolean isReady() {
		if (this.tasks == 0) {
			printHelp();
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void open() {
		try {
			this.serializer = createRdfSerializer();
			this.serializer.open();
		} catch (IOException e) {
			// TODO better add proper exceptions to open() declaration
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		this.serializer.processItemDocument(itemDocument);
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		this.serializer.processPropertyDocument(propertyDocument);
	}

	@Override
	public void processLexemeDocument(LexemeDocument lexemeDocument) {
		this.serializer.processLexemeDocument(lexemeDocument);
	}

	@Override
	public void processMediaInfoDocument(MediaInfoDocument mediaInfoDocument) {
		this.serializer.processMediaInfoDocument(mediaInfoDocument);
	}

	@Override
	public void close() {
		this.serializer.close();
		super.close();
	}

	/**
	 * Creates a new RDF serializer based on the current configuration of this
	 * object.
	 *
	 * @return the newly created RDF serializer
	 * @throws IOException
	 *             if there were problems opening the output files
	 */
	protected RdfSerializer createRdfSerializer() throws IOException {

		String outputDestinationFinal;
		if (this.outputDestination != null) {
			outputDestinationFinal = this.outputDestination;
		} else {
			outputDestinationFinal = "{PROJECT}" + this.taskName + "{DATE}"
					+ ".nt";
		}

		OutputStream exportOutputStream = getOutputStream(this.useStdOut,
				insertDumpInformation(outputDestinationFinal),
				this.compressionType);

		RdfSerializer serializer = new RdfSerializer(RDFFormat.NTRIPLES,
				exportOutputStream, this.sites,
				PropertyRegister.getWikidataPropertyRegister());
		serializer.setTasks(this.tasks);

		return serializer;
	}

	/**
	 * Sets the RDF serialization tasks based on the given string value.
	 *
	 * @param tasks
	 *            a space-free, comma-separated list of task names
	 */
	private void setTasks(String tasks) {
		for (String task : tasks.split(",")) {
			if (KNOWN_TASKS.containsKey(task)) {
				this.tasks |= KNOWN_TASKS.get(task);
				this.taskName += (this.taskName.isEmpty() ? "" : "-") + task;
			} else {
				logger.warn("Unsupported RDF serialization task \"" + task
						+ "\". Run without specifying any tasks for help.");
			}
		}
	}

	private void printHelp() {
		List<String> rdfTasks = new ArrayList<>(KNOWN_TASKS.keySet());
		Collections.sort(rdfTasks);
		System.out.println("---");
		System.out
				.println(WordUtils
						.wrap("Use option --"
								+ OPTION_RDF_TASKS
								+ " to select the supported tasks for RDF export. "
								+ "Tasks may select which entities to consider, and which data to include.",
								75)
						+ "\nExample: \"items,labels,aliases\" exports labels and aliases of items."
						+ "\n\nAvailable tasks:");
		for (String supportedTask : rdfTasks) {
			System.out.println(WordUtils.wrap("* \"" + supportedTask + "\": "
					+ TASK_HELP.get(supportedTask), 75, "\n   ", true));
		}
	}

	@Override
	public String getReport() {
		if (this.serializer != null) {
			String message = "Finished serialization of "
					+ this.serializer.getTripleCount()
					+ " RDF triples in file "
					+ this.insertDumpInformation(this.outputDestination);
			if (!this.compressionType.equals(COMPRESS_NONE)) {
				message += "." + this.compressionType;
			}
			return message;
		} else {
			logger.warn("Could not create report. open() was not executed.");
			return "";
		}
	}

	@Override
	public String getDefaultActionName() {
		return DEFAULT_ACTION_NAME;
	}
}
