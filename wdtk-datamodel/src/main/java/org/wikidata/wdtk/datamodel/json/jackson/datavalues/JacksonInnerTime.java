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

import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Helper object that represents the JSON object structure that is used to
 * represent values of type {@link JacksonValue#JSON_VALUE_TYPE_STRING}.
 *
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonInnerTime {
	private String time;
	private int timezone;
	private int before;
	private int after;
	private int precision;
	private String calendarmodel;

	@JsonIgnore
	private long year;
	@JsonIgnore
	private byte month;
	@JsonIgnore
	private byte day;
	@JsonIgnore
	private byte hour;
	@JsonIgnore
	private byte minute;
	@JsonIgnore
	private byte second;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonInnerTime() {
	}

	/**
	 * Constructs a new object for the given data.
	 *
	 * @param time
	 * @param timezoneOffset
	 * @param beforeTolerance
	 * @param afterTolerance
	 * @param precision
	 * @param calendarModel
	 */
	public JacksonInnerTime(String time, int timezoneOffset,
			int beforeTolerance, int afterTolerance, int precision,
			String calendarModel) {
		this.time = time;
		this.timezone = timezoneOffset;
		this.before = beforeTolerance;
		this.after = afterTolerance;
		this.precision = precision;
		this.calendarmodel = calendarModel;

		this.decomposeTimeString();
	}

	/**
	 * TODO Review the utility of this constructor.
	 *
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @param timezone
	 * @param before
	 * @param after
	 * @param precision
	 * @param calendarmodel
	 */
	public JacksonInnerTime(long year, byte month, byte day, byte hour,
			byte minute, byte second, int timezone, int before, int after,
			int precision, String calendarmodel) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.timezone = timezone;
		this.before = before;
		this.after = after;
		this.precision = precision;
		this.calendarmodel = calendarmodel;

		this.composeTimeString();
	}

	/**
	 * Helper method to decompose the time string into its parts.
	 */
	private void decomposeTimeString() {
		// decompose the time string into its parts
		String[] substrings = time.split("(?<!\\A)[\\-:TZ]");

		// get the components of the date
		this.year = Long.parseLong(substrings[0]);
		this.month = Byte.parseByte(substrings[1]);
		this.day = Byte.parseByte(substrings[2]);
		this.hour = Byte.parseByte(substrings[3]);
		this.minute = Byte.parseByte(substrings[4]);
		this.second = Byte.parseByte(substrings[5]);
	}

	/**
	 * Helper method to compose the time string from its components.
	 */
	private void composeTimeString() {
		this.time = String.format("%+012d-%02d-%02dT%02d:%02d:%02dZ",
				this.year, this.month, this.day, this.hour, this.minute,
				this.second);
	}

	/**
	 * Returns the formatted date-time string as used in JSON. Only for use by
	 * Jackson during serialization.
	 *
	 * @return the time string
	 */
	public String getTime() {
		return this.time;
	}

	/**
	 * Sets the formatted date-time string to the given value. Only for use by
	 * Jackson during deserialization.
	 *
	 * @param time
	 *            new value
	 */
	public void setTime(String time) {
		this.time = time;
		this.decomposeTimeString();
	}

	/**
	 * Returns the timezone offset.
	 *
	 * @see TimeValue#getTimezoneOffset()
	 * @return timezone offset
	 */
	public int getTimezone() {
		return timezone;
	}

	/**
	 * Sets the timezone offset to the given value. Only for use by Jackson
	 * during deserialization.
	 *
	 * @param timezone
	 *            new value
	 */
	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}

	/**
	 * Returns the before tolerance.
	 *
	 * @see TimeValue#getBeforeTolerance()
	 * @return before tolerance
	 */
	public int getBefore() {
		return before;
	}

	/**
	 * Sets the before tolerance to the given value. Only for use by Jackson
	 * during deserialization.
	 *
	 * @param before
	 *            new value
	 */
	public void setBefore(int before) {
		this.before = before;
	}

	/**
	 * Returns the after tolerance.
	 *
	 * @see TimeValue#getAfterTolerance()
	 * @return after tolerance
	 */
	public int getAfter() {
		return after;
	}

	/**
	 * Sets the after tolerance to the given value. Only for use by Jackson
	 * during deserialization.
	 *
	 * @param after
	 *            new value
	 */
	public void setAfter(int after) {
		this.after = after;
	}

	/**
	 * Returns the precision constant.
	 *
	 * @see TimeValue#getPrecision()
	 * @return precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * Sets the precision to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param precision
	 *            new value
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	/**
	 * Returns the calendar model IRI.
	 *
	 * @see TimeValue#getPreferredCalendarModel()
	 * @return calendar model item IRI
	 */
	public String getCalendarmodel() {
		return calendarmodel;
	}

	/**
	 * Sets the calendar model IRI to the given value. Only for use by Jackson
	 * during deserialization.
	 *
	 * @param calendarmodel
	 *            new value
	 */
	public void setCalendarmodel(String calendarmodel) {
		this.calendarmodel = calendarmodel;
	}

	/**
	 * Returns the second.
	 *
	 * @see TimeValue#getSecond()
	 * @return second
	 */
	@JsonIgnore
	public byte getSecond() {
		return this.second;
	}

	/**
	 * Returns the minute.
	 *
	 * @see TimeValue#getMinute()
	 * @return minute
	 */
	@JsonIgnore
	public byte getMinute() {
		return this.minute;
	}

	/**
	 * Returns the hour.
	 *
	 * @see TimeValue#getHour()
	 * @return hour
	 */
	@JsonIgnore
	public byte getHour() {
		return this.hour;
	}

	/**
	 * Returns the day.
	 *
	 * @see TimeValue#getDay()
	 * @return day
	 */
	@JsonIgnore
	public byte getDay() {
		return this.day;
	}

	/**
	 * Returns the month.
	 *
	 * @see TimeValue#getMonth()
	 * @return month
	 */
	@JsonIgnore
	public byte getMonth() {
		return this.month;
	}

	/**
	 * Returns the year.
	 *
	 * @see TimeValue#getYear()
	 * @return year
	 */
	@JsonIgnore
	public long getYear() {
		return this.year;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof JacksonInnerTime)) {
			return false;
		}

		JacksonInnerTime other = (JacksonInnerTime) o;

		return (this.calendarmodel.equals(other.calendarmodel)
				&& this.year == other.year && this.month == other.month
				&& this.day == other.day && this.hour == other.hour
				&& this.minute == other.minute && this.second == other.second
				&& this.before == other.before && this.after == other.after
				&& this.timezone == other.timezone && this.precision == other.precision);
	}
}
