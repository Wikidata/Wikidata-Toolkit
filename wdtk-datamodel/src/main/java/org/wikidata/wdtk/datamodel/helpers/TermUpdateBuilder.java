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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

/**
 * Builder for incremental construction of {@link TermUpdate} objects.
 */
public class TermUpdateBuilder {

	private static DataObjectFactory factory = new DataObjectFactoryImpl();

	private final Map<String, MonolingualTextValue> modified = new HashMap<>();
	private final Set<String> removed = new HashSet<>();

	/**
	 * Adds or changes term. If there is no term for the language code, new term is
	 * added. If a term with this language code already exists, it is replaced.
	 * Terms with other language codes are not touched. Calling this method
	 * overrides any previous changes made with the same language code by this
	 * method or {@link #removeTerm(String)}.
	 * 
	 * @param term
	 *            term to add or change
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code term} is {@code null}
	 */
	public TermUpdateBuilder setTerm(MonolingualTextValue term) {
		Objects.requireNonNull(term, "Term cannot be null.");
		modified.put(term.getLanguageCode(), term);
		removed.remove(term.getLanguageCode());
		return this;
	}

	/**
	 * Removes term. Terms with other language codes are not touched. Calling this
	 * method overrides any previous changes made with the same language code by
	 * this method or {@link #setTerm(MonolingualTextValue)}.
	 * 
	 * @param languageCode
	 *            language code of the removed term
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code languageCode} is {@code null}
	 */
	public TermUpdateBuilder removeTerm(String languageCode) {
		Objects.requireNonNull(languageCode, "Language code cannot be null.");
		removed.add(languageCode);
		modified.remove(languageCode);
		return this;
	}

	/**
	 * Creates new {@link TermUpdate} object with contents of this builder object.
	 * 
	 * @return constructed object
	 */
	public TermUpdate build() {
		return factory.getTermUpdate(modified.values(), removed);
	}

}
