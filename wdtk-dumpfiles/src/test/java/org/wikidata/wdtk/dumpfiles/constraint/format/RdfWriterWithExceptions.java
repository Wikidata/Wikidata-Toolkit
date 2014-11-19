package org.wikidata.wdtk.dumpfiles.constraint.format;

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

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.wikidata.wdtk.rdf.RdfWriter;

/**
 * This class throws an exception in every method that declares the exception.
 * This is used for testing.
 * 
 * @author Julian Mendez
 * 
 */
public class RdfWriterWithExceptions extends RdfWriter {

	public RdfWriterWithExceptions() {
		super(RDFFormat.RDFJSON, System.out);
	}

	@Override
	public void start() throws RDFHandlerException {
		throw new RDFHandlerException("RDFHandlerException");
	}

	@Override
	public void finish() throws RDFHandlerException {
		throw new RDFHandlerException("RDFHandlerException");
	}

	@Override
	public void writeNamespaceDeclaration(String prefix, String uri)
			throws RDFHandlerException {
		throw new RDFHandlerException("RDFHandlerException");
	}

	@Override
	public void writeTripleStringObject(Resource subject, URI predicate,
			String objectLiteral) throws RDFHandlerException {
		throw new RDFHandlerException("RDFHandlerException");
	}

	@Override
	public void writeTripleIntegerObject(Resource subject, URI predicate,
			int objectLiteral) throws RDFHandlerException {
		throw new RDFHandlerException("RDFHandlerException");
	}

	@Override
	public void writeTripleUriObject(String subjectUri, URI predicate,
			String objectUri) throws RDFHandlerException {
		throw new RDFHandlerException("RDFHandlerException");
	}

	@Override
	public void writeTripleUriObject(Resource subject, URI predicate,
			String objectUri) throws RDFHandlerException {
		throw new RDFHandlerException("RDFHandlerException");
	}

	@Override
	public void writeTripleValueObject(String subjectUri, URI predicate,
			Value object) throws RDFHandlerException {
		throw new RDFHandlerException("RDFHandlerException");
	}

	@Override
	public void writeTripleValueObject(Resource subject, URI predicate,
			Value object) throws RDFHandlerException {
		throw new RDFHandlerException("RDFHandlerException");
	}

	@Override
	public void writeTripleLiteralObject(Resource subject, URI predicate,
			String objectLexicalValue, URI datatype) throws RDFHandlerException {
		throw new RDFHandlerException("RDFHandlerException");
	}

}
