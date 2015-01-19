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

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract Jackson implementation of {@link Snak}.
 *
 * @author Fredo Erxleben
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "snaktype")
@JsonSubTypes({
		@Type(value = JacksonNoValueSnak.class, name = JacksonSnak.JSON_SNAK_TYPE_NOVALUE),
		@Type(value = JacksonSomeValueSnak.class, name = JacksonSnak.JSON_SNAK_TYPE_SOMEVALUE),
		@Type(value = JacksonValueSnak.class, name = JacksonSnak.JSON_SNAK_TYPE_VALUE) })
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class JacksonSnak implements Snak {

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
	private String property;

	/**
	 * The site IRI of this snak. This is needed since the site that this snak
	 * refers to is not part of the JSON serialization of snaks, but is needed
	 * in WDTK to build {@link PropertyIdValue} objects etc. Thus, it is
	 * necessary to set this information after each deserialization.
	 */
	@JsonIgnore
	String siteIri = null;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	protected JacksonSnak() {
	}

	/**
	 * Returns the property id string. Only for use by Jackson during
	 * serialization.
	 *
	 * @return the property id string
	 */
	public String getProperty() {
		return this.property;
	}

	/**
	 * Sets the property id string to the given value. Only for use by Jackson
	 * during deserialization.
	 *
	 * @param property
	 *            new value
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	@JsonIgnore
	@Override
	public PropertyIdValue getPropertyId() {
		if (this.siteIri != null) {
			return Datamodel.makePropertyIdValue(property, this.siteIri);
		} else {
			throw new RuntimeException(
					"Cannot access the property id of an insufficiently initialised Jackson snak.");
			// return Datamodel.makeWikidataPropertyIdValue(property);
		}
	}

	/**
	 * Sets the IRI of the site this snak belongs to. This provides the snak
	 * with information about the site IRI of its components, which is not part
	 * of the JSON serialization of snaks. This method should only be used
	 * during deserialization.
	 *
	 * @param parentDocument
	 *            new value
	 */
	@JsonIgnore
	void setSiteIri(String siteIri) {
		this.siteIri = siteIri;
	}

}
