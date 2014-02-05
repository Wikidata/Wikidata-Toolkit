package org.wikidata.wdtk.datamodel.interfaces;

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

import java.util.List;
import java.util.Map;

/**
 * Interface for datasets that describe an entity. This data mainly consists of
 * various pieces of text, such as labels, descriptions, and aliases. More
 * specific interfaces further add sitelinks and statements.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface EntityRecord {

	/**
	 * Return the ID of the entity that the data refers to
	 * 
	 * @return entity id
	 */
	EntityId getEntityId();

	/**
	 * Return a Map from Wikibase language codes to labels.
	 * 
	 * @return the map of labels
	 */
	Map<String, String> getLabels();

	/**
	 * Return a Map from Wikibase language codes to descriptions.
	 * 
	 * @return the map of descriptions
	 */
	Map<String, String> getDescriptions();

	/**
	 * Return a Map from Wikibase language codes to lists of alias labels for a
	 * given language.
	 * 
	 * @return the aliases for this language
	 */
	Map<String, List<String>> getAliases();
}
