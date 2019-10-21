package org.wikidata.wdtk.rdf;

/*
 * #%L
 * Wikidata Toolkit RDF
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.wikidata.wdtk.datamodel.implementation.SitesImpl;

public class RdfSerializerTest {

	final TestObjectFactory objectFactory = new TestObjectFactory();

	ByteArrayOutputStream out;

	RdfSerializer rdfSerializer;

	@Before
	public void setUp() {
		this.out = new ByteArrayOutputStream();
		this.rdfSerializer = new RdfSerializer(RDFFormat.TURTLE, this.out,
				new SitesImpl(), new MockPropertyRegister());

	}

	@Test
	public void testSerialization() throws RDFParseException,
			RDFHandlerException, IOException {
		this.rdfSerializer.open();
		this.rdfSerializer.processItemDocument(this.objectFactory
				.createItemDocument());
		this.rdfSerializer.close();
		Model model = RdfTestHelpers.parseRdf(this.out.toString());
		assertEquals(RdfTestHelpers.parseRdf(RdfTestHelpers
				.getResourceFromFile("completeRDFDocument.rdf")), model);
	}

}
