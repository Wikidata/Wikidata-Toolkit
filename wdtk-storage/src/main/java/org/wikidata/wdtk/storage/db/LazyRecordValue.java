package org.wikidata.wdtk.storage.db;

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
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;

public class LazyRecordValue implements ObjectValue,
		Iterator<PropertyValuePair>, PropertyValuePair {

	final RecordValueDictionary recordDictionary;
	final RecordValueForSerialization rvfs;

	int iLong;
	int iString;
	int i;
	boolean atString;

	public LazyRecordValue(RecordValueForSerialization rvfs,
			RecordValueDictionary recordDictionary) {
		this.recordDictionary = recordDictionary;
		this.rvfs = rvfs;
	}

	@Override
	public Sort getSort() {
		return this.recordDictionary.getSort();
	}

	@Override
	public Iterator<PropertyValuePair> iterator() {
		this.iLong = -1;
		this.iString = -1;
		this.i = 0;
		return this;
	}

	@Override
	public boolean hasNext() {
		return (this.iLong + 1 < this.rvfs.getLongs().length)
				|| (this.iString + 1 < this.rvfs.getStrings().length);
	}

	@Override
	public PropertyValuePair next() {
		if (this.recordDictionary.isString(this.i)) {
			this.iString++;
			this.atString = true;
		} else {
			this.iLong++;
			this.atString = false;
		}
		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProperty() {
		return this.recordDictionary.getSort().getPropertyRanges().get(this.i)
				.getProperty();
	}

	@Override
	public Value getValue() {
		if (this.atString) {
			return new StringValueImpl(this.rvfs.getStrings()[this.iString],
					Sort.SORT_STRING);
		} else {
			return this.recordDictionary.databaseManager.fetchValue(
					this.rvfs.getLongs()[this.iLong], this.recordDictionary
							.getSort().getPropertyRanges().get(this.i)
							.getRange());
		}
	}

}
