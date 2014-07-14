package org.wikidata.wdtk.storage.wdtkbindings;

/*
 * #%L
 * Wikidata Toolkit Storage
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

import java.util.Iterator;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.util.NestedIterator;

public class ItemDocumentEdgeContainer implements EdgeContainer,
		Iterator<PropertyTargets> {

	final ItemDocument itemDocument;
	final WdtkAdaptorHelper helpers;
	int propertyIteratorPos;
	int labelOffset;
	int descriptionOffset;
	int aliasOffset;
	int statementOffset;
	int sitelinkOffset;
	int maxOffset;

	public ItemDocumentEdgeContainer(ItemDocument itemDocument,
			WdtkAdaptorHelper helpers) {
		this.itemDocument = itemDocument;
		this.helpers = helpers;
		this.propertyIteratorPos = 0;

		int totalOffset = 0;
		if (itemDocument.getLabels().isEmpty()) {
			this.labelOffset = -1;
		} else {
			this.labelOffset = totalOffset;
			totalOffset++;
		}
		if (itemDocument.getDescriptions().isEmpty()) {
			this.descriptionOffset = -1;
		} else {
			this.descriptionOffset = totalOffset;
			totalOffset++;
		}
		if (itemDocument.getAliases().isEmpty()) {
			this.aliasOffset = -1;
		} else {
			this.aliasOffset = totalOffset;
			totalOffset++;
		}
		if (itemDocument.getStatementGroups().isEmpty()) {
			this.statementOffset = -1;
		} else {
			this.statementOffset = totalOffset;
			totalOffset += itemDocument.getStatementGroups().size();
		}
		// if (itemDocument.getSiteLinks().isEmpty()) {
		// this.sitelinkOffset = -1;
		// } else {
		// this.sitelinkOffset = totalOffset;
		// totalOffset++;
		// }
		this.maxOffset = totalOffset - 1;
	}

	@Override
	public Iterator<PropertyTargets> iterator() {
		this.propertyIteratorPos = -1;
		return this;
	}

	@Override
	public Value getSource() {
		return new EntityValueAdaptor(this.itemDocument.getEntityId());
	}

	@Override
	public boolean hasNext() {
		return this.propertyIteratorPos < this.maxOffset;
	}

	@Override
	public PropertyTargets next() {
		this.propertyIteratorPos++;
		if (this.propertyIteratorPos == this.labelOffset) {
			return new TermsAdaptor("wdtk:labels", this.itemDocument
					.getLabels().values().iterator(), this.itemDocument
					.getLabels().size(), this.helpers);
		} else if (this.propertyIteratorPos == this.descriptionOffset) {
			return new TermsAdaptor("wdtk:descriptions", this.itemDocument
					.getDescriptions().values().iterator(), this.itemDocument
					.getDescriptions().values().size(), this.helpers);
		} else if (this.propertyIteratorPos == this.aliasOffset) {
			int targetCount = 0;
			for (List<MonolingualTextValue> l : this.itemDocument.getAliases()
					.values()) {
				targetCount += l.size();
			}
			return new TermsAdaptor("wdtk:aliases",
					new NestedIterator<MonolingualTextValue>(this.itemDocument
							.getAliases().values()), targetCount, this.helpers);
		} else if (this.statementOffset >= 0
				&& this.propertyIteratorPos - this.statementOffset < this.itemDocument
						.getStatementGroups().size()) {
			return new StatementGroupAdaptor(this.itemDocument
					.getStatementGroups().get(
							this.propertyIteratorPos - this.statementOffset),
					this.helpers);
		}
		return null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getEdgeCount() {
		return this.maxOffset + 1;
	}

}
