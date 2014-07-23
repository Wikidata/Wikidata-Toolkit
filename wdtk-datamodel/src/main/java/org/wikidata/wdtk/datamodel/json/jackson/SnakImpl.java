package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="snaktype")
@JsonSubTypes({  
    @Type(value = NoValueSnakImpl.class, name = "novalue"),  
    @Type(value = SomeValueSnakImpl.class, name = "somevalue"),  
    @Type(value = ValueSnakImpl.class, name = "value") }) 
public abstract class SnakImpl implements Snak {
	
	private String property;
	private String snaktype;
	
	protected SnakImpl(){}
	protected SnakImpl(String propertyId){
		this.property = propertyId;
	}

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

	public String getSnaktype(){
		return this.snaktype;
	}
	
	public void setSnakType(String snacktype){
		this.snaktype = snacktype;
	}
	
	@Override
	public <T> T accept(SnakVisitor<T> snakVisitor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean equals(Object o){
		
		if(this == o){
			return true;
		}
		
		if(!(o instanceof SnakImpl)){
			return false;
		}
		
		SnakImpl other = (SnakImpl) o;
		if(this.getSnaktype().equals(other.getSnaktype())
				&& this.getProperty().equals(other.getProperty())){
			return true;
		}
		return false;
		
	}

}
