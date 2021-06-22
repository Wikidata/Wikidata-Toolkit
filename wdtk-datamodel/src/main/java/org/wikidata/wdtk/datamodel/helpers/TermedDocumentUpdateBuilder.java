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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.AliasUpdate;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocumentUpdate;

/**
 * Builder for incremental construction of {@link TermedStatementDocumentUpdate}
 * objects.
 */
public abstract class TermedDocumentUpdateBuilder extends LabeledDocumentUpdateBuilder {

	TermUpdate descriptions = TermUpdate.EMPTY;
	final Map<String, AliasUpdate> aliases = new HashMap<>();

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
	protected TermedDocumentUpdateBuilder(EntityIdValue entityId, long revisionId) {
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
	protected TermedDocumentUpdateBuilder(TermedStatementDocument revision) {
		super(revision);
	}

	/**
	 * Creates new builder object for constructing update of entity with given
	 * revision ID.
	 * <p>
	 * Supported entity IDs include {@link ItemIdValue} and {@link PropertyIdValue}.
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
	public static TermedDocumentUpdateBuilder forBaseRevisionId(EntityIdValue entityId, long revisionId) {
		Objects.requireNonNull(entityId, "Entity ID cannot be null.");
		if (entityId instanceof ItemIdValue) {
			return ItemUpdateBuilder.forBaseRevisionId((ItemIdValue) entityId, revisionId);
		}
		if (entityId instanceof PropertyIdValue) {
			return PropertyUpdateBuilder.forBaseRevisionId((PropertyIdValue) entityId, revisionId);
		}
		throw new IllegalArgumentException("Unrecognized entity ID type.");
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
	 *             if {@code entityId} is of unrecognized type or it is a
	 *             placeholder ID
	 */
	public static TermedDocumentUpdateBuilder forEntityId(EntityIdValue entityId) {
		return forBaseRevisionId(entityId, 0);
	}

	/**
	 * Creates new builder object for constructing update of given base entity
	 * revision. Provided entity document might not represent the latest revision of
	 * the entity as currently stored in Wikibase. It will be used for validation in
	 * builder methods. If the document has revision ID, it will be used to detect
	 * edit conflicts.
	 * <p>
	 * Supported entity types include {@link ItemDocument} and
	 * {@link PropertyDocument}.
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
	public static TermedDocumentUpdateBuilder forBaseRevision(TermedStatementDocument revision) {
		Objects.requireNonNull(revision, "Base entity revision cannot be null.");
		if (revision instanceof ItemDocument) {
			return ItemUpdateBuilder.forBaseRevision((ItemDocument) revision);
		}
		if (revision instanceof PropertyDocument) {
			return PropertyUpdateBuilder.forBaseRevision((PropertyDocument) revision);
		}
		throw new IllegalArgumentException("Unrecognized entity document type.");
	}

	@Override
	TermedStatementDocument getBaseRevision() {
		return (TermedStatementDocument) super.getBaseRevision();
	}

	@Override
	public TermedDocumentUpdateBuilder updateStatements(StatementUpdate update) {
		super.updateStatements(update);
		return this;
	}

	@Override
	public TermedDocumentUpdateBuilder updateLabels(TermUpdate update) {
		super.updateLabels(update);
		return this;
	}

	/**
	 * Updates entity descriptions. If this method is called multiple times, changes
	 * are accumulated. If base entity revision was provided, redundant changes are
	 * silently ignored, resulting in empty update.
	 * 
	 * @param update
	 *            changes in entity descriptions
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 */
	public TermedDocumentUpdateBuilder updateDescriptions(TermUpdate update) {
		Objects.requireNonNull(update, "Update cannot be null.");
		TermUpdateBuilder combined = getBaseRevision() != null
				? TermUpdateBuilder.forTerms(getBaseRevision().getDescriptions().values())
				: TermUpdateBuilder.create();
		combined.append(descriptions);
		combined.append(update);
		descriptions = combined.build();
		return this;
	}

	/**
	 * Updates entity aliases. If this method is called multiple times, changes are
	 * accumulated. If base entity revision was provided, the update is checked
	 * against it and redundant changes are silently ignored, resulting in empty
	 * update.
	 * 
	 * @param language
	 *            language code of the altered aliases
	 * @param update
	 *            alias changes
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code language} or {@code aliases} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code language} is blank or {@code aliases} has inconsistent
	 *             language code
	 */
	public TermedDocumentUpdateBuilder updateAliases(String language, AliasUpdate update) {
		Validate.notBlank(language, "Specify language code.");
		Objects.requireNonNull(update, "Alias update cannot be null.");
		if (update.getLanguageCode().isPresent()) {
			Validate.isTrue(language.equals(update.getLanguageCode().get()),
					"Alias update must have matching language code.");
		}
		AliasUpdateBuilder builder;
		if (getBaseRevision() != null) {
			builder = AliasUpdateBuilder
					.forAliases(getBaseRevision().getAliases().getOrDefault(language, Collections.emptyList()));
		} else {
			builder = AliasUpdateBuilder.create();
		}
		builder.append(aliases.getOrDefault(language, AliasUpdate.EMPTY));
		builder.append(update);
		AliasUpdate combined = builder.build();
		if (!combined.isEmpty()) {
			aliases.put(language, combined);
		} else {
			aliases.remove(language);
		}
		return this;
	}

	void append(TermedStatementDocumentUpdate update) {
		super.append(update);
		updateDescriptions(update.getDescriptions());
		for (Map.Entry<String, AliasUpdate> entry : update.getAliases().entrySet()) {
			updateAliases(entry.getKey(), entry.getValue());
		}
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
