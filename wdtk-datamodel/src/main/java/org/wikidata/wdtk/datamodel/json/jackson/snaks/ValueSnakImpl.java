package org.wikidata.wdtk.datamodel.json.jackson.snaks;

import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.ValueImpl;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class ValueSnakImpl extends SnakImpl implements ValueSnak {

	static final String value = "value";
	
	// the names used in the JSON
	public static final String datatypeString = "string";
	public static final String datatypeTime = "time";
	public static final String datatypeCoordinate = "globe-coordinate";
	public static final String datatypeEntity = "wikibase-item";
	public static final String datatypeCommons = "commonsMedia";
	
	private ValueImpl datavalue;
	private String datatype; // should correspond to "type" in datavalue
	
	public ValueSnakImpl(){
		super();
		this.setSnakType(value);
	}
	
	public ValueSnakImpl(String propertyId, String datatype, ValueImpl datavalue){
		super(propertyId);
		this.setSnakType(value);
		this.setDatatype(datatype);
		this.setDatavalue(datavalue);
	}
	
	public String getDatatype(){
		return this.datatype;
	}
	
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	@JsonIgnore
	@Override
	public Value getValue() {
		return this.datavalue;
	}
	
	
	public void setDatavalue(ValueImpl datavalue){
		this.datavalue = datavalue;
	}
	
	public ValueImpl getDatavalue(){
		return this.datavalue;
	}
}
