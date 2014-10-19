package org.wikidata.wdtk.datamodel.implementation;

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Sites;

/**
 * Implementation of the {@link Sites} interface that allows sites to be
 * registered. Objects of this type are not immutable, since they are not data
 * objects, but the {@link Sites} interface only supports read access.
 *
 * @author Markus Kroetzsch
 *
 */
public class SitesImpl implements Sites {

	/**
	 * Simple record for holding information about a site.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	class SiteInformation {
		final String siteKey;
		final String group;
		final String languageCode;
		final String siteType;
		final String filePathPre;
		final String filePathPost;
		final String pagePathPre;
		final String pagePathPost;

		SiteInformation(String siteKey, String group, String languageCode,
				String siteType, String filePath, String pagePath) {
			// Null might be acceptable for some of the following; but this
			// should only be changed when we have a case where this is correct.
			Validate.notNull(siteKey, "Site key must not be null.");
			Validate.notNull(group, "Group must not be null.");
			Validate.notNull(languageCode, "Language code must not be null.");
			Validate.notNull(siteType, "Site type must not be null.");
			Validate.notNull(filePath, "File path must not be null.");
			Validate.notNull(pagePath, "Page path must not be null.");

			this.siteKey = siteKey;
			this.group = group;
			this.languageCode = languageCode;
			this.siteType = siteType;

			int iFileName = filePath.indexOf("$1");
			this.filePathPre = filePath.substring(0, iFileName);
			this.filePathPost = filePath.substring(iFileName + 2,
					filePath.length());

			int iPageName = pagePath.indexOf("$1");
			this.pagePathPre = pagePath.substring(0, iPageName);
			this.pagePathPost = pagePath.substring(iPageName + 2,
					pagePath.length());
		}

		/**
		 * Returns the file URL.
		 *
		 * @see Sites#getFileUrl(String, String)
		 * @param fileName
		 *            the file name
		 * @return the file URL
		 */
		String getFileUrl(String fileName) {
			return this.filePathPre + fileName + this.filePathPost;
		}

		/**
		 * Returns the page URL. The method replaces spaces by underscores in
		 * page titles on MediaWiki sites, since this is how MediaWiki page URLs
		 * are constructed. For other sites, this might not be the case and
		 * spaces will just be escaped in the standard way using "+".
		 *
		 * @see Sites#getPageUrl(String, String)
		 * @param pageTitle
		 *            the page title, not escaped
		 * @return the page URL
		 */
		String getPageUrl(String pageTitle) {
			try {
				String encodedTitle;
				if ("mediawiki".equals(this.siteType)) {
					encodedTitle = URLEncoder.encode(
							pageTitle.replace(" ", "_"), "utf-8");
					// Keep special title symbols unescaped:
					encodedTitle = encodedTitle.replace("%3A", ":").replace(
							"%2F", "/");
				} else {
					encodedTitle = URLEncoder.encode(pageTitle, "utf-8");
				}
				return this.pagePathPre + encodedTitle + this.pagePathPost;
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(
						"Your JRE does not support UTF-8 encoding. Srsly?!", e);
			}
		}
	}

	final HashMap<String, SiteInformation> sites = new HashMap<String, SiteInformation>();

	@Override
	public void setSiteInformation(String siteKey, String group,
			String languageCode, String siteType, String filePath,
			String pagePath) {
		this.sites.put(siteKey, new SiteInformation(siteKey, group,
				languageCode, siteType, filePath, pagePath));
	}

	@Override
	public String getLanguageCode(String siteKey) {
		if (this.sites.containsKey(siteKey)) {
			return this.sites.get(siteKey).languageCode;
		} else {
			return null;
		}
	}

	@Override
	public String getGroup(String siteKey) {
		if (this.sites.containsKey(siteKey)) {
			return this.sites.get(siteKey).group;
		} else {
			return null;
		}
	}

	@Override
	public String getPageUrl(String siteKey, String pageTitle) {
		if (this.sites.containsKey(siteKey)) {
			return this.sites.get(siteKey).getPageUrl(pageTitle);
		} else {
			return null;
		}
	}

	@Override
	public String getSiteLinkUrl(SiteLink siteLink) {
		return this.getPageUrl(siteLink.getSiteKey(), siteLink.getPageTitle());
	}

	@Override
	public String getFileUrl(String siteKey, String fileName) {
		if (this.sites.containsKey(siteKey)) {
			return this.sites.get(siteKey).getFileUrl(fileName);
		} else {
			return null;
		}
	}

	@Override
	public String getSiteType(String siteKey) {
		if (this.sites.containsKey(siteKey)) {
			return this.sites.get(siteKey).siteType;
		} else {
			return null;
		}
	}

}
