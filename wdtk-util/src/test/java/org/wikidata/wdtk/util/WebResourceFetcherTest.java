package org.wikidata.wdtk.util;

/*
 * #%L
 * Wikidata Toolkit Utilities
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.junit.Test;

public class WebResourceFetcherTest {

	@Test
	public void testSetUserAgent() {
		WebResourceFetcherImpl.setUserAgent("My user agent");
		assertEquals("My user agent", WebResourceFetcherImpl.getUserAgent());
	}

	@Test
	public void testSetProxy() {
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"test.adress", 8080));
		WebResourceFetcherImpl.setProxy(proxy);
		assertTrue(WebResourceFetcherImpl.hasProxy());
		assertEquals(proxy, WebResourceFetcherImpl.getProxy());
	}
}
