package org.wikidata.wdtk.datamodel.json.jackson.documents;

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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.json.jackson.MonolingualTextValueImpl;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.EntityIdImpl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="type")
@JsonSubTypes({  
    @Type(value = ItemDocumentImpl.class, name = "item"),  
    @Type(value = PropertyDocumentImpl.class, name = "property")
})
public abstract class EntityDocumentImpl 
implements EntityDocument, TermedDocument {
	
	public static final String typeItem = "item";
	public static final String typeProperty = "property";
	
	protected Map<String, List<MonolingualTextValueImpl>> aliases = new HashMap<>();
	protected Map<String, MonolingualTextValueImpl> labels = new HashMap<>();
	protected Map<String, MonolingualTextValueImpl> descriptions = new HashMap<>();
	
	// the following is not mapped directly towards JSON
	// rather split up into two JSON fields:
	// "type" and "id"
	// the type field in the JSON will be ignored.
	// for a concrete document the type is clear.
	// for writing out to external JSON there is a hard-coded solution
	@JsonIgnore
	protected EntityIdImpl id;

	
	public EntityDocumentImpl(){}
	/**
	 * A constructor for generating ItemDocumentImpl-objects from other
	 * implementations that satisfy the ItemDocument-interface. This can be used
	 * for converting other implementations into this one for later export.
	 * 
	 * @param source is the implementation to be used as a base.
	 */
	public EntityDocumentImpl(TermedDocument source) {
		
		// build id
		this.id = (EntityIdImpl) source.getEntityId();

		// build aliases
		for (Entry<String, List<MonolingualTextValue>> mltvs : source
				.getAliases().entrySet()) {
			List<MonolingualTextValueImpl> value = new LinkedList<>();
			for (MonolingualTextValue mltv : mltvs.getValue()) {
				value.add(new MonolingualTextValueImpl(mltv));
			}
			this.aliases.put(mltvs.getKey(), value);
		}

		// build labels
		for (Entry<String, MonolingualTextValue> mltvs : source.getLabels()
				.entrySet()) {
			this.labels.put(mltvs.getKey(),
					new MonolingualTextValueImpl(mltvs.getValue()));
		}
		// build descriptions
		for (Entry<String, MonolingualTextValue> mltvs : source
				.getDescriptions().entrySet()) {
			this.descriptions.put(mltvs.getKey(), new MonolingualTextValueImpl(
					mltvs.getValue()));
		}
	}
	
	
	@JsonIgnore
	@Override
	public EntityIdValue getEntityId(){
		return this.id;
	}
	
	public void setAliases(Map<String, List<MonolingualTextValueImpl>> aliases) {
		this.aliases = aliases;
	}

	@Override
	public Map<String, List<MonolingualTextValue>> getAliases() {

		// because of the typing provided by the interface one has to
		// re-create the map anew, simple casting is not possible
		Map<String, List<MonolingualTextValue>> returnMap = new HashMap<>();

		for (Entry<String, List<MonolingualTextValueImpl>> entry : this.aliases
				.entrySet()) {
			List<MonolingualTextValue> mltvList = new LinkedList<>();
			mltvList.addAll(entry.getValue());
			returnMap.put(entry.getKey(), mltvList);
		}
		return returnMap;
	}
	
	public void setDescriptions(
			Map<String, MonolingualTextValueImpl> descriptions) {
		this.descriptions = descriptions;
	}

	@Override
	public Map<String, MonolingualTextValue> getDescriptions() {

		// because of the typing provided by the interface one has to
		// re-create the map anew, simple casting is not possible
		Map<String, MonolingualTextValue> returnMap = new HashMap<>();
		returnMap.putAll(this.descriptions);
		return returnMap;
	}
	
	public void setLabels(Map<String, MonolingualTextValueImpl> labels) {
		this.labels = labels;
	}

	@Override
	public Map<String, MonolingualTextValue> getLabels() {

		// because of the typing provided by the interface one has to
		// re-create the map anew, simple casting is not possible
		Map<String, MonolingualTextValue> returnMap = new HashMap<>();
		returnMap.putAll(this.labels);
		return returnMap;
	}
	
	@JsonProperty("id")
	public void setId(String id) {
		this.id.setId(id);
	}

	@JsonProperty("id")
	public String getId() {
		return this.id.getId();
	}
	
	/**
	 * This method is only used for handling the JSON export correctly.
	 * @return either "item" or "property"
	 */
	@JsonProperty("type")
	public abstract String getType();
	
	@Override
	public boolean equals(Object o){
		if(this == o){ return true; }
		if(!(o instanceof EntityDocumentImpl)){
			return false;
		}
		EntityDocumentImpl other = (EntityDocumentImpl)o;
		
		return this.id.equals(other.id)
				&& this.aliases.equals(other.aliases)
				&& this.descriptions.equals(other.descriptions)
				&& this.labels.equals(other.labels);
	}
}
