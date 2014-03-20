package org.wikidata.wdtk.util;

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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Standard implementation of {@link WebResourceFetcher}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class WebResourceFetcherImpl implements WebResourceFetcher {

	// @Override
	// public BufferedReader getBufferedReaderForUrl(String urlString)
	// throws IOException {
	// return new BufferedReader(new InputStreamReader(
	// this.getInputStreamForUrl(urlString), StandardCharsets.UTF_8));
	// }
	//
	// @Override
	// public BufferedReader getBufferedReaderForGzipUrl(String urlString)
	// throws IOException {
	// return new BufferedReader(new InputStreamReader(
	// this.getInputStreamForGzipUrl(urlString),
	// StandardCharsets.UTF_8));
	// }

	@Override
	public InputStream getInputStreamForUrl(String urlString)
			throws IOException {
		URL url = new URL(urlString);
		return url.openStream();
	}

	@Override
	public InputStream getInputStreamForGzipUrl(String urlString)
			throws IOException {
		URL url = new URL(urlString);
		return new GZIPInputStream(url.openStream());
	}

}
