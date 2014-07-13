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

import org.apache.commons.lang3.Validate;
import org.mapdb.Atomic;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.SortSchema;

public class DbSortSchema implements SortSchema {

	final DatabaseManager databaseManager;
	protected final Map<String, Sort> sorts;
	protected final Map<String, Integer> sortIds;
	protected final Atomic.Integer nextId;

	protected DbSortSchema(DatabaseManager databaseManager) {
		Validate.notNull(databaseManager, "database manager cannot be null");

		nextId = databaseManager.getDb().getAtomicInteger("sorts-inc");

		this.sorts = databaseManager.getDb().createHashMap("sorts")
				.valueSerializer(new SortSerializer()).makeOrGet();
		this.sortIds = databaseManager.getDb().createHashMap("sorts-ids")
				.makeOrGet();

		this.databaseManager = databaseManager;

		for (Sort sort : sorts.values()) {
			this.databaseManager.initializeDictionary(sort);
			// DEBUG:
			// System.out.println("Found sort: " + sort.getName());
		}
		this.declareSort(Sort.SORT_STRING);
		this.declareSort(Sort.SORT_LONG);
	}

	@Override
	public Sort declareSort(Sort sort) {
		if (this.sorts.containsKey(sort.getName())) {
			if (!sort.equals(this.sorts.get(sort.getName()))) {
				throw new IllegalArgumentException("Sort \"" + sort.getName()
						+ "\" already declared. Cannot redeclare sorts.");
			} // else: no action; sort already declared
		} else {
			int id = this.nextId.incrementAndGet();
			this.sorts.put(sort.getName(), sort);
			this.sortIds.put(sort.getName(), id);
			this.databaseManager.initializeDictionary(sort);
		}
		return sort;
	}

	@Override
	public Sort getSort(String name) {
		return this.sorts.get(name);
	}

	public int getSortId(String name) {
		Integer result = this.sortIds.get(name);
		return (result == null) ? -1 : result;
	}

}
