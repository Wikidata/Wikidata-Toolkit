package org.wikidata.wdtk.client;

/*
 * #%L
 * Wikidata Toolkit Command-line Tool
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test for general functionality of the abstract class
 * {@link DumpProcessingOutputActionTest}. We use
 * {@link JsonSerializationAction} as an example implementation to do the tests.
 *
 * @author Markus Kroetzsch
 *
 */
public class DumpProcessingOutputActionTest {

	public static DumpProcessingOutputAction getActionFromArgs(String[] args) {
		ClientConfiguration config = new ClientConfiguration(args);
		return (DumpProcessingOutputAction) config.getActions().get(0);
	}

	@Test
	public void testDefaults() {
		String[] args = new String[] { "-a", "json" };
		DumpProcessingOutputAction action = getActionFromArgs(args);

		assertEquals(action.compressionType,
				DumpProcessingOutputAction.COMPRESS_NONE);
		assertFalse(action.useStdOut);
	}

	@Test
	public void testCompressionOutputArgumentsShort() {
		String[] args = new String[] { "-a", "json", "-z", "bz2" };
		DumpProcessingOutputAction action = getActionFromArgs(args);

		assertEquals(action.compressionType,
				DumpProcessingOutputAction.COMPRESS_BZ2);
	}

	@Test
	public void testCompressionOutputArgumentsLong() {
		String[] args = new String[] { "-a", "json", "--compression", "GZ" };
		DumpProcessingOutputAction action = getActionFromArgs(args);

		assertEquals(action.compressionType,
				DumpProcessingOutputAction.COMPRESS_GZIP);
	}

	@Test
	public void testStdOutOutputArgumentsShort() {
		String[] args = new String[] { "-a", "json", "-s" };
		DumpProcessingOutputAction action = getActionFromArgs(args);

		assertTrue(action.useStdOut);
	}

	@Test
	public void testStdOutOutputArgumentsLong() {
		String[] args = new String[] { "-a", "json", "--stdout" };
		DumpProcessingOutputAction action = getActionFromArgs(args);

		assertTrue(action.useStdOut);
	}

	@Test
	public void testInsertDumpInformation() {
		DumpProcessingOutputAction action = new JsonSerializationAction();
		action.setDumpInformation("wikidata", "20150131");
		String result = action
				.insertDumpInformation("{PROJECT}-{DATE}-dump.json");
		assertEquals(result, "wikidata-20150131-dump.json");
	}

}
