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
import java.util.HashMap;
import java.util.Map;

import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
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
	 * meaning "statements,terms,sitelinks,datatypes", "taxonomy", "instanceof",
	 * "simplestatements").
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
		KNOWN_TASKS.put("taxonomy", RdfSerializer.TASK_TAXONOMY);
		KNOWN_TASKS.put("instanceof", RdfSerializer.TASK_INSTANCE_OF);
		KNOWN_TASKS.put("simplestatements",
				RdfSerializer.TASK_SIMPLE_STATEMENTS);
	}

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

	/**
	 * Constructor.
	 *
	 * @param conversionProperties
	 */
	public RdfSerializationAction(ClientConfiguration conversionProperties) {
		super(conversionProperties);
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
		return true;
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
	public void close() {
		this.serializer.close();
		logger.info("Finished serialization of "
				+ this.serializer.getTripleCount() + " RDF triples in file "
				+ this.outputDestination);
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
			outputDestinationFinal = this.taskName + ".nt";
		}

		OutputStream exportOutputStream = getOutputStream(this.useStdOut,
				outputDestinationFinal, this.compressionType);

		RdfSerializer serializer = new RdfSerializer(RDFFormat.NTRIPLES,
				exportOutputStream, this.sites);
		serializer.setTasks(this.tasks);

		if (this.tasks == 0) {
			logger.warn("No tasks configured for RDF export. Use option \""
					+ OPTION_RDF_TASKS
					+ "\" to define which RDF exports to generate.");
		}

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
						+ "\". Supported tasks are: " + KNOWN_TASKS.keySet());
			}
		}
	}

}
