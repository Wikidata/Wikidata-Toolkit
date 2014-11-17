package org.wikidata.wdtk.storage.dbtowdtk;

/*
 * #%L
 * Wikidata Toolkit Storage
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

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.storage.datamodel.LongValue;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.datamodel.StringValue;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;

public class TimeValueFromObjectValue implements TimeValue {

	final ObjectValue objectValue;

	String calendarModel = null;
	long year;
	byte month;
	byte day;
	byte hour;
	byte minute;
	byte second;
	byte precision;

	public TimeValueFromObjectValue(ObjectValue objectValue) {
		this.objectValue = objectValue;
	}

	private void initialize() {
		if (this.calendarModel == null) {
			for (PropertyValuePair pvp : this.objectValue) {
				switch (pvp.getProperty()) {
				case WdtkSorts.PROP_TIME_YEAR:
					this.year = ((LongValue) pvp.getValue()).getLong();
					break;
				case WdtkSorts.PROP_TIME_MONTH:
					this.month = (byte) ((LongValue) pvp.getValue()).getLong();
					break;
				case WdtkSorts.PROP_TIME_DAY:
					this.day = (byte) ((LongValue) pvp.getValue()).getLong();
					break;
				case WdtkSorts.PROP_TIME_SECONDS:
					long seconds = ((LongValue) pvp.getValue()).getLong();
					this.hour = (byte) (seconds / 3600);
					seconds = seconds % 3600;
					this.minute = (byte) (seconds / 60);
					this.second = (byte) (seconds % 60);
					break;
				case WdtkSorts.PROP_TIME_PRECISION:
					this.precision = (byte) ((LongValue) pvp.getValue())
					.getLong();
					break;
				case WdtkSorts.PROP_TIME_CALENDAR_MODEL:
					this.calendarModel = ((StringValue) pvp.getValue())
							.getString();
					break;
				default:
					throw new RuntimeException("Unexpected property "
							+ pvp.getProperty() + " in time record.");
				}
			}
		}
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	public long getYear() {
		initialize();
		return this.year;
	}

	@Override
	public byte getMonth() {
		initialize();
		return this.month;
	}

	@Override
	public byte getDay() {
		initialize();
		return this.day;
	}

	@Override
	public byte getHour() {
		initialize();
		return this.hour;
	}

	@Override
	public byte getMinute() {
		initialize();
		return this.minute;
	}

	@Override
	public byte getSecond() {
		initialize();
		return this.second;
	}

	@Override
	public String getPreferredCalendarModel() {
		initialize();
		return this.calendarModel;
	}

	@Override
	public byte getPrecision() {
		initialize();
		return this.precision;
	}

	@Override
	public int getTimezoneOffset() {
		// TODO not stored yet
		return 0;
	}

	@Override
	public int getBeforeTolerance() {
		// TODO not stored yet
		return 0;
	}

	@Override
	public int getAfterTolerance() {
		// TODO not stored yet
		return 0;
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

}
