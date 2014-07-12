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

import org.apache.commons.lang3.Validate;

public class PropertyRange {
	final String property;
	final String rangeSort;

	public PropertyRange(String property, String rangeSort) {
		Validate.notNull(property, "property cannot be null");
		Validate.notNull(rangeSort, "range sort cannot be null");

		this.property = property;
		this.rangeSort = rangeSort;
	}

	public String getProperty() {
		return this.property;
	}

	public String getRange() {
		return this.rangeSort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + property.hashCode();
		result = prime * result + rangeSort.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PropertyRange)) {
			return false;
		}
		PropertyRange other = (PropertyRange) obj;
		return property.equals(other.property)
				&& rangeSort.equals(other.rangeSort);
	}
}