package org.wikidata.wdtk.datamodel.implementation.json;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.implementation.ValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
 * {@link ValueImpl#JSON_VALUE_TYPE_GLOBE_COORDINATES}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonInnerGlobeCoordinates {

	static final Logger logger = LoggerFactory
			.getLogger(JacksonInnerGlobeCoordinates.class);

	private final double latitude;
	private final double longitude;
	private final double precision;
	private final String globe;

	/**
	 * Constructor. Creates an oject that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	@JsonCreator
	public JacksonInnerGlobeCoordinates(
			@JsonProperty("latitude") double latitude,
			@JsonProperty("longitude") double longitude,
			@JsonProperty("precision") double precision,
			@JsonProperty("globe") String globe)  {
		Validate.notNull(globe, "globe IRI must not be null");
		if ((latitude > 90 * GlobeCoordinatesValue.PREC_DEGREE)
				|| (latitude < -90 * GlobeCoordinatesValue.PREC_DEGREE)) {
			throw new IllegalArgumentException(
					"Latitude must be between 90 degrees and -90 degrees.");
		}
		if ((longitude > 360 * GlobeCoordinatesValue.PREC_DEGREE)
				|| (longitude < -360 * GlobeCoordinatesValue.PREC_DEGREE)) {
			throw new IllegalArgumentException(
					"Longitude must be between -360 degrees and +360 degrees.");
		}
		this.latitude = latitude;
		this.longitude = longitude;
		if (precision <= 0.0) {
			// We just do this silently because it is so common in the data.
			// Precision "0" does not make sense for a physical quantity.
			// Automatic precision does not make sense for floating point
			// values. "0" also is commonly produced from "null" in JSON.
			this.precision = GlobeCoordinatesValue.PREC_ARCSECOND;
		} else {
			this.precision = precision;
		}
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
	 * Returns the longitude.
	 *
	 * @see GlobeCoordinatesValue#getLongitude()
	 * @return longitude
	 */
	public double getLongitude() {
		return this.longitude;
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
	 * Returns the globe.
	 *
	 * @see GlobeCoordinatesValue#getGlobe()
	 * @return globe
	 */
	public String getGlobe() {
		return this.globe;
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
