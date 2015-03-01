package org.wikidata.wdtk.util;

/*
 * #%L
 * Wikidata Toolkit utilities
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
import java.net.HttpURLConnection;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebInputStream {

	static final Logger logger = LoggerFactory.getLogger(WebInputStream.class);

	/**
	 * Creates a new progress-tracking {@link InputStream} using the supplied
	 * {@link URL}.
	 * 
	 * @param url
	 * @throws IOException
	 */
	public static InputStream create(URL url) throws IOException {
		InputStream inputStream = url.openStream();
		String prefix = "Retrieved ";
		String postfix = "% of " + url.toString();
		long size = getContentLength(url);
		return new ProgressLoggingInputStream(inputStream, prefix, postfix,
				size);
	}

	/**
	 * Creates a new progress-tracking {@link InputStream} using the supplied
	 * URL.
	 * 
	 * @param url
	 * @throws IOException
	 */
	public static InputStream create(String urlString) throws IOException {
		return create(new URL(urlString));
	}

	private static long getContentLength(URL url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("HEAD");
			connection.setDoInput(true);
			connection.setDoOutput(false);
			long contentLength = connection.getHeaderFieldLong(
					"Content-Length", -1);
			connection.disconnect();
			return contentLength;
		} catch (Exception e) {
			logger.error("Determining content length", e);
			return -1;
		}
	}

}
