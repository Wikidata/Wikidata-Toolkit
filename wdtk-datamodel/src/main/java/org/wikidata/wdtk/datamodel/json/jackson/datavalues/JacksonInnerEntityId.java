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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the inner anonymous object in the JSON type of
 * "wikibase-entityid". Not to be confused with the entityId of the main
 * document.
 *
 * @author Fredo Erxleben
 *
 */
public class JacksonInnerEntityId {
	// TODO maybe replace IllegalArgumentException with a checked one; maybe do
	// the check when the type is set

	/**
	 * The string used in JSON to denote the type of entity id values that are
	 * items.
	 */
	public final static String JSON_ENTITY_TYPE_ITEM = "item";
	/**
	 * The string used in JSON to denote the type of entity id values that are
	 * properties.
	 */
	public final static String JSON_ENTITY_TYPE_PROPERTY = "property";

	@JsonProperty("entity-type")
	private String entityType;

	@JsonProperty("numeric-id")
	private int numericId;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonInnerEntityId() {
	}

	/**
	 * Constructor. Supported entity types so far are "item" and "property".
	 *
	 * @param entityType
	 *            (case-sensitive)
	 * @param numericId
	 */
	public JacksonInnerEntityId(String entityType, int numericId) {
		setJsonEntityType(entityType);
		this.numericId = numericId;
	}

	/**
	 * Returns the entity type string as used in JSON. Only for use by Jackson
	 * during serialization.
	 *
	 * @return the entity type string
	 */
	@JsonProperty("entity-type")
	public String getJsonEntityType() {
		return entityType;
	}

	/**
	 * Sets the entity type string to the given value. Only for use by Jackson
	 * during deserialization.
	 *
	 * @param entityType
	 *            new value
	 */
	@JsonProperty("entity-type")
	public void setJsonEntityType(String entityType) {
		this.entityType = entityType;
	}

	/**
	 * Returns the numeric item id as used in JSON. Only for use by Jackson
	 * during serialization.
	 *
	 * @return the numeric entity id
	 */
	@JsonProperty("numeric-id")
	public int getNumericId() {
		return numericId;
	}

	/**
	 * Sets the numeric item id to the given value. Only for use by Jackson
	 * during deserialization.
	 *
	 * @param numericId
	 *            new value
	 */
	@JsonProperty("numeric-id")
	public void setNumericId(int numericId) {
		this.numericId = numericId;
	}

	/**
	 * Returns the standard string version of the entity id encoded in this
	 * value. For example, an id with entityType "item" and numericId "42" is
	 * normally identified as "Q42".
	 *
	 * @return the string id
	 * @throws IllegalArgumentException
	 *             if the entity type of this value is unknown and can thus not
	 *             be mapped to a string id
	 */
	@JsonIgnore
	public String getStringId() throws IllegalArgumentException {
		switch (entityType) {
		case JSON_ENTITY_TYPE_ITEM:
			return "Q" + this.numericId;
		case JSON_ENTITY_TYPE_PROPERTY:
			return "P" + this.numericId;
		default:
			throw new IllegalArgumentException("Entities of type \""
					+ entityType + "\" are not supported in property values.");
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof JacksonInnerEntityId)) {
			return false;
		}

		return (this.numericId == ((JacksonInnerEntityId) o).numericId)
				&& (this.entityType
						.equals(((JacksonInnerEntityId) o).entityType));
	}

}
