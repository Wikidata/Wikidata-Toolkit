package org.wikidata.wdtk.datamodel.json.jackson;

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

import java.util.ArrayList;
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

	private Map<String, List<MonolingualTextValueImpl>> aliases = new HashMap<>();
	private Map<String, MonolingualTextValueImpl> labels = new HashMap<>();
	private Map<String, MonolingualTextValueImpl> descriptions = new HashMap<>();
	private Map<String, List<StatementImpl>> claim = new HashMap<>();

	@JsonProperty("sitelinks") // has a different name in the JSON files
	private Map<String, SiteLinkImpl> siteLinks = new HashMap<>();

	// the following is not mapped directly towards JSON
	// rather split up into two JSON fields:
	// "type" and "id"
	// the type field in the JSON will be ignored.
	// this is an ItemDocument, so the type is clear.
	// for writing out to external JSON there is a hard-coded solution
	@JsonIgnore
	private ItemIdImpl itemId = new ItemIdImpl();

	public ItemDocumentImpl() {
	}

	/**
	 * A constructor for generating ItemDocumentImpl-objects from other
	 * implementations that satisfy the ItemDocument-interface. This can be used
	 * for converting other implementations into this one for later export.
	 * 
	 * @param source is the implementation to be used as a base.
	 */
	public ItemDocumentImpl(ItemDocument source) {

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
		// build siteLinks
		for (Entry<String, SiteLink> mltvs : source.getSiteLinks().entrySet()) {
			this.siteLinks.put(mltvs.getKey(),
					new SiteLinkImpl(mltvs.getValue()));
		}
		// build StatementGroups
		// TODO
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

	@JsonProperty("id")
	public void setId(String id) {
		this.itemId.setId(id);
	}

	@JsonProperty("id")
	public String getId() {
		return this.itemId.getId();
	}

	@JsonProperty("type")
	public String getType() {
		return "item";
	}

	@JsonIgnore // ignored since the JSON field is handled by setId()
	public void setItemId(ItemIdImpl itemId) {
		this.itemId = itemId;
	}

	@JsonIgnore // ignored since the JSON field is handled by getId()
	@Override 
	public ItemIdValue getItemId() {
		return this.itemId;
	}

	@JsonIgnore // only needed to satisfy the interface
	@Override
	public List<StatementGroup> getStatementGroups() {
		List<StatementGroup> resultList = new ArrayList<>();
		for(StatementGroupImpl statementGroup : Helper.buildStatementGroups(this.claim)){
			resultList.add(statementGroup);
		}
		return resultList;
	}

	public void setSitelinks(Map<String, SiteLinkImpl> sitelinks) {
		this.siteLinks = sitelinks;
	}

	@JsonProperty("sitelinks")
	@Override
	public Map<String, SiteLink> getSiteLinks() {

		// because of the typing provided by the interface one has to
		// re-create the map anew, simple casting is not possible
		Map<String, SiteLink> returnMap = new HashMap<>();
		returnMap.putAll(this.siteLinks);
		return returnMap;
	}

	@JsonIgnore // not needed in JSON, just to satisfy the interface
	@Override
	public EntityIdValue getEntityId() {
		return this.itemId;
	}

	public void setClaim(Map<String, List<StatementImpl>> claim){
		this.claim = claim;
	}
	
	public Map<String, List<StatementImpl>> getClaim(){
		return this.claim;
	}
}
