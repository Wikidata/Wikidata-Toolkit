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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

/**
 * Most basic implementation of {@link SiteLink}.
 *
 * WARNING: Site key and site base IRI are currently specified independently. It
 * is expected that this will change to use a mapping from site keys to IRIs
 * instead.
 *
 * @author Markus Kroetzsch
 *
 */
public class SiteLinkImpl implements SiteLink, Serializable {

	private static final long serialVersionUID = 8921712582883517425L;
	
	final String title;
	final String siteKey;
	final List<String> badges;

	/**
	 * Constructor.
	 *
	 * @param title
	 *            the title string of the linked page, including namespace
	 *            prefixes if any
	 * @param siteKey
	 *            the string key of the site of the linked article
	 * @param badges
	 *            the list of badges of the linked article
	 */
	SiteLinkImpl(String title, String siteKey, List<String> badges) {
		Validate.notNull(title, "title cannot be null");
		Validate.notNull(siteKey, "siteKey cannot be null");
		Validate.notNull(badges, "list of badges cannot be null");

		this.title = title;
		this.siteKey = siteKey;
		this.badges = badges;
	}

	@Override
	public String getPageTitle() {
		return title;
	}

	@Override
	public String getSiteKey() {
		return siteKey;
	}

	@Override
	public List<String> getBadges() {
		return Collections.unmodifiableList(badges);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsSiteLink(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
