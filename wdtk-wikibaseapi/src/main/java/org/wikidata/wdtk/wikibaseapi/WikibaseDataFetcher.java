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
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.DocumentDataFilter;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonItemDocument;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonTermedStatementDocument;
import org.wikidata.wdtk.util.WebResourceFetcher;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple class to fetch data from Wikibase via the online API. Only anonymous,
 * read-only access is supported here.
 *
 * @author Markus Kroetzsch
 *
 */
public class WikibaseDataFetcher {

	static final Logger logger = LoggerFactory
			.getLogger(WikibaseDataFetcher.class);

	/**
	 * URL of the Web API of wikidata.org.
	 */
	final static String WIKIDATA_API_URL = "https://www.wikidata.org/w/api.php";

	/**
	 * Object used to make web requests. Package-private so that it can be
	 * overwritten with a mock object in tests.
	 */
	WebResourceFetcher webResourceFetcher = new WebResourceFetcherImpl();

	/**
	 * The URL where the MediaWiki API can be found.
	 */
	final String apiBaseUrl;

	/**
	 * The IRI that identifies the site that the data is from.
	 */
	final String siteIri;

	/**
	 * Mapper object used for deserializing JSON data.
	 */
	final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Filter that is used to restrict API requests.
	 */
	private final DocumentDataFilter filter = new DocumentDataFilter();

	/**
	 * Creates an object to fetch data from wikidata.org.
	 */
	public WikibaseDataFetcher() {
		this(WIKIDATA_API_URL, Datamodel.SITE_WIKIDATA);
	}

	/**
	 * Creates an object to fetch data from API at the given URL. The site URI
	 * is necessary since it is not contained in the data retrieved from the
	 * URI.
	 *
	 * @param apiBaseUrl
	 *            the base URL of the Web API that should be accessed, e.g.,
	 *            "http://www.wikidata.org/w/api.php"
	 * @param siteUri
	 *            the URI identifying the site that is accessed (usually the
	 *            prefix of entity URIs), e.g.,
	 *            "http://www.wikidata.org/entity/"
	 */
	public WikibaseDataFetcher(String apiBaseUrl, String siteUri) {
		this.apiBaseUrl = apiBaseUrl;
		this.siteIri = siteUri;
	}

	/**
	 * Returns the {@link DocumentDataFilter} object that is used to filter API
	 * requests. Settings made in this object will affect the API request, and
	 * often lead to reduced network traffic and better performance.
	 * <p>
	 * Note: Filtering individual properties is currently not supported (such
	 * filters will be ignored). However, filtering all properties is possible;
	 * in this case all statements are excluded.
	 *
	 * @return the filter used by this object
	 */
	public DocumentDataFilter getFilter() {
		return this.filter;
	}

	/**
	 * Fetches the documents for the entity of the given string IDs. The result
	 * is an {@link EntityDocument} or null if the data could not be fetched.
	 *
	 * @param entityId
	 *            string IDs (e.g., "P31" or "Q42") of requested entity
	 * @return retrieved entity document or null
	 */
	public EntityDocument getEntityDocument(String entityId) {
		return getEntityDocuments(entityId).get(entityId);
	}

	/**
	 * Fetches the documents for the entities of the given string IDs. The
	 * result is a map from entity IDs to {@link EntityDocument} objects. It is
	 * possible that a requested ID could not be found: then this key is not set
	 * in the map.
	 *
	 * @param entityIds
	 *            string IDs (e.g., "P31", "Q42") of requested entities
	 * @return map from IDs for which data could be found to the documents that
	 *         were retrieved
	 */
	public Map<String, EntityDocument> getEntityDocuments(String... entityIds) {
		return getEntityDocuments(Arrays.asList(entityIds));
	}

	/**
	 * Fetches the documents for the entities of the given string IDs. The
	 * result is a map from entity IDs to {@link EntityDocument} objects. It is
	 * possible that a requested ID could not be found: then this key is not set
	 * in the map.
	 *
	 * @param entityIds
	 *            list of string IDs (e.g., "P31", "Q42") of requested entities
	 * @return map from IDs for which data could be found to the documents that
	 *         were retrieved
	 */
	public Map<String, EntityDocument> getEntityDocuments(List<String> entityIds) {
		String url = getWbGetEntitiesUrl(entityIds);
		return getStringEntityDocumentMap(entityIds.size(), url, null);
	}

	/**
	 * Fetches the document for the entity that has a page of the given title on
	 * the given site. Site keys should be some site identifier known to the
	 * Wikibase site that is queried, such as "enwiki" for Wikidata.org.
	 * <p>
	 * Note: This method will not work properly if a filter is set for sites
	 * that excludes the requested site.
	 *
	 * @param siteKey
	 *            wiki site id, e.g., "enwiki"
	 * @param title
	 *            string titles (e.g. "Douglas Adams") of requested entities
	 * @return document for the entity with this title, or null if no such
	 *         document exists
	 */
	public EntityDocument getEntityDocumentByTitle(String siteKey, String title) {
		return getEntityDocumentsByTitle(siteKey, title).get(title);
	}

	/**
	 * Fetches the documents for the entities that have pages of the given
	 * titles on the given site. Site keys should be some site identifier known
	 * to the Wikibase site that is queried, such as "enwiki" for Wikidata.org.
	 * <p>
	 * Note: This method will not work properly if a filter is set for sites
	 * that excludes the requested site.
	 *
	 * @param siteKey
	 *            wiki site id, e.g. "enwiki"
	 * @param titles
	 *            list of string titles (e.g. "Douglas Adams") of requested
	 *            entities
	 * @return map from titles for which data could be found to the documents
	 *         that were retrieved
	 */
	public Map<String, EntityDocument> getEntityDocumentsByTitle(
			String siteKey, String... titles) {
		return getEntityDocumentsByTitle(siteKey, Arrays.asList(titles));
	}

	/**
	 * Fetches the documents for the entities that have pages of the given
	 * titles on the given site. Site keys should be some site identifier known
	 * to the Wikibase site that is queried, such as "enwiki" for Wikidata.org.
	 * <p>
	 * Note: This method will not work properly if a filter is set for sites
	 * that excludes the requested site.
	 *
	 * @param siteKey
	 *            wiki site id, e.g. "enwiki"
	 * @param titles
	 *            list of string titles (e.g. "Douglas Adams") of requested
	 *            entities
	 * @return map from titles for which data could be found to the documents
	 *         that were retrieved
	 */
	public Map<String, EntityDocument> getEntityDocumentsByTitle(
			String siteKey, List<String> titles) {
		String url = getWbGetEntitiesUrl(siteKey, titles);
		return getStringEntityDocumentMap(titles.size(), url, siteKey);
	}

	/**
	 * Creates a map of identifiers or page titles to documents retrieved via
	 * the API URL.
	 *
	 * @param numOfEntities
	 *            number of entities that should be retrieved
	 * @param url
	 *            the API URL (with parameters)
	 * @param siteKey
	 *            null if the map keys should be document ids; siite key (e.g.,
	 *            "enwiki") if the map should use page titles of a linked site
	 *            as keys
	 * @return map of document identifiers or titles to documents retrieved via
	 *         the API URL
	 */
	Map<String, EntityDocument> getStringEntityDocumentMap(int numOfEntities,
			String url, String siteKey) {
		if (numOfEntities == 0 || url == null) {
			return Collections.<String, EntityDocument> emptyMap();
		}
		Map<String, EntityDocument> result = new HashMap<>(numOfEntities);

		try (InputStream inStream = this.webResourceFetcher
				.getInputStreamForUrl(url)) {

			JsonNode root = mapper.readTree(inStream);

			if (root.has("error")) {
				JsonNode errorNode = root.path("error");
				logger.error("Error when reading data from API: "
						+ errorNode.path("info").asText("DESCRIPTION MISSING")
						+ " ["
						+ errorNode.path("code").asText("UNKNOWN ERROR CODE")
						+ "]");
			} // fall through: maybe there are some entities anyway

			JsonNode entities = root.path("entities");
			for (JsonNode entityNode : entities) {
				if (!entityNode.has("missing")) {
					try {
						JacksonTermedStatementDocument ed = mapper.treeToValue(
								entityNode,
								JacksonTermedStatementDocument.class);
						ed.setSiteIri(this.siteIri);

						if (siteKey == null) {
							result.put(ed.getEntityId().getId(), ed);
						} else {
							if (ed instanceof JacksonItemDocument
									&& ((JacksonItemDocument) ed)
											.getSiteLinks()
											.containsKey(siteKey)) {
								result.put(((JacksonItemDocument) ed)
										.getSiteLinks().get(siteKey)
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
			logger.error("Could not retrieve data from " + url + ". Error:\n"
					+ e.toString());
		}

		return result;
	}

	/**
	 * Returns the URL string for a wbgetentities request to the Wikibase API,
	 * or null if it was not possible to build such a string with the current
	 * settings.
	 *
	 * @param parameters
	 *            map of possible parameters (e.g. ("sites", "enwiki"),
	 *            ("titles", titles of entities to retrieve), ("ids", ids of
	 *            entities to retrieve). See
	 *            WikibaseDataFetcher.getWbGetEntitiesUrl(List<String>
	 *            entityIds) as an example.
	 * @return URL string
	 */
	String getWbGetEntitiesUrl(Map<String, String> parameters) {

		URIBuilder uriBuilder;
		try {
			uriBuilder = new URIBuilder(this.apiBaseUrl);
		} catch (URISyntaxException e1) {
			logger.error("Error in API URL \"" + this.apiBaseUrl + "\": "
					+ e1.toString());
			return null;
		}

		uriBuilder.setParameter("action", "wbgetentities");
		uriBuilder.setParameter("format", "json");
		setRequestProps(uriBuilder);
		setRequestLanguages(uriBuilder);
		setRequestSitefilter(uriBuilder);
		for (String parameter : parameters.keySet()) {
			String value = parameters.get(parameter);
			uriBuilder.setParameter(parameter, value);
		}

		return uriBuilder.toString();
	}

	/**
	 * Returns the URL string for a wbgetentities request to the Wikibase API,
	 * or null if it was not possible to build such a string with the current
	 * settings.
	 *
	 * @param entityIds
	 *            list of string IDs (e.g., "P31", "Q42") of requested entities
	 * @return URL string
	 */
	String getWbGetEntitiesUrl(List<String> entityIds) {
		final String entityString = implodeObjects(entityIds);
		Map<String, String> parameters = new HashMap<>();
		parameters.put("ids", entityString);
		return getWbGetEntitiesUrl(parameters);
	}

	/**
	 * Returns the URL string for a wbgetentities request to the Wikibase API,
	 * or null if it was not possible to build such a string with the current
	 * settings.
	 *
	 * @param siteKey
	 *            wiki site id, e.g. "enwiki"
	 * @param titles
	 *            list of string titles (e.g. "Douglas Adams") of requested
	 *            entities.
	 * @return URL string
	 */
	String getWbGetEntitiesUrl(String siteKey, List<String> titles) {
		final String titleString = implodeObjects(titles);
		Map<String, String> parameters = new HashMap<>();
		parameters.put("sites", siteKey);
		parameters.put("titles", titleString);
		return getWbGetEntitiesUrl(parameters);
	}

	/**
	 * Sets the value for the API's "props" parameter based on the current
	 * settings.
	 *
	 * @param uriBuilder
	 *            the URI builder to set the parameter in
	 */
	private void setRequestProps(URIBuilder uriBuilder) {
		StringBuilder builder = new StringBuilder();
		builder.append("datatype");
		if (!this.filter.excludeAllLanguages()) {
			builder.append("|labels|aliases|descriptions");
		}
		if (!this.filter.excludeAllProperties()) {
			builder.append("|claims");
		}
		if (!this.filter.excludeAllSiteLinks()) {
			builder.append("|sitelinks");
		}

		uriBuilder.setParameter("props", builder.toString());
	}

	/**
	 * Sets the value for the API's "languages" parameter based on the current
	 * settings.
	 *
	 * @param uriBuilder
	 *            the URI builder to set the parameter in
	 */
	private void setRequestLanguages(URIBuilder uriBuilder) {
		if (this.filter.excludeAllLanguages()
				|| this.filter.getLanguageFilter() == null) {
			return;
		}
		uriBuilder.setParameter("languages",
				implodeObjects(this.filter.getLanguageFilter()));
	}

	/**
	 * Sets the value for the API's "sitefilter" parameter based on the current
	 * settings.
	 *
	 * @param uriBuilder
	 *            the URI builder to set the parameter in
	 */
	private void setRequestSitefilter(URIBuilder uriBuilder) {
		if (this.filter.excludeAllSiteLinks()
				|| this.filter.getSiteLinkFilter() == null) {
			return;
		}
		uriBuilder.setParameter("sitefilter",
				implodeObjects(this.filter.getSiteLinkFilter()));
	}

	/**
	 * Builds a string that serializes a list of objects separated by the pipe
	 * character. The toString methods are used to turn objects into strings.
	 *
	 * @param objects
	 *            the objects to implode
	 * @return string of imploded objects
	 */
	private String implodeObjects(Iterable<? extends Object> objects) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (Object o : objects) {
			if (first) {
				first = false;
			} else {
				builder.append("|");
			}
			builder.append(o.toString());
		}
		return builder.toString();
	}

}
