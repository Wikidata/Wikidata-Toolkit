package org.wikidata.wdtk.datamodel.externalJsonImplementation;

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

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDocumentImpl extends EntityDocumentImpl implements
		ItemDocument {
	
	// the type field will be ignored.
	// this is an ItemDocument, so the type is clear.
	// for writing out to external Json there is a hard-coded solution

	Map<String, List<MonolingualTextValueImpl>> aliases = new HashMap<>();
	Map<String, MonolingualTextValueImpl> labels = new HashMap<>();
	Map<String, MonolingualTextValueImpl> descriptions = new HashMap<>();
	
	// the following is not mapped directly towards Json
	// rather split up into two Json fields
	@JsonIgnore
	ItemIdValueImpl itemId = new ItemIdValueImpl();
	
	// TODO siteLinks
	// TODO StatementGroups (claims)

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

	public void setAliases(Map<String, List<MonolingualTextValueImpl>> aliases){
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

	@JsonProperty("id")
	public void setId(String id){
		this.itemId.setId(id);
	}
	
	@JsonProperty("id")
	public String getId(){
		return this.itemId.getId();
	}
	
	@JsonProperty("type")
	public String getType(){
		return "item";
	}
	
	@JsonIgnore
	public void setItemId(ItemIdValueImpl itemId){
		this.itemId = itemId;
	}
	
	@JsonIgnore
	@Override
	public ItemIdValue getItemId() {
		return this.itemId;
	}

	@JsonIgnore
	@Override
	public List<StatementGroup> getStatementGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	@JsonIgnore
	@Override
	public Map<String, SiteLink> getSiteLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@JsonIgnore
	@Override
	public EntityIdValue getEntityId() {
		return this.itemId;
	}

}
