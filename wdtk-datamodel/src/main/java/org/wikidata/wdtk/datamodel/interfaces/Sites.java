package org.wikidata.wdtk.datamodel.interfaces;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
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
 * Registry to manage the association between site keys (such as "enwiki") and
 * base URLs (such as "http://en.wikipedia.org/wiki/") that is needed to
 * interpret {@link SiteLink} objects. These associations are part of the
 * configuration of a MediaWiki site and therefore not fixed.
 * <p>
 * This is not a Wikibase data object as such, but part of the general
 * configuration of a Wikibase site. The interface supports modification, e.g.,
 * to insert additional associations. This can be useful to augment data
 * manually (even when loading most of the data from a file dump). For example,
 * some of Wikimedia's data exports are more frequent than their sites table
 * exports, so it might be useful to add some very recent sites.
 *
 * @author Markus Kroetzsch
 *
 */
public interface Sites {

	/**
	 * Sets the stored information for the site of the given key to the given
	 * values.
	 * <p>
	 * Note that the path URLs given here should be absolute. In MediaWiki, it
	 * is common to use protocol-relative paths (starting with "//" rather than
	 * with "http://" or "https://"). The code in this class is not prepared to
	 * handle this yet (URL-returning methods would need to allow for a
	 * preferred protocol to be specified).
	 *
	 * @param siteKey
	 *            the global site key, e.g., "enwiki" or "fawikivoyage"
	 * @param group
	 *            the site group, e.g., "wikipedia" or "wikivoyage"
	 * @param languageCode
	 *            the site MediaWiki language code, e.g., "en" or "fa"
	 * @param siteType
	 *            the site type, typically "mediawiki"
	 * @param filePath
	 *            the file path with $1 as a placeholder for the file name,
	 *            e.g., "http://en.wikipedia.org/w/$1" or
	 *            "http://fa.wikivoyage.org/w/$1"
	 * @param pagePath
	 *            the page path with $1 as a placeholder for the page title,
	 *            e.g., "http://en.wikipedia.org/wiki/$1" or
	 *            "http://fa.wikivoyage.org/wiki/$1"
	 */
	void setSiteInformation(String siteKey, String group, String languageCode,
			String siteType, String filePath, String pagePath);

	/**
	 * Returns the MediaWiki language code for the given site, or null if there
	 * is no such data for this site key.
	 * <p>
	 * The language code follows the MediaWiki conventions for language codes,
	 * which do not follow any standard. Most codes agree with those in <a
	 * href="http://www.rfc-editor.org/rfc/bcp/bcp47.txt">BCP 47 </a>, but there
	 * are a number of <a
	 * href="http://meta.wikimedia.org/wiki/Special_language_codes"
	 * >exceptions</a>.
	 *
	 * @param siteKey
	 *            the global site key
	 * @return the corresponding MediaWiki language code, or null if not known
	 */
	String getLanguageCode(String siteKey);

	/**
	 * Returns the group for the given site, or null if there is no such data
	 * for this site key. The group is a string identifier used for
	 * configuration purposes. Typical groups on Wikimedia sites include
	 * "wikipedia", "wikisource", "wikivoyage", and "wikiquote", used for most
	 * sites of these projects, but also singleton groups like "commons" and
	 * "wikimania2013".
	 *
	 * @param siteKey
	 *            the global site key
	 * @return the corresponding group, or null if not known
	 */
	String getGroup(String siteKey);

	/**
	 * Returns the URL for the page of the given name, or null if the site is
	 * not known. All characters in the page title will be escaped for use in
	 * URLs.
	 *
	 * @param siteKey
	 *            the global site key
	 * @param pageTitle
	 *            the title of the page, including namespace prefixes if any
	 * @return the URL to link to this page on the given site, or null if not
	 *         known
	 */
	String getPageUrl(String siteKey, String pageTitle);

	/**
	 * Returns the URL for the given site link, or null if its site key is not
	 * known.
	 *
	 * @param siteLink
	 *            the SiteLink object
	 * @return the page URL for this site link, or null if not known
	 */
	String getSiteLinkUrl(SiteLink siteLink);

	/**
	 * Returns the URL for the file of the given name, or null if the site is
	 * not known. The file name is <i>not escaped</i> for use in URLs, so that
	 * one can use this method to construct URLs with parameters, e.g., when
	 * calling the API of the site. Also note that this method does not
	 * construct URLs for files uploaded to a MediaWiki site using the given
	 * file name; such files are usually placed in some subdirectory.
	 *
	 * @param siteKey
	 *            the global site key
	 * @param fileName
	 *            the name of the file
	 * @return the URL to link to this page on the given site, or null if not
	 *         known
	 */
	String getFileUrl(String siteKey, String fileName);

	/**
	 * Returns the type for the given site, or null if there is no such data for
	 * this site key. For MediaWiki sites, this is "mediawiki".
	 *
	 * @param siteKey
	 *            the global site key
	 * @return the corresponding type, or null if not known
	 */
	String getSiteType(String siteKey);

}
