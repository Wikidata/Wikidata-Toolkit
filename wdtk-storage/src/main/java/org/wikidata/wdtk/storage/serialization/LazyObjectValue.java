package org.wikidata.wdtk.storage.serialization;

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

import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.db.DatabaseManager;
import org.wikidata.wdtk.storage.db.PropertySignature;

public class LazyObjectValue implements ObjectValue,
		Iterator<PropertyValuePair>, PropertyValuePair {

	final Sort sort;
	final DatabaseManager databaseManager;
	final int[] properties;
	final int[] refs;
	final Value[] values;

	int iRef;
	int iValue;
	int i;
	boolean curRef;
	PropertySignature currentPropertySignature;

	public LazyObjectValue(int[] properties, int[] refs, Value[] values,
			Sort sort, DatabaseManager databaseManager) {
		this.properties = properties;
		this.refs = refs;
		this.values = values;
		this.sort = sort;
		this.databaseManager = databaseManager;
	}

	@Override
	public Sort getSort() {
		return this.sort;
	}

	@Override
	public Iterator<PropertyValuePair> iterator() {
		this.iRef = -1;
		this.iValue = -1;
		this.i = -1;
		return this;
	}

	@Override
	public boolean hasNext() {
		return this.i + 1 < this.properties.length;
	}

	@Override
	public PropertyValuePair next() {
		this.i++;
		this.currentPropertySignature = null;
		// TODO maybe make the current sort computation a bit more direct
		String valueSortName = this.databaseManager.getSortSchema()
				.getSort(getCurrentPropertySignature().getRangeId()).getName();
		this.curRef = databaseManager.getSortSchema().useDictionary(
				valueSortName);

		if (this.curRef) {
			this.iRef++;
		} else {
			this.iValue++;
		}
		this.currentPropertySignature = null;
		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProperty() {
		return getCurrentPropertySignature().getPropertyName();
	}

	@Override
	public Value getValue() {
		if (this.curRef) {
			return this.databaseManager.fetchValue(this.refs[this.iRef],
					getCurrentPropertySignature().getRangeId());
		} else {
			return this.values[this.iValue];
		}
	}

	PropertySignature getCurrentPropertySignature() {
		if (this.currentPropertySignature == null) {
			this.currentPropertySignature = this.databaseManager
					.fetchPropertySignature(this.properties[this.i]);
		}
		return this.currentPropertySignature;
	}

	@Override
	public int size() {
		return this.properties.length;
	}

}
