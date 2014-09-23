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

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A one-on-one representation of the external Json's monolingual text values.
 * Java attributes are named equally to the JSON fields. Deviations are due to
 * different naming in the implemented interfaces.
 * 
 * <b>This is a variation of the MonolingualTextValue.</b> The difference is
 * that this class extends {@link ValueImpl} which adds a type association done
 * by the JSON.
 * 
 * Also the "value" in this JSON context is called "text".
 * 
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonolingualTextDatavalueImpl extends ValueImpl implements
		MonolingualTextValue {

	public MonolingualTextDatavalueImpl() {
		super(typeMonolingualText);
	}

	public MonolingualTextDatavalueImpl(String language, String value) {
		super(typeMonolingualText);
		this.value = new MonolingualText(language, value);
	}

	public MonolingualTextDatavalueImpl(MonolingualTextValue mltv) {
		this(mltv.getLanguageCode(), mltv.getText());
	}

	MonolingualText value;

	public MonolingualText getValue() {
		return this.value;
	}

	public void setValue(MonolingualText value) {
		this.value = value;
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@JsonIgnore
	@Override
	public String getText() {
		return this.value.getText();
	}

	@JsonIgnore
	@Override
	public String getLanguageCode() {
		return this.value.getLanguageCode();
	}

	@Override
	public boolean equals(Object o) {
		return Equality.equalsMonolingualTextValue(this, o);
	}
}
