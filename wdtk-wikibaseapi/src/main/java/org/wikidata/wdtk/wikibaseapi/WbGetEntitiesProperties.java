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
public class WbGetEntitiesProperties {

	/**
	 * ids for entities
	 */
	public String ids = null;

	/**
	 * refers to a sites (e.g. "enwiki") to determine the language for the
	 * titles
	 */
	public String sites = null;

	/**
	 * titles for item documents
	 */
	public String titles = null;

	/**
	 * Describes what should be included in the response. Possible values are
	 * labels, descriptions, sitelinks, claims, datatype and aliases.
	 */
	public String props = null;

	/**
	 * languages for titles, descriptions and aliases
	 */
	public String languages = null;

	/**
	 * filter for sitelinks
	 */
	public String sitefilter = null;
}
