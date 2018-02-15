package org.wikidata.wdtk.datamodel.implementation;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer.None;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

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
 * Jackson implementation of {@link StringValue}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = None.class)
public class StringValueImpl extends ValueImpl implements StringValue {

	private final String value;

	/**
	 * Constructor for deserialization from JSON by Jackson.
	 * 
	 * @param value
	 * 		the string represented by this Wikibase value
	 */
	@JsonCreator
	public StringValueImpl(
			@JsonProperty("value") String value) {
		super(ValueImpl.JSON_VALUE_TYPE_STRING);
		Validate.notNull(value, "A string value must provide a non-null string");
		this.value = value;
	}

	/**
	 * Returns the string. Only for use by Jackson during serialization.
	 *
	 * @return the string value
	 */
	public String getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsStringValue(this, obj);
	}

	@JsonIgnore
	@Override
	public String getString() {
		return this.value;
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
