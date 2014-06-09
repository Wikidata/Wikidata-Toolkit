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

/**
 * This models a property constraint that defines the inverse property of
 * another one.
 * <p>
 * For example, <i>preceded by (P155)</i> and <i>succeeded by (P156)</i> are
 * mutually one the inverse of the other.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintInverse implements Constraint {

	final PropertyIdValue constrainedProperty;
	final PropertyIdValue property;

	/**
	 * Constructs a new {@link ConstraintInverse}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param property
	 *            the inverse property of the given property
	 */
	public ConstraintInverse(PropertyIdValue constrainedProperty,
			PropertyIdValue property) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		Validate.notNull(property, "Property cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.property = property;
	}

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	/**
	 * Returns the inverse property.
	 * 
	 * @return the inverse property
	 */
	public PropertyIdValue getProperty() {
		return this.property;
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
		if (!(obj instanceof ConstraintInverse)) {
			return false;
		}
		ConstraintInverse other = (ConstraintInverse) obj;
		return (this.constrainedProperty.equals(other.constrainedProperty) && this.property
				.equals(other.property));
	}

	@Override
	public int hashCode() {
		return (this.constrainedProperty.hashCode() + (0x1F * this.property
				.hashCode()));
	}

}
