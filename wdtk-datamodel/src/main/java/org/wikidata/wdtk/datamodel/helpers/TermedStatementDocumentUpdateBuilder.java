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
import org.wikidata.wdtk.datamodel.interfaces.MultilingualTextUpdate;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocumentUpdate;

/**
 * Builder for incremental construction of {@link TermedStatementDocumentUpdate}
 * objects.
 */
public abstract class TermedStatementDocumentUpdateBuilder extends LabeledStatementDocumentUpdateBuilder {

	private MultilingualTextUpdate descriptions = MultilingualTextUpdate.NULL;

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
	protected TermedStatementDocumentUpdateBuilder(EntityIdValue entityId) {
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
	protected TermedStatementDocumentUpdateBuilder(TermedStatementDocument document) {
		super(document);
	}

	/**
	 * Creates new builder object for constructing update of entity with given ID.
	 * <p>
	 * Supported entity IDs include {@link ItemIdValue} and {@link PropertyIdValue}.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @return builder object matching entity type
	 * @throws NullPointerException
	 *             if {@code entityId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code entityId} is of unrecognized type or it is not valid
	 */
	public static TermedStatementDocumentUpdateBuilder forEntityId(EntityIdValue entityId) {
		Objects.requireNonNull(entityId, "Entity ID cannot be null.");
		if (entityId instanceof ItemIdValue) {
			return ItemUpdateBuilder.forItemId((ItemIdValue) entityId);
		}
		if (entityId instanceof PropertyIdValue) {
			return PropertyUpdateBuilder.forPropertyId((PropertyIdValue) entityId);
		}
		throw new IllegalArgumentException("Unrecognized entity ID type.");
	}

	/**
	 * Creates new builder object for constructing update of given entity revision.
	 * Provided entity document might not represent the latest revision of the
	 * entity as currently stored in Wikibase. It will be used for validation in
	 * builder methods. If the document has revision ID, it will be used to detect
	 * edit conflicts.
	 * <p>
	 * Supported entity types include {@link ItemDocument} and
	 * {@link PropertyDocument}.
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
	public static TermedStatementDocumentUpdateBuilder forTermedStatementDocument(TermedStatementDocument document) {
		Objects.requireNonNull(document, "Entity document cannot be null.");
		if (document instanceof ItemDocument) {
			return ItemUpdateBuilder.forItemDocument((ItemDocument) document);
		}
		if (document instanceof PropertyDocument) {
			return PropertyUpdateBuilder.forPropertyDocument((PropertyDocument) document);
		}
		throw new IllegalArgumentException("Unrecognized entity document type.");
	}

	@Override
	protected TermedStatementDocument getCurrentDocument() {
		return (TermedStatementDocument) super.getCurrentDocument();
	}

	/**
	 * Returns description changes.
	 * 
	 * @return description update or {@code null} for no change
	 */
	protected MultilingualTextUpdate getDescriptions() {
		return descriptions;
	}

	@Override
	public TermedStatementDocumentUpdateBuilder updateStatements(StatementUpdate update) {
		super.updateStatements(update);
		return this;
	}

	@Override
	public TermedStatementDocumentUpdateBuilder updateLabels(MultilingualTextUpdate update) {
		super.updateLabels(update);
		return this;
	}

	/**
	 * Updates entity descriptions. Any previous changes to descriptions are
	 * discarded.
	 * 
	 * @param update
	 *            changes to entity descriptions
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if removed description is not present in current entity revision
	 *             (if available)
	 */
	public TermedStatementDocumentUpdateBuilder updateDescriptions(MultilingualTextUpdate update) {
		Objects.requireNonNull(update, "Update cannot be null.");
		if (getCurrentDocument() != null) {
			for (String removed : update.getRemovedValues()) {
				if (!getCurrentDocument().getDescriptions().containsKey(removed)) {
					throw new IllegalArgumentException("Removed description is not in the current revision.");
				}
			}
		}
		descriptions = update;
		return this;
	}

	/**
	 * Creates new {@link TermedStatementDocumentUpdate} object with contents of
	 * this builder object.
	 * 
	 * @return constructed object
	 */
	@Override
	public abstract TermedStatementDocumentUpdate build();

}
