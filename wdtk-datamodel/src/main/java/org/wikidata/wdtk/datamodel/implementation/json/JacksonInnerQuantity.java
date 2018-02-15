package org.wikidata.wdtk.datamodel.implementation.json;

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

import com.fasterxml.jackson.annotation.JsonInclude;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.implementation.ValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Helper object that represents the JSON object structure that is used to
 * represent values of type {@link ValueImpl#JSON_VALUE_TYPE_QUANTITY}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonInnerQuantity {

	private final BigDecimal amount;
	private final BigDecimal upperBound;
	private final BigDecimal lowerBound;
	private final String jsonUnit;

	/**
	 * Constructor. The unit given here is a unit string as used in WDTK, with
	 * the empty string meaning "no unit".
	 *
	 * @param amount
	 * 		the main value of this quantity
	 * @param lowerBound
	 * 		the lower bound of this quantity
	 * @param upperBound
	 * 		the upper bound of this quantity
	 * @param unit
	 * 		the unit of this string, as an IRI to the relevant entity
	 */
	@JsonCreator
	public JacksonInnerQuantity(
			@JsonProperty("amount") BigDecimal numericValue,
			@JsonProperty("lowerBound") BigDecimal lowerBound,
			@JsonProperty("upperBound") BigDecimal upperBound,
			@JsonProperty("unit") String unit) {
		Validate.notNull(numericValue, "Numeric value cannot be null");
		Validate.notNull(unit, "Unit cannot be null");

		if(lowerBound != null || upperBound != null) {
			Validate.notNull(lowerBound, "Lower and upper bounds should be null at the same time");
			Validate.notNull(upperBound, "Lower and upper bounds should be null at the same time");

			if (lowerBound.compareTo(numericValue) == 1) {
				throw new IllegalArgumentException(
						"Lower bound cannot be strictly greater than numeric value");
			}
			if (numericValue.compareTo(upperBound) == 1) {
				throw new IllegalArgumentException(
						"Upper bound cannot be strictly smaller than numeric value");
			}
		}
		this.amount = numericValue;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
		if ("".equals(unit)) {
			this.jsonUnit = "1";
		} else {
			this.jsonUnit = unit;
		}
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
	 * Returns the upper bound.
	 *
	 * @see QuantityValue#getUpperBound()
	 * @return the upper bound
	 */
	public BigDecimal getUpperBound() {
		return upperBound;
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

	@JsonProperty("amount")
	public String getAmountAsString() {
		return bigDecimalToSignedString(this.amount);
	}

	@JsonProperty("upperBound")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getUpperBoundAsString() {
		return this.upperBound != null ? bigDecimalToSignedString(this.upperBound) : null;
	}

	@JsonProperty("lowerBound")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String getLowerBoundAsString() {
		return this.lowerBound != null ? bigDecimalToSignedString(this.lowerBound) : null;
	}

	/**
	 * Returns the string to use for the "unit" field in JSON. The value "1" is
	 * used to denote "no unit"; otherwise an IRI is used to denote specific
	 * units.
	 *
	 * @return unit string
	 */
	@JsonProperty("unit")
	public String getJsonUnit() {
		return this.jsonUnit;
	}

	/**
	 * Returns the unit to be used, converting JSON-specific encodings of
	 * "no unit" to the one used in WDTK.
	 *
	 * @return unit string
	 */
	public String getUnit() {
		if ("1".equals(this.jsonUnit)) {
			return "";
		} else {
			return this.jsonUnit;
		}
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
				&& equalsNullable(this.lowerBound, other.lowerBound)
				&& equalsNullable(this.upperBound, other.upperBound)
				&& this.jsonUnit.equals(other.jsonUnit);
	}

	private boolean equalsNullable(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
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
