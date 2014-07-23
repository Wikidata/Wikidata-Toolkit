package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;

public class NoValueSnakImpl extends SnakImpl implements NoValueSnak {
	
	static final String novalue = "novalue";
	
	public NoValueSnakImpl(){
		super();
		this.setSnakType(novalue);
	}
	
	public NoValueSnakImpl(String propertyId){
		super(propertyId);
		this.setSnakType(novalue);
	}
}
