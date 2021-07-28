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
package org.wikidata.wdtk.datamodel.helpers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.AliasUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

/**
 * Builder for incremental construction of {@link AliasUpdate} objects.
 */
public class AliasUpdateBuilder {

	private String languageCode;
	private final List<MonolingualTextValue> base;
	private List<MonolingualTextValue> recreated;
	private final List<MonolingualTextValue> added = new ArrayList<>();
	private final Set<MonolingualTextValue> removed = new HashSet<>();

	private AliasUpdateBuilder(List<MonolingualTextValue> base) {
		if (base != null) {
			for (MonolingualTextValue alias : base) {
				Objects.requireNonNull(alias, "Base document aliases cannot be null.");
			}
			Validate.isTrue(
					base.stream().map(v -> v.getLanguageCode()).distinct().count() <= 1,
					"Base document aliases must have the same language code.");
			Validate.isTrue(base.stream().distinct().count() == base.size(), "Base document aliases must be unique.");
			this.base = new ArrayList<>(base);
			languageCode = base.stream().map(v -> v.getLanguageCode()).findFirst().orElse(null);
		} else
			this.base = null;
	}

	/**
	 * Creates new builder object for constructing alias update.
	 * 
	 * @return update builder object
	 */
	public static AliasUpdateBuilder create() {
		return new AliasUpdateBuilder(null);
	}

	/**
	 * Creates new builder object for constructing update of given base revision
	 * aliases. Provided aliases will be used to check correctness of changes.
	 * <p>
	 * Since all changes will be checked after the {@link AliasUpdate} is passed to
	 * {@link TermedDocumentUpdateBuilder} anyway, it is usually unnecessary to use
	 * this method. It is simpler to initialize the builder with {@link #create()}.
	 * 
	 * @param aliases
	 *            aliases from base revision of the document
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code aliases} or any of its items is {@code null}
	 * @throws IllegalArgumentException
	 *             if there are duplicate items in {@code aliases}
	 */
	public static AliasUpdateBuilder forAliases(List<MonolingualTextValue> aliases) {
		Objects.requireNonNull(aliases, "Base document alias collection cannot be null.");
		return new AliasUpdateBuilder(aliases);
	}

	/**
	 * Adds new alias. This operation can be repeated to add multiple aliases in one
	 * update. It can be combined with {@link #remove(MonolingualTextValue)}.
	 * Attempt to add the same alias twice or to add alias already present in base
	 * document (if available) is silently ignored. Adding previously removed alias
	 * cancels the removal. If {@link #recreate(List)} was called before, this
	 * method will add the alias to the end of the new alias list.
	 * 
	 * @param alias
	 *            new alias
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code alias} is {@code null}
	 * @throws IllegalArgumentException
	 *             if the alias has language code inconsistent with other aliases
	 */
	public AliasUpdateBuilder add(MonolingualTextValue alias) {
		Objects.requireNonNull(alias, "Alias cannot be null.");
		if (languageCode != null) {
			Validate.isTrue(languageCode.equals(alias.getLanguageCode()), "Inconsistent language codes.");
		}
		if (recreated != null) {
			if (!recreated.contains(alias)) {
				recreated.add(alias);
				if (recreated.equals(base)) {
					recreated = null;
				}
			}
		} else if (removed.contains(alias)) {
			removed.remove(alias);
		} else if (!added.contains(alias) && (base == null || !base.contains(alias))) {
			added.add(alias);
		}
		languageCode = alias.getLanguageCode();
		return this;
	}

	/**
	 * Removed existing alias. This operation can be repeated to remove multiple
	 * aliases in one update. It can be combined with
	 * {@link #add(MonolingualTextValue)}. Attempt to remove the same alias twice or
	 * to remove alias not present in base document (if available) is silently
	 * ignored. Removing previously added alias cancels the addition. If
	 * {@link #recreate(List)} was called before, this method will remove the alias
	 * from the new alias list.
	 * 
	 * @param alias
	 *            removed alias
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code alias} is {@code null}
	 * @throws IllegalArgumentException
	 *             if the alias has language code inconsistent with other aliases
	 */
	public AliasUpdateBuilder remove(MonolingualTextValue alias) {
		Objects.requireNonNull(alias, "Alias cannot be null.");
		if (languageCode != null) {
			Validate.isTrue(languageCode.equals(alias.getLanguageCode()), "Inconsistent language codes.");
		}
		if (recreated != null) {
			recreated.remove(alias);
			if (recreated.equals(base)) {
				recreated = null;
			}
		} else if (added.contains(alias)) {
			added.remove(alias);
		} else if (!removed.contains(alias) && (base == null || base.contains(alias))) {
			removed.add(alias);
		}
		languageCode = alias.getLanguageCode();
		return this;
	}

	/**
	 * Replaces current alias list with completely new alias list. Any previous
	 * changes are discarded. To remove all aliases, pass empty list to this method.
	 * If the new alias list is identical (including order) to base document alias
	 * list (if provided), the update will be empty.
	 * 
	 * @param aliases
	 *            new list of aliases
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code aliases} or any of its items is {@code null}
	 * @throws IllegalArgumentException
	 *             if some alias has inconsistent language code or there are
	 *             duplicates
	 */
	public AliasUpdateBuilder recreate(List<MonolingualTextValue> aliases) {
		Objects.requireNonNull(aliases, "Alias list cannot be null.");
		for (MonolingualTextValue alias : aliases) {
			Objects.requireNonNull(alias, "Aliases cannot be null.");
		}
		Validate.isTrue(
				aliases.stream().map(v -> v.getLanguageCode()).distinct().count() <= 1,
				"Aliases must have the same language code.");
		Validate.isTrue(
				aliases.stream().map(v -> v.getText()).distinct().count() == aliases.size(),
				"All aliases must be unique.");
		if (languageCode != null && !aliases.isEmpty()) {
			Validate.isTrue(languageCode.equals(aliases.get(0).getLanguageCode()), "Inconsistent language codes.");
		}
		added.clear();
		removed.clear();
		if (!aliases.equals(base)) {
			recreated = new ArrayList<>(aliases);
		} else {
			recreated = null;
		}
		if (!aliases.isEmpty()) {
			languageCode = aliases.get(0).getLanguageCode();
		}
		return this;
	}

	/**
	 * Replays all changes in provided update into this builder object. Changes are
	 * performed as if by calling {@link #add(MonolingualTextValue)},
	 * {@link #remove(MonolingualTextValue)}, and {@link #recreate(List)} methods.
	 * 
	 * @param update
	 *            alias update to replay
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 */
	public AliasUpdateBuilder append(AliasUpdate update) {
		Objects.requireNonNull(update, "Alias update cannot be null.");
		update.getRecreated().ifPresent(this::recreate);
		for (MonolingualTextValue alias : update.getRemoved()) {
			remove(alias);
		}
		for (MonolingualTextValue alias : update.getAdded()) {
			add(alias);
		}
		return this;
	}

	/**
	 * Creates new {@link AliasUpdate} object with contents of this builder object.
	 * 
	 * @return constructed object
	 */
	public AliasUpdate build() {
		return Datamodel.makeAliasUpdate(recreated, added, removed);
	}

}
