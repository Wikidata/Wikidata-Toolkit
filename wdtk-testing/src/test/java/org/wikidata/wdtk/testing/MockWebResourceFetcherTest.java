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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.testing.MockStringContentFactory;
import org.wikidata.wdtk.testing.MockWebResourceFetcher;

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
	public void inputStreamForHtml() throws IOException {
		String content = MockStringContentFactory.getStringFromInputStream(mwrf
				.getInputStreamForUrl("http://example.com/test.html"));
		assertEquals(content, "Line1\nLine2");
	}

	@Test
	public void inputStreamForGzip() throws IOException {
		String content = MockStringContentFactory.getStringFromInputStream(mwrf
				.getInputStreamForGzipUrl("http://example.com/test.gzip"));
		assertEquals(content, "Line1\nLine2");
	}

	@Test(expected = IllegalArgumentException.class)
	public void readOnlyGzipFiles() throws IOException {
		mwrf.getInputStreamForGzipUrl("http://example.com/test.html");
	}

	@Test(expected = IOException.class)
	public void readOnlyMockedUrls() throws IOException {
		mwrf.getInputStreamForUrl("http://not-mocked.com");
	}

}
