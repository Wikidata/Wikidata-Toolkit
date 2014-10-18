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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

/**
 * This class contains static methods and constants that define the various OWL
 * and RDF vocabularies that are used in the export.
 *
 * @author Markus Kroetzsch
 *
 */
public class Vocabulary {

	final static MessageDigest md;
	static {
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(
					"Your Java does not support MD5 hashes. You should be concerned.");
		}
	}

	// TODO The prefix for wiki entities should be determined from the data; not
	// a constant
	private static final String PREFIX_WIKIDATA = "http://www.wikidata.org/entity/";
	// Prefixes
	public static final String PREFIX_WBONTO = "http://www.wikidata.org/ontology#";
	public static final String PREFIX_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String PREFIX_RDFS = "http://www.w3.org/2000/01/rdf-schema#";
	public static final String PREFIX_OWL = "http://www.w3.org/2002/07/owl#";
	public static final String PREFIX_XSD = "http://www.w3.org/2001/XMLSchema#";
	public static final String PREFIX_SCHEMA = "http://schema.org/";
	public static final String PREFIX_SKOS = "http://www.w3.org/2004/02/skos/core#";
	public static final String PREFIX_PROV = "http://www.w3.org/ns/prov#";

	// Vocabulary elements that are part of ontology language standards
	public static final String RDF_TYPE = PREFIX_RDF + "type";
	public static final String RDFS_LABEL = PREFIX_RDFS + "label";
	public static final String RDFS_LITERAL = PREFIX_RDFS + "Literal";
	public static final String RDFS_SUBCLASS_OF = PREFIX_RDFS + "subClassOf";
	public static final String OWL_THING = PREFIX_OWL + "Thing";
	public static final String OWL_CLASS = PREFIX_OWL + "Class";
	public static final String OWL_OBJECT_PROPERTY = PREFIX_OWL
			+ "ObjectProperty";
	public static final String OWL_DATATYPE_PROPERTY = PREFIX_OWL
			+ "DatatypeProperty";
	public static final String OWL_RESTRICTION = PREFIX_OWL + "Restriction";
	public static final String OWL_SOME_VALUES_FROM = PREFIX_OWL
			+ "someValuesFrom";
	public static final String OWL_ON_PROPERTY = PREFIX_OWL + "onProperty";
	public static final String OWL_COMPLEMENT_OF = PREFIX_OWL + "complementOf";
	public static final String XSD_DECIMAL = PREFIX_XSD + "decimal";
	public static final String XSD_INT = PREFIX_XSD + "int";
	public static final String XSD_DATE = PREFIX_XSD + "date";
	public static final String XSD_G_YEAR = PREFIX_XSD + "gYear";
	public static final String XSD_G_YEAR_MONTH = PREFIX_XSD + "gYearMonth";
	public static final String XSD_DATETIME = PREFIX_XSD + "dateTime";
	public static final String XSD_STRING = PREFIX_XSD + "string";

	// Prefixes for value/reference URI construction
	static final String VALUE_PREFIX_GLOBECOORDS = "VC";
	static final String VALUE_PREFIX_QUANTITY = "VQ";
	static final String VALUE_PREFIX_TIME = "VT";
	static final String VALUE_PREFIX_REFERENCE = "R";

	/**
	 * Hash map defining the OWL declaration types of the standard vocabulary.
	 * Declaring this explicitly is useful to obtain a self-contained RDF file,
	 * even when importing ontologies that provide further details on some of
	 * the vocabulary.
	 */
	static final Map<String, String> VOCABULARY_TYPES = new HashMap<String, String>();

	// Vocabulary elements that are not declared by the ontology language

	/**
	 * Property "altLabel" of SKOS.
	 */
	public static final String SKOS_ALT_LABEL = PREFIX_SKOS + "altLabel";
	static {
		VOCABULARY_TYPES.put(SKOS_ALT_LABEL, OWL_DATATYPE_PROPERTY);
	}

	/**
	 * Property "about" of schema.org.
	 */
	public static final String SCHEMA_ABOUT = PREFIX_SCHEMA + "about";
	static {
		VOCABULARY_TYPES.put(SCHEMA_ABOUT, OWL_OBJECT_PROPERTY);
	}

	/**
	 * Property "description" of schema.org.
	 */
	public static final String SCHEMA_DESCRIPTION = PREFIX_SCHEMA
			+ "description";
	static {
		VOCABULARY_TYPES.put(SCHEMA_DESCRIPTION, OWL_DATATYPE_PROPERTY);
	}

	/**
	 * Property "inLanguage" of schema.org.
	 */
	public static final String SCHEMA_IN_LANGUAGE = PREFIX_SCHEMA
			+ "inLanguage";
	static {
		VOCABULARY_TYPES.put(SCHEMA_IN_LANGUAGE, OWL_DATATYPE_PROPERTY);
	}

	/**
	 * Property "wasDerivedFrom" of the provenance ontology.
	 */
	public static final String PROV_WAS_DERIVED_FROM = PREFIX_PROV
			+ "wasDerivedFrom";
	static {
		VOCABULARY_TYPES.put(PROV_WAS_DERIVED_FROM, OWL_OBJECT_PROPERTY);
	}

	// Wikibase ontology

	/**
	 * Class for Wikibase items.
	 */
	public static final String WB_ITEM = PREFIX_WBONTO + "Item";
	static {
		VOCABULARY_TYPES.put(WB_ITEM, OWL_CLASS);
	}

	/**
	 * Class for Wikibase reference.
	 */
	public static final String WB_REFERENCE = PREFIX_WBONTO + "Reference";
	static {
		VOCABULARY_TYPES.put(WB_REFERENCE, OWL_CLASS);
	}

	/**
	 * Class for Wikibase properties.
	 */
	public static final String WB_PROPERTY = PREFIX_WBONTO + "Property";
	static {
		VOCABULARY_TYPES.put(WB_PROPERTY, OWL_CLASS);
	}

	/**
	 * Class for Wikibase statements.
	 */
	public static final String WB_STATEMENT = PREFIX_WBONTO + "Statement";
	static {
		VOCABULARY_TYPES.put(WB_STATEMENT, OWL_CLASS);
	}

	/**
	 * Class for Wikipedia articles.
	 */
	public static final String WB_ARTICLE = PREFIX_WBONTO + "Article";
	static {
		VOCABULARY_TYPES.put(WB_ARTICLE, OWL_CLASS);
	}

	/**
	 * Class for Wikibase quantity values.
	 */
	public static final String WB_QUANTITY_VALUE = PREFIX_WBONTO
			+ "QuantityValue";
	static {
		VOCABULARY_TYPES.put(WB_QUANTITY_VALUE, OWL_CLASS);
	}

	/**
	 * Class for Wikibase time values.
	 */
	public static final String WB_TIME_VALUE = PREFIX_WBONTO + "TimeValue";
	static {
		VOCABULARY_TYPES.put(WB_TIME_VALUE, OWL_CLASS);
	}

	/**
	 * Class for Wikibase globe coordinates values.
	 */
	public static final String WB_GLOBE_COORDINATES_VALUE = PREFIX_WBONTO
			+ "GlobeCoordinatesValue";
	static {
		VOCABULARY_TYPES.put(WB_GLOBE_COORDINATES_VALUE, OWL_CLASS);
	}

	/**
	 * Property for defining the datatype of a Wikibase property.
	 */
	public static final String WB_PROPERTY_TYPE = PREFIX_WBONTO
			+ "propertyType";
	static {
		VOCABULARY_TYPES.put(WB_PROPERTY_TYPE, OWL_OBJECT_PROPERTY);
	}

	/**
	 * Property for defining the globe of a globe coordinates value.
	 */
	public static final String WB_GLOBE = PREFIX_WBONTO + "globe";
	static {
		VOCABULARY_TYPES.put(WB_GLOBE, OWL_OBJECT_PROPERTY);
	}

	/**
	 * Property for defining the latitude of a globe coordinates value.
	 */
	public static final String WB_LATITUDE = PREFIX_WBONTO + "latitude";
	static {
		VOCABULARY_TYPES.put(WB_LATITUDE, OWL_DATATYPE_PROPERTY);
	}

	/**
	 * Property for defining the longitude of a globe coordinates value.
	 */
	public static final String WB_LONGITUDE = PREFIX_WBONTO + "longitude";
	static {
		VOCABULARY_TYPES.put(WB_LONGITUDE, OWL_DATATYPE_PROPERTY);
	}

	/**
	 * Property for defining the precision of a globe coordinates value.
	 */
	public static final String WB_GC_PRECISION = PREFIX_WBONTO + "gcPrecision";
	static {
		VOCABULARY_TYPES.put(WB_GC_PRECISION, OWL_DATATYPE_PROPERTY);
	}

	/**
	 * Property for defining the time point of a time value.
	 */
	public static final String WB_TIME = PREFIX_WBONTO + "time";
	static {
		VOCABULARY_TYPES.put(WB_TIME, OWL_DATATYPE_PROPERTY);
	}

	/**
	 * Property for defining the precision of a time value.
	 */
	public static final String WB_TIME_PRECISION = PREFIX_WBONTO
			+ "timePrecision";
	static {
		VOCABULARY_TYPES.put(WB_TIME_PRECISION, OWL_DATATYPE_PROPERTY);
	}

	/**
	 * Property for defining the preferred calendar of a time value.
	 */
	public static final String WB_PREFERRED_CALENDAR = PREFIX_WBONTO
			+ "preferredCalendar";
	static {
		VOCABULARY_TYPES.put(WB_PREFERRED_CALENDAR, OWL_OBJECT_PROPERTY);
	}

	/**
	 * Property for defining the numeric value of a quantity value.
	 */
	public static final String WB_NUMERIC_VALUE = PREFIX_WBONTO
			+ "numericValue";
	static {
		VOCABULARY_TYPES.put(WB_NUMERIC_VALUE, OWL_DATATYPE_PROPERTY);
	}

	/**
	 * Property for defining the lower bound of a quantity value.
	 */
	public static final String WB_LOWER_BOUND = PREFIX_WBONTO + "lowerBound";
	static {
		VOCABULARY_TYPES.put(WB_LOWER_BOUND, OWL_DATATYPE_PROPERTY);
	}

	/**
	 * Property for defining the upper bound of a quantity value.
	 */
	public static final String WB_UPPER_BOUND = PREFIX_WBONTO + "upperBound";
	static {
		VOCABULARY_TYPES.put(WB_UPPER_BOUND, OWL_DATATYPE_PROPERTY);
	}

	/**
	 * Returns a map that defines OWL types for all known vocabulary elements.
	 *
	 * @return a map from vocabulary URIs to OWL type URIs
	 */
	public static Map<String, String> getKnownVocabularyTypes() {
		return Collections.unmodifiableMap(VOCABULARY_TYPES);
	}

	/**
	 * Get the URI for the given statement.
	 *
	 * @param statement
	 *            the statement for which to create a URI
	 * @return the URI
	 */
	public static String getStatementUri(Statement statement) {
		int i = statement.getStatementId().indexOf('$') + 1;
		return statement.getClaim().getSubject().getIri() + "S"
				+ statement.getStatementId().substring(i);
	}

	/**
	 * Get the URI for the given property in the given context.
	 *
	 * @param propertyIdValue
	 *            the property id for which to create a URI
	 * @param propertyContext
	 *            the context for which the URI will be needed
	 * @return the URI
	 */
	public static String getPropertyUri(PropertyIdValue propertyIdValue,
			PropertyContext propertyContext) {
		switch (propertyContext) {
		case STATEMENT:
			return propertyIdValue.getIri() + "s";
		case VALUE:
			return propertyIdValue.getIri() + "v";
		case QUALIFIER:
			return propertyIdValue.getIri() + "q";
		case REFERENCE:
			return propertyIdValue.getIri() + "r";
		case SIMPLE_CLAIM:
			return propertyIdValue.getIri() + "c";
		default:
			return null;
		}
	}

	public static String getReferenceUri(Reference reference) {
		md.reset();
		for (SnakGroup snakgroup : reference.getSnakGroups()) {
			for (Snak snak : snakgroup.getSnaks()) {
				updateMessageDigestWithInt(md, snak.hashCode());
			}
		}

		return PREFIX_WIKIDATA + VALUE_PREFIX_REFERENCE
				+ bytesToHex(md.digest());
	}

	public static String getTimeValueUri(TimeValue value) {
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

		return PREFIX_WIKIDATA + VALUE_PREFIX_TIME + bytesToHex(md.digest());
	}

	public static String getGlobeCoordinatesValueUri(GlobeCoordinatesValue value) {
		md.reset();
		updateMessageDigestWithString(md, value.getGlobe());
		updateMessageDigestWithLong(md, value.getLatitude());
		updateMessageDigestWithLong(md, value.getLongitude());
		updateMessageDigestWithLong(md, value.getPrecision());

		return PREFIX_WIKIDATA + VALUE_PREFIX_GLOBECOORDS
				+ bytesToHex(md.digest());
	}

	public static String getQuantityValueUri(QuantityValue value) {
		md.reset();
		updateMessageDigestWithInt(md, value.getNumericValue().hashCode());
		updateMessageDigestWithInt(md, value.getLowerBound().hashCode());
		updateMessageDigestWithInt(md, value.getUpperBound().hashCode());

		return PREFIX_WIKIDATA + VALUE_PREFIX_QUANTITY
				+ bytesToHex(md.digest());
	}

	static ByteBuffer longByteBuffer = ByteBuffer.allocate(Long.SIZE / 8);

	static void updateMessageDigestWithLong(MessageDigest md, long x) {
		longByteBuffer.putLong(0, x);
		longByteBuffer.rewind(); // important!
		md.update(longByteBuffer);
	}

	static ByteBuffer intByteBuffer = ByteBuffer.allocate(Integer.SIZE / 8);

	static void updateMessageDigestWithInt(MessageDigest md, int x) {
		intByteBuffer.putInt(0, x);
		intByteBuffer.rewind(); // important!
		md.update(intByteBuffer);
	}

	static void updateMessageDigestWithString(MessageDigest md, String s) {
		if (s == null) {
			return;
		}
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
