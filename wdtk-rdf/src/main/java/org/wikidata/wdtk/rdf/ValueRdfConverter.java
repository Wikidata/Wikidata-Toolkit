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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
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

	static final String VALUE_PREFIX_GLOBECOORDS = "VC";
	static final String VALUE_PREFIX_QUANTITY = "VQ";
	static final String VALUE_PREFIX_TIME = "VT";

	final ValueFactory factory = ValueFactoryImpl.getInstance();
	final MessageDigest md;
	final PropertyTypes propertyTypes = new PropertyTypes();
	final RdfWriter rdfWriter;
	final RdfConversionBuffer rdfConversionBuffer;

	PropertyIdValue currentPropertyIdValue;

	static final Logger logger = LoggerFactory
			.getLogger(ValueRdfConverter.class);

	public ValueRdfConverter(RdfWriter rdfWriter,
			RdfConversionBuffer rdfConversionBuffer) {
		this.rdfWriter = rdfWriter;
		this.rdfConversionBuffer = rdfConversionBuffer;

		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(
					"Your Java does not support MD5 hashes. You should be concerned.");
		}
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
		this.rdfWriter.writeTripleUriObject(resource, Vocabulary.RDF_TYPE,
				Vocabulary.WB_QUANTITY_VALUE);
		this.rdfWriter.writeTripleLiteralObject(resource,
				Vocabulary.WB_NUMERIC_VALUE, quantityValue.getNumericValue()
						.toString(), Vocabulary.XSD_DECIMAL);
		this.rdfWriter.writeTripleLiteralObject(resource,
				Vocabulary.WB_LOWER_BOUND, quantityValue.getLowerBound()
						.toString(), Vocabulary.XSD_DECIMAL);
		this.rdfWriter.writeTripleLiteralObject(resource,
				Vocabulary.WB_UPPER_BOUND, quantityValue.getUpperBound()
						.toString(), Vocabulary.XSD_DECIMAL);
	}

	/**
	 * Write the auxiliary RDF data for encoding the given value.
	 * 
	 * @param timeValue
	 *            the value to write
	 * @param resource
	 *            the (subject) URI to use to represent this value in RDF
	 * @throws RDFHandlerException
	 */
	public void writeTimeValue(TimeValue timeValue, Resource resource)
			throws RDFHandlerException {
		this.rdfWriter.writeTripleUriObject(resource, Vocabulary.RDF_TYPE,
				Vocabulary.WB_TIME_VALUE);
		// TODO finish
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
		this.rdfWriter.writeTripleUriObject(resource, Vocabulary.RDF_TYPE,
				Vocabulary.WB_GLOBE_COORDINATES_VALUE);
		// TODO finish
	}

	public Value getRdfValueForWikidataValue(
			org.wikidata.wdtk.datamodel.interfaces.Value value,
			PropertyIdValue propertyIdValue) {
		this.currentPropertyIdValue = propertyIdValue;
		return value.accept(this);
	}

	@Override
	public Value visit(DatatypeIdValue value) {
		return this.factory.createURI(value.getIri());
	}

	@Override
	public Value visit(EntityIdValue value) {
		return this.factory.createURI(Vocabulary.getEntityUri(value));
	}

	@Override
	public Value visit(GlobeCoordinatesValue value) {

		String hash = getGlobeCoordinatesValueHash(value);

		URI valueUri = this.factory.createURI(Vocabulary.PREFIX_WIKIDATA
				+ VALUE_PREFIX_GLOBECOORDS + hash);

		this.rdfConversionBuffer.addGlobeCoordinatesValue(value, valueUri);

		return valueUri;
	}

	@Override
	public Value visit(MonolingualTextValue value) {
		String languageCode = WikimediaLanguageCodes.getLanguageCode(value
				.getLanguageCode());
		return factory.createLiteral(value.getText(), languageCode);
	}

	@Override
	public Value visit(QuantityValue value) {

		String hash = getQuantityValueHash(value);

		URI valueUri = this.factory.createURI(Vocabulary.PREFIX_WIKIDATA
				+ VALUE_PREFIX_QUANTITY + hash);

		this.rdfConversionBuffer.addQuantityValue(value, valueUri);

		return valueUri;
	}

	@Override
	public Value visit(StringValue value) {
		String datatype = this.propertyTypes.setPropertyTypeFromValue(
				this.currentPropertyIdValue, value);

		if (datatype == null) {
			datatype = DatatypeIdValue.DT_STRING;
			logNoDatatypeError(datatype);
		}

		switch (datatype) {
		case DatatypeIdValue.DT_STRING:
			return factory.createLiteral(value.getString());
		case DatatypeIdValue.DT_COMMONS_MEDIA:
			// TODO use a smarter function to build those URLs
			return factory.createURI("http://commons.wikimedia.org/wiki/File:"
					+ value.getString().replace(' ', '_'));
		default:
			logIncompatibleValueError(datatype, "string");
			return null;
		}
	}

	@Override
	public Value visit(TimeValue value) {
		String hash = getTimeValueHash(value);

		URI valueUri = this.factory.createURI(Vocabulary.PREFIX_WIKIDATA
				+ VALUE_PREFIX_TIME + hash);

		this.rdfConversionBuffer.addTimeValue(value, valueUri);

		return valueUri;
	}

	String getGlobeCoordinatesValueHash(GlobeCoordinatesValue value) {
		md.reset();
		updateMessageDigestWithString(md, value.getGlobe());
		updateMessageDigestWithLong(md, value.getLatitude());
		updateMessageDigestWithLong(md, value.getLongitude());
		updateMessageDigestWithLong(md, value.getPrecision());
		return bytesToHex(md.digest());
	}

	String getQuantityValueHash(QuantityValue value) {
		md.reset();
		updateMessageDigestWithInt(md, value.getNumericValue().hashCode());
		updateMessageDigestWithInt(md, value.getLowerBound().hashCode());
		updateMessageDigestWithInt(md, value.getUpperBound().hashCode());
		return bytesToHex(md.digest());
	}

	String getTimeValueHash(TimeValue value) {
		md.reset();
		updateMessageDigestWithLong(md, value.getYear());
		md.update(value.getMonth());
		md.update(value.getDay());
		md.update(value.getHour());
		md.update(value.getMinute());
		md.update(value.getSecond());
		updateMessageDigestWithString(md, value.getPreferredCalendarModel());
		updateMessageDigestWithInt(md, value.getBeforeTolerance());
		updateMessageDigestWithInt(md, value.getAfterTolerance());
		updateMessageDigestWithInt(md, value.getTimezoneOffset());
		return bytesToHex(md.digest());
	}

	/**
	 * Logs a message for the case that the declared datatype of a property
	 * could not be found.
	 * 
	 * @param fallBackType
	 *            the property datatype that will be used as a fallback instead
	 */
	void logNoDatatypeError(String fallBackType) {
		logger.warn("Failed to find type of property "
				+ this.currentPropertyIdValue.getId() + "; using type \""
				+ fallBackType + "\"");
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

	ByteBuffer longByteBuffer = ByteBuffer.allocate(Long.SIZE);

	void updateMessageDigestWithLong(MessageDigest md, long x) {
		this.longByteBuffer.putLong(0, x);
		md.update(this.longByteBuffer);
	}

	ByteBuffer intByteBuffer = ByteBuffer.allocate(Integer.SIZE);

	void updateMessageDigestWithInt(MessageDigest md, int x) {
		this.intByteBuffer.putInt(0, x);
		md.update(this.intByteBuffer);
	}

	void updateMessageDigestWithString(MessageDigest md, String s) {
		md.update(s.getBytes(StandardCharsets.UTF_8));
	}

	final protected static char[] hexArray = "0123456789abcdef".toCharArray();

	static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

}
