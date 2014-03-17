package org.wikidata.wdtk.datamodel.jsonconverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

/**
 * This class contains static methods to create string notations for values of
 * several data types and classes
 * 
 * @author michael
 * 
 */
public class DatatypeConverters {

	final static String FORMAT_YEAR = "00000000000";
	final static String FORMAT_OTHER = "00";

	/**
	 * Returns a representation of the date from the value attributes as ISO
	 * 8601 encoding.
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @return ISO 8601 value (String)
	 * 
	 * @throws IllegalArgumentException
	 */
	public static String formatTimeISO8601(TimeValue value)
			throws IllegalArgumentException {
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
	 * Returns a String Representation of the given number attribute.
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
