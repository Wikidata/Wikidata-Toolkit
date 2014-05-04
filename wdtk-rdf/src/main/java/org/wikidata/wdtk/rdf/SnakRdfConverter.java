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
import org.openrdf.model.Resource;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class SnakRdfConverter implements SnakVisitor<Void> {

	static final Logger logger = LoggerFactory
			.getLogger(SnakRdfConverter.class);

	final ValueRdfConverter valueRdfConverter;

	final RdfWriter rdfWriter;
	final PropertyTypes propertyTypes;
	final RdfConversionBuffer rdfConversionBuffer;

	Resource currentSubject;
	PropertyContext currentPropertyContext;

	public SnakRdfConverter(RdfWriter rdfWriter,
			RdfConversionBuffer rdfConversionBuffer,
			PropertyTypes propertyTypes, ValueRdfConverter valueRdfConverter) {
		this.rdfWriter = rdfWriter;
		this.rdfConversionBuffer = rdfConversionBuffer;
		this.propertyTypes = propertyTypes;
		this.valueRdfConverter = valueRdfConverter;
	}

	public void writeSnak(Snak snak, Resource subject,
			PropertyContext propertyContext) {
		this.currentSubject = subject;
		this.currentPropertyContext = propertyContext;
		snak.accept(this);
	}

	public void setSnakContext(Resource subject, PropertyContext propertyContext) {
		this.currentSubject = subject;
		this.currentPropertyContext = propertyContext;
	}

	@Override
	public Void visit(ValueSnak snak) {
		String propertyUri = Vocabulary.getPropertyUri(snak.getPropertyId(),
				this.currentPropertyContext);
		Value value = valueRdfConverter.getRdfValueForWikidataValue(
				snak.getValue(), snak.getPropertyId());
		if (value == null) {
			logger.error("Could not serialize snak: missing value (Snak: "
					+ snak.toString() + ")");
			return null;
		}

		try {
			this.rdfWriter.writeTripleValueObject(this.currentSubject,
					propertyUri, value);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e.toString(), e);
		}
		return null;
	}

	@Override
	public Void visit(SomeValueSnak snak) {
		String rangeUri = getRangeUri(snak.getPropertyId());
		if (rangeUri == null) {
			logger.error("Count not export SomeValueSnak for property "
					+ snak.getPropertyId().getId() + ": OWL range not known.");
			return null;
		}

		String propertyUri = Vocabulary.getPropertyUri(snak.getPropertyId(),
				this.currentPropertyContext);
		Resource bnode = this.rdfWriter.getFreshBNode();
		this.rdfConversionBuffer.addSomeValuesRestriction(bnode, propertyUri,
				rangeUri);
		try {
			this.rdfWriter.writeTripleValueObject(this.currentSubject,
					Vocabulary.RDF_TYPE, bnode);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e.toString(), e);
		}

		return null;
	}

	@Override
	public Void visit(NoValueSnak snak) {
		String rangeUri = getRangeUri(snak.getPropertyId());
		if (rangeUri == null) {
			logger.error("Count not export NoValueSnak for property "
					+ snak.getPropertyId().getId() + ": OWL range not known.");
			return null;
		} else if (!Vocabulary.OWL_THING.equals(rangeUri)) {
			rangeUri = Vocabulary.RDFS_LITERAL;
		}

		String propertyUri = Vocabulary.getPropertyUri(snak.getPropertyId(),
				this.currentPropertyContext);
		Resource bnode = this.rdfWriter.getFreshBNode();
		this.rdfConversionBuffer.addNoValuesRestriction(bnode, propertyUri,
				rangeUri);
		try {
			this.rdfWriter.writeTripleValueObject(this.currentSubject,
					Vocabulary.RDF_TYPE, bnode);
		} catch (RDFHandlerException e) {
			throw new RuntimeException(e.toString(), e);
		}

		return null;
	}

	public void writeSomeValueRestriction(String propertyUri, String rangeUri,
			Resource bnode) throws RDFHandlerException {
		this.rdfWriter.writeTripleUriObject(bnode, Vocabulary.RDF_TYPE,
				Vocabulary.OWL_RESTRICTION);
		this.rdfWriter.writeTripleUriObject(bnode, Vocabulary.OWL_ON_PROPERTY,
				propertyUri);
		this.rdfWriter.writeTripleUriObject(bnode,
				Vocabulary.OWL_SOME_VALUES_FROM, rangeUri);
	}

	public void writeNoValueRestriction(String propertyUri, String rangeUri,
			Resource bnode) throws RDFHandlerException {

		Resource bnodeSome = this.rdfWriter.getFreshBNode();
		this.rdfWriter.writeTripleUriObject(bnode, Vocabulary.RDF_TYPE,
				Vocabulary.OWL_CLASS);
		this.rdfWriter.writeTripleValueObject(bnode,
				Vocabulary.OWL_COMPLEMENT_OF, bnodeSome);
		this.rdfWriter.writeTripleUriObject(bnodeSome, Vocabulary.RDF_TYPE,
				Vocabulary.OWL_RESTRICTION);
		this.rdfWriter.writeTripleUriObject(bnodeSome,
				Vocabulary.OWL_ON_PROPERTY, propertyUri);
		this.rdfWriter.writeTripleUriObject(bnodeSome,
				Vocabulary.OWL_SOME_VALUES_FROM, rangeUri);
	}

	String getRangeUri(PropertyIdValue propertyIdValue) {
		String datatype = this.propertyTypes.getPropertyType(propertyIdValue);

		switch (datatype) {
		case DatatypeIdValue.DT_STRING:
			this.rdfConversionBuffer.addDatatypeProperty(propertyIdValue);
			return Vocabulary.XSD_STRING;
		case DatatypeIdValue.DT_COMMONS_MEDIA:
		case DatatypeIdValue.DT_GLOBE_COORDINATES:
		case DatatypeIdValue.DT_ITEM:
		case DatatypeIdValue.DT_QUANTITY:
		case DatatypeIdValue.DT_TIME:
		case DatatypeIdValue.DT_URL:
			this.rdfConversionBuffer.addObjectProperty(propertyIdValue);
			return Vocabulary.OWL_THING;
		default:
			return null;
		}
	}
}
