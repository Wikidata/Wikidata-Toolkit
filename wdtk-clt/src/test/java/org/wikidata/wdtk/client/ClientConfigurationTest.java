package org.wikidata.wdtk.client;

/*
 * #%L
 * Wikidata Toolkit Command-line Tool
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.wikidata.wdtk.rdf.RdfSerializer;

public class ClientConfigurationTest {

	@Test
	public void testReadConfigFile() throws IOException {
		String configFile = "src/test/resources/testConf.ini";
		String[] args = new String[] { "-c", configFile };
		ClientConfiguration config = new ClientConfiguration(args);

		assertTrue(config.getOfflineMode());
		assertTrue(config.isQuiet());
		assertEquals(config.getDumpLocation(), "dumps/wikidata/");

		assertEquals(config.getActions().size(), 2);
		assertTrue(config.getActions().get(0) instanceof RdfSerializationAction);
		assertTrue(config.getActions().get(1) instanceof JsonSerializationAction);
		RdfSerializationAction rdfAction = (RdfSerializationAction) config
				.getActions().get(0);
		JsonSerializationAction jsonAction = (JsonSerializationAction) config
				.getActions().get(1);

		assertTrue(rdfAction.useStdOut);
		assertEquals(rdfAction.compressionType,
				DumpProcessingOutputAction.COMPRESS_GZIP);
		assertEquals(rdfAction.outputDestination, "/tmp/wikidata-items.nt");
		assertEquals(rdfAction.conversionProperties, config);
		assertEquals(rdfAction.tasks, RdfSerializer.TASK_ITEMS
				| RdfSerializer.TASK_STATEMENTS | RdfSerializer.TASK_TERMS);

		assertFalse(jsonAction.useStdOut);
		assertEquals(jsonAction.compressionType,
				DumpProcessingOutputAction.COMPRESS_BZ2);
		assertEquals(jsonAction.outputDestination, "/tmp/wikidata-dump.json");
		assertEquals(jsonAction.conversionProperties, config);
	}

	@Test
	public void testDefaultArguments() {
		String[] args = new String[] {};
		ClientConfiguration config = new ClientConfiguration(args);
		assertFalse(config.getOfflineMode());
		assertEquals(config.getDumpLocation(), null);
		assertFalse(config.isQuiet());
	}

	@Test
	public void testUnknownAction() {
		String[] args = new String[] { "-a", "notImplemented" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertEquals(config.getActions().size(), 0);
	}

	@Test
	public void testUnknownArguments() {
		String[] args = new String[] { "--unknown", "-foo" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertFalse(config.getOfflineMode());
		assertEquals(config.getDumpLocation(), null);
		assertFalse(config.isQuiet());
	}

	@Test
	public void testDumpLocationArgumentsShort() {
		String[] args = new String[] { "-d", "dumps/wikidata/" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertEquals(config.getDumpLocation(), "dumps/wikidata/");
	}

	@Test
	public void testDumpLocationArgumentsLong() {
		String[] args = new String[] { "--dumps", "dumps/wikidata/" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertEquals(config.getDumpLocation(), "dumps/wikidata/");
	}

	@Test
	public void testOfflineModeArgumentsShort() {
		String[] args = new String[] { "-n" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertTrue(config.getOfflineMode());
	}

	@Test
	public void testOfflineModeArgumentsLong() {
		String[] args = new String[] { "--offline" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertTrue(config.getOfflineMode());
	}

	@Test
	public void testStdOutOutputArgumentsShort() {
		String[] args = new String[] { "-a", "json", "-s" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertTrue(config.isQuiet());
	}

	@Test
	public void testStdOutOutputArgumentsLong() {
		String[] args = new String[] { "--action", "json", "--stdout" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertTrue(config.isQuiet());
	}

	@Test
	public void testQuietArgumentsShort() {
		String[] args = new String[] { "-q" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertTrue(config.isQuiet());
	}

	@Test
	public void testQuietArgumentsLong() {
		String[] args = new String[] { "--quiet" };
		ClientConfiguration config = new ClientConfiguration(args);
		assertTrue(config.isQuiet());
	}

}
