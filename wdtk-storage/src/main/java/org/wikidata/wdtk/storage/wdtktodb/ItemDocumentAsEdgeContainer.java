package org.wikidata.wdtk.storage.wdtktodb;

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

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class ItemDocumentAsEdgeContainer implements EdgeContainer,
Iterator<PropertyTargets> {

	private static final byte DO_DOCTYPE = 1;
	private static final byte DO_SITELINKS = 2;

	final ItemDocument itemDocument;
	final WdtkAdaptorHelper helpers;

	final TermsPropertyTargetIterator termPropertyTargetIterator;
	final StatementsPropertyTargetIterator statementsPropertyTargetIterator;

	int todos;

	public ItemDocumentAsEdgeContainer(ItemDocument itemDocument,
			WdtkAdaptorHelper helpers) {
		this.itemDocument = itemDocument;
		this.helpers = helpers;

		this.termPropertyTargetIterator = new TermsPropertyTargetIterator(
				itemDocument);
		this.statementsPropertyTargetIterator = new StatementsPropertyTargetIterator(
				itemDocument.getStatementGroups(), helpers);
	}

	protected void reset() {
		this.termPropertyTargetIterator.reset();
		this.statementsPropertyTargetIterator.reset();

		this.todos = DO_DOCTYPE;
		if (!itemDocument.getSiteLinks().isEmpty()) {
			this.todos |= DO_SITELINKS;
		}
	}

	@Override
	public Iterator<PropertyTargets> iterator() {
		reset();
		return this;
	}

	@Override
	public Value getSource() {
		return new EntityValueAsValue(this.itemDocument.getEntityId());
	}

	@Override
	public boolean hasNext() {
		return (this.todos != 0) || this.termPropertyTargetIterator.hasNext()
				|| this.statementsPropertyTargetIterator.hasNext();
	}

	@Override
	public PropertyTargets next() {
		if ((this.todos & DO_DOCTYPE) != 0) {
			this.todos = this.todos & ~DO_DOCTYPE;
			return new SimplePropertyTargets(WdtkSorts.PROP_DOCTYPE,
					new StringValueImpl(WdtkSorts.VALUE_DOCTYPE_ITEM,
							WdtkSorts.SORT_SPECIAL_STRING));
		} else if (this.statementsPropertyTargetIterator.hasNext()) {
			return this.statementsPropertyTargetIterator.next();
		} else if (this.termPropertyTargetIterator.hasNext()) {
			return this.termPropertyTargetIterator.next();
		} else if ((this.todos & DO_SITELINKS) != 0) {
			this.todos = this.todos & ~DO_SITELINKS;
			return new SiteLinksAsPropertyTargets(this.itemDocument
					.getSiteLinks().values());
		} else {
			return null;
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getEdgeCount() {
		return 1 + this.termPropertyTargetIterator.getEdgeCount()
				+ this.statementsPropertyTargetIterator.getEdgeCount()
				+ (this.itemDocument.getSiteLinks().isEmpty() ? 0 : 1);
	}

}
