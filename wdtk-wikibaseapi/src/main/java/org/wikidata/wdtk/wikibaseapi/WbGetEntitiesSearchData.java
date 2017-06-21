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

/**
 * This class is a simple record that holds the properties of an wbgetentities
 * action request. It is used internally by {@link WikibaseDataFetcher} to
 * gather parameters for the request.
 *
 * @author Michael Guenther
 *
 */
public class WbGetEntitiesSearchData {

	/**
	 * search for this text. See
	 * {@link WbSearchEntitiesAction#wbSearchEntities(String, String, Boolean, String, Long, Long)}
	 * for details.
	 */
	public String search = null;

	/**
	 * search in this language. See
	 * {@link WbSearchEntitiesAction#wbSearchEntities(String, String, Boolean, String, Long, Long)}
	 * for details.
	 */
	public String language = null;

	/**
	 * whether to disable language fallback. See
	 * {@link WbSearchEntitiesAction#wbSearchEntities(String, String, Boolean, String, Long, Long)}
	 * for details.
	 */
	public Boolean strictlanguage = null;

	/**
	 * search for this type of entity
	 * One of the following values: item, property. See
	 * {@link WbSearchEntitiesAction#wbSearchEntities(String, String, Boolean, String, Long, Long)}
	 * for details.
	 */
	public String type = null;

	/**
	 * maximal number of results
	 * no more than 50 (500 for bots) allowed. See
	 * {@link WbSearchEntitiesAction#wbSearchEntities(String, String, Boolean, String, Long, Long)}
	 * for details.
	 */
	public Long limit = null;

	/**
	 * offset where to continue a search
	 * this parameter is called "continue" in the API (which is a Java keyword). See
	 * {@link WbSearchEntitiesAction#wbSearchEntities(String, String, Boolean, String, Long, Long)}
	 * for details.
	 */
	public Long offset = null;
}
