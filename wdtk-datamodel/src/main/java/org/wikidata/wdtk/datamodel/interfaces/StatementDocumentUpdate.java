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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Collection of changes that can be applied to an entity that has statements.
 */
public interface StatementDocumentUpdate extends EntityUpdate {

	@Override
	StatementDocument getCurrentDocument();

	/**
	 * Returns statements added to the entity in this update.
	 * 
	 * @return list of added statements
	 */
	List<Statement> getAddedStatements();

	/**
	 * Returns entity statements modified in this update.
	 * 
	 * @return modified statements indexed by statement ID
	 */
	Map<String, Statement> getReplacedStatements();

	/**
	 * Returns IDs of statements removed from the entity in this update.
	 * 
	 * @return list of IDs of removed statements
	 */
	Set<String> getRemovedStatements();

}
