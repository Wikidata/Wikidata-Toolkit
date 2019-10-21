package org.wikidata.wdtk.datamodel.implementation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

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

import java.io.IOException;

/**
 * Abstract Jackson implementation of {@link Value}.
 *
 * @author Fredo Erxleben
 * @author Markus Kroetzsch
 *
 */
@JsonDeserialize(using = ValueImpl.JacksonDeserializer.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ValueImpl implements Value {

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
	private final String type;

	/**
	 * Constructor. Creates a value object with the given JSON type.
	 *
	 * @param type
	 *            JSON type constant
	 */
	public ValueImpl(String type) {
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

	/**
	 * Custom Jackson deserializer that maps the JSON representation of Wikibase
	 * values to WDTK classes. In most cases, the class to use is defined by the
	 * value of the "type" field, but for entities one has to look deeper into the
	 * structure to get the "entity-type" field as well. This is not possible using
	 * simpler mechanisms.
	 *
	 */
	static class JacksonDeserializer extends StdDeserializer<ValueImpl> {

		JacksonDeserializer() {
			super(ValueImpl.class);
		}

		@Override
		public ValueImpl deserialize(JsonParser jsonParser,
				DeserializationContext ctxt) throws IOException {

			ObjectCodec mapper = jsonParser.getCodec();
			JsonNode root = mapper.readTree(jsonParser);
			Class<? extends ValueImpl> valueClass = getValueClass(root, jsonParser);

			return mapper.treeToValue(root, valueClass);
		}

		/**
		 * Finds the Java class to use for deserializing the JSON structure
		 * represented by the given node.
		 *
		 * @param jsonNode
		 *            the JSON node that represents the value to deserialize
		 * @return the Java class to use for deserialization
		 * @throws JsonMappingException
		 *             if we do not have a class for the given JSON
		 */
		private Class<? extends ValueImpl> getValueClass(JsonNode jsonNode, JsonParser jsonParser)
				throws JsonMappingException {
			String jsonType = jsonNode.get("type").asText();

			switch (jsonType) {
			case JSON_VALUE_TYPE_ENTITY_ID:
				JsonNode valueNode = jsonNode.get("value");
				if (valueNode != null) {
					if(valueNode.has("entity-type")) {
						try {
							return getValueClassFromEntityType(valueNode.get("entity-type").asText());
						} catch (IllegalArgumentException e) {
							return UnsupportedEntityIdValueImpl.class;
						}
					} else if(valueNode.has("id")) {
						try {
							return getValueClassFromEntityType(
									EntityIdValueImpl.guessEntityTypeFromId(valueNode.get("id").asText())
							);
						} catch (IllegalArgumentException e) {
							return UnsupportedEntityIdValueImpl.class;
						}
					} else {
						throw new JsonMappingException(jsonParser, "Unexpected entity id serialization");
					}

				}
			case JSON_VALUE_TYPE_STRING:
				return StringValueImpl.class;
			case JSON_VALUE_TYPE_TIME:
				return TimeValueImpl.class;
			case JSON_VALUE_TYPE_GLOBE_COORDINATES:
				return GlobeCoordinatesValueImpl.class;
			case JSON_VALUE_TYPE_QUANTITY:
				return QuantityValueImpl.class;
			case JSON_VALUE_TYPE_MONOLINGUAL_TEXT:
				return MonolingualTextValueImpl.class;
			default:
				return UnsupportedValueImpl.class;
			}
		}

		private Class<? extends ValueImpl> getValueClassFromEntityType(String entityType) {
			switch (entityType) {
				case EntityIdValueImpl.JSON_ENTITY_TYPE_ITEM:
					return ItemIdValueImpl.class;
				case EntityIdValueImpl.JSON_ENTITY_TYPE_LEXEME:
					return LexemeIdValueImpl.class;
				case EntityIdValueImpl.JSON_ENTITY_TYPE_PROPERTY:
					return PropertyIdValueImpl.class;
				case EntityIdValueImpl.JSON_ENTITY_TYPE_FORM:
					return FormIdValueImpl.class;
				case EntityIdValueImpl.JSON_ENTITY_TYPE_SENSE:
					return SenseIdValueImpl.class;
				case EntityIdValueImpl.JSON_ENTITY_TYPE_MEDIA_INFO:
					return MediaInfoIdValueImpl.class;
				default:
					throw new IllegalArgumentException("Entities of type \"" + entityType + "\" are not supported.");
			}
		}
	}
}
