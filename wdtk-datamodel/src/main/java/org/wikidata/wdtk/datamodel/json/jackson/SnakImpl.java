package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class SnakImpl implements Snak {
	
	private String property;
	private String snacktype;

	public String getProperty(){
		return this.property;
	}
	
	public void setProperty(String property){
		this.property = property;
	}
	
	@JsonIgnore
	@Override
	public PropertyIdValue getPropertyId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getSnacktype(){
		return this.snacktype;
	}
	
	public void setSnackType(String snacktype){
		this.snacktype = snacktype;
	}
	
	@Override
	public <T> T accept(SnakVisitor<T> snakVisitor) {
		// TODO Auto-generated method stub
		return null;
	}

}
