package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.json.jackson.datavalues.EntityId;

// TODO test
public class EntityIdValueImpl extends ValueImpl {

	private EntityId value;
	
	public EntityIdValueImpl(){
		super(typeEntity);
	}
	
	public EntityIdValueImpl(EntityId value){
		super(typeEntity);
		this.value = value;
	}
	
	public EntityId getValue(){
		return value;
	}
	
	public void setValue(EntityId value){
		this.value = value;
	}
	

}
