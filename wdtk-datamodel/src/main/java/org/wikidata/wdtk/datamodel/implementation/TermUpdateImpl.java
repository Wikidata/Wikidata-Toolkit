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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Jackson implementation of {@link TermUpdate}.
 */
public class TermUpdateImpl implements TermUpdate {

	@JsonIgnore
	private final Map<String, MonolingualTextValue> modified;
	@JsonIgnore
	private final Set<String> removed;

	/**
	 * Initializes new term update.
	 * 
	 * @param modified
	 *            added or changed terms
	 * @param removed
	 *            language codes of removed terms
	 * @throws NullPointerException
	 *             if any required parameter or its item is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	public TermUpdateImpl(Collection<MonolingualTextValue> modified, Collection<String> removed) {
		Objects.requireNonNull(modified, "Collection of modified terms cannot be null.");
		Objects.requireNonNull(removed, "Collection of removed terms cannot be null.");
		for (MonolingualTextValue value : modified) {
			Objects.requireNonNull(value, "Modified term cannot be null.");
		}
		for (String language : removed) {
			Validate.notBlank(language, "Language code must be a non-blank string.");
		}
		long distinct = Stream.concat(removed.stream(), modified.stream().map(v -> v.getLanguageCode())).distinct().count();
		Validate.isTrue(distinct == modified.size() + removed.size(), "Every term must have unique language code.");
		this.modified = Collections.unmodifiableMap(modified.stream()
				.map(TermImpl::new)
				.collect(toMap(v -> v.getLanguageCode(), r -> r)));
		this.removed = Collections.unmodifiableSet(new HashSet<>(removed));
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return modified.isEmpty() && removed.isEmpty();
	}

	@JsonIgnore
	@Override
	public Map<String, MonolingualTextValue> getModified() {
		return modified;
	}

	@JsonIgnore
	@Override
	public Set<String> getRemoved() {
		return removed;
	}

	static class RemovedTerm {

		private final String language;

		RemovedTerm(String language) {
			this.language = language;
		}

		@JsonProperty
		String getLanguage() {
			return language;
		}

		@JsonProperty("remove")
		String getRemoveCommand() {
			return "";
		}

	}

	@JsonValue
	Map<String, Object> toJson() {
		Map<String, Object> map = new HashMap<>();
		for (MonolingualTextValue value : modified.values()) {
			map.put(value.getLanguageCode(), value);
		}
		for (String language : removed) {
			map.put(language, new RemovedTerm(language));
		}
		return map;
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsTermUpdate(this, obj);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

}
