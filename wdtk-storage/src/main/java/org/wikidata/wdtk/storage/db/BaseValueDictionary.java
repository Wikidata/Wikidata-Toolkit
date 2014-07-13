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

import org.apache.commons.lang3.Validate;
import org.mapdb.Atomic;
import org.wikidata.wdtk.storage.datamodel.Sort;

public abstract class BaseValueDictionary implements ValueDictionary {

	protected final Sort sort;
	protected final Atomic.Long nextId;
	protected final DatabaseManager databaseManager;

	public BaseValueDictionary(Sort sort, DatabaseManager databaseManager) {
		Validate.notNull(sort, "sort cannot be null");
		Validate.notNull(databaseManager, "database manager cannot be null");

		this.sort = sort;
		this.databaseManager = databaseManager;

		nextId = databaseManager.getDb().getAtomicLong(
				"sort-inc-" + sort.getName());
	}

	@Override
	public Sort getSort() {
		return this.sort;
	}

}
