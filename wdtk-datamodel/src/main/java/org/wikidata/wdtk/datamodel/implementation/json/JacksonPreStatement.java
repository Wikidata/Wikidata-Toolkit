package org.wikidata.wdtk.datamodel.implementation.json;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.implementation.ReferenceImpl;
import org.wikidata.wdtk.datamodel.implementation.SnakImpl;
import org.wikidata.wdtk.datamodel.implementation.StatementImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
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
 * @author Antonin Delpeuch
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
	private final String id;

	/**
	 * Rank of this statement.
	 */
	@JsonSerialize(using = StatementRankSerializer.class)
	@JsonDeserialize(using = StatementRankDeserializer.class)
	private final StatementRank rank;

	@JsonDeserialize(contentAs=ReferenceImpl.class)
	private final List<Reference> references;

	/**
	 * The main snak of this statement.
	 */
	@JsonDeserialize(contentAs=SnakImpl.class)
	private final Snak mainsnak;

	/**
	 * A map from property id strings to snaks that encodes the qualifiers.
	 * We use the explicit type {@link SnakImpl} because Jackson does
	 * not know yet how to specify subclasses for interfaces nested in two
	 * containers.
	 */
	@JsonIgnore
	private final Map<String, List<Snak>> qualifiers;
	
	/**
	 * List of property string ids that encodes the desired order of qualifiers,
	 * which is not specified by the map.
	 */
	private final List<String> propertyOrder;

	/**
	 * Constructor.
	 */
	public JacksonPreStatement(
			String id,
			StatementRank rank,
			Snak mainsnak,
			Map<String, List<Snak>> qualifiers,
			List<String> propertyOrder,
			List<Reference> references) {
		if (id == null) {
			id = "";
		}
		this.id = id;
		Validate.notNull(rank, "No rank provided to create a statement.");
		this.rank = rank;
		Validate.notNull(mainsnak, "No main snak provided to create a statement.");
		this.mainsnak = mainsnak;
		if (qualifiers != null) {
			this.qualifiers = qualifiers;
		} else {
			this.qualifiers = Collections.<String, List<Snak>>emptyMap();
		}
		if (propertyOrder != null) {
			this.propertyOrder = propertyOrder;
		} else {
			this.propertyOrder = Collections.<String>emptyList();
		}
		if (references != null) {
			this.references = references;
		} else {
			this.references = Collections.<Reference>emptyList();
		}
	}
	
	/**
	 * JSON deserialization creator.
	 */
	@JsonCreator
	protected static JacksonPreStatement fromJson(
			@JsonProperty("id") String id,
			@JsonProperty("rank") StatementRank rank,
			@JsonProperty("mainsnak") SnakImpl mainsnak,
			@JsonProperty("qualifiers") Map<String, List<SnakImpl>> qualifiers,
			@JsonProperty("qualifiers-order") List<String> propertyOrder,
			@JsonProperty("references") List<Reference> references) {
		// Forget the concrete type of Jackson snaks for the qualifiers
		if(qualifiers == null) {
			qualifiers = Collections.emptyMap();
		}
		Map<String, List<Snak>> newQualifiers = new HashMap<>(qualifiers.size());
		for(Map.Entry<String,List<SnakImpl>> entry : qualifiers.entrySet()) {
			List<Snak> snaks = entry.getValue().stream().collect(Collectors.toList());
			newQualifiers.put(entry.getKey(), snaks);
		}
		return new JacksonPreStatement(id, rank, mainsnak, newQualifiers, propertyOrder, references);
	}
	
	
	public StatementImpl withSubject(EntityIdValue subject) {
		return new StatementImpl(
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
	@JsonProperty("rank")
	public StatementRank getRank() {
		return this.rank;
	}

	/**
	 * Returns the references of this statement
	 * @return "references"
	 */
	@JsonProperty("references")
	public List<Reference> getReferences() {
		return Collections.unmodifiableList(this.references);
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
	@JsonProperty("mainsnak")
	public Snak getMainsnak() {
		return this.mainsnak;
	}

	/**
	 * Returns the qualifiers of the claim of this statement. Only for use by
	 * Jackson during serialization. To access this data, use
	 * {@link #getClaim()}.
	 *
	 * @return qualifiers
	 */
	@JsonProperty("qualifiers")
	public Map<String, List<Snak>> getQualifiers() {
		return Collections.unmodifiableMap(this.qualifiers);
	}

	/**
	 * Returns the list of property ids used to order qualifiers as found in
	 * JSON. Only for use by Jackson during serialization.
	 *
	 * @return the list of property ids
	 */
	@JsonProperty("qualifiers-order")
	public List<String> getPropertyOrder() {
		return Collections.unmodifiableList(this.propertyOrder);
	}

	@JsonIgnore
	public Value getValue() {
		return this.mainsnak.getValue();
	}

}
