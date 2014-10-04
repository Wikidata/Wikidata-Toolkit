package org.wikidata.wdtk.datamodel.helpers;

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

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

/**
 * This class contains static methods to create string notations for values of
 * several datatypes and classes.
 *
 * @author Michael Günther
 *
 */
public class DataFormatter {

	final static String FORMAT_YEAR = "00000000000";
	final static String FORMAT_OTHER = "00";

	/**
	 * Returns a representation of the date from the value attributes as ISO
	 * 8601 encoding.
	 *
	 * @param value
	 * @return ISO 8601 value (String)
	 */
	public static String formatTimeISO8601(TimeValue value) {
		StringBuilder builder = new StringBuilder();
		DecimalFormat yearForm = new DecimalFormat(FORMAT_YEAR);
		DecimalFormat timeForm = new DecimalFormat(FORMAT_OTHER);
		if (value.getYear() > 0) {
			builder.append("+");
		}
		builder.append(yearForm.format(value.getYear()));
		builder.append("-");
		builder.append(timeForm.format(value.getMonth()));
		builder.append("-");
		builder.append(timeForm.format(value.getDay()));
		builder.append("T");
		builder.append(timeForm.format(value.getHour()));
		builder.append(":");
		builder.append(timeForm.format(value.getMinute()));
		builder.append(":");
		builder.append(timeForm.format(value.getSecond()));
		builder.append("Z");
		return builder.toString();
	}

	/**
	 * Returns a signed string representation of the given number.
	 *
	 * @param number
	 * @return String for BigDecimal value
	 */
	public static String formatBigDecimal(BigDecimal number) {
		if (number.signum() != -1) {
			return "+" + number.toString();
		} else {
			return number.toString();
		}
	}

}
