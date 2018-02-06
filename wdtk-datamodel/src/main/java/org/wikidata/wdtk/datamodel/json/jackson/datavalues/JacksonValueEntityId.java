package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

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

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Abstract base implementation of {@link EntityIdValue} for Jackson.
 *
 * @author Markus Kroetzsch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class JacksonValueEntityId extends JacksonValue implements
		EntityIdValue {

	/**
	 * The site IRI that this value refers to. This data not part of the JSON
	 * serialization of value, but is needed in WDTK to build all current types
	 * of {@link EntityIdValue} objects.
	 */
	@JsonIgnore
	private final String siteIri;

	/**
	 * Inner helper object to store the actual data. Used to get the nested JSON
	 * structure that is required here.
	 */
	protected final JacksonInnerEntityId value;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	@JsonCreator
	public JacksonValueEntityId(
			@JsonProperty("value") JacksonInnerEntityId value,
			@JacksonInject String siteIri) {
		super(JSON_VALUE_TYPE_ENTITY_ID);
		this.value = value;
		this.siteIri = siteIri;
	}

	/**
	 * Returns the inner value helper object. Only for use by Jackson during
	 * serialization.
	 *
	 * @return the inner entity id value
	 */
	@JsonProperty("value")
	public JacksonInnerEntityId getValue() {
		return value;
	}

	@JsonIgnore
	@Override
	public String getIri() {
		return this.getSiteIri().concat(this.getId());
	}

	@JsonIgnore
	@Override
	public String getId() {
		return this.value.getStringId();
	}

	@JsonIgnore
	@Override
	public String getSiteIri() {
		if (this.siteIri != null) {
			return this.siteIri;
		} else {
			throw new RuntimeException(
					"Cannot access the site IRI id of an insufficiently initialised Jackson value.");
		}
	}

}
