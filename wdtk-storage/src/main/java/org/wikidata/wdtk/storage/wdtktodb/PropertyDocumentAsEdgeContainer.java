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

import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class PropertyDocumentAsEdgeContainer implements EdgeContainer,
		Iterator<PropertyTargets> {

	private static final byte DO_DOCTYPE = 1;
	private static final byte DO_DATATYPE = 2;

	final PropertyDocument propertyDocument;
	final WdtkAdaptorHelper helpers;

	final TermsPropertyTargetIterator termPropertyTargetIterator;

	int todos;

	public PropertyDocumentAsEdgeContainer(PropertyDocument propertyDocument,
			WdtkAdaptorHelper helpers) {
		this.propertyDocument = propertyDocument;
		this.helpers = helpers;

		this.termPropertyTargetIterator = new TermsPropertyTargetIterator(
				propertyDocument);
	}

	protected void reset() {
		this.termPropertyTargetIterator.reset();
		this.todos = DO_DOCTYPE | DO_DATATYPE;
	}

	@Override
	public Iterator<PropertyTargets> iterator() {
		reset();
		return this;
	}

	@Override
	public Value getSource() {
		return new EntityValueAsValue(this.propertyDocument.getEntityId());
	}

	@Override
	public boolean hasNext() {
		return (this.todos != 0) || this.termPropertyTargetIterator.hasNext();
	}

	@Override
	public PropertyTargets next() {
		if ((this.todos & DO_DOCTYPE) != 0) {
			this.todos = this.todos & ~DO_DOCTYPE;
			return new SimplePropertyTargets(WdtkSorts.PROP_DOCTYPE,
					new StringValueImpl(WdtkSorts.VALUE_DOCTYPE_PROPERTY,
							WdtkSorts.SORT_SPECIAL_STRING));
		} else if ((this.todos & DO_DATATYPE) != 0) {
			this.todos = this.todos & ~DO_DATATYPE;
			return new SimplePropertyTargets(WdtkSorts.PROP_DATATYPE,
					new StringValueImpl(this.propertyDocument.getDatatype()
							.getIri(), WdtkSorts.SORT_SPECIAL_STRING));
		} else if (this.termPropertyTargetIterator.hasNext()) {
			return this.termPropertyTargetIterator.next();
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
		return 2 + this.termPropertyTargetIterator.getEdgeCount();
	}
}
