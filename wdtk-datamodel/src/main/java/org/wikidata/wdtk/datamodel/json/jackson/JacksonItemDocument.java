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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson implementation of {@link ItemDocument}. Like all Jackson objects, it
 * is not technically immutable, but it is strongly recommended to treat it as
 * such in all contexts: the setters are for Jackson; never call them in your
 * code.
 *
 * @author Fredo Erxleben
 *
 */
@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonItemDocument extends JacksonTermedDocument implements
		ItemDocument {

	// TODO instead of building the statement groups on demand, maybe cache
	// them?

	/**
	 * This is what is called <i>claim</i> in the JSON model. It corresponds to
	 * the statement group in the WDTK model.
	 */
	private Map<String, List<JacksonStatement>> claims = new HashMap<>();
	/**
	 * Map to store site links.
	 */
	private Map<String, JacksonSiteLink> sitelinks = new HashMap<>();

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonItemDocument() {
		this.entityIdValue = new JacksonItemId();
	}

	/**
	 * Copy constructor. Can be used for converting other implementations of
	 * {@link ItemDocument} into objects of this class for conversion to JSON.
	 *
	 * @param source
	 *            the object to copy
	 */
	public JacksonItemDocument(ItemDocument source) {
		super(source);

		// set id
		this.entityIdValue = new JacksonItemId(source.getItemId().getId());

		// build siteLinks
		for (Entry<String, SiteLink> mltvs : source.getSiteLinks().entrySet()) {
			this.sitelinks.put(mltvs.getKey(),
					new JacksonSiteLink(mltvs.getValue()));
		}

		// FIXME statements? claims?
	}

	@Override
	public String getJsonType() {
		return JacksonTermedDocument.JSON_TYPE_ITEM;
	}

	@Override
	public void setJsonId(String id) {
		this.entityIdValue = new JacksonItemId(id);
	}

	@JsonIgnore
	@Override
	public ItemIdValue getItemId() {
		return (JacksonItemId) this.entityIdValue;
	}

	@JsonIgnore
	@Override
	public List<StatementGroup> getStatementGroups() {
		List<StatementGroup> resultList = new ArrayList<>();
		for (JacksonStatementGroup statementGroup : Helper
				.buildStatementGroups(this.claims)) {
			resultList.add(statementGroup);
		}
		return resultList;
	}

	/**
	 * Sets the site links to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param sitelinks
	 *            new value
	 */
	@JsonProperty("sitelinks")
	public void setSiteLinks(Map<String, JacksonSiteLink> sitelinks) {
		this.sitelinks = sitelinks;
	}

	@JsonProperty("sitelinks")
	@Override
	public Map<String, SiteLink> getSiteLinks() {

		// because of the typing provided by the interface one has to
		// re-create the map anew, simple casting is not possible
		Map<String, SiteLink> returnMap = new HashMap<>();
		returnMap.putAll(this.sitelinks);
		return returnMap;
	}

	/**
	 * Sets the "claims" to the given value. Only for use by Jackson during
	 * deserialization.
	 * <p>
	 * The name refers to the JSON model, where claims are similar to statement
	 * groups. This should not be confused with claims as used in the WDTK data
	 * model. This will probably only be used by the Jacksons' ObjectMapper.
	 *
	 * @param claim
	 */
	@JsonProperty("claims")
	public void setJsonClaims(Map<String, List<JacksonStatement>> claim) {
		this.claims = claim;
		this.buildClaims();
	}

	/**
	 * Returns the "claims". Only used by Jackson.
	 * <p>
	 * JSON "claims" correspond to statement groups in the WDTK model. You
	 * should use {@link JacksonItemDocument#getStatementGroups()} to obtain
	 * this data.
	 *
	 * @return map of statement groups
	 */
	@JsonProperty("claims")
	public Map<String, List<JacksonStatement>> getJsonClaims() {
		return this.claims;
	}

	/**
	 * Recreates the claims of the statements from the JSON for the data model.
	 * Has to be run after all statements are set.
	 */
	private void buildClaims() {
		for (Entry<String, List<JacksonStatement>> entry : this.claims
				.entrySet()) {
			for (JacksonStatement statement : entry.getValue()) {
				JacksonEntityId wdtkClaimSubject = Helper
						.constructEntityId(entry.getKey());
				JacksonClaim wdtkClaim = new JacksonClaim(statement,
						wdtkClaimSubject);
				statement.setClaim(wdtkClaim);
			}
		}
	}

	@Override
	public Iterator<Statement> getAllStatements() {
		// FIXME inefficient; use nested iterators instead
		List<Statement> allStatements = new ArrayList<>();

		for (List<JacksonStatement> value : this.claims.values()) {
			allStatements.addAll(value);
		}
		return allStatements.iterator();
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
