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

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Before;
import org.junit.Test;

public class MockWebResourceFetcherTest {

	MockWebResourceFetcher mwrf;

	@Before
	public void setUp() throws Exception {
		mwrf = new MockWebResourceFetcher();
		mwrf.setWebResourceContents("http://example.com/test.html",
				"Line1\nLine2", MockWebResourceFetcher.TYPE_HTML);
		mwrf.setWebResourceContents("http://example.com/test.gzip",
				"Line1\nLine2", MockWebResourceFetcher.TYPE_GZIP);
	}

	@Test
	public void bufferedReaderForHtml() throws IOException {
		BufferedReader br = mwrf
				.getBufferedReaderForUrl("http://example.com/test.html");
		String firstLine = br.readLine();
		String secondLine = br.readLine();
		String thirdLine = br.readLine();
		assertEquals(firstLine, "Line1");
		assertEquals(secondLine, "Line2");
		assertEquals(thirdLine, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void readOnlyHtmlFiles() throws IOException {
		mwrf.getBufferedReaderForUrl("http://example.com/test.gzip");
	}

	@Test
	public void bufferedReaderForGzip() throws IOException {
		BufferedReader br = mwrf
				.getBufferedReaderForGzipUrl("http://example.com/test.gzip");
		String firstLine = br.readLine();
		String secondLine = br.readLine();
		String thirdLine = br.readLine();
		assertEquals(firstLine, "Line1");
		assertEquals(secondLine, "Line2");
		assertEquals(thirdLine, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void readOnlyGzipFiles() throws IOException {
		mwrf.getBufferedReaderForGzipUrl("http://example.com/test.html");
	}

	@Test(expected = IOException.class)
	public void readOnlyMockedUrls() throws IOException {
		mwrf.getInputStreamForUrl("http://not-mocked.com");
	}

	@Test
	public void inputStreamForUrl() throws IOException {
		InputStream inputStream = mwrf
				.getInputStreamForUrl("http://example.com/test.html");

		BufferedReader br = new BufferedReader(new InputStreamReader(
				inputStream));
		String firstLine = br.readLine();
		String secondLine = br.readLine();
		String thirdLine = br.readLine();
		assertEquals(firstLine, "Line1");
		assertEquals(secondLine, "Line2");
		assertEquals(thirdLine, null);
	}

}
