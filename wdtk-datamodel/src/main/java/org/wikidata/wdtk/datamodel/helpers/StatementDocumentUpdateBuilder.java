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

import java.util.Objects;

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
import org.wikidata.wdtk.datamodel.interfaces.StatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

/**
 * Builder for incremental construction of {@link StatementDocumentUpdate}
 * objects.
 * 
 * @see StatementUpdateBuilder
 */
public abstract class StatementDocumentUpdateBuilder extends EntityUpdateBuilder {

	private StatementUpdate statements = StatementUpdate.NULL;

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
			return LabeledStatementDocumentUpdateBuilder
					.forLabeledStatementDocument((LabeledStatementDocument) document);
		}
		throw new IllegalArgumentException("Unrecognized entity document type.");
	}

	@Override
	protected StatementDocument getCurrentDocument() {
		return (StatementDocument) super.getCurrentDocument();
	}

	/**
	 * Returns statement changes.
	 * 
	 * @return statement update, possibly empty
	 */
	protected StatementUpdate getStatements() {
		return statements;
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
	 * Updates entity statements. Any previous changes to statements are discarded.
	 * 
	 * @param update
	 *            statement update, possibly empty
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if replaced or removed statement is not present in current entity
	 *             revision (if available)
	 */
	public void updateStatements(StatementUpdate update) {
		Objects.requireNonNull(update, "Update cannot be null.");
		if (getCurrentDocument() != null) {
			for (Statement replaced : update.getReplacedStatements().values()) {
				if (!hadStatementId(replaced.getStatementId())) {
					throw new IllegalArgumentException("Replaced statement is not in the current revision.");
				}
			}
			for (String removed : update.getRemovedStatements()) {
				if (!hadStatementId(removed)) {
					throw new IllegalArgumentException("Removed statement is not in the current revision.");
				}
			}
		}
		statements = update;
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
