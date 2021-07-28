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
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LabeledStatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.LabeledStatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;

/**
 * Builder for incremental construction of
 * {@link LabeledStatementDocumentUpdate} objects.
 */
public abstract class LabeledDocumentUpdateBuilder extends StatementDocumentUpdateBuilder {

	TermUpdate labels = TermUpdate.EMPTY;

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
	protected LabeledDocumentUpdateBuilder(EntityIdValue entityId, long revisionId) {
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
	protected LabeledDocumentUpdateBuilder(LabeledStatementDocument revision) {
		super(revision);
	}

	/**
	 * Creates new builder object for constructing update of entity with given
	 * revision ID.
	 * <p>
	 * Supported entity IDs include {@link ItemIdValue}, {@link PropertyIdValue},
	 * and {@link MediaInfoIdValue}.
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
	public static LabeledDocumentUpdateBuilder forBaseRevisionId(EntityIdValue entityId, long revisionId) {
		Objects.requireNonNull(entityId, "Entity ID cannot be null.");
		if (entityId instanceof MediaInfoIdValue) {
			return MediaInfoUpdateBuilder.forBaseRevisionId((MediaInfoIdValue) entityId, revisionId);
		}
		return TermedDocumentUpdateBuilder.forBaseRevisionId(entityId, revisionId);
	}

	/**
	 * Creates new builder object for constructing update of entity with given ID.
	 * <p>
	 * Supported entity IDs include {@link ItemIdValue}, {@link PropertyIdValue},
	 * and {@link MediaInfoIdValue}.
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
	public static LabeledDocumentUpdateBuilder forEntityId(EntityIdValue entityId) {
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
	 * {@link PropertyDocument}, and {@link MediaInfoDocument}.
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
	public static LabeledDocumentUpdateBuilder forBaseRevision(LabeledStatementDocument revision) {
		Objects.requireNonNull(revision, "Base entity revision cannot be null.");
		if (revision instanceof MediaInfoDocument) {
			return MediaInfoUpdateBuilder.forBaseRevision((MediaInfoDocument) revision);
		}
		if (revision instanceof TermedStatementDocument) {
			return TermedDocumentUpdateBuilder.forBaseRevision((TermedStatementDocument) revision);
		}
		throw new IllegalArgumentException("Unrecognized entity document type.");
	}

	@Override
	LabeledStatementDocument getBaseRevision() {
		return (LabeledStatementDocument) super.getBaseRevision();
	}

	@Override
	public LabeledDocumentUpdateBuilder updateStatements(StatementUpdate update) {
		super.updateStatements(update);
		return this;
	}

	/**
	 * Updates entity labels. If this method is called multiple times, changes are
	 * accumulated. If base entity revision was provided, redundant changes are
	 * silently ignored, resulting in empty update.
	 * 
	 * @param update
	 *            changes in entity labels
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 */
	public LabeledDocumentUpdateBuilder updateLabels(TermUpdate update) {
		Objects.requireNonNull(update, "Update cannot be null.");
		TermUpdateBuilder combined = getBaseRevision() != null
				? TermUpdateBuilder.forTerms(getBaseRevision().getLabels().values())
				: TermUpdateBuilder.create();
		combined.append(labels);
		combined.append(update);
		labels = combined.build();
		return this;
	}

	void append(LabeledStatementDocumentUpdate update) {
		super.append(update);
		updateLabels(update.getLabels());
	}

	/**
	 * Creates new {@link LabeledStatementDocumentUpdate} object with contents of
	 * this builder object.
	 * 
	 * @return constructed object
	 */
	@Override
	public abstract LabeledStatementDocumentUpdate build();

}
