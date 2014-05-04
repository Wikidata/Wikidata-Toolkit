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

import java.io.OutputStream;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentsSerializer;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.Sites;
import org.wikidata.wdtk.datamodel.json.JsonSerializer;

/**
 * This class implements {@link EntityDocumentsSerializer} to provide a RDF
 * serializer to render RDF graphs of {@link EntityDocument} objects.
 * 
 * @author Michael Günther
 * 
 */
public class RdfSerializer implements EntityDocumentsSerializer {

	static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

	RdfConverter rdfConverter;
	RdfWriter rdfWriter;

	/**
	 * Creates a new RDF serializer for the specified format and output stream.
	 * 
	 * @param format
	 *            RDF format, such as RDFFormat.TURTLE
	 * @param output
	 *            the output stream to write to
	 * @param sites
	 *            information about site links
	 */
	public RdfSerializer(RDFFormat format, OutputStream output, Sites sites) {
		this.rdfWriter = new RdfWriter(format, output);
		this.rdfConverter = new RdfConverter(this.rdfWriter, sites);
	}

	/**
	 * Returns the number of triples that have been written so far.
	 * 
	 * @return number of triples
	 */
	public long getTripleCount() {
		return this.rdfWriter.getTripleCount();
	}

	@Override
	public void startSerialization() {
		try {
			this.rdfWriter.start();
			this.rdfConverter.writeNamespaceDeclarations();
			this.rdfConverter.writeBasicDeclarations();
		} catch (RDFHandlerException e) { // we cannot recover here
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		try {
			this.rdfConverter.writeItemDocument(itemDocument);
		} catch (RDFHandlerException e) { // we cannot recover here
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		try {
			this.rdfConverter.writePropertyDocument(propertyDocument);
		} catch (RDFHandlerException e) { // we cannot recover here
			throw new RuntimeException(e.toString(), e);
		}
	}

	@Override
	public void finishProcessingEntityDocuments() {
		// do nothing
	}

	@Override
	public void finishSerialization() {
		try {
			this.rdfWriter.finish();
		} catch (RDFHandlerException e) { // we cannot recover here
			throw new RuntimeException(e.toString(), e);
		}
	}

}
