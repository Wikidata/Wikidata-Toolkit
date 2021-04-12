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
package org.wikidata.wdtk.datamodel.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemUpdate;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

/**
 * Builder for incremental construction of {@link ItemUpdate} objects.
 */
public class ItemUpdateBuilder extends TermedStatementDocumentUpdateBuilder {

	private final Map<String, SiteLink> modifiedSiteLinks = new HashMap<>();
	private final Set<String> removedSiteLinks = new HashSet<>();

	private ItemUpdateBuilder(ItemIdValue itemId) {
		super(itemId);
	}

	private ItemUpdateBuilder(ItemDocument revision) {
		super(revision);
	}

	/**
	 * Creates new builder object for constructing update of item entity with given
	 * ID.
	 * 
	 * @param itemId
	 *            ID of the item entity that is to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code itemId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code itemId} is a placeholder ID
	 */
	public static ItemUpdateBuilder forEntityId(ItemIdValue itemId) {
		return new ItemUpdateBuilder(itemId);
	}

	/**
	 * Creates new builder object for constructing update of given base item entity
	 * revision. Provided item document might not represent the latest revision of
	 * the item entity as currently stored in Wikibase. It will be used for
	 * validation in builder methods. If the document has revision ID, it will be
	 * used to detect edit conflicts.
	 * 
	 * @param revision
	 *            base item entity revision to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} has placeholder ID
	 */
	public static ItemUpdateBuilder forBaseRevision(ItemDocument revision) {
		return new ItemUpdateBuilder(revision);
	}

	@Override
	ItemIdValue getEntityId() {
		return (ItemIdValue) super.getEntityId();
	}

	@Override
	ItemDocument getBaseRevision() {
		return (ItemDocument) super.getBaseRevision();
	}

	@Override
	public ItemUpdateBuilder updateStatements(StatementUpdate update) {
		super.updateStatements(update);
		return this;
	}

	@Override
	public ItemUpdateBuilder updateLabels(TermUpdate update) {
		super.updateLabels(update);
		return this;
	}

	@Override
	public ItemUpdateBuilder updateDescriptions(TermUpdate update) {
		super.updateDescriptions(update);
		return this;
	}

	@Override
	public ItemUpdateBuilder setAliases(String language, List<String> aliases) {
		super.setAliases(language, aliases);
		return this;
	}

	/**
	 * Adds or replaces site link. If there is no site link for the site key, new
	 * site link is added. If a site link with this site key already exists, it is
	 * replaced. Site links with other site keys are not touched. Calling this
	 * method overrides any previous changes made with the same site key by this
	 * method or {@link #removeSiteLink(String)}.
	 * <p>
	 * If base entity revision was provided, attempt to overwrite some site link
	 * with identical site link will be silently ignored, resulting in empty update.
	 * 
	 * @param link
	 *            new or replacement site link
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code link} is {@code null}
	 */
	public ItemUpdateBuilder setSiteLink(SiteLink link) {
		Objects.requireNonNull(link, "Site link cannot be null.");
		if (getBaseRevision() != null) {
			SiteLink original = getBaseRevision().getSiteLinks().get(link.getSiteKey());
			if (link.equals(original)) {
				modifiedSiteLinks.remove(link.getSiteKey());
				removedSiteLinks.remove(link.getSiteKey());
				return this;
			}
		}
		modifiedSiteLinks.put(link.getSiteKey(), link);
		removedSiteLinks.remove(link.getSiteKey());
		return this;
	}

	/**
	 * Removes site link. Site links with other site keys are not touched. Calling
	 * this method overrides any previous changes made with the same site key by
	 * this method or {@link #setSiteLink(SiteLink)}.
	 * <p>
	 * If base entity revision was provided, attempts to remove missing site links
	 * will be silently ignored, resulting in empty update.
	 * 
	 * @param site
	 *            site key of the removed site link
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code site} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code site} is blank
	 */
	public ItemUpdateBuilder removeSiteLink(String site) {
		Validate.notBlank(site, "Site key cannot be null.");
		if (getBaseRevision() != null && !getBaseRevision().getSiteLinks().containsKey(site)) {
			modifiedSiteLinks.remove(site);
			return this;
		}
		removedSiteLinks.add(site);
		modifiedSiteLinks.remove(site);
		return this;
	}

	/**
	 * Replays all changes in provided update into this builder object. Changes from
	 * the update are added on top of changes already present in this builder
	 * object.
	 * 
	 * @param update
	 *            item update to replay
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code update} cannot be applied to base entity revision (if
	 *             available)
	 */
	public ItemUpdateBuilder apply(ItemUpdate update) {
		super.apply(update);
		for (SiteLink link : update.getModifiedSiteLinks().values()) {
			setSiteLink(link);
		}
		for (String site : update.getRemovedSiteLinks()) {
			removeSiteLink(site);
		}
		return this;
	}

	@Override
	public ItemUpdate build() {
		return Datamodel.makeItemUpdate(getEntityId(), getBaseRevision(), labels, descriptions, aliases, statements,
				modifiedSiteLinks.values(), removedSiteLinks);
	}

}
