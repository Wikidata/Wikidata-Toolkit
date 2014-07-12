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
import java.util.concurrent.ConcurrentNavigableMap;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.SortSchema;

public class DbSortSchema extends SortSchema {

	final DatabaseManager databaseManager;

	public static DbSortSchema create(DatabaseManager databaseManager) {
		Validate.notNull(databaseManager, "database manager cannot be null");

		ConcurrentNavigableMap<String, Sort> sorts = databaseManager.getDb()
				.createTreeMap("sorts").valueSerializer(new SortSerializer())
				.makeOrGet();
		// DEBUG:
		// for (Sort sort : sorts.values()) {
		// System.out.println("Found sort: " + sort.getName());
		// }
		return new DbSortSchema(sorts, databaseManager);
	}

	protected DbSortSchema(Map<String, Sort> sorts,
			DatabaseManager databaseManager) {
		super(sorts);
		this.databaseManager = databaseManager;
	}

}
