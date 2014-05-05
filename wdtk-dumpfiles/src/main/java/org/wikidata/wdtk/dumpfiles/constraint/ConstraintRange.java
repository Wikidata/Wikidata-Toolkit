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
	final DateAndNow minDate;
	final DateAndNow maxDate;
	final Double minNum;
	final Double maxNum;
	final boolean isTime;

	public ConstraintRange(PropertyIdValue constrainedProperty,
			DateAndNow minDate, DateAndNow maxDate) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		Validate.notNull(minDate, "Min date cannot be null.");
		Validate.notNull(maxDate, "Max date cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.minDate = minDate;
		this.maxDate = maxDate;
		this.minNum = null;
		this.maxNum = null;
		this.isTime = true;
	}

	public ConstraintRange(PropertyIdValue constrainedProperty,
			Double minNum, Double maxNum) {
		Validate.notNull(constrainedProperty, "Property cannot be null.");
		Validate.notNull(minNum, "Min date cannot be null.");
		Validate.notNull(maxNum, "Max date cannot be null.");
		this.constrainedProperty = constrainedProperty;
		this.minNum = minNum;
		this.maxNum = maxNum;
		this.minDate = null;
		this.maxDate = null;
		this.isTime = false;
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

	public Double getMinNum() {
		return minNum;
	}

	public Double getMaxNum() {
		return maxNum;
	}

	public DateAndNow getMinDate() {
		return minDate;
	}

	public DateAndNow getMaxDate() {
		return maxDate;
	}

	@Override
	public <T> T accept(ConstraintVisitor<T> visitor) {
		Validate.notNull(visitor, "Visitor cannot be null.");
		return visitor.visit(this);
	}

}
