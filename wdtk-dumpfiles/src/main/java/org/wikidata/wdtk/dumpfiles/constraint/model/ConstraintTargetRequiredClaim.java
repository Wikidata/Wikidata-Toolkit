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
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * This models a property constraint that says that for every item that has a
 * property with some other item, the latter has also property <i>r</i>,
 * optionally with value <i>q</i>.
 * <p>
 * For example, property <i>sister (P9)</i> has a constraint that the item has
 * to have <i>sex or gender (P21)</i> and that value must be <i>female
 * (Q6581072)</i>.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintTargetRequiredClaim implements Constraint {

	final PropertyIdValue constrainedProperty;
	final PropertyIdValue property;
	final ItemIdValue item;

	/**
	 * Constructs a new {@link ConstraintTargetRequiredClaim}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param property
	 *            another property
	 */
	public ConstraintTargetRequiredClaim(PropertyIdValue constrainedProperty,
			PropertyIdValue property) {
		Validate.notNull(property, "Property cannot be null.");
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.property = property;
		this.item = null;
	}

	/**
	 * Constructs a new ConstraintTargetRequiredClaim.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param property
	 *            another property
	 * @param item
	 *            an item
	 */
	public ConstraintTargetRequiredClaim(PropertyIdValue constrainedProperty,
			PropertyIdValue property, ItemIdValue item) {
		Validate.notNull(property, "Property cannot be null.");
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.property = property;
		this.item = item;
	}

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	public PropertyIdValue getProperty() {
		return this.property;
	}

	public ItemIdValue getItem() {
		return this.item;
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
		if (!(obj instanceof ConstraintTargetRequiredClaim)) {
			return false;
		}
		ConstraintTargetRequiredClaim other = (ConstraintTargetRequiredClaim) obj;
		boolean ret = this.constrainedProperty
				.equals(other.constrainedProperty)
				&& this.property.equals(other.property)
				&& equals(this.item, other.item);
		return ret;
	}

	private boolean equals(Object obj0, Object obj1) {
		if (obj0 == null) {
			return (obj1 == null);
		}
		return obj0.equals(obj1);
	}

	@Override
	public int hashCode() {
		return (this.constrainedProperty.hashCode() + (0x1F * this.property
				.hashCode()));
	}

	@Override
	public String getTemplate() {
		StringBuilder sb = new StringBuilder();
		sb.append("{{");
		sb.append("Constraint:Target required claim");
		sb.append("|");
		sb.append("property");
		sb.append("=");
		sb.append(this.property.getId());
		if (this.item != null) {
			sb.append("|");
			sb.append("item");
			sb.append("=");
			sb.append(this.item.getId());
		}
		sb.append("}}");
		return sb.toString();
	}

	@Override
	public String toString() {
		return this.constrainedProperty.getId() + " " + getTemplate();
	}

}
