package org.wikidata.wdtk.datamodel.json.jackson.serializers;

import java.io.IOException;

import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * A serializer implementation for the StatementRank enumeration. This is
 * necessary since Java enumerations are in upper case but the Json counterpart
 * is in lower case.
 * 
 * @author Fredo Erxleben
 * 
 */
public class StatementRankSerializer extends JsonSerializer<StatementRank> {

	@Override
	public void serialize(StatementRank value, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonProcessingException {

		jgen.writeString(value.name().toLowerCase());

	}

}
