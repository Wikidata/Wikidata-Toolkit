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

import com.fasterxml.jackson.annotation.*;
import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Jackson implementation of {@link SiteLink}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 * @author Thomas Pellissier Tanon
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteLinkImpl implements SiteLink {

	private final String title;
	private final String site;
	private final List<ItemIdValue> badges;

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
	public SiteLinkImpl(
			String title,
			String site,
			List<ItemIdValue> badges) {
		Validate.notNull(title);
		this.title = title;
		Validate.notNull(site);
		this.site = site;
		this.badges = (badges == null) ? Collections.emptyList() : badges;
		this.badges.sort(Comparator.comparing(EntityIdValue::getId));
	}

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
	SiteLinkImpl(
			@JsonProperty("title") String title,
			@JsonProperty("site") String site,
			@JsonProperty("badges") List<String> badges,
			@JacksonInject("siteIri") String siteIri
	) {
		Validate.notNull(title);
		this.title = title;
		Validate.notNull(site);
		this.site = site;
		this.badges = (badges == null || badges.isEmpty())
			? Collections.emptyList()
			: constructBadges(badges, siteIri);
	}

	private List<ItemIdValue> constructBadges(List<String> badges, String siteIri) {
		List<ItemIdValue> output = new ArrayList<>(badges.size());
		for(String badge : badges) {
			output.add(new ItemIdValueImpl(badge, siteIri));
		}
		return output;
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

	@JsonIgnore
	@Override
	public List<ItemIdValue> getBadges() {
		return this.badges;
	}

	@JsonProperty("badges")
	List<String> getBadgesString() {
		if (badges.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> output = new ArrayList<>(badges.size());
		for(ItemIdValue badge : badges) {
			output.add(badge.getId());
		}
		return output;
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
