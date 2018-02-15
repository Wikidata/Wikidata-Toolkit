package org.wikidata.wdtk.datamodel.implementation.json;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.implementation.ValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

/**
 * Helper object that represents the JSON object structure that is used to
 * represent values of type
 * {@link ValueImpl#JSON_VALUE_TYPE_MONOLINGUAL_TEXT}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonInnerMonolingualText {

	private final String language;
	private final String text;

	/**
	 * Constructor.
	 *
	 * @param language
	 * 		the Wikimedia language code
	 * @param text
	 * 		the text of the value
	 */
	@JsonCreator
	public JacksonInnerMonolingualText(
			@JsonProperty("language") String language,
			@JsonProperty("text") String text) {
		Validate.notNull(language, "A language has to be provided to create a MonolingualTextValue");
		this.language = language;
		Validate.notNull(text, "A text has to be provided to create a MonolingualTextValue");
		this.text = text;
	}

	/**
	 * Returns the language code.
	 *
	 * @see MonolingualTextValue#getLanguageCode()
	 * @return language code
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Returns the text.
	 *
	 * @see MonolingualTextValue#getText()
	 * @return text
	 */
	public String getText() {
		return this.text;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof JacksonInnerMonolingualText)) {
			return false;
		}

		JacksonInnerMonolingualText other = (JacksonInnerMonolingualText) o;
		return this.text.equals(other.text)
				&& this.language.equals(other.language);
	}
}
