package org.wikidata.wdtk.dumpfiles.constraint;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * An object of this class represents a set of pairs property-value, for a fixed
 * property. It can be constructed to span all possible values, or to have only
 * some specific values.
 * 
 * @author Julian Mendez
 * 
 */
public class PropertyValues {

	final PropertyIdValue property;
	final boolean hasAllValues;
	final List<ItemIdValue> items = new ArrayList<ItemIdValue>();

	/**
	 * Constructs a set of pairs for all the items.
	 * 
	 * @param property
	 *            property
	 */
	public PropertyValues(PropertyIdValue property) {
		Validate.notNull(property, "Property cannot be null.");
		this.property = property;
		this.hasAllValues = true;
	}

	/**
	 * Constructs a set of pairs for some items.
	 * 
	 * @param property
	 *            property
	 * @param list
	 *            of items
	 */
	public PropertyValues(PropertyIdValue property, List<ItemIdValue> items) {
		Validate.notNull(property, "Property cannot be null.");
		Validate.notNull(items, "List of items cannot be null.");
		this.property = property;
		this.items.addAll(items);
		this.hasAllValues = false;
	}

	/**
	 * Returns the property.
	 * 
	 * @return the property
	 */
	public PropertyIdValue getProperty() {
		return this.property;
	}

	/**
	 * Tells whether this objects has been constructed to include all values.
	 * 
	 * @return <code>true</code> if and only if this objects has been
	 *         constructed to include all values
	 */
	public boolean hasAllValues() {
		return this.hasAllValues;

	}

	/**
	 * Returns the items.
	 * 
	 * @return the items
	 */
	public List<ItemIdValue> getItems() {
		return this.items;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof PropertyValues)) {
			return false;
		}
		PropertyValues other = (PropertyValues) obj;
		return (this.property.equals(other.property)
				&& (this.hasAllValues == other.hasAllValues) && this.items
					.equals(other.items));
	}

	@Override
	public int hashCode() {
		return (this.property.hashCode() + (0x1F * this.items.hashCode()));
	}

}
