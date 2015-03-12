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

	public enum TypeOfConstraint {
		UNDEFINED, ITEM, QUANTITY, STRING
	}

	final PropertyIdValue constrainedProperty;
	final List<ItemIdValue> itemValues = new ArrayList<ItemIdValue>();
	final List<Integer> quantityValues = new ArrayList<Integer>();
	final List<String> stringValues = new ArrayList<String>();
	final TypeOfConstraint typeOfConstraint;

	/**
	 * Constructs a new {@link ConstraintOneOf}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param values
	 *            possible values that the constrained property can have
	 */
	public ConstraintOneOf(PropertyIdValue constrainedProperty,
			List<ItemIdValue> values) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		Validate.notNull(values, "List of values cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.itemValues.addAll(values);
		this.typeOfConstraint = TypeOfConstraint.ITEM;
	}

	/**
	 * Constructs a new {@link ConstraintOneOf}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param values
	 *            possible values that the constrained property can have
	 * @param x
	 *            dummy integer value to use this constructor (this value is
	 *            ignored)
	 */
	public ConstraintOneOf(PropertyIdValue constrainedProperty,
			List<Integer> values, int x) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		Validate.notNull(values, "List of values cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.quantityValues.addAll(values);
		this.typeOfConstraint = TypeOfConstraint.QUANTITY;
	}

	/**
	 * Constructs a new {@link ConstraintOneOf}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param values
	 *            possible values that the constrained property can have
	 * @param x
	 *            dummy String value to use this constructor (this value is
	 *            ignored)
	 */
	public ConstraintOneOf(PropertyIdValue constrainedProperty,
			List<String> values, String x) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		Validate.notNull(values, "List of values cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.stringValues.addAll(values);
		this.typeOfConstraint = TypeOfConstraint.STRING;
	}

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	/**
	 * Returns the item values that the constrained property can have.
	 * 
	 * @return the item values that the constrained property can have
	 */
	public List<ItemIdValue> getItemValues() {
		return Collections.unmodifiableList(this.itemValues);
	}

	/**
	 * Returns the quantity values that the constrained property can have.
	 * 
	 * @return the quantity values that the constrained property can have
	 */
	public List<Integer> getQuantityValues() {
		return Collections.unmodifiableList(this.quantityValues);
	}

	/**
	 * Returns the string values that the constrained property can have.
	 * 
	 * @return the string values that the constrained property can have
	 */
	public List<String> getStringValues() {
		return Collections.unmodifiableList(this.stringValues);
	}

	/**
	 * Return the type of constraint.
	 * 
	 * @return the type of constraint
	 */
	public TypeOfConstraint getTypeOfConstraint() {
		return this.typeOfConstraint;
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
		return this.constrainedProperty.equals(other.constrainedProperty)
				&& this.typeOfConstraint.equals(other.typeOfConstraint)
				&& this.itemValues.equals(other.itemValues)
				&& this.quantityValues.equals(other.quantityValues)
				&& this.stringValues.equals(other.stringValues);
	}

	@Override
	public int hashCode() {
		return (this.constrainedProperty.hashCode() + (0x1F * this.typeOfConstraint
				.hashCode() + (0x1F * (this.itemValues.hashCode() + (0x1F * this.quantityValues
				.hashCode() + (0x1F * this.stringValues.hashCode()))))));
	}

	static String toString(List<?> values) {
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = values.iterator();
		while (it.hasNext()) {
			String str = it.next().toString();
			sb.append(str);
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
		if (this.typeOfConstraint.equals(TypeOfConstraint.ITEM)) {
			sb.append(ConstraintItem.toString(this.itemValues));
		} else if (this.typeOfConstraint.equals(TypeOfConstraint.QUANTITY)) {
			sb.append(toString(this.quantityValues));
		} else if (this.typeOfConstraint.equals(TypeOfConstraint.STRING)) {
			sb.append(toString(this.stringValues));
		}
		sb.append(TemplateConstant.CLOSING_BRACES);
		return sb.toString();
	}

	@Override
	public String toString() {
		return this.constrainedProperty.getId() + " " + getTemplate();
	}

}
