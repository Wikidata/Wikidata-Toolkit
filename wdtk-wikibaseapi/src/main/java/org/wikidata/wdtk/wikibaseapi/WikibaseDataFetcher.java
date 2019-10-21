package org.wikidata.wdtk.wikibaseapi;

import java.io.IOException;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.DocumentDataFilter;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple class to fetch data from Wikibase via the online API.
 *
 * @author Markus Kroetzsch
 * @author Michael Guenther
 */
public class WikibaseDataFetcher {

	static final Logger logger = LoggerFactory
			.getLogger(WikibaseDataFetcher.class);

	/**
	 * API Action to fetch data.
	 */
	final WbGetEntitiesAction wbGetEntitiesAction;

	final WbSearchEntitiesAction wbSearchEntitiesAction;

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
	 * Maximal value for the size of a list that can be processed by the
	 * Wikibase API in one cycle
	 */
	int maxListSize = 50;

	/**
	 * Creates an object to fetch data from wikidata.org. This convenience
	 * method creates a default {@link ApiConnection} that is not logged in. To
	 * use an existing connection, the constructor
	 * {@link #WikibaseDataFetcher(ApiConnection, String)} should be called,
	 * using {@link Datamodel#SITE_WIKIDATA} as a site URI.
	 */
	public static WikibaseDataFetcher getWikidataDataFetcher() {
		return new WikibaseDataFetcher(
				ApiConnection.getWikidataApiConnection(),
				Datamodel.SITE_WIKIDATA);
	}

	/**
	 * Creates an object to fetch data from API with the given
	 * {@link ApiConnection} object. The site URI is necessary since it is not
	 * contained in the data retrieved from the URI.
	 *
	 * @param connection
	 *            ApiConnection
	 * @param siteUri
	 *            the URI identifying the site that is accessed (usually the
	 *            prefix of entity URIs), e.g.,
	 *            "http://www.wikidata.org/entity/"
	 */
	public WikibaseDataFetcher(ApiConnection connection, String siteUri) {
		this.wbGetEntitiesAction = new WbGetEntitiesAction(connection, siteUri);
		this.wbSearchEntitiesAction = new WbSearchEntitiesAction(connection, siteUri);
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
	 * @throws MediaWikiApiErrorException
	 * @throws IOException 
	 */
	public EntityDocument getEntityDocument(String entityId)
			throws MediaWikiApiErrorException, IOException {
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
	 * @throws MediaWikiApiErrorException
	 * @throws IOException 
	 */
	public Map<String, EntityDocument> getEntityDocuments(String... entityIds)
			throws MediaWikiApiErrorException, IOException {
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
	 * @throws MediaWikiApiErrorException
	 * @throws IOException 
	 */
	public Map<String, EntityDocument> getEntityDocuments(List<String> entityIds)
			throws MediaWikiApiErrorException, IOException {
		Map<String, EntityDocument> result = new HashMap<>();
		List<String> newEntityIds = new ArrayList<>(entityIds);
		boolean moreItems = !newEntityIds.isEmpty();
		while (moreItems) {
			List<String> subListOfEntityIds;
			if (newEntityIds.size() <= maxListSize) {
				subListOfEntityIds = newEntityIds;
				moreItems = false;
			} else {
				subListOfEntityIds = newEntityIds.subList(0, maxListSize);
			}
			WbGetEntitiesActionData properties = new WbGetEntitiesActionData();
			properties.ids = ApiConnection.implodeObjects(subListOfEntityIds);
			result.putAll(getEntityDocumentMap(entityIds.size(), properties));
			subListOfEntityIds.clear();
		}
		return result;
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
	 * @throws MediaWikiApiErrorException
	 * @throws IOException 
	 */
	public EntityDocument getEntityDocumentByTitle(String siteKey, String title)
			throws MediaWikiApiErrorException, IOException {
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
	 * @throws MediaWikiApiErrorException
	 * @throws IOException 
	 */
	public Map<String, EntityDocument> getEntityDocumentsByTitle(
			String siteKey, String... titles) throws MediaWikiApiErrorException, IOException {
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
	 * @throws MediaWikiApiErrorException
	 * @throws IOException 
	 */
	public Map<String, EntityDocument> getEntityDocumentsByTitle(
			String siteKey, List<String> titles)
			throws MediaWikiApiErrorException, IOException {
		List<String> newTitles = new ArrayList<>(titles);
		Map<String, EntityDocument> result = new HashMap<>();
		boolean moreItems = !newTitles.isEmpty();

		while (moreItems) {
			List<String> subListOfTitles;
			if (newTitles.size() <= maxListSize) {
				subListOfTitles = newTitles;
				moreItems = false;
			} else {
				subListOfTitles = newTitles.subList(0, maxListSize);
			}
			WbGetEntitiesActionData properties = new WbGetEntitiesActionData();
			properties.titles = ApiConnection.implodeObjects(subListOfTitles);
			properties.sites = siteKey;
			result.putAll(getEntityDocumentMap(subListOfTitles.size(),
					properties));
			subListOfTitles.clear();
		}
		return result;
	}

	/**
	 * Creates a map of identifiers or page titles to documents retrieved via
	 * the APIs.
	 *
	 * @param numOfEntities
	 *            number of entities that should be retrieved
	 * @param properties
	 *            WbGetEntitiesProperties object that includes all relevant
	 *            parameters for the wbgetentities action
	 * @return map of document identifiers or titles to documents retrieved via
	 *         the API URL
	 * @throws MediaWikiApiErrorException
	 * @throws IOException 
	 */
	Map<String, EntityDocument> getEntityDocumentMap(int numOfEntities,
			WbGetEntitiesActionData properties)
			throws MediaWikiApiErrorException, IOException {
		if (numOfEntities == 0) {
			return Collections.emptyMap();
		}
		configureProperties(properties);
		return this.wbGetEntitiesAction.wbGetEntities(properties);
	}

	public List<WbSearchEntitiesResult> searchEntities(String search)
			throws MediaWikiApiErrorException {
		WbGetEntitiesSearchData properties = new WbGetEntitiesSearchData();
		properties.search = search;
		properties.language = "en";
		return searchEntities(properties);
	}

	public List<WbSearchEntitiesResult> searchEntities(String search, String language)
			throws MediaWikiApiErrorException {
		WbGetEntitiesSearchData properties = new WbGetEntitiesSearchData();
		properties.search = search;
		properties.language = language;
		return searchEntities(properties);
	}

	public List<WbSearchEntitiesResult> searchEntities(String search, Long limit)
			throws MediaWikiApiErrorException {
		WbGetEntitiesSearchData properties = new WbGetEntitiesSearchData();
		properties.search = search;
		properties.language = "en";
		properties.limit = limit;
		return searchEntities(properties);
	}

	public List<WbSearchEntitiesResult> searchEntities(String search, String language, Long limit)
			throws MediaWikiApiErrorException {
		WbGetEntitiesSearchData properties = new WbGetEntitiesSearchData();
		properties.search = search;
		properties.language = language;
		properties.limit = limit;
		return searchEntities(properties);
	}

	public List<WbSearchEntitiesResult> searchEntities(WbGetEntitiesSearchData properties)
			throws MediaWikiApiErrorException {
		return this.wbSearchEntitiesAction.wbSearchEntities(properties);
	}

	/**
	 * Configures props, languages and sitefilter properties.
	 *
	 * @param properties
	 */
	void configureProperties(WbGetEntitiesActionData properties) {
		setRequestProps(properties);
		setRequestLanguages(properties);
		setRequestSitefilter(properties);
	}

	/**
	 * Sets the value for the API's "props" parameter based on the current
	 * settings.
	 *
	 * @param properties
	 *            current setting of parameters
	 */
	private void setRequestProps(WbGetEntitiesActionData properties) {
		StringBuilder builder = new StringBuilder();
		builder.append("info|datatype");
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
	 * @param properties
	 *            current setting of parameters
	 */
	private void setRequestLanguages(WbGetEntitiesActionData properties) {
		if (this.filter.excludeAllLanguages()
				|| this.filter.getLanguageFilter() == null) {
			return;
		}
		properties.languages = ApiConnection.implodeObjects(this.filter
				.getLanguageFilter());
	}

	/**
	 * Sets the value for the API's "sitefilter" parameter based on the current
	 * settings.
	 *
	 * @param properties
	 *            current setting of parameters
	 */
	private void setRequestSitefilter(WbGetEntitiesActionData properties) {
		if (this.filter.excludeAllSiteLinks()
				|| this.filter.getSiteLinkFilter() == null) {
			return;
		}
		properties.sitefilter = ApiConnection.implodeObjects(this.filter
				.getSiteLinkFilter());
	}

}
