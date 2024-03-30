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

	private final DatatypeJsonUtils datatypeJsonUtils = new DatatypeJsonUtils();

	private static final Pattern JSON_DATATYPE_PATTERN = Pattern.compile("^[a-zA-Z\\-]+$");

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
		this.jsonString = datatypeJsonUtils.getJsonDatatypeFromDatatypeIri(this.iri);
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
		this.iri = iri != null ? iri : datatypeJsonUtils.getDatatypeIriFromJsonDatatype(jsonString);
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
