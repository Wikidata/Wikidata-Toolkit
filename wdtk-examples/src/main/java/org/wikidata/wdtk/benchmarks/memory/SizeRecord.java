package org.wikidata.wdtk.benchmarks.memory;

/*
 * #%L
 * Wikidata Toolkit Examples
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

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;

/**
 * This is record to store the size in bytes of items ({@link ItemDocument}) and
 * properties ({@link PropertyDocument}).
 * 
 * @author Julian Mendez
 * 
 */
public class SizeRecord {

	long sizeOfAliases = 0;
	long sizeOfChars = 0;
	long sizeOfDescriptions = 0;
	long sizeOfEntity = 0;
	long sizeOfLabels = 0;
	long sizeOfSiteLinks = 0;
	long sizeOfStatementGroups = 0;

	/**
	 * Constructs a new size record.
	 */
	public SizeRecord() {
	}

	/**
	 * Constructs a new size record using an item document.
	 * 
	 * @param itemDocument
	 *            item document
	 */
	public SizeRecord(ItemDocument itemDocument) {
		this.sizeOfEntity = MemoryMeasurer.getInstance().getDeepSize(
				itemDocument);
		this.sizeOfChars = MemoryMeasurer.getInstance().getDeepSizeWithClass(
				itemDocument, char[].class);
		this.sizeOfLabels = MemoryMeasurer.getInstance().getDeepSize(
				itemDocument.getLabels());
		this.sizeOfDescriptions = MemoryMeasurer.getInstance().getDeepSize(
				itemDocument.getDescriptions());
		this.sizeOfAliases = MemoryMeasurer.getInstance().getDeepSize(
				itemDocument.getAliases());
		this.sizeOfStatementGroups = MemoryMeasurer.getInstance().getDeepSize(
				itemDocument.getStatementGroups());
		this.sizeOfSiteLinks = MemoryMeasurer.getInstance().getDeepSize(
				itemDocument.getSiteLinks());
	}

	/**
	 * Constructs a new size record using a property document.
	 * 
	 * @param propertyDocument
	 *            property document
	 */
	public SizeRecord(PropertyDocument propertyDocument) {
		this.sizeOfEntity = MemoryMeasurer.getInstance().getDeepSize(
				propertyDocument);
		this.sizeOfChars = MemoryMeasurer.getInstance().getDeepSizeWithClass(
				propertyDocument, char[].class);
		this.sizeOfLabels = MemoryMeasurer.getInstance().getDeepSize(
				propertyDocument.getLabels());
		this.sizeOfDescriptions = MemoryMeasurer.getInstance().getDeepSize(
				propertyDocument.getDescriptions());
		this.sizeOfAliases = MemoryMeasurer.getInstance().getDeepSize(
				propertyDocument.getAliases());
	}

	/**
	 * Accumulates the values of other size record in this size record.
	 * 
	 * @param other
	 *            other size record
	 * @return this size record
	 */
	public SizeRecord add(SizeRecord other) {
		this.sizeOfEntity += other.sizeOfEntity;
		this.sizeOfChars += other.sizeOfChars;
		this.sizeOfLabels += other.sizeOfLabels;
		this.sizeOfDescriptions += other.sizeOfDescriptions;
		this.sizeOfAliases += other.sizeOfAliases;
		this.sizeOfStatementGroups += other.sizeOfStatementGroups;
		this.sizeOfSiteLinks += other.sizeOfSiteLinks;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof SizeRecord)) {
			return false;
		}
		SizeRecord other = (SizeRecord) obj;
		return ((this.sizeOfEntity == other.sizeOfEntity)
				&& (this.sizeOfChars == other.sizeOfChars)
				&& (this.sizeOfLabels == other.sizeOfLabels)
				&& (this.sizeOfDescriptions == other.sizeOfDescriptions)
				&& (this.sizeOfAliases == other.sizeOfAliases)
				&& (this.sizeOfStatementGroups == other.sizeOfStatementGroups) && (this.sizeOfSiteLinks == other.sizeOfSiteLinks));
	}

	/**
	 * Returns the size of aliases.
	 * 
	 * @return the size of aliases
	 */
	public long getSizeOfAliases() {
		return this.sizeOfAliases;
	}

	/**
	 * Returns the size of char arrays.
	 * 
	 * @return the size of char arrays
	 */
	public long getSizeOfChars() {
		return this.sizeOfChars;
	}

	/**
	 * Returns the size of descriptions.
	 * 
	 * @return the size of descriptions
	 */
	public long getSizeOfDescriptions() {
		return this.sizeOfDescriptions;
	}

	/**
	 * Returns the size of the entity.
	 * 
	 * @return the size of the entity
	 */
	public long getSizeOfEntity() {
		return this.sizeOfEntity;
	}

	/**
	 * Returns the size of the labels.
	 * 
	 * @return the size of the labels
	 */
	public long getSizeOfLabels() {
		return this.sizeOfLabels;
	}

	/**
	 * Returns the size of site links.
	 * 
	 * @return the size of site links
	 */
	public long getSizeOfSiteLinks() {
		return this.sizeOfSiteLinks;
	}

	/**
	 * Returns the size of statement groups.
	 * 
	 * @return the size of statement groups
	 */
	public long getSizeOfStatementGroups() {
		return this.sizeOfStatementGroups;
	}

	@Override
	public int hashCode() {
		return (int) (this.sizeOfEntity + (0x1F * this.sizeOfChars));
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("<record entity=\"");
		sb.append(this.sizeOfEntity);
		sb.append("\" chars=\"");
		sb.append(this.sizeOfChars);
		sb.append("\" labels=\"");
		sb.append(this.sizeOfLabels);
		sb.append("\" descriptions=\"");
		sb.append(this.sizeOfDescriptions);
		sb.append("\" aliases=\"");
		sb.append(this.sizeOfAliases);
		sb.append("\" statement_groups=\"");
		sb.append(this.sizeOfStatementGroups);
		sb.append("\" site_links=\"");
		sb.append(this.sizeOfSiteLinks);
		sb.append("\" />");
		return sb.toString();
	}

}
