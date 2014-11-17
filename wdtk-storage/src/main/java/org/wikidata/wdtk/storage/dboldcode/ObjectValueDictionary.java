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
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.db.BaseValueDictionary;
import org.wikidata.wdtk.storage.db.DatabaseManager;

public class ObjectValueDictionary extends
		BaseValueDictionary<ObjectValue, ObjectValueForSerialization> {

	public ObjectValueDictionary(Sort sort, DatabaseManager databaseManager) {
		super(sort, databaseManager);
	}

	@Override
	protected ObjectValueForSerialization getInnerObject(ObjectValue outer) {
		return SerializationConverter.makeObjectValueForSerialization(outer,
				this.databaseManager);
	}

	@Override
	public ObjectValue getOuterObject(ObjectValueForSerialization inner) {
		return new LazyObjectValue(inner, this.sort, this.databaseManager);
	}

	@Override
	protected MapWithModificationListener<Integer, ObjectValueForSerialization> initValues(
			String name) {
		return databaseManager.getDb().createTreeMap(name)
				.keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_INT)
				.valueSerializer(new ObjectValueSerializer()).makeOrGet();
	}

	@Override
	protected Map<ObjectValueForSerialization, Integer> initIds(String name) {
		return databaseManager.getDb().createHashMap(name)
				.keySerializer(new ObjectValueSerializer()).makeOrGet();
	}
}
