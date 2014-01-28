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
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;

/**
 * Implementation of {@link TimeValue}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class TimeValueImpl implements TimeValue {

	final int year;
	final byte month;
	final byte day;
	final byte precision;
	final String calendarModel;

	public TimeValueImpl(int year, byte month, byte day, byte precision,
			String calendarModel) {
		Validate.notNull(calendarModel, "Calendar model must not be null");
		this.year = year;
		this.month = month;
		this.day = day;
		this.precision = precision;
		this.calendarModel = calendarModel;
	}

	@Override
	public int getYear() {
		return year;
	}

	@Override
	public byte getMonth() {
		return month;
	}

	@Override
	public byte getDay() {
		return day;
	}

	@Override
	public String getPreferredCalendarModel() {
		return calendarModel;
	}

	@Override
	public byte getPrecision() {
		return precision;
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
		result = prime * result + calendarModel.hashCode();
		result = prime * result + day;
		result = prime * result + month;
		result = prime * result + precision;
		result = prime * result + year;
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
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TimeValueImpl)) {
			return false;
		}
		TimeValueImpl other = (TimeValueImpl) obj;
		return calendarModel.equals(other.calendarModel) && day == other.day
				&& month == other.month && precision == other.precision
				&& year == other.year;
	}

}
