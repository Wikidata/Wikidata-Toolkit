package org.wikidata.wdtk.dumpfiles.constraint.model;

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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateConstant;

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
	 * Constructs a set of pairs for all the items, for a given property.
	 * 
	 * @param property
	 *            property
	 */
	public PropertyValues(PropertyIdValue property) {
		this(property, Collections.<ItemIdValue> emptyList(), true);
	}

	/**
	 * Constructs a set of pairs for some items. When an empty list of items is
	 * used, the set of pairs property-value is empty. This is different from
	 * the case which only the property is used to construct the set of pairs
	 * property-value.
	 * 
	 * @param property
	 *            property
	 * @param list
	 *            list of items
	 */
	public PropertyValues(PropertyIdValue property, List<ItemIdValue> items) {
		this(property, items, false);
	}

	/**
	 * Constructs a set of pairs for some items.
	 * 
	 * @param other
	 *            other set of pairs property-value
	 */
	public PropertyValues(PropertyValues other) {
		this(other.getProperty(), other.getItems(), other.hasAllValues());
	}

	/**
	 * Constructs a set of pairs for some items.
	 * 
	 * @param property
	 *            property
	 * @param list
	 *            list of items
	 * @param hasAllValues
	 *            <code>true</code> if the property spans all values
	 */
	private PropertyValues(PropertyIdValue property, List<ItemIdValue> items,
			boolean hasAllValues) {
		Validate.notNull(property, "Property cannot be null.");
		Validate.notNull(items, "List of items cannot be null.");
		this.property = property;
		this.items.addAll(items);
		this.hasAllValues = hasAllValues;
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(TemplateConstant.OPENING_BRACES);
		sb.append("P");
		sb.append(TemplateConstant.VERTICAL_BAR);
		String pNumber = this.property.getId().substring(1);
		sb.append(pNumber);
		sb.append(TemplateConstant.CLOSING_BRACES);
		if (!this.hasAllValues) {
			sb.append(TemplateConstant.COLON);
			sb.append(TemplateConstant.SPACE);
			Iterator<ItemIdValue> it = this.items.iterator();
			while (it.hasNext()) {
				sb.append(TemplateConstant.OPENING_BRACES);
				sb.append("Q");
				sb.append(TemplateConstant.VERTICAL_BAR);
				String qNumber = it.next().getId().substring(1);
				sb.append(qNumber);
				sb.append(TemplateConstant.CLOSING_BRACES);
				if (it.hasNext()) {
					sb.append(TemplateConstant.COMMA);
					sb.append(TemplateConstant.SPACE);
				}
			}
		}
		return sb.toString();
	}

}
