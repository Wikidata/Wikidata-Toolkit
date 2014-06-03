package org.wikidata.wdtk.dumpfiles.constraint;

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

import java.util.Date;

/**
 * 
 * @author Julian Mendez
 * 
 */
public class DateAndNow {

	final Date date;
	final boolean isNow;

	/**
	 * Constructor to represent 'now'.
	 */
	public DateAndNow() {
		this.date = null;
		this.isNow = true;
	}

	/**
	 * Constructor to represent a date that is not necessarily now.
	 * 
	 * @param date
	 *            date
	 */
	public DateAndNow(Date date) {
		this.date = date;
		this.isNow = false;
	}

	public Date getDate() {
		if (this.isNow) {
			return new Date();
		}
		return this.date;
	}

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
			return "NOW";
		} else {
			return this.date.toString();
		}
	}

}
