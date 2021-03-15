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
import org.wikidata.wdtk.datamodel.interfaces.MultilingualTextUpdate;

/**
 * Builder for incremental construction of {@link MultilingualTextUpdate}
 * objects.
 */
public class MultilingualTextUpdateBuilder {

	private static DataObjectFactory factory = new DataObjectFactoryImpl();

	private final Map<String, MonolingualTextValue> modified = new HashMap<>();
	private final Set<String> removed = new HashSet<>();

	/**
	 * Adds or changes monolingual value. If there is no value for the language
	 * code, new value is added. If a value with this language code already exists,
	 * it is replaced. Values with other language codes are not touched. Calling
	 * this method overrides any previous changes made with the same language code
	 * by this method or {@link #removeMonolingualText(String)}.
	 * 
	 * @param value
	 *            monolingual value to add or change
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code value} is {@code null}
	 */
	public MultilingualTextUpdateBuilder setMonolingualText(MonolingualTextValue value) {
		Objects.requireNonNull(value, "Value cannot be null.");
		modified.put(value.getLanguageCode(), value);
		removed.remove(value.getLanguageCode());
		return this;
	}

	/**
	 * Removes monolingual value. Values with other language codes are not touched.
	 * Calling this method overrides any previous changes made with the same
	 * language code by this method or
	 * {@link #setMonolingualText(MonolingualTextValue)}.
	 * 
	 * @param languageCode
	 *            language code of the removed monolingual value
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code languageCode} is {@code null}
	 */
	public MultilingualTextUpdateBuilder removeMonolingualText(String languageCode) {
		Objects.requireNonNull(languageCode, "Language code cannot be null.");
		removed.add(languageCode);
		modified.remove(languageCode);
		return this;
	}

	/**
	 * Creates new {@link MultilingualTextUpdate} object with contents of this
	 * builder object.
	 * 
	 * @return constructed object
	 */
	public MultilingualTextUpdate build() {
		return factory.getMultilingualTextUpdate(modified.values(), removed);
	}

}
