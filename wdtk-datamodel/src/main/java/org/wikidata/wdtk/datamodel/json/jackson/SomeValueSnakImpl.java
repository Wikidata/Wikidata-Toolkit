package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;

public class SomeValueSnakImpl extends SnakImpl implements SomeValueSnak {
	
	static final String somevalue = "somevalue";
	
	public SomeValueSnakImpl(){
		super();
		this.setSnakType(somevalue);
	}
	
	public SomeValueSnakImpl(String propertyId){
		super(propertyId);
		this.setSnakType(somevalue);
	}
}
