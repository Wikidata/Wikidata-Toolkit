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

import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

/**
 * Builder for incremental construction of {@link TermUpdate} objects.
 */
public class TermUpdateBuilder {

	private final Map<String, MonolingualTextValue> base;
	private final Map<String, MonolingualTextValue> modified = new HashMap<>();
	private final Set<String> removed = new HashSet<>();

	private TermUpdateBuilder(Collection<MonolingualTextValue> base) {
		if (base != null) {
			for (MonolingualTextValue value : base) {
				Objects.requireNonNull(value, "Base document terms cannot be null.");
			}
			Validate.isTrue(
					base.stream().map(v -> v.getLanguageCode()).distinct().count() == base.size(),
					"Base document terms must have unique language codes.");
			this.base = base.stream().collect(toMap(v -> v.getLanguageCode(), v -> v));
		} else
			this.base = null;
	}

	/**
	 * Creates new builder object for constructing term update.
	 * 
	 * @return update builder object
	 */
	public static TermUpdateBuilder create() {
		return new TermUpdateBuilder(null);
	}

	/**
	 * Creates new builder object for constructing update of given base revision
	 * terms. Provided terms will be used to check correctness of changes.
	 * <p>
	 * Since all changes will be checked after the {@link TermUpdate} is passed to
	 * {@link EntityUpdateBuilder} anyway, it is usually unnecessary to use this
	 * method. It is simpler to initialize the builder with {@link #create()}.
	 * 
	 * @param terms
	 *            terms from base revision of the document
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code terms} or any of its items is {@code null}
	 * @throws IllegalArgumentException
	 *             if there are duplicate items in {@code terms}
	 */
	public static TermUpdateBuilder forTerms(Collection<MonolingualTextValue> terms) {
		Objects.requireNonNull(terms, "Base document term collection cannot be null.");
		return new TermUpdateBuilder(terms);
	}

	/**
	 * Adds or changes term. If a term with this language code already exists, it is
	 * replaced. Terms with other language codes are not touched. Calling this
	 * method overrides any previous changes made with the same language code by
	 * this method or {@link #remove(String)}.
	 * <p>
	 * If base revision terms were provided, attempt to overwrite some term with the
	 * same value will be silently ignored, resulting in empty update.
	 * 
	 * @param term
	 *            term to add or change
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code term} is {@code null}
	 */
	public TermUpdateBuilder put(MonolingualTextValue term) {
		Objects.requireNonNull(term, "Term cannot be null.");
		if (base != null) {
			if (term.equals(base.get(term.getLanguageCode()))) {
				modified.remove(term.getLanguageCode());
				removed.remove(term.getLanguageCode());
				return this;
			}
		}
		modified.put(term.getLanguageCode(), term);
		removed.remove(term.getLanguageCode());
		return this;
	}

	/**
	 * Removes term. Terms with other language codes are not touched. Calling this
	 * method overrides any previous changes made with the same language code by
	 * this method or {@link #put(MonolingualTextValue)}.
	 * <p>
	 * If base revision terms were provided, attempts to remove missing terms will
	 * be silently ignored, resulting in empty update.
	 * 
	 * @param languageCode
	 *            language code of the removed term
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code languageCode} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code languageCode} is blank
	 */
	public TermUpdateBuilder remove(String languageCode) {
		Validate.notBlank(languageCode, "Language code must be provided.");
		if (base != null && !base.containsKey(languageCode)) {
			modified.remove(languageCode);
			return this;
		}
		removed.add(languageCode);
		modified.remove(languageCode);
		return this;
	}

	/**
	 * Replays all changes in provided update into this builder object. Changes are
	 * performed as if by calling {@link #put(MonolingualTextValue)} and
	 * {@link #remove(String)} methods.
	 * 
	 * @param update
	 *            term update to replay
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 */
	public TermUpdateBuilder append(TermUpdate update) {
		Objects.requireNonNull(update, "Term update cannot be null.");
		for (MonolingualTextValue term : update.getModified().values()) {
			put(term);
		}
		for (String language : update.getRemoved()) {
			remove(language);
		}
		return this;
	}

	/**
	 * Creates new {@link TermUpdate} object with contents of this builder object.
	 * 
	 * @return constructed object
	 */
	public TermUpdate build() {
		return Datamodel.makeTermUpdate(modified.values(), removed);
	}

}
