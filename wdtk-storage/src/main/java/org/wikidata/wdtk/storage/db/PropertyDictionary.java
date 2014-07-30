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
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.mapdb.Atomic;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.Bind;
import org.mapdb.Serializer;

public class PropertyDictionary implements Dictionary<PropertySignature> {

	protected final Atomic.Integer nextId;
	protected final DatabaseManager databaseManager;
	final Bind.MapWithModificationListener<Integer, PropertySignature> values;
	final Map<PropertySignature, Integer> ids;

	public PropertyDictionary(DatabaseManager databaseManager) {
		Validate.notNull(databaseManager, "database manager cannot be null");

		this.databaseManager = databaseManager;

		nextId = databaseManager.getDb().getAtomicInteger("properties-inc");

		Serializer<PropertySignature> serializer = new PropertySignatureSerializer();

		this.values = databaseManager.getDb()
				.createTreeMap("properties-values")
				.keySerializer(BTreeKeySerializer.ZERO_OR_POSITIVE_INT)
				.valueSerializer(serializer).makeOrGet();
		this.ids = databaseManager.getDb().createHashMap("properties-ids")
				.keySerializer(serializer).makeOrGet();
		Bind.mapInverse(this.values, this.ids);
	}

	@Override
	public Iterator<PropertySignature> iterator() {
		return this.values.values().iterator();
	}

	@Override
	public PropertySignature getValue(int id) {
		return this.values.get(id);
	}

	@Override
	public int getId(PropertySignature value) {
		Integer result = this.ids.get(value);
		return (result == null) ? -1 : result;
	}

	@Override
	public int getOrCreateId(PropertySignature value) {
		int id = getId(value);
		if (id == -1) {
			id = this.nextId.incrementAndGet();
			this.values.put(id, value);
		}
		return id;
	}

}
