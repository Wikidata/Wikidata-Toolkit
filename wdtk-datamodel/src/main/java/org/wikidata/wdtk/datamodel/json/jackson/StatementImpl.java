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

import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.json.jackson.serializers.StatementRankDeserializer;
import org.wikidata.wdtk.datamodel.json.jackson.serializers.StatementRankSerializer;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.SnakImpl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatementImpl implements Statement {

	private String id;

	@JsonSerialize(using = StatementRankSerializer.class)
	@JsonDeserialize(using = StatementRankDeserializer.class)
	private StatementRank rank;

	private List<ReferenceImpl> references;

	/**
	 * While this is called "claim" in the wdtk-datamodel, it is called
	 * "mainsnak" in the external JSON. There a claim is the entirety of
	 * statements in their respective groups.
	 */
	private SnakImpl mainsnak;

	private Map<String, List<SnakImpl>> qualifiers;

	// TODO set claim externally
	/**
	 * This is needed to satisfy the interface. Since from the JSON the subject
	 * of the claim can not be derived, after the deserialization the claim has
	 * to be derived by the statement group.
	 */
	@JsonIgnore
	private ClaimImpl claim;

	public StatementImpl() {
	}

	public StatementImpl(String id, SnakImpl mainsnak) {
		this.id = id;
		this.rank = StatementRank.NORMAL;
		this.mainsnak = mainsnak;
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
	public void setClaim(ClaimImpl claim){
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

	public void setReferences(List<ReferenceImpl> references) {
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

	public SnakImpl getMainsnak() {
		return this.mainsnak;
	}

	public void setMainsnak(SnakImpl mainsnak) {
		this.mainsnak = mainsnak;
	}

	public void setQualifiers(Map<String, List<SnakImpl>> qualifiers) {
		this.qualifiers = qualifiers;
	}

	public Map<String, List<SnakImpl>> getQualifiers() {
		return this.qualifiers;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof StatementImpl)) {
			return false;
		}
		StatementImpl other = (StatementImpl) o;

		if (!(this.id.equals(other.id) && this.rank.equals(other.rank) && this.mainsnak
				.equals(other.mainsnak))) {
			return false;
		}
		if (this.qualifiers != null
				&& !(this.qualifiers.equals(other.qualifiers))) {
			return false;
		}
		if (this.references != null
				&& !(this.references.equals(other.references))) {
			return false;
		}
		return true;
	}
}
