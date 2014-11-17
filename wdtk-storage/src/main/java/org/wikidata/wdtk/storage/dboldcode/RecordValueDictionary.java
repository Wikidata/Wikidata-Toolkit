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

import java.util.Map;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.Bind.MapWithModificationListener;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyRange;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.db.BaseValueDictionary;
import org.wikidata.wdtk.storage.db.DatabaseManager;

public class RecordValueDictionary extends
		BaseValueDictionary<ObjectValue, RecordValueForSerialization> {

	int propertyCount = -1;
	int refCount;
	byte[] types;

	public RecordValueDictionary(Sort sort, DatabaseManager databaseManager) {
		super(sort, databaseManager);
	}

	@Override
	protected RecordValueForSerialization getInnerObject(ObjectValue outer) {
		return SerializationConverter.makeRecordValueForSerialization(outer,
				this.sort, this.types, this.refCount, this.databaseManager);
	}

	@Override
	public ObjectValue getOuterObject(RecordValueForSerialization inner) {
		return new LazyRecordValue(inner, this.sort, this.databaseManager);
	}

	@Override
	protected MapWithModificationListener<Integer, RecordValueForSerialization> initValues(
			String name) {
		return databaseManager.getDb().createTreeMap(name)
				.keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_INT)
				// .valuesOutsideNodesEnable() // much slower; maybe enable
				// selectively for large records based on sort?
				.valueSerializer(new RecordSerializer(getTypes())).makeOrGet();
	}

	@Override
	protected Map<RecordValueForSerialization, Integer> initIds(String name) {
		return databaseManager.getDb().createHashMap(name)
				.keySerializer(new RecordSerializer(getTypes())).makeOrGet();
	}

	private byte[] getTypes() {

		if (this.propertyCount < 0) {
			this.propertyCount = this.sort.getPropertyRanges().size();
			this.types = new byte[this.propertyCount];

			int i = 0;
			int rCount = 0;
			for (PropertyRange pr : this.sort.getPropertyRanges()) {
				this.types[i] = SerializationConverter.getFieldType(
						pr.getRange(), this.databaseManager);
				if (this.types[i] == SerializationConverter.TYPE_REF) {
					rCount++;
				}
				i++;
			}

			this.refCount = rCount;
		}

		return this.types;
	}

}
