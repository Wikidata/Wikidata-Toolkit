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
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

/**
 * Builder for incremental construction of {@link StatementDocumentUpdate}
 * objects.
 * 
 * @see StatementUpdateBuilder
 */
public abstract class StatementDocumentUpdateBuilder extends EntityUpdateBuilder {

	StatementUpdate statements = StatementUpdate.EMPTY;

	/**
	 * Initializes new builder object for constructing update of entity with given
	 * ID.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @param revisionId
	 *            ID of the base entity revision to be updated or zero if not
	 *            available
	 * @throws NullPointerException
	 *             if {@code entityId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code entityId} is a placeholder ID
	 */
	protected StatementDocumentUpdateBuilder(EntityIdValue entityId, long revisionId) {
		super(entityId, revisionId);
	}

	/**
	 * Initializes new builder object for constructing update of given base entity
	 * revision.
	 * 
	 * @param revision
	 *            base entity revision to be updated
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} has placeholder ID
	 */
	protected StatementDocumentUpdateBuilder(StatementDocument revision) {
		super(revision);
	}

	/**
	 * Creates new builder object for constructing update of entity with given
	 * revision ID.
	 * <p>
	 * Supported entity IDs include {@link ItemIdValue}, {@link PropertyIdValue},
	 * {@link LexemeIdValue}, {@link FormIdValue}, {@link SenseIdValue}, and
	 * {@link MediaInfoIdValue}.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @param revisionId
	 *            ID of the base entity revision to be updated or zero if not
	 *            available
	 * @return builder object matching entity type
	 * @throws NullPointerException
	 *             if {@code entityId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code entityId} is of unrecognized type or it is a
	 *             placeholder ID
	 */
	public static StatementDocumentUpdateBuilder forBaseRevisionId(EntityIdValue entityId, long revisionId) {
		Objects.requireNonNull(entityId, "Entity ID cannot be null.");
		if (entityId instanceof SenseIdValue) {
			return SenseUpdateBuilder.forBaseRevisionId((SenseIdValue) entityId, revisionId);
		}
		if (entityId instanceof FormIdValue) {
			return FormUpdateBuilder.forBaseRevisionId((FormIdValue) entityId, revisionId);
		}
		if (entityId instanceof LexemeIdValue) {
			return LexemeUpdateBuilder.forBaseRevisionId((LexemeIdValue) entityId, revisionId);
		}
		return LabeledDocumentUpdateBuilder.forBaseRevisionId(entityId, revisionId);
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
	 *             if {@code entityId} is of unrecognized type or it is a
	 *             placeholder ID
	 */
	public static StatementDocumentUpdateBuilder forEntityId(EntityIdValue entityId) {
		return forBaseRevisionId(entityId, 0);
	}

	/**
	 * Creates new builder object for constructing update of given base entity
	 * revision. Provided entity document might not represent the latest revision of
	 * the entity as currently stored in Wikibase. It will be used for validation in
	 * builder methods. If the document has revision ID, it will be used to detect
	 * edit conflicts.
	 * <p>
	 * Supported entity types include {@link ItemDocument},
	 * {@link PropertyDocument}, {@link LexemeDocument}, {@link FormDocument},
	 * {@link SenseDocument}, and {@link MediaInfoDocument}.
	 * 
	 * @param revision
	 *            base entity revision to be updated
	 * @return builder object matching entity type
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} is of unrecognized type or its ID is a
	 *             placeholder ID
	 */
	public static StatementDocumentUpdateBuilder forBaseRevision(StatementDocument revision) {
		Objects.requireNonNull(revision, "Base entity revision cannot be null.");
		if (revision instanceof SenseDocument) {
			return SenseUpdateBuilder.forBaseRevision((SenseDocument) revision);
		}
		if (revision instanceof FormDocument) {
			return FormUpdateBuilder.forBaseRevision((FormDocument) revision);
		}
		if (revision instanceof LexemeDocument) {
			return LexemeUpdateBuilder.forBaseRevision((LexemeDocument) revision);
		}
		if (revision instanceof LabeledStatementDocument) {
			return LabeledDocumentUpdateBuilder
					.forBaseRevision((LabeledStatementDocument) revision);
		}
		throw new IllegalArgumentException("Unrecognized entity document type.");
	}

	@Override
	StatementDocument getBaseRevision() {
		return (StatementDocument) super.getBaseRevision();
	}

	/**
	 * Updates entity statements. If this method is called multiple times, changes
	 * are accumulated. If base entity revision was provided, the update is checked
	 * against it and redundant changes are silently ignored, resulting in empty
	 * update.
	 * 
	 * @param update
	 *            statement update, possibly empty
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if replaced or removed statement is not present in current entity
	 *             revision (if available)
	 */
	public StatementDocumentUpdateBuilder updateStatements(StatementUpdate update) {
		Objects.requireNonNull(update, "Update cannot be null.");
		StatementUpdateBuilder combined = getBaseRevision() != null
				? StatementUpdateBuilder.forStatementGroups(getEntityId(), getBaseRevision().getStatementGroups())
				: StatementUpdateBuilder.create(getEntityId());
		combined.append(statements);
		combined.append(update);
		statements = combined.build();
		return this;
	}

	void append(StatementDocumentUpdate update) {
		Objects.requireNonNull(update, "Update cannot be null.");
		updateStatements(update.getStatements());
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
