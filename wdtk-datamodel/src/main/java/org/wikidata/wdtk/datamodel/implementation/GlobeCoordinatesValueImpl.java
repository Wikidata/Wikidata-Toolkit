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

import java.io.Serializable;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

/**
 * Implementation of {@link GlobeCoordinatesValue}.
 *
 * @author Markus Kroetzsch
 *
 */
public class GlobeCoordinatesValueImpl implements GlobeCoordinatesValue,
		Serializable {

	private static final long serialVersionUID = 5232034046447738117L;

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
		if (precision <= 0) {
			throw new IllegalArgumentException(
					"Precision must be positive. Given value was " + precision
							+ ".");
		}
		this.latitude = latitude;
		this.longitude = longitude;
		this.precision = precision;
		this.globeIri = globeIri;
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

}
