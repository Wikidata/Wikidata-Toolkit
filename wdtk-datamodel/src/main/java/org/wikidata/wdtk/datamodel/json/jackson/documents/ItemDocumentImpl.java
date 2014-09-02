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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.json.jackson.ClaimImpl;
import org.wikidata.wdtk.datamodel.json.jackson.Helper;
import org.wikidata.wdtk.datamodel.json.jackson.SiteLinkImpl;
import org.wikidata.wdtk.datamodel.json.jackson.StatementGroupImpl;
import org.wikidata.wdtk.datamodel.json.jackson.StatementImpl;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.EntityIdImpl;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.ItemIdImpl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDocumentImpl extends EntityDocumentImpl implements
		ItemDocument {

	// TODO instead of building the statement groups on demand, maybe cache
	// them?

	/**
	 * This is what is called <i>claim</i> in the JSON model. It corresponds to
	 * the statement group in the WDTK model.
	 */
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

		// TODO statements? claims?
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
		this.buildClaims();
	}

	/**
	 * This is needed by the JSON serialization. It corresponds to statement
	 * groups in the WDTK model. On using the WDTK, refer to
	 * {@link ItemDocumentImpl#getStatementGroups()} instead.
	 * 
	 * @return
	 */
	public Map<String, List<StatementImpl>> getClaim() {
		return this.claim;
	}

	/**
	 * Recreate the claims of the statements from the JSON for the data model. Has to be run after
	 * all statements are set.
	 */
	private void buildClaims() {
		for(Entry<String, List<StatementImpl>> entry : this.claim.entrySet()){
			for( StatementImpl statement : entry.getValue()){
				EntityIdImpl wdtkClaimSubject = Helper.constructEntityId(entry.getKey());
				ClaimImpl wdtkClaim = new ClaimImpl(statement, wdtkClaimSubject);
				statement.setClaim(wdtkClaim);
			}
		}
	}

	@Override
	public Iterator<Statement> getAllStatements() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsItemDocument(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
