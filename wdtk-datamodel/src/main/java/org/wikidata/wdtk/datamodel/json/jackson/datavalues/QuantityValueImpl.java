package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

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
