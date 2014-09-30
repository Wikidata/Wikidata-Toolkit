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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.util.NestedIterator;

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
	Map<String, List<JacksonStatement>> claims = new HashMap<>();
	/**
	 * Map to store site links.
	 */
	private Map<String, JacksonSiteLink> sitelinks = new HashMap<>();

	/**
	 * Statement groups. This member is initialized when statements are
	 * accessed.
	 */
	private List<StatementGroup> statementGroups = null;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonItemDocument() {
	}

	@Override
	public String getJsonType() {
		return JacksonTermedDocument.JSON_TYPE_ITEM;
	}

	@JsonIgnore
	@Override
	public ItemIdValue getItemId() {
		if (this.siteIri == null) {
			return Datamodel.makeWikidataItemIdValue(this.entityId);
		} else {
			return Datamodel.makeItemIdValue(this.entityId, this.siteIri);
		}
	}

	@JsonIgnore
	@Override
	public EntityIdValue getEntityId() {
		return getItemId();
	}

	@JsonIgnore
	@Override
	public List<StatementGroup> getStatementGroups() {
		if (this.statementGroups == null) {
			this.statementGroups = new ArrayList<>(this.claims.size());
			for (List<JacksonStatement> statements : this.claims.values()) {
				this.statementGroups
						.add(new StatementGroupFromJson(statements));
			}
		}
		return this.statementGroups;
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
		return Collections.<String, SiteLink> unmodifiableMap(this.sitelinks);
	}

	/**
	 * Sets the "claims" to the given value. Only for use by Jackson during
	 * deserialization.
	 * <p>
	 * The name refers to the JSON model, where claims are similar to statement
	 * groups. This should not be confused with claims as used in the WDTK data
	 * model. This will probably only be used by the Jacksons' ObjectMapper.
	 *
	 * @param claims
	 */
	@JsonProperty("claims")
	public void setJsonClaims(Map<String, List<JacksonStatement>> claims) {
		this.claims = claims;
		this.statementGroups = null; // clear cache
		updateClaims();
	}

	/**
	 * Sets the subject of each of the current statements ("claims" in JSON) to
	 * the current entity id. This is required since the JSON serialization of
	 * statements does not contain a subject id, but subject ids are part of the
	 * statement data in WDTK. The update is needed whenever the statements or
	 * the entity id have changed.
	 */
	private void updateClaims() {
		this.statementGroups = null; // clear cache

		for (Entry<String, List<JacksonStatement>> entry : this.claims
				.entrySet()) {
			for (JacksonStatement statement : entry.getValue()) {
				statement.setParentDocument(this);
			}
		}
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

	@Override
	public Iterator<Statement> getAllStatements() {
		return new NestedIterator<>(this.getStatementGroups());
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
