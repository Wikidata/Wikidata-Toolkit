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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wikidata.wdtk.datamodel.implementation.StatementUpdateImpl;

/**
 * Collection of statement changes.
 * 
 * @see StatementDocumentUpdate
 */
public interface StatementUpdate {

	/**
	 * Empty update that does not alter or add any statements.
	 */
	StatementUpdate EMPTY = new StatementUpdateImpl(
			Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

	/**
	 * Checks whether the update is empty. Empty update will not change or remove
	 * any statements.
	 * 
	 * @return {@code true} if the update is empty, {@code false} otherwise
	 */
	boolean isEmpty();

	/**
	 * Returns statements added to the entity in this update.
	 * 
	 * @return list of added statements
	 */
	List<Statement> getAdded();

	/**
	 * Returns entity statements modified in this update.
	 * 
	 * @return modified statements indexed by statement ID
	 */
	Map<String, Statement> getReplaced();

	/**
	 * Returns IDs of statements removed from the entity in this update.
	 * 
	 * @return list of IDs of removed statements
	 */
	Set<String> getRemoved();

}
