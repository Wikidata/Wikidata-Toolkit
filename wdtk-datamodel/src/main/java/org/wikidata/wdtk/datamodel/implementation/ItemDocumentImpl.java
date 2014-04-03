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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

public class ItemDocumentImpl extends TermedDocumentImpl implements
		ItemDocument {

	final ItemIdValue itemId;
	final List<StatementGroup> statementGroups;
	final Map<String, SiteLink> siteLinks;

	/**
	 * Constructor.
	 * 
	 * @param itemIdValue
	 *            the id of the item that data is about
	 * @param labels
	 *            the list of labels of this item, with at most one label for
	 *            each language code
	 * @param descriptions
	 *            the list of descriptions of this item, with at most one
	 *            description for each language code
	 * @param aliases
	 *            the list of aliases of this item
	 * @param statementGroups
	 *            the list of statement groups of this item; all of them must
	 *            have the given itemIdValue as their subject
	 * @param siteLinks
	 *            the sitelinks of this item by site key
	 */
	ItemDocumentImpl(ItemIdValue itemIdValue,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statementGroups,
			Map<String, SiteLink> siteLinks) {
		super(labels, descriptions, aliases);
		Validate.notNull(itemIdValue, "item ID cannot be null");
		Validate.notNull(statementGroups, "statement list cannot be null");
		Validate.notNull(siteLinks, "site links cannot be null");

		if (!statementGroups.isEmpty()) {
			for (StatementGroup sg : statementGroups) {
				if (!itemIdValue.equals(sg.getSubject())) {
					throw new IllegalArgumentException(
							"All statement groups in a document must have the same subject");
				}
			}
		}

		this.itemId = itemIdValue;
		this.statementGroups = statementGroups;
		this.siteLinks = siteLinks;
	}

	@Override
	public EntityIdValue getEntityId() {
		return itemId;
	}

	@Override
	public ItemIdValue getItemId() {
		return itemId;
	}

	@Override
	public List<StatementGroup> getStatementGroups() {
		return Collections.unmodifiableList(statementGroups);
	}

	@Override
	public Map<String, SiteLink> getSiteLinks() {
		return Collections.unmodifiableMap(siteLinks);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + itemId.hashCode();
		result = prime * result + siteLinks.hashCode();
		result = prime * result + statementGroups.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof ItemDocumentImpl)) {
			return false;
		}
		ItemDocumentImpl other = (ItemDocumentImpl) obj;

		return itemId.equals(other.itemId) && siteLinks.equals(other.siteLinks)
				&& statementGroups.equals(other.statementGroups);
	}

	@Override
	public String toString(){
		return "ItemDocument {qId = " + this.itemId 
				+ ", " + this.labels.size() + " labels, "
				+ this.descriptions.size() + " descriptions, "
				+ this.aliases.size() + " aliases, "
				+ this.siteLinks.size() + " site links, "
				+ this.statementGroups.size() + " statement groups"
				+ "}";
	}
}
