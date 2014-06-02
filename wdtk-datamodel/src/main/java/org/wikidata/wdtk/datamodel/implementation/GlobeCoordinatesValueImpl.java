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
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

/**
 * Implementation of {@link GlobeCoordinatesValue}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class GlobeCoordinatesValueImpl implements GlobeCoordinatesValue {

	final long latitude;
	final long longitude;
	final long precision;
	final String globeIri;

	/**
	 * Constructor.
	 * 
	 * @param latitude
	 *            the latitude of the coordinates in nanodegrees
	 * @param longitude
	 *            the longitude of the coordinates in nanodegrees
	 * @param precision
	 *            the precision of the coordinates in nanodegrees
	 * @param globeIri
	 *            IRI specifying the celestial objects of the coordinates
	 */
	GlobeCoordinatesValueImpl(long latitude, long longitude, long precision,
			String globeIri) {
		Validate.notNull(globeIri, "globe IRI must not be null");
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
		if ((precision != GlobeCoordinatesValue.PREC_TEN_DEGREE)
				&& (precision != GlobeCoordinatesValue.PREC_DEGREE)
				&& (precision != GlobeCoordinatesValue.PREC_DECI_DEGREE)
				&& (precision != GlobeCoordinatesValue.PREC_ARCMINUTE)
				&& (precision != GlobeCoordinatesValue.PREC_CENTI_DEGREE)
				&& (precision != GlobeCoordinatesValue.PREC_MILLI_DEGREE)
				&& (precision != GlobeCoordinatesValue.PREC_ARCSECOND)
				&& (precision != GlobeCoordinatesValue.PREC_HUNDRED_MICRO_DEGREE)
				&& (precision != GlobeCoordinatesValue.PREC_DECI_ARCSECOND)
				&& (precision != GlobeCoordinatesValue.PREC_TEN_MICRO_DEGREE)
				&& (precision != GlobeCoordinatesValue.PREC_CENTI_ARCSECOND)
				&& (precision != GlobeCoordinatesValue.PREC_MICRO_DEGREE)
				&& (precision != GlobeCoordinatesValue.PREC_MILLI_ARCSECOND)) {
			throw new IllegalArgumentException(
					"Precision must be one of the predefined values.");
		}
		this.latitude = latitude;
		this.longitude = longitude;
		this.precision = precision;
		this.globeIri = globeIri;
	}

	@Override
	public long getLatitude() {
		return latitude;
	}

	@Override
	public long getLongitude() {
		return longitude;
	}

	@Override
	public long getPrecision() {
		return precision;
	}

	@Override
	public String getGlobe() {
		return globeIri;
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
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

	@Override
	public String toString(){
		return "(Coordinate)" + this.latitude + "°N, " + this.longitude + "°E (±" 
				+ this.precision + ") " + this.globeIri;
	}
	
}
