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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import org.wikidata.wdtk.util.CompressionType;
import org.wikidata.wdtk.util.WebResourceFetcher;

/**
 * Mock implementation of {@link WebResourceFetcher}.
 *
 * @author Markus Kroetzsch
 *
 */
public class MockWebResourceFetcher implements WebResourceFetcher {

	public final HashMap<String, byte[]> webResources;
	boolean returnFailingReaders;

	/**
	 * Constructor.
	 *
	 */
	public MockWebResourceFetcher() {
		this.webResources = new HashMap<>();
	}

	/**
	 * When set to true, every operation that returns reader or stream objects
	 * to access some resource will return objects that fail with exceptions
	 * when trying to read data. This can be used to simulate problems like
	 * failing network connections after opening an online resource.
	 *
	 * @param returnFailingReaders
	 *            whether read operations should fail
	 */
	public void setReturnFailingReaders(boolean returnFailingReaders) {
		this.returnFailingReaders = returnFailingReaders;
	}

	/**
	 * Defines the contents of a new web resource.
	 *
	 * @param url
	 *            the URL string
	 * @param contents
	 *            the string contents
	 * @throws IOException
	 */
	public void setWebResourceContents(String url, String contents)
			throws IOException {
		setWebResourceContents(url, contents, CompressionType.NONE);
	}

	/**
	 * Defines the contents of a new web resource.
	 *
	 * @param url
	 *            the URL string
	 * @param contents
	 *            the string contents
	 * @param compressionType
	 *            the compression to use on the mocked contents
	 * @throws IOException
	 */
	public void setWebResourceContents(String url, String contents,
			CompressionType compressionType) throws IOException {
		this.webResources.put(url, MockStringContentFactory.getBytesFromString(
				contents, compressionType));
	}

	/**
	 * Defines the contents of a new web resource by taking the string from a
	 * given (Java) resource.
	 *
	 * @param url
	 *            the URL string
	 * @param resource
	 *            the Java resource name
	 * @param resourceClass
	 *            the Class relative to which the resource should be resolved
	 *            (since resources are stored relative to a classpath); can
	 *            usually be obtained with getClass() from the calling object
	 * @throws IOException
	 *             if the Java resource could not be loaded
	 */
	public void setWebResourceContentsFromResource(String url, String resource,
			Class<?> resourceClass) throws IOException {
		setWebResourceContentsFromResource(url, resource, resourceClass,
				CompressionType.NONE);
	}

	/**
	 * Defines the contents of a new web resource by taking the string from a
	 * given (Java) resource, possibly using additional compression.
	 *
	 * @param url
	 *            the URL string
	 * @param resource
	 *            the Java resource name
	 * @param resourceClass
	 *            the Class relative to which the resource should be resolved
	 *            (since resources are stored relative to a classpath); can
	 *            usually be obtained with getClass() from the calling object
	 * @param compressionType
	 *            the compression to use on the mocked contents
	 * @throws IOException
	 *             if the Java resource could not be loaded
	 */
	public void setWebResourceContentsFromResource(String url, String resource,
			Class<?> resourceClass, CompressionType compressionType)
			throws IOException {
		URL resourceUrl = resourceClass.getResource(resource);
		String contents = MockStringContentFactory
				.getStringFromUrl(resourceUrl);
		setWebResourceContents(url, contents, compressionType);
	}

	@Override
	public InputStream getInputStreamForUrl(String urlString)
			throws IOException {
		return getInputStreamForMockWebResource(urlString);
	}

	/**
	 * Returns an input stream for the content mocked for given URL. It is
	 * checked that the URL is valid.
	 *
	 * @param urlString
	 * @return input stream for resource
	 * @throws IOException
	 */
	InputStream getInputStreamForMockWebResource(String urlString)
			throws IOException {
		if (!this.webResources.containsKey(urlString)) {
			throw new IOException("Inaccessible URL (not mocked): " + urlString);
		}

		if (this.returnFailingReaders) {
			return MockStringContentFactory.getFailingInputStream();
		} else {
			return MockStringContentFactory
					.newMockInputStream(this.webResources.get(urlString));
		}
	}

}
