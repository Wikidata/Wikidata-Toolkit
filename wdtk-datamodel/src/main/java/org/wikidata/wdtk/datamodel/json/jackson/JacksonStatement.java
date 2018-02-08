package org.wikidata.wdtk.datamodel.json.jackson;

import java.util.HashMap;

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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Jackson implementation of {@link Statement}. In JSON, the corresponding
 * structures are referred to as "claim".
 * <p>
 * Like all Jackson objects, it is not technically immutable, but it is strongly
 * recommended to treat it as such in all contexts: the setters are for Jackson;
 * never call them in your code.
 *
 * @author Fredo Erxleben
 *
 */
public class JacksonStatement extends JacksonPreStatement implements Statement {
	
	/**
	 * The subject entity that this statement refers to. This is needed since it
	 * is not part of the JSON serialization of statements, but is needed in
	 * WDTK as part of {@link Claim}. Thus, it is necessary to set this
	 * information after each deserialization using
	 * {@link JacksonPreStatement#setSubject(EntityIdValue)}
	 * .
	 */
	@JsonIgnore
	private final EntityIdValue subject;
	
	/**
	 * Constructor for freshly created objects, not deserialized from JSON.
	 * @param id
	 * @param rank
	 * @param claim
	 * @param references
	 * @param subject
	 */
	public JacksonStatement(String id, StatementRank rank, Claim claim,
			List<Reference> references, EntityIdValue subject) {
		super(id, rank, claim.getMainSnak(),
				qualifierListToMap(claim.getQualifiers()),
				claim.getQualifiers().stream().map(g -> g.getProperty().getId()).collect(Collectors.toList()),
				references);
		Validate.notNull(subject);
		this.subject = subject;
	}

	/**
	 * Constructor.
	 */
	public JacksonStatement(
			String id,
			StatementRank rank,
			Snak mainsnak,
			List<SnakGroup> qualifiers,
			List<Reference> references,
			EntityIdValue subjectId) {
		super(id, rank, mainsnak,
				qualifiers.stream()
				.collect(Collectors.toMap(g -> g.getProperty().getId(), SnakGroup::getSnaks)),
				qualifiers.stream()
					.map(g -> g.getProperty().getId())
					.collect(Collectors.toList()),
				references);
		Validate.notNull(subjectId);
		this.subject = subjectId;
	}
	
	/**
	 * Constructor used for JSON deserialization with Jackson.
	 * Not marked as @JsonCreator because it is not called directly by Jackson,
	 * but rather from {@link JacksonTermedStatementDocument} to convert it
	 * from a {@link JacksonPreStatement}.
	 */
	public JacksonStatement(
			String id,
			StatementRank rank,
			Snak mainsnak,
			Map<String,List<Snak>> qualifiers,
			List<String> propertyOrder,
			List<Reference> references,
			EntityIdValue subjectId) {
		super(id, rank, mainsnak, qualifiers, propertyOrder, references);
		Validate.notNull(subjectId);
		this.subject = subjectId;
	}
	

	/**
	 * TODO review the utility of this constructor.
	 *
	 * @param id
	 * @param mainsnak
	 */
	public JacksonStatement(String id, Snak mainsnak, EntityIdValue subject) {
		super(id, StatementRank.NORMAL, mainsnak, null, null, null);
		this.subject = subject;
	}


	/**
	 * Returns the subject that this statement refers to. This method is only
	 * used by {@link ClaimFromJson} to retrieve data about the subject of this
	 * statement. To access this data from elsewhere, use {@link #getClaim()}.
	 *
	 * @see Claim#getSubject()
	 * @return the subject of this statement
	 */
	@JsonIgnore 
	public EntityIdValue getSubject() {
		return this.subject;
	}
	
	@JsonIgnore
	public Claim getClaim() {
		return new ClaimFromJson(this);
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
	 * Helper to convert a list of qualifiers to the internal JSON representation.
	 * @param qualifiers
	 * @return
	 */
	private static Map<String, List<Snak>> qualifierListToMap(List<SnakGroup> qualifiers) {
		Map<String, List<Snak>> map = new HashMap<>();
		for(SnakGroup group : qualifiers) {
			if(map.containsKey(group.getProperty().getId())) {
				throw new IllegalArgumentException("Attempting to create qualifiers with two snak groups for the same property");
			} else {
				map.put(group.getProperty().getId(), group.getSnaks());
			}
		}
		return map;
	}
}
