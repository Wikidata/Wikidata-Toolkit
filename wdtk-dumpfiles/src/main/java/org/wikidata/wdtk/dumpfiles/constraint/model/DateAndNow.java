package org.wikidata.wdtk.dumpfiles.constraint.model;

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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * An object of this class is a date with a distinguished value to represent the
 * current moment ('now').
 * 
 * @author Julian Mendez
 * 
 */
public class DateAndNow {

	private static ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {

		@Override
		protected SimpleDateFormat initialValue() {
			SimpleDateFormat ret = new SimpleDateFormat("yyyy-MM-dd");
			ret.setTimeZone(TimeZone.getTimeZone("UTC"));
			return ret;
		}

	};

	final Date date;
	final boolean isNow;

	/**
	 * Creates a new {@link DateAndNow} that represents the current moment
	 * ('now').
	 */
	public DateAndNow() {
		this.date = null;
		this.isNow = true;
	}

	/**
	 * Creates a new {@link DateAndNow} that represents a date that is not
	 * necessarily the current moment.
	 * 
	 * @param date
	 *            date
	 */
	public DateAndNow(Date date) {
		this.date = date;
		this.isNow = false;
	}

	/**
	 * Returns current moment if this date is 'now', otherwise the stored date.
	 * 
	 * @return current moment if this date is 'now', otherwise the stored date
	 */
	public Date getDate() {
		if (this.isNow) {
			return new Date();
		}
		return this.date;
	}

	/**
	 * Tells whether this object represents the current moment ('now').
	 * 
	 * @return <code>true</code> if and only if this object represents the
	 *         current moment ('now')
	 */
	public boolean isNow() {
		return this.isNow;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DateAndNow)) {
			return false;
		}
		DateAndNow other = (DateAndNow) obj;
		return (this.isNow == other.isNow) && (this.date.equals(other.date));
	}

	@Override
	public int hashCode() {
		if (this.date != null) {
			return this.date.hashCode();
		} else {
			return 1;
		}
	}

	@Override
	public String toString() {
		if (this.isNow) {
			return "now";
		} else {
			return dateFormat.get().format(this.date);
		}
	}

}
