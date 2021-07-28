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
package org.wikidata.wdtk.datamodel.interfaces;

/**
 * Collection of changes that can be applied to an entity via Wikibase API.
 */
public interface EntityUpdate {

	/**
	 * Returns ID of the entity that is being updated.
	 * 
	 * @return ID of the updated entity
	 */
	EntityIdValue getEntityId();

	/**
	 * Returns entity revision, upon which this update is built. This might not be
	 * the latest revision of the entity as currently stored in Wikibase. If base
	 * revision was not provided, this method returns zero.
	 * 
	 * @return entity revision that is being updated or zero
	 */
	long getBaseRevisionId();

	/**
	 * Checks whether the update is empty. Empty update will not change the entity
	 * in any way.
	 * 
	 * @return {@code true} if the update is empty, {@code false} otherwise
	 */
	boolean isEmpty();

}
