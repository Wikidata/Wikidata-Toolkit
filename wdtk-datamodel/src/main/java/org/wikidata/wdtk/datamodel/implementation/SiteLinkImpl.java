package org.wikidata.wdtk.datamodel.implementation;

import java.util.Collections;

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

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson implementation of {@link SiteLink}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteLinkImpl implements SiteLink {

	private final String title;
	private final String site;
	private final List<String> badges;

	/**
	 * Constructor.
	 * 
	 * @param title
	 * 		the title of the page on the target site
	 * @param site
	 * 		the identifier of the target site (such as "dewiki")
	 * @param badges
	 * 		the list of badge identifiers worn by this site link.
	 * 		Can be null.
	 */
	@JsonCreator
	public SiteLinkImpl(
			@JsonProperty("title") String title,
			@JsonProperty("site") String site,
			@JsonProperty("badges") List<String> badges) {
		Validate.notNull(title);
		this.title = title;
		Validate.notNull(site);
		this.site = site;
		if (badges != null) {
			this.badges = badges;
		} else {
			this.badges = Collections.emptyList();
		}
	}

	@JsonProperty("title")
	@Override
	public String getPageTitle() {
		return this.title;
	}

	@JsonProperty("site")
	@Override
	public String getSiteKey() {
		return this.site;
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
