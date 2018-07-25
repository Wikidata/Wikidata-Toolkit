package org.wikidata.wdtk.datamodel.implementation;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2018 Wikidata Toolkit Developers
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

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityRedirectDocument;

/**
 * Implementation of {@link EntityRedirectDocument}
 *
 * @author Thomas Pellissier Tanon
 *
 */
public class EntityRedirectDocumentImpl implements EntityRedirectDocument  {

	private final EntityIdValue entityId;
	private final EntityIdValue targetId;
	private final long revisionId;

	/**
	 * Constructor.
	 *
	 * @param id
	 * 		the identifier of the subject of this document
	 * @param targetId
	 * 		the identifier of the entity this document redirect to
	 * @param revisionId
	 * 		the id of the last revision of this document
	 */
	EntityRedirectDocumentImpl(EntityIdValue id, EntityIdValue targetId, long revisionId) {
		Validate.notNull(id);
		this.entityId = id;
		Validate.notNull(targetId);
		Validate.isTrue(id.getEntityType().equals(targetId.getEntityType()), "You could only do redirects between entities of the same type");
		this.targetId = targetId;
		this.revisionId = revisionId;
	}

	/**
	 * Constructor used for JSON deserialization with Jackson.
	 */
	@JsonCreator
	EntityRedirectDocumentImpl(
			@JsonProperty("entity") String jsonId,
			@JsonProperty("redirect") String jsonTargetId,
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
		this.entityId = EntityIdValueImpl.fromId(jsonId, siteIri);
		Validate.notNull(jsonTargetId);
		this.targetId = EntityIdValueImpl.fromId(jsonTargetId, siteIri);
		Validate.isTrue(getEntityId().getEntityType().equals(targetId.getEntityType()), "You could only do redirects between entities of the same type");
		this.revisionId = revisionId;
	}

	@JsonIgnore
	@Override
	public EntityIdValue getEntityId() {
		return entityId;
	}

	@JsonProperty("entity")
	String getEntityJson() {
		return entityId.getId();
	}

	@JsonIgnore
	@Override
	public long getRevisionId() {
		return revisionId;
	}

	@Override
	public EntityRedirectDocument withRevisionId(long newRevisionId) {
		return new EntityRedirectDocumentImpl(entityId, targetId, newRevisionId);
	}

	@JsonIgnore
	@Override
	public EntityIdValue getTargetId() {
		return targetId;
	}

	@JsonProperty("redirect")
	String getTargetJson() {
		return targetId.getId();
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsEntityRedirectDocument(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
