package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({  
    @Type(value = StringValueImpl.class, name = "string"),  
    @Type(value = TimeValueImpl.class, name = "time"),  
    @Type(value = EntityIdValueImpl.class, name = "wikibase-entityid"),
    @Type(value = GlobeCoordinateValueImpl.class, name = "globecoordinate")}) 
public abstract class ValueImpl implements Value {
	
	public static final String typeString = "string";
	public static final String typeTime = "time";
	public static final String typeCoordinate = "globecoordinate";
	public static final String typeEntity = "wikibase-entityid";
	
	private String type;
	
	public ValueImpl(){}
	public ValueImpl(String type){
		this.type = type;
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}

}
