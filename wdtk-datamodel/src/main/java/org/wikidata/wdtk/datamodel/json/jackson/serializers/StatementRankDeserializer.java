package org.wikidata.wdtk.datamodel.json.jackson.serializers;

import java.io.IOException;

import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * A deserializer implementation for the StatementRank enumeration. This is
 * necessary since Java enumerations are in upper case but the Json counterpart
 * is in lower case.
 * 
 * @author Fredo Erxleben
 * 
 */
public class StatementRankDeserializer extends JsonDeserializer<StatementRank> {

	@Override
	public StatementRank deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		
		return StatementRank.valueOf(jp.getText().toUpperCase());
	}


}
