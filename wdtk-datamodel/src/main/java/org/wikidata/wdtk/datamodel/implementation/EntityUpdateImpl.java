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
package org.wikidata.wdtk.datamodel.implementation;

import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityUpdate;

/**
 * Jackson implementation of {@link EntityUpdate}.
 */
public abstract class EntityUpdateImpl implements EntityUpdate {

	private final EntityIdValue entityId;
	private final EntityDocument currentDocument;

	/**
	 * Initializes new entity update.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @param document
	 *            entity revision to be updated or {@code null} if not available
	 * @throws NullPointerException
	 *             if {@code entityId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code entityId} is not a valid ID or it does not match
	 *             current revision document ID (if provided)
	 */
	protected EntityUpdateImpl(EntityIdValue entityId, EntityDocument document) {
		Objects.requireNonNull(entityId, "Entity ID cannot be null.");
		Validate.isTrue(entityId.isValid(), "Entity ID must be valid.");
		if (document != null) {
			Validate.isTrue(entityId.equals(document.getEntityId()),
					"Entity ID must be the same as ID of the current revision document.");
		}
		this.entityId = entityId;
		currentDocument = document;
	}

	@Override
	public EntityIdValue getEntityId() {
		return entityId;
	}

	@Override
	public EntityDocument getCurrentDocument() {
		return currentDocument;
	}

}
