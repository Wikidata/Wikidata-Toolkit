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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Standard implementation of {@link WebResourceFetcher}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class WebResourceFetcherImpl implements WebResourceFetcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.dumpfiles.WebResourceFetcher#getBufferedReaderForUrl
	 * (java.lang.String)
	 */
	@Override
	public BufferedReader getBufferedReaderForUrl(String urlString)
			throws IOException {
		URL url = new URL(urlString);
		return new BufferedReader(new InputStreamReader(url.openStream()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.dumpfiles.WebResourceFetcher#getBufferedReaderForGzipUrl
	 * (java.lang.String)
	 */
	@Override
	public BufferedReader getBufferedReaderForGzipUrl(String urlString)
			throws IOException {
		URL url = new URL(urlString);
		return new BufferedReader(new InputStreamReader(new GZIPInputStream(
				url.openStream())));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wikidata.wdtk.dumpfiles.WebResourceFetcher#getReadableByteChannelForUrl
	 * (java.lang.String)
	 */
	@Override
	public InputStream getInputStreamForUrl(String urlString)
			throws IOException {
		URL url = new URL(urlString);
		return url.openStream();
	}

}
