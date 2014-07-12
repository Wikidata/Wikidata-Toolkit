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
import org.mapdb.Bind;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.Sort;

public class RecordDictionary implements Dictionary<ObjectValue> {

	final Sort sort;
	final Atomic.Long nextId;
	final Bind.MapWithModificationListener<Long, ObjectValue> values;
	final Map<ObjectValue, Long> ids;

	public RecordDictionary(Sort sort, DatabaseManager databaseManager) {
		Validate.notNull(sort, "sort cannot be null");
		Validate.notNull(databaseManager, "database manager cannot be null");

		this.sort = sort;

		nextId = databaseManager.getDb().getAtomicLong(
				"sort-inc-" + sort.getName());

		this.values = databaseManager.getDb()
				.createHashMap("sort-values-" + sort.getName())
				.valueSerializer(new RecordSerializer(sort)).makeOrGet();
		this.ids = databaseManager.getDb()
				.createHashMap("sort-ids-" + sort.getName())
				.keySerializer(new RecordSerializer(sort)).makeOrGet();
		Bind.mapInverse(this.values, this.ids);
	}

	@Override
	public Iterator<ObjectValue> iterator() {
		return this.values.values().iterator();
	}

	@Override
	public Sort getSort() {
		return this.sort;
	}

	@Override
	public ObjectValue getValue(long id) {
		return this.values.get(id);
	}

	@Override
	public long getId(ObjectValue value) {
		Long result = this.ids.get(value);
		return (result == null) ? -1L : result;
	}

	@Override
	public long getOrCreateId(ObjectValue value) {
		long id = getId(value);
		if (id == -1L) {
			id = this.nextId.incrementAndGet();
			this.values.put(id, value);
		}
		return id;
	}

}
