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

import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;

public class ReferenceAdaptor implements ObjectValue,
		Iterator<PropertyValuePair> {

	final WdtkAdaptorHelper helpers;
	final Iterator<Snak> snakIterator;

	public ReferenceAdaptor(Reference reference, WdtkAdaptorHelper helpers) {
		this.snakIterator = reference.getAllSnaks();
		this.helpers = helpers;
	}

	@Override
	public Sort getSort() {
		return WdtkSorts.SORT_REFERENCE;
	}

	@Override
	public Iterator<PropertyValuePair> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return this.snakIterator.hasNext();
	}

	@Override
	public PropertyValuePair next() {
		this.snakIterator.next().accept(this.helpers.getSnakAdaptor());
		return this.helpers.getSnakAdaptor();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
