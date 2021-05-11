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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Jackson implementation of {@link QuantityValue}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize()
public class QuantityValueImpl extends ValueImpl implements QuantityValue {

	/**
	 * Inner helper object to store the actual data. Used to get the nested JSON
	 * structure that is required here.
	 */
	private final JacksonInnerQuantity value;
	
	/**
	 * Constructor.
	 *
	 * @param numericValue
	 *            the numeric value of this quantity
	 * @param lowerBound
	 *            the lower bound of the numeric value of this quantity
	 * @param upperBound
	 *            the upper bound of the numeric value of this quantity
	 * @param unit
	 *            the unit of this quantity (optional)
	 */
	public QuantityValueImpl(
			BigDecimal numericValue,
			Optional<BigDecimal> lowerBound,
			Optional<BigDecimal> upperBound,
			Optional<ItemIdValue> unit) {
		super(JSON_VALUE_TYPE_QUANTITY);
		this.value = new JacksonInnerQuantity(numericValue, lowerBound,
				upperBound, unit);
	}
	
	/**
     * Deprecated constructor.
     *
     * @param numericValue
     *            the numeric value of this quantity
     * @param lowerBound
     *            the lower bound of the numeric value of this quantity or null
     *            if not set
     * @param upperBound
     *            the upper bound of the numeric value of this quantity or null
     *            if not set
     * @param unit
     *            the unit of this quantity or "1" if no unit is set
     */
	@Deprecated
    public QuantityValueImpl(
            BigDecimal numericValue,
            BigDecimal lowerBound,
            BigDecimal upperBound,
            String unit) {
        super(JSON_VALUE_TYPE_QUANTITY);
        this.value = new JacksonInnerQuantity(numericValue, lowerBound,
                upperBound, unit);
    }

	/**
	 * Constructor used for deserialization from JSON with Jackson.
	 */
	@JsonCreator
	QuantityValueImpl(
			@JsonProperty("value") JacksonInnerQuantity value) {
		super(JSON_VALUE_TYPE_QUANTITY);
		this.value = value;
	}

	/**
	 * Returns the inner value helper object. Only for use by Jackson during
	 * serialization.
	 *
	 * @return the inner quantity value
	 */
	public JacksonInnerQuantity getValue() {
		return this.value;
	}

	@JsonIgnore
	@Override
	public BigDecimal getNumericValue() {
		return this.value.getAmount();
	}

	@JsonIgnore
	@Override
	public BigDecimal getLowerBound() {
		return this.value.getLowerBound().orElse(null);
	}
	
	@JsonIgnore
    @Override
    public Optional<BigDecimal> getOptionalLowerBound() {
        return this.value.getLowerBound();
    }

	@JsonIgnore
	@Override
	public BigDecimal getUpperBound() {
		return this.value.getUpperBound().orElse(null);
	}
	
	@JsonIgnore
    @Override
    public Optional<BigDecimal> getOptionalUpperBound() {
        return this.value.getUpperBound();
    }

	@JsonIgnore
	@Override
	public String getUnit() {
		return this.value.getUnitString();
	}

	@JsonIgnore
	@Override
	public ItemIdValue getUnitItemId() {
		return this.value.getUnit().orElse(null);
	}
	
	@JsonIgnore
	@Override
	public Optional<ItemIdValue> getOptionalUnitItemId() {
	    return this.value.getUnit();
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

	/**
	 * Helper object that represents the JSON object structure of the value.
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class JacksonInnerQuantity {

		private final BigDecimal amount;
		private final Optional<BigDecimal> upperBound;
		private final Optional<BigDecimal> lowerBound;
		private final Optional<ItemIdValue> unit;

		/**
		 * Constructor to deserialize from JSON.
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
		JacksonInnerQuantity(
				@JsonProperty("amount") BigDecimal amount,
				@JsonProperty("lowerBound") BigDecimal lowerBound,
				@JsonProperty("upperBound") BigDecimal upperBound,
				@JsonProperty("unit") String unit) {
			this(amount,
			     lowerBound == null ? Optional.empty() : Optional.of(lowerBound),
			     upperBound == null ? Optional.empty() : Optional.of(upperBound),
			     parseUnit(unit));
		}
		
		protected static Optional<ItemIdValue> parseUnit(String unit) {
		    Validate.notEmpty(unit, "Unit cannot be empty. Use \"1\" for unit-less quantities.");
		    return "1".equals(unit) ? Optional.empty() : Optional.of(ItemIdValueImpl.fromIri(unit));
		}
		
		/**
		 * Constructor to construct a fresh quantity value.
		 * 
		 * @param amount
		 *      the main value of this quantity
		 * @param lowerBound
		 *      the lower bound of this quantity
		 * @param upperBound
		 *      the upper bound of this quantity
		 * @param unit
		 *      the unit of this string, as an IRI to the relevant entity
		 */
	    JacksonInnerQuantity(
                BigDecimal amount,
                Optional<BigDecimal> lowerBound,
                Optional<BigDecimal> upperBound,
                Optional<ItemIdValue> unit) {
            Validate.notNull(amount, "Numeric value cannot be null");
            Validate.notNull(unit, "Unit cannot be null");

            if(lowerBound.isPresent() || upperBound.isPresent()) {
                Validate.isTrue(lowerBound.isPresent(), "Lower and upper bounds should be absent at the same time");
                Validate.isTrue(upperBound.isPresent(), "Lower and upper bounds should be absent at the same time");

                if (lowerBound.get().compareTo(amount) > 0) {
                    throw new IllegalArgumentException(
                            "Lower bound cannot be strictly greater than numeric value");
                }
                if (amount.compareTo(upperBound.get()) > 0) {
                    throw new IllegalArgumentException(
                            "Upper bound cannot be strictly smaller than numeric value");
                }
            }
            this.amount = amount;
            this.upperBound = upperBound;
            this.lowerBound = lowerBound;
            this.unit = unit;
        }

		/**
		 * Returns the numeric value.
		 *
		 * @see QuantityValue#getNumericValue()
		 * @return the value
		 */
		@JsonIgnore
		BigDecimal getAmount() {
			return amount;
		}

		/**
		 * Returns the upper bound.
		 *
		 * @see QuantityValue#getUpperBound()
		 * @return the upper bound
		 */
		@JsonIgnore
		Optional<BigDecimal> getUpperBound() {
			return upperBound;
		}

		/**
		 * Returns the lower bound.
		 *
		 * @see QuantityValue#getLowerBound()
		 * @return the lower bound
		 */
		@JsonIgnore
		Optional<BigDecimal> getLowerBound() {
			return lowerBound;
		}

		@JsonProperty("amount")
		String getAmountAsString() {
			return bigDecimalToSignedString(this.amount);
		}

		@JsonProperty("upperBound")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		String getUpperBoundAsString() {
			return this.upperBound.isPresent() ? bigDecimalToSignedString(this.upperBound.get()) : null;
		}

		@JsonProperty("lowerBound")
		@JsonInclude(JsonInclude.Include.NON_NULL)
		String getLowerBoundAsString() {
			return this.lowerBound.isPresent() ? bigDecimalToSignedString(this.lowerBound.get()) : null;
		}

		/**
		 * Returns the string to use for the "unit" field in JSON. The value "1" is
		 * used to denote "no unit"; otherwise an IRI is used to denote specific
		 * units.
		 *
		 * @return unit string
		 */
		@JsonProperty("unit")
		String getUnitString() {
			return unit.isPresent() ? unit.get().getIri() : "1";
		}
		
		/**
		 * Returns the unit as an ItemIdValue.
		 */
		Optional<ItemIdValue> getUnit() {
		    return unit;
		}

		/**
		 * Formats the string output with a leading signum as JSON expects it.
		 */
		private String bigDecimalToSignedString(BigDecimal value) {
			if (value.signum() < 0) {
				return value.toString();
			} else {
				return "+" + value.toString();
			}
		}

	}
}
