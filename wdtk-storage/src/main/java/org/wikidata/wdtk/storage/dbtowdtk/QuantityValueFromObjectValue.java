package org.wikidata.wdtk.storage.dbtowdtk;

/*
 * #%L
 * Wikidata Toolkit Storage
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
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.storage.datamodel.DecimalValue;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class QuantityValueFromObjectValue implements QuantityValue {

	final ObjectValue value;

	DecimalValue numericValue = null;
	DecimalValue lowerBound = null;
	DecimalValue upperBound = null;

	public QuantityValueFromObjectValue(ObjectValue value) {
		this.value = value;
	}

	private void initialize() {
		if (this.numericValue == null) {
			for (PropertyValuePair pvp : this.value) {
				switch (pvp.getProperty()) {
				case WdtkSorts.PROP_QUANTITY_VALUE:
					this.numericValue = ((DecimalValue) pvp.getValue());
					break;
				case WdtkSorts.PROP_QUANTITY_LOWER:
					this.lowerBound = ((DecimalValue) pvp.getValue());
					break;
				case WdtkSorts.PROP_QUANTITY_UPPER:
					this.upperBound = ((DecimalValue) pvp.getValue());
					break;
				default:
					throw new RuntimeException("Unexpected property "
							+ pvp.getProperty() + " in quantity record.");
				}
			}
		}
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	public BigDecimal getNumericValue() {
		initialize();
		return this.numericValue.getDecimal();
	}

	@Override
	public BigDecimal getLowerBound() {
		initialize();
		return this.lowerBound.getDecimal();
	}

	@Override
	public BigDecimal getUpperBound() {
		initialize();
		return this.upperBound.getDecimal();
	}

}
