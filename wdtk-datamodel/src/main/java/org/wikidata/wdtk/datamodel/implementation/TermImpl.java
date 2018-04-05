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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson representation of {@link MonolingualTextValue} data used in labels,
 * aliases, and descriptions in JSON. Note that this is distinct from the JSON
 * representation for property values of type {@link MonolingualTextValue},
 * which is implemented in {@link MonolingualTextValueImpl}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TermImpl implements MonolingualTextValue {

	/**
	 * The language code.
	 */
	private final String languageCode;
	/**
	 * The text value.
	 */
	private final String text;

	/**
	 * Create a new object from the given data.
	 *
	 * @param languageCode
	 *            the language code of the value
	 * @param text
	 *            the text content of the value
	 */
	@JsonCreator
	public TermImpl(
			@JsonProperty("language") String languageCode,
			@JsonProperty("value") String text) {
		Validate.notNull(languageCode, "A language has to be provided to create a MonolingualTextValue");
		this.languageCode = languageCode;
		Validate.notNull(text, "A text has to be provided to create a MonolingualTextValue");
		this.text = text;
	}

	/**
	 * Copy constructor.
	 *
	 * @param mltv
	 *            the object to copy the data from
	 */
	@Deprecated
	public TermImpl(MonolingualTextValue mltv) {
		this(mltv.getLanguageCode(), mltv.getText());
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@JsonProperty("value")
	@Override
	public String getText() {
		return this.text;
	}

	@JsonProperty("language")
	@Override
	public String getLanguageCode() {
		return this.languageCode;
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
}
