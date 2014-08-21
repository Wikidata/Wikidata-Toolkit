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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.json.jackson.Helper;
import org.wikidata.wdtk.datamodel.json.jackson.SiteLinkImpl;
import org.wikidata.wdtk.datamodel.json.jackson.StatementGroupImpl;
import org.wikidata.wdtk.datamodel.json.jackson.StatementImpl;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.ItemIdImpl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true) // TODO better handling
public class ItemDocumentImpl extends EntityDocumentImpl implements
		ItemDocument {

	// TODO instead of building the statement groups on demand, maybe cache
	// them?

	private Map<String, List<StatementImpl>> claim = new HashMap<>();
	private Map<String, SiteLinkImpl> sitelinks = new HashMap<>();

	public ItemDocumentImpl() {
		this.id = new ItemIdImpl();
	}

	/**
	 * A constructor for generating ItemDocumentImpl-objects from other
	 * implementations that satisfy the ItemDocument-interface. This can be used
	 * for converting other implementations into this one for later export.
	 * 
	 * @param source
	 *            is the implementation to be used as a base.
	 */
	public ItemDocumentImpl(ItemDocument source) {
		super(source);

		// set id
		this.id = new ItemIdImpl(source.getItemId().getId());

		// build siteLinks
		for (Entry<String, SiteLink> mltvs : source.getSiteLinks().entrySet()) {
			this.sitelinks.put(mltvs.getKey(),
					new SiteLinkImpl(mltvs.getValue()));
		}
	}

	@Override
	public String getType() {
		return typeItem;
	}

	@JsonIgnore
	// here for the interface; JSON field is handled by getId()
	@Override
	public ItemIdValue getItemId() {
		return (ItemIdImpl) this.id;
	}

	@JsonIgnore
	// only needed to satisfy the interface
	@Override
	public List<StatementGroup> getStatementGroups() {
		List<StatementGroup> resultList = new ArrayList<>();
		for (StatementGroupImpl statementGroup : Helper
				.buildStatementGroups(this.claim)) {
			resultList.add(statementGroup);
		}
		return resultList;
	}

	public void setSitelinks(Map<String, SiteLinkImpl> sitelinks) {
		this.sitelinks = sitelinks;
	}

	@JsonProperty("sitelinks")
	// camel case to be compatible with interface
	@Override
	public Map<String, SiteLink> getSiteLinks() {

		// because of the typing provided by the interface one has to
		// re-create the map anew, simple casting is not possible
		Map<String, SiteLink> returnMap = new HashMap<>();
		returnMap.putAll(this.sitelinks);
		return returnMap;
	}

	/**
	 * This is needed for the JSON model, where claims are similar to statement
	 * groups. <b>Do not confuse this with claims as stated in the WDTK data
	 * model.</b> This will probably only be used by the Jacksons' ObjectMapper.
	 * 
	 * @param claim
	 */
	public void setClaim(Map<String, List<StatementImpl>> claim) {
		this.claim = claim;
	}

	public Map<String, List<StatementImpl>> getClaim() {
		return this.claim;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof ItemDocumentImpl)) {
			return false;
		}
		ItemDocumentImpl other = (ItemDocumentImpl) o;

		return this.getItemId().equals(other.getItemId())
				&& super.equals(other)
				&& this.claim.equals(other.claim)
				&& this.sitelinks.equals(other.sitelinks);

	}

	/**
	 * Recreate the claims from the JSON for the data model. Has to be run after
	 * all statements are set.
	 */
	void buildClaims() {
		// TODO
	}
}
