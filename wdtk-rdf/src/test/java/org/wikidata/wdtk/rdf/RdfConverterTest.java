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

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Model;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.wikidata.wdtk.datamodel.implementation.SitesImpl;
import org.wikidata.wdtk.rdf.RdfConverter;
import org.wikidata.wdtk.rdf.TestObjectFactory;
import org.wikidata.wdtk.testing.MockStringContentFactory;

public class RdfConverterTest {

	ByteArrayOutputStream out;

	RdfWriter rdfWriter;
	RdfConverter rdfConverter;

	final TestObjectFactory objectFactory = new TestObjectFactory();

	public String getResourceFromFile(String fileName) throws IOException {
		return MockStringContentFactory.getStringFromUrl(this.getClass()
				.getResource("/" + fileName));
	}

	public Model parseRdf(String rdfResource) throws RDFParseException,
			RDFHandlerException, IOException {
		InputStream inStream = new ByteArrayInputStream(rdfResource.getBytes());
		RDFParser parser = Rio.createParser(RDFFormat.N3);
		parser.parse(inStream, "http://test/");
		Model graph = new LinkedHashModel();
		parser.setRDFHandler(new StatementCollector(graph));
		return graph;
	}

	@Before
	public void setUp() throws Exception {
		out = new ByteArrayOutputStream();
		rdfWriter = new RdfWriter(RDFFormat.N3, out);
		rdfConverter = new RdfConverter(rdfWriter, new SitesImpl());
		rdfWriter.start();
	}

	@Test
	public void testWriteItemDocument() throws RDFHandlerException,
			IOException, RDFParseException {
		org.wikidata.wdtk.datamodel.interfaces.ItemDocument document = objectFactory
				.createItemDocument();
		rdfConverter.writeItemDocument(document);
		rdfWriter.finish();
		Model model = parseRdf(out.toString());
		assertEquals(model, parseRdf(getResourceFromFile("ItemDocument.rdf")));
	}

	@Test
	public void testWritePropertyDocument() throws RDFHandlerException,
			RDFParseException, IOException {
		org.wikidata.wdtk.datamodel.interfaces.PropertyDocument document = objectFactory
				.createEmptyPropertyDocument();
		rdfConverter.writePropertyDocument(document);
		rdfWriter.finish();
		Model model = parseRdf(out.toString());
		assertEquals(model,
				parseRdf(getResourceFromFile("EmptyPropertyDocument.rdf")));
	}

	@After
	public void clear() throws RDFHandlerException, IOException {
		out.close();
	}

}
