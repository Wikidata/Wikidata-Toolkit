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

import static org.junit.Assert.*;

import org.junit.Test;

public class ReportTest {

	@Test
	public void testJsonOutput() {
		DumpProcessingAction action = new JsonSerializationAction();
		Report report = new Report();
		action.setReport(report);
		action.open();
		action.close();
		assertEquals(report.createReport(), "Finished serialization of 0 EntityDocuments in file {PROJECT}-{DATE}.json\n");
	}
	
	@Test
	public void testRdfOutput() {
		DumpProcessingAction action = new RdfSerializationAction();
		Report report = new Report();
		action.setReport(report);
		action.open();
		action.close();
		assertEquals(report.createReport(), "Finished serialization of 24 RDF triples in file null\n");
	}
	
//	@Test public 

}
