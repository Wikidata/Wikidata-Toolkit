package org.wikidata.wdtk.datamodel.implementation.json;

import com.fasterxml.jackson.annotation.JsonCreator;

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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the inner anonymous object in the JSON type of
 * "wikibase-entityid". Not to be confused with the entityId of the main
 * document.
 *
 * @author Fredo Erxleben
 * @author Thomas Pellissier Tanon
 * @author Antonin Delpeuch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

	private final String id;

	private final String entityType;

	private final int numericId;

	/**
	 * Constructor. Supported entity types so far are "item" and "property".
	 *
	 * @param entityType
	 *            (case-sensitive)
	 * @param numericId the numerical part of the id like "111"
	 *
	 * @deprecated You should input the entity Id
	 */
	@Deprecated
	public JacksonInnerEntityId(String entityType, int numericId) {
		id = buildIdFromNumericId(entityType, numericId);
		this.entityType = entityType;
		this.numericId = numericId;
	}

	/**
	 * Constructor.
	 */
	public JacksonInnerEntityId(String id) {
		this.id = id;
		entityType = buildEntityType(id);
		numericId = buildNumericId(id);
	}
	
	/**
	 * Constructor. Creates an object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	@JsonCreator
	public JacksonInnerEntityId(
			@JsonProperty("id") String id,
			@JsonProperty("numeric-id") int numericId,
			@JsonProperty("entity-type") String entityType
		) {
		if(id == null) {
			if(entityType == null || numericId == 0) {
				throw new IllegalArgumentException("You should provide an id or an entity type and a numeric id");
			} else {
				this.id = buildIdFromNumericId(entityType, numericId);
				this.entityType = entityType;
				this.numericId = numericId;
			}
		} else {
			this.id = id;
			if(entityType == null || numericId == 0) {
				this.entityType = buildEntityType(id);
				this.numericId = buildNumericId(id);
			} else if(!id.equals(buildIdFromNumericId(entityType, numericId))) {
				throw new IllegalArgumentException("Numerical id is different from the string id");
			} else {
				this.entityType = entityType;
				this.numericId = numericId;
			}
		}
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
	 * Returns the standard string version of the entity id encoded in this
	 * value. For example, an id with entityType "item" and numericId "42" is
	 * normally identified as "Q42".
	 *
	 * @return the string id
	 * @throws IllegalArgumentException
	 *             if the entity type of this value is unknown and can thus not
	 *             be mapped to a string id
	 */
	@JsonProperty("id")
	public String getStringId() throws IllegalArgumentException {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		return o instanceof JacksonInnerEntityId
			&& (this.numericId == ((JacksonInnerEntityId) o).numericId)
			&& (this.entityType.equals(((JacksonInnerEntityId) o).entityType));
	}

	private String buildEntityType(String id) {
		if (id.length() <= 1) {
			throw new IllegalArgumentException(
						"Wikibase entity ids must have the form \"(Q|P)<positive integer>\". Given id was \""
								+ id + "\"");
		}
		switch (id.charAt(0)) {
			case 'Q':
				return JacksonInnerEntityId.JSON_ENTITY_TYPE_ITEM;
			case 'P':
				return JacksonInnerEntityId.JSON_ENTITY_TYPE_PROPERTY;
			default:
				throw new IllegalArgumentException("Unrecognized entity id: \"" + id + "\"");
		}
	}

	private int buildNumericId(String id) {
		if (id.length() <= 1) {
			throw new IllegalArgumentException(
						"Wikibase entity ids must have the form \"(Q|P)<positive integer>\". Given id was \""
								+ id + "\"");
		}
		try {
			return Integer.parseInt(id.substring(1));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Wikibase entity ids must have the form \"(Q|P)<positive integer>\". Given id was \""  + id
								+ "\"");
		}
	}

	private String buildIdFromNumericId(String entityType, int numericId) {
		switch (entityType) {
			case JSON_ENTITY_TYPE_ITEM:
				return  "Q" + numericId;
			case JSON_ENTITY_TYPE_PROPERTY:
				return "P" + numericId;
			default:
				throw new IllegalArgumentException("Entities of type \""
						+ entityType + "\" are not supported in property values.");
		}
	}
}
