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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementUpdate;

/**
 * Builder for incremental construction of {@link TermedStatementUpdate}
 * objects.
 */
public abstract class TermedStatementUpdateBuilder extends LabeledStatementUpdateBuilder {

	private final Map<String, MonolingualTextValue> modifiedDescriptions = new HashMap<>();
	private final Set<String> removedDescriptions = new HashSet<>();

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
	protected TermedStatementUpdateBuilder(EntityIdValue entityId) {
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
	protected TermedStatementUpdateBuilder(TermedStatementDocument document) {
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
	public static TermedStatementUpdateBuilder forEntityId(EntityIdValue entityId) {
		Objects.requireNonNull(entityId, "Entity ID cannot be null.");
		if (entityId instanceof ItemIdValue) {
			return ItemUpdateBuilder.forItemId((ItemIdValue)entityId);
		}
		if (entityId instanceof PropertyIdValue) {
			return PropertyUpdateBuilder.forPropertyId((PropertyIdValue)entityId);
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
	public static TermedStatementUpdateBuilder forTermedStatementDocument(TermedStatementDocument document) {
		Objects.requireNonNull(document, "Entity document cannot be null.");
		if (document instanceof ItemDocument) {
			return ItemUpdateBuilder.forItemDocument((ItemDocument)document);
		}
		if (document instanceof PropertyDocument) {
			return PropertyUpdateBuilder.forPropertyDocument((PropertyDocument)document);
		}
		throw new IllegalArgumentException("Unrecognized entity document type.");
	}

	@Override
	protected TermedStatementDocument getCurrentDocument() {
		return (TermedStatementDocument) super.getCurrentDocument();
	}

	protected Map<String, MonolingualTextValue> getModifiedDescriptions() {
		return modifiedDescriptions;
	}

	protected Set<String> getRemovedDescriptions() {
		return removedDescriptions;
	}

	/**
	 * Adds or changes entity description. If there is no description for the
	 * language code, new description is added. If a description with this language
	 * code already exists, it is replaced. Descriptions with other language codes
	 * are not touched. Calling this method overrides any previous changes made with
	 * the same language code by this method or {@link #removeDescription(String)}.
	 * 
	 * @param description
	 *            entity description to add or change
	 * @throws NullPointerException
	 *             if {@code description} is {@code null}
	 */
	public void setDescription(MonolingualTextValue description) {
		Objects.requireNonNull(description, "Description cannot be null.");
		modifiedDescriptions.put(description.getLanguageCode(), description);
		removedDescriptions.remove(description.getLanguageCode());
	}

	/**
	 * Removes entity description. Descriptions with other language codes are not
	 * touched. Calling this method overrides any previous changes made with the
	 * same language code by this method or
	 * {@link #setDescription(MonolingualTextValue)}.
	 * 
	 * @param languageCode
	 *            language code of the removed entity description
	 * @throws NullPointerException
	 *             if {@code languageCode} is {@code null}
	 * @throws IllegalArgumentException
	 *             if the description is not present in current entity revision (if
	 *             available)
	 */
	public void removeDescription(String languageCode) {
		Objects.requireNonNull(languageCode, "Language code cannot be null.");
		if (getCurrentDocument() != null && !getCurrentDocument().getDescriptions().containsKey(languageCode)) {
			throw new IllegalArgumentException("Description with this language code is not in the current revision.");
		}
		removedDescriptions.add(languageCode);
		modifiedDescriptions.remove(languageCode);
	}

	/**
	 * Creates new {@link TermedStatementUpdate} object with contents of this
	 * builder object.
	 * 
	 * @return constructed object
	 */
	@Override
	public abstract TermedStatementUpdate build();

}
