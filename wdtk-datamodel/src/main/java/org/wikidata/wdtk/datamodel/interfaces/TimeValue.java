package org.wikidata.wdtk.datamodel.interfaces;

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
 * Time values represent points and intervals in time, and additional
 * information about their format. Information includes a specific time point,
 * information about its precision, and the preferred calendar model and
 * timezone (for display). Moreover, time values can describe some uncertainty
 * regarding the exact position in time. This is achieved by tolerance values
 * that specify how much {@link #getBeforeTolerance() before} or
 * {@link #getBeforeTolerance() after} the given time point an event might have
 * occurred.
 * <p>
 * Time points cannot describe durations (which are quantities), recurring
 * events ("1st of May"), or time spans (
 * "He reigned from 1697 to 1706, i.e., during <i>every</i> moment of that time span"
 * ). Intervals expressed by times always encode uncertainty ("He died in the
 * year 546 BCE, i.e., in <i>some</i> moment within that interval").
 * <p>
 * The main time point of the value generally refers to the proleptic Gregorian
 * calendar. However, if dates are imprecise (like "Jan 1512" or even "1200")
 * then one cannot convert this reliably and Wikidata will just keep the value
 * as entered.
 * <p>
 * "Y0K issue": Neither the Gregorian nor the Julian calendar assume a year 0,
 * i.e., the year 1 BCE was followed by 1 CE in these calendars. See <a
 * href="http://en.wikipedia.org/wiki/Year_zero"
 * >http://en.wikipedia.org/wiki/Year_zero</a>. Wikibase internally uses the
 * year 0. This is the same as ISO-8601, where 1 BCE is represented as "0000".
 * However, note that XML Schema dates (1.0 and 2.0) do not have a year 0, so in
 * their case 1BCE is represented as "-1". Understanding the difference is
 * relevant for computing leap years, for computing temporal intervals, and for
 * exporting data.
 * <p>
 * Timezone information is to be given in the form of a positive or negative
 * offset with respect to UTC, measured in minutes. This information specifies
 * the timezone that the time should be displayed in when shown to a user. The
 * recorded time point is in UTC, so timezone can be ignored for comparing
 * values. See {@link #getTimezoneOffset()}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface TimeValue extends Value {

	/**
	 * IRI of the proleptic Gregorian calendar; often used to specify the
	 * calendar model
	 */
	String CM_GREGORIAN_PRO = "http://www.wikidata.org/entity/Q1985727";
	/**
	 * IRI of the proleptic Julian calendar; often used to specify the calendar
	 * model
	 */
	String CM_JULIAN_PRO = "http://www.wikidata.org/entity/Q1985786";

	/**
	 * Precision constant for dates that are precise to the second.
	 */
	byte PREC_SECOND = 14;
	/**
	 * Precision constant for dates that are precise to the minute.
	 */
	byte PREC_MINUTE = 13;
	/**
	 * Precision constant for dates that are precise to the hour.
	 */
	byte PREC_HOUR = 12;
	/**
	 * Precision constant for dates that are precise to the day.
	 */
	byte PREC_DAY = 11;
	/**
	 * Precision constant for dates that are precise to the month.
	 */
	byte PREC_MONTH = 10;
	/**
	 * Precision constant for dates that are precise to the year.
	 */
	byte PREC_YEAR = 9;
	/**
	 * Precision constant for dates that are precise to the decade.
	 */
	byte PREC_DECADE = 8;
	/**
	 * Precision constant for dates that are precise to 100 years.
	 */
	byte PREC_100Y = 7;
	/**
	 * Precision constant for dates that are precise to 1,000 years.
	 */
	byte PREC_1KY = 6;
	/**
	 * Precision constant for dates that are precise to 10,000 years.
	 */
	byte PREC_10KY = 5;
	/**
	 * Precision constant for dates that are precise to 100,000 years.
	 */
	byte PREC_100KY = 4;
	/**
	 * Precision constant for dates that are precise to 1 million years.
	 */
	byte PREC_1MY = 3;
	/**
	 * Precision constant for dates that are precise to 10 million years.
	 */
	byte PREC_10MY = 2;
	/**
	 * Precision constant for dates that are precise to 100 million years.
	 */
	byte PREC_100MY = 1;
	/**
	 * Precision constant for dates that are precise to 10^9 years.
	 */
	byte PREC_1GY = 0;

	/**
	 * Get the year stored for this date. Years in Wikibase can be 0; see "Y0K"
	 * issue in the interface documentation.
	 * 
	 * @return year number
	 */
	long getYear();

	/**
	 * Get the month stored for this date. It will be a number from 1 to 12.
	 * 
	 * @return month number
	 */
	byte getMonth();

	/**
	 * Get the day stored for this date. It will be a number from 1 to 31.
	 * 
	 * @return day number
	 */
	byte getDay();

	/**
	 * Get the hour stored for this date. It will be a number from 0 to 23.
	 * 
	 * @return hour number
	 */
	byte getHour();

	/**
	 * Get the minute stored for this date. It will be a number from 0 to 59.
	 * 
	 * @return minute number
	 */
	byte getMinute();

	/**
	 * Get the seconds stored for this date. The value will be between 0 and 60
	 * (inclusive) to account for leap seconds. Implementations are not expected
	 * to validate leap seconds but they should provide consistent ordering: the
	 * time 23:59:60 is always before 00:00:00 on the next day.
	 * 
	 * @return second number
	 */
	byte getSecond();

	/**
	 * Get the IRI of the preferred calendar model that should be used to
	 * display this date (and that was presumably used when entering it). This
	 * is usually {@link TimeValue#CM_GREGORIAN_PRO} or
	 * {@link TimeValue#CM_JULIAN_PRO}.
	 * 
	 * @return IRI of the preferred calendar model
	 */
	String getPreferredCalendarModel();

	/**
	 * Get the {@link ItemIdValue} of the preferred calendar model that should
	 * be used to display this date (and that was presumably used when entering it).
	 *
	 * @throws IllegalArgumentException if the calendar model is not a valid item IRI
	 */
	ItemIdValue getPreferredCalendarModelItemId();

	/**
	 * Get the precision hint of this date. The return value will be in the
	 * range of {@link TimeValue#PREC_DAY}, ..., {@link TimeValue#PREC_1GY}.
	 * 
	 * @return precision hint for this date
	 */
	byte getPrecision();

	/**
	 * Get the offset in minutes from UTC that should be applied when displaying
	 * this time to users. The recorded time point is always in UTC, so the
	 * timezone can be ignored for comparing values. The offset should be
	 * <i>added</i> to the given time to obtain the intended local value. For
	 * example, an offset of +60 and a time of 10:45:00 should be displayed as
	 * 11:45:00 to the user (ideally with some indication of the shift; time
	 * zone abbreviations like "CET" could be used when matching the given
	 * offset, but the offset might also have values that do not correspond to
	 * any current or modern time zone). Therefore positive offsets are used for
	 * timezones that to the east of the prime meridian.
	 * 
	 * @return minute number (positive or negative)
	 */
	int getTimezoneOffset();

	/**
	 * Get a tolerance value that specifies how much earlier in time the value
	 * could at most be, measured as a multiple of {@link #getPrecision()
	 * precision}. The value is a non-negative integer.
	 * <p>
	 * For example, for the date 2007-05-12T10:45:00 with precision
	 * {@link TimeValue#PREC_MONTH}, a before-tolerance value of 3 means that
	 * the earliest possible time of this event could have been
	 * 2007-02-12T10:45:00. This information about the uncertainty of time
	 * points can be taken into account in query answering, but simplified
	 * implementations can also ignore it and work with the given (exact) time
	 * point instead. If not set specifically by the user, the before-tolerance
	 * value should be 0, i.e., the given time point marks the earliest possible
	 * time.
	 * <p>
	 * This boundary is inclusive. For example, a date 2014-02-17T00:00:00 with
	 * precision {@link TimeValue#PREC_DAY} and before-tolerance value 1
	 * specifies a time that between 2014-02-17T00:00:00
	 * 
	 * @see TimeValue#getAfterTolerance()
	 * 
	 * @return a non-negative integer tolerance measured in terms of precision
	 */
	int getBeforeTolerance();

	/**
	 * Get a tolerance value that specifies how much later in time the value
	 * could at most be, measured as a multiple of {@link #getPrecision()
	 * precision}. The value is a positive integer.
	 * <p>
	 * For example, for the date 2007-05-12T10:45:00 with precision
	 * {@link TimeValue#PREC_MONTH}, an after-tolerance value of 2 means that
	 * the latest possible time of this event could have been strictly before
	 * 2007-07-12T10:45:00. This information about the uncertainty of time
	 * points can be taken into account in query answering, but simplified
	 * implementations can also ignore it and work with the given (exact) time
	 * point instead. If not set specifically by the user, the after-tolerance
	 * value should be 1, i.e., the interval of uncertainty is exactly the
	 * length given by precision. However, because most (if not all) other
	 * known implementations of the data model got this detail wrong and use 0
	 * instead, we are also using 0 as a default value. This issue is tracked
	 * at https://phabricator.wikimedia.org/T194869.
	 * <p>
	 * The boundary is exclusive. For example, a date 2013-02-01T00:00:00 with
	 * precision {@link TimeValue#PREC_MONTH} and after-tolerance value 1 and
	 * before-tolerance value of 0 specifies a time "sometime in February 2013",
	 * but it excludes any time in March 2013. The after-tolerance must not be 0
	 * (which would make no sense if the bound is exclusive, and which is not
	 * needed since precision up to a single second can be specified anyway).
	 * 
	 * @see TimeValue#getBeforeTolerance()
	 * 
	 * @return a non-zero, positive integer tolerance measured in terms of
	 *         precision
	 */
	int getAfterTolerance();

}
