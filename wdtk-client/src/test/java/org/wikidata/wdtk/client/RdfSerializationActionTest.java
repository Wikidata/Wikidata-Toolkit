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

import org.junit.Test;
import org.wikidata.wdtk.rdf.RdfSerializer;

public class RdfSerializationActionTest {

	@Test
	public void testDefaults() {
		String[] args = new String[] { "-a", "rdf", "--rdftasks", "entities" };
		DumpProcessingOutputAction action = DumpProcessingOutputActionTest
				.getActionFromArgs(args);

		assertTrue(action instanceof RdfSerializationAction);
		assertTrue(action.needsSites());
		assertTrue(action.isReady());
		assertEquals(action.getActionName(), "RdfSerializationAction");
	}

	@Test
	public void testDefaultsNoTasks() {
		String[] args = new String[] { "-a", "rdf", "--stdout" };
		DumpProcessingOutputAction action = DumpProcessingOutputActionTest
				.getActionFromArgs(args);
		action.open();
		action.close();

		assertTrue(action instanceof RdfSerializationAction);
		assertFalse(action.needsSites());
		assertFalse(action.isReady());
	}

	@Test
	public void testSerializerSetup() {
		String[] args = new String[] { "-a", "rdf", "--stdout", "--rdftasks",
				"properties,labels" };
		RdfSerializationAction action = (RdfSerializationAction) DumpProcessingOutputActionTest
				.getActionFromArgs(args);
		action.open(); // creates and initializes serializer (prints to stdout)
		action.close(); // just to test that this causes no exceptions

		assertTrue(action.needsSites());
		assertEquals(action.serializer.getTasks(),
				RdfSerializer.TASK_PROPERTIES | RdfSerializer.TASK_LABELS);

	}
}
