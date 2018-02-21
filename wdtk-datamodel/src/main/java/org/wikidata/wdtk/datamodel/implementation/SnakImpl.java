package org.wikidata.wdtk.datamodel.implementation;

import org.apache.commons.lang3.Validate;

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

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Value;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract Jackson implementation of {@link Snak}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "snaktype")
@JsonSubTypes({
		@Type(value = NoValueSnakImpl.class, name = SnakImpl.JSON_SNAK_TYPE_NOVALUE),
		@Type(value = SomeValueSnakImpl.class, name = SnakImpl.JSON_SNAK_TYPE_SOMEVALUE),
		@Type(value = ValueSnakImpl.class, name = SnakImpl.JSON_SNAK_TYPE_VALUE) })
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class SnakImpl implements Snak {

	/**
	 * Type string used to denote value snaks in JSON.
	 */
	public static final String JSON_SNAK_TYPE_VALUE = "value";
	/**
	 * Type string used to denote somevalue snaks in JSON.
	 */
	public static final String JSON_SNAK_TYPE_SOMEVALUE = "somevalue";
	/**
	 * Type string used to denote novalue snaks in JSON.
	 */
	public static final String JSON_SNAK_TYPE_NOVALUE = "novalue";

	/**
	 * Value of the "property" field in JSON, e.g., "P31".
	 */
	private final String property;

	/**
	 * The site IRI of this snak. This is needed since the site that this snak
	 * refers to is not part of the JSON serialization of snaks, but is needed
	 * in WDTK to build {@link PropertyIdValue} objects etc. Thus, it is
	 * necessary to set this information after each deserialization.
	 */
	@JsonIgnore
	private final String siteIri;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	@JsonCreator
	protected SnakImpl(
			@JsonProperty("property") String property,
			@JacksonInject("siteIri") String siteIri) {
		Validate.notNull(property);
		this.property = property;
		Validate.notNull(siteIri);
		this.siteIri = siteIri;
	}

	/**
	 * Returns the property id string. Only for use by Jackson during
	 * serialization.
	 *
	 * @return the property id string
	 */
	@JsonProperty("property")
	public String getProperty() {
		return this.property;
	}

	@JsonIgnore
	@Override
	public PropertyIdValue getPropertyId() {
		return Datamodel.makePropertyIdValue(property, this.siteIri);
	}

	@JsonIgnore
	@Override
	public Value getValue() {
		return null;
	}
	
	@JsonProperty("snaktype")
	public abstract String getSnakType();
}
