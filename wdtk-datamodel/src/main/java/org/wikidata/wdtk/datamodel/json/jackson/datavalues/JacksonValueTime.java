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
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonDeserializer.None;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Jackson implementation of {@link TimeValue}.
 *
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = None.class)
public class JacksonValueTime extends JacksonValue implements TimeValue {

	/**
	 * Inner helper object to store the actual data. Used to get the nested JSON
	 * structure that is required here.
	 */
	private JacksonInnerTime value;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonValueTime() {
		super(JSON_VALUE_TYPE_TIME);
	}

	/**
	 * Returns the inner value helper object. Only for use by Jackson during
	 * serialization.
	 *
	 * @return the inner time value
	 */
	public JacksonInnerTime getValue() {
		return value;
	}

	/**
	 * Sets the inner value helper object to the given value. Only for use by
	 * Jackson during deserialization.
	 *
	 * @param value
	 *            new value
	 */
	public void setValue(JacksonInnerTime value) {
		this.value = value;
	}

	@JsonIgnore
	@Override
	public long getYear() {
		return this.value.getYear();
	}

	@JsonIgnore
	@Override
	public byte getMonth() {
		return this.value.getMonth();
	}

	@JsonIgnore
	@Override
	public byte getDay() {
		return this.value.getDay();
	}

	@JsonIgnore
	@Override
	public byte getHour() {
		return this.value.getHour();
	}

	@JsonIgnore
	@Override
	public byte getMinute() {
		return this.value.getMinute();
	}

	@JsonIgnore
	@Override
	public byte getSecond() {
		return this.value.getSecond();
	}

	@JsonIgnore
	@Override
	public String getPreferredCalendarModel() {
		return this.value.getCalendarmodel();
	}

	@JsonIgnore
	@Override
	public byte getPrecision() {
		return (byte) this.value.getPrecision();
	}

	@JsonIgnore
	@Override
	public int getTimezoneOffset() {
		return this.value.getTimezone();
	}

	@JsonIgnore
	@Override
	public int getBeforeTolerance() {
		return this.value.getBefore();
	}

	@JsonIgnore
	@Override
	public int getAfterTolerance() {
		return this.value.getAfter();
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
		return Equality.equalsTimeValue(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
