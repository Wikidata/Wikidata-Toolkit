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
 * This models a property constraint that says that the value of a property must
 * be formatted using some pattern, which is expressed as a regular expression.
 * <p>
 * For example, <i>ISO 639-1 (P218)</i> is a code for languages. It is
 * constrained with pattern <i>[a-z]{2}</i>, which says that the code contains
 * exactly two lower case letters.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintFormat implements Constraint {

	final PropertyIdValue constrainedProperty;
	final String pattern;

	/**
	 * Constructs a new {@link ConstraintFormat}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param pattern
	 *            pattern expressed as a regular expression
	 */
	public ConstraintFormat(PropertyIdValue constrainedProperty, String pattern) {
		Validate.notNull(pattern, "Pattern cannot be null.");
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.pattern = pattern;
	}

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	/**
	 * Returns the pattern.
	 * 
	 * @return the pattern
	 */
	public String getPattern() {
		return this.pattern;
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
		if (!(obj instanceof ConstraintFormat)) {
			return false;
		}
		ConstraintFormat other = (ConstraintFormat) obj;
		return (this.constrainedProperty.equals(other.constrainedProperty) && this.pattern
				.equals(other.pattern));
	}

	@Override
	public int hashCode() {
		return (this.constrainedProperty.hashCode() + (0x1F * this.pattern
				.hashCode()));
	}

}
