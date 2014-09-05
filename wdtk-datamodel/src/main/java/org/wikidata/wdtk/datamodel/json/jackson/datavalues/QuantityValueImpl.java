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

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuantityValueImpl extends ValueImpl implements QuantityValue {
	
	private Quantity value;

	public QuantityValueImpl(){
		super(typeQuantity);
	}
	
	public QuantityValueImpl(Quantity value){
		super(typeQuantity);
		this.value = value;
	}
	
	public void setValue(Quantity value){
		this.value = value;
	}
	
	public Quantity getValue(){
		return this.value;
	}
	
	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}
	
	@JsonIgnore
	@Override
	public BigDecimal getNumericValue() {
		return this.value.getAmount();
	}

	@JsonIgnore
	@Override
	public BigDecimal getLowerBound() {
		return this.value.getLowerBound();
	}

	@JsonIgnore
	@Override
	public BigDecimal getUpperBound() {
		return this.value.getUpperBound();
	}

	@Override
	public boolean equals(Object o){
		return Equality.equalsQuantityValue(this, o);
	}
}
