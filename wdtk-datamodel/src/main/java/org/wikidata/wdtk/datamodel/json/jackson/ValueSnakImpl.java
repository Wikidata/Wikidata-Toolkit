package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

// TODO test
public abstract class ValueSnakImpl extends SnakImpl implements ValueSnak {

	static final String value = "value";
	
	static final String datatypeString = "string";
	static final String datatypeTime = "time";
	static final String datatypeCoordinate = "globe-coordinate";
	static final String datatypeEntity = "wikibase-item";
	static final String datatypeCommons = "commonsMedia";
	
	private ValueImpl datavalue;
	private String datatype; // should correspond to "type" in datavalue
	
	public ValueSnakImpl(){
		super();
		this.setSnakType(value);
	}
	
	public ValueSnakImpl(String propertyId, String datatype, ValueImpl datavalue){
		super(propertyId);
		this.setSnakType(value);
		this.setDataType(datatype);
		this.setValue(datavalue);
	}
	
	public String getDataType(){
		return this.datatype;
	}
	
	public void setDataType(String datatype) {
		this.datatype = datatype;
	}

	@Override
	public Value getValue() {
		return this.datavalue;
	}
	
	
	public void setValue(ValueImpl datavalue){
		this.datavalue = datavalue;
	}
}
