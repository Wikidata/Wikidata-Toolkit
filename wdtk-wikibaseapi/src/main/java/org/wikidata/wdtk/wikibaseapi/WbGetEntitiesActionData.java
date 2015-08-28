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
public class WbGetEntitiesActionData {

	/**
	 * List of ids for entities. Use | as a separator. See
	 * {@link WbGetEntitiesAction#wbGetEntities(String, String, String, String, String, String)}
	 * for details.
	 */
	public String ids = null;

	/**
	 * List of site keys. Use | as a separator. See
	 * {@link WbGetEntitiesAction#wbGetEntities(String, String, String, String, String, String)}
	 * for details.
	 */
	public String sites = null;

	/**
	 * List of page titles. Use | as a separator. See
	 * {@link WbGetEntitiesAction#wbGetEntities(String, String, String, String, String, String)}
	 * for details.
	 */
	public String titles = null;

	/**
	 * List of strings that define which data should be returned. Use | as a
	 * separator. See
	 * {@link WbGetEntitiesAction#wbGetEntities(String, String, String, String, String, String)}
	 * for details.
	 */
	public String props = null;

	/**
	 * List of language codes for restricting language-specific data. Use | as a
	 * separator. See
	 * {@link WbGetEntitiesAction#wbGetEntities(String, String, String, String, String, String)}
	 * for details.
	 */
	public String languages = null;

	/**
	 * List of site keys for restricting site links. Use | as a separator. See
	 * {@link WbGetEntitiesAction#wbGetEntities(String, String, String, String, String, String)}
	 * for details.
	 */
	public String sitefilter = null;
}
