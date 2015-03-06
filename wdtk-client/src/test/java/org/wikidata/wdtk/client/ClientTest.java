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

import java.io.IOException;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;
import org.junit.Test;

public class ClientTest {

	@Test
	public void testDefaultLoggingConfig() throws ParseException, IOException {
		String[] args = new String[] {};
		Client client = new Client(args);
		client.performActions(); // print help

		assertEquals(Client.consoleAppender.getThreshold(), Level.INFO);
		assertEquals(Client.errorAppender.getThreshold(), Level.WARN);
	}

	@Test
	public void testQuietStdOutLoggingConfig() throws ParseException,
			IOException {
		String[] args = new String[] { "-a", "json", "-s" };
		new Client(args);

		assertEquals(Client.consoleAppender.getThreshold(), Level.OFF);
		assertEquals(Client.errorAppender.getThreshold(), Level.WARN);
	}

	@Test
	public void testQuietLoggingConfig() throws ParseException, IOException {
		String[] TEST_ARGS = new String[] { "-a", "json", "-q" };
		new Client(TEST_ARGS);

		assertEquals(Client.consoleAppender.getThreshold(), Level.OFF);
		assertEquals(Client.errorAppender.getThreshold(), Level.WARN);
	}

	@Test
	public void testJsonOutput() {
		DumpProcessingAction action = new JsonSerializationAction();
		action.open();
		action.close();
		assertEquals(action.getReport(),
				"Finished serialization of 0 EntityDocuments in file {PROJECT}-{DATE}.json");
	}

	@Test
	public void testRdfOutput() {
		DumpProcessingAction action = new RdfSerializationAction();
		action.open();
		action.close();
		assertEquals(action.getReport(),
				"Finished serialization of 24 RDF triples in file null");
	}

}
