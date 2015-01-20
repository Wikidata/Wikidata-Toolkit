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

import java.io.Serializable;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

public class MonolingualTextValueImpl implements MonolingualTextValue, Serializable {

	private static final long serialVersionUID = -6165541960096088292L;
	
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
}
