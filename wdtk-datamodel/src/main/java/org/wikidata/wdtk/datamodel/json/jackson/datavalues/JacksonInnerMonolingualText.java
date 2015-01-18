package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

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
 * {@link JacksonValue#JSON_VALUE_TYPE_MONOLINGUAL_TEXT}.
 *
 * @author Fredo Erxleben
 *
 */
public class JacksonInnerMonolingualText {

	String language = "";
	String text = "";

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonInnerMonolingualText() {
	}

	/**
	 * TODO Review the utility of this constructor.
	 *
	 * @param language
	 * @param text
	 */
	public JacksonInnerMonolingualText(String language, String text) {
		this.language = language;
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
	 * Sets the language code to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param language
	 *            new value
	 */
	public void setLanguage(String language) {
		this.language = language;
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

	/**
	 * Sets the text to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param text
	 *            new value
	 */
	public void setText(String text) {
		this.text = text;
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
