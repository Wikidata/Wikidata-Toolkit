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

import java.util.Map;

/**
 * Collection of changes that can be applied to an entity that has labels,
 * aliases, and descriptions.
 */
public interface TermedDocumentUpdate extends LabeledDocumentUpdate {

	/**
	 * Returns changes in entity descriptions.
	 * 
	 * @return update of entity descriptions, possibly empty
	 */
	TermUpdate getDescriptions();

	/**
	 * Returns changes in entity aliases. All {@link AliasUpdate} instances are
	 * non-empty. If language code is not in the returned map, aliases for that
	 * language do not change.
	 * 
	 * @return changes in aliases
	 */
	Map<String, AliasUpdate> getAliases();

}
