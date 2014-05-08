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
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintRange implements Constraint {

	final PropertyIdValue constrainedProperty;
	final String min;
	final String max;
	final boolean isTime;

	public ConstraintRange(PropertyIdValue constrainedProperty, String min,
			String max, boolean isTime) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		Validate.notNull(min, "Min cannot be null.");
		Validate.notNull(max, "Max cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.min = min;
		this.max = max;
		this.isTime = isTime;
	}

	@Override
	public PropertyIdValue getConstrainedProperty() {
		return this.constrainedProperty;
	}

	public boolean isQuantity() {
		return !isTime;
	}

	public boolean isTime() {
		return isTime;
	}

	public String getMin() {
		return min;
	}

	public String getMax() {
		return max;
	}

	@Override
	public <T> T accept(ConstraintVisitor<T> visitor) {
		Validate.notNull(visitor, "Visitor cannot be null.");
		return visitor.visit(this);
	}

}
