package org.wikidata.wdtk.datamodel.implementation;

/*-
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2019 Wikidata Toolkit Developers
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

import java.util.HashMap;
import java.util.Map;

import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.UnsupportedValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents a value with an unsupported datatype.
 * We can still "deserialize" it by just storing its
 * JSON representation, so that it can be serialized
 * back to its original representation.
 * This avoids parsing failures on documents containing
 * these values.
 * 
 * @author Antonin Delpeuch
 *
 */
@JsonDeserialize()
public class UnsupportedValueImpl extends ValueImpl implements UnsupportedValue {
	
	private final String typeString;
	private final Map<String, JsonNode> contents; 

	@JsonCreator
	private UnsupportedValueImpl(
			@JsonProperty("type")
			String typeString) {
		super(typeString);
		this.typeString = typeString;
		this.contents = new HashMap<>();
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	@JsonProperty("type")
	public String getTypeJsonString() {
		return typeString;
	}
	
	@JsonAnyGetter
	protected Map<String, JsonNode> getContents() {
		return contents;
	}
	
	@JsonAnySetter
	protected void loadContents(String key, JsonNode value) {
		this.contents.put(key, value);
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
	/**
	 * We do not use the Hash helper as in other datamodel
	 * classes because this would require exposing the contents
	 * of the value publicly, which goes against the desired
	 * opacity of the representation.
	 */
	@Override
	public int hashCode() {
		return typeString.hashCode() + 31*contents.hashCode();
	}
	
	/**
	 * We do not use the Equality helper as in other datamodel
	 * classes because this would require exposing the contents
	 * of the value publicly, which goes against the desired
	 * opacity of the representation.
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof UnsupportedValueImpl)) {
			return false;
		}
		UnsupportedValueImpl otherValue = (UnsupportedValueImpl) other;
		return typeString.equals(otherValue.getTypeJsonString()) &&
				contents.equals(otherValue.getContents());
	}
}
