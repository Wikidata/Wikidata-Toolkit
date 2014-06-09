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

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * This models a property constraint that says that a property is used as
 * qualifier.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintQualifier implements Constraint {

	/**
	 * Constructs a new {@link ConstraintQualifier}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 */
	public ConstraintQualifier(PropertyIdValue constrainedProperty) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		this.constrainedProperty = constrainedProperty;
	}

	final PropertyIdValue constrainedProperty;

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	@Override
	public <T> T accept(ConstraintVisitor<T> visitor) {
		Validate.notNull(visitor, "Visitor cannot be null.");
		return visitor.visit(this);
	}

}
