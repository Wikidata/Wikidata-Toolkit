package org.wikidata.wdtk.storage.wdtkbindings;

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

import org.wikidata.wdtk.storage.db.DatabaseManager;

public class WdtkDatabaseManager extends DatabaseManager {

	public WdtkDatabaseManager(String dbName) {
		super(dbName);

		this.sortSchema.declareSort(WdtkSorts.SORT_ENTITY, true);
		this.sortSchema.declareSort(WdtkSorts.SORT_SPECIAL_STRING, true);
		this.sortSchema.declareSort(WdtkSorts.SORT_MTV, false);
		this.sortSchema.declareSort(WdtkSorts.SORT_LABEL, false);
		this.sortSchema.declareSort(WdtkSorts.SORT_LABEL_STRING, false);
		this.sortSchema.declareSort(WdtkSorts.SORT_DESCRIPTION, false);
		this.sortSchema.declareSort(WdtkSorts.SORT_ALIAS, false);
		// this.sortSchema.declareSort(WdtkSorts.SORT_TERMS, true);
		this.sortSchema.declareSort(WdtkSorts.SORT_REFERENCE, true);
		this.sortSchema.declareSort(WdtkSorts.SORT_TIME_VALUE, true);
		this.sortSchema.declareSort(WdtkSorts.SORT_GLOBE_COORDINATES_VALUE,
				true);
		this.sortSchema.declareSort(WdtkSorts.SORT_SITE_LINK, false);
	}

}
