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

import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.SortSchema;

public class WdtkAdaptorHelper {

	final SortSchema sortSchema;
	final ValueAdaptor valueAdaptor;
	final SnakAdaptor snakAdaptor;

	final Sort entitySort;
	final Sort referenceSort;
	final Sort mtvSort;

	public WdtkAdaptorHelper(SortSchema sortSchema) {
		this.sortSchema = sortSchema;
		this.entitySort = sortSchema
				.getSort(WdtkDatabaseManager.SORTNAME_ENTITY);
		this.referenceSort = sortSchema
				.getSort(WdtkDatabaseManager.SORTNAME_REFERENCE);
		this.mtvSort = sortSchema.getSort(WdtkDatabaseManager.SORTNAME_MTV);
		this.valueAdaptor = new ValueAdaptor(this);
		this.snakAdaptor = new SnakAdaptor(this);
	}

	public SortSchema getSortSchema() {
		return this.sortSchema;
	}

	public ValueAdaptor getValueAdaptor() {
		return this.valueAdaptor;
	}

	public SnakAdaptor getSnakAdaptor() {
		return this.snakAdaptor;
	}

	public Sort getEntitySort() {
		return this.entitySort;
	}

	public Sort getReferenceSort() {
		return this.referenceSort;
	}

	public Sort getMtvSort() {
		return this.mtvSort;
	}

	public Sort getStringSort() {
		return this.sortSchema.getStringSort();
	}

}
