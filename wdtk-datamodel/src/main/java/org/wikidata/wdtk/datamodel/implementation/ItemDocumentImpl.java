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
import org.wikidata.wdtk.datamodel.interfaces.EntityId;
import org.wikidata.wdtk.datamodel.interfaces.ItemId;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class ItemDocumentImpl extends TermedDocumentImpl implements ItemDocument {

	final ItemId itemId;
	final List<Statement> statements;
	final Map<String, SiteLink> siteLinks;

	/**
	 * Constructor.
	 * 
	 * @param itemId
	 *            the id of the item that data is about
	 * @param labels
	 *            the labels of this item by language code
	 * @param descriptions
	 *            the descriptions of this item by language code
	 * @param aliases
	 *            the alias lists of this item by language code
	 * @param statements
	 *            the list of statements of this item
	 * @param siteLinks
	 *            the sitelinks of this item by site key
	 */
	ItemDocumentImpl(ItemId itemId, Map<String, String> labels,
			Map<String, String> descriptions,
			Map<String, List<String>> aliases, List<Statement> statements,
			Map<String, SiteLink> siteLinks) {
		super(labels, descriptions, aliases);
		Validate.notNull(itemId, "item ID cannot be null");
		Validate.notNull(statements, "statement list cannot be null");
		Validate.notNull(siteLinks, "site links cannot be null");
		this.itemId = itemId;
		this.statements = statements;
		this.siteLinks = siteLinks;
	}

	@Override
	public EntityId getEntityId() {
		return itemId;
	}

	@Override
	public ItemId getItemId() {
		return itemId;
	}

	@Override
	public List<Statement> getStatements() {
		return Collections.unmodifiableList(statements);
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
		result = prime * result + statements.hashCode();
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
				&& statements.equals(other.statements);
	}

}
