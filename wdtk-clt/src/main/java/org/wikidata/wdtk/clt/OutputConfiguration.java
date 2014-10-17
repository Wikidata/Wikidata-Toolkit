package org.wikidata.wdtk.clt;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipParameters;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.dumpfiles.DumpProcessingController;

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
 * This class holds a configuration of parameters for a conversion.
 * 
 * @author Michael Günther
 * 
 */
public abstract class OutputConfiguration {

	final static String COMPRESS_BZ2 = ".bz2";
	final static String COMPRESS_GZIP = ".gz";
	final static String COMPRESS_NONE = "";

	ConversionProperties conversionProperties;

	String outputDestination = "";
	String compressionExtension = ConversionClient.COMPRESS_NONE;
	Boolean useStdout = false;

	/**
	 * Constructor. It takes an instance of {@link ConversionProperties} to get
	 * access to additional general arguments.
	 * 
	 * @param conversionProperties
	 */
	public OutputConfiguration(ConversionProperties conversionProperties) {
		this.conversionProperties = conversionProperties;
	}

	/**
	 * Returns the name of the output format.
	 * 
	 * @return format name
	 */
	public abstract String getOutputFormat();

	/**
	 * Setup the serializer for the respective format and add it to the
	 * processing pipeline.
	 * 
	 * @param dumpProcessingController
	 *            Builds up the pipeline and is need to register the serializer.
	 * @param sites
	 *            needed to link to wikipedia pages, if requested
	 * @throws IOException
	 */
	public abstract void setupSerializer(
			DumpProcessingController dumpProcessingController, Sites sites)
			throws IOException;

	/**
	 * Things which should be done before the serialization of the
	 * {@link EntityDocument} objects started, can be implemented here.
	 */
	public abstract void startSerializer();

	/**
	 * Called after the serialization of all {@link EntityDocument} objects.
	 */
	public abstract void closeSerializer();

	public String getCompressionExtension() {
		return compressionExtension;
	}

	public void setCompressionExtension(String compressionExtention) {
		this.compressionExtension = compressionExtention;
	}

	public String getOutputDestination() {
		return outputDestination;
	}

	public void setOutputDestination(String outputDestination) {
		this.outputDestination = outputDestination;
	}

	public Boolean getUseStdout() {
		return useStdout;
	}

	public void setUseStdout(Boolean useStdout) {
		this.useStdout = useStdout;
	}

	/**
	 * Creates an compressing {@link OutputStream}. The filename of the
	 * output-file will be ignored if useStdout equals true. The compression
	 * format can be specified by the class attribute compressionExtension.
	 * 
	 * @return compressing {@link OutputStream}
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	protected OutputStream getCompressorOutputStream() throws IOException {
		if (this.useStdout) {
			return System.out;
		} else {
			Path outputDirectory = Paths.get(this.outputDestination)
					.getParent();
			if (outputDirectory != null) {
				new File(outputDirectory.toString()).mkdirs();
			}
			OutputStream bufferedFileOutputStream = new BufferedOutputStream(
					new FileOutputStream(this.outputDestination
							+ this.compressionExtension), 1024 * 1024 * 5);

			OutputStream compressorOutputStream = null;
			switch (this.compressionExtension) {
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
						"Unsupported compression format: "
								+ compressionExtension);
			}

			OutputStream exportOutputStream;

			exportOutputStream = asynchronousOutputStream(compressorOutputStream);

			return exportOutputStream;
		}
	}

	/**
	 * Closes a Closeable and swallows any exceptions that might occur in the
	 * process.
	 * 
	 * @param closeable
	 */
	static private void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ignored) {
			}
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
	protected static OutputStream asynchronousOutputStream(
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
}
