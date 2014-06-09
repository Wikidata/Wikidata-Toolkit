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
 * This models a property constraint that says that a property can take values
 * between the defined range. This range can be dates, integers, or real
 * numbers.
 * <p>
 * For example, <i>spacecraft landing date (P620)</i> (in UTC) can take 4
 * October 1957 or any date after that; <i>atomic number (P1086)</i>, number of
 * protons found in the nucleus of the atom, can take values between 1 and 118;
 * <i>orbital eccentricity (P1096)</i>, the amount of the deviation of an orbit
 * from a perfect circle, can take values between 0 and 1.
 * 
 * @author Julian Mendez
 * 
 */
public class ConstraintRange implements Constraint {

	final PropertyIdValue constrainedProperty;
	final String min;
	final String max;
	final boolean isTime;

	/**
	 * Constructs a new {@link ConstraintRange}.
	 * 
	 * @param constrainedProperty
	 *            constrained property
	 * @param min
	 *            minimum value of the interval
	 * @param max
	 *            maximum value of the interval
	 * @param isTime
	 *            <code>true</code> if the range is time, or <code>false</code>
	 *            is the range is of other type of quantities
	 */
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

}
