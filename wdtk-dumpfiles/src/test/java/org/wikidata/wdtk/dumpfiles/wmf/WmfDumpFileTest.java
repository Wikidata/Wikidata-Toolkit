package org.wikidata.wdtk.dumpfiles.wmf;

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

import org.junit.Test;
import org.wikidata.wdtk.util.CompressionType;

import static org.junit.Assert.assertEquals;

public class WmfDumpFileTest {

	@Test
	public void getDumpFileCompressionType() {
		assertEquals(WmfDumpFile.getDumpFileCompressionType("foo.tar.gz"), CompressionType.GZIP);
		assertEquals(WmfDumpFile.getDumpFileCompressionType("bar.txt.bz2"), CompressionType.BZ2);
		assertEquals(WmfDumpFile.getDumpFileCompressionType("baz.txt"), CompressionType.NONE);
		assertEquals(WmfDumpFile.getDumpFileCompressionType("bat.txt"), CompressionType.NONE);
	}
}
