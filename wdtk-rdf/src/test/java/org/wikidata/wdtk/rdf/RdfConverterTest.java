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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.datamodel.implementation.SitesImpl;
import org.wikidata.wdtk.rdf.RdfConverter;
import org.wikidata.wdtk.rdf.TestObjectFactory;
import org.wikidata.wdtk.testing.MockStringContentFactory;

public class RdfConverterTest {

	ByteArrayOutputStream out;
	
	RdfWriter rdfWriter;
	RdfConverter rdfConverter;
	
	final TestObjectFactory objectFactory = new TestObjectFactory();
	
	public String getResourceFromFile(String fileName)
			throws IOException {
		return MockStringContentFactory.getStringFromUrl(this
				.getClass().getResource("/" + fileName));
	}
	
	public String removeBlankNodes(String rdf){
		StringBuilder builder = new StringBuilder();
		int pos = 0;
		do {
			pos = rdf.indexOf("node");
			if (pos != -1){
				builder.append(rdf.substring(0, pos));
				rdf = rdf.substring(pos + 15);
			}
		}while (pos != -1);
		builder.append(rdf);
		return builder.toString();
	}
	
	@Before
	public void setUp() throws Exception {
		out = new ByteArrayOutputStream();
		rdfWriter = new RdfWriter(RDFFormat.N3, out);
		rdfConverter = new RdfConverter(rdfWriter, new SitesImpl());
		rdfWriter.start();
	}

	@Test
	public void testWriteItemDocument() throws RDFHandlerException, IOException {
		org.wikidata.wdtk.datamodel.interfaces.ItemDocument document = objectFactory.createItemDocument();
		rdfConverter.writeItemDocument(document);
		rdfWriter.finish();
		assertEquals(removeBlankNodes(out.toString()), getResourceFromFile("ItemDocument.rdf"));
	}
	
	@Test
	public void testWritePropertyDocument() throws RDFHandlerException, IOException{
		org.wikidata.wdtk.datamodel.interfaces.PropertyDocument document = objectFactory.createEmptyPropertyDocument();
		rdfConverter.writePropertyDocument(document);
		rdfWriter.finish();
		assertEquals(removeBlankNodes(out.toString()), getResourceFromFile("EmptyPropertyDocument.rdf"));
	}
	
	@After
	public void clear() throws RDFHandlerException, IOException{
		out.close();
	}

}
