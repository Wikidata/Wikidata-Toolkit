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

public class LazyObjectValue implements ObjectValue,
		Iterator<PropertyValuePair>, PropertyValuePair {

	final ObjectValueForSerialization ovfs;
	final ObjectValueDictionary objectValueDictionary;

	int iLong;
	int iString;
	int i;
	byte currentType;
	PropertySignature currentProperty;

	public LazyObjectValue(ObjectValueForSerialization ovfs,
			ObjectValueDictionary objectValueDictionary) {
		this.ovfs = ovfs;
		this.objectValueDictionary = objectValueDictionary;
	}

	@Override
	public Sort getSort() {
		return this.objectValueDictionary.getSort();
	}

	@Override
	public Iterator<PropertyValuePair> iterator() {
		this.iLong = -1;
		this.iString = -1;
		this.i = -1;
		return this;
	}

	@Override
	public boolean hasNext() {
		return this.i < this.ovfs.getTypes().length;
	}

	@Override
	public PropertyValuePair next() {
		this.i++;
		if (this.ovfs.getTypes()[this.i] == ObjectValueForSerialization.TYPE_REF) {
			this.iLong++;
			this.currentType = ObjectValueForSerialization.TYPE_REF;
		} else if (this.ovfs.getTypes()[this.i] == ObjectValueForSerialization.TYPE_STRING) {
			this.iString++;
			this.currentType = ObjectValueForSerialization.TYPE_STRING;
		}
		this.currentProperty = null;
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
		if (this.currentType == ObjectValueForSerialization.TYPE_STRING) {
			return new StringValueImpl(this.ovfs.getStrings()[this.iString],
					Sort.SORT_STRING);
		} else if (this.currentType == ObjectValueForSerialization.TYPE_REF) {
			return this.objectValueDictionary.databaseManager.fetchValue(
					this.ovfs.getRefs()[this.iLong],
					getCurrentPropertySignature().getRangeId());
		} else {
			throw new UnsupportedOperationException(
					"Cannot reconstruct objects of type " + this.currentType);
		}
	}

	PropertySignature getCurrentPropertySignature() {
		if (this.currentProperty == null) {
			this.currentProperty = this.objectValueDictionary.databaseManager
					.fetchPropertySignature(this.ovfs.getProperties()[this.i - 1]);
		}
		return this.currentProperty;
	}

	@Override
	public int size() {
		return this.ovfs.getTypes().length;
	}

}
