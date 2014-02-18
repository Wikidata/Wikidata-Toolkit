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

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

public class MonolingualTextValueImpl implements MonolingualTextValue {

	final String text;
	final String languageCode;

	/**
	 * Constructor. The language code can be any string; the class does not make
	 * any assumptions on how language codes are defined.
	 * 
	 * @param text
	 *            the text of the value
	 * @param languageCode
	 *            the language code of the value
	 */
	MonolingualTextValueImpl(String text, String languageCode) {
		Validate.notNull(text, "Text cannot be null");
		Validate.notNull(languageCode, "Language code cannot be null");
		this.text = text;
		this.languageCode = languageCode;
	}

	@Override
	public String getText() {
		return this.text;
	}

	@Override
	public String getLanguageCode() {
		return this.languageCode;
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
		result = prime * result + languageCode.hashCode();
		result = prime * result + text.hashCode();
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
		if (!(obj instanceof MonolingualTextValueImpl)) {
			return false;
		}
		MonolingualTextValueImpl other = (MonolingualTextValueImpl) obj;
		return this.text.equals(other.text)
				&& this.languageCode.equals(other.languageCode);
	}

}
