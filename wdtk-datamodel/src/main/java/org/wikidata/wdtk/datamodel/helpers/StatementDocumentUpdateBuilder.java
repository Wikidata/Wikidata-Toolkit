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
package org.wikidata.wdtk.datamodel.helpers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LabeledStatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocumentUpdate;

/**
 * Builder for incremental construction of {@link StatementDocumentUpdate}
 * objects.
 */
public abstract class StatementDocumentUpdateBuilder extends EntityUpdateBuilder {

	private final List<Statement> addedStatements = new ArrayList<>();
	private final Map<String, Statement> replacedStatements = new HashMap<>();
	private final Set<String> removedStatements = new HashSet<>();

	/**
	 * Initializes new builder object for constructing update of entity with given
	 * ID.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @throws NullPointerException
	 *             if {@code entityId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code entityId} is not a valid ID
	 */
	protected StatementDocumentUpdateBuilder(EntityIdValue entityId) {
		super(entityId);
	}

	/**
	 * Initializes new builder object for constructing update of given entity
	 * revision.
	 * 
	 * @param document
	 *            entity revision to be updated
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} does not have valid ID
	 */
	protected StatementDocumentUpdateBuilder(StatementDocument document) {
		super(document);
	}

	/**
	 * Creates new builder object for constructing update of entity with given ID.
	 * <p>
	 * Supported entity IDs include {@link ItemIdValue}, {@link PropertyIdValue},
	 * {@link LexemeIdValue}, {@link FormIdValue}, {@link SenseIdValue}, and
	 * {@link MediaInfoIdValue}.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @return builder object matching entity type
	 * @throws NullPointerException
	 *             if {@code entityId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code entityId} is of unrecognized type or it is not valid
	 */
	public static StatementDocumentUpdateBuilder forEntityId(EntityIdValue entityId) {
		Objects.requireNonNull(entityId, "Entity ID cannot be null.");
		if (entityId instanceof SenseIdValue) {
			return SenseUpdateBuilder.forSenseId((SenseIdValue) entityId);
		}
		if (entityId instanceof FormIdValue) {
			return FormUpdateBuilder.forFormId((FormIdValue) entityId);
		}
		if (entityId instanceof LexemeIdValue) {
			return LexemeUpdateBuilder.forLexemeId((LexemeIdValue) entityId);
		}
		return LabeledStatementDocumentUpdateBuilder.forEntityId(entityId);
	}

	/**
	 * Creates new builder object for constructing update of given entity revision.
	 * Provided entity document might not represent the latest revision of the
	 * entity as currently stored in Wikibase. It will be used for validation in
	 * builder methods. If the document has revision ID, it will be used to detect
	 * edit conflicts.
	 * <p>
	 * Supported entity types include {@link ItemDocument},
	 * {@link PropertyDocument}, {@link LexemeDocument}, {@link FormDocument},
	 * {@link SenseDocument}, and {@link MediaInfoDocument}.
	 * 
	 * @param document
	 *            entity revision to be updated
	 * @return builder object matching entity type
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} is of unrecognized type or it does not have
	 *             valid ID
	 */
	public static StatementDocumentUpdateBuilder forStatementDocument(StatementDocument document) {
		Objects.requireNonNull(document, "Entity document cannot be null.");
		if (document instanceof SenseDocument) {
			return SenseUpdateBuilder.forSenseDocument((SenseDocument) document);
		}
		if (document instanceof FormDocument) {
			return FormUpdateBuilder.forFormDocument((FormDocument) document);
		}
		if (document instanceof LexemeDocument) {
			return LexemeUpdateBuilder.forLexemeDocument((LexemeDocument) document);
		}
		if (document instanceof LabeledStatementDocument) {
			return LabeledStatementDocumentUpdateBuilder.forLabeledStatementDocument((LabeledStatementDocument) document);
		}
		throw new IllegalArgumentException("Unrecognized entity document type.");
	}

	@Override
	protected StatementDocument getCurrentDocument() {
		return (StatementDocument) super.getCurrentDocument();
	}

	/**
	 * Returns collection of added statements.
	 * 
	 * @return added statements
	 */
	protected Collection<Statement> getAddedStatements() {
		return addedStatements;
	}

	/**
	 * Returns collection of replaced statements.
	 * 
	 * @return replaced statements
	 */
	protected Collection<Statement> getReplacedStatements() {
		return replacedStatements.values();
	}

	/**
	 * Returns collection of removed statement IDs.
	 * 
	 * @return removed statement IDs
	 */
	protected Collection<String> getRemovedStatements() {
		return removedStatements;
	}

	/**
	 * Adds statement to the entity. If {@code statement} has an ID (perhaps because
	 * it is a modified copy of another statement), its ID is stripped to ensure the
	 * statement is added and no other statement is modified.
	 * 
	 * @param statement
	 *            new statement to add
	 * @throws NullPointerException
	 *             if {@code statement} is {@code null}
	 */
	public void addStatement(Statement statement) {
		Objects.requireNonNull(statement, "Statement cannot be null.");
		if (!statement.getStatementId().isEmpty()) {
			statement = statement.withStatementId("");
		}
		addedStatements.add(statement);
	}

	private boolean hadStatementId(String statementId) {
		for (StatementGroup group : getCurrentDocument().getStatementGroups()) {
			for (Statement statement : group.getStatements()) {
				if (statement.getStatementId().equals(statementId)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Replaces existing statement in the entity. Provided {@code statement} must
	 * have statement ID identifying statement to replace. Calling this method
	 * overrides any previous changes made to the same statement ID by this method
	 * or {@link #removeStatement(String)}.
	 * 
	 * @param statement
	 *            replacement for existing statement
	 * @throws NullPointerException
	 *             if {@code statement} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code statement} does not have statement ID of if such ID
	 *             does not exist in current version of the document (if available)
	 */
	public void replaceStatement(Statement statement) {
		Objects.requireNonNull(statement, "Statement cannot be null.");
		Validate.notEmpty(statement.getStatementId(), "Statement must have an ID.");
		if (getCurrentDocument() != null && !hadStatementId(statement.getStatementId())) {
			throw new IllegalArgumentException("Statement with this ID is not in the current revision.");
		}
		replacedStatements.put(statement.getStatementId(), statement);
		removedStatements.remove(statement.getStatementId());
	}

	/**
	 * Removes existing statement from the entity. Calling this method overrides any
	 * previous changes made to the same statement ID by
	 * {@link #replaceStatement(Statement)}. Removing the same statement ID twice is
	 * silently tolerated.
	 * 
	 * @param statementId
	 *            ID of the removed statement
	 * @throws IllegalArgumentException
	 *             if {@code statementId} is empty or if such ID does not exist in
	 *             current version of the document (if available)
	 */
	public void removeStatement(String statementId) {
		Validate.notEmpty(statementId, "Statement ID must not be empty.");
		if (getCurrentDocument() != null && !hadStatementId(statementId)) {
			throw new IllegalArgumentException("Statement with this ID is not in the current revision.");
		}
		removedStatements.add(statementId);
		replacedStatements.remove(statementId);
	}

	/**
	 * Creates new {@link StatementDocumentUpdate} object with contents of this
	 * builder object.
	 * 
	 * @return constructed object
	 */
	@Override
	public abstract StatementDocumentUpdate build();

}
