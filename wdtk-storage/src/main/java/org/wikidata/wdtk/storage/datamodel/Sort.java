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

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.Validate;

/**
 * Representation of one sort. Sorts are like datatypes for Value objects.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class Sort implements Serializable {

	private static final long serialVersionUID = -4309351376613799056L;

	final String name;
	final SortType sortType;
	final List<PropertyRange> propertyRanges;

	public Sort(String name, SortType sortType,
			List<PropertyRange> propertyRanges) {
		Validate.notNull(name, "name cannot be null");
		Validate.notNull(sortType, "sort type cannot be null");
		if (sortType == SortType.RECORD) {
			Validate.notNull(propertyRanges,
					"property-range list required for record sorts");
			if (propertyRanges.size() == 0) {
				throw new IllegalArgumentException(
						"property-range list must contain at least one entry");
			}
		} else if (propertyRanges != null) {
			throw new IllegalArgumentException(
					"property-range list must be null for non-record types");
		}

		this.name = name;
		this.sortType = sortType;
		this.propertyRanges = propertyRanges;
	}

	public String getName() {
		return this.name;
	}

	public SortType getType() {
		return this.sortType;
	}

	public List<PropertyRange> getPropertyRanges() {
		return this.propertyRanges;
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
		result = prime * result + name.hashCode();
		result = prime * result
				+ ((propertyRanges == null) ? 0 : propertyRanges.hashCode());
		result = prime * result + sortType.hashCode();
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
		if (!(obj instanceof Sort)) {
			return false;
		}
		Sort other = (Sort) obj;
		if (!name.equals(other.name)) {
			return false;
		}
		if (propertyRanges == null) {
			if (other.propertyRanges != null) {
				return false;
			}
		} else if (!propertyRanges.equals(other.propertyRanges)) {
			return false;
		}
		if (sortType != other.sortType) {
			return false;
		}
		return true;
	}

}
