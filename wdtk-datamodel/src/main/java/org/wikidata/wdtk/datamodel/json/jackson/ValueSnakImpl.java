package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

// TODO test
public abstract class ValueSnakImpl extends SnakImpl implements ValueSnak {

	static final String value = "value";
	
	private ValueImpl datavalue;
	private String datatype; // TODO should correspond to "type" in datavalue
	
	public ValueSnakImpl(){
		super();
		this.setSnakType(value);
	}
	
	public ValueSnakImpl(String propertyId, ValueImpl datavalue){
		super(propertyId);
		this.setSnakType(value);
		this.setValue(datavalue);
	}
	
	@Override
	public Value getValue() {
		return this.datavalue;
	}
	
	
	public void setValue(ValueImpl datavalue){
		this.datavalue = datavalue;
	}
}
