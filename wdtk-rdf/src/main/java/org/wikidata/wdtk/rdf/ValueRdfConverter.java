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
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.datamodel.interfaces.WikimediaLanguageCodes;

/**
 * Class to convert Wikibase data values to RDF. The class is a visitor that
 * that computes an RDF value (URI or literal) to represent any kind of Wikibase
 * data value. Some values are complex and require further RDF triples to be
 * written. In such cases, the class stores the values to a buffer. Methods for
 * writing additional triples for these buffered values can be called later.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ValueRdfConverter implements ValueVisitor<Value> {

	final PropertyTypes propertyTypes;
	final RdfWriter rdfWriter;
	final RdfConversionBuffer rdfConversionBuffer;

	PropertyIdValue currentPropertyIdValue;

	static final Logger logger = LoggerFactory
			.getLogger(ValueRdfConverter.class);

	public ValueRdfConverter(RdfWriter rdfWriter,
			RdfConversionBuffer rdfConversionBuffer, PropertyTypes propertyTypes) {
		this.rdfWriter = rdfWriter;
		this.rdfConversionBuffer = rdfConversionBuffer;
		this.propertyTypes = propertyTypes;
	}

	/**
	 * Write the auxiliary RDF data for encoding the given value.
	 * 
	 * @param quantityValue
	 *            the value to write
	 * @param resource
	 *            the (subject) URI to use to represent this value in RDF
	 * @throws RDFHandlerException
	 */
	public void writeQuantityValue(QuantityValue quantityValue,
			Resource resource) throws RDFHandlerException {
		this.rdfWriter.writeTripleValueObject(resource, RdfWriter.RDF_TYPE,
				RdfWriter.WB_QUANTITY_VALUE);
		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_NUMERIC_VALUE, quantityValue.getNumericValue()
						.toString(), RdfWriter.XSD_DECIMAL);
		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_LOWER_BOUND, quantityValue.getLowerBound()
						.toString(), RdfWriter.XSD_DECIMAL);
		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_UPPER_BOUND, quantityValue.getUpperBound()
						.toString(), RdfWriter.XSD_DECIMAL);
	}

	/**
	 * Write the auxiliary RDF data for encoding the given value.
	 * <p>
	 * Times with limited precision are exported using limited-precision XML
	 * Schema datatypes, such as gYear, if available. Wikidata encodes the year
	 * 1BCE as 0000, while XML Schema, even in version 2, does not allow 0000
	 * and interprets -0001 as 1BCE. Thus all negative years must be shifted by
	 * 1, but we only do this if the year is precise.
	 * 
	 * @param timeValue
	 *            the value to write
	 * @param resource
	 *            the (subject) URI to use to represent this value in RDF
	 * @throws RDFHandlerException
	 */
	public void writeTimeValue(TimeValue timeValue, Resource resource)
			throws RDFHandlerException {
		this.rdfWriter.writeTripleValueObject(resource, RdfWriter.RDF_TYPE,
				RdfWriter.WB_TIME_VALUE);

		String xsdYearString;
		if (timeValue.getYear() == 0
				|| (timeValue.getYear() < 0 && timeValue.getPrecision() >= TimeValue.PREC_YEAR)) {
			xsdYearString = String.format("%05d", timeValue.getYear() - 1);
		} else {
			xsdYearString = String.format("%04d", timeValue.getYear());
		}

		if (timeValue.getPrecision() >= TimeValue.PREC_DAY) {
			if (timeValue.getPrecision() > TimeValue.PREC_DAY) {
				logger.error("Time values with times of day not supported yet. Exporting only date of "
						+ timeValue.toString());
			}
			this.rdfWriter.writeTripleLiteralObject(
					resource,
					RdfWriter.WB_TIME,
					xsdYearString + "-"
							+ String.format("%02d", timeValue.getMonth()) + "-"
							+ String.format("%02d", timeValue.getDay()),
					RdfWriter.XSD_DATE);
		} else if (timeValue.getPrecision() == TimeValue.PREC_MONTH) {
			this.rdfWriter.writeTripleLiteralObject(
					resource,
					RdfWriter.WB_TIME,
					xsdYearString + "-"
							+ String.format("%02d", timeValue.getMonth()),
					RdfWriter.XSD_G_YEAR_MONTH);
		} else if (timeValue.getPrecision() <= TimeValue.PREC_YEAR) {
			this.rdfWriter.writeTripleLiteralObject(resource,
					RdfWriter.WB_TIME, xsdYearString, RdfWriter.XSD_G_YEAR);
		}

		this.rdfWriter.writeTripleIntegerObject(resource,
				RdfWriter.WB_TIME_PRECISION, timeValue.getPrecision());
		this.rdfWriter.writeTripleUriObject(resource,
				RdfWriter.WB_PREFERRED_CALENDAR,
				timeValue.getPreferredCalendarModel());
	}

	/**
	 * Write the auxiliary RDF data for encoding the given value.
	 * 
	 * @param globeCoordinatesValue
	 *            the value to write
	 * @param resource
	 *            the (subject) URI to use to represent this value in RDF
	 * @throws RDFHandlerException
	 */
	public void writeGlobeCoordinatesValue(
			GlobeCoordinatesValue globeCoordinatesValue, Resource resource)
			throws RDFHandlerException {
		this.rdfWriter.writeTripleValueObject(resource, RdfWriter.RDF_TYPE,
				RdfWriter.WB_GLOBE_COORDINATES_VALUE);

		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_LATITUDE,
				getDecimalStringForCoordinate(globeCoordinatesValue
						.getLatitude()), RdfWriter.XSD_DECIMAL);
		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_LONGITUDE,
				getDecimalStringForCoordinate(globeCoordinatesValue
						.getLongitude()), RdfWriter.XSD_DECIMAL);
		this.rdfWriter.writeTripleLiteralObject(resource,
				RdfWriter.WB_GC_PRECISION,
				getDecimalStringForCoordinate(globeCoordinatesValue
						.getPrecision()), RdfWriter.XSD_DECIMAL);
		this.rdfWriter.writeTripleUriObject(resource, RdfWriter.WB_GLOBE,
				globeCoordinatesValue.getGlobe());
	}

	public Value getRdfValueForWikidataValue(
			org.wikidata.wdtk.datamodel.interfaces.Value value,
			PropertyIdValue propertyIdValue) {
		this.currentPropertyIdValue = propertyIdValue;
		return value.accept(this);
	}

	public Value getDatatypeIdValueLiteral(DatatypeIdValue value) {
		return this.rdfWriter.getUri(value.getIri());
	}

	public Value getMonolingualTextValueLiteral(MonolingualTextValue value) {
		String languageCode = WikimediaLanguageCodes.getLanguageCode(value
				.getLanguageCode());
		return this.rdfWriter.getLiteral(value.getText(), languageCode);
	}

	@Override
	public Value visit(DatatypeIdValue value) {
		throw new RuntimeException(
				"DatatypeIdValue cannot be processed like a value of a user-defined property. "
						+ "Use getDatatypeIdValueLiteral() to get a Literal for such values.");
	}

	@Override
	public Value visit(EntityIdValue value) {
		String datatype = this.propertyTypes.setPropertyTypeFromValue(
				this.currentPropertyIdValue, value);

		switch (datatype) {
		case DatatypeIdValue.DT_ITEM:
			this.rdfConversionBuffer
					.addObjectProperty(this.currentPropertyIdValue);
			return this.rdfWriter.getUri(value.getIri());
		default:
			logIncompatibleValueError(datatype, "entity");
			return null;
		}
	}

	@Override
	public Value visit(GlobeCoordinatesValue value) {

		String datatype = this.propertyTypes.setPropertyTypeFromValue(
				this.currentPropertyIdValue, value);

		switch (datatype) {
		case DatatypeIdValue.DT_GLOBE_COORDINATES:
			URI valueUri = this.rdfWriter.getUri(Vocabulary
					.getGlobeCoordinatesValueUri(value));

			this.rdfConversionBuffer
					.addObjectProperty(this.currentPropertyIdValue);
			this.rdfConversionBuffer.addGlobeCoordinatesValue(value, valueUri);

			return valueUri;
		default:
			logIncompatibleValueError(datatype, "globe coordinates");
			return null;
		}
	}

	@Override
	public Value visit(MonolingualTextValue value) {
		throw new RuntimeException(
				"MonolingualTextValue cannot be processed like a value of a user-defined property. "
						+ "Use getMonolingualTextValueLiteral() to get a Literal for such values.");
	}

	@Override
	public Value visit(QuantityValue value) {

		String datatype = this.propertyTypes.setPropertyTypeFromValue(
				this.currentPropertyIdValue, value);

		switch (datatype) {
		case DatatypeIdValue.DT_QUANTITY:
			URI valueUri = this.rdfWriter.getUri(Vocabulary
					.getQuantityValueUri(value));

			this.rdfConversionBuffer
					.addObjectProperty(this.currentPropertyIdValue);
			this.rdfConversionBuffer.addQuantityValue(value, valueUri);

			return valueUri;
		default:
			logIncompatibleValueError(datatype, "quantity");
			return null;
		}
	}

	@Override
	public Value visit(StringValue value) {
		String datatype = this.propertyTypes.setPropertyTypeFromValue(
				this.currentPropertyIdValue, value);

		switch (datatype) {
		case DatatypeIdValue.DT_STRING:
			this.rdfConversionBuffer
					.addDatatypeProperty(this.currentPropertyIdValue);
			return this.rdfWriter.getLiteral(value.getString());
		case DatatypeIdValue.DT_COMMONS_MEDIA:
			this.rdfConversionBuffer
					.addObjectProperty(this.currentPropertyIdValue);
			// TODO use a smarter function to build those URLs
			return this.rdfWriter
					.getUri("http://commons.wikimedia.org/wiki/File:"
							+ value.getString().replace(' ', '_'));
		case DatatypeIdValue.DT_URL:
			this.rdfConversionBuffer
					.addObjectProperty(this.currentPropertyIdValue);
			return this.rdfWriter.getUri(value.getString());
		default:
			logIncompatibleValueError(datatype, "string");
			return null;
		}
	}

	@Override
	public Value visit(TimeValue value) {

		String datatype = this.propertyTypes.setPropertyTypeFromValue(
				this.currentPropertyIdValue, value);

		switch (datatype) {
		case DatatypeIdValue.DT_TIME:
			URI valueUri = this.rdfWriter.getUri(Vocabulary
					.getTimeValueUri(value));

			this.rdfConversionBuffer
					.addObjectProperty(this.currentPropertyIdValue);
			this.rdfConversionBuffer.addTimeValue(value, valueUri);

			return valueUri;
		default:
			logIncompatibleValueError(datatype, "time");
			return null;
		}
	}

	String getDecimalStringForCoordinate(long value) {
		String valueString;
		if (value >= 0) {
			valueString = String.format("%010d", value);
		} else {
			valueString = String.format("%011d", value);
		}
		return valueString.substring(0, valueString.length() - 9) + "."
				+ valueString.substring(valueString.length() - 9);
	}

	/**
	 * Logs a message for a case where the value of a property does not fit to
	 * its declared datatype.
	 * 
	 * @param datatype
	 *            the declared type of the property
	 * @param valueType
	 *            a string to denote the type of value
	 */
	void logIncompatibleValueError(String datatype, String valueType) {
		logger.warn("Property " + this.currentPropertyIdValue.getId()
				+ " has type \"" + datatype + "\" but a value of type "
				+ valueType + ". Data ignored.");
	}

}
