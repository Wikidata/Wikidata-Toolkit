package org.wikidata.wdtk.clt;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.datamodel.json.JsonSerializer;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;
import org.wikidata.wdtk.dumpfiles.MwRevision;
import org.wikidata.wdtk.dumpfiles.MwRevisionProcessor;
import org.wikidata.wdtk.dumpfiles.StatisticsMwRevisionProcessor;
import org.wikidata.wdtk.rdf.RdfSerializer;

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

/**
 * This class provides a java comand line client to generate dumps in various
 * data formats like json and rdf.
 * 
 * @author Michael Günther
 * 
 */
public class ConversionClient {

	final static String COMPRESS_BZ2 = ".bz2";
	final static String COMPRESS_GZIP = ".gz";
	final static String COMPRESS_NONE = "";

	private static Sites sites;
	private static DumpProcessingController dumpProcessingController;
	private static List<RdfSerializer> serializers = new ArrayList<RdfSerializer>();
	private static List<String> serializerNames = new ArrayList<String>();

	List<ConversionConfiguration> configuration;

	// true if any of the serializers want to put its output to stdout to
	// prevent logging things to stdout
	Boolean stdout = false;
	// true if any conversion format was specified
	Boolean convertAnything = false;

	static final Logger logger = LoggerFactory
			.getLogger(ConversionClient.class);

	public List<ConversionConfiguration> getConfiguration() {
		return this.configuration;
	}

	public Boolean getConvertAnything() {
		return convertAnything;
	}

	public Boolean getStdout() {
		return stdout;
	}

	/**
	 * Builds up serializers for the different rdf files.
	 * 
	 * @throws IOException
	 */
	public void setupForRdfSerialization(
			ConversionConfiguration conversionConfiguration) throws IOException {
		String compressionExtension = conversionConfiguration
				.getCompressionExtension();

		// Create serializers for several data parts and encodings depending on
		// the Rdfdump property:
		if (conversionConfiguration.getRdfdump().toLowerCase()
				.equals("all_exact_data")) {
			createRdfSerializer(conversionConfiguration.getOutputDestination(),
					"wikidata-properties.nt", compressionExtension,
					RdfSerializer.TASK_PROPERTIES
							| RdfSerializer.TASK_ALL_EXACT_DATA,
					conversionConfiguration.getStdout());
		}
		if (conversionConfiguration.getRdfdump().toLowerCase().equals("terms")) {
			createRdfSerializer(conversionConfiguration.getOutputDestination(),
					"wikidata-terms.nt", compressionExtension,
					RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_TERMS,
					conversionConfiguration.getStdout());
		}
		if (conversionConfiguration.getRdfdump().toLowerCase()
				.equals("statements")) {
			createRdfSerializer(conversionConfiguration.getOutputDestination(),
					"wikidata-statements.nt", compressionExtension,
					RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_STATEMENTS,
					conversionConfiguration.getStdout());
		}
		if (conversionConfiguration.getRdfdump().toLowerCase()
				.equals("simple_statements")) {
			createRdfSerializer(conversionConfiguration.getOutputDestination(),
					"wikidata-simple-statements.nt", compressionExtension,
					RdfSerializer.TASK_ITEMS
							| RdfSerializer.TASK_SIMPLE_STATEMENTS,
					conversionConfiguration.getStdout());
		}
		if (conversionConfiguration.getRdfdump().toLowerCase()
				.equals("taxonomy")) {
			createRdfSerializer(conversionConfiguration.getOutputDestination(),
					"wikidata-taxonomy.nt", compressionExtension,
					RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_TAXONOMY,
					conversionConfiguration.getStdout());
		}
		if (conversionConfiguration.getRdfdump().toLowerCase()
				.equals("instance_of")) {
			createRdfSerializer(conversionConfiguration.getOutputDestination(),
					"wikidata-instances.nt", compressionExtension,
					RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_INSTANCE_OF,
					conversionConfiguration.getStdout());
		}
		if (conversionConfiguration.getRdfdump().toLowerCase()
				.equals("sitelinks")) {
			createRdfSerializer(conversionConfiguration.getOutputDestination(),
					"wikidata-sitelinks.nt", compressionExtension,
					RdfSerializer.TASK_ITEMS | RdfSerializer.TASK_SITELINKS,
					conversionConfiguration.getStdout());
		}

	}

	/**
	 * Builds up a serializer for json.
	 * 
	 * @param conversionConfiguration
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void setupForJsonSerialization(
			ConversionConfiguration conversionConfiguration)
			throws FileNotFoundException, IOException {
		OutputStream outputStream;
		if (conversionConfiguration.getStdout()) {
			outputStream = System.out;
		} else {
			new File(conversionConfiguration.getOutputDestination()).mkdirs();
			OutputStream bufferedFileOutputStream = new BufferedOutputStream(
					new FileOutputStream(
							conversionConfiguration.getOutputDestination()
									+ "WikidataDump.json"
									+ conversionConfiguration
											.getCompressionExtension()), 1024
							* 1024 * 5 * 0 + 100);

			switch (conversionConfiguration.getCompressionExtension()) {
			case COMPRESS_BZ2:
				outputStream = new BZip2CompressorOutputStream(
						bufferedFileOutputStream);
				break;
			case COMPRESS_GZIP:
				GzipParameters gzipParameters = new GzipParameters();
				gzipParameters.setCompressionLevel(7);
				outputStream = new GzipCompressorOutputStream(
						bufferedFileOutputStream, gzipParameters);
				break;
			case COMPRESS_NONE:
				outputStream = bufferedFileOutputStream;
				break;
			default:
				bufferedFileOutputStream.close();
				throw new IllegalArgumentException(
						"Unsupported compression format: "
								+ conversionConfiguration
										.getCompressionExtension());
			}
		}
		// Create an object for managing the serialization process
		JsonSerializer serializer = new JsonSerializer(outputStream);

		// Subscribe to the most recent entity documents of type wikibase item
		// and property:
		dumpProcessingController.registerEntityDocumentProcessor(serializer,
				MwRevision.MODEL_WIKIBASE_ITEM, true);
		dumpProcessingController.registerEntityDocumentProcessor(serializer,
				MwRevision.MODEL_WIKIBASE_PROPERTY, true);
	}

	/**
	 * Defines how messages should be logged. This method can be modified to
	 * restrict the logging messages that are shown on the console or to change
	 * their formatting. See the documentation of Log4J for details on how to do
	 * this.
	 */
	public static void configureLogging() {
		// Create the appender that will write log messages to the console.
		ConsoleAppender consoleAppender = new ConsoleAppender();
		// Define the pattern of log messages.
		// Insert the string "%c{1}:%L" to also show class name and line.
		String pattern = "%d{yyyy-MM-dd HH:mm:ss} %-5p - %m%n";
		consoleAppender.setLayout(new PatternLayout(pattern));
		// Change to Level.ERROR for fewer messages:
		consoleAppender.setThreshold(Level.INFO);

		consoleAppender.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(consoleAppender);
	}

	/**
	 * Manages the serialization process. Therefore a
	 * {@link DumpProcessingController} and a serializer for the chosen output
	 * formats will be set up. After that the serialization process will be
	 * initiated.
	 * 
	 * @param conversionConfiguration
	 * @throws IOException
	 */
	public void convert() throws IOException {

		if (!stdout) {
			// Define where log messages go
			configureLogging();
		}

		// Controller object for processing dumps:
		dumpProcessingController = new DumpProcessingController("wikidatawiki");

		// Initialize sites; needed to link to Wikipedia pages in RDF
		sites = dumpProcessingController.getSitesInformation();

		if (configuration.get(0).getOfflineMode()) {
			dumpProcessingController.setOfflineMode(true);
		}

		for (ConversionConfiguration props : configuration) {
			switch (props.getOutputFormat()) {
			case "json":
				setupForJsonSerialization(props);
				break;
			case "rdf":
				setupForRdfSerialization(props);
				break;
			}

		}

		if (!stdout) {
			// General statistics and time keeping:
			MwRevisionProcessor rpRevisionStats = new StatisticsMwRevisionProcessor(
					"revision processing statistics", 10000);

			// Subscribe to all current revisions (null = no filter):
			dumpProcessingController.registerMwRevisionProcessor(
					rpRevisionStats, null, true);
		}

		if (configuration.get(0).getDumplocation() != null) {
			dumpProcessingController.setDownloadDirectory(configuration.get(0)
					.getDumplocation());
		}

		// Set up the serializer and write headers
		startSerializers();

		System.out.println("processMostrecentMainDump");
		// Start processing (may trigger downloads where needed)
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
	 * @param compressionExtension
	 *            the extension of the chosen compression format or the empty
	 *            string for no compression
	 * @param tasks
	 *            an integer that is a bitwise OR of flags like
	 *            {@link RdfSerializer#TASK_LABELS}.
	 * @param stdout
	 * @return the newly created serializer
	 * @throws FileNotFoundException
	 *             if the given file cannot be opened for writing for some
	 *             reason
	 * @throws IOException
	 *             if it was not possible to write the BZ2 header to the file
	 */
	@SuppressWarnings("resource")
	private RdfSerializer createRdfSerializer(String outputDestination,
			String outputFileName, String compressionExtension, int tasks,
			Boolean stdout) throws FileNotFoundException, IOException {
		new File(outputDestination).mkdirs();
		OutputStream bufferedFileOutputStream = new BufferedOutputStream(
				new FileOutputStream(outputDestination + outputFileName
						+ compressionExtension), 1024 * 1024 * 5 * 0 + 100);

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
			bufferedFileOutputStream.close();
			throw new IllegalArgumentException(
					"Unsupported compression format: " + compressionExtension);
		}

		OutputStream exportOutputStream;

		if (stdout) {
			exportOutputStream = System.out;
		} else {
			exportOutputStream = asynchronousOutputStream(compressorOutputStream);
		}

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

	public ConversionClient(String args[]) throws ParseException, IOException {
		ConversionProperties conversionProperties = new ConversionProperties(
				args);
		this.configuration = conversionProperties.getProperties();

		// set flags (stdout and convertAnything)
		for (ConversionConfiguration configuration : this.getConfiguration()) {
			if (configuration.getStdout() == true) {
				this.stdout = true;
			}
			if (configuration.getOutputFormat() != "none") {
				this.convertAnything = true;
			}
		}

	}

	public static void main(String[] args) throws ParseException, IOException {
		ConversionClient client = new ConversionClient(args);
		if (client.getConvertAnything()) {
			client.convert();
		}

	}
}
