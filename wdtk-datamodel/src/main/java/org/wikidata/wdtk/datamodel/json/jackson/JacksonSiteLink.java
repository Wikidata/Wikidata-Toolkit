package org.wikidata.wdtk.datamodel.json.jackson;

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

import java.util.LinkedList;
import java.util.List;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson implementation of {@link SiteLink}.
 *
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonSiteLink implements SiteLink {

	String title;
	String site;
	List<String> badges = new LinkedList<>();

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	JacksonSiteLink() {
	}

	/**
	 * Sets the page title to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param title
	 *            new value
	 */
	@JsonProperty("title")
	public void setPageTitle(String title) {
		this.title = title;
	}

	@JsonProperty("title")
	@Override
	public String getPageTitle() {
		return this.title;
	}

	/**
	 * Sets the site key to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param siteKey
	 *            new value
	 */
	@JsonProperty("site")
	public void setSiteKey(String siteKey) {
		this.site = siteKey;
	}

	@JsonProperty("site")
	@Override
	public String getSiteKey() {
		return this.site;
	}

	/**
	 * Sets the badges to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param badges
	 *            new value
	 */
	public void setBadges(List<String> badges) {
		this.badges = badges;
	}

	@Override
	public List<String> getBadges() {
		return this.badges;
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
