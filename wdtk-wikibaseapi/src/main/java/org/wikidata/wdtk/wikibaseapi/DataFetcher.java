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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.DocumentDataFilter;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.util.WebResourceFetcher;
import org.wikidata.wdtk.util.WebResourceFetcherImpl;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple class to fetch data from Wikibase via the online API. Only anonymous,
 * read-only access is supported here.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class DataFetcher {

	static final Logger logger = LoggerFactory.getLogger(DataFetcher.class);

	/**
	 * Object used to make web requests. Package-private so that it can be
	 * overwritten with a mock object in tests.
	 */
	WebResourceFetcher webResourceFetcher = new WebResourceFetcherImpl();

	/**
	 * API Action to fetch data.
	 */
	final GetEntitiesAction entitiesAction;

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
	public DataFetcher() {
		this(ApiConnection.getWikidataApiConnection(), Datamodel.SITE_WIKIDATA);
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
	public DataFetcher(ApiConnection connection, String siteUri) {
		this.entitiesAction = new GetEntitiesAction(connection, siteUri);
		this.siteIri = siteUri;
	}

	class WbGetEntitiesProperties {
		String ids = null;
		String sites = null;
		String titles = null;
		String props = null;
		String languages = null;
		String sitefilter = null;
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
		WbGetEntitiesProperties properties = new WbGetEntitiesProperties();
		final String entityString = implodeObjects(entityIds);
		properties.ids = entityString;
		return getEntityDocumentMap(entityIds.size(), properties);
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
		WbGetEntitiesProperties properties = new WbGetEntitiesProperties();
		String titleString = implodeObjects(titles);
		properties.titles = titleString;
		properties.sites = siteKey;
		return getEntityDocumentMap(titles.size(), properties);
	}

	/**
	 * Creates a map of identifiers or page titles to documents retrieved via
	 * the API URL.
	 * 
	 * @param numOfEntities
	 *            number of entities that should be retrieved
	 * @return map of document identifiers or titles to documents retrieved via
	 *         the API URL
	 */
	Map<String, EntityDocument> getEntityDocumentMap(int numOfEntities,
			WbGetEntitiesProperties properties) {
		if (numOfEntities == 0) {
			return Collections.<String, EntityDocument> emptyMap();
		}
		this.configureProperties(properties);
		Map<String, EntityDocument> result = entitiesAction.wbgetEntities(
				properties.ids, properties.sites, properties.titles,
				properties.props, properties.languages, properties.sitefilter);

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
	 *            DataFetcher.getWbGetEntitiesUrl(List<String> entityIds) as an
	 *            example.
	 * @return URL string
	 */
	void configureProperties(WbGetEntitiesProperties properties) {
		setRequestProps(properties);
		setRequestLanguages(properties);
		setRequestSitefilter(properties);
	}

	/**
	 * Sets the value for the API's "props" parameter based on the current
	 * settings.
	 * 
	 * @param uriBuilder
	 *            the URI builder to set the parameter in
	 */
	private void setRequestProps(WbGetEntitiesProperties properties) {
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

		properties.props = builder.toString();
	}

	/**
	 * Sets the value for the API's "languages" parameter based on the current
	 * settings.
	 * 
	 * @param uriBuilder
	 *            the URI builder to set the parameter in
	 */
	private void setRequestLanguages(WbGetEntitiesProperties properties) {
		if (this.filter.excludeAllLanguages()
				|| this.filter.getLanguageFilter() == null) {
			return;
		}
		properties.languages = implodeObjects(this.filter.getLanguageFilter());
	}

	/**
	 * Sets the value for the API's "sitefilter" parameter based on the current
	 * settings.
	 * 
	 * @param uriBuilder
	 *            the URI builder to set the parameter in
	 */
	private void setRequestSitefilter(WbGetEntitiesProperties properties) {
		if (this.filter.excludeAllSiteLinks()
				|| this.filter.getSiteLinkFilter() == null) {
			return;
		}
		properties.sitefilter = implodeObjects(this.filter.getSiteLinkFilter());
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
