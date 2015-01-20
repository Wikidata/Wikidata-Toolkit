package org.wikidata.wdtk.examples;

/*
 * #%L
 * Wikidata Toolkit Examples
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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentProcessor;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonObjectFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This example illustrates how to create a JSON serialization of some of the
 * data found in a dump. It uses a {@link DatamodelConverter} with filter
 * settings to eliminate some of the data.
 * <p>
 * As an example, the program only serializes data for people who were born in
 * Dresden, Germany. This can be changed by modifying the code in
 * {@link #includeDocument(ItemDocument)}.
 *
 * @author Markus Kroetzsch
 *
 */
public class JsonSerializationProcessor implements EntityDocumentProcessor {

	static final String OUTPUT_FILE_NAME = "json-serialization-example.json.gz";

	/**
	 * Object used to make simplified copies of Wikidata documents for
	 * re-serialization in JSON.
	 */
	final DatamodelConverter datamodelConverter;

	/**
	 * The stream that the resulting JSON is written to.
	 */
	final OutputStream outputStream;
	/**
	 * A writer to add some characters to the output file in between the actual
	 * JSON serializations.
	 */
	final Writer outputStreamWriter;
	/**
	 * Object mapper that is used to serialize JSON.
	 */
	private final ObjectMapper mapper;

	/**
	 * Counter to keep track how many item documents have been exported.
	 */
	int itemCount = 0;

	/**
	 * Runs the example program.
	 *
	 * @param args
	 * @throws IOException
	 *             if there was a problem in writing the output file
	 */
	public static void main(String[] args) throws IOException {
		ExampleHelpers.configureLogging();
		JsonSerializationProcessor.printDocumentation();

		JsonSerializationProcessor jsonSerializationProcessor = new JsonSerializationProcessor();
		ExampleHelpers
				.processEntitiesFromWikidataDump(jsonSerializationProcessor);
		jsonSerializationProcessor.close();
	}

	/**
	 * Constructor. Initializes various helper objects we use for the JSON
	 * serialization, and opens the file that we want to write to.
	 *
	 * @throws IOException
	 *             if there is a problem opening the output file
	 */
	public JsonSerializationProcessor() throws IOException {
		// The converter is used to copy selected parts of the data. We use this
		// to remove some parts from the documents we serialize. The converter
		// uses a JacksonObjectFactory to be sure that we create objects for
		// which we know how to write JSON. A similar converter without the
		// filtering enabled would also be used to create JSON-serializable
		// objects from any other implementation of the Wikidata Toolkit
		// datamodel.
		this.datamodelConverter = new DatamodelConverter(
				new JacksonObjectFactory());
		// Do not copy references at all:
		this.datamodelConverter.setOptionDeepCopyReferences(false);
		// Only copy English labels, descriptions, and aliases:
		this.datamodelConverter.setOptionLanguageFilter(Collections
				.singleton("en"));
		// Only copy statements of some properties:
		Set<PropertyIdValue> propertyFilter = new HashSet<>();
		propertyFilter.add(Datamodel.makeWikidataPropertyIdValue("P18")); // image
		propertyFilter.add(Datamodel.makeWikidataPropertyIdValue("P106")); // occupation
		propertyFilter.add(Datamodel.makeWikidataPropertyIdValue("P569")); // birthdate
		this.datamodelConverter.setOptionPropertyFilter(propertyFilter);
		// Do not copy any sitelinks:
		this.datamodelConverter.setOptionSiteLinkFilter(Collections
				.<String> emptySet());

		// The mapper is an object from the Jackson library that handles the
		// serialization in JSON.
		this.mapper = new ObjectMapper();
		// Make sure it does not close our file after the first object:
		this.mapper.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);

		// The (compressed) file we write to.
		this.outputStream = new GzipCompressorOutputStream(
				new BufferedOutputStream(
						ExampleHelpers
								.openExampleFileOuputStream(OUTPUT_FILE_NAME)));

		// We use this OutputStreamWriter to inject extra characters in between
		// JSON output. This is maybe not the cleanest way of doing it (calling
		// flush() is essential).
		this.outputStreamWriter = new OutputStreamWriter(this.outputStream);
		this.outputStreamWriter.write("[\n");
		this.outputStreamWriter.flush();
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		if (includeDocument(itemDocument)) {
			this.itemCount++;
			try {
				this.mapper.writeValue(this.outputStream,
						this.datamodelConverter.copy(itemDocument));
				this.outputStreamWriter.write(",\n");
				this.outputStreamWriter.flush();
			} catch (IOException e) {
				throw new RuntimeException(e.toString(), e);
			}
		}
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		// we do not serialize any properties
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: JsonSerializationProcessor");
		System.out.println("*** ");
		System.out
				.println("*** This program will download and process dumps from Wikidata.");
		System.out
				.println("*** It will filter the data and store the results in a new JSON file.");
		System.out.println("*** See source code for further details.");
		System.out
				.println("********************************************************************");
	}

	/**
	 * Closes the output. Should be called after the JSON serialization was
	 * finished.
	 *
	 * @throws IOException
	 *             if there was a problem closing the output
	 */
	public void close() throws IOException {
		System.out.println("Serialized " + this.itemCount
				+ " item documents to JSON file " + OUTPUT_FILE_NAME + ".");
		this.outputStreamWriter.write("]");
		this.outputStreamWriter.flush();
		this.outputStreamWriter.close();
	}

	/**
	 * Returns true if the given document should be included in the
	 * serialization.
	 *
	 * @param itemDocument
	 *            the document to check
	 * @return true if the document should be serialized
	 */
	private boolean includeDocument(ItemDocument itemDocument) {
		for (StatementGroup sg : itemDocument.getStatementGroups()) {
			// "P19" is "place of birth" on Wikidata
			if (!"P19".equals(sg.getProperty().getId())) {
				continue;
			}
			for (Statement s : sg.getStatements()) {
				if (s.getClaim().getMainSnak() instanceof ValueSnak) {
					Value v = ((ValueSnak) s.getClaim().getMainSnak())
							.getValue();
					// "Q1731" is "Dresden" on Wikidata
					if (v instanceof ItemIdValue
							&& "Q1731".equals(((ItemIdValue) v).getId())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
