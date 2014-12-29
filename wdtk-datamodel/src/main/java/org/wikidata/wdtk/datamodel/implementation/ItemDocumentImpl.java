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
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

public class ItemDocumentImpl extends TermedStatementDocumentImpl implements
		ItemDocument {

	final ItemIdValue itemId;
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
		super(itemIdValue, labels, descriptions, aliases, statementGroups);
		Validate.notNull(itemIdValue, "item ID cannot be null");
		Validate.notNull(siteLinks, "site links cannot be null");

		this.itemId = itemIdValue;
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
	public Map<String, SiteLink> getSiteLinks() {
		return Collections.unmodifiableMap(siteLinks);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsItemDocument(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
