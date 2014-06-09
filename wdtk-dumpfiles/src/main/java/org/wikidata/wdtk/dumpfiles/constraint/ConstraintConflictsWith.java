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
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * This models a property constraint that says that a property creates a
 * conflict with the provided properties and values. If no values are specified
 * for a given property, it is assumed that all values are in conflict.
 * <p>
 * For example, <i>ICD-10 (P494)</i>, the ICD catalogue codes for diseases -
 * Version 10, cannot be used together with <i>taxon name (P225)</i>, the
 * scientific name of a taxon (in biology).
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintConflictsWith implements Constraint {

	final PropertyIdValue constrainedProperty;
	final List<PropertyValues> list = new ArrayList<PropertyValues>();

	/**
	 * Constructs a new {@link ConstraintConflictWith}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param list
	 *            list of property values that are in conflict with the
	 *            constrained property
	 */
	public ConstraintConflictsWith(PropertyIdValue constrainedProperty,
			List<PropertyValues> list) {
		Validate.notNull(list, "List cannot be null.");
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.list.addAll(list);
	}

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	/**
	 * Returns a list of property values in conflict.
	 * 
	 * @return a list of property values in conflict
	 */
	public List<PropertyValues> getList() {
		return Collections.unmodifiableList(this.list);
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
		if (!(obj instanceof ConstraintConflictsWith)) {
			return false;
		}
		ConstraintConflictsWith other = (ConstraintConflictsWith) obj;
		return (this.constrainedProperty.equals(other.constrainedProperty) && this.list
				.equals(other.list));
	}

	@Override
	public int hashCode() {
		return (this.constrainedProperty.hashCode() + (0x1F * this.list
				.hashCode()));
	}

}
