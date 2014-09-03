package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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


@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobeCoordinate {

	private long latitude;
	private long longitude;
	private long precision;
	private String globe;
	
	public GlobeCoordinate(){}
	public GlobeCoordinate(long latitude, long longitude, long precision, String globe){
		this.latitude = latitude;
		this.longitude = longitude;
		this.precision = precision;
		this.globe = globe;
	}
	
	public long getLatitude() {
		return latitude;
	}
	
	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}
	
	public long getLongitude() {
		return longitude;
	}
	
	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}
	
	public long getPrecision() {
		return precision;
	}
	
	public void setPrecision(long precision) {
		this.precision = precision;
	}
	
	public String getGlobe() {
		return globe;
	}
	
	public void setGlobe(String globe) {
		this.globe = globe;
	}
	
	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		
		if(!(o instanceof GlobeCoordinate)){
			return false;
		}
		
		GlobeCoordinate other = (GlobeCoordinate)o;
		
		return (this.globe.equals(other.globe)
				&& this.latitude == other.latitude
				&& this.longitude == other.longitude
				&& this.precision == other.precision);
	}
}
