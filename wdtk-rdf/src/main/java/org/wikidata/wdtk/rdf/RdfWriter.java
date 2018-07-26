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

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;

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

	static final ValueFactory factory = SimpleValueFactory.getInstance();

	public static final IRI RDF_TYPE = factory.createIRI(Vocabulary.RDF_TYPE);
	public static final IRI RDFS_LABEL = factory
			.createIRI(Vocabulary.RDFS_LABEL);
	public static final IRI RDFS_SEE_ALSO = factory
			.createIRI(Vocabulary.RDFS_SEE_ALSO);
	public static final IRI RDFS_LITERAL = factory
			.createIRI(Vocabulary.RDFS_LITERAL);
	public static final IRI RDFS_SUBCLASS_OF = factory
			.createIRI(Vocabulary.RDFS_SUBCLASS_OF);
	public static final IRI RDFS_SUBPROPERTY_OF = factory
			.createIRI(Vocabulary.RDFS_SUBPROPERTY_OF);
	public static final IRI OWL_THING = factory.createIRI(Vocabulary.OWL_THING);
	public static final IRI OWL_CLASS = factory.createIRI(Vocabulary.OWL_CLASS);
	public static final IRI OWL_OBJECT_PROPERTY = factory
			.createIRI(Vocabulary.OWL_OBJECT_PROPERTY);
	public static final IRI OWL_DATATYPE_PROPERTY = factory
			.createIRI(Vocabulary.OWL_DATATYPE_PROPERTY);
	public static final IRI OWL_RESTRICTION = factory
			.createIRI(Vocabulary.OWL_RESTRICTION);
	public static final IRI OWL_SOME_VALUES_FROM = factory
			.createIRI(Vocabulary.OWL_SOME_VALUES_FROM);
	public static final IRI OWL_ON_PROPERTY = factory
			.createIRI(Vocabulary.OWL_ON_PROPERTY);
	public static final IRI OWL_COMPLEMENT_OF = factory
			.createIRI(Vocabulary.OWL_COMPLEMENT_OF);
	public static final IRI XSD_DOUBLE = factory
			.createIRI(Vocabulary.XSD_DOUBLE);
	public static final IRI XSD_DECIMAL = factory
			.createIRI(Vocabulary.XSD_DECIMAL);
	public static final IRI XSD_INT = factory.createIRI(Vocabulary.XSD_INT);
	public static final IRI XSD_DATE = factory.createIRI(Vocabulary.XSD_DATE);
	public static final IRI XSD_G_YEAR = factory
			.createIRI(Vocabulary.XSD_G_YEAR);
	public static final IRI XSD_G_YEAR_MONTH = factory
			.createIRI(Vocabulary.XSD_G_YEAR_MONTH);
	public static final IRI XSD_DATETIME = factory
			.createIRI(Vocabulary.XSD_DATETIME);
	public static final IRI XSD_STRING = factory
			.createIRI(Vocabulary.XSD_STRING);
	public static final IRI SKOS_ALT_LABEL = factory
			.createIRI(Vocabulary.SKOS_ALT_LABEL);
	public static final IRI SCHEMA_ABOUT = factory
			.createIRI(Vocabulary.SCHEMA_ABOUT);
	public static final IRI SCHEMA_ARTICLE = factory
			.createIRI(Vocabulary.SCHEMA_ARTICLE);
	public static final IRI SCHEMA_DESCRIPTION = factory
			.createIRI(Vocabulary.SCHEMA_DESCRIPTION);
	public static final IRI SCHEMA_IN_LANGUAGE = factory
			.createIRI(Vocabulary.SCHEMA_IN_LANGUAGE);
	public static final IRI PROV_WAS_DERIVED_FROM = factory
			.createIRI(Vocabulary.PROV_WAS_DERIVED_FROM);
	public static final IRI WB_ITEM = factory.createIRI(Vocabulary.WB_ITEM);
	public static final IRI WB_REFERENCE = factory
			.createIRI(Vocabulary.WB_REFERENCE);
	public static final IRI WB_PROPERTY = factory
			.createIRI(Vocabulary.WB_PROPERTY);
	public static final IRI WB_STATEMENT = factory
			.createIRI(Vocabulary.WB_STATEMENT);
	public static final IRI WB_QUANTITY_VALUE = factory
			.createIRI(Vocabulary.WB_QUANTITY_VALUE);
	public static final IRI WB_TIME_VALUE = factory
			.createIRI(Vocabulary.WB_TIME_VALUE);
	public static final IRI WB_GLOBE_COORDINATES_VALUE = factory
			.createIRI(Vocabulary.WB_GLOBE_COORDINATES_VALUE);
	public static final IRI WB_PROPERTY_TYPE = factory
			.createIRI(Vocabulary.WB_PROPERTY_TYPE);
	public static final IRI WB_GEO_GLOBE = factory.createIRI(Vocabulary.WB_GEO_GLOBE);
	public static final IRI WB_GEO_LATITUDE = factory
			.createIRI(Vocabulary.WB_GEO_LATITUDE);
	public static final IRI WB_GEO_LONGITUDE = factory
			.createIRI(Vocabulary.WB_GEO_LONGITUDE);
	public static final IRI WB_GEO_PRECISION = factory
			.createIRI(Vocabulary.WB_GEO_PRECISION);
	public static final IRI WB_TIME = factory.createIRI(Vocabulary.WB_TIME);
	public static final IRI WB_TIME_PRECISION = factory
			.createIRI(Vocabulary.WB_TIME_PRECISION);
	public static final IRI WB_TIME_TIMEZONE = factory
			.createIRI(Vocabulary.WB_TIME_TIMEZONE);
	public static final IRI WB_TIME_CALENDAR_MODEL = factory
			.createIRI(Vocabulary.WB_TIME_CALENDAR_MODEL);
	public static final IRI WB_QUANTITY_AMOUNT = factory
			.createIRI(Vocabulary.WB_QUANTITY_AMOUNT);
	public static final IRI WB_QUANTITY_LOWER_BOUND = factory
			.createIRI(Vocabulary.WB_QUANTITY_LOWER_BOUND);
	public static final IRI WB_QUANTITY_UPPER_BOUND = factory
			.createIRI(Vocabulary.WB_QUANTITY_UPPER_BOUND);
	public static final IRI WB_QUANTITY_UNIT = factory
			.createIRI(Vocabulary.WB_QUANTITY_UNIT);
	public static final IRI OGC_LOCATION = factory
			.createIRI(Vocabulary.OGC_LOCATION);
	public static final IRI WB_RANK = factory.createIRI(Vocabulary.WB_RANK);
	public static final IRI WB_BEST_RANK = factory
			.createIRI(Vocabulary.WB_BEST_RANK);
	public static final IRI WB_BADGE = factory.createIRI(Vocabulary.WB_BADGE);

	RDFWriter writer;

	long tripleCount = 0;

	public RdfWriter(RDFFormat format, OutputStream output) throws UnsupportedRDFormatException {
		this(Rio.createWriter(format, output));
	}

	public RdfWriter(RDFWriter writer) {
		this.writer = writer;
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
	 * Creates a IRI object for the given IRI string. Callers who use this with
	 * user-provided data should check for exceptions.
	 *
	 * @param uri
	 *            the IRI string
	 * @return the IRI object
	 * @throws IllegalArgumentException
	 *             if the string is not a valid absolute URI.
	 */
	public IRI getUri(String uri) {
		return factory.createIRI(uri);
	}

	public Literal getLiteral(String value) {
		return factory.createLiteral(value);
	}

	public Literal getLiteral(String value, String languageCode) {
		return factory.createLiteral(value, languageCode);
	}

	public Literal getLiteral(String value, IRI datatypeUri) {
		return factory.createLiteral(value, datatypeUri);
	}

	public void writeNamespaceDeclaration(String prefix, String uri)
			throws RDFHandlerException {
		this.writer.handleNamespace(prefix, uri);
	}

	public void writeTripleStringObject(Resource subject, IRI predicate,
			String objectLiteral) throws RDFHandlerException {
		writeTripleValueObject(subject, predicate,
				factory.createLiteral(objectLiteral));
	}

	public void writeTripleIntegerObject(Resource subject, IRI predicate,
			int objectLiteral) throws RDFHandlerException {
		writeTripleValueObject(subject, predicate,
				factory.createLiteral(objectLiteral));
	}

	public void writeTripleUriObject(String subjectUri, IRI predicate,
			String objectUri) throws RDFHandlerException {
		writeTripleValueObject(subjectUri, predicate,
				factory.createIRI(objectUri));
	}

	public void writeTripleUriObject(Resource subject, IRI predicate,
			String objectUri) throws RDFHandlerException {
		writeTripleValueObject(subject, predicate, factory.createIRI(objectUri));
	}

	public void writeTripleValueObject(String subjectUri, IRI predicate,
			Value object) throws RDFHandlerException {
		IRI subject = factory.createIRI(subjectUri);

		this.tripleCount++;
		this.writer.handleStatement(factory.createStatement(subject, predicate,
				object));
	}

	public void writeTripleValueObject(Resource subject, IRI predicate,
			Value object) throws RDFHandlerException {
		this.tripleCount++;
		this.writer.handleStatement(factory.createStatement(subject, predicate,
				object));
	}

	public void writeTripleLiteralObject(Resource subject, IRI predicate,
			String objectLexicalValue, IRI datatype) throws RDFHandlerException {
		Literal object = factory.createLiteral(objectLexicalValue, datatype);

		this.tripleCount++;
		this.writer.handleStatement(factory.createStatement(subject, predicate,
				object));
	}

}
