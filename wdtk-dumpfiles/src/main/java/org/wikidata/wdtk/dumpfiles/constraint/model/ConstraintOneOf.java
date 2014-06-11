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
 * This models a property constraint that says that a property must have one of
 * the specified values.
 * <p>
 * For example, <i>voice type (P412)</i> can have the following values: <i>tenor
 * (Q27914)</i>, <i>soprano (Q30903)</i>, <i>baritone (Q31687)</i>, <i>contralto
 * (Q37137)</i>, <i>mezzo-soprano (Q186506)</i>, <i>bass (Q27911)</i>.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintOneOf implements Constraint {

	final PropertyIdValue constrainedProperty;
	final List<ItemIdValue> itemValues = new ArrayList<ItemIdValue>();
	final List<Integer> quantityValues = new ArrayList<Integer>();
	final boolean hasItems;

	/**
	 * Constructs a new {@link ConstraintOneOf}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param values
	 *            possible values that a property can have
	 */
	public ConstraintOneOf(PropertyIdValue constrainedProperty,
			List<ItemIdValue> values) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		Validate.notNull(values, "List of values cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.itemValues.addAll(values);
		this.hasItems = true;
	}

	public ConstraintOneOf(List<Integer> values,
			PropertyIdValue constrainedProperty) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		Validate.notNull(values, "List of values cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.quantityValues.addAll(values);
		this.hasItems = false;
	}

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	/**
	 * Returns the item values that a property can have.
	 * 
	 * @return the item values that a property can have
	 */
	public List<ItemIdValue> getItemValues() {
		return Collections.unmodifiableList(this.itemValues);
	}

	/**
	 * Returns the quantity values that a property can have.
	 * 
	 * @return the quantity values that a property can have
	 */
	public List<Integer> getQuantityValues() {
		return Collections.unmodifiableList(this.quantityValues);
	}

	/**
	 * Tells whether the values are only items.
	 * 
	 * @return <code>true</code> if and only if the values are only items
	 */
	public boolean hasItems() {
		return this.hasItems;
	}

	@Override
	public <T> T accept(ConstraintVisitor<T> visitor) {
		Validate.notNull(visitor, "Visitor cannot be null.");
		return visitor.visit(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ConstraintOneOf)) {
			return false;
		}
		ConstraintOneOf other = (ConstraintOneOf) obj;
		return (this.constrainedProperty.equals(other.constrainedProperty)
				&& (this.hasItems == other.hasItems)
				&& this.itemValues.equals(other.itemValues) && this.quantityValues
					.equals(other.quantityValues));
	}

	@Override
	public int hashCode() {
		return (this.constrainedProperty.hashCode() + (0x1F * (this.itemValues
				.hashCode() + (0x1F * this.quantityValues.hashCode()))));
	}

	static String toString(List<Integer> values) {
		StringBuilder sb = new StringBuilder();
		Iterator<Integer> it = values.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	@Override
	public String getTemplate() {
		StringBuilder sb = new StringBuilder();
		sb.append(TemplateConstant.OPENING_BRACES);
		sb.append("Constraint:One of");
		sb.append(TemplateConstant.VERTICAL_BAR);
		sb.append("values");
		sb.append(TemplateConstant.EQUALS_SIGN);
		if (this.hasItems) {
			sb.append(ConstraintItem.toString(this.itemValues));
		} else {
			sb.append(toString(this.quantityValues));
		}
		sb.append(TemplateConstant.CLOSING_BRACES);
		return sb.toString();
	}

	@Override
	public String toString() {
		return this.constrainedProperty.getId() + " " + getTemplate();
	}

}
