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
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
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
@JsonDeserialize()
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
	 * @return a {@link TimeValue} corresponding to the input
	 */
	public TimeValueImpl(long year, byte month, byte day, byte hour, byte minute,
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
	TimeValueImpl(
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
	public ItemIdValue getPreferredCalendarModelItemId() {
		return ItemIdValueImpl.fromIri(this.value.getCalendarmodel());
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

	/**
	 * Helper object that represents the JSON object structure of the value.
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class JacksonInnerTime {
		private final String time;
		private final int timezone;
		private final int before;
		private final int after;
		private final int precision;
		private final String calendarmodel;

		private long year;
		private byte month;
		private byte day;
		private byte hour;
		private byte minute;
		private byte second;

		/**
		 * Constructs a new object for the given data.
		 *
		 * @param time
		 * 		      an ISO timestamp
		 * @param timezone
		 *            offset in minutes that should be applied when displaying this
		 *            time
		 * @param before
		 *            non-negative integer tolerance before the value; see
		 *            {@link TimeValue#getBeforeTolerance()}
		 * @param after
		 *            non-zero, positive integer tolerance before the value; see
		 *            {@link TimeValue#getAfterTolerance()}
		 * @param precision
		 *            a value in the range of {@link TimeValue#PREC_DAY}, ...,
		 *            {@link TimeValue#PREC_1GY}
		 * @param calendarModel
		 *            the IRI of the calendar model preferred when displaying the
		 *            date; usually {@link TimeValue#CM_GREGORIAN_PRO} or
		 *            {@link TimeValue#CM_JULIAN_PRO}
		 */
		@JsonCreator
		JacksonInnerTime(
				@JsonProperty("time") String time,
				@JsonProperty("timezone") int timezone,
				@JsonProperty("before") int before,
				@JsonProperty("after") int after,
				@JsonProperty("precision") int precision,
				@JsonProperty("calendarmodel") String calendarModel) {
			this.time = time;
			this.timezone = timezone;
			this.before = before;
			this.after = after;
			this.precision = precision;
			this.calendarmodel = calendarModel;

			this.decomposeTimeString();
		}

		/**
		 * Constructor for times that have already been parsed.
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
		 * @param timezone
		 *            offset in minutes that should be applied when displaying this
		 *            time
		 * @param before
		 *            non-negative integer tolerance before the value; see
		 *            {@link TimeValue#getBeforeTolerance()}
		 * @param after
		 *            non-zero, positive integer tolerance before the value; see
		 *            {@link TimeValue#getAfterTolerance()}
		 * @param precision
		 *            a value in the range of {@link TimeValue#PREC_DAY}, ...,
		 *            {@link TimeValue#PREC_1GY}
		 * @param calendarModel
		 *            the IRI of the calendar model preferred when displaying the
		 *            date; usually {@link TimeValue#CM_GREGORIAN_PRO} or
		 *            {@link TimeValue#CM_JULIAN_PRO}
		 */
		JacksonInnerTime(long year, byte month, byte day, byte hour,
						 byte minute, byte second, int timezone, int before, int after,
						 int precision, String calendarModel) {
			Validate.notNull(calendarModel, "Calendar model must not be null");
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
			this.calendarmodel = calendarModel;
			this.time = composeTimeString();
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
		private String composeTimeString() {
			return String.format("%+04d-%02d-%02dT%02d:%02d:%02dZ",
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
		 * Returns the timezone offset.
		 *
		 * @see TimeValue#getTimezoneOffset()
		 * @return timezone offset
		 */
		public int getTimezone() {
			return timezone;
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
		 * Returns the after tolerance.
		 *
		 * @see TimeValue#getAfterTolerance()
		 * @return after tolerance
		 */
		public int getAfter() {
			return after;
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
		 * Returns the calendar model IRI.
		 *
		 * @see TimeValue#getPreferredCalendarModel()
		 * @return calendar model item IRI
		 */
		public String getCalendarmodel() {
			return calendarmodel;
		}

		/**
		 * Returns the second.
		 *
		 * @see TimeValue#getSecond()
		 * @return second
		 */
		@JsonIgnore
		byte getSecond() {
			return this.second;
		}

		/**
		 * Returns the minute.
		 *
		 * @see TimeValue#getMinute()
		 * @return minute
		 */
		@JsonIgnore
		byte getMinute() {
			return this.minute;
		}

		/**
		 * Returns the hour.
		 *
		 * @see TimeValue#getHour()
		 * @return hour
		 */
		@JsonIgnore
		byte getHour() {
			return this.hour;
		}

		/**
		 * Returns the day.
		 *
		 * @see TimeValue#getDay()
		 * @return day
		 */
		@JsonIgnore
		byte getDay() {
			return this.day;
		}

		/**
		 * Returns the month.
		 *
		 * @see TimeValue#getMonth()
		 * @return month
		 */
		@JsonIgnore
		byte getMonth() {
			return this.month;
		}

		/**
		 * Returns the year.
		 *
		 * @see TimeValue#getYear()
		 * @return year
		 */
		@JsonIgnore
		long getYear() {
			return this.year;
		}
	}
}
