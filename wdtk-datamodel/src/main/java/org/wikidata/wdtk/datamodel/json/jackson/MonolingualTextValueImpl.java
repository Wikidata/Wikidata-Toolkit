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

import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A one-on-one representation of the external Json's monolingual text values.
 * Java attributes are named equally to the JSON fields.
 * Deviations are due to different naming in the implemented interfaces.
 * 
 * @author Fredo Erxleben
 *
 */
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
		// TODO Auto-generated method stub
		return null;
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
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		
		if(!(o instanceof MonolingualTextValue)){
			return false;
		}
		
		return this.getLanguageCode().equals(((MonolingualTextValue) o).getLanguageCode())
				&& this.getText().equals(((MonolingualTextValue) o).getText());
	}
}
