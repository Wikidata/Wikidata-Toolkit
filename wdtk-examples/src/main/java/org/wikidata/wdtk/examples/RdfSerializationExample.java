package org.wikidata.wdtk.examples;

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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.openrdf.rio.RDFFormat;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;
import org.wikidata.wdtk.dumpfiles.StatisticsMwRevisionProcessor;
import org.wikidata.wdtk.rdf.RdfSerializer;

/**
 * This class shows how convert data from wikidata.org to RDF in N3 format. The
 * compressed output will be written into several files that will be placed in
 * the example directory.
 * 
 * @author Michael Günther
 * @author Markus Kroetzsch
 */
public class RdfSerializationExample {

	private static Sites sites;
	private static DumpProcessingController dumpProcessingController;
	private static List<RdfSerializer> serializers = new ArrayList<RdfSerializer>();
	private static List<String> serializerNames = new ArrayList<String>();

	public static void main(String[] args) throws IOException {

		// Define where log messages go
		ExampleHelpers.configureLogging();

		// Print information about this program
		printDocumentation();

		// Controller object for processing dumps:
		dumpProcessingController = new DumpProcessingController("wikidatawiki");
		// dumpProcessingController.setOfflineMode(true);

		sites = dumpProcessingController.getSitesInformation();

		createRdfSerializer("Wikidata-terms.n3.bz2",
				RdfSerializer.TASK_ALL_ENTITIES | RdfSerializer.TASK_TERMS);
		createRdfSerializer("Wikidata-statements.n3.bz2",
				RdfSerializer.TASK_ALL_ENTITIES | RdfSerializer.TASK_STATEMENTS);
		createRdfSerializer("Wikidata-simple-statements.n3.bz2",
				RdfSerializer.TASK_ALL_ENTITIES
						| RdfSerializer.TASK_SIMPLE_STATEMENTS);
		createRdfSerializer("Wikidata-taxonomy.n3.bz2",
				RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_TAXONOMY);
		createRdfSerializer("Wikidata-instances.n3.bz2",
				RdfSerializer.TASK_ALL_ENTITIES
						| RdfSerializer.TASK_INSTANCE_OF);
		createRdfSerializer("Wikidata-sitelinks.n3.bz2",
				RdfSerializer.TASK_ALL_ENTITIES | RdfSerializer.TASK_SITELINKS);

		// General statistics and time keeping:
		MwRevisionProcessor rpRevisionStats = new StatisticsMwRevisionProcessor(
				"revision processing statistics", 10000);
		// Subscribe to all current revisions (null = no filter):
		dumpProcessingController.registerMwRevisionProcessor(rpRevisionStats,
				null, true);

		// Set up the serializer and write headers
		startSerializers();

		// // Start processing (may trigger downloads where needed)
		// dumpProcessingController.processAllRecentRevisionDumps();
		// Process just the most recent main dump:
		dumpProcessingController.processMostRecentMainDump();

		// Finish the serialization
		closeSerializers();
	}

	/**
	 * Creates a new RDF Serializer. Output is written to the file of the given
	 * name (it will always be a compressed file, so the name should reflect
	 * that). The tasks define what the serializer will be writing into this
	 * file. The new serializer is also registered in an internal list, so it
	 * can be started and closed more conveniently.
	 * 
	 * @param outputFileName
	 *            filename to write output to
	 * @param tasks
	 *            an integer that is a bitwise OR of flags like
	 *            {@link RdfSerializer#TASK_LABELS}.
	 * @return the newly created serializer
	 * @throws FileNotFoundException
	 *             if the given file cannot be opened for writing for some
	 *             reason
	 * @throws IOException
	 *             if it was not possible to write the BZ2 header to the file
	 */
	private static RdfSerializer createRdfSerializer(String outputFileName,
			int tasks) throws FileNotFoundException, IOException {
		OutputStream simpleStatementsOutputStream = new BZip2CompressorOutputStream(
				new FileOutputStream(outputFileName));
		RdfSerializer serializer = new RdfSerializer(RDFFormat.NTRIPLES,
				simpleStatementsOutputStream, sites);
		serializer.setTasks(tasks);

		dumpProcessingController.registerEntityDocumentProcessor(serializer,
				MwRevision.MODEL_WIKIBASE_ITEM, true);
		dumpProcessingController.registerEntityDocumentProcessor(serializer,
				MwRevision.MODEL_WIKIBASE_PROPERTY, true);

		serializers.add(serializer);
		serializerNames.add(outputFileName);

		return serializer;
	}

	/**
	 * Starts the serializers. This includes the writing of headers if any (N3
	 * has no headers, but other formats have).
	 */
	private static void startSerializers() {
		for (RdfSerializer serializer : serializers) {
			serializer.start();
		}
	}

	/**
	 * Closes the serializers (and their output streams), and prints a short
	 * summary of the number of triples serialized by each.
	 */
	private static void closeSerializers() {
		Iterator<String> nameIterator = serializerNames.iterator();
		for (RdfSerializer serializer : serializers) {
			serializer.close();
			System.out.println("*** Finished serialization of "
					+ serializer.getTripleCount() + " RDF triples in file "
					+ nameIterator.next());
		}
	}

	/**
	 * Print some basic documentation about this program.
	 */
	private static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: Serialization Example");
		System.out.println("*** ");
		System.out
				.println("*** This program will download dumps from Wikidata and serialize the data in a RDF format.");
		System.out
				.println("*** Downloading may take some time initially. After that, files");
		System.out
				.println("*** are stored on disk and are used until newer dumps are available.");
		System.out
				.println("*** You can delete files manually when no longer needed (see ");
		System.out
				.println("*** message below for the directory where dump files are found).");
		System.out
				.println("*** The output will be stored in the directory of the example.");
		System.out
				.println("********************************************************************");
	}
}
