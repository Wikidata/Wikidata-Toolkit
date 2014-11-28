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
import org.wikidata.wdtk.storage.datamodel.PropertyRange;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.db.DatabaseManager;

public class RecordValueFromSerialization implements ObjectValue,
		Iterator<PropertyValuePair>, PropertyValuePair {

	private static final long serialVersionUID = -8190931606758008228L;
	
	final int[] refs;
	final Value[] values;
	final Sort sort;
	final DatabaseManager databaseManager;

	int iRef;
	int iValue;
	int i;
	boolean curRef;
	PropertyRange curPropertyRange;

	public RecordValueFromSerialization(int[] refs, Value[] values, Sort sort,
			DatabaseManager databaseManager) {
		this.sort = sort;
		this.databaseManager = databaseManager;
		this.refs = refs;
		this.values = values;
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
		return (this.iRef + 1 < this.refs.length)
				|| (this.iValue + 1 < this.values.length);
	}

	@Override
	public PropertyValuePair next() {
		this.i++;
		this.curPropertyRange = this.sort.getPropertyRanges().get(this.i);
		this.curRef = databaseManager.getSortSchema().useDictionary(
				this.curPropertyRange.getRange());

		if (this.curRef) {
			this.iRef++;
		} else {
			this.iValue++;
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
		if (this.curRef) {
			String sortName = this.curPropertyRange.getRange();
			return this.databaseManager.fetchValue(this.refs[this.iRef],
					sortName);
		} else {
			return this.values[this.iValue];
		}
	}

	@Override
	public int size() {
		return this.getSort().getPropertyRanges().size();
	}

}
