package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

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

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class represents the inner anonymous object in the JSON type of
 * "wikibase-entityid". Not to be confused with the entityId of the main
 * document.
 *
 * @author Fredo Erxleben
 *
 */
public class EntityId {
	// TODO replace IllegalArgumentException with a checked one
	// NOTE make sure to adapt all methods, once more types then only
	// "item" are supported

	public EntityId() {
	}

	/**
	 * The only known entity type so far is "item". In the future "property"
	 * might also be available.
	 * 
	 * @param entityType
	 *            (case-sensitive)
	 * @param numericId
	 * @throws IllegalArgumentException
	 *             if the entityType was unrecognized
	 */
	public EntityId(String entityType, int numericId)
			throws IllegalArgumentException {
		if (!entityType.equals("item")) {
			throw new IllegalArgumentException(
					"Unknown type given for EntityId");
		}
		this.entityType = entityType;
		this.numericId = numericId;
	}

	@JsonProperty("entity-type")
	private String entityType;

	@JsonProperty("numeric-id")
	private int numericId; // TODO maybe better use a long?

	@JsonProperty("entity-type")
	public String getEntityType() {
		return entityType;
	}

	@JsonProperty("entity-type")
	public void setEntityType(String entityType)
			throws IllegalArgumentException {
		if (!entityType.equals("item")) {
			throw new IllegalArgumentException(
					"Unknown type given for EntityId");
		}
		this.entityType = entityType;
	}

	@JsonProperty("numeric-id")
	public int getNumericId() {
		return numericId;
	}

	@JsonProperty("numeric-id")
	public void setNumericId(int numericId) {
		this.numericId = numericId;
	}

	/**
	 * Returns the standard string version of the entity id encoded in this
	 * value. For example, an id with entityType "item" and numericId "42" is
	 * normally identified as "Q42".
	 *
	 * @return the string id
	 */
	@JsonIgnore
	public String getStringId() {
		return "Q" + this.numericId;
	}

	/**
	 * Returns the entity type used in the datamodel. For example, the JSON
	 * entityType "item" corresponds to {@link EntityIdValue#ET_ITEM}.
	 *
	 * @return the entity type
	 */
	@JsonIgnore
	public String getDatamodelEntityType() {
		return EntityIdValue.ET_ITEM;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof EntityId)) {
			return false;
		}

		return this.numericId == ((EntityId) o).numericId;
	}

	@Override
	public String toString() {
		return this.getStringId();
	}
}
