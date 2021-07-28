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
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Jackson implementation of {@link EntityUpdate}.
 */
public abstract class EntityUpdateImpl implements EntityUpdate {

	@JsonIgnore
	private final EntityIdValue entityId;
	@JsonIgnore
	private final long revisionId;

	/**
	 * Initializes new entity update.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @param revisionId
	 *            base entity revision to be updated or zero if not available
	 * @throws NullPointerException
	 *             if {@code entityId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code entityId} is a placeholder ID or it does not match base
	 *             revision document ID (if provided)
	 */
	protected EntityUpdateImpl(EntityIdValue entityId, long revisionId) {
		Objects.requireNonNull(entityId, "Entity ID cannot be null.");
		Validate.isTrue(!entityId.isPlaceholder(), "Cannot create update for placeholder entity ID.");
		this.entityId = entityId;
		this.revisionId = revisionId;
	}

	@JsonIgnore
	@Override
	public EntityIdValue getEntityId() {
		return entityId;
	}

	@JsonIgnore
	@Override
	public long getBaseRevisionId() {
		return revisionId;
	}

}
