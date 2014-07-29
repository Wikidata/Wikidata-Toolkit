package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the inner anonymous object in the JSON type
 * of "wikibase-entityid".
 * Not to be confused with the entityId of the main document.
 * 
 * @author Fredo Erxleben
 *
 */
public class EntityId {
	
	public EntityId(){}
	public EntityId(String entityType, int numericId){
		this.entityType = entityType;
		this.numericId = numericId;
	}

	@JsonProperty("entity-type")
	private String entityType;
	
	@JsonProperty("numeric-id")
	private int numericId; // TODO maybe better use a long?

	@JsonProperty("entity-type")
	public String getEntityType() {
		return entityType;
	}
	
	@JsonProperty("entity-type")
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	
	@JsonProperty("numeric-id")
	public int getNumericId() {
		return numericId;
	}
	
	@JsonProperty("numeric-id")
	public void setNumericId(int numericId) {
		this.numericId = numericId;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		
		if(!(o instanceof EntityId)){
			return false;
		}
		
		return this.numericId == ((EntityId)o).numericId;
	}
}
