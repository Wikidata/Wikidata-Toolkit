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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.implementation.EntityDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.ItemDocumentImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Java implementation of the wbgetentities action.
 *
 * @author Michael Guenther
 *
 */
public class WbGetEntitiesAction {

	static final Logger logger = LoggerFactory
			.getLogger(WbGetEntitiesAction.class);

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
	final ObjectMapper mapper;

	/**
	 * Creates an object to fetch data from the given ApiConnection. The site
	 * URI is necessary since it is not contained in the data retrieved from the
	 * API.
	 *
	 * @param connection
	 *            {@link ApiConnection} Object to send the requests
	 * @param siteIri
	 *            the URI identifying the site that is accessed (usually the
	 *            prefix of entity URIs), e.g.,
	 *            "http://www.wikidata.org/entity/"
	 */
	public WbGetEntitiesAction(ApiConnection connection, String siteIri) {
		this.connection = connection;
		this.siteIri = siteIri;
		this.mapper = new DatamodelMapper(siteIri);
	}

	/**
	 * Creates a map of identifiers or page titles to documents retrieved via
	 * the API URL
	 *
	 * @param properties
	 *            parameter setting for wbgetentities
	 * @return map of document identifiers or titles to documents retrieved via
	 *         the API URL
	 * @throws MediaWikiApiErrorException
	 *             if the API returns an error
	 * @throws IOException
	 * 			   if we encounter network issues or HTTP 500 errors from Wikibase
	 */
	public Map<String, EntityDocument> wbGetEntities(
			WbGetEntitiesActionData properties)
			throws MediaWikiApiErrorException, IOException {
		return wbGetEntities(properties.ids, properties.sites,
				properties.titles, properties.props, properties.languages,
				properties.sitefilter);
	}

	/**
	 * Creates a map of identifiers or page titles to documents retrieved via
	 * the API. All parameters that accept lists expect the pipe character | to
	 * be used as a separator, as created by
	 * {@link ApiConnection#implodeObjects(Iterable)}. There is a limit on how
	 * many entities can be retrieved in one request, usually 50 by default and
	 * 500 for bots. This limit may also apply to the number of language codes
	 * and sites used for filtering.
	 * <p>
	 * If errors occur (e.g., exceptions trying to access the Web API), then the
	 * errors will be logged and an empty collection will be returned.
	 *
	 * @param ids
	 *            list of ids of entities for which data should be retrieved
	 * @param sites
	 *            site key (e.g. "enwiki"); used together with parameters
	 *            "titles"; the API supports the use of many site keys with a
	 *            single title, but this implementation does not support this
	 *            (the resulting map will use title strings for keys)
	 * @param titles
	 *            list of titles of the page corresponding to the requested
	 *            entities on the given site; use together with 'sites', but
	 *            only give one site for several titles or several sites for one
	 *            title
	 * @param props
	 *            list of strings that specifies what kind of data should be
	 *            retrieved for each entity; possible values include "info",
	 *            "sitelinks", "sitelinks/urls", "aliases", "labels",
	 *            "descriptions", "claims" (statements), "datatype"; additional
	 *            filters may apply; defaults to
	 *            "info|sitelinks|aliases|labels|descriptions|claims|datatype"
	 * @param languages
	 *            list of language codes to return labels, aliases or
	 *            descriptions for; if omitted, data for all languages is
	 *            returned
	 * @param sitefilter
	 *            list of site keys to return sitelinks for; if omitted, data
	 *            for all languages is returned
	 *
	 * @return map of document identifiers or titles to documents retrieved via
	 *         the API URL
	 * @throws MediaWikiApiErrorException
	 *             if the API returns an error
	 * @throws IOException
	 *             if we encounter network errors, or HTTP 500 errors on Wikibase's side
	 * @throws IllegalArgumentException
	 *             if the given combination of parameters does not make sense
	 */
	public Map<String, EntityDocument> wbGetEntities(String ids, String sites,
			String titles, String props, String languages, String sitefilter)
			throws MediaWikiApiErrorException, IOException {

		Map<String, String> parameters = new HashMap<>();
		parameters.put(ApiConnection.PARAM_ACTION, "wbgetentities");

		if (ids != null) {
			parameters.put("ids", ids);
			if (titles != null || sites != null) {
				throw new IllegalArgumentException(
						"Cannot use parameters \"sites\" or \"titles\" when using ids to get entity data");
			}
		} else if (titles != null) {
			parameters.put("titles", titles);
			if (sites == null) {
				throw new IllegalArgumentException(
						"Sites parameter is required when using titles parameter to get entity data.");
			}
			parameters.put("sites", sites);
		} else {
			throw new IllegalArgumentException(
					"Either ids, or titles and site must be specified for this action.");
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

		Map<String, EntityDocument> result = new HashMap<>();

		try {
			JsonNode root = this.connection.sendJsonRequest("POST", parameters);

			JsonNode entities = root.path("entities");
			Iterator<Entry<String,JsonNode>> entitiesIterator = entities.fields();
			while(entitiesIterator.hasNext()) {
				Entry<String,JsonNode> entry = entitiesIterator.next();
				JsonNode entityNode = entry.getValue();
				if (!entityNode.has("missing")) {
					try {
						EntityDocument ed = mapper.treeToValue(entityNode, EntityDocumentImpl.class);

						if (titles == null) {
							// We use the JSON key rather than the id of the value
							// so that retrieving redirected entities works.
							result.put(entry.getKey(), ed);
						} else {
							if (ed instanceof ItemDocumentImpl
									&& ((ItemDocumentImpl) ed)
											.getSiteLinks().containsKey(sites)) {
								result.put(((ItemDocumentImpl) ed)
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
		} catch (IOException e) {
			logger.error("Could not retrive data: " + e.toString());
			throw e;
		}

		return result;
	}

}
