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
import java.util.HashMap;

/**
 * Mock implementation of {@link WebResourceFetcher}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class MockWebResourceFetcher implements WebResourceFetcher {

	public static final String TYPE_HTML = "html";
	public static final String TYPE_GZIP = "gz";

	final HashMap<String, String> webResources;
	final HashMap<String, String> webResourceTypes;

	public MockWebResourceFetcher() {
		this.webResources = new HashMap<String, String>();
		this.webResourceTypes = new HashMap<String, String>();
	}

	/**
	 * Define the contents of a new web resource. The contents type is used to
	 * define which methods are allowed to access this contents. All contents is
	 * stored as plain text, but contents of type {@link #TYPE_GZIP} can only be
	 * accessed when using a suitable method, etc.
	 * 
	 * @param url
	 *            the URL string
	 * @param contents
	 *            the string contents
	 * @param contentsType
	 *            one of the predefined type constants
	 */
	public void setWebResourceContents(String url, String contents,
			String contentsType) {
		this.webResources.put(url, contents);
		this.webResourceTypes.put(url, contentsType);
	}

	@Override
	public BufferedReader getBufferedReaderForUrl(String urlString)
			throws IOException {
		if (!MockWebResourceFetcher.TYPE_HTML.equals(this.webResourceTypes
				.get(urlString))) {
			throw new IllegalArgumentException("Cannot access non-html content");
		}
		return getBufferedReaderForMockWebResource(urlString);
	}

	@Override
	public BufferedReader getBufferedReaderForGzipUrl(String urlString)
			throws IOException {
		if (!MockWebResourceFetcher.TYPE_GZIP.equals(this.webResourceTypes
				.get(urlString))) {
			throw new IllegalArgumentException("Cannot access non-gzip content");
		}
		return getBufferedReaderForMockWebResource(urlString);
	}

	BufferedReader getBufferedReaderForMockWebResource(String urlString)
			throws IOException {
		assert (this.webResources.containsKey(urlString));
		return MockStringContentFactory
				.newMockBufferedReader(this.webResources.get(urlString));
	}

	@Override
	public InputStream getInputStreamForUrl(String urlString)
			throws IOException {
		if (!this.webResources.containsKey(urlString)) {
			throw new IOException("Invalid mock URL");
		}
		return MockStringContentFactory.newMockInputStream(this.webResources
				.get(urlString));
	}

}
