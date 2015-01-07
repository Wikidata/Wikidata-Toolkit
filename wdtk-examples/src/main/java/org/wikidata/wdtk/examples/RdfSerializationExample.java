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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.openrdf.rio.RDFFormat;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.rdf.RdfSerializer;

/**
 * This class shows how convert data from wikidata.org to RDF in N3 format. The
 * compressed output will be written into several files that will be placed in
 * the example directory.
 * <p>
 * In the future, this will probably become a stand-alone tool that can be
 * called directly.
 *
 * @author Michael Günther
 * @author Markus Kroetzsch
 */
public class RdfSerializationExample {

	final static String COMPRESS_BZ2 = ".bz2";
	final static String COMPRESS_GZIP = ".gz";
	final static String COMPRESS_NONE = "";

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
		dumpProcessingController.setOfflineMode(ExampleHelpers.OFFLINE_MODE);

		// Initialize sites; needed to link to Wikipedia pages in RDF
		sites = dumpProcessingController.getSitesInformation();

		// Create serializers for several data parts and encodings:
		createRdfSerializer("wikidata-properties.nt", COMPRESS_GZIP,
				RdfSerializer.TASK_PROPERTIES
						| RdfSerializer.TASK_ALL_EXACT_DATA);
		createRdfSerializer("wikidata-terms.nt", COMPRESS_GZIP,
				RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_TERMS);
		createRdfSerializer("wikidata-statements.nt", COMPRESS_GZIP,
				RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_STATEMENTS);
		createRdfSerializer("wikidata-simple-statements.nt", COMPRESS_GZIP,
				RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_SIMPLE_STATEMENTS);
		createRdfSerializer("wikidata-taxonomy.nt", COMPRESS_GZIP,
				RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_TAXONOMY);
		createRdfSerializer("wikidata-instances.nt", COMPRESS_GZIP,
				RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_INSTANCE_OF);
		createRdfSerializer("wikidata-sitelinks.nt", COMPRESS_GZIP,
				RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_SITELINKS);

		EntityTimerProcessor entityTimerProcessor = new EntityTimerProcessor(0);
		dumpProcessingController.registerEntityDocumentProcessor(
				entityTimerProcessor, null, true);

		// Set up the serializer and write headers
		startSerializers();

		// Start processing (may trigger downloads where needed)
		dumpProcessingController.processMostRecentJsonDump();

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
	 * @param compressionExtension
	 *            the extension of the chosen compression format or the empty
	 *            string for no compression
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
	@SuppressWarnings("resource")
	private static RdfSerializer createRdfSerializer(String outputFileName,
			String compressionExtension, int tasks)
			throws FileNotFoundException, IOException {

		OutputStream bufferedFileOutputStream = new BufferedOutputStream(
				ExampleHelpers.openExampleFileOuputStream(outputFileName
						+ compressionExtension), 1024 * 1024 * 5);

		OutputStream compressorOutputStream = null;
		switch (compressionExtension) {
		case COMPRESS_BZ2:
			compressorOutputStream = new BZip2CompressorOutputStream(
					bufferedFileOutputStream);
			break;
		case COMPRESS_GZIP:
			GzipParameters gzipParameters = new GzipParameters();
			gzipParameters.setCompressionLevel(7);
			compressorOutputStream = new GzipCompressorOutputStream(
					bufferedFileOutputStream, gzipParameters);
			break;
		case COMPRESS_NONE:
			compressorOutputStream = bufferedFileOutputStream;
			break;
		default:
			throw new IllegalArgumentException(
					"Unsupported compression format: " + compressionExtension);
		}

		OutputStream exportOutputStream = asynchronousOutputStream(compressorOutputStream);
		// // Alternative code: if not using separate threads, increase
		// // pre-compression buffer:
		// OutputStream exportOutputStream = new
		// BufferedOutputStream(compressorOutputStream,1024 * 1024 * 50);

		RdfSerializer serializer = new RdfSerializer(RDFFormat.NTRIPLES,
				exportOutputStream, sites);
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
		new Thread(new Runnable() {
			@Override
			public void run() {
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
