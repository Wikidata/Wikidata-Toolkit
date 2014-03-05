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
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

/**
 * Implementation of {@link TimeValue}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class TimeValueImpl implements TimeValue {

	final long year;
	final byte month;
	final byte day;
	final byte hour;
	final byte minute;
	final byte second;
	final byte precision;
	final int timezoneOffset;
	final int beforeTolerance;
	final int afterTolerance;
	final String calendarModel;

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
	 *            {@link TimeValue#PREC_GY}
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
		Validate.notNull(calendarModel, "Calendar model must not be null");
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.precision = precision;
		this.beforeTolerance = beforeTolerance;
		this.afterTolerance = afterTolerance;
		this.timezoneOffset = timezoneOffset;
		this.calendarModel = calendarModel;
	}

	@Override
	public long getYear() {
		return this.year;
	}

	@Override
	public byte getMonth() {
		return this.month;
	}

	@Override
	public byte getDay() {
		return this.day;
	}

	@Override
	public byte getHour() {
		return this.hour;
	}

	@Override
	public byte getMinute() {
		return this.minute;
	}

	@Override
	public byte getSecond() {
		return this.second;
	}

	@Override
	public int getTimezoneOffset() {
		return this.timezoneOffset;
	}

	@Override
	public String getPreferredCalendarModel() {
		return this.calendarModel;
	}

	@Override
	public byte getPrecision() {
		return this.precision;
	}

	@Override
	public int getBeforeTolerance() {
		return this.beforeTolerance;
	}

	@Override
	public int getAfterTolerance() {
		return this.afterTolerance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + new Long(this.year).hashCode();
		result = prime * result + this.month;
		result = prime * result + this.day;
		result = prime * result + this.hour;
		result = prime * result + this.minute;
		result = prime * result + this.second;
		result = prime * result + this.precision;
		result = prime * result + this.beforeTolerance;
		result = prime * result + this.afterTolerance;
		result = prime * result + this.timezoneOffset;
		result = prime * result + this.calendarModel.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TimeValueImpl)) {
			return false;
		}
		TimeValueImpl other = (TimeValueImpl) obj;
		return this.year == other.year && this.month == other.month
				&& this.day == other.day && this.hour == other.hour
				&& this.minute == other.minute && this.second == other.second
				&& this.precision == other.precision
				&& this.beforeTolerance == other.beforeTolerance
				&& this.afterTolerance == other.afterTolerance
				&& this.timezoneOffset == other.timezoneOffset
				&& this.calendarModel.equals(other.calendarModel);
	}

}
