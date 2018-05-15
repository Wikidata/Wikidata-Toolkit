package org.wikidata.wdtk.datamodel.implementation;

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

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Jackson implementation of {@link GlobeCoordinatesValue}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 * @author Markus Kroetzsch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize()
public class GlobeCoordinatesValueImpl extends ValueImpl implements
		GlobeCoordinatesValue {

	/**
	 * Inner helper object to store the actual data. Used to get the nested JSON
	 * structure that is required here.
	 */
	private final JacksonInnerGlobeCoordinates value;
	
	/**
	 * Constructor.
	 * 
	 * @param latitude
	 *            the latitude of the coordinates in degrees
	 * @param longitude
	 *            the longitude of the coordinates in degrees
	 * @param precision
	 *            the precision of the coordinates in degrees
	 * @param globe
	 *            IRI specifying the celestial objects of the coordinates
	 */
	public GlobeCoordinatesValueImpl(double latitude, double longitude,
			double precision, String globe) {
		super(JSON_VALUE_TYPE_GLOBE_COORDINATES);
		this.value = new JacksonInnerGlobeCoordinates(latitude, longitude,
				precision, globe);
	}

	
	/**
	 * Constructor for deserialization from JSON via Jackson.
	 */
	@JsonCreator
	GlobeCoordinatesValueImpl(
			@JsonProperty("value") JacksonInnerGlobeCoordinates innerCoordinates) {
		super(JSON_VALUE_TYPE_GLOBE_COORDINATES);
		this.value = innerCoordinates;
	}

	/**
	 * Returns the inner value helper object. Only for use by Jackson during
	 * serialization.
	 *
	 * @return the inner globe coordinates value
	 */
	public JacksonInnerGlobeCoordinates getValue() {
		return value;
	}

	@JsonIgnore
	@Override
	public double getLatitude() {
		return this.value.getLatitude();
	}

	@JsonIgnore
	@Override
	public double getLongitude() {
		return this.value.getLongitude();
	}

	@JsonIgnore
	@Override
	public double getPrecision() {
		return this.value.getPrecision();
	}

	@JsonIgnore
	@Override
	public String getGlobe() {
		return this.value.getGlobe();
	}

	@JsonIgnore
	@Override
	public ItemIdValue getGlobeItemId() {
		return ItemIdValueImpl.fromIri(this.value.getGlobe());
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsGlobeCoordinatesValue(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

	/**
	 * Helper object that represents the JSON object structure of the value.
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class JacksonInnerGlobeCoordinates {

		private final double latitude;
		private final double longitude;
		private final double precision;
		private final String globe;

		/**
		 * Constructor. Creates an oject that can be populated during JSON
		 * deserialization. Should only be used by Jackson for this very purpose.
		 */
		@JsonCreator
		JacksonInnerGlobeCoordinates(
				@JsonProperty("latitude") double latitude,
				@JsonProperty("longitude") double longitude,
				@JsonProperty("precision") double precision,
				@JsonProperty("globe") String globe)  {
			Validate.notNull(globe, "globe IRI must not be null");
			if ((latitude > 90 * PREC_DEGREE)
					|| (latitude < -90 * PREC_DEGREE)) {
				throw new IllegalArgumentException(
						"Latitude must be between 90 degrees and -90 degrees.");
			}
			if ((longitude > 360 * PREC_DEGREE)
					|| (longitude < -360 * PREC_DEGREE)) {
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
				this.precision = PREC_ARCSECOND;
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
	}
}
