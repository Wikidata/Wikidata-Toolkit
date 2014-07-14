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

import java.util.Map;

import org.mapdb.Bind.MapWithModificationListener;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.SortType;
import org.wikidata.wdtk.storage.datamodel.StringValue;

public class ObjectValueDictionary extends
		BaseValueDictionary<ObjectValue, ObjectValueForSerialization> {

	public ObjectValueDictionary(Sort sort, DatabaseManager databaseManager) {
		super(sort, databaseManager);
	}

	@Override
	protected ObjectValueForSerialization getInnerObject(ObjectValue outer) {
		int length = outer.size();
		byte[] types = new byte[length];
		long[] properties = new long[length];
		Object[] values = new Object[length];

		int refCount = 0;
		int stringCount = 0;

		int i = 0;
		for (PropertyValuePair pvp : outer) {
			properties[i] = this.databaseManager.getOrCreatePropertyId(
					pvp.getProperty(), outer.getSort().getName(), pvp
							.getValue().getSort().getName());

			if (pvp.getValue().getSort().getType() == SortType.STRING) {
				types[i] = ObjectValueForSerialization.TYPE_STRING;
				values[i] = ((StringValue) pvp.getValue()).getString();
				stringCount++;
			} else if (pvp.getValue().getSort().getType() == SortType.OBJECT
					|| pvp.getValue().getSort().getType() == SortType.RECORD) {
				types[i] = ObjectValueForSerialization.TYPE_REF;
				values[i] = Long.valueOf(this.databaseManager
						.getOrCreateValueId(pvp.getValue()));
				refCount++;
			} else {
				throw new IllegalArgumentException(
						"Value sort not supported yet");
			}
			i++;
		}

		long[] refs = new long[refCount];
		int iRef = 0;
		String[] strings = new String[stringCount];
		int iString = 0;
		for (int j = 0; j < length; j++) {
			if (types[j] == ObjectValueForSerialization.TYPE_REF) {
				refs[iRef] = ((Long) values[j]).longValue();
				iRef++;
			} else if (types[j] == ObjectValueForSerialization.TYPE_STRING) {
				strings[iString] = ((String) values[j]);
				iString++;
			}
		}

		return new ObjectValueForSerialization(properties, types, refs, strings);
	}

	@Override
	public ObjectValue getOuterObject(ObjectValueForSerialization inner) {
		return new LazyObjectValue(inner, this);
	}

	@Override
	protected MapWithModificationListener<Long, ObjectValueForSerialization> initValues(
			String name) {
		return databaseManager.getDb().createHashMap(name)
				.valueSerializer(new ObjectValueSerializer()).makeOrGet();
	}

	@Override
	protected Map<ObjectValueForSerialization, Long> initIds(String name) {
		return databaseManager.getDb().createHashMap(name)
				.keySerializer(new ObjectValueSerializer()).makeOrGet();
	}
}
