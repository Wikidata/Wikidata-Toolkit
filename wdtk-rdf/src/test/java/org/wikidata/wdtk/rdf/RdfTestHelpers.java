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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.wikidata.wdtk.testing.MockStringContentFactory;

public class RdfTestHelpers {
	public static String getResourceFromFile(String fileName)
			throws IOException {
		return MockStringContentFactory.getStringFromUrl(RdfTestHelpers.class
				.getResource("/" + fileName));
	}

	public static Model parseRdf(String rdfResource) throws RDFParseException,
			RDFHandlerException, IOException {
		InputStream inStream = new ByteArrayInputStream(rdfResource.getBytes());
		RDFParser parser = Rio.createParser(RDFFormat.TURTLE);
		Model graph = new LinkedHashModel();
		parser.setRDFHandler(new StatementCollector(graph));
		parser.parse(inStream, "http://test/");
		return graph;
	}
}
