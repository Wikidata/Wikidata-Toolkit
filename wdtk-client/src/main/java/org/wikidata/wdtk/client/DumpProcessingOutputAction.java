package org.wikidata.wdtk.client;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.Sites;

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
 * Abstract implementation of {@link DumpProcessingAction} that represents
 * actions which generate outputs (in the form of files). It supports options
 * {@link #OPTION_DESTINATION}, {@link #OPTION_COMPRESSION}, and
 * {@link #OPTION_USE_STDOUT}. Moreover, it provides some static helper
 * functions for opening files for writing.
 *
 * @author Michael Günther
 * @author Markus Kroetzsch
 */
public abstract class DumpProcessingOutputAction implements
		DumpProcessingAction {

	static final Logger logger = LoggerFactory
			.getLogger(DumpProcessingAction.class);

	/**
	 * Name of the option used to define that output be compressed in a
	 * particular way. By default, output is not compressed. When using
	 * compression, the output file will be extended with a suitable file
	 * extension.
	 */
	public static final String OPTION_COMPRESSION = "compression";
	/**
	 * Name of the option to set the destination file name (may include path)
	 * for outputs generated by this action. If not given, actions should pick a
	 * default output file.
	 */
	public static final String OPTION_DESTINATION = "output";
	/**
	 * Name of the option to redirect output to stdout. This is achieved by
	 * setting the value to the string "true". Other values are ignored.
	 */
	public static final String OPTION_USE_STDOUT = "stdout";

	public static final String COMPRESS_BZ2 = "bz2";
	public static final String COMPRESS_GZIP = "gz";
	public static final String COMPRESS_NONE = "";

	/**
	 * The {@link Sites} object if provided.
	 *
	 * @see #needsSites()
	 */
	protected Sites sites;

	/**
	 * For operations that generate output, this is the name of the output file,
	 * or null to use the default file name for the given options.
	 */
	protected String outputDestination = null;

	/**
	 * Specifies if output should be redirected to stdout rather than being
	 * written to a file.
	 */
	protected boolean useStdOut = false;

	/**
	 * String to indicate the output compression to be used, if any.
	 */
	protected String compressionType = COMPRESS_NONE;

	/**
	 * Date stamp of the dump to be processed.
	 */
	protected String dateStamp = "UNKNOWN";
	/**
	 * String name of the site that the processed dump file comes from.
	 */
	protected String project = "UNKNOWN";

	@Override
	public void setSites(Sites sites) {
		this.sites = sites;
	}

	@Override
	public boolean setOption(String option, String value) {
		switch (option) {
		case OPTION_DESTINATION:
			this.outputDestination = value;
			return true;
		case OPTION_COMPRESSION:
			this.compressionType = value.toLowerCase();
			return true;
		case OPTION_USE_STDOUT:
			this.useStdOut = (value == null) || "true".equals(value);
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean useStdOut() {
		return this.useStdOut;
	}

	@Override
	public void setDumpInformation(String project, String dateStamp) {
		this.project = project;
		this.dateStamp = dateStamp;
	}

	public String insertDumpInformation(String pattern) {
		return pattern.replace("{DATE}", this.dateStamp).replace("{PROJECT}",
				this.project);
	}

	/**
	 * Creates an compressing {@link OutputStream}.
	 *
	 * @param useStdOut
	 *            if true, {@link System#out} is returned and the other
	 *            parameters are ignored
	 * @param filePath
	 *            the string name of the output file, possibly including path
	 *            information
	 * @param compressionType
	 *            a string that refers to a type of output compression or the
	 *            empty string (no compression); a suitable file extension will
	 *            be added to the output file
	 *
	 * @return compressing {@link OutputStream}
	 * @throws IOException
	 *             if there were problems opening the required streams
	 */
	protected static OutputStream getOutputStream(boolean useStdOut,
			String filePath, String compressionType) throws IOException {
		if (useStdOut) {
			return System.out;
		}

		if (!compressionType.isEmpty()) {
			filePath += "." + compressionType;
		}

		Path outputDirectory = Paths.get(filePath).getParent();
		if (outputDirectory != null) {
			new File(outputDirectory.toString()).mkdirs();
		}
		OutputStream bufferedFileOutputStream = new BufferedOutputStream(
				new FileOutputStream(filePath), 1024 * 1024 * 5);

		switch (compressionType) {
		case COMPRESS_BZ2:
			return getAsynchronousOutputStream(new BZip2CompressorOutputStream(
					bufferedFileOutputStream));
		case COMPRESS_GZIP:
			GzipParameters gzipParameters = new GzipParameters();
			gzipParameters.setCompressionLevel(7);
			return getAsynchronousOutputStream(new GzipCompressorOutputStream(
					bufferedFileOutputStream, gzipParameters));
		case COMPRESS_NONE:
			return bufferedFileOutputStream;
		default:
			bufferedFileOutputStream.close();
			throw new IllegalArgumentException(
					"Unsupported compression format: " + compressionType);
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
	protected static OutputStream getAsynchronousOutputStream(
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
	private static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ignored) {
				logger.error("Failed to close output stream: "
						+ ignored.getMessage());
			}
		}
	}
}