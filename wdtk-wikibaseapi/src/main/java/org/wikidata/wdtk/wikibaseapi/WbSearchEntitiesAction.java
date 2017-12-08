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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Java implementation of the wbsearchentities action.
 *
 * @author Sören Brunk
 *
 */
public class WbSearchEntitiesAction {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(WbSearchEntitiesAction.class);

    /**
     * Connection to a Wikibase API.
     */
    private final ApiConnection connection;

    /**
     * The IRI that identifies the site that the data is from.
     */
    private final String siteIri;

    /**
     * Mapper object used for deserializing JSON data.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates an object to fetch data from the given ApiConnection. The site
     * URI is necessary since it is not contained in the data retrieved from the
     * API.
     *
     * @param connection
     *            {@link ApiConnection} Object to send the requests
     * @param siteUri
     *            the URI identifying the site that is accessed (usually the
     *            prefix of entity URIs), e.g.,
     *            "http://www.wikidata.org/entity/"
     */
    public WbSearchEntitiesAction(ApiConnection connection, String siteUri) {
        this.connection = connection;
        this.siteIri = siteUri;
    }

    public List<WbSearchEntitiesResult> wbSearchEntities(WbGetEntitiesSearchData properties)
            throws MediaWikiApiErrorException {
        return wbSearchEntities(properties.search, properties.language,
                properties.strictlanguage, properties.type, properties.limit, properties.offset);
    }

    /**
     * Executes the API action "wbsearchentity" for the given parameters.
     * Searches for entities using labels and aliases. Returns a label and
     * description for the entity in the user language if possible. Returns
     * details of the matched term. The matched term text is also present in the
     * aliases key if different from the display label.
     *
     * <p>
     * See the <a href=
     * "https://www.wikidata.org/w/api.php?action=help&modules=wbsearchentity"
     * >online API documentation</a> for further information.
     * <p>
     *
     * @param search
     *            (required) search for this text
     * @param language
     *            (required) search in this language
     * @param strictLanguage
     *            (optional) whether to disable language fallback
     * @param type
     *            (optional) search for this type of entity
     *            One of the following values: item, property
     *            Default: item
     * @param limit
     *            (optional) maximal number of results
     *            no more than 50 (500 for bots) allowed
     *            Default: 7
     * @param offset
     *            (optional) offset where to continue a search
     *            Default: 0
     *            this parameter is called "continue" in the API (which is a Java keyword)
     *
     * @return list of matching entities retrieved via the API URL
     * @throws MediaWikiApiErrorException
     *             if the API returns an error
     * @throws IllegalArgumentException
     *             if the given combination of parameters does not make sense
     */
    public List<WbSearchEntitiesResult> wbSearchEntities(String search, String language,
                                                         Boolean strictLanguage, String type, Long limit, Long offset)
            throws MediaWikiApiErrorException {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(ApiConnection.PARAM_ACTION, "wbsearchentities");

        if (search != null) {
            parameters.put("search", search);
        } else {
            throw new IllegalArgumentException(
                    "Search parameter must be specified for this action.");
        }

        if (language != null) {
            parameters.put("language", language);
        } else {
            throw new IllegalArgumentException(
                    "Language parameter must be specified for this action.");
        }
        if (strictLanguage != null) {
            parameters.put("strictlanguage", Boolean.toString(strictLanguage));
        }

        if (type != null) {
            parameters.put("type", type);
        }

        if (limit != null) {
            parameters.put("limit", Long.toString(limit));
        }

        if (offset != null) {
            parameters.put("continue", Long.toString(offset));
        }

        List<WbSearchEntitiesResult> results = new ArrayList<>();

        try {
            JsonNode root = this.connection.sendJsonRequest("POST", parameters);
            JsonNode entities = root.path("search");
            for (JsonNode entityNode : entities) {
                try {
                    JacksonWbSearchEntitiesResult ed = mapper.treeToValue(entityNode,
                            JacksonWbSearchEntitiesResult.class);
                    results.add(ed);
                } catch (JsonProcessingException e) {
                    LOGGER.error("Error when reading JSON for entity "
                            + entityNode.path("id").asText("UNKNOWN") + ": "
                            + e.toString());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Could not retrive data: " + e.toString());
        }

        return results;
    }

}
