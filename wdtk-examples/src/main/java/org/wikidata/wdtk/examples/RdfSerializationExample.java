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

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.rdf.PropertyRegister;
import org.wikidata.wdtk.rdf.RdfSerializer;

/**
 * This class shows how convert data from wikidata.org to RDF in N-Triples format. The
 * compressed output will be written into an output file.
 *
 * @author Michael Günther
 * @author Markus Kroetzsch
 */
public class RdfSerializationExample {

	public static void main(String[] args) throws IOException {

		// Define where log messages go
		ExampleHelpers.configureLogging();

		// Print information about this program
		printDocumentation();

		// Initialize sites; only needed to link to Wikipedia pages in RDF
		DumpProcessingController dumpProcessingController = new DumpProcessingController(
				"wikidatawiki");
		dumpProcessingController.setOfflineMode(ExampleHelpers.OFFLINE_MODE);
		Sites sites = dumpProcessingController.getSitesInformation();

		// Prepare a compressed output stream to write the data to
		// (admittedly, this is slightly over-optimized for an example)
		try(OutputStream bufferedFileOutputStream = new BufferedOutputStream(
				ExampleHelpers.openExampleFileOuputStream("wikidata-simple-statements.nt.gz"),
				1024 * 1024 * 5
		)) {
			GzipParameters gzipParameters = new GzipParameters();
			gzipParameters.setCompressionLevel(7);
			OutputStream compressorOutputStream = new GzipCompressorOutputStream(
					bufferedFileOutputStream, gzipParameters);
			OutputStream exportOutputStream = asynchronousOutputStream(compressorOutputStream);

			// Create a serializer processor
			RdfSerializer serializer = new RdfSerializer(RDFFormat.NTRIPLES,
					exportOutputStream, sites,
					PropertyRegister.getWikidataPropertyRegister());
			// Serialize simple statements (and nothing else) for all items
			serializer.setTasks(RdfSerializer.TASK_ITEMS
					| RdfSerializer.TASK_SIMPLE_STATEMENTS);

			// Run serialization
			serializer.open();
			ExampleHelpers.processEntitiesFromWikidataDump(serializer);
			serializer.close();
		}

	}

	/**
	 * Print some basic documentation about this program.
	 */
	private static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: RDF Serialization Example");
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
				.println("********************************************************************");
	}

	/**
	 * Creates a separate thread for writing into the given output stream and
	 * returns a pipe output stream that can be used to pass data to this
	 * thread.
	 * <p>
	 * This code is inspired by
	 * http://stackoverflow.com/questions/12532073/gzipoutputstream
	 * -that-does-its-compression-in-a-separate-thread
	 *
	 * @param outputStream
	 *            the stream to write to in the thread
	 * @return a new stream that data should be written to
	 * @throws IOException
	 *             if the pipes could not be created for some reason
	 */
	public static OutputStream asynchronousOutputStream(
			final OutputStream outputStream) throws IOException {
		final int SIZE = 1024 * 1024 * 10;
		final PipedOutputStream pos = new PipedOutputStream();
		final PipedInputStream pis = new PipedInputStream(pos, SIZE);
		new Thread(() -> {
			try {
				byte[] bytes = new byte[SIZE];
				for (int len; (len = pis.read(bytes)) > 0;) {
					outputStream.write(bytes, 0, len);
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			} finally {
				close(pis);
				close(outputStream);
			}
		}, "async-output-stream").start();
		return pos;
	}

	/**
	 * Closes a Closeable and swallows any exceptions that might occur in the
	 * process.
	 *
	 * @param closeable
	 */
	static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ignored) {
			}
		}
	}
}
