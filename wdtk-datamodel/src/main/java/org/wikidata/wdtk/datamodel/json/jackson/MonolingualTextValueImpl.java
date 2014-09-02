package org.wikidata.wdtk.datamodel.json.jackson;

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
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A one-on-one representation of the external Json's monolingual text values.
 * Java attributes are named equally to the JSON fields.
 * Deviations are due to different naming in the implemented interfaces.
 * 
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonolingualTextValueImpl 
implements MonolingualTextValue {
	
	public MonolingualTextValueImpl(){}
	public MonolingualTextValueImpl(String language, String value){
		this.language = language;
		this.value = value;
	}
	public MonolingualTextValueImpl(MonolingualTextValue mltv) {
		this(mltv.getLanguageCode(), mltv.getText());
	}

	String language;
	String value;
	
	public void setLanguage(String language){
		this.language = language;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@JsonProperty("value")
	@Override
	public String getText() {
		return this.value;
	}

	@JsonProperty("language")
	@Override
	public String getLanguageCode() {
		return this.language;
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
