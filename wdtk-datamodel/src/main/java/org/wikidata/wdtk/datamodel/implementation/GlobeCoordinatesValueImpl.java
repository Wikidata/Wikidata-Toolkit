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
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;

/**
 * Implementation of {@link GlobeCoordinatesValue}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class GlobeCoordinatesValueImpl implements GlobeCoordinatesValue {

	final double latitude;
	final double longitude;
	final double precision;
	final String globeIri;

	/**
	 * Constructor.
	 * 
	 * @param latitude
	 *            the latitude of the coordinates in degrees
	 * @param longitude
	 *            the longitude of the coordinates in degrees
	 * @param precision
	 *            the precision of the coordinates in degrees
	 * @param globeIri
	 *            IRI specifying the celestial objects of the coordinates
	 */
	GlobeCoordinatesValueImpl(double latitude, double longitude,
			double precision, String globeIri) {
		Validate.notNull(globeIri, "globe IRI must not be null");
		this.latitude = normalizeDouble(latitude);
		this.longitude = normalizeDouble(longitude);
		this.precision = normalizeDouble(precision);
		this.globeIri = globeIri;
	}

	/**
	 * Ensure that a double value is neither negative zero nor NaN.
	 * 
	 * @param value
	 *            the double to normalize
	 * @return the normalized double
	 */
	double normalizeDouble(double value) {
		if (Double.isNaN(value)) {
			throw new IllegalArgumentException("NaN is not accepted as a value");
		}
		if (value == -0.0) {
			return 0.0;
		} else {
			return value;
		}
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	@Override
	public double getPrecision() {
		return precision;
	}

	@Override
	public String getGlobe() {
		return globeIri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + globeIri.hashCode();
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(precision);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Compare two globe coordinates. The implementation compares the numeric
	 * value of the double components, which is safe since NaN is not allowed in
	 * our implementation, and since positive and negative zero are considered
	 * equal in this class.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof GlobeCoordinatesValueImpl)) {
			return false;
		}
		GlobeCoordinatesValueImpl other = (GlobeCoordinatesValueImpl) obj;
		return globeIri.equals(other.globeIri) && latitude == other.latitude
				&& longitude == other.longitude && precision == other.precision;
	}

}
