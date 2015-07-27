package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonItemDocument;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonTermedStatementDocument;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Java implementation of the wbgetentities action
 * 
 * @author Michael Guenther
 * 
 */
public class GetEntitiesAction {

	static final Logger logger = LoggerFactory
			.getLogger(GetEntitiesAction.class);

	/**
	 * Connection to an Wikibase API.
	 */
	final ApiConnection connection;

	/**
	 * The IRI that identifies the site that the data is from.
	 */
	final String siteIri;

	/**
	 * Mapper object used for deserializing JSON data.
	 */
	final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Creates an object to fetch data from API at the given URL. The site URI
	 * is necessary since it is not contained in the data retrieved from the
	 * URI.
	 * 
	 * @param connection
	 *            {@link ApiConnection} Object to send the requests
	 * @param siteUri
	 *            the URI identifying the site that is accessed (usually the
	 *            prefix of entity URIs), e.g.,
	 *            "http://www.wikidata.org/entity/"
	 */
	public GetEntitiesAction(ApiConnection connection, String siteUri) {
		this.connection = connection;
		this.siteIri = siteUri;
	}

	/**
	 * Creates a map of identifiers or page titles to documents retrieved via
	 * the API URL
	 * 
	 * @param properties
	 *            parameter setting for wbgetentities
	 * @return map of document identifiers or titles to documents retrieved via
	 *         the API URL
	 */
	public Map<String, EntityDocument> wbgetEntities(
			WbGetEntitiesProperties properties) {
		return wbgetEntities(properties.ids, properties.sites,
				properties.titles, properties.props, properties.languages,
				properties.sitefilter);
	}

	/**
	 * Creates a map of identifiers or page titles to documents retrieved via
	 * the API URL.
	 * 
	 * @param ids
	 * @param sites
	 * @param titles
	 *            null if the map keys should be document ids; siite key (e.g.,
	 *            "enwiki") if the map should use page titles of a linked site
	 *            as keys
	 * @param props
	 * @param languages
	 * @param sitefilter
	 * @return map of document identifiers or titles to documents retrieved via
	 *         the API URL
	 */
	public Map<String, EntityDocument> wbgetEntities(String ids, String sites,
			String titles, String props, String languages, String sitefilter) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("action", "wbgetentities");
		if (ids != null) {
			parameters.put("ids", ids);
		}
		if (sites != null) {
			parameters.put("sites", sites);
		}
		if (titles != null) {
			parameters.put("titles", titles);
		}

		if (props != null) {
			parameters.put("props", props);
		}
		if (languages != null) {
			parameters.put("languages", languages);
		}
		if (sitefilter != null) {
			parameters.put("sitefilter", sitefilter);
		}

		parameters.put("format", "json");
		InputStream response;
		try {
			response = this.connection.sendRequest("POST", parameters);
			JsonNode root;
			root = mapper.readTree(response);
			Map<String, EntityDocument> result = new HashMap<String, EntityDocument>();

			if (this.connection.parseErrorsAndWarnings(root) == false) {
				logger.error("Could not retrive data");
				return result;
			}

			JsonNode entities = root.path("entities");
			for (JsonNode entityNode : entities) {
				if (!entityNode.has("missing")) {
					try {
						JacksonTermedStatementDocument ed = mapper.treeToValue(
								entityNode,
								JacksonTermedStatementDocument.class);
						ed.setSiteIri(this.siteIri);

						if (titles == null) {
							result.put(ed.getEntityId().getId(), ed);
						} else {
							if (ed instanceof JacksonItemDocument
									&& ((JacksonItemDocument) ed)
											.getSiteLinks().containsKey(sites)) {
								result.put(((JacksonItemDocument) ed)
										.getSiteLinks().get(sites)
										.getPageTitle(), ed);
							}
						}
					} catch (JsonProcessingException e) {
						logger.error("Error when reading JSON for entity "
								+ entityNode.path("id").asText("UNKNOWN")
								+ ": " + e.toString());
					}
				}
			}
			return result;
		} catch (IOException e) {
			logger.error("Could not retrive data: " + e.toString());
			return null;
		}
	}

}
