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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.mapdb.Atomic;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.SortSchema;

public class DbSortSchema implements SortSchema {

	final DatabaseManager databaseManager;
	protected final Map<String, DbSortData> dbSorts;
	protected final Map<Integer, DbSortData> sortsById;
	protected final Atomic.Integer nextId;

	protected final Map<String, DbSortData> dbSortsCache;

	protected DbSortSchema(DatabaseManager databaseManager) {
		Validate.notNull(databaseManager, "database manager cannot be null");

		nextId = databaseManager.getDb().getAtomicInteger("sorts-inc");

		this.dbSorts = databaseManager.getDb().createHashMap("sorts")
				.valueSerializer(new DbSortDataSerializer()).makeOrGet();
		this.sortsById = new HashMap<>();
		this.dbSortsCache = new HashMap<>();

		this.databaseManager = databaseManager;

		for (DbSortData dbSortData : dbSorts.values()) {
			this.sortsById.put(dbSortData.id, dbSortData);
			this.dbSortsCache.put(dbSortData.sort.getName(), dbSortData);
			if (dbSortData.useDictionary) {
				this.databaseManager.initializeDictionary(dbSortData.sort,
						dbSortData.id);
			}
			// DEBUG:
			// System.out.println("Found sort: " + sort.getName());
		}

		declareSort(Sort.SORT_STRING, false);
		declareSort(Sort.SORT_LONG, false);
	}

	public void declareSort(Sort sort, boolean useDictionary) {
		if (this.dbSorts.containsKey(sort.getName())) {
			DbSortData dbSort = this.dbSorts.get(sort.getName());
			if (dbSort.useDictionary != useDictionary
					|| !sort.equals(dbSort.sort)) {
				throw new IllegalArgumentException("Sort \"" + sort.getName()
						+ "\" already declared. Cannot redeclare sorts.");
			} // else: no action; sort already declared
		} else {
			int id = this.nextId.incrementAndGet();
			DbSortData dbSortData = new DbSortData(sort, id, useDictionary);

			this.dbSorts.put(sort.getName(), dbSortData);
			this.sortsById.put(id, dbSortData);

			if (dbSortData.useDictionary) {
				this.databaseManager.initializeDictionary(sort, id);
			}

			// TODO It would be nice to register record properties, but this can
			// only be done after all sorts are registered; i.e., not during
			// sort declaration -- not clear where to do this
			// // Make Record properties known; they won't be introduced for
			// // serialization but the DB should have them anyway:
			// if (sort.getType() == SortType.RECORD) {
			// for (PropertyRange pr : sort.getPropertyRanges()) {
			// this.databaseManager.getOrCreatePropertyId(
			// pr.getProperty(), sort.getName(), pr.getRange());
			// }
			// }
		}
	}

	@Override
	public Sort getSort(String name) {
		return getDbSortsData(name).sort;
	}

	public Sort getSort(int id) {
		return this.sortsById.get(id).sort;
	}

	/**
	 * Returns the id of the sort of the given name. If no such sort is known,
	 * the method fails with an exception. To check if a sort is known, use
	 * {@link DbSortSchema#getSort(String)} instead.
	 * 
	 * @param name
	 *            the name of the sort
	 * @return the id of the sort
	 * @throws IllegalArgumentException
	 *             if no sort of this name is known
	 */
	public int getSortId(String name) {
		DbSortData dbSortData = getDbSortsData(name);

		if (dbSortData == null) {
			throw new IllegalArgumentException("Sort \"" + name
					+ "\" not known.");
		} else {
			return dbSortData.id;
		}
	}

	public boolean useDictionary(String name) {
		return getDbSortsData(name).useDictionary;
	}

	DbSortData getDbSortsData(String name) {
		DbSortData result = this.dbSortsCache.get(name);
		if (result == null) {
			result = this.dbSorts.get(name);
			this.dbSortsCache.put(name, result);
		}
		return result;
	}

}
