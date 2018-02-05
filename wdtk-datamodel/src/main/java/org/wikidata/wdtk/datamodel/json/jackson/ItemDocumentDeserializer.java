package org.wikidata.wdtk.datamodel.json.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class ItemDocumentDeserializer extends StdDeserializer<JacksonItemDocument> {

	private static final long serialVersionUID = -449410827877729321L;

	protected ItemDocumentDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public JacksonItemDocument deserialize(JsonParser parser, DeserializationContext context)
			throws IOException, JsonProcessingException {
		JsonNode node = parser.getCodec().readTree(parser);
		// ObjectMapper mapper = parser.
		// TODO Auto-generated method stub
		return null;
	}

}
