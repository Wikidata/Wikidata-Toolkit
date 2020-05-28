package org.wikidata.wdtk.wikibaseapi;

/*-
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2020 Wikidata Toolkit Developers
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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;
import java.util.*;

/**
 * Action for MediaInfoId retrieval.
 *
 * @author Lu Liu
 */
public class MediaInfoIdQueryAction {

	private final ApiConnection connection;

	private final String siteIri;

	public MediaInfoIdQueryAction(ApiConnection connection, String siteIri) {
		this.connection = connection;
		this.siteIri = siteIri;
	}

	/**
	 * Fetches the MediaInfoIds of files with the given names.
	 * <p>
	 * This method <b>only works with file name</b> (e.g. "File:Albert Einstein Head.jpg").
	 * The "File:" prefix can be omitted, in this case, it will be automatically added during processing.
	 * For example, "Albert Einstein Head.jpg" will be processed as "File:Albert Einstein Head.jpg".
	 * <p>
	 * Notice that pages other than file pages will also be fitted with the "File:" prefix.
	 * For example, "Main Page" will be processed as "File:Main Page", which doesn't exist.
	 * <b>So always make sure you are dealing with file name.</b>
	 *
	 * @param fileNames list of file names of the requested MediaInfoIds
	 * @return map from file names for which data could be found to the MediaInfoIds
	 * that were retrieved
	 */
	public Map<String, MediaInfoIdValue> getMediaInfoIds(List<String> fileNames)
			throws IOException, MediaWikiApiErrorException {
		// file name => file name with prefix
		List<String> fileNamesWithPrefix = new ArrayList<>();
		for (String fileName : fileNames) {
			fileName = fileName.startsWith("File:") ? fileName : "File:" + fileName;
			fileNamesWithPrefix.add(fileName);
		}

		Map<String, String> parameters = new HashMap<>();
		parameters.put(ApiConnection.PARAM_ACTION, "query");
		parameters.put("titles", ApiConnection.implodeObjects(fileNamesWithPrefix));

		Map<String, MediaInfoIdValue> result = new HashMap<>();

		JsonNode root = connection.sendJsonRequest("POST", parameters);
		if (!root.has("query")) return result; // empty query
		JsonNode query = root.get("query");

		// file name with prefix => normalized file name
		Map<String, String> normalizedMap = new HashMap<>();
		if (query.has("normalized")) {
			ArrayNode normalized = (ArrayNode) query.get("normalized");
			Iterator<JsonNode> iterator = normalized.elements();
			while (iterator.hasNext()) {
				JsonNode next = iterator.next();
				String from = next.get("from").asText();
				String to = next.get("to").asText();
				normalizedMap.put(from, to);
			}
		}

		// normalized file name => Mid
		Map<String, MediaInfoIdValue> midMap = new HashMap<>();
		JsonNode pages = query.get("pages");
		Iterator<Map.Entry<String, JsonNode>> iterator = pages.fields();
		while (iterator.hasNext()) {
			Map.Entry<String, JsonNode> page = iterator.next();
			String pageId = page.getKey();
			String title = page.getValue().get("title").textValue();
			if (!"-1".equals(pageId)) { // "-1" means not found
				midMap.put(title, Datamodel.makeMediaInfoIdValue("M" + pageId, siteIri));
			}
		}

		for (String fileName : fileNames) {
			String fileNameWithPrefix = fileName.startsWith("File:") ? fileName : "File:" + fileName;
			String normalizedFileName = normalizedMap.getOrDefault(fileNameWithPrefix, fileNameWithPrefix);
			result.put(fileName, midMap.get(normalizedFileName));
		}

		return result;
	}
}
