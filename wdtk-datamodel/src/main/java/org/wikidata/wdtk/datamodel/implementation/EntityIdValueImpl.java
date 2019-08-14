package org.wikidata.wdtk.datamodel.implementation;

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;

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

/**
 * Abstract base implementation of {@link EntityIdValue} for Jackson.
 *
 * @author Markus Kroetzsch
 * @author Fredo Erxleben
 * @author Thomas Pellissier Tanon
 * @author Antonin Delpeuch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class EntityIdValueImpl extends ValueImpl implements
		EntityIdValue {

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
	/**
	 * The string used in JSON to denote the type of entity id values that are
	 * lexemes.
	 */
	public final static String JSON_ENTITY_TYPE_LEXEME = "lexeme";
	/**
	 * The string used in JSON to denote the type of entity id values that are
	 * lexemes forms.
	 */
	public final static String JSON_ENTITY_TYPE_FORM = "form";
	/**
	 * The string used in JSON to denote the type of entity id values that are
	 * lexemes senses.
	 */
	public final static String JSON_ENTITY_TYPE_SENSE = "sense";
	/**
	 * The string used in JSON to denote the type of entity id values that are
	 * media info.
	 */
	public final static String JSON_ENTITY_TYPE_MEDIA_INFO = "mediainfo";

	/**
	 * The site IRI that this value refers to. This data not part of the JSON
	 * serialization of value, but is needed in WDTK to build all current types
	 * of {@link EntityIdValue} objects.
	 */
	private final String siteIri;

	/**
	 * Inner helper object to store the actual data. Used to get the nested JSON
	 * structure that is required here.
	 */
	private final JacksonInnerEntityId value;
	
	/**
	 * Constructor.
	 * @param id
	 * 		the identifier of the entity, such as "Q42"
	 * @param siteIri
	 *      the siteIRI that this value refers to
	 */
	protected EntityIdValueImpl(
			String id,
			String siteIri) {
		super(JSON_VALUE_TYPE_ENTITY_ID);
		this.value = new JacksonInnerEntityId(id);
		Validate.notNull(siteIri, "Entity site IRIs cannot be null");
		this.siteIri = siteIri;
	}

	/**
	 * Constructor used for deserialization with Jackson.
	 */
	@JsonCreator
	protected EntityIdValueImpl(
			@JsonProperty("value") JacksonInnerEntityId value,
			@JacksonInject String siteIri) {
		super(JSON_VALUE_TYPE_ENTITY_ID);
		this.value = value;
		this.siteIri = siteIri;
	}

	/**
	 * Parses an item id
	 *
	 * @param id
	 * 		the identifier of the entity, such as "Q42"
	 * @param siteIri
	 *      the siteIRI that this value refers to
	 * @throws IllegalArgumentException
	 *      if the id is invalid
	 */
	static EntityIdValue fromId(String id, String siteIri) {
		switch (guessEntityTypeFromId(id)) {
			case EntityIdValueImpl.JSON_ENTITY_TYPE_ITEM:
				return new ItemIdValueImpl(id, siteIri);
			case EntityIdValueImpl.JSON_ENTITY_TYPE_PROPERTY:
				return new PropertyIdValueImpl(id, siteIri);
			case EntityIdValueImpl.JSON_ENTITY_TYPE_LEXEME:
				return new LexemeIdValueImpl(id, siteIri);
			case EntityIdValueImpl.JSON_ENTITY_TYPE_FORM:
				return new FormIdValueImpl(id, siteIri);
			case EntityIdValueImpl.JSON_ENTITY_TYPE_SENSE:
				return new SenseIdValueImpl(id, siteIri);
				case EntityIdValueImpl.JSON_ENTITY_TYPE_MEDIA_INFO:
				return new MediaInfoIdValueImpl(id, siteIri);
			default:
				throw new IllegalArgumentException("Entity id \"" + id + "\" is not supported.");
		}
	}

	/**
	 * RReturns the entity type of the id like "item" or "property"
	 *
	 * @param id
	 * 		the identifier of the entity, such as "Q42"
	 * @throws IllegalArgumentException
	 *      if the id is invalid
	 */
	static String guessEntityTypeFromId(String id) {
		if(id.isEmpty()) {
			throw new IllegalArgumentException("Entity ids should not be empty.");
		}
		switch (id.charAt(0)) {
			case 'L':
				if(id.contains("-F")) {
					return JSON_ENTITY_TYPE_FORM;
				} else if(id.contains("-S")) {
					return JSON_ENTITY_TYPE_SENSE;
				} else {
					return JSON_ENTITY_TYPE_LEXEME;
				}
			case 'M':
				return JSON_ENTITY_TYPE_MEDIA_INFO;
			case 'P':
				return JSON_ENTITY_TYPE_PROPERTY;
			case 'Q':
				return JSON_ENTITY_TYPE_ITEM;
			default:
				throw new IllegalArgumentException("Entity id \"" + id + "\" is not supported.");
		}
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

	protected void assertHasJsonEntityType(String expectedType) {
		if(!expectedType.equals(value.entityType)) {
			throw new IllegalArgumentException(
					"The value should have the entity-type \"" + expectedType + "\": " + this
			);
		}
	}

	/**
	 * Helper object that represents the JSON object structure of the value.
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class JacksonInnerEntityId {

		private final String id;

		private final String entityType;

		private final int numericId;

		JacksonInnerEntityId(String id) {
			this.id = id;
			entityType = guessEntityTypeFromId(id);
			numericId = buildNumericId(id);
		}

		/**
		 * Creates an object that can be populated during JSON deserialization.
		 * Should only be used by Jackson for this very purpose.
		 */
		@JsonCreator
		JacksonInnerEntityId(
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
					this.entityType = guessEntityTypeFromId(id);
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
		 */
		@JsonProperty("id")
		public String getStringId() {
			return id;
		}

		private int buildNumericId(String id) {
			if (id.length() <= 1) {
				throw new IllegalArgumentException(
							"Wikibase entity ids must have the form \"(L|P|Q)<positive integer>\". Given id was \""
									+ id + "\"");
			}
			try {
				return Integer.parseInt(id.substring(1));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
						"Wikibase entity ids must have the form \"(L|P|Q)<positive integer>\". Given id was \""  + id
									+ "\"");
			}
		}

		private String buildIdFromNumericId(String entityType, int numericId) {
			switch (entityType) {
				case JSON_ENTITY_TYPE_ITEM:
					return  "Q" + numericId;
				case JSON_ENTITY_TYPE_LEXEME:
					return  "L" + numericId;
				case JSON_ENTITY_TYPE_PROPERTY:
					return "P" + numericId;
				case JSON_ENTITY_TYPE_MEDIA_INFO:
					return "M" + numericId;
				default:
					throw new IllegalArgumentException("Entities of type \""
							+ entityType + "\" are not supported in property values.");
			}
		}
	}
}
