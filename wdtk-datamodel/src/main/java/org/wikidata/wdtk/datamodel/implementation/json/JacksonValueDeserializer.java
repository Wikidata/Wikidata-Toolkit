package org.wikidata.wdtk.datamodel.implementation.json;

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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

import org.wikidata.wdtk.datamodel.implementation.GlobeCoordinatesValueImpl;
import org.wikidata.wdtk.datamodel.implementation.ItemIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.MonolingualTextValueImpl;
import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.QuantityValueImpl;
import org.wikidata.wdtk.datamodel.implementation.StringValueImpl;
import org.wikidata.wdtk.datamodel.implementation.TimeValueImpl;
import org.wikidata.wdtk.datamodel.implementation.ValueImpl;

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
public class JacksonValueDeserializer extends StdDeserializer<ValueImpl> {

	/**
	 *
	 */
	private static final long serialVersionUID = -2851517075035995962L;

	/**
	 * Constructor.
	 */
	public JacksonValueDeserializer() {
		super(ValueImpl.class);
	}

	@Override
	public ValueImpl deserialize(JsonParser jsonParser,
			DeserializationContext ctxt) throws IOException {

		ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
		JsonNode root = mapper.readTree(jsonParser);
		Class<? extends ValueImpl> valueClass = getValueClass(root);

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
	private Class<? extends ValueImpl> getValueClass(JsonNode jsonNode)
			throws JsonMappingException {
		String jsonType = jsonNode.get("type").asText();

		switch (jsonType) {
		case ValueImpl.JSON_VALUE_TYPE_ENTITY_ID:
			JsonNode valueNode = jsonNode.get("value");
			if (valueNode != null) {
				if(valueNode.has("entity-type")) {
					String entityType = valueNode.get("entity-type").asText();
					switch (entityType) {
						case JacksonInnerEntityId.JSON_ENTITY_TYPE_ITEM:
							return ItemIdValueImpl.class;
						case JacksonInnerEntityId.JSON_ENTITY_TYPE_PROPERTY:
							return PropertyIdValueImpl.class;
						default:
							throw new JsonMappingException("Entities of type \""
									+ entityType
									+ "\" are not supported as property values yet.");
					}
				} else if(valueNode.has("id")) {
					String id = valueNode.get("id").asText();
					if(id.isEmpty()) {
						throw new JsonMappingException("Entity ids should not be empty.");
					}
					switch (id.charAt(0)) {
						case 'Q':
							return ItemIdValueImpl.class;
						case 'P':
							return PropertyIdValueImpl.class;
						default:
							throw new JsonMappingException("Entity id \"" + id
									+ "\" is not supported as property values yet.");
					}
				} else {
					throw new JsonMappingException("Unexpected entity id serialization");
				}

			}
		case ValueImpl.JSON_VALUE_TYPE_STRING:
			return StringValueImpl.class;
		case ValueImpl.JSON_VALUE_TYPE_TIME:
			return TimeValueImpl.class;
		case ValueImpl.JSON_VALUE_TYPE_GLOBE_COORDINATES:
			return GlobeCoordinatesValueImpl.class;
		case ValueImpl.JSON_VALUE_TYPE_QUANTITY:
			return QuantityValueImpl.class;
		case ValueImpl.JSON_VALUE_TYPE_MONOLINGUAL_TEXT:
			return MonolingualTextValueImpl.class;
		default:
			throw new JsonMappingException("Property values of type \""
					+ jsonType + "\" are not supported yet.");
		}
	}
}
