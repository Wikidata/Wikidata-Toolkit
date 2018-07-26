package org.wikidata.wdtk.wikibaseapi;

/*
 * #%L
 * Wikidata Toolkit Wikibase API
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.DatamodelMapper;
import org.wikidata.wdtk.datamodel.helpers.JsonSerializer;
import org.wikidata.wdtk.datamodel.implementation.StatementImpl;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;
import java.util.*;

/**
 * Class to plan a statement update operation.
 *
 * @author Markus Kroetzsch
 *
 */
public class StatementUpdate {

	static final Logger logger = LoggerFactory.getLogger(StatementUpdate.class);

	/**
	 * Helper class to store a statement together with the information of
	 * whether or not it is new (modified, not in current data) and therefore
	 * needs to be written.
	 *
	 * @author Markus Kroetzsch
	 *
	 */
	class StatementWithUpdate {
		public final Statement statement;
		public final boolean write;

		public StatementWithUpdate(Statement statement, boolean write) {
			this.statement = statement;
			this.write = write;
		}
	}
	
	/**
	 * Helper class to ease serialization of deleted statements. Jackson will
	 * serialize this class according to the format required by the API.
	 * 
	 * @author antonin
	 */
	class DeletedStatement implements Statement {
		
		private String id;
		
		public DeletedStatement(String id) {
			this.id = id;
		}

		@Override
		@JsonIgnore
		public Claim getClaim() {
			return null;
		}

		@Override
		@JsonIgnore
		public EntityIdValue getSubject() {
			return null;
		}

		@Override
		@JsonIgnore
		public Snak getMainSnak() {
			return null;
		}

		@Override
		@JsonIgnore
		public List<SnakGroup> getQualifiers() {
			return null;
		}

		@Override
		@JsonIgnore
		public Iterator<Snak> getAllQualifiers() {
			return null;
		}

		@Override
		@JsonIgnore
		public StatementRank getRank() {
			return null;
		}

		@Override
		@JsonIgnore
		public List<Reference> getReferences() {
			return null;
		}

		@Override
		@JsonProperty("id")
		public String getStatementId() {
			return id;
		}

		@Override
		@JsonIgnore
		public Value getValue() {
			return null;
		}
		
		@JsonProperty("remove")
		public String getRemoveCommand() {
			return "";
		}

		@Override
		public Statement withStatementId(String id) {
			return null;
		}
		
	}

	private GuidGenerator guidGenerator = new RandomGuidGenerator();
	private final ObjectMapper mapper;
	
	@JsonIgnore
	final HashMap<PropertyIdValue, List<StatementWithUpdate>> toKeep;
	@JsonIgnore
	final List<String> toDelete;
	@JsonIgnore
	StatementDocument currentDocument;

	/**
	 * Constructor. Marks the given lists of statements for being added to or
	 * deleted from the given document, respectively. The current content of the
	 * document is compared with the requested changes to avoid duplicates
	 * (merging references of duplicate statements), and to avoid deletions of
	 * statements that have changed or ceased to exist.
	 *
	 * @param currentDocument
	 *            the document with the current statements
	 * @param addStatements
	 *            the list of new statements to be added
	 * @param deleteStatements
	 *            the list of statements to be deleted
	 */
	public StatementUpdate(StatementDocument currentDocument,
			List<Statement> addStatements, List<Statement> deleteStatements) {
		this.currentDocument = currentDocument;
		this.toKeep = new HashMap<>();
		this.toDelete = new ArrayList<>();
		markStatementsForUpdate(currentDocument, addStatements,
				deleteStatements);
		this.mapper = new DatamodelMapper(currentDocument.getEntityId().getSiteIri());
	}

	/**
	 * Returns a JSON serialization of the marked insertions and deletions of
	 * statements, in the format required by the Wikibase "wbeditentity" action.
	 *
	 * @return JSON serialization of updates
	 */
	@JsonIgnore
	public String getJsonUpdateString() {
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return ("Failed to serialize statement update to JSON: " + e.toString());
		}
	}
	
	/**
	 * Performs the update, selecting the appropriate API action depending on
	 * the nature of the change.
	 * 
	 * @return the new document after update with the API
	 * @throws MediaWikiApiErrorException 
	 * @throws IOException 
	 */
	public StatementDocument performEdit(WbEditingAction action, boolean editAsBot, String summary)
			throws IOException, MediaWikiApiErrorException {
		if (isEmptyEdit()) {
			return currentDocument;
		} else if (toDelete.isEmpty() && getUpdatedStatements().size() == 1) {
			// we can use "wbsetclaim" because we only have one statement to change
			List<Statement> statements = getUpdatedStatements();
			Statement statement = statements.get(0);
			
			if (statement.getStatementId() == null || statement.getStatementId().isEmpty()) {
				statement = statement.withStatementId(guidGenerator.freshStatementId(currentDocument.getEntityId().getId()));

			}
			
			JsonNode response = action.wbSetClaim(
						JsonSerializer.getJsonString(statement), editAsBot,
						currentDocument.getRevisionId(), summary);
			
			StatementImpl.PreStatement preStatement = getDatamodelObjectFromResponse(response, Collections.singletonList("claim"), StatementImpl.PreStatement.class);
			Statement returnedStatement = preStatement.withSubject(statement.getClaim().getSubject());
			long revisionId = getRevisionIdFromResponse(response);
			
			return currentDocument.withStatement(returnedStatement).withRevisionId(revisionId);
		} else if (!toDelete.isEmpty() && getUpdatedStatements().size() == toDelete.size() && toDelete.size() <= 50) {
			// we can use "wbremoveclaims" because we are only removing statements
			
			JsonNode response = action.wbRemoveClaims(toDelete, editAsBot, currentDocument.getRevisionId(), summary);
			
			long revisionId = getRevisionIdFromResponse(response);
			
			return currentDocument.withoutStatementIds(new HashSet<>(toDelete)).withRevisionId(revisionId);
		} else {
			return (StatementDocument) action.wbEditEntity(currentDocument
				.getEntityId().getId(), null, null, null, getJsonUpdateString(),
				false, editAsBot, currentDocument
				.getRevisionId(), summary);
		}
	}
	
	@JsonProperty("claims")
	@JsonInclude(Include.NON_EMPTY)
	public List<Statement> getUpdatedStatements() {
		List<Statement> updatedStatements = new ArrayList<>();
		for (List<StatementWithUpdate> swus : toKeep.values()) {
			for (StatementWithUpdate swu : swus) {
				if (!swu.write) {
					continue;
				}
				updatedStatements.add(swu.statement);
			}
		}
		
		for (String id : toDelete) {
			updatedStatements.add(new DeletedStatement(id));
		}
		return updatedStatements;
	}
	
	/**
	 * Returns true when the edit is not going to change anything on the item.
	 * In this case, the change can be safely skipped, except if the side effects
	 * of a null edit are desired.
	 */
	@JsonIgnore
	public boolean isEmptyEdit() {
		return getUpdatedStatements().isEmpty();
	}

	/**
	 * Marks the given lists of statements for being added to or deleted from
	 * the given document, respectively. The current content of the document is
	 * compared with the requested changes to avoid duplicates (merging
	 * references of duplicate statements), and to avoid deletions of statements
	 * that have changed or ceased to exist.
	 *
	 * @param currentDocument
	 *            the document with the current statements
	 * @param addStatements
	 *            the list of new statements to be added
	 * @param deleteStatements
	 *            the list of statements to be deleted
	 */
	protected void markStatementsForUpdate(StatementDocument currentDocument,
			List<Statement> addStatements, List<Statement> deleteStatements) {
		markStatementsForDeletion(currentDocument, deleteStatements);
		markStatementsForInsertion(currentDocument, addStatements);
	}

	/**
	 * Marks the given list of statements for deletion. It is verified that the
	 * current document actually contains the statements before doing so. This
	 * check is based on exact statement equality, including qualifier order and
	 * statement id.
	 *
	 * @param currentDocument
	 *            the document with the current statements
	 * @param deleteStatements
	 *            the list of statements to be deleted
	 */
	protected void markStatementsForDeletion(StatementDocument currentDocument,
			List<Statement> deleteStatements) {
		for (Statement statement : deleteStatements) {
			boolean found = false;
			for (StatementGroup sg : currentDocument.getStatementGroups()) {
				if (!sg.getProperty().equals(statement.getMainSnak().getPropertyId())) {
					continue;
				}

				Statement changedStatement = null;
				for (Statement existingStatement : sg) {
					if (existingStatement.equals(statement)) {
						found = true;
						toDelete.add(statement.getStatementId());
					} else if (existingStatement.getStatementId().equals(
							statement.getStatementId())) {
						// (we assume all existing statement ids to be nonempty
						// here)
						changedStatement = existingStatement;
						break;
					}
				}

				if (!found) {
					StringBuilder warning = new StringBuilder();
					warning.append("Cannot delete statement (id ")
							.append(statement.getStatementId())
							.append(") since it is not present in data. Statement was:\n")
							.append(statement);

					if (changedStatement != null) {
						warning.append(
								"\nThe data contains another statement with the same id: maybe it has been edited? Other statement was:\n")
								.append(changedStatement);
					}
					logger.warn(warning.toString());
				}
			}
		}
	}

	/**
	 * Marks a given list of statements for insertion into the current document.
	 * Inserted statements can have an id if they should update an existing
	 * statement, or use an empty string as id if they should be added. The
	 * method removes duplicates and avoids unnecessary modifications by
	 * checking the current content of the given document before marking
	 * statements for being written.
	 *
	 * @param currentDocument
	 *            the document with the current statements
	 * @param addStatements
	 *            the list of new statements to be added
	 */
	protected void markStatementsForInsertion(
			StatementDocument currentDocument, List<Statement> addStatements) {
		for (Statement statement : addStatements) {
			addStatement(statement, true);
		}

		for (StatementGroup sg : currentDocument.getStatementGroups()) {
			if (this.toKeep.containsKey(sg.getProperty())) {
				for (Statement statement : sg) {
					if (!this.toDelete.contains(statement.getStatementId())) {
						addStatement(statement, false);
					}
				}
			}
		}
	}

	/**
	 * Adds one statement to the list of statements to be kept, possibly merging
	 * it with other statements to be kept if possible. When two existing
	 * statements are merged, one of them will be updated and the other will be
	 * marked for deletion.
	 *
	 * @param statement
	 *            statement to add
	 * @param isNew
	 *            if true, the statement should be marked for writing; if false,
	 *            the statement already exists in the current data and is only
	 *            added to remove duplicates and avoid unnecessary writes
	 */
	protected void addStatement(Statement statement, boolean isNew) {
		PropertyIdValue pid = statement.getMainSnak().getPropertyId();

		// This code maintains the following properties:
		// (1) the toKeep structure does not contain two statements with the
		// same statement id
		// (2) the toKeep structure does not contain two statements that can
		// be merged
		if (this.toKeep.containsKey(pid)) {
			List<StatementWithUpdate> statements = this.toKeep.get(pid);
			for (int i = 0; i < statements.size(); i++) {
				Statement currentStatement = statements.get(i).statement;
				boolean currentIsNew = statements.get(i).write;

				if (!"".equals(currentStatement.getStatementId())
						&& currentStatement.getStatementId().equals(
								statement.getStatementId())) {
					// Same, non-empty id: ignore existing statement as if
					// deleted
					return;
				}

				Statement newStatement = mergeStatements(statement,
						currentStatement);
				if (newStatement != null) {
					boolean writeNewStatement = (isNew || !newStatement
							.equals(statement))
							&& (currentIsNew || !newStatement
									.equals(currentStatement));
					// noWrite: (newS == statement && !isNew)
					// || (newS == cur && !curIsNew)
					// Write: (newS != statement || isNew )
					// && (newS != cur || curIsNew)

					statements.set(i, new StatementWithUpdate(newStatement,
							writeNewStatement));

					// Impossible with default merge code:
					// Kept here for future extensions that may choose to not
					// reuse this id.
					if (!"".equals(statement.getStatementId())
							&& !newStatement.getStatementId().equals(
									statement.getStatementId())) {
						this.toDelete.add(statement.getStatementId());
					}
					if (!"".equals(currentStatement.getStatementId())
							&& !newStatement.getStatementId().equals(
									currentStatement.getStatementId())) {
						this.toDelete.add(currentStatement.getStatementId());
					}
					return;
				}
			}

			statements.add(new StatementWithUpdate(statement, isNew));
		} else {
			List<StatementWithUpdate> statements = new ArrayList<>();
			statements.add(new StatementWithUpdate(statement, isNew));
			this.toKeep.put(pid, statements);
		}
	}

	/**
	 * Returns a statement obtained by merging two given statements, if
	 * possible, or null if the statements cannot be merged. Statements are
	 * merged if they contain the same claim, but possibly with qualifiers in a
	 * different order. The statements may have different ids, ranks, and
	 * references. References will be merged. Different ranks are supported if
	 * one of the statement uses {@link StatementRank#NORMAL}, and the rank of
	 * the other (non-normal) statement is used in this case; otherwise the
	 * statements will not merge. The first statement takes precedence for
	 * determining inessential details of the merger, such as the order of
	 * qualifiers.
	 *
	 * @param statement1
	 *            first statement
	 * @param statement2
	 *            second statement
	 * @return merged statement or null if merging is not possible
	 */
	private Statement mergeStatements(Statement statement1, Statement statement2) {
		if (!equivalentClaims(statement1.getClaim(), statement2.getClaim())) {
			return null;
		}

		StatementRank newRank = statement1.getRank();
		if (newRank == StatementRank.NORMAL) {
			newRank = statement2.getRank();
		} else if (statement2.getRank() != StatementRank.NORMAL
				&& newRank != statement2.getRank()) {
			return null;
		}

		String newStatementId = statement1.getStatementId();
		if ("".equals(newStatementId)) {
			newStatementId = statement2.getStatementId();
		}

		List<Reference> newReferences = mergeReferences(
				statement1.getReferences(), statement2.getReferences());

		return Datamodel.makeStatement(statement1.getClaim(), newReferences,
				newRank, newStatementId);
	}

	/**
	 * Merges two lists of references, eliminating duplicates in the process.
	 *
	 * @param references1
	 * @param references2
	 * @return merged list
	 */
	protected List<Reference> mergeReferences(
			List<? extends Reference> references1,
			List<? extends Reference> references2) {
		List<Reference> result = new ArrayList<>();
		for (Reference reference : references1) {
			addBestReferenceToList(reference, result);
		}
		for (Reference reference : references2) {
			addBestReferenceToList(reference, result);
		}
		return result;
	}

	protected void addBestReferenceToList(Reference reference,
			List<Reference> referenceList) {
		for (Reference existingReference : referenceList) {
			if (isSameSnakSet(existingReference.getAllSnaks(),
					reference.getAllSnaks())) {
				return;
			}
		}
		referenceList.add(reference);
	}

	/**
	 * Checks if two claims are equivalent in the sense that they have the same
	 * main snak and the same qualifiers, but possibly in a different order.
	 *
	 * @param claim1
	 * @param claim2
	 * @return true if claims are equivalent
	 */
	protected boolean equivalentClaims(Claim claim1, Claim claim2) {
		return claim1.getMainSnak().equals(claim2.getMainSnak())
				&& isSameSnakSet(claim1.getAllQualifiers(),
						claim2.getAllQualifiers());
	}

	/**
	 * Compares two sets of snaks, given by iterators. The method is optimised
	 * for short lists of snaks, as they are typically found in claims and
	 * references.
	 *
	 * @param snaks1
	 * @param snaks2
	 * @return true if the lists are equal
	 */
	protected boolean isSameSnakSet(Iterator<Snak> snaks1, Iterator<Snak> snaks2) {
		ArrayList<Snak> snakList1 = new ArrayList<>(5);
		while (snaks1.hasNext()) {
			snakList1.add(snaks1.next());
		}

		int snakCount2 = 0;
		while (snaks2.hasNext()) {
			snakCount2++;
			Snak snak2 = snaks2.next();
			boolean found = false;
			for (int i = 0; i < snakList1.size(); i++) {
				if (snak2.equals(snakList1.get(i))) {
					snakList1.set(i, null);
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}

		return snakCount2 == snakList1.size();
	}
	
	/**
	 * Sets the GUID generator for this statement update.
	 */
	public void setGuidGenerator(GuidGenerator generator) {
		guidGenerator = generator;
	}

	
	/**
	 * Extracts the last revision id from the JSON response returned
	 * by the API after an edit
	 * 
	 * @param response
	 * 		the response as returned by Mediawiki
	 * @return
	 * 		the new revision id of the edited entity
	 * @throws JsonMappingException 
	 */
	protected long getRevisionIdFromResponse(JsonNode response) throws JsonMappingException {
		if(response == null) {
			throw new JsonMappingException("API response is null");
		}
		JsonNode entity = null;
		if(response.has("entity")) {
			entity = response.path("entity");
		} else if(response.has("pageinfo")) {
			entity = response.path("pageinfo");
		} 
		if(entity != null && entity.has("lastrevid")) {
			return entity.path("lastrevid").asLong();
		}
		throw new JsonMappingException("The last revision id could not be found in API response");
	}
	
    /**
     * Extracts a particular data model instance from a JSON response
     * returned by MediaWiki. The location is described by a list of successive
     * fields to use, from the root to the target object.
     * 
     * @param response
     * 		the API response as returned by MediaWiki
     * @param path
     * 		a list of fields from the root to the target object
     * @return
     * 		the parsed POJO object
     * @throws JsonProcessingException 
     */
	protected <T> T getDatamodelObjectFromResponse(JsonNode response, List<String> path, Class<T> targetClass) throws JsonProcessingException {
		if(response == null) {
			throw new JsonMappingException("The API response is null");
		}
		JsonNode currentNode = response;
		for(String field : path) {
			if (!currentNode.has(field)) {
				throw new JsonMappingException("Field '"+field+"' not found in API response.");
			}
			currentNode = currentNode.path(field);
		}
		return mapper.treeToValue(currentNode, targetClass);
	}

}
