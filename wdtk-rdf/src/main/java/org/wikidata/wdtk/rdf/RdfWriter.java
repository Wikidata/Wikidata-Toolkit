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

import org.openrdf.model.BNode;
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

	static final ValueFactory factory = ValueFactoryImpl.getInstance();

	public static final URI RDF_TYPE = factory.createURI(Vocabulary.RDF_TYPE);
	public static final URI RDFS_LABEL = factory
			.createURI(Vocabulary.RDFS_LABEL);
	public static final URI RDFS_LITERAL = factory
			.createURI(Vocabulary.RDFS_LITERAL);
	public static final URI RDFS_SUBCLASS_OF = factory
			.createURI(Vocabulary.RDFS_SUBCLASS_OF);
	public static final URI OWL_THING = factory.createURI(Vocabulary.OWL_THING);
	public static final URI OWL_CLASS = factory.createURI(Vocabulary.OWL_CLASS);
	public static final URI OWL_OBJECT_PROPERTY = factory
			.createURI(Vocabulary.OWL_OBJECT_PROPERTY);
	public static final URI OWL_DATATYPE_PROPERTY = factory
			.createURI(Vocabulary.OWL_DATATYPE_PROPERTY);
	public static final URI OWL_RESTRICTION = factory
			.createURI(Vocabulary.OWL_RESTRICTION);
	public static final URI OWL_SOME_VALUES_FROM = factory
			.createURI(Vocabulary.OWL_SOME_VALUES_FROM);
	public static final URI OWL_ON_PROPERTY = factory
			.createURI(Vocabulary.OWL_ON_PROPERTY);
	public static final URI OWL_COMPLEMENT_OF = factory
			.createURI(Vocabulary.OWL_COMPLEMENT_OF);
	public static final URI XSD_DOUBLE = factory
			.createURI(Vocabulary.XSD_DOUBLE);
	public static final URI XSD_DECIMAL = factory
			.createURI(Vocabulary.XSD_DECIMAL);
	public static final URI XSD_INT = factory.createURI(Vocabulary.XSD_INT);
	public static final URI XSD_DATE = factory.createURI(Vocabulary.XSD_DATE);
	public static final URI XSD_G_YEAR = factory
			.createURI(Vocabulary.XSD_G_YEAR);
	public static final URI XSD_G_YEAR_MONTH = factory
			.createURI(Vocabulary.XSD_G_YEAR_MONTH);
	public static final URI XSD_DATETIME = factory
			.createURI(Vocabulary.XSD_DATETIME);
	public static final URI XSD_STRING = factory
			.createURI(Vocabulary.XSD_STRING);
	public static final URI SKOS_ALT_LABEL = factory
			.createURI(Vocabulary.SKOS_ALT_LABEL);
	public static final URI SCHEMA_ABOUT = factory
			.createURI(Vocabulary.SCHEMA_ABOUT);
	public static final URI SCHEMA_DESCRIPTION = factory
			.createURI(Vocabulary.SCHEMA_DESCRIPTION);
	public static final URI SCHEMA_IN_LANGUAGE = factory
			.createURI(Vocabulary.SCHEMA_IN_LANGUAGE);
	public static final URI PROV_WAS_DERIVED_FROM = factory
			.createURI(Vocabulary.PROV_WAS_DERIVED_FROM);
	public static final URI WB_ITEM = factory.createURI(Vocabulary.WB_ITEM);
	public static final URI WB_REFERENCE = factory
			.createURI(Vocabulary.WB_REFERENCE);
	public static final URI WB_PROPERTY = factory
			.createURI(Vocabulary.WB_PROPERTY);
	public static final URI WB_STATEMENT = factory
			.createURI(Vocabulary.WB_STATEMENT);
	public static final URI WB_ARTICLE = factory
			.createURI(Vocabulary.WB_ARTICLE);
	public static final URI WB_QUANTITY_VALUE = factory
			.createURI(Vocabulary.WB_QUANTITY_VALUE);
	public static final URI WB_TIME_VALUE = factory
			.createURI(Vocabulary.WB_TIME_VALUE);
	public static final URI WB_GLOBE_COORDINATES_VALUE = factory
			.createURI(Vocabulary.WB_GLOBE_COORDINATES_VALUE);
	public static final URI WB_PROPERTY_TYPE = factory
			.createURI(Vocabulary.WB_PROPERTY_TYPE);
	public static final URI WB_GLOBE = factory.createURI(Vocabulary.WB_GLOBE);
	public static final URI WB_LATITUDE = factory
			.createURI(Vocabulary.WB_LATITUDE);
	public static final URI WB_LONGITUDE = factory
			.createURI(Vocabulary.WB_LONGITUDE);
	public static final URI WB_GC_PRECISION = factory
			.createURI(Vocabulary.WB_GC_PRECISION);
	public static final URI WB_TIME = factory.createURI(Vocabulary.WB_TIME);
	public static final URI WB_TIME_PRECISION = factory
			.createURI(Vocabulary.WB_TIME_PRECISION);
	public static final URI WB_PREFERRED_CALENDAR = factory
			.createURI(Vocabulary.WB_PREFERRED_CALENDAR);
	public static final URI WB_NUMERIC_VALUE = factory
			.createURI(Vocabulary.WB_NUMERIC_VALUE);
	public static final URI WB_LOWER_BOUND = factory
			.createURI(Vocabulary.WB_LOWER_BOUND);
	public static final URI WB_UPPER_BOUND = factory
			.createURI(Vocabulary.WB_UPPER_BOUND);

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

	public BNode getFreshBNode() {
		return factory.createBNode();
	}

	/**
	 * Creates a URI object for the given URI string. Callers who use this with
	 * user-provided data should check for exceptions.
	 *
	 * @param uri
	 *            the URI string
	 * @return the URI object
	 * @throws IllegalArgumentException
	 *             if the string is not a valid absolute URI.
	 */
	public URI getUri(String uri) {
		return factory.createURI(uri);
	}

	public Literal getLiteral(String value) {
		return factory.createLiteral(value);
	}

	public Literal getLiteral(String value, String languageCode) {
		return factory.createLiteral(value, languageCode);
	}

	public Literal getLiteral(String value, URI datatypeUri) {
		return factory.createLiteral(value, datatypeUri);
	}

	public void writeNamespaceDeclaration(String prefix, String uri)
			throws RDFHandlerException {
		this.writer.handleNamespace(prefix, uri);
	}

	public void writeTripleStringObject(Resource subject, URI predicate,
			String objectLiteral) throws RDFHandlerException {
		writeTripleValueObject(subject, predicate,
				factory.createLiteral(objectLiteral));
	}

	public void writeTripleIntegerObject(Resource subject, URI predicate,
			int objectLiteral) throws RDFHandlerException {
		writeTripleValueObject(subject, predicate,
				factory.createLiteral(objectLiteral));
	}

	public void writeTripleUriObject(String subjectUri, URI predicate,
			String objectUri) throws RDFHandlerException {
		writeTripleValueObject(subjectUri, predicate,
				factory.createURI(objectUri));
	}

	public void writeTripleUriObject(Resource subject, URI predicate,
			String objectUri) throws RDFHandlerException {
		writeTripleValueObject(subject, predicate, factory.createURI(objectUri));
	}

	public void writeTripleValueObject(String subjectUri, URI predicate,
			Value object) throws RDFHandlerException {
		URI subject = factory.createURI(subjectUri);

		this.tripleCount++;
		this.writer.handleStatement(factory.createStatement(subject, predicate,
				object));
	}

	public void writeTripleValueObject(Resource subject, URI predicate,
			Value object) throws RDFHandlerException {
		this.tripleCount++;
		this.writer.handleStatement(factory.createStatement(subject, predicate,
				object));
	}

	public void writeTripleLiteralObject(Resource subject, URI predicate,
			String objectLexicalValue, URI datatype) throws RDFHandlerException {
		Literal object = factory.createLiteral(objectLexicalValue, datatype);

		this.tripleCount++;
		this.writer.handleStatement(factory.createStatement(subject, predicate,
				object));
	}

}
