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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for directory manager implementation. We can only test the read-only
 * operation of this non-mock component, and only to some degree.
 *
 * @author Markus Kroetzsch
 *
 */
public class DirectoryManagerTest {

	DirectoryManagerImpl dm;

	@Before
	public void setUp() throws Exception {
		Path path = Paths.get(System.getProperty("user.dir"));
		dm = new DirectoryManagerImpl(path, true);
	}

	@Test
	public void testToString() {
		assertEquals(Paths.get(System.getProperty("user.dir")).toString(),
				dm.toString());
	}

	@Test(expected = IOException.class)
	public void MissingSubdirectoryReadOnly() throws IOException {
		dm.getSubdirectoryManager("1 2 3 not a subdirectory that exists in the test system, hopefully");
	}

	@Test(expected = IOException.class)
	public void OutputStreamReadOnly() throws IOException {
		dm.getOutputStreamForFile("file.txt");
	}

	@Test(expected = IOException.class)
	public void NoCreateFileStringReadOnly() throws IOException {
		dm.createFile("new-test-file.txt", "new contents");
	}

	@Test(expected = IOException.class)
	public void NoCreateFileInputStreamReadOnly() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(
				"new contents".getBytes(StandardCharsets.UTF_8));
		dm.createFile("new-test-file.txt", in);
	}

	@Test(expected = IOException.class)
	public void NoCreateFileAtomicInputStreamReadOnly() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(
				"new contents".getBytes(StandardCharsets.UTF_8));
		dm.createFileAtomic("new-test-file.txt", in);
	}

	@Test
	public void getCompressionInputStreamNone() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(
				"new contents".getBytes(StandardCharsets.UTF_8));
		assertEquals(in, dm.getCompressorInputStream(in, CompressionType.NONE));
	}

	@Test
	public void getCompressionInputStreamGzip() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter ow = new OutputStreamWriter(
				new GzipCompressorOutputStream(out), StandardCharsets.UTF_8);
		ow.write("Test data");
		ow.close();

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		InputStream cin = dm.getCompressorInputStream(in, CompressionType.GZIP);

		assertEquals("Test data",
				new BufferedReader(new InputStreamReader(cin)).readLine());
	}

	@Test
	public void getCompressionInputStreamBz2() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		OutputStreamWriter ow = new OutputStreamWriter(
				new BZip2CompressorOutputStream(out), StandardCharsets.UTF_8);
		ow.write("Test data");
		ow.close();

		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		InputStream cin = dm.getCompressorInputStream(in, CompressionType.BZ2);

		assertEquals("Test data",
				new BufferedReader(new InputStreamReader(cin)).readLine());
	}
}
