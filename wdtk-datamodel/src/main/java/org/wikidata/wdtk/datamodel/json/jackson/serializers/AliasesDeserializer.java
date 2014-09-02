package org.wikidata.wdtk.datamodel.json.jackson.serializers;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.json.jackson.MonolingualTextValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.documents.EntityDocumentImpl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * A deserializer implementation for the aliases in an EntityDocument. <b> This
 * is part of a Workaround. </b>
 * 
 * @see EntityDocumentImpl setAliases()
 * 
 * @author Fredo Erxleben
 * 
 */
public class AliasesDeserializer extends
		JsonDeserializer<Map<String, List<MonolingualTextValueImpl>>> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<MonolingualTextValueImpl>> deserialize(JsonParser jp,
			DeserializationContext ctxt) throws IOException,
			JsonProcessingException {

		Map<String, List<MonolingualTextValueImpl>> contents = new HashMap<>();

		if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
			contents = jp.readValueAs(contents.getClass());
		}

		return contents;

	}

}
