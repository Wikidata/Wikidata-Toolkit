package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;

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

/**
 * Helper object that represents the JSON object structure that is used to
 * represent values of type
 * {@link JacksonValue#JSON_VALUE_TYPE_GLOBE_COORDINATES}.
 *
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonInnerGlobeCoordinates {

	private double latitude = 0;
	private double longitude = 0;
	private double precision = 0;
	private String globe = GlobeCoordinatesValue.GLOBE_EARTH;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonInnerGlobeCoordinates() {
	}

	/**
	 * TODO Review the utility of this constructor.
	 *
	 * @param latitude
	 * @param longitude
	 * @param precision
	 * @param globe
	 */
	public JacksonInnerGlobeCoordinates(double latitude, double longitude,
			double precision, String globe) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.precision = precision;
		this.globe = globe;
	}

	/**
	 * Returns the latitude.
	 *
	 * @see GlobeCoordinatesValue#getLatitude()
	 * @return latitude
	 */
	public double getLatitude() {
		return this.latitude;
	}

	/**
	 * Sets the latitude to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param latitude
	 *            new value
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * Returns the longitude.
	 *
	 * @see GlobeCoordinatesValue#getLongitude()
	 * @return longitude
	 */
	public double getLongitude() {
		return this.longitude;
	}

	/**
	 * Sets the longitude to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param longitude
	 *            new value
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * Returns the precision.
	 *
	 * @see GlobeCoordinatesValue#getPrecision()
	 * @return precision
	 */
	public double getPrecision() {
		return this.precision;
	}

	/**
	 * Sets the precision to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param precision
	 *            new value
	 */
	public void setPrecision(double precision) {
		this.precision = precision;
	}

	/**
	 * Returns the globe.
	 *
	 * @see GlobeCoordinatesValue#getGlobe()
	 * @return globe
	 */
	public String getGlobe() {
		return this.globe;
	}

	/**
	 * Sets the globe to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param globe
	 *            new value
	 */
	public void setGlobe(String globe) {
		this.globe = globe;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof JacksonInnerGlobeCoordinates)) {
			return false;
		}

		JacksonInnerGlobeCoordinates other = (JacksonInnerGlobeCoordinates) o;

		return (this.globe.equals(other.globe)
				&& this.latitude == other.latitude
				&& this.longitude == other.longitude && this.precision == other.precision);
	}
}
