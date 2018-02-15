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

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonInnerTime;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonDeserializer.None;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Jackson implementation of {@link TimeValue}.
 *
 * @author Fredo Erxleben
 * @author Markus Kroetzsch
 * @author Antonin Delpeuch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = None.class)
public class TimeValueImpl extends ValueImpl implements TimeValue {

	/**
	 * Inner helper object to store the actual data. Used to get the nested JSON
	 * structure that is required here.
	 */
	private final JacksonInnerTime value;
	
	/**
	 * Constructor.
	 *
	 * @param year
	 *            a year number, where 0 refers to 1BCE
	 * @param month
	 *            a month number between 1 and 12
	 * @param day
	 *            a day number between 1 and 31
	 * @param hour
	 *            an hour number between 0 and 23
	 * @param minute
	 *            a minute number between 0 and 59
	 * @param second
	 *            a second number between 0 and 60 (possible leap second)
	 * @param precision
	 *            a value in the range of {@link TimeValue#PREC_DAY}, ...,
	 *            {@link TimeValue#PREC_1GY}
	 * @param beforeTolerance
	 *            non-negative integer tolerance before the value; see
	 *            {@link TimeValue#getBeforeTolerance()}
	 * @param afterTolerance
	 *            non-zero, positive integer tolerance before the value; see
	 *            {@link TimeValue#getAfterTolerance()}
	 * @param calendarModel
	 *            the IRI of the calendar model preferred when displaying the
	 *            date; usually {@link TimeValue#CM_GREGORIAN_PRO} or
	 *            {@link TimeValue#CM_JULIAN_PRO}
	 * @param timezoneOffset
	 *            offset in minutes that should be applied when displaying this
	 *            time
	 * @return a {@link DatatypeIdValue} corresponding to the input
	 */
	TimeValueImpl(long year, byte month, byte day, byte hour, byte minute,
			byte second, byte precision, int beforeTolerance,
			int afterTolerance, int timezoneOffset, String calendarModel) {
		super(JSON_VALUE_TYPE_TIME);
		this.value = new JacksonInnerTime(
				year, month, day, hour, minute, second,
				timezoneOffset, beforeTolerance, afterTolerance,
				precision, calendarModel);
	}

	/**
	 * Constructor used for deserialization from JSON with Jackson.
	 */
	@JsonCreator
	public TimeValueImpl(
			@JsonProperty("value") JacksonInnerTime value) {
		super(JSON_VALUE_TYPE_TIME);
		this.value = value;
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
