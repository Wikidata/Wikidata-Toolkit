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
import java.util.Map;
import java.util.Set;

import org.wikidata.wdtk.datamodel.implementation.TermUpdateImpl;

/**
 * Collection of changes made to terms (labels, descriptions, ...).
 */
public interface TermUpdate {

	/**
	 * Empty update that does not alter or remove any terms.
	 */
	TermUpdate EMPTY = new TermUpdateImpl(Collections.emptyList(), Collections.emptyList());

	/**
	 * Checks whether the update is empty. Empty update will not change or remove
	 * any terms.
	 * 
	 * @return {@code true} if the update is empty, {@code false} otherwise
	 */
	boolean isEmpty();

	/**
	 * Returns terms added or modified in this update. Existing terms are preserved
	 * if their language code is not listed here.
	 * 
	 * @return added or modified terms indexed by language code
	 */
	Map<String, MonolingualTextValue> getModified();

	/**
	 * Returns language codes of terms removed in this update.
	 * 
	 * @return language codes of removed terms
	 */
	Set<String> getRemoved();

}
