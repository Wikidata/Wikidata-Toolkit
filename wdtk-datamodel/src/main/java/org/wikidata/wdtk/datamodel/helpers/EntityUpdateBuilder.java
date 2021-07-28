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
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;

/**
 * Builder for incremental construction of {@link EntityUpdate} objects.
 */
public abstract class EntityUpdateBuilder {

	private final EntityIdValue entityId;
	private final long baseRevisionId;
	private final EntityDocument baseRevision;

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
	 *             if {@code entityId} is as placeholder ID
	 */
	protected EntityUpdateBuilder(EntityIdValue entityId, long revisionId) {
		Objects.requireNonNull(entityId, "Entity ID cannot be null.");
		Validate.isTrue(!entityId.isPlaceholder(), "Cannot create update for placeholder entity ID.");
		this.entityId = entityId;
		this.baseRevisionId = revisionId;
		baseRevision = null;
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
	 *             if {@code revision} has a placeholder ID
	 */
	protected EntityUpdateBuilder(EntityDocument revision) {
		Objects.requireNonNull(revision, "Base entity revision cannot be null.");
		Validate.isTrue(!revision.getEntityId().isPlaceholder(), "Cannot create update for placeholder entity ID.");
		entityId = revision.getEntityId();
		baseRevision = revision;
		baseRevisionId = baseRevision.getRevisionId();
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
	 *             if {@code entityId} is of unrecognized type or it's a placeholder
	 *             ID
	 */
	public static EntityUpdateBuilder forBaseRevisionId(EntityIdValue entityId, long revisionId) {
		return StatementDocumentUpdateBuilder.forBaseRevisionId(entityId, revisionId);
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
	 *             if {@code entityId} is of unrecognized type or it's a placeholder
	 *             ID
	 */
	public static EntityUpdateBuilder forEntityId(EntityIdValue entityId) {
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
	public static EntityUpdateBuilder forBaseRevision(EntityDocument revision) {
		Objects.requireNonNull(revision, "Base entity revision cannot be null.");
		if (revision instanceof StatementDocument) {
			return StatementDocumentUpdateBuilder.forBaseRevision((StatementDocument) revision);
		}
		throw new IllegalArgumentException("Unrecognized entity document type.");
	}

	/**
	 * Returns ID of the entity that is being updated.
	 * 
	 * @return ID of the updated entity
	 */
	EntityIdValue getEntityId() {
		return entityId;
	}

	/**
	 * Returns base entity revision, upon which this update is built. If no base
	 * revision was provided when this builder was constructed, this method returns
	 * {@code null}.
	 * 
	 * @return base entity revision that is being updated
	 */
	EntityDocument getBaseRevision() {
		return baseRevision;
	}

	/**
	 * Returns base entity revision ID, upon which this update is built. If no base
	 * revision nor base revision ID was provided when this builder was constructed,
	 * this method returns zero.
	 * 
	 * @return base entity revision ID
	 */
	long getBaseRevisionId() {
		return baseRevisionId;
	}

	/**
	 * Creates new {@link EntityUpdate} object with contents of this builder object.
	 * 
	 * @return constructed object
	 */
	public abstract EntityUpdate build();

}
