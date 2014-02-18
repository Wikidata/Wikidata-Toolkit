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

import java.util.Collections;
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
	 * @param descriptions
	 * @param aliases
	 */
	TermedDocumentImpl(Map<String, MonolingualTextValue> labels,
			Map<String, MonolingualTextValue> descriptions,
			Map<String, List<MonolingualTextValue>> aliases) {
		Validate.notNull(labels, "map of labels cannot be null");
		Validate.notNull(descriptions, "map of descriptions cannot be null");
		Validate.notNull(aliases, "map of aliases cannot be null");
		this.labels = labels;
		this.descriptions = descriptions;
		this.aliases = aliases;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + aliases.hashCode();
		result = prime * result + descriptions.hashCode();
		result = prime * result + labels.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TermedDocumentImpl)) {
			return false;
		}
		TermedDocumentImpl other = (TermedDocumentImpl) obj;
		return aliases.equals(other.aliases)
				&& descriptions.equals(other.descriptions)
				&& labels.equals(other.labels);
	}

}
