package org.wikidata.wdtk.datamodel.json.jackson;

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

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

/**
 * Jackson implementation of {@link DatatypeIdValue}. This is not actually
 * present in JSON but needed to satisfy the interface.
 *
 * @author Fredo Erxleben
 *
 */
public class JacksonDatatypeId implements DatatypeIdValue {

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
	 * Datatype IRI as used in Wikidata Toolkit.
	 */
	private final String iri;

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
		default:
			throw new IllegalArgumentException("Unknown JSON datatype \""
					+ jsonDatatype + "\"");
		}
	}

	/**
	 * Constructs an object representing the datatype id from a string denoting
	 * the datatype. It also sets the correct IRI for the datatype.
	 * <p>
	 * TODO Review the utility of this constructor.
	 *
	 * @param jsonDatatype
	 *            denotes the datatype which to represent; case-sensitive
	 * @throws IllegalArgumentException
	 *             if the given datatype string could not be matched to a known
	 *             datatype or was null
	 */
	public JacksonDatatypeId(String jsonDatatype)
			throws IllegalArgumentException {
		this.iri = getDatatypeIriFromJsonDatatype(jsonDatatype);
	}

	@Override
	public String getIri() {
		return this.iri;
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
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
