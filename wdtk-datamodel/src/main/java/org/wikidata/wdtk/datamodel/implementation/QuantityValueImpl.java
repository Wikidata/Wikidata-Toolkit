package org.wikidata.wdtk.datamodel.implementation;

/*
 * #%L
 * Wikidata Toolkit Data Model
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

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

/**
 * Implementation of {@link QuantityValue}.
 *
 * @author Markus Kroetzsch
 *
 */
public class QuantityValueImpl implements QuantityValue, Serializable {

	private static final long serialVersionUID = 3245696048836886990L;
	
	final BigDecimal numericValue;
	final BigDecimal lowerBound;
	final BigDecimal upperBound;

	/**
	 * Constructor.
	 *
	 * @param numericValue
	 *            the numeric value of this quantity
	 * @param lowerBound
	 *            the lower bound of the numeric value of this quantity
	 * @param upperBound
	 *            the upper bound of the numeric value of this quantity
	 */
	QuantityValueImpl(BigDecimal numericValue, BigDecimal lowerBound,
			BigDecimal upperBound) {
		Validate.notNull(numericValue, "Numeric value cannot be null");
		Validate.notNull(lowerBound, "Lower bound cannot be null");
		Validate.notNull(upperBound, "Upper bound cannot be null");
		if (lowerBound.compareTo(numericValue) == 1) {
			throw new IllegalArgumentException(
					"Lower bound cannot be strictly greater than numeric value");
		}
		if (numericValue.compareTo(upperBound) == 1) {
			throw new IllegalArgumentException(
					"Upper bound cannot be strictly smaller than numeric value");
		}

		this.numericValue = numericValue;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	@Override
	public BigDecimal getNumericValue() {
		return numericValue;
	}

	@Override
	public BigDecimal getLowerBound() {
		return lowerBound;
	}

	@Override
	public BigDecimal getUpperBound() {
		return upperBound;
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsQuantityValue(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

}
