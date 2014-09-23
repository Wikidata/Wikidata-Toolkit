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

import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// TODO test
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonValueTime extends JacksonValue implements TimeValue {
	
	private JacksonInnerTime value;
	
	public JacksonValueTime(){
		super(typeTime);
	}
	public JacksonValueTime(JacksonInnerTime value){
		super(typeTime);
		this.value = value;
	}
	
	
	public JacksonInnerTime getValue() {
		return value;
	}

	public void setValue(JacksonInnerTime value) {
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
	
	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}
}
