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
package org.wikidata.wdtk.datamodel.implementation;

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Jackson implementation of {@link ItemUpdate}.
 */
public class ItemUpdateImpl extends TermedStatementDocumentUpdateImpl implements ItemUpdate {

	@JsonIgnore
	private final Map<String, SiteLink> modifiedSiteLinks;
	@JsonIgnore
	private final Set<String> removedSiteLinks;

	/**
	 * Initializes new item update.
	 * 
	 * @param entityId
	 *            ID of the item that is to be updated
	 * @param revision
	 *            base item revision to be updated or {@code null} if not available
	 * @param labels
	 *            changes in entity labels or {@code null} for no change
	 * @param descriptions
	 *            changes in entity descriptions or {@code null} for no change
	 * @param aliases
	 *            changes in entity aliases, possibly empty
	 * @param statements
	 *            changes in entity statements, possibly empty
	 * @param modifiedSiteLinks
	 *            added or replaced site links
	 * @param removedSiteLinks
	 *            site keys of removed site links
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	public ItemUpdateImpl(
			ItemIdValue entityId,
			ItemDocument revision,
			TermUpdate labels,
			TermUpdate descriptions,
			Map<String, List<MonolingualTextValue>> aliases,
			StatementUpdate statements,
			Collection<SiteLink> modifiedSiteLinks,
			Collection<String> removedSiteLinks) {
		super(entityId, revision, labels, descriptions, aliases, statements);
		this.modifiedSiteLinks = Collections.unmodifiableMap(modifiedSiteLinks.stream()
				.collect(toMap(sl -> sl.getSiteKey(), sl -> sl)));
		this.removedSiteLinks = Collections.unmodifiableSet(new HashSet<>(removedSiteLinks));
	}

	@JsonIgnore
	@Override
	public ItemIdValue getEntityId() {
		return (ItemIdValue) super.getEntityId();
	}

	@JsonIgnore
	@Override
	public ItemDocument getBaseRevision() {
		return (ItemDocument) super.getBaseRevision();
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return super.isEmpty() && modifiedSiteLinks.isEmpty() && removedSiteLinks.isEmpty();
	}

	@JsonIgnore
	@Override
	public Map<String, SiteLink> getModifiedSiteLinks() {
		return modifiedSiteLinks;
	}

	@JsonIgnore
	@Override
	public Set<String> getRemovedSiteLinks() {
		return removedSiteLinks;
	}

	static class RemovedSiteLink {

		private final String site;

		RemovedSiteLink(String site) {
			this.site = site;
		}

		@JsonProperty
		String getSite() {
			return site;
		}

		@JsonProperty("remove")
		String getRemoveCommand() {
			return "";
		}

	}

	@JsonProperty("sitelinks")
	@JsonInclude(Include.NON_EMPTY)
	Map<String, Object> getJsonSiteLinks() {
		Map<String, Object> map = new HashMap<>();
		for (SiteLink link : modifiedSiteLinks.values()) {
			map.put(link.getSiteKey(), link);
		}
		for (String site : removedSiteLinks) {
			map.put(site, new RemovedSiteLink(site));
		}
		return map;
	}

}
