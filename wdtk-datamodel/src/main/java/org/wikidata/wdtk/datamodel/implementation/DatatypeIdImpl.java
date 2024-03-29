package org.wikidata.wdtk.datamodel.implementation;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Jackson implementation of {@link DatatypeIdValue}. This is not actually
 * present in JSON but needed to satisfy the interface.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 *
 */
public class DatatypeIdImpl implements DatatypeIdValue {

	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_ITEM} in JSON.
	 */
	public static final String JSON_DT_ITEM = "wikibase-item";
	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_PROPERTY} in JSON.
	 */
	public static final String JSON_DT_PROPERTY = "wikibase-property";
	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_GLOBE_COORDINATES} in JSON.
	 */
	public static final String JSON_DT_GLOBE_COORDINATES = "globe-coordinate";
	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_ITEM} in JSON.
	 */
	public static final String JSON_DT_URL = "url";
	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_COMMONS_MEDIA} in JSON.
	 */
	public static final String JSON_DT_COMMONS_MEDIA = "commonsMedia";
	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_TIME} in JSON.
	 */
	public static final String JSON_DT_TIME = "time";
	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_QUANTITY} in JSON.
	 */
	public static final String JSON_DT_QUANTITY = "quantity";
	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_STRING} in JSON.
	 */
	public static final String JSON_DT_STRING = "string";
	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_MONOLINGUAL_TEXT} in JSON.
	 */
	public static final String JSON_DT_MONOLINGUAL_TEXT = "monolingualtext";
	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_EXTERNAL_ID} in JSON.
	 */
	public static final String JSON_DT_EXTERNAL_ID = "external-id";
	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_MATH} in JSON.
	 */
	public static final String JSON_DT_MATH = "math";
	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_GEO_SHAPE} in JSON.
	 */
	public static final String JSON_DT_GEO_SHAPE = "geo-shape";
	/**
	 * String used to refer to the property datatype
	 * {@link DatatypeIdValue#DT_EDTF} in JSON.
	 */
	public static final String JSON_DT_EDTF = "edtf";

	private static final Pattern JSON_DATATYPE_PATTERN = Pattern.compile("^[a-zA-Z\\-]+$");
	private static final Pattern DATATYPE_ID_PATTERN = Pattern.compile("^http://wikiba\\.se/ontology#([a-zA-Z]+)$");

	/**
	 * Datatype IRI as used in Wikidata Toolkit.
	 */
	private final String iri;

	/**
	 * JSON representation of the datatype. We store this as well
	 * because the conversion mechanism between JSON datatypes and
	 * datatype URIs is sadly not perfect: see
	 * <a href="https://github.com/Wikidata/Wikidata-Toolkit/issues/716">issue #716</a>.
	 */
	private final String jsonString;

	/**
	 * Returns the WDTK datatype IRI for the property datatype as represented by
	 * the given JSON datatype string.
	 *
	 * @param jsonDatatype
	 *            the JSON datatype string; case-sensitive
	 * @throws IllegalArgumentException
	 *             if the given datatype string is not known
	 */
	public static String getDatatypeIriFromJsonDatatype(String jsonDatatype) {
		switch (jsonDatatype) {
		case JSON_DT_ITEM:
			return DT_ITEM;
		case JSON_DT_PROPERTY:
			return DT_PROPERTY;
		case JSON_DT_GLOBE_COORDINATES:
			return DT_GLOBE_COORDINATES;
		case JSON_DT_URL:
			return DT_URL;
		case JSON_DT_COMMONS_MEDIA:
			return DT_COMMONS_MEDIA;
		case JSON_DT_TIME:
			return DT_TIME;
		case JSON_DT_QUANTITY:
			return DT_QUANTITY;
		case JSON_DT_STRING:
			return DT_STRING;
		case JSON_DT_MONOLINGUAL_TEXT:
			return DT_MONOLINGUAL_TEXT;
		case JSON_DT_EDTF:
			return DT_EDTF;
		default:

			String[] parts = jsonDatatype.split("-");
			for(int i = 0; i < parts.length; i++) {
				parts[i] = StringUtils.capitalize(parts[i]);
			}
			return "http://wikiba.se/ontology#" + StringUtils.join(parts);
		}
	}
	
	/**
	 * Returns the JSON datatype for the property datatype as represented by
	 * the given WDTK datatype IRI string.
	 *
	 * @param datatypeIri
	 *            the WDTK datatype IRI string; case-sensitive
	 * @throws IllegalArgumentException
	 *             if the given datatype string is not known
	 * @deprecated this method is unreliable and will be removed in a future release.
	 */
	public static String getJsonDatatypeFromDatatypeIri(String datatypeIri) {
		switch (datatypeIri) {
			case DatatypeIdValue.DT_ITEM:
				return DatatypeIdImpl.JSON_DT_ITEM;
			case DatatypeIdValue.DT_GLOBE_COORDINATES:
				return DatatypeIdImpl.JSON_DT_GLOBE_COORDINATES;
			case DatatypeIdValue.DT_URL:
				return DatatypeIdImpl.JSON_DT_URL;
			case DatatypeIdValue.DT_COMMONS_MEDIA:
				return DatatypeIdImpl.JSON_DT_COMMONS_MEDIA;
			case DatatypeIdValue.DT_TIME:
				return DatatypeIdImpl.JSON_DT_TIME;
			case DatatypeIdValue.DT_QUANTITY:
				return DatatypeIdImpl.JSON_DT_QUANTITY;
			case DatatypeIdValue.DT_STRING:
				return DatatypeIdImpl.JSON_DT_STRING;
			case DatatypeIdValue.DT_MONOLINGUAL_TEXT:
				return DatatypeIdImpl.JSON_DT_MONOLINGUAL_TEXT;
			case DatatypeIdValue.DT_PROPERTY:
				return DatatypeIdImpl.JSON_DT_PROPERTY;
			case DatatypeIdValue.DT_EDTF:
				return DatatypeIdImpl.JSON_DT_EDTF;
			default:
				//We apply the reverse algorithm of JacksonDatatypeId::getDatatypeIriFromJsonDatatype
				Matcher matcher = DATATYPE_ID_PATTERN.matcher(datatypeIri);
				if(!matcher.matches()) {
					throw new IllegalArgumentException("Unknown datatype: " + datatypeIri);
				}
		
				StringBuilder jsonDatatypeBuilder = new StringBuilder();
				for(char ch : StringUtils.uncapitalize(matcher.group(1)).toCharArray()) {
					if(Character.isUpperCase(ch)) {
						jsonDatatypeBuilder
								.append('-')
								.append(Character.toLowerCase(ch));
					} else {
						jsonDatatypeBuilder.append(ch);
					}
				}
				return jsonDatatypeBuilder.toString();
		}
	}
	
	/**
	 * Copy constructor.
	 */
	public DatatypeIdImpl(DatatypeIdValue other) {
		this.iri = other.getIri();
		this.jsonString = other.getJsonString();
	}

	/**
	 * Constructs an object representing the datatype id from a IRI denoting
	 * the datatype. It also tries to determine the JSON datatype based on this
	 * IRI, based on a buggy heuristic. If you also happen to have the JSON datatype
	 * at hand, better use {@link DatatypeIdImpl(String, String)}.
	 *
	 * @param iri
	 *             the WDTK IRI for the datatype
	 * @throws IllegalArgumentException
	 *             if the given datatype string could not be matched to a known
	 *             datatype or was null
	 * @deprecated use {@link #DatatypeIdImpl(String, String)}
	 */
	public DatatypeIdImpl(String iri)
			throws IllegalArgumentException {
		Validate.notNull(iri, "An IRI must be provided to create a DatatypeIdValue");
		this.iri = iri;
		// the JSON datatype is not supplied, so we fall back on our buggy heuristic
		// to guess how it should be represented in JSON.
		this.jsonString = getJsonDatatypeFromDatatypeIri(this.iri);
	}

	/**
	 * Constructs an object representing the datatype id from an IRI denoting the datatype,
	 * as well as a string corresponding to its JSON serialization. This constructor
	 * is meant to be used for JSON deserialization.
	 *
	 * @param iri
	 *             the WDTK IRI for the datatype. This can be null.
	 * @param jsonString
	 *             the JSON representation of the datatype. This cannot be null.
	 * @throws IllegalArgumentException
	 *             if the given datatype string could not be matched to a known
	 *             datatype or was null
	 */
	public DatatypeIdImpl(String iri, String jsonString)
			throws IllegalArgumentException {
		Validate.notNull(jsonString, "A JSON representation of the datatype must be provided to create a DatatypeIdValue");
		if(!JSON_DATATYPE_PATTERN.matcher(jsonString).matches()) {
			throw new IllegalArgumentException("Invalid JSON datatype \"" + jsonString + "\"");
		}
		this.jsonString = jsonString;
		this.iri = iri != null ? iri : getDatatypeIriFromJsonDatatype(jsonString);
	}
	
	/**
	 * Returns the string used to represent this datatype in JSON.
	 */
	@Override
	public String getJsonString() {
		return this.jsonString;
	}

	@Override
	public String getIri() {
		return this.iri;
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

	@Override
	public boolean equals(Object o) {
		return Equality.equalsDatatypeIdValue(this, o);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}
}
