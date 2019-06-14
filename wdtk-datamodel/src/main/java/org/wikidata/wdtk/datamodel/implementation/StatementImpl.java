package org.wikidata.wdtk.datamodel.implementation;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2018 Wikidata Toolkit Developers
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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.util.NestedIterator;

import java.io.IOException;
import java.util.*;

/**
 * Jackson implementation of {@link Statement}. In JSON, the corresponding
 * structures are referred to as "claim".
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 * @author Thomas Pellissier Tanon
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StatementImpl implements Statement {

	private final String statementId;

	private final StatementRank rank;

	private final Snak mainSnak;

	/**
	 * A map from property id strings to snaks that encodes the qualifiers.
	 */
	private final Map<String, List<Snak>> qualifiers;

	/**
	 * List of property string ids that encodes the desired order of qualifiers,
	 * which is not specified by the map.
	 */
	private final List<String> qualifiersOrder;

	private final EntityIdValue subjectId;

	private final List<Reference> references;

	private List<SnakGroup> qualifiersGroups;

	/**
	 * Constructor.
	 * <p>
	 * The statementId is used mainly for communication with a Wikibase site, in
	 * order to refer to statements of that site. When creating new statements
	 * that are not on any site, the empty string can be used.
	 *
	 * @param statementId
	 *            the string id of the Statement: can be empty if the statement has not obtained it yet
	 * @param rank
	 *            the rank of the Statement
	 * @param mainSnak
	 * 			  the main snak for the Claim of the Statement
	 * @param qualifiers
	 *            the snak groups for the qualifiers
	 * @param references
	 *            the references for the Statement
     * @param subjectId
     *            the subject of this Statement
	 */
	public StatementImpl(
			String statementId,
			StatementRank rank,
			Snak mainSnak,
			List<SnakGroup> qualifiers,
			List<Reference> references,
			EntityIdValue subjectId) {
		this.statementId = (statementId == null) ? "" : statementId;

		Validate.notNull(rank, "No rank provided to create a statement.");
		this.rank = rank;

		Validate.notNull(mainSnak, "No main snak provided to create a statement.");
		this.mainSnak = mainSnak;

		this.qualifiers = new HashMap<>();
		this.qualifiersOrder = new ArrayList<>();
		for(SnakGroup qualifiersGroup : qualifiers) {
			this.qualifiers.put(qualifiersGroup.getProperty().getId(), qualifiersGroup.getSnaks());
			this.qualifiersOrder.add(qualifiersGroup.getProperty().getId());
		}

		this.references = (references == null) ? Collections.emptyList() : references;
		Validate.notNull(subjectId);

		this.subjectId = subjectId;
	}

	public StatementImpl(
			String statementId,
			StatementRank rank,
			Snak mainSnak,
			Map<String,List<Snak>> qualifiers,
			List<String> qualifiersOrder,
			List<Reference> references,
			EntityIdValue subjectId) {
		this.statementId = (statementId == null) ? "" : statementId;
		Validate.notNull(rank, "No rank provided to create a statement.");
		this.rank = rank;
		Validate.notNull(mainSnak, "No main snak provided to create a statement.");
		this.mainSnak = mainSnak;
		this.qualifiers = (qualifiers == null) ? Collections.emptyMap() : qualifiers;
		this.qualifiersOrder = (qualifiersOrder == null) ? Collections.emptyList() : qualifiersOrder;
		this.references = (references == null) ? Collections.emptyList() : references;
		Validate.notNull(subjectId);
		this.subjectId = subjectId;
	}

	/**
	 * TODO review the utility of this constructor.
	 */
	public StatementImpl(String statementId, Snak mainsnak, EntityIdValue subjectId) {
		this(statementId, StatementRank.NORMAL, mainsnak, null, null, null, subjectId);
	}

	/**
	 * Returns the value for the "type" field used in JSON. Only for use by
	 * Jackson during deserialization.
	 *
	 * @return "statement"
	 */
	@JsonProperty("type")
	String getJsonType() {
		return "statement";
	}


	@Override
	@JsonIgnore
	public Claim getClaim() {
		return new ClaimImpl(this);
	}

	@Override
	@JsonIgnore
	public EntityIdValue getSubject() {
		return subjectId;
	}

	@Override
	@JsonProperty("mainsnak")
	public Snak getMainSnak() {
		return mainSnak;
	}

	@Override
	@JsonIgnore
	public List<SnakGroup> getQualifiers() {
		if (qualifiersGroups == null) {
			qualifiersGroups = SnakGroupImpl.makeSnakGroups(qualifiers, qualifiersOrder);
		}
		return qualifiersGroups;
	}

	@Override
	@JsonIgnore
	public Iterator<Snak> getAllQualifiers() {
		return new NestedIterator<>(getQualifiers());
	}

	/**
	 * Returns the qualifiers of the claim of this statement. Only for use by
	 * Jackson during serialization. To access this data, use
	 * {@link Statement#getQualifiers()}.
	 */
	@JsonProperty("qualifiers")
	Map<String, List<Snak>> getJsonQualifiers() {
		return Collections.unmodifiableMap(qualifiers);
	}

	/**
	 * Returns the list of property ids used to order qualifiers as found in
	 * JSON. Only for use by Jackson during serialization.
	 *
	 * @return the list of property ids
	 */
	@JsonProperty("qualifiers-order")
	List<String> getQualifiersOrder() {
		return Collections.unmodifiableList(this.qualifiersOrder);
	}

	@Override
	@JsonSerialize(using = StatementRankSerializer.class)
	public StatementRank getRank() {
		return rank;
	}

	@Override
	@JsonProperty("references")
	public List<Reference> getReferences() {
		return references;
	}

	@Override
	@JsonProperty("id")
	public String getStatementId() {
		return statementId;
	}

	@Override
	@JsonIgnore
	public Value getValue() {
		return mainSnak.getValue();
	}

	@Override
	public Statement withStatementId(String id) {
		return new StatementImpl(id,
				getRank(),
				getMainSnak(),
				getQualifiers(),
				getReferences(),
				getSubject());
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

	/**
	 * Helper class for deserializing statements from JSON.
	 */
	@JsonIgnoreProperties(ignoreUnknown=true)
	public
	static class PreStatement {

		private final String statementId;

		private final StatementRank rank;

		private final List<Reference> references;

		private final Snak mainSnak;

		private final Map<String, List<Snak>> qualifiers;

		private final List<String> qualifiersOrder;

		private PreStatement(
				String statementId,
				StatementRank rank,
				Snak mainsnak,
				Map<String, List<Snak>> qualifiers,
				List<String> qualifiersOrder,
				List<Reference> references) {
			this.statementId = statementId;
			this.rank = rank;
			this.mainSnak = mainsnak;
			this.qualifiers = qualifiers;
			this.qualifiersOrder = qualifiersOrder;
			this.references = references;
		}

		/**
		 * JSON deserialization creator.
		 */
		@JsonCreator
		static PreStatement fromJson(
				@JsonProperty("id") String id,
				@JsonProperty("rank") @JsonDeserialize(using = StatementRankDeserializer.class) StatementRank rank,
				@JsonProperty("mainsnak") SnakImpl mainsnak,
				@JsonProperty("qualifiers") Map<String, List<SnakImpl>> qualifiers,
				@JsonProperty("qualifiers-order") List<String> qualifiersOrder,
				@JsonProperty("references") @JsonDeserialize(contentAs=ReferenceImpl.class) List<Reference> references) {
			// Forget the concrete type of Jackson snaks for the qualifiers
			if(qualifiers == null) {
				qualifiers = Collections.emptyMap();
			}
			Map<String, List<Snak>> newQualifiers = new HashMap<>(qualifiers.size());
			for(Map.Entry<String,List<SnakImpl>> entry : qualifiers.entrySet()) {
				List<Snak> snaks = new ArrayList<>(entry.getValue());
				newQualifiers.put(entry.getKey(), snaks);
			}
			return new PreStatement(id, rank, mainsnak, newQualifiers, qualifiersOrder, references);
		}

		public StatementImpl withSubject(EntityIdValue subjectId) {
			return new StatementImpl(statementId, rank, mainSnak, qualifiers, qualifiersOrder, references, subjectId);
		}
	}


	/**
	 * A serializer implementation for the StatementRank enumeration. This is
	 * necessary since Java enumerations are in upper case but the Json counterpart
	 * is in lower case.
	 */
	static class StatementRankSerializer extends JsonSerializer<StatementRank> {

		@Override
		public void serialize(StatementRank value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
			jgen.writeString(value.name().toLowerCase());

		}
	}

	/**
	 * A deserializer implementation for the StatementRank enumeration. This is
	 * necessary since Java enumerations are in upper case but the Json counterpart
	 * is in lower case.
	 */
	static class StatementRankDeserializer extends JsonDeserializer<StatementRank> {

		@Override
		public StatementRank deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
			return StatementRank.valueOf(jp.getText().toUpperCase());
		}
	}
}
