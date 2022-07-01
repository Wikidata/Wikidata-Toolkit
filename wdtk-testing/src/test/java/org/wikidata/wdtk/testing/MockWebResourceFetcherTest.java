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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class MockWebResourceFetcherTest {

	MockWebResourceFetcher mwrf;

	@Before
	public void setUp() throws Exception {
		mwrf = new MockWebResourceFetcher();
		mwrf.setWebResourceContents("http://example.com/test.html",
				"Line1\nLine2");
	}

	@Test
	public void inputStreamForHtml() throws IOException {
		String content = MockStringContentFactory.getStringFromInputStream(mwrf
				.getInputStreamForUrl("http://example.com/test.html"));
		assertEquals(content, "Line1\nLine2");
	}

	@Test
	public void setConcentsFromResource() throws IOException {
		mwrf.setWebResourceContentsFromResource(
				"http://example.com/resource.html", "/test.txt",
				this.getClass());
		String content = MockStringContentFactory.getStringFromInputStream(mwrf
				.getInputStreamForUrl("http://example.com/resource.html"));
		assertEquals(content, "This file is here\nto test resource loading.");
	}

	@Test
	public void inputStreamForHtmlFails() throws IOException {
		mwrf.setReturnFailingReaders(true);
		InputStream in = mwrf
				.getInputStreamForUrl("http://example.com/test.html");
		// We do not use @Test(expected = IOException.class) in order to check
		// if the exception is really thrown at the right moment.
		boolean exception = false;
		try {
			MockStringContentFactory.getStringFromInputStream(in);
		} catch (IOException e) {
			exception = true;
		}
		assertTrue(exception);
	}

	@Test(expected = IOException.class)
	public void readOnlyMockedUrls() throws IOException {
		mwrf.getInputStreamForUrl("http://not-mocked.com");
	}

}
