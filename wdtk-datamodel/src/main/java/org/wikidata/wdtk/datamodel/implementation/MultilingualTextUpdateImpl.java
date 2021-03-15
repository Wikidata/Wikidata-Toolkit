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

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.MultilingualTextUpdate;

/**
 * Jackson implementation of {@link MultilingualTextUpdate}.
 */
public class MultilingualTextUpdateImpl implements MultilingualTextUpdate {

	private final Map<String, MonolingualTextValue> modified;
	private final Set<String> removed;

	/**
	 * Initializes new multilingual text update.
	 * 
	 * @param modified
	 *            added or changed values
	 * @param removed
	 *            language codes of removed values
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	public MultilingualTextUpdateImpl(Collection<MonolingualTextValue> modified, Collection<String> removed) {
		this.modified = Collections.unmodifiableMap(modified.stream().collect(toMap(r -> r.getLanguageCode(), r -> r)));
		this.removed = Collections.unmodifiableSet(new HashSet<>(removed));
	}

	@Override
	public boolean isEmpty() {
		return modified.isEmpty() && removed.isEmpty();
	}

	@Override
	public Map<String, MonolingualTextValue> getModifiedValues() {
		return modified;
	}

	@Override
	public Set<String> getRemovedValues() {
		return removed;
	}

}
