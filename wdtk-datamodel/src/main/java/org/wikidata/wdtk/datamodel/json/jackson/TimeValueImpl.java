package org.wikidata.wdtk.datamodel.json.jackson;

import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.Time;

import com.fasterxml.jackson.annotation.JsonIgnore;

// TODO test
public class TimeValueImpl extends ValueImpl implements TimeValue {
	
	private Time value;
	
	public TimeValueImpl(){
		super(typeTime);
	}
	public TimeValueImpl(Time value){
		super(typeTime);
		this.value = value;
	}
	
	
	public Time getValue() {
		return value;
	}

	public void setValue(Time value) {
		this.value = value;
	}
	
	@JsonIgnore
	@Override
	public long getYear() {
		return this.value.getYear();
	}
	
	@JsonIgnore
	@Override
	public byte getMonth() {
		return this.value.getMonth();
	}
	
	@JsonIgnore
	@Override
	public byte getDay() {
		return this.value.getDay();
	}
	
	@JsonIgnore
	@Override
	public byte getHour() {
		return this.value.getHour();
	}
	
	@JsonIgnore
	@Override
	public byte getMinute() {
		return this.value.getMinute();
	}
	
	@JsonIgnore
	@Override
	public byte getSecond() {
		return this.value.getSecond();
	}
	
	@JsonIgnore
	@Override
	public String getPreferredCalendarModel() {
		return this.value.getCalendarmodel();
	}
	
	@JsonIgnore
	@Override
	public byte getPrecision() {
		return (byte)this.value.getPrecision();
	}
	
	@JsonIgnore
	@Override
	public int getTimezoneOffset() {
		return this.value.getTimezone();
	}
	
	@JsonIgnore
	@Override
	public int getBeforeTolerance() {
		return this.value.getBefore();
	}
	
	@JsonIgnore
	@Override
	public int getAfterTolerance() {
		return this.value.getAfter();
	}
}
