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
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

/**
 * Standard implementation of {@link WebResourceFetcher}.
 *
 * @author Markus Kroetzsch
 *
 */
public class WebResourceFetcherImpl implements WebResourceFetcher {

	protected static String userAgent = "Wikidata Toolkit; Java "
			+ System.getProperty("java.version");

	protected static Proxy proxy = null;

	/**
	 * Returns the proxy that will be used for all requests made by Wikidata
	 * Toolkit.
	 *
	 * @return the proxy represented as java object
	 */
	public static Proxy getProxy() {
		return proxy;
	}

	/**
	 * Sets the proxy that will be used for alle requests made by Wikidata
	 * Toolkit. This should be set in own tools based on Wikidata Toolkit esp.
	 * when making large amounts of requests.
	 *
	 * @param proxy
	 *            the proxy represented as java object
	 */
	public static void setProxy(Proxy proxy) {
		WebResourceFetcherImpl.proxy = proxy;
	}

	/**
	 * Checks whether a proxy is set.
	 *
	 * @return True if a proxy is set, false, if there isn't set any proxy.
	 */
	public static boolean hasProxy() {
		return (proxy != null);
	}

	/**
	 * Returns the string that will be used to identify the user agent on all
	 * requests made by Wikidata Toolkit.
	 *
	 * @return the user agent string
	 */
	public static String getUserAgent() {
		return userAgent;
	}

	/**
	 * Sets the string that will be used to identify the user agent on all
	 * requests made by Wikidata Toolkit. This should be set in own tools based
	 * on Wikidata Toolkit esp. when making large amounts of requests.
	 *
	 * @param userAgent
	 *            the user agent string
	 */
	public static void setUserAgent(String userAgent) {
		WebResourceFetcherImpl.userAgent = userAgent;
	}

	/**
	 * Opens a basic URL connection for the given URL and performs basic
	 * configurations. In particular, it will set the User-Agent. The current
	 * proxy settings are also respected. For http(s) URLs, the result is a
	 * {@link HttpURLConnection}.
	 *
	 * @param url
	 *            the URL to open
	 * @return the URL connection to access this URL
	 * @throws IOException
	 */
	public static URLConnection getUrlConnection(URL url) throws IOException {
		URLConnection urlConnection;
		if (hasProxy()) {
			urlConnection = url.openConnection(proxy);
		} else {
			urlConnection = url.openConnection();
		}
		urlConnection.setRequestProperty("User-Agent", userAgent);
		return urlConnection;
	}

	@Override
	public InputStream getInputStreamForUrl(String urlString)
			throws IOException {
		URL url = new URL(urlString);
		URLConnection urlConnection = getUrlConnection(url);
		return urlConnection.getInputStream();
	}

}
