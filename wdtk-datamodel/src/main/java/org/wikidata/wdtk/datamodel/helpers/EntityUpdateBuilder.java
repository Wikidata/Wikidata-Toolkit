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

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityUpdate;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;

/**
 * Builder for incremental construction of {@link EntityUpdate} objects.
 */
public abstract class EntityUpdateBuilder {

	static DataObjectFactory factory = new DataObjectFactoryImpl();

	private final EntityIdValue entityId;
	private final EntityDocument currentDocument;

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
	protected EntityUpdateBuilder(EntityIdValue entityId) {
		Objects.requireNonNull(entityId, "Entity ID cannot be null.");
		Validate.isTrue(entityId.isValid(), "Entity ID must be valid.");
		this.entityId = entityId;
		currentDocument = null;
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
	protected EntityUpdateBuilder(EntityDocument document) {
		Objects.requireNonNull(document, "Entity document cannot be null.");
		Validate.isTrue(document.getEntityId().isValid(), "Entity ID must be valid.");
		entityId = document.getEntityId();
		currentDocument = document;
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
	 *             if {@code entityId} is of unrecognized type
	 */
	public static EntityUpdateBuilder forEntityId(EntityIdValue entityId) {
		return StatementDocumentUpdateBuilder.forEntityId(entityId);
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
	 *             if {@code document} is of unrecognized type
	 */
	public static EntityUpdateBuilder forEntityDocument(EntityDocument document) {
		return StatementDocumentUpdateBuilder.forEntityDocument(document);
	}

	/**
	 * Returns ID of the entity that is being updated.
	 * 
	 * @return ID of the updated entity
	 */
	protected EntityIdValue getEntityId() {
		return entityId;
	}

	/**
	 * Returns entity revision, upon which this update is built. If no entity
	 * revision was provided when this builder was constructed, this method returns
	 * {@code null}.
	 * 
	 * @return entity revision that is being updated
	 */
	protected EntityDocument getCurrentDocument() {
		return currentDocument;
	}

	/**
	 * Creates new {@link EntityUpdate} object with contents of this builder object.
	 * 
	 * @return constructed object
	 */
	public abstract EntityUpdate build();

}
