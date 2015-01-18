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

import org.wikidata.wdtk.datamodel.interfaces.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Abstract Jackson implementation of {@link Value}.
 *
 * @author Fredo Erxleben
 *
 */
@JsonDeserialize(using = JacksonValueDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class JacksonValue implements Value {

	/**
	 * String used to denote the string value type in JSON.
	 */
	public static final String JSON_VALUE_TYPE_STRING = "string";
	/**
	 * String used to denote the time value type in JSON.
	 */
	public static final String JSON_VALUE_TYPE_TIME = "time";
	/**
	 * String used to denote the globe coordinates value type in JSON.
	 */
	public static final String JSON_VALUE_TYPE_GLOBE_COORDINATES = "globecoordinate";
	/**
	 * String used to denote the entity id value type in JSON.
	 */
	public static final String JSON_VALUE_TYPE_ENTITY_ID = "wikibase-entityid";
	/**
	 * String used to denote the quantity value type in JSON.
	 */
	public static final String JSON_VALUE_TYPE_QUANTITY = "quantity";
	/**
	 * String used to denote the monolingual text value type in JSON.
	 */
	public static final String JSON_VALUE_TYPE_MONOLINGUAL_TEXT = "monolingualtext";

	/**
	 * JSON type id of this value.
	 */
	private String type;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonValue() {
	}

	/**
	 * Constructor. Creates a value object with the given JSON type.
	 *
	 * @param type
	 *            JSON type constant
	 */
	public JacksonValue(String type) {
		this.type = type;
	}

	/**
	 * Sets the JSON type string to the given value. Only for use by Jackson
	 * during deserialization.
	 *
	 * @param type
	 *            new value
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Returns the JSON type string of this value. Only for use by Jackson
	 * during serialization.
	 *
	 * @return the JSON type string
	 */
	public String getType() {
		return this.type;
	}
}
