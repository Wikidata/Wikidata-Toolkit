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
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValueImpl;
import org.wikidata.wdtk.storage.datamodel.Value;
import org.wikidata.wdtk.storage.db.DatabaseManager;
import org.wikidata.wdtk.storage.db.PropertySignature;

public class LazyObjectValue implements ObjectValue,
		Iterator<PropertyValuePair>, PropertyValuePair {

	final ObjectValueForSerialization ovfs;
	final Sort sort;
	final DatabaseManager databaseManager;

	int iRef;
	int iObject;
	int i;
	byte currentType;
	PropertySignature currentProperty;

	public LazyObjectValue(ObjectValueForSerialization ovfs, Sort sort,
			DatabaseManager databaseManager) {
		this.ovfs = ovfs;
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
		this.iObject = -1;
		this.i = -1;
		return this;
	}

	@Override
	public boolean hasNext() {
		return this.i + 1 < this.ovfs.getTypes().length;
	}

	@Override
	public PropertyValuePair next() {
		this.i++;
		this.currentType = this.ovfs.getTypes()[this.i];
		if (this.currentType == SerializationConverter.TYPE_REF) {
			this.iRef++;
		} else if (this.currentType == SerializationConverter.TYPE_STRING) {
			this.iObject++;
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
		switch (this.currentType) {
		case SerializationConverter.TYPE_STRING:
			return new StringValueImpl(
					(String) this.ovfs.getObjects()[this.iObject],
					getCurrentSort());
		case SerializationConverter.TYPE_REF:
			return this.databaseManager.fetchValue(
					this.ovfs.getRefs()[this.iRef],
					getCurrentPropertySignature().getRangeId());
		case SerializationConverter.TYPE_RECORD:
			return new LazyRecordValue(
					(RecordValueForSerialization) this.ovfs.getObjects()[this.iObject],
					getCurrentSort(), this.databaseManager);
		case SerializationConverter.TYPE_OBJECT:
			return new LazyObjectValue(
					(ObjectValueForSerialization) this.ovfs.getObjects()[this.iObject],
					getCurrentSort(), this.databaseManager);
		default:
			throw new UnsupportedOperationException(
					"Cannot reconstruct objects of type " + this.currentType);
		}

	}

	PropertySignature getCurrentPropertySignature() {
		if (this.currentProperty == null) {
			this.currentProperty = this.databaseManager
					.fetchPropertySignature(this.ovfs.getProperties()[this.i]);
		}
		return this.currentProperty;
	}

	@Override
	public int size() {
		return this.ovfs.getTypes().length;
	}

	private Sort getCurrentSort() {
		return this.databaseManager.getSortSchema().getSort(
				getCurrentPropertySignature().getDomainId());
	}

}
