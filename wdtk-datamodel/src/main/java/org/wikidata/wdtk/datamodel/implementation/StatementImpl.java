package org.wikidata.wdtk.datamodel.implementation;

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
import org.wikidata.wdtk.datamodel.implementation.json.JacksonPreStatement;
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
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 *
 */
public class StatementImpl extends JacksonPreStatement implements Statement {
	
	/**
	 * The subject entity that this statement refers to. This is needed since it
	 * is not part of the JSON serialization of statements, but is needed in
	 * WDTK as part of {@link Claim}. Thus, it is necessary to set this
	 * information after each deserialization in
	 * {@link JacksonTermedStatementDocument}.
	 */
	@JsonIgnore
	private final EntityIdValue subject;

	/**
	 * Constructor.
	 * <p>
	 * The string id is used mainly for communication with a Wikibase site, in
	 * order to refer to statements of that site. When creating new statements
	 * that are not on any site, the empty string can be used.
	 *
	 * @param statementId
	 *            the string id of the Statement: can be empty if the statement has not obtained it yet
	 * @param rank
	 *            the rank of the Statement
	 * @param mainsnak
	 * 			  the main snak for the Claim of the Statement
	 * @param qualifiers
	 *            the snak groups for the qualifiers
	 * @param references
	 *            the references for the Statement
     * @param subject
     *            the subject of this Statement
	 */
	public StatementImpl(
			String statementId,
			StatementRank rank,
			Snak mainsnak,
			List<SnakGroup> qualifiers,
			List<Reference> references,
			EntityIdValue subjectId) {
		super(statementId, rank, mainsnak,
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
	 * Constructor provided for compatibility with previous implementation.
	 * Note that constructing a {@link Statement} from an existing {@link Claim}
	 * is inefficient: the constructor above should be preferred instead.
	 * <p>
	 * The string id is used mainly for communication with a Wikibase site, in
	 * order to refer to statements of that site. When creating new statements
	 * that are not on any site, the empty string can be used.
	 *
	 * @param claim
	 *            the main claim the Statement refers to
	 * @param references
	 *            the references for the Statement
	 * @param rank
	 *            the rank of the Statement
	 * @param statementId
	 *            the string id of the Statement: can be empty if the statement has not obtained it yet
	 */
	@Deprecated
	public StatementImpl(
			Claim claim,
			List<Reference> references,
			StatementRank rank,
			String id) {
		super(id, rank, claim.getMainSnak(),
				qualifierListToMap(claim.getQualifiers()),
				claim.getQualifiers().stream().map(g -> g.getProperty().getId()).collect(Collectors.toList()),
				references);
		this.subject = claim.getSubject();
	}
	
	/**
	 * Constructor used for JSON deserialization with Jackson.
	 * Not marked as @JsonCreator because it is not called directly by Jackson,
	 * but rather from {@link TermedStatementDocumentImpl} to convert it
	 * from a {@link JacksonPreStatement}.
	 */
	public StatementImpl(
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
	public StatementImpl(String id, Snak mainsnak, EntityIdValue subject) {
		super(id, StatementRank.NORMAL, mainsnak, null, null, null);
		this.subject = subject;
	}


	/**
	 * Returns the subject that this statement refers to. This method is only
	 * used by {@link ClaimImpl} to retrieve data about the subject of this
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
		return new ClaimImpl(this);
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
