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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.Value;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Helper class storing a statement as represented in JSON. This
 * is a Statement missing a subject.
 * 
 * @author antonin
 *
 */
@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonPreStatement {

	/**
	 * Id of this statement.
	 *
	 * @see Statement#getStatementId()
	 */
	private String id;

	/**
	 * Rank of this statement.
	 */
	@JsonSerialize(using = StatementRankSerializer.class)
	@JsonDeserialize(using = StatementRankDeserializer.class)
	private StatementRank rank;

	private List<JacksonReference> references;

	/**
	 * The main snak of this statement.
	 */
	private JacksonSnak mainsnak;

	/**
	 * A map from property id strings to snaks that encodes the qualifiers.
	 */
	private Map<String, List<JacksonSnak>> qualifiers;
	/**
	 * List of property string ids that encodes the desired order of qualifiers,
	 * which is not specified by the map.
	 */
	private List<String> propertyOrder;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	@JsonCreator
	public JacksonPreStatement(
			@JsonProperty("id") String id,
			@JsonProperty("rank") StatementRank rank,
			@JsonProperty("mainsnak") JacksonSnak mainsnak,
			@JsonProperty("qualifiers") Map<String, List<JacksonSnak>> qualifiers,
			@JsonProperty("qualifiers-order") List<String> propertyOrder,
			@JsonProperty("references") List<JacksonReference> references) {
		this.id = id;
		Validate.notNull(rank, "No rank provided to create a statement.");
		this.rank = rank;
		Validate.notNull(mainsnak, "No main snak provided to create a statement.");
		this.mainsnak = mainsnak;
		if (qualifiers != null) {
			this.qualifiers = qualifiers;
		} else {
			this.qualifiers = Collections.<String, List<JacksonSnak>>emptyMap();
		}
		if (propertyOrder != null) {
			this.propertyOrder = propertyOrder;
		} else {
			this.propertyOrder = Collections.<String>emptyList();
		}
		if (references != null) {
			this.references = references;
		} else {
			this.references = Collections.<JacksonReference>emptyList();
		}
	}
	
	public JacksonStatement withSubject(EntityIdValue subject) {
		return new JacksonStatement(
				id, rank, mainsnak, qualifiers, propertyOrder, references, subject);
	}

	/**
	 * Returns the value for the "type" field used in JSON. Only for use by
	 * Jackson during deserialization.
	 *
	 * @return "statement"
	 */
	@JsonProperty("type")
	public String getJsonType() {
		return "statement";
	}

	/**
	 * Returns the rank of the statement.
	 * @return "rank"
	 */
	public StatementRank getRank() {
		return this.rank;
	}

	/**
	 * Returns the references of this statement
	 * @return "references"
	 */
	public List<JacksonReference> getReferences() {
		return this.references;
	}

	/**
	 * Returns the statement identifier.
	 * @return "id"
	 */
	@JsonProperty("id")
	public String getStatementId() {
		return this.id;
	}

	/**
	 * Returns the main snak of the claim of this statement. Only for use by
	 * Jackson during serialization. To access this data, use
	 * {@link #getClaim()}.
	 *
	 * @return main snak
	 */
	public JacksonSnak getMainsnak() {
		return this.mainsnak;
	}

	/**
	 * Returns the qualifiers of the claim of this statement. Only for use by
	 * Jackson during serialization. To access this data, use
	 * {@link #getClaim()}.
	 *
	 * @return qualifiers
	 */
	public Map<String, List<JacksonSnak>> getQualifiers() {
		return this.qualifiers;
	}

	/**
	 * Returns the list of property ids used to order qualifiers as found in
	 * JSON. Only for use by Jackson during serialization.
	 *
	 * @return the list of property ids
	 */
	@JsonProperty("qualifiers-order")
	public List<String> getPropertyOrder() {
		return this.propertyOrder;
	}

	@JsonIgnore
	public Value getValue() {
		return this.mainsnak.getValue();
	}

}
