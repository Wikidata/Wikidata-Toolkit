package org.wikidata.wdtk.datamodel.implementation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.EntityId;
import org.wikidata.wdtk.datamodel.interfaces.ItemId;
import org.wikidata.wdtk.datamodel.interfaces.ItemRecord;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class ItemRecordImpl extends EntityRecordImpl implements ItemRecord {

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
	public ItemRecordImpl(ItemId itemId, Map<String, String> labels,
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
		if (!(obj instanceof ItemRecordImpl)) {
			return false;
		}
		ItemRecordImpl other = (ItemRecordImpl) obj;

		return itemId.equals(other.itemId) && siteLinks.equals(other.siteLinks)
				&& statements.equals(other.statements);
	}

}
