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

public class LazyStringRecordValue implements ObjectValue,
		Iterator<PropertyValuePair>, PropertyValuePair {

	private static final long serialVersionUID = -9044711997743139121L;
	
	final String[] strings;
	final Sort sort;

	int currentPosition;

	public LazyStringRecordValue(String innerString, Sort sort) {
		this.sort = sort;
		this.strings = innerString.split("\\|");
	}

	@Override
	public Sort getSort() {
		return this.sort;
	}

	@Override
	public Iterator<PropertyValuePair> iterator() {
		this.currentPosition = -1;
		return this;
	}

	@Override
	public int size() {
		return this.strings.length;
	}

	@Override
	public boolean hasNext() {
		return this.currentPosition + 1 < this.strings.length;
	}

	@Override
	public PropertyValuePair next() {
		this.currentPosition++;
		return this;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProperty() {
		return this.sort.getPropertyRanges().get(this.currentPosition)
				.getProperty();
	}

	@Override
	public Value getValue() {
		return new StringValueImpl(this.strings[this.currentPosition].replace(
				"@i", "|").replace("@@", "@"), Sort.SORT_STRING);
	}

}
