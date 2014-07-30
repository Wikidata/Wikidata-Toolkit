package org.wikidata.wdtk.storage.dboldcode;

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
import org.wikidata.wdtk.storage.datamodel.PropertyRange;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.db.DatabaseManager;

public class LazyRecordValue implements ObjectValue,
		Iterator<PropertyValuePair>, PropertyValuePair {

	final RecordValueForSerialization rvfs;
	final Sort sort;
	final DatabaseManager databaseManager;

	int iRef;
	int iObject;
	int i;
	byte curType;
	PropertyRange curPropertyRange;

	public LazyRecordValue(RecordValueForSerialization rvfs, Sort sort,
			DatabaseManager databaseManager) {
		this.sort = sort;
		this.databaseManager = databaseManager;
		this.rvfs = rvfs;
	}

	@Override
	public Sort getSort() {
		return this.sort;
	}

	@Override
	public Iterator<PropertyValuePair> iterator() {
		this.iRef = -1;
		this.iObject = -1;
		this.i = -1;
		return this;
	}

	@Override
	public boolean hasNext() {
		return (this.iRef + 1 < this.rvfs.getRefs().length)
				|| (this.iObject + 1 < this.rvfs.getObjects().length);
	}

	@Override
	public PropertyValuePair next() {
		this.i++;
		this.curPropertyRange = this.sort.getPropertyRanges().get(this.i);
		this.curType = SerializationConverter.getFieldType(
				this.curPropertyRange.getRange(), this.databaseManager);

		if (this.curType == SerializationConverter.TYPE_REF) {
			this.iRef++;
		} else {
			this.iObject++;
		}

		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProperty() {
		return this.curPropertyRange.getProperty();
	}

	@Override
	public Value getValue() {
		switch (this.curType) {
		case SerializationConverter.TYPE_REF:
			String sortName = this.curPropertyRange.getRange();
			return this.databaseManager.fetchValue(
					this.rvfs.getRefs()[this.iRef], sortName);
		case SerializationConverter.TYPE_STRING:
			return new StringValueImpl(
					(String) this.rvfs.getObjects()[this.iObject],
					getCurrentSort());
		case SerializationConverter.TYPE_RECORD:
			return new LazyRecordValue(
					(RecordValueForSerialization) this.rvfs.getObjects()[this.iObject],
					getCurrentSort(), this.databaseManager);
		case SerializationConverter.TYPE_OBJECT:
			return new LazyObjectValue(
					(ObjectValueForSerialization) this.rvfs.getObjects()[this.iObject],
					getCurrentSort(), this.databaseManager);
		default:
			throw new RuntimeException("Unsupported type");
		}
	}

	@Override
	public int size() {
		return this.getSort().getPropertyRanges().size();
	}

	protected Sort getCurrentSort() {
		return databaseManager.getSortSchema().getSort(
				this.curPropertyRange.getRange());
	}

}
