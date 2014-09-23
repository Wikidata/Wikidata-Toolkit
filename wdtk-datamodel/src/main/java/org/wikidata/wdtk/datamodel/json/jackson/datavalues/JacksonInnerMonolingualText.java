package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

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

import com.fasterxml.jackson.annotation.JsonProperty;

public class JacksonInnerMonolingualText {

	String language;
	String text;

	public JacksonInnerMonolingualText() {
	}

	public JacksonInnerMonolingualText(String language, String text) {
		this.language = language;
		this.text = text;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@JsonProperty("language")
	public String getLanguageCode() {
		return this.language;
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
