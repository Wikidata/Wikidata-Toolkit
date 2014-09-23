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

import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


// TODO test
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonValueGlobeCoordinates extends JacksonValue implements GlobeCoordinatesValue {

	private JacksonInnerGlobeCoordinate value;
	
	public JacksonValueGlobeCoordinates(){
		super(typeCoordinate);
	}
	public JacksonValueGlobeCoordinates(JacksonInnerGlobeCoordinate value){
		super(typeCoordinate);
		this.value = value;
	}
	
	public JacksonInnerGlobeCoordinate getValue() {
		return value;
	}

	public void GlobeCoordinate(JacksonInnerGlobeCoordinate value) {
		this.value = value;
	}
	
	@JsonIgnore
	@Override
	public long getLatitude() {
		return this.value.getLatitude();
	}
	
	@JsonIgnore
	@Override
	public long getLongitude() {
		return this.value.getLongitude();
	}
	
	@JsonIgnore
	@Override
	public long getPrecision() {
		return this.value.getPrecision();
	}
	
	@JsonIgnore
	@Override
	public String getGlobe() {
		return this.value.getGlobe();
	}
	
	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}
}
