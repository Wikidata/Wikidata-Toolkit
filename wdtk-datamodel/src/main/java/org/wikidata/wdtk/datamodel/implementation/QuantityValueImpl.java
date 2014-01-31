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

	/**
	 * Constructor.
	 * 
	 * @param numericValue
	 *            the numeric value of this quantity
	 */
	QuantityValueImpl(BigDecimal numericValue) {
		Validate.notNull(numericValue, "Numeric value cannot be null");
		this.numericValue = numericValue;
	}

	@Override
	public BigDecimal getNumericValue() {
		return numericValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return numericValue.hashCode();
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
		return numericValue.equals(other.numericValue);
	}

}
