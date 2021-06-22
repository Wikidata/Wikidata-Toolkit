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
import java.util.Optional;
import java.util.Set;

import org.wikidata.wdtk.datamodel.implementation.AliasUpdateImpl;

/**
 * Collection of changes made to entity aliases. This class represents changes
 * in single language only. Alias update consists either of added (see
 * {@link #getAdded()}) and removed (see {@link #getRemoved()}) aliases or a new
 * list of aliases that completely replace old aliases (see
 * {@link #getRecreated()}).
 */
public interface AliasUpdate {

	/**
	 * Empty update that does not alter or remove any aliases.
	 */
	AliasUpdate EMPTY = new AliasUpdateImpl(null, Collections.emptyList(), Collections.emptyList());

	/**
	 * Checks whether the update is empty. Empty update will not alter alias list in
	 * any way.
	 * 
	 * @return {@code true} if the update is empty, {@code false} otherwise
	 */
	boolean isEmpty();

	/**
	 * Returns language code of aliases in this update. Language code is only
	 * available for non-empty updates.
	 * 
	 * @return alias language code or {@link Optional#empty()} when the update is
	 *         empty
	 */
	Optional<String> getLanguageCode();

	/**
	 * Returns the new list of aliases that completely replaces current aliases. If
	 * this list is present, then the update contains no added/removed aliases.
	 * 
	 * @return new list of aliases or {@link Optional#empty()} if aliases are not
	 *         being recreated
	 */
	Optional<List<MonolingualTextValue>> getRecreated();

	/**
	 * Returns aliases added in this update. If there are any added aliases, then
	 * {@link #getRecreated()} must return {@link Optional#empty()}. It is however
	 * possible to add and remove aliases in the same update.
	 * 
	 * @return added aliases
	 */
	List<MonolingualTextValue> getAdded();

	/**
	 * Returns aliases removed in this update. If there are any removed aliases,
	 * then {@link #getRecreated()} must return {@link Optional#empty()}. It is
	 * however possible to add and remove aliases in the same update.
	 * 
	 * @return removed aliases
	 */
	Set<MonolingualTextValue> getRemoved();

}
