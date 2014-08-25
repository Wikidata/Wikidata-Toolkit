package org.wikidata.wdtk.datamodel.implementation;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;

/**
 * Implementation of {@link TermedDocument}. This abstract class defines the
 * code for shared functionality of concrete types of TermedDocument.
 *
 * @author Markus Kroetzsch
 *
 */
public abstract class TermedDocumentImpl implements TermedDocument {

	final Map<String, MonolingualTextValue> labels;
	final Map<String, MonolingualTextValue> descriptions;
	final Map<String, List<MonolingualTextValue>> aliases;

	/**
	 * Constructor.
	 *
	 * @param labels
	 *            the list of labels of this entity, with at most one label for
	 *            each language code
	 * @param descriptions
	 *            the list of descriptions of this entity, with at most one
	 *            description for each language code
	 * @param aliases
	 *            the list of aliases of this entity
	 */
	TermedDocumentImpl(List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases) {
		Validate.notNull(labels, "list of labels cannot be null");
		Validate.notNull(descriptions, "list of descriptions cannot be null");
		Validate.notNull(aliases, "list of aliases cannot be null");

		this.labels = new HashMap<String, MonolingualTextValue>();
		for (MonolingualTextValue label : labels) {
			if (this.labels.containsKey(label.getLanguageCode())) {
				throw new IllegalArgumentException(
						"At most one label allowed per language code");
			} else {
				this.labels.put(label.getLanguageCode(), label);
			}
		}

		this.descriptions = new HashMap<String, MonolingualTextValue>();
		for (MonolingualTextValue description : descriptions) {
			if (this.descriptions.containsKey(description.getLanguageCode())) {
				throw new IllegalArgumentException(
						"At most one description allowed per language code");
			} else {
				this.descriptions.put(description.getLanguageCode(),
						description);
			}
		}

		this.aliases = new HashMap<String, List<MonolingualTextValue>>();
		for (MonolingualTextValue alias : aliases) {
			if (this.aliases.containsKey(alias.getLanguageCode())) {
				this.aliases.get(alias.getLanguageCode()).add(alias);
			} else {
				List<MonolingualTextValue> aliasesForLanguage = new ArrayList<MonolingualTextValue>();
				aliasesForLanguage.add(alias);
				this.aliases.put(alias.getLanguageCode(), aliasesForLanguage);
			}
		}
	}

	@Override
	public Map<String, MonolingualTextValue> getLabels() {
		return Collections.unmodifiableMap(labels);
	}

	@Override
	public Map<String, MonolingualTextValue> getDescriptions() {
		return Collections.unmodifiableMap(descriptions);
	}

	@Override
	public Map<String, List<MonolingualTextValue>> getAliases() {
		// TODO This still allows inner lists of aliases to be modified. Do
		// we have to protect against this?
		return Collections.unmodifiableMap(aliases);
	}

}
