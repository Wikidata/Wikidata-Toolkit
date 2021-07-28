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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.interfaces.AliasUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Jackson implementation of {@link AliasUpdate}.
 */
public class AliasUpdateImpl implements AliasUpdate {

	@JsonIgnore
	private final String languageCode;
	@JsonIgnore
	private final List<MonolingualTextValue> recreated;
	private final List<MonolingualTextValue> added;
	private final Set<MonolingualTextValue> removed;

	/**
	 * Initializes new alias update. This update applies to aliases in one language
	 * only. Callers should specify either {@code recreated} parameter or
	 * {@code added} and {@code removed} parameters, because combination of the two
	 * update approaches is not possible. To remove all aliases, pass empty list in
	 * {@code recreated} parameter.
	 * 
	 * @param recreated
	 *            new list of aliases that completely replaces the old ones or
	 *            {@code null} to not recreate aliases
	 * @param added
	 *            aliases added in this update or empty collection for no additions
	 * @param removed
	 *            aliases removed in this update or empty collection for no removals
	 * @throws NullPointerException
	 *             if {@code added}, {@code removed}, or any alias is {@code null}
	 * @throws IllegalArgumentException
	 *             if given invalid combination of parameters
	 */
	public AliasUpdateImpl(List<MonolingualTextValue> recreated, List<MonolingualTextValue> added,
			Collection<MonolingualTextValue> removed) {
		Objects.requireNonNull(added, "List of added aliases cannot be null.");
		Objects.requireNonNull(removed, "List of removed aliases cannot be null.");
		Validate.isTrue(recreated == null || added.isEmpty() && removed.isEmpty(),
				"Cannot combine additions/removals with recreating the alias list.");
		List<MonolingualTextValue> all = new ArrayList<>();
		if (recreated != null) {
			all.addAll(recreated);
		}
		all.addAll(added);
		all.addAll(removed);
		for (MonolingualTextValue alias : all) {
			Validate.notNull(alias, "Alias object cannot be null.");
		}
		Validate.isTrue(all.stream().map(v -> v.getLanguageCode()).distinct().count() <= 1,
				"Inconsistent language codes.");
		if (recreated != null) {
			Validate.isTrue(recreated.stream().distinct().count() == recreated.size(),
					"Every alias in the new list of aliases must be unique.");
		}
		Validate.isTrue(added.stream().distinct().count() == added.size(),
				"Every new alias must be unique.");
		Validate.isTrue(removed.stream().distinct().count() == removed.size(),
				"Every removed alias must be unique.");
		Validate.isTrue(all.stream().distinct().count() == all.size(),
				"Cannot add and remove the same alias.");
		languageCode = all.stream().map(v -> v.getLanguageCode()).findFirst().orElse(null);
		this.recreated = recreated != null
				? Collections.unmodifiableList(recreated.stream().map(TermImpl::new).collect(toList()))
				: null;
		this.added = Collections.unmodifiableList(added.stream().map(AddedAlias::new).collect(toList()));
		this.removed = Collections.unmodifiableSet(removed.stream().map(RemovedAlias::new).collect(toSet()));
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return recreated == null && added.isEmpty() && removed.isEmpty();
	}

	@JsonIgnore
	@Override
	public Optional<String> getLanguageCode() {
		return Optional.ofNullable(languageCode);
	}

	@JsonIgnore
	@Override
	public Optional<List<MonolingualTextValue>> getRecreated() {
		return Optional.ofNullable(recreated);
	}

	@JsonIgnore
	@Override
	public List<MonolingualTextValue> getAdded() {
		return added;
	}

	@JsonIgnore
	@Override
	public Set<MonolingualTextValue> getRemoved() {
		return removed;
	}

	static class AddedAlias extends TermImpl {

		AddedAlias(MonolingualTextValue alias) {
			super(alias);
		}

		@JsonProperty("add")
		String getAddCommand() {
			return "";
		}

	}

	static class RemovedAlias extends TermImpl {

		RemovedAlias(MonolingualTextValue alias) {
			super(alias);
		}

		@JsonProperty("remove")
		String getRemoveCommand() {
			return "";
		}

	}

	@JsonValue
	List<Object> toJson() {
		List<Object> items = new ArrayList<>();
		if (recreated != null) {
			items.addAll(recreated);
		}
		items.addAll(removed);
		items.addAll(added);
		if (items.isEmpty() && recreated == null) {
			return null;
		}
		return items;
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsAliasUpdate(this, obj);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

}
