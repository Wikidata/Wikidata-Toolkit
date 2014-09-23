package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

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

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JacksonInnerTime {
	private String time;
	private int timezone;
	private int before;
	private int after;
	private int precision;
	private String calendarmodel;
	
	@JsonIgnore
	private long year;
	@JsonIgnore
	private byte month;
	@JsonIgnore
	private byte day;
	@JsonIgnore
	private byte hour;
	@JsonIgnore
	private byte minute;
	@JsonIgnore
	private byte second;
	
	public JacksonInnerTime() {}

	public JacksonInnerTime(String time, int timezone, int before, int after,
			int precision, String calendarmodel) {
		this.time = time;
		this.timezone = timezone;
		this.before = before;
		this.after = after;
		this.precision = precision;
		this.calendarmodel = calendarmodel;
		
		this.decomposeTimeString();
	}
	
	public JacksonInnerTime(long year, byte month, byte day, byte hour, byte minute, byte second,
			int timezone, int before, int after, int precision, String calendarmodel){
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.timezone = timezone;
		this.before = before;
		this.after = after;
		this.precision = precision;
		this.calendarmodel = calendarmodel;
		
		this.composeTimeString();
	}
	
	/**
	 * A helper method to decompose the time string into its parts.
	 */
	private void decomposeTimeString(){
		// decompose the time string into its parts
		String[] substrings = time.split("(?<!\\A)[\\-\\:TZ]");

		// get the components of the date
		this.year = Long.parseLong(substrings[0]);
		this.month = Byte.parseByte(substrings[1]);
		this.day = Byte.parseByte(substrings[2]);
		this.hour = Byte.parseByte(substrings[3]);
		this.minute = Byte.parseByte(substrings[4]);
		this.second = Byte.parseByte(substrings[5]);
	}
	
	/**
	 * A helper method to compose the time string from its components.
	 */
	private void composeTimeString(){
		this.time = 
				String.format("%+-11d-%02d-%02dT%02d:%02d:%02d", 
						this.year, this.month, this.day, this.hour, this.minute, this.second);
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
		this.decomposeTimeString();
	}

	public int getTimezone() {
		return timezone;
	}

	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}

	public int getBefore() {
		return before;
	}

	public void setBefore(int before) {
		this.before = before;
	}

	public int getAfter() {
		return after;
	}

	public void setAfter(int after) {
		this.after = after;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public String getCalendarmodel() {
		return calendarmodel;
	}

	public void setCalendarmodel(String calendarmodel) {
		this.calendarmodel = calendarmodel;
	}

	@JsonIgnore
	public byte getSecond() {
		return this.second;
	}

	@JsonIgnore
	public byte getMinute() {
		return this.minute;
	}

	@JsonIgnore
	public byte getHour() {
		return this.hour;
	}

	@JsonIgnore
	public byte getDay() {
		return this.day;
	}

	@JsonIgnore
	public byte getMonth() {
		return this.month;
	}

	@JsonIgnore
	public long getYear() {
		return this.year;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		
		if(!(o instanceof JacksonInnerTime)){
			return false;
		}
		
		JacksonInnerTime other = (JacksonInnerTime)o;
		
		return (this.calendarmodel.equals(other.calendarmodel)
				&& this.year == other.year
				&& this.month == other.month
				&& this.day == other.day
				&& this.hour == other.hour
				&& this.minute == other.minute
				&& this.second == other.second
				&& this.before == other.before
				&& this.after == other.after
				&& this.timezone == other.timezone
				&& this.precision == other.precision);
	}
}
