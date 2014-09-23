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
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.json.jackson.serializers.StatementRankDeserializer;
import org.wikidata.wdtk.datamodel.json.jackson.serializers.StatementRankSerializer;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.JacksonSnak;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonStatement implements Statement {

	private String id;

	@JsonSerialize(using = StatementRankSerializer.class)
	@JsonDeserialize(using = StatementRankDeserializer.class)
	private StatementRank rank;

	private List<JacksonReference> references = new ArrayList<>();

	/**
	 * While this is called "claim" in the WDTK data model, it is called
	 * "mainsnak" in the external JSON. There a claim is the entirety of
	 * statements in their respective groups.
	 */
	private JacksonSnak mainsnak;

	private Map<String, List<JacksonSnak>> qualifiers = new HashMap<>();

	// TODO check if this is the correct approach
	/**
	 * This is needed to satisfy the interface. The claim will be derived from
	 * the Statement itself and the propertyId of its mainsnak.
	 */
	@JsonIgnore
	private JacksonClaim claim;

	public JacksonStatement() {
	}

	public JacksonStatement(String id, JacksonSnak mainsnak) {
		this.id = id;
		this.rank = StatementRank.NORMAL;
		this.mainsnak = mainsnak;
		claim = new JacksonClaim(this, mainsnak.getPropertyId());
	}

	/**
	 * Only needed for correct JSON serialization
	 * 
	 * @return "statement"
	 */
	@JsonProperty("type")
	public String getTypeAsString() {
		return "statement";
	}

	@JsonIgnore
	@Override
	public Claim getClaim() {
		return this.claim;
	}

	@JsonIgnore
	public void setClaim(JacksonClaim claim) {
		this.claim = claim;
	}

	@Override
	public StatementRank getRank() {
		return this.rank;
	}

	public void setRank(StatementRank rank) {
		this.rank = rank;
	}

	@Override
	public List<? extends Reference> getReferences() {
		return this.references;
	}

	public void setReferences(List<JacksonReference> references) {
		this.references = references;
	}

	@JsonProperty("id")
	@Override
	public String getStatementId() {
		return this.id;
	}

	@JsonProperty("id")
	public void setStatementId(String id) {
		this.id = id;
	}

	public JacksonSnak getMainsnak() {
		return this.mainsnak;
	}

	public void setMainsnak(JacksonSnak mainsnak) {
		this.mainsnak = mainsnak;
	}

	public void setQualifiers(Map<String, List<JacksonSnak>> qualifiers) {
		this.qualifiers = qualifiers;
	}

	public Map<String, List<JacksonSnak>> getQualifiers() {
		return this.qualifiers;
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsStatement(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
