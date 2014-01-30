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
 * information about its precision, and the preferred calendar model.
 * 
 * The main time point of the value generally refers to the proleptic Gregorian
 * calendar. However, if dates are imprecise (like "Jan 1512" or even "1200")
 * then one cannot convert this reliably and Wikidata will just keep the value
 * as entered. (TODO check if this is true)
 * 
 * "Y0K issue": Neither the Gregorian nor the Julian calendar assume a year 0,
 * i.e., the year 1 BCE was followed by 1 CE in these calendars. See
 * http://en.wikipedia.org/wiki/Year_zero. Wikibase internally uses the year 0.
 * This is the same as ISO-8601, where 1 BCE is represented as "0000". However,
 * note that XML Schema dates (1.0 and 2.0) do not have a year 0, so in their
 * case 1BCE is represented as "-1". Understanding the difference is relevant
 * for computing leap years, for computing temporal intervals, and for exporting
 * data.
 * 
 * While the Wikibase export format foresees the use of concrete times of the
 * day as well as timezones, this data can not be entered into the system so
 * far. This also means that the final details of these components might still
 * change. Hence, this implementation does not support this yet.
 * 
 * It is also planned that intervals should at some point be supported by
 * specifying a "before" and "after" distance, though the semantics of this is
 * not entirely clear yet.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface TimeValue extends Value {

	/**
	 * IRI of the proleptic Gregorian calendar; often used to specify the
	 * calendar model
	 */
	static final String CM_GREGORIAN_PRO = "http://www.wikidata.org/entity/Q1985727";
	/**
	 * IRI of the proleptic Julian calendar; often used to specify the calendar
	 * model
	 */
	static final String CM_JULIAN_PRO = "http://www.wikidata.org/entity/Q1985786";

	/**
	 * Precision constant for dates that are precise to the day.
	 */
	static final byte PREC_DAY = 11;
	/**
	 * Precision constant for dates that are precise to the month.
	 */
	static final byte PREC_MONTH = 10;
	/**
	 * Precision constant for dates that are precise to the year.
	 */
	static final byte PREC_YEAR = 9;
	/**
	 * Precision constant for dates that are precise to the decade.
	 */
	static final byte PREC_DECADE = 8;
	/**
	 * Precision constant for dates that are precise to 100 years.
	 */
	static final byte PREC_100Y = 7;
	/**
	 * Precision constant for dates that are precise to 1,000 years.
	 */
	static final byte PREC_1KY = 6;
	/**
	 * Precision constant for dates that are precise to 10,000 years.
	 */
	static final byte PREC_10KY = 5;
	/**
	 * Precision constant for dates that are precise to 100,000 years.
	 */
	static final byte PREC_100KY = 4;
	/**
	 * Precision constant for dates that are precise to 1 million years.
	 */
	static final byte PREC_1MY = 3;
	/**
	 * Precision constant for dates that are precise to 10 million years.
	 */
	static final byte PREC_10MY = 2;
	/**
	 * Precision constant for dates that are precise to 100 million years.
	 */
	static final byte PREC_100MY = 1;
	/**
	 * Precision constant for dates that are precise to 10^9 years.
	 */
	static final byte PREC_1GY = 0;

	/**
	 * Get the year stored for this date. Years in Wikibase can be 0; see "Y0K"
	 * issue in the interface documentation.
	 * 
	 * @return year number
	 */
	public int getYear();

	/**
	 * Get the month stored for this date. It will be a number from 1 to 12.
	 * 
	 * @return month number
	 */
	public byte getMonth();

	/**
	 * Get the day stored for this date. It will be a number from 1 to 31.
	 * 
	 * @return day number
	 */
	public byte getDay();

	/**
	 * Get the IRI of the preferred calendar model that should be used to
	 * display this date (and that was presumably used when entering it). This
	 * is usually {@link TimeValue#CM_GREGORIAN_PRO} or
	 * {@link TimeValue#CM_JULIAN_PRO}.
	 * 
	 * @return IRI of the preferred calendar model
	 */
	public String getPreferredCalendarModel();

	/**
	 * Get the precision hint of this date. The return value will be in the
	 * range of {@link TimeValue#PREC_DAY}, ..., {@link TimeValue#PREC_GY}.
	 * 
	 * @return precision hint for this date
	 */
	public byte getPrecision();

	/*
	 * Get the hour stored for this date. It will be a number from 1 to 23.
	 * 
	 * @return hour number
	 */
	// public byte getHour();

	/*
	 * Get the minute stored for this date. It will be a number from 1 to 59.
	 * 
	 * @return minute number
	 */
	// public byte getMinute();

	/*
	 * Get the seconds stored for this date. This is a decimal number,
	 * specifying fractions of a second to arbitrary precision.
	 * 
	 * TODO It might be possible to limit precision here, or to use an interface
	 * that allows implementations to use another number type if BigDecimal is
	 * not needed. However, float and double must never be used to store data.
	 * 
	 * @return second number and fraction
	 */
	// public Number getSeconds();

	/*
	 * Get the whole seconds stored for this date, without any sub-second
	 * fraction.
	 * 
	 * @return second number without any fraction
	 */
	// public byte getFullSeconds();

}
