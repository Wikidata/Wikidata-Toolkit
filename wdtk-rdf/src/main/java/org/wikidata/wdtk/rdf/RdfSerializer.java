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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocumentsSerializer;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
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

	final Model model = new LinkedHashModel();
	RDFWriter writer;

	RdfConverter rdfConverter;

	Map<String, String> namespaces = new HashMap<String, String>();

	void addNamespaces(Map<String, String> namespaces) {
		this.namespaces.putAll(namespaces);
	}

	void clearNamespaces() {
		this.namespaces.clear();
	}

	void writeStatements(Set<org.openrdf.model.Statement> statements)
			throws RDFHandlerException {
		for (Statement st : statements) {
			writer.handleStatement(st);

		}
	}

	void setupNamespaces() {
		for (String key : this.namespaces.keySet()) {
			try {
				writer.handleNamespace(key, this.namespaces.get(key));
			} catch (RDFHandlerException e) {
				logger.error(e.toString());
			}
		}
	}

	public RdfSerializer(RDFFormat format, OutputStream output)
			throws RDFHandlerException {
		this.writer = Rio.createWriter(format, output);
	}

	@Override
	public void processItemDocument(ItemDocument itemDocument) {
		try {
			this.rdfConverter.getRdfForItemDocument(itemDocument);
		} catch (RDFHandlerException e) {
			logger.error(e.toString());
		}

	}

	@Override
	public void processPropertyDocument(PropertyDocument propertyDocument) {
		try {
			this.rdfConverter.getRdfForPropertyDocument(propertyDocument);
		} catch (RDFHandlerException e) {
			logger.error(e.toString());
		}
	}

	@Override
	public void finishProcessingEntityDocuments() {
		// do nothing
	}

	@Override
	public void startSerialization() {

		rdfConverter = new RdfConverter(writer);

		addNamespaces(this.rdfConverter.getNamespaces());

		setupNamespaces();

		try {
			writer.startRDF();
		} catch (RDFHandlerException e1) {
			logger.error(e1.toString());
		}

		try {
			writeStatements(this.rdfConverter.getBasicDefinitions());
		} catch (RDFHandlerException e) {
			logger.error(e.toString());
		}
	}

	@Override
	public void finishSerialization() {
		try {
			this.writer.endRDF();
		} catch (RDFHandlerException e) {
			logger.error(e.toString());
		}
	}

}
