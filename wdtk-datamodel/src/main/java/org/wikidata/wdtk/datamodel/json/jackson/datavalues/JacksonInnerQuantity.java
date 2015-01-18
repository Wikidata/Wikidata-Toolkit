package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

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

import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Helper object that represents the JSON object structure that is used to
 * represent values of type {@link JacksonValue#JSON_VALUE_TYPE_QUANTITY}.
 *
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonInnerQuantity {

	private BigDecimal amount;
	private BigDecimal upperBound;
	private BigDecimal lowerBound;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonInnerQuantity() {
	}

	/**
	 * TODO Review the utility of this constructor.
	 *
	 * @param amount
	 * @param upperBound
	 * @param lowerBound
	 */
	public JacksonInnerQuantity(BigDecimal amount, BigDecimal upperBound,
			BigDecimal lowerBound) {
		this.amount = amount;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}

	/**
	 * Returns the numeric value.
	 *
	 * @see QuantityValue#getNumericValue()
	 * @return the value
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * Sets the numerical value to the given value. Only for use by Jackson
	 * during deserialization.
	 *
	 * @param amount
	 *            new value
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * Returns the upper bound.
	 *
	 * @see QuantityValue#getUpperBound()
	 * @return the upper bound
	 */
	public BigDecimal getUpperBound() {
		return upperBound;
	}

	/**
	 * Sets the upper bound to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param upperBound
	 *            new value
	 */
	public void setUpperBound(BigDecimal upperBound) {
		this.upperBound = upperBound;
	}

	/**
	 * Returns the lower bound.
	 *
	 * @see QuantityValue#getLowerBound()
	 * @return the lower bound
	 */
	public BigDecimal getLowerBound() {
		return lowerBound;
	}

	/**
	 * Sets the lower bound to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param lower
	 *            bound new value
	 */
	public void setLowerBound(BigDecimal lowerBound) {
		this.lowerBound = lowerBound;
	}

	@JsonProperty("amount")
	public String getAmountAsString() {
		return bigDecimalToSignedString(this.amount);
	}

	@JsonProperty("upperBound")
	public String getUpperBoundAsString() {
		return bigDecimalToSignedString(this.upperBound);
	}

	@JsonProperty("lowerBound")
	public String getLowerBoundAsString() {
		return bigDecimalToSignedString(this.lowerBound);
	}

	/**
	 * Returns the string to use for the "unit" field in JSON. Units of
	 * mesurement are not supported yet, so this is a constant value of 1.
	 *
	 * @return unit string
	 */
	@JsonProperty("unit")
	public String getUnit() {
		return "1";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof JacksonInnerQuantity)) {
			return false;
		}
		JacksonInnerQuantity other = (JacksonInnerQuantity) o;

		return this.amount.equals(other.amount)
				&& this.lowerBound.equals(other.lowerBound)
				&& this.upperBound.equals(other.upperBound);
	}

	/**
	 * Formats the string output with a leading signum as JSON expects it.
	 *
	 * @param value
	 * @return
	 */
	private String bigDecimalToSignedString(BigDecimal value) {
		if (value.signum() < 0) {
			return value.toString();
		} else {
			return "+" + value.toString();
		}
	}

}
