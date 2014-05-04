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

import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;

/**
 * This class provides methods for writing RDF data to an output stream. It
 * encapsulates many details of the RDF library we use. It also provides a
 * unique point at which statistics about the number of triples etc. can be
 * gathered.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class RdfWriter {

	final ValueFactory factory = ValueFactoryImpl.getInstance();
	RDFWriter writer;

	long tripleCount = 0;

	public RdfWriter(RDFFormat format, OutputStream output)
			throws UnsupportedRDFormatException {
		this.writer = Rio.createWriter(format, output);
	}

	public long getTripleCount() {
		return this.tripleCount;
	}

	public void start() throws RDFHandlerException {
		this.tripleCount = 0;
		this.writer.startRDF();
	}

	public void finish() throws RDFHandlerException {
		this.writer.endRDF();
	}

	public Resource getFreshBNode() {
		return this.factory.createBNode();
	}

	public void writeNamespaceDeclaration(String prefix, String uri)
			throws RDFHandlerException {
		this.writer.handleNamespace(prefix, uri);
	}

	public void writeTripleStringObject(String subjectUri, String predicateUri,
			String objectLiteral) throws RDFHandlerException {
		writeTripleValueObject(subjectUri, predicateUri,
				this.factory.createLiteral(objectLiteral));
	}

	public void writeTripleIntegerObject(Resource subject, String predicateUri,
			int objectLiteral) throws RDFHandlerException {
		writeTripleValueObject(subject, predicateUri,
				this.factory.createLiteral(objectLiteral));
	}

	public void writeTripleUriObject(String subjectUri, String predicateUri,
			String objectUri) throws RDFHandlerException {
		writeTripleValueObject(subjectUri, predicateUri,
				this.factory.createURI(objectUri));
	}

	public void writeTripleUriObject(Resource subject, String predicateUri,
			String objectUri) throws RDFHandlerException {
		writeTripleValueObject(subject, predicateUri,
				this.factory.createURI(objectUri));
	}

	public void writeTripleValueObject(String subjectUri, String predicateUri,
			Value object) throws RDFHandlerException {
		URI subject = this.factory.createURI(subjectUri);
		URI predicate = this.factory.createURI(predicateUri);

		this.tripleCount++;
		this.writer.handleStatement(this.factory.createStatement(subject,
				predicate, object));
	}

	public void writeTripleValueObject(Resource subject, String predicateUri,
			Value object) throws RDFHandlerException {
		URI predicate = this.factory.createURI(predicateUri);

		this.tripleCount++;
		this.writer.handleStatement(this.factory.createStatement(subject,
				predicate, object));
	}

	public void writeTripleLiteralObject(Resource subject, String predicateUri,
			String objectLexicalValue, String objectDatatypeUri)
			throws RDFHandlerException {
		URI predicate = this.factory.createURI(predicateUri);
		URI datatype = this.factory.createURI(objectDatatypeUri);
		Literal object = this.factory.createLiteral(objectLexicalValue,
				datatype);

		this.tripleCount++;
		this.writer.handleStatement(this.factory.createStatement(subject,
				predicate, object));
	}

}
