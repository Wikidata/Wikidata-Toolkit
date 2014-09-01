package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
