package org.wikidata.wdtk.datamodel.interfaces;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import java.util.Set;

/**
 * This class is used to describe restrictions to data in {@link EntityDocument}
 * objects. This is used, e.g., to restrict only part of the data when copying
 * objects.
 *
 * @author Markus Kroetzsch
 */
public class DocumentDataFilter {

	/**
	 * Set of language codes to restrict terms (labels, descriptions, aliases).
	 * If set to null, terms will not be restricted.
	 */
	private Set<String> languageFilter = null;

	/**
	 * Set of property id values to restrict statements. If set to null,
	 * statements will not be restricted.
	 */
	private Set<PropertyIdValue> propertyFilter = null;

	/**
	 * Set of site keys to restrict site keys. If set to null, site links will
	 * not be restricted.
	 */
	private Set<String> siteLinkFilter = null;

	/**
	 * Returns the (possibly empty) set of language codes that are used to
	 * filter data, or null if no such filter is configured (default). If not
	 * equal to null, only terms in the given languages will be included.
	 *
	 * @return set of language codes to use for filtering
	 */
	public Set<String> getLanguageFilter() {
		return this.languageFilter;
	}

	/**
	 * Sets the (possibly empty) set of language codes that are used to filter
	 * data. Setting this to null disables this filter (this is the default). If
	 * not equal to null, only terms in the given language will be included.
	 * <p>
	 * The language filter is not applied to monolingual text values in
	 * statements. Only labels, descriptions, and aliases are filtered.
	 *
	 * @param languageFilter
	 *            set of language codes to restrict to
	 */
	public void setLanguageFilter(Set<String> languageFilter) {
		this.languageFilter = languageFilter;
	}

	/**
	 * Returns the (possibly empty) set of {@link PropertyIdValue} objects that
	 * are used to filter statements, or null if no such filter is configured
	 * (default). If not equal to null, only statements using the given
	 * properties will be included.
	 *
	 * @return set of properties to use for filtering
	 */
	public Set<PropertyIdValue> getPropertyFilter() {
		return this.propertyFilter;
	}

	/**
	 * Sets the (possibly empty) set of {@link PropertyIdValue} objects that are
	 * used to filter statements. Setting this to null disables this filter
	 * (this is the default). If not equal to null, only statements using the
	 * given properties will be included.
	 * <p>
	 * The property filter is not applied to qualifiers and references in
	 * statements. Only the main property of statements is filtered.
	 *
	 * @param propertyFilter
	 *            set of properties to use for filtering
	 */
	public void setPropertyFilter(Set<PropertyIdValue> propertyFilter) {
		this.propertyFilter = propertyFilter;
	}

	/**
	 * Returns the (possibly empty) set of site keys that are used to filter
	 * {@link SiteLink} objects, or null if no such filter is configured
	 * (default). If not equal to null, only site links for the given sites will
	 * be included.
	 *
	 * @return set of site keys to use for filtering
	 */
	public Set<String> getSiteLinkFilter() {
		return this.siteLinkFilter;
	}

	/**
	 * Sets the (possibly empty) set of site keys that are used to filter
	 * {@link SiteLink} objects. Setting this to null disables this filter (this
	 * is the default). If not equal to null, only site links for the given
	 * sites will be included.
	 *
	 * @param siteLinkFilter
	 *            set of site keys to use for filtering
	 */
	public void setSiteLinkFilter(Set<String> siteLinkFilter) {
		this.siteLinkFilter = siteLinkFilter;
	}

	/**
	 * Returns true if the given language is included (not filtered).
	 *
	 * @param languageCode
	 *            code of the language to check
	 * @return true if there is no language filter, or a language filter that
	 *         includes the given language
	 */
	public boolean includeLanguage(String languageCode) {
		return this.languageFilter == null
				|| this.languageFilter.contains(languageCode);
	}

	/**
	 * Returns true if the given property is included (not filtered).
	 *
	 * @param propertyIdValue
	 *            property id to check
	 * @return true if there is no property filter, or a property filter that
	 *         includes the given property
	 */
	public boolean includePropertyId(PropertyIdValue propertyIdValue) {
		return this.propertyFilter == null
				|| this.propertyFilter.contains(propertyIdValue);
	}

	/**
	 * Returns true if the given site link is included (not filtered).
	 *
	 * @param siteLink
	 *            key of the site to check
	 * @return true if there is no site link filter, or a site link filter that
	 *         includes the given site
	 */
	public boolean includeSiteLink(String siteLink) {
		return this.siteLinkFilter == null
				|| this.siteLinkFilter.contains(siteLink);
	}

	/**
	 * Returns true if terms in all languages are excluded.
	 *
	 * @return true if all terms are excluded
	 */
	public boolean excludeAllLanguages() {
		return this.languageFilter != null && this.languageFilter.isEmpty();
	}

	/**
	 * Returns true if statements for all properties are excluded.
	 *
	 * @return true if all statements are excluded
	 */
	public boolean excludeAllProperties() {
		return this.propertyFilter != null && this.propertyFilter.isEmpty();
	}

	/**
	 * Returns true if site links for all sites are excluded.
	 *
	 * @return true if all site links are excluded
	 */
	public boolean excludeAllSiteLinks() {
		return this.siteLinkFilter != null && this.siteLinkFilter.isEmpty();
	}

}
