package org.wikidata.wdtk.datamodel.json.jackson;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * A deserializer implementation for the aliases in an
 * {@link JacksonTermedStatementDocument}.
 * <p>
 * It implements a workaround to cope with empty aliases being represented as
 * <code>"aliases":[]</code> despite its declaration as map and not as list or
 * array. This is neither nice nor fast, and should be obsolete as soon as
 * possible.
 *
 * @see JacksonTermedStatementDocument#setAliases(Map)
 *
 * @author Fredo Erxleben
 *
 */
public class AliasesDeserializer extends
		JsonDeserializer<Map<String, List<JacksonMonolingualTextValue>>> {

	@Override
	public Map<String, List<JacksonMonolingualTextValue>> deserialize(
			JsonParser jp, DeserializationContext ctxt) throws IOException,
			JsonProcessingException {

		Map<String, List<JacksonMonolingualTextValue>> contents = new HashMap<>();

		try {
			JsonNode node = jp.getCodec().readTree(jp);
			if (!node.isArray()) {
				Iterator<Entry<String, JsonNode>> nodeIterator = node.fields();
				while (nodeIterator.hasNext()) {
					List<JacksonMonolingualTextValue> mltvList = new ArrayList<>();
					Entry<String, JsonNode> currentNode = nodeIterator.next();
					// get the list of MLTVs
					Iterator<JsonNode> mltvListIterator = currentNode
							.getValue().iterator();
					while (mltvListIterator.hasNext()) {
						JsonNode mltvEntry = mltvListIterator.next();
						String language = mltvEntry.get("language").asText();
						String value = mltvEntry.get("value").asText();
						mltvList.add(new JacksonMonolingualTextValue(language,
								value));
					}

					contents.put(currentNode.getKey(), mltvList);
				}
			}

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return contents;

	}

}
