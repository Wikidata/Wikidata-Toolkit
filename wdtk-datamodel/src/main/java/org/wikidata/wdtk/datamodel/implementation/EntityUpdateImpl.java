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

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Jackson implementation of {@link EntityUpdate}.
 */
public abstract class EntityUpdateImpl implements EntityUpdate {

	private final EntityIdValue entityId;
	private final EntityDocument baseRevision;

	/**
	 * Initializes new entity update.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @param revision
	 *            base entity revision to be updated or {@code null} if not
	 *            available
	 * @throws NullPointerException
	 *             if {@code entityId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code entityId} is not a valid ID or it does not match base
	 *             revision document ID (if provided)
	 */
	protected EntityUpdateImpl(EntityIdValue entityId, EntityDocument revision) {
		Objects.requireNonNull(entityId, "Entity ID cannot be null.");
		Validate.isTrue(entityId.isValid(), "Entity ID must be valid.");
		if (revision != null) {
			Validate.isTrue(entityId.equals(revision.getEntityId()),
					"Entity ID must be the same as ID of the base revision document.");
		}
		this.entityId = entityId;
		baseRevision = revision;
	}

	@JsonIgnore
	@Override
	public EntityIdValue getEntityId() {
		return entityId;
	}

	String getId() {
		return entityId.getId();
	}

	@JsonIgnore
	@Override
	public EntityDocument getBaseRevision() {
		return baseRevision;
	}

}
