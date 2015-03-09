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

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.template.TemplateConstant;

/**
 * This models a property constraint that says that a property value ​​should be
 * different from the value of some other property for not more than a
 * predetermined amount. This template can be applied to properties of type
 * <i>Time</i> and <i>Number</i>. For the case of properties of type
 * <i>Time</i>, the values are given in years.
 * <p>
 * For example, the value of property <i>date of death (P570)</i> should be not
 * more than 150 years greater than the value of <i>date of birth (P569)</i>. In
 * this case, the constraint in property <i>date of death (P570)</i> should say
 * that, considering the base property <i>date of birth (P569)</i>, the
 * difference is at least 0 years and at most 150 years.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintDiffWithinRange implements Constraint {

	final PropertyIdValue constrainedProperty;
	final PropertyIdValue baseProperty;
	final String min;
	final String max;
	final boolean isTime;

	/**
	 * Constructs a new {@link ConstraintDiffWithinRange}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param baseProperty
	 *            base property
	 * @param min
	 *            minimum value of the interval
	 * @param max
	 *            maximum value of the interval
	 * @param isTime
	 *            <code>true</code> if the range is time, or <code>false</code>
	 *            is the range is of other type of quantities
	 */
	public ConstraintDiffWithinRange(PropertyIdValue constrainedProperty,
			PropertyIdValue baseProperty, String min, String max, boolean isTime) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		Validate.notNull(baseProperty, "Base property cannot be null.");
		Validate.notNull(min, "Min cannot be null.");
		Validate.notNull(max, "Max cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.baseProperty = baseProperty;
		this.min = min;
		this.max = max;
		this.isTime = isTime;
	}

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	/**
	 * Returns the base property.
	 * 
	 * @return the base property
	 */
	public PropertyIdValue getBaseProperty() {
		return this.baseProperty;
	}

	/**
	 * Tells whether this range is a range of quantities.
	 * 
	 * @return <code>true</code> if and only if this range is a range of
	 *         quantities
	 */
	public boolean isQuantity() {
		return !this.isTime;
	}

	/**
	 * Tells whether this range is a range of time.
	 * 
	 * @return <code>true</code> if and only if this range is a range of time
	 */
	public boolean isTime() {
		return this.isTime;
	}

	/**
	 * Returns the minimum value of the range.
	 * 
	 * @return the minimum value of the range
	 */
	public String getMin() {
		return this.min;
	}

	/**
	 * Returns the maximum value of the range.
	 * 
	 * @return the maximum value of the range
	 */
	public String getMax() {
		return this.max;
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
		if (!(obj instanceof ConstraintDiffWithinRange)) {
			return false;
		}
		ConstraintDiffWithinRange other = (ConstraintDiffWithinRange) obj;
		return this.constrainedProperty.equals(other.constrainedProperty)
				&& this.baseProperty.equals(other.baseProperty)
				&& (this.isTime == other.isTime) && this.min.equals(other.min)
				&& this.max.equals(other.max);
	}

	@Override
	public int hashCode() {
		return this.constrainedProperty.hashCode()
				+ (0x1F * (this.baseProperty.hashCode() + (0x1F * (this.min
						.hashCode() + (0x1F * this.max.hashCode())))));
	}

	@Override
	public String getTemplate() {
		StringBuilder sb = new StringBuilder();
		sb.append(TemplateConstant.OPENING_BRACES);
		sb.append("Constraint:Diff within range");
		sb.append(TemplateConstant.VERTICAL_BAR);
		sb.append("base_property");
		sb.append(TemplateConstant.EQUALS_SIGN);
		sb.append(this.baseProperty.getId());
		sb.append(TemplateConstant.VERTICAL_BAR);
		sb.append("min");
		sb.append(TemplateConstant.EQUALS_SIGN);
		sb.append(this.min);
		sb.append(TemplateConstant.VERTICAL_BAR);
		sb.append("max");
		sb.append(TemplateConstant.EQUALS_SIGN);
		sb.append(this.max);
		sb.append(TemplateConstant.CLOSING_BRACES);
		return sb.toString();
	}

	@Override
	public String toString() {
		return this.constrainedProperty.getId() + " " + getTemplate();
	}

}
