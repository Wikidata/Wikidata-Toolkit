package org.wikidata.wdtk.dumpfiles.constraint.builder;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.dumpfiles.constraint.model.ConstraintRange;
import org.wikidata.wdtk.dumpfiles.constraint.model.DateAndNow;
import org.wikidata.wdtk.dumpfiles.constraint.template.Template;
import org.wikidata.wdtk.rdf.WikidataPropertyTypes;

/**
 * An object of this class is a builder of a 'Range' constraint.
 * 
 * @author Julian Mendez
 * 
 */
class ConstraintRangeBuilder implements ConstraintBuilder {

	private static ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {

		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat ret = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ret.setTimeZone(TimeZone.getTimeZone("UTC"));
			return ret;
		}

	};

	/**
	 * Returns the date contained in the given string. Only years in the range
	 * 0000 to 9999 are supported.
	 * 
	 * @param dateOrNowStr
	 *            string
	 * @return the date contained in the given string
	 */
	public DateAndNow parseDate(String dateOrNowStr) {
		Validate.notNull(dateOrNowStr);
		if (dateOrNowStr.equals("now")) {
			return new DateAndNow();
		} else if (dateOrNowStr.startsWith("-")) {
			// not supported
			return null;
		} else {
			String str = dateOrNowStr;
			while (str.length() < 4) {
				str = "0" + str;
			}
			if (str.length() == 4) {
				str += "-01";
			}
			if (str.length() == 7) {
				str += "-01";
			}
			if (str.length() == 10) {
				str += " 00:00:00";
			}
			try {
				Date date = dateFormat.get().parse(str);
				return new DateAndNow(date);
			} catch (ParseException e) {
			}
			return null;
		}
	}

	/**
	 * Returns the double contained in the given string.
	 * 
	 * @param str
	 *            string
	 * @return the double contained in the given string
	 */
	public Double parseDouble(String str) {
		Validate.notNull(str);
		Double ret = null;
		try {
			ret = Double.parseDouble(str);
		} catch (NumberFormatException e) {
		}
		return ret;
	}

	/**
	 * Constructs a new builder.
	 */
	public ConstraintRangeBuilder() {
	}

	@Override
	public ConstraintRange parse(PropertyIdValue constrainedProperty,
			Template template) {
		ConstraintRange ret = null;
		String minStr = template.getValue(ConstraintBuilderConstant.P_MIN)
				.toLowerCase().trim();
		String maxStr = template.getValue(ConstraintBuilderConstant.P_MAX)
				.toLowerCase().trim();
		if ((constrainedProperty != null) && (minStr != null)
				&& (maxStr != null)) {
			WikidataPropertyTypes wdPropertyTypes = new WikidataPropertyTypes();
			String propertyType = wdPropertyTypes
					.getPropertyType(constrainedProperty);

			if (propertyType.equals(DatatypeIdValue.DT_TIME)) {
				DateAndNow minDate = parseDate(minStr);
				DateAndNow maxDate = parseDate(maxStr);
				if ((minDate != null) && (maxDate != null)) {
					ret = new ConstraintRange(constrainedProperty, minStr,
							maxStr, true);
				}

			} else if (propertyType.equals(DatatypeIdValue.DT_QUANTITY)) {
				Double minNum = parseDouble(minStr);
				Double maxNum = parseDouble(maxStr);
				if ((minNum != null) && (maxNum != null)) {
					ret = new ConstraintRange(constrainedProperty, minStr,
							maxStr, false);
				}

			} else {
				throw new IllegalArgumentException("Property '"
						+ constrainedProperty.getId() + "' has type '"
						+ propertyType
						+ "' and cannot have a range constraint as in '"
						+ template + "'.");
			}

		}
		return ret;
	}

}
