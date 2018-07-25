package org.wikidata.wdtk.datamodel.implementation;

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

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.util.NestedIterator;

import java.util.*;
import java.util.Map.Entry;

/**
 * Abstract Jackson implementation of {@link StatementDocument}.
 * You should not rely on it directly.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 * @author Thomas Pellissier Tanon
 *
 */
abstract class StatementDocumentImpl extends EntityDocumentImpl implements StatementDocument {

	/**
	 * This is what is called <i>claim</i> in the JSON model. It corresponds to
	 * the statement group in the WDTK model.
	 */
	protected final Map<String, List<Statement>> claims;

	/**
	 * Statement groups. This member is initialized when statements are
	 * accessed.
	 */
	private List<StatementGroup> statementGroups;

	/**
	 * Constructor.
	 *
	 * @param id
	 * 		the identifier of the subject of this document
	 * @param claims
	 * 		the statement groups contained in this document
	 * @param revisionId
	 * 		the id of the last revision of this document
	 */
	StatementDocumentImpl(
			EntityIdValue id,
			List<StatementGroup> claims,
			long revisionId) {
		super(id, revisionId);
		this.claims = new HashMap<>();
		if(claims != null) {
			for(StatementGroup group : claims) {
				EntityIdValue otherId = group.getSubject();
				otherId.getIri();
				Validate.isTrue(group.getSubject().equals(id), "Subject for the statement group and the document are different: "+otherId.toString()+" vs "+id.toString());
				this.claims.put(group.getProperty().getId(), group.getStatements());
			}
		}
	}
	
	/**
	 * Copy constructor.
	 * 	
	 * @param id
	 * @param claims
	 * @param revisionId
	 */
	protected StatementDocumentImpl(
			EntityIdValue id,
			Map<String, List<Statement>> claims,
			long revisionId) {
		super(id, revisionId);
		this.claims = claims;
	}

	/**
	 * Constructor used for JSON deserialization with Jackson.
	 */
	StatementDocumentImpl(
			@JsonProperty("id") String jsonId,
			@JsonProperty("claims") Map<String, List<StatementImpl.PreStatement>> claims,
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
		super(jsonId, revisionId, siteIri);
		if (claims != null) {
			this.claims = new HashMap<>();
			EntityIdValue subject = this.getEntityId();
			for (Entry<String, List<StatementImpl.PreStatement>> entry : claims
					.entrySet()) {
				List<Statement> statements = new ArrayList<>(entry.getValue().size());
				for (StatementImpl.PreStatement statement : entry.getValue()) {
					statements.add(statement.withSubject(subject));
				}
				this.claims.put(entry.getKey(), statements);
			}
		} else {
			this.claims = Collections.emptyMap();
		}
	}

	@JsonIgnore
	@Override
	public List<StatementGroup> getStatementGroups() {
		if (this.statementGroups == null) {
			this.statementGroups = new ArrayList<>(this.claims.size());
			for (List<Statement> statements : this.claims.values()) {
				this.statementGroups
						.add(new StatementGroupImpl(statements));
			}
		}
		return this.statementGroups;
	}
	
	/**
	 * Find a statement group by its property id, without checking for 
	 * equality with the site IRI. More efficient implementation than
	 * the default one.
	 */
	public StatementGroup findStatementGroup(String propertyIdValue) {
		if (this.claims.containsKey(propertyIdValue)) {
			return new StatementGroupImpl(this.claims.get(propertyIdValue));
		}
		return null;
	}

	/**
	 * Returns the "claims". Only used by Jackson.
	 * <p>
	 * JSON "claims" correspond to statement groups in the WDTK model. You
	 * should use {@link ItemDocumentImpl#getStatementGroups()} to obtain
	 * this data.
	 *
	 * @return map of statement groups
	 */
	@JsonProperty("claims")
	public Map<String, List<Statement>> getJsonClaims() {
		return this.claims;
	}

	@Override
	@JsonIgnore
	public Iterator<Statement> getAllStatements() {
		return new NestedIterator<>(getStatementGroups());
	}
	
	/**
	 * Adds a Statement to a given collection of statement groups.
	 * If the statement id is not null and matches that of an existing statement,
	 * this statement will be replaced.
	 * 
	 * @param statement
	 * @param claims
	 * @return
	 */
	protected static Map<String, List<Statement>> addStatementToGroups(Statement statement, Map<String, List<Statement>> claims) {
		Map<String, List<Statement>> newGroups = new HashMap<>(claims);
		String pid = statement.getMainSnak().getPropertyId().getId();
		if(newGroups.containsKey(pid)) {
			List<Statement> newGroup = new ArrayList<>(newGroups.get(pid).size());
			boolean statementReplaced = false;
			for(Statement existingStatement : newGroups.get(pid)) {
				if(existingStatement.getStatementId().equals(statement.getStatementId()) &&
						!existingStatement.getStatementId().isEmpty()) {
					statementReplaced = true;
					newGroup.add(statement);
				} else {
					newGroup.add(existingStatement);
				}
			}
			if(!statementReplaced) {
				newGroup.add(statement);
			}
			newGroups.put(pid, newGroup);
		} else {
			newGroups.put(pid, Collections.singletonList(statement));
		}
		return newGroups;
	}
	
	/**
	 * Removes statement ids from a collection of statement groups.
	 * @param statementIds
	 * @param claims
	 * @return
	 */
	protected static Map<String, List<Statement>> removeStatements(Set<String> statementIds, Map<String, List<Statement>> claims) {
		Map<String, List<Statement>> newClaims = new HashMap<>(claims.size());
		for(Entry<String, List<Statement>> entry : claims.entrySet()) {
			List<Statement> filteredStatements = new ArrayList<>();
			for(Statement s : entry.getValue()) {
				if(!statementIds.contains(s.getStatementId())) {
					filteredStatements.add(s);
				}
			}
			if(!filteredStatements.isEmpty()) {
				newClaims.put(entry.getKey(),
					filteredStatements);
			}
		}
		return newClaims;
	}
}
