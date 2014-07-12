package org.wikidata.wdtk.storage.datamodel;

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
import java.util.List;
import java.util.Map;

public class SortSchema {

	public static final String SORTNAME_STRING = "string";
	public static final String SORTNAME_LONG = "long";

	public final Sort sortString;
	public final Sort sortLong;

	protected final Map<String, Sort> sorts;

	public SortSchema() {
		this(new HashMap<String, Sort>());
	}

	public SortSchema(Map<String, Sort> sorts) {
		this.sorts = sorts;
		this.sortString = declareSort(SORTNAME_STRING, SortType.STRING, null);
		this.sortLong = declareSort(SORTNAME_LONG, SortType.LONG, null);
	}

	public Sort declareSort(String name, SortType sortType,
			List<PropertyRange> propertyRanges) {
		Sort sort = new Sort(name, sortType, propertyRanges);
		if (this.sorts.containsKey(name)) {
			if (!sort.equals(this.sorts.get(name))) {
				System.out.print("New sort: " + sort.getName() + " T"
						+ sort.getType());
				if (sort.getPropertyRanges() != null) {
					for (PropertyRange pr : sort.getPropertyRanges()) {
						System.out.print("(" + pr.getProperty() + ":"
								+ pr.getRange() + ")");
					}
				}
				System.out.println();
				System.out.print("Old sort: " + this.sorts.get(name).getName()
						+ " T" + this.sorts.get(name).getType());
				if (this.sorts.get(name).getPropertyRanges() != null) {
					for (PropertyRange pr : this.sorts.get(name)
							.getPropertyRanges()) {
						System.out.print("(" + pr.getProperty() + ":"
								+ pr.getRange() + ")");
					}
				}
				System.out.println();
				throw new IllegalArgumentException("Sort \"" + sort.getName()
						+ "\" already declared. Cannot redeclare sorts.");
			} // else: no action; sort already declared
		} else {
			this.sorts.put(name, sort);
		}
		return sort;
	}

	public Sort getSort(String name) {
		return this.sorts.get(name);
	}

	public Sort getStringSort() {
		return this.sortString;
	}

	public Sort getLongSort() {
		return this.sortLong;
	}
}
