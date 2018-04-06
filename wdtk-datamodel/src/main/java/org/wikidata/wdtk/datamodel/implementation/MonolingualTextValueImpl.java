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
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Jackson implementation of {@link MonolingualTextValue}. Java attributes are
 * named equally to the JSON fields. Deviations are due to different naming in
 * the implemented interfaces. The "value" in this JSON context is called
 * "text".
 * <p>
 * The class extends {@link ValueImpl} which adds a type association done by
 * the JSON.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize()
public class MonolingualTextValueImpl extends ValueImpl implements
		MonolingualTextValue {

	/**
	 * Inner helper object to store the actual data. Used to get the nested JSON
	 * structure that is required here.
	 */
	private final JacksonInnerMonolingualText value;
	
	/**
	 * Constructor.
	 */
	public MonolingualTextValueImpl(String text, String language) {
		super(JSON_VALUE_TYPE_MONOLINGUAL_TEXT);
		this.value = new JacksonInnerMonolingualText(language, text);
	}

	/**
	 * Constructor used for deserialization from JSON with Jackson.
	 */
	@JsonCreator
	MonolingualTextValueImpl(
			@JsonProperty("value") JacksonInnerMonolingualText value) {
		super(JSON_VALUE_TYPE_MONOLINGUAL_TEXT);
		this.value = value;
	}

	/**
	 * Returns the inner value helper object. Only for use by Jackson during
	 * serialization.
	 *
	 * @return the inner monolingual text value
	 */
	public JacksonInnerMonolingualText getValue() {
		return this.value;
	}

	@JsonIgnore
	@Override
	public String getText() {
		return this.value.getText();
	}

	@JsonIgnore
	@Override
	public String getLanguageCode() {
		return this.value.getLanguage();
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsMonolingualTextValue(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

	/**
	 * Helper object that represents the JSON object structure of the value.
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class JacksonInnerMonolingualText {

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
		JacksonInnerMonolingualText(
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
	}
}
