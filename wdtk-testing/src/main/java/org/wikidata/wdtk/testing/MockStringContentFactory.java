package org.wikidata.wdtk.testing;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.mockito.Mockito;
import org.wikidata.wdtk.util.CompressionType;

/**
 * Helper class to create BufferedReaders and InputStreams with predefined
 * contents.
 *
 * @author Markus Kroetzsch
 *
 */
public class MockStringContentFactory {

	/**
	 * Returns a new InputStream that gives access to the contents as given in
	 * the input string, encoded in UTF-8.
	 *
	 * @param contents
	 * @return an input stream for the given string
	 */
	public static InputStream newMockInputStream(String contents) {
		return new ByteArrayInputStream(
				contents.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Returns a new InputStream that gives access to the contents as given in
	 * the input bytes.
	 *
	 * @param contents
	 * @return an input stream for the given bytes
	 */
	public static InputStream newMockInputStream(byte[] contents) {
		return new ByteArrayInputStream(contents);
	}

	/**
	 * Loads a string from the file at the given URL. This should only be used
	 * for relatively small files, obviously. The file contents is interpreted
	 * as UTF-8.
	 *
	 * @param url
	 * @return string contents of the file at the given URL
	 * @throws IOException
	 *             if the URL could not be resolved or the file could not be
	 *             read
	 */
	public static String getStringFromUrl(URL url) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(
				url.openStream(), StandardCharsets.UTF_8));
		return getStringFromBufferedReader(br);
	}

	/**
	 * Loads a string from the given buffered reader. Newline will be appended
	 * after each line but the last.
	 *
	 * @param bufferedReader
	 * @return string contents of the buffered reader
	 * @throws IOException
	 *             if it was not possible to read from the buffered reader
	 */
	public static String getStringFromBufferedReader(
			BufferedReader bufferedReader) throws IOException {
		StringBuilder contentsBuilder = new StringBuilder();
		String line;
		boolean firstLine = true;
		while ((line = bufferedReader.readLine()) != null) {
			if (firstLine) {
				firstLine = false;
			} else {
				contentsBuilder.append("\n");
			}
			contentsBuilder.append(line);
		}
		return contentsBuilder.toString();
	}

	/**
	 * Loads a string from the given input stream. UTF-8 encoding will be
	 * assumed. Newline will be appended after each line but the last.
	 *
	 * @param inputStream
	 * @return string contents of the input stream
	 * @throws IOException
	 *             if it was not possible to read from the buffered reader
	 */
	public static String getStringFromInputStream(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream, StandardCharsets.UTF_8));
		String result = MockStringContentFactory
				.getStringFromBufferedReader(bufferedReader);
		bufferedReader.close();
		return result;
	}

	/**
	 * Returns an input stream that will throw IOExceptions on common reading
	 * operations.
	 *
	 * @return input stream that fails on reading
	 */
	public static InputStream getFailingInputStream() {
		InputStream is = Mockito.mock(InputStream.class);
		try {
			Mockito.doThrow(new IOException()).when(is).read();
			Mockito.doThrow(new IOException()).when(is).read(Mockito.anyObject());
			Mockito.doThrow(new IOException())
					.when(is)
					.read(Mockito.anyObject(), Mockito.anyInt(), Mockito.anyInt());
		} catch (IOException e) {
			throw new RuntimeException(
					"Mockito should not throw anything here. Strange.", e);
		}
		return is;
	}

	/**
	 * Turns a string into a sequence of bytes, possibly compressed. In any
	 * case, the character encoding used for converting the string into bytes is
	 * UTF8.
	 *
	 * @param string
	 * @param compressionType
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBytesFromString(String string,
			CompressionType compressionType) throws IOException {
		switch (compressionType) {
		case NONE:
			return string.getBytes(StandardCharsets.UTF_8);
		case BZ2:
		case GZIP:
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStreamWriter ow;
			if (compressionType == CompressionType.GZIP) {
				ow = new OutputStreamWriter(
						new GzipCompressorOutputStream(out),
						StandardCharsets.UTF_8);
			} else {
				ow = new OutputStreamWriter(
						new BZip2CompressorOutputStream(out),
						StandardCharsets.UTF_8);
			}

			ow.write(string);
			ow.close();
			return out.toByteArray();
		default:
			throw new RuntimeException("Unknown compression type "
					+ compressionType);
		}
	}
}
