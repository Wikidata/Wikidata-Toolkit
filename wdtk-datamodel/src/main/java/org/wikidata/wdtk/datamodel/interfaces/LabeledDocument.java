package org.wikidata.wdtk.datamodel.interfaces;

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

import java.util.Map;

/**
 * Interface for EntityDocuments that can be described by labels
 * in several languages.
 *
 * @author Thomas Pellissier Tanon
 */
public interface LabeledDocument extends EntityDocument {
	/**
	 * Return a Map from Wikibase language codes to labels.
	 *
	 * @return the map of labels
	 */
	Map<String, MonolingualTextValue> getLabels();

	/**
	 * Returns the string label for the given language code, or null if there is
	 * no label for this code. This is a convenience method for accessing the
	 * data that can be obtained via {@link #getLabels()}.
	 *
	 * @param languageCode
	 *            a string that represents language
	 * @return the label string or null if it does not exists
	 */
	default String findLabel(String languageCode) {
		MonolingualTextValue value = this.getLabels().get(languageCode);
		return (value != null) ? value.getText() : null;
	}

	/**
	 * Returns a copy of this document with an updated revision id.
	 */
	@Override
	LabeledDocument withRevisionId(long newRevisionId);
	
	/**
	 * Returns a new version of this document with a new label
	 * (which overrides any existing label for this language).
	 */
	LabeledDocument withLabel(MonolingualTextValue newLabel);
}
