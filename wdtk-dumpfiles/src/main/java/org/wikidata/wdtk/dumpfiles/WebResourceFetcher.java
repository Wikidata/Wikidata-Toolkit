package org.wikidata.wdtk.dumpfiles;

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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.zip.GZIPInputStream;

/**
 * Class to access files on the Web.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class WebResourceFetcher {

	/**
	 * Get a BufferedReader for the document at the given URL. The reader should
	 * be closed after use.
	 * 
	 * @param urlString
	 *            the URL of the document
	 * @return BufferedReader for the requested document
	 * @throws IOException
	 *             if the document at the URL could not be opended or the URL
	 *             was invalid
	 */
	BufferedReader getBufferedReaderForUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
		return new BufferedReader(new InputStreamReader(url.openStream()));
	}

	/**
	 * Get a BufferedReader for the Gzip-compressed document at the given URL.
	 * The reader should be closed after use.
	 * 
	 * @param urlString
	 *            the URL of the gzipped document
	 * @return BufferedReader for the requested document
	 * @throws IOException
	 *             if the document at the URL could not be opended or the URL
	 *             was invalid
	 */
	BufferedReader getBufferedReaderForGzipUrl(String urlString)
			throws IOException {
		URL url = new URL(urlString);
		return new BufferedReader(new InputStreamReader(new GZIPInputStream(
				url.openStream())));
	}

	/**
	 * Get a ReadableByteChannel for the document at the given URL. This can be
	 * used for downloading. The channel should be closed after use.
	 * 
	 * @param urlString
	 *            the URL of the document
	 * @return ReadableByteChannel for the requested document
	 * @throws IOException
	 *             if the document at the URL could not be opended or the URL
	 *             was invalid
	 */
	ReadableByteChannel getReadableByteChannelForUrl(String urlString)
			throws IOException {
		URL url = new URL(urlString);
		return Channels.newChannel(url.openStream());
	}

}
