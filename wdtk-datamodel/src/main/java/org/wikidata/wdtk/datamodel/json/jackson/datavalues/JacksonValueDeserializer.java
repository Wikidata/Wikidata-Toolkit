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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * Custom Jackson deserializer that maps the JSON representation of Wikibase
 * values to WDTK classes. In most cases, the class to use is defined by the
 * value of the "type" field, but for entities one has to look deeper into the
 * structure to get the "entity-type" field as well. This is not possible using
 * simpler mechanisms.
 *
 * @author Markus Kroetzsch
 *
 */
public class JacksonValueDeserializer extends StdDeserializer<JacksonValue> {

	/**
	 *
	 */
	private static final long serialVersionUID = -2851517075035995962L;

	/**
	 * Constructor.
	 */
	public JacksonValueDeserializer() {
		super(JacksonValue.class);
	}

	@Override
	public JacksonValue deserialize(JsonParser jsonParser,
			DeserializationContext ctxt) throws IOException,
			JsonProcessingException {

		ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
		JsonNode root = mapper.readTree(jsonParser);
		Class<? extends JacksonValue> valueClass = getValueClass(root);

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
	private Class<? extends JacksonValue> getValueClass(JsonNode jsonNode)
			throws JsonMappingException {
		String jsonType = jsonNode.get("type").asText();

		switch (jsonType) {
		case JacksonValue.JSON_VALUE_TYPE_ENTITY_ID:
			JsonNode valueNode = jsonNode.get("value");
			if (valueNode != null) {
				String entityType = valueNode.get("entity-type").asText();
				switch (entityType) {
				case JacksonInnerEntityId.JSON_ENTITY_TYPE_ITEM:
					return JacksonValueItemId.class;
				case JacksonInnerEntityId.JSON_ENTITY_TYPE_PROPERTY:
					return JacksonValuePropertyId.class;
				default:
					throw new JsonMappingException("Entities of type \""
							+ entityType
							+ "\" are not supported as property values yet.");
				}
			}
		case JacksonValue.JSON_VALUE_TYPE_STRING:
			return JacksonValueString.class;
		case JacksonValue.JSON_VALUE_TYPE_TIME:
			return JacksonValueTime.class;
		case JacksonValue.JSON_VALUE_TYPE_GLOBE_COORDINATES:
			return JacksonValueGlobeCoordinates.class;
		case JacksonValue.JSON_VALUE_TYPE_QUANTITY:
			return JacksonValueQuantity.class;
		case JacksonValue.JSON_VALUE_TYPE_MONOLINGUAL_TEXT:
			return JacksonValueMonolingualText.class;
		default:
			throw new JsonMappingException("Property values of type \""
					+ jsonType + "\" are not supported yet.");
		}
	}
}
