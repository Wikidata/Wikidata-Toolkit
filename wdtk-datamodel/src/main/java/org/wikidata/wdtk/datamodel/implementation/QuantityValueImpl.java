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

import java.math.BigDecimal;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;

/**
 * Implementation of {@link QuantityValue}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class QuantityValueImpl implements QuantityValue {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numericValue.hashCode();
		result = prime * result + lowerBound.hashCode();
		result = prime * result + upperBound.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof QuantityValueImpl)) {
			return false;
		}
		QuantityValueImpl other = (QuantityValueImpl) obj;
		return numericValue.equals(other.numericValue)
				&& lowerBound.equals(other.lowerBound)
				&& upperBound.equals(other.upperBound);
	}
	
	@Override
	public String toString(){
		return "(Quantity)" + this.lowerBound 
				+ " <= " + this.numericValue 
				+ " <= " + this.upperBound;
	}

}
