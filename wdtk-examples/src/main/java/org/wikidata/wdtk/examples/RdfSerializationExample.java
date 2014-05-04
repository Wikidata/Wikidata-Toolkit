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

import java.io.FileOutputStream;
import java.io.IOException;

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
 * compressed output will be written into a file named WikidataDump.n3.bz2. You
 * can find it in the example directory after you ran the example code.
 * 
 * @author Michael Günther
 * 
 */
public class RdfSerializationExample {

	public static void main(String[] args) throws IOException {

		// Define where log messages go
		ExampleHelpers.configureLogging();

		// Print information about this program
		printDocumentation();

		// Controller object for processing dumps:
		DumpProcessingController dumpProcessingController = new DumpProcessingController(
				"wikidatawiki");
		// dumpProcessingController.setOfflineMode(true);

		// Write the output to a BZip2-compressed file
		Sites sites = dumpProcessingController.getSitesInformation();

		BZip2CompressorOutputStream termsOutputStream = new BZip2CompressorOutputStream(
				new FileOutputStream("Wikidata-terms.n3.bz2"));
		RdfSerializer termSerializer = new RdfSerializer(RDFFormat.NTRIPLES,
				termsOutputStream, sites);
		termSerializer.setStatementsEnabled(false);
		termSerializer.setSiteLinksEnabled(false);

		BZip2CompressorOutputStream statementsOutputStream = new BZip2CompressorOutputStream(
				new FileOutputStream("Wikidata-statements.n3.bz2"));
		RdfSerializer statementSerializer = new RdfSerializer(
				RDFFormat.NTRIPLES, statementsOutputStream, sites);
		statementSerializer.setTermsEnabled(false);
		statementSerializer.setSiteLinksEnabled(false);

		BZip2CompressorOutputStream siteLinksOutputStream = new BZip2CompressorOutputStream(
				new FileOutputStream("Wikidata-sitelinks.n3.bz2"));
		RdfSerializer siteLinkSerializer = new RdfSerializer(
				RDFFormat.NTRIPLES, siteLinksOutputStream, sites);
		siteLinkSerializer.setTermsEnabled(false);
		siteLinkSerializer.setStatementsEnabled(false);

		// Subscribe to the most recent entity documents of type wikibase item
		// and property:
		dumpProcessingController.registerEntityDocumentProcessor(
				termSerializer, MwRevision.MODEL_WIKIBASE_ITEM, true);
		dumpProcessingController.registerEntityDocumentProcessor(
				termSerializer, MwRevision.MODEL_WIKIBASE_PROPERTY, true);
		dumpProcessingController.registerEntityDocumentProcessor(
				statementSerializer, MwRevision.MODEL_WIKIBASE_ITEM, true);
		dumpProcessingController.registerEntityDocumentProcessor(
				statementSerializer, MwRevision.MODEL_WIKIBASE_PROPERTY, true);
		dumpProcessingController.registerEntityDocumentProcessor(
				siteLinkSerializer, MwRevision.MODEL_WIKIBASE_ITEM, true);
		dumpProcessingController.registerEntityDocumentProcessor(
				siteLinkSerializer, MwRevision.MODEL_WIKIBASE_PROPERTY, true);

		// General statistics and time keeping:
		MwRevisionProcessor rpRevisionStats = new StatisticsMwRevisionProcessor(
				"revision processing statistics", 10000);
		// Subscribe to all current revisions (null = no filter):
		dumpProcessingController.registerMwRevisionProcessor(rpRevisionStats,
				null, true);

		// Set up the serializer and write headers
		termSerializer.startSerialization();
		statementSerializer.startSerialization();
		siteLinkSerializer.startSerialization();

		// Start processing (may trigger downloads where needed)
		dumpProcessingController.processAllRecentRevisionDumps();
		// // Process just a recent main dump for testing:
		// dumpProcessingController.processMostRecentMainDump();

		// Finish the serialization
		termSerializer.finishSerialization();
		statementSerializer.finishSerialization();
		siteLinkSerializer.finishSerialization();
		termsOutputStream.close();
		statementsOutputStream.close();
		siteLinksOutputStream.close();

		System.out.println("*** Finished serialization of "
				+ termSerializer.getTripleCount() + " RDF term triples.");
		System.out.println("*** Finished serialization of "
				+ statementSerializer.getTripleCount()
				+ " RDF statement triples.");
		System.out.println("*** Finished serialization of "
				+ siteLinkSerializer.getTripleCount()
				+ " RDF site link triples.");
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
				.println("*** This program will download dumps from Wikidata and serialize the data in a json format.");
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
