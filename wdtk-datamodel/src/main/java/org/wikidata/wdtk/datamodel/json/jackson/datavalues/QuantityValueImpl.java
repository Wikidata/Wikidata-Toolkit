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
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuantityValueImpl extends ValueImpl implements QuantityValue {

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	private BigDecimal amount;
	private BigDecimal upperBound;
	private BigDecimal lowerBound;
	
	public void setAmount(String amount){
		this.amount = new BigDecimal(amount);
	}
	
	public BigDecimal getAmount(){
		return this.amount;
	}
	
	public void setUpperBound(String upperBound){
		this.upperBound = new BigDecimal(upperBound);
	}
	
	public void setLowerBound(String lowerBound){
		this.lowerBound = new BigDecimal(lowerBound);
	}
	
	@JsonIgnore
	@Override
	public BigDecimal getNumericValue() {
		return this.amount;
	}

	@Override
	public BigDecimal getLowerBound() {
		return this.lowerBound;
	}

	@Override
	public BigDecimal getUpperBound() {
		return this.upperBound;
	}

}
