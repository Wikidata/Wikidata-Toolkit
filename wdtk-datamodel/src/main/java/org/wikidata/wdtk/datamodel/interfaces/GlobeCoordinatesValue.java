package org.wikidata.wdtk.datamodel.interfaces;

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
 * Globe coordinates specify a position on some globe (usually Earth, but
 * possibly also another celestial body, such as Mars).
 * <p>
 * Altitude is not supported in this value.
 * <p>
 * All numeric data in coordinates is represented by floating point numbers. The
 * one general problem with any underlying number format is the conversion
 * between degrees (with fraction) and a degrees-minute-second view, which will
 * always lead to a loss in arithmetic precision that one has to live with.
 * <p>
 * Precision is measured in degrees, but must be a positive (non-zero) number.
 *
 * @author Markus Kroetzsch
 *
 */
public interface GlobeCoordinatesValue extends Value {

	/**
	 * Precision constant for globe coordinates that are precise to ten degrees.
	 */
	double PREC_TEN_DEGREE = 10.0;
	/**
	 * Precision constant for globe coordinates that are precise to the degree.
	 */
	double PREC_DEGREE = 1.0;
	/**
	 * Precision constant for globe coordinates that are precise to the tenth of
	 * a degree.
	 */
	double PREC_DECI_DEGREE = 0.1;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * arcminute.
	 */
	double PREC_ARCMINUTE = 1.0 / 60;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * hundredth of a degree.
	 */
	double PREC_CENTI_DEGREE = 0.01;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * thousandth of a degree.
	 */
	double PREC_MILLI_DEGREE = 0.001;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * arcsecond.
	 */
	double PREC_ARCSECOND = 1.0 / 3600;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * ten-thousandth of a degree.
	 */
	double PREC_HUNDRED_MICRO_DEGREE = 0.0001;
	/**
	 * Precision constant for globe coordinates that are precise to the tenth of
	 * an arcsecond.
	 */
	double PREC_DECI_ARCSECOND = 1.0 / 36000;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * hundred-thousandth of a degree.
	 */
	double PREC_TEN_MICRO_DEGREE = 0.00001;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * hundredth of an arcsecond.
	 */
	double PREC_CENTI_ARCSECOND = 1.0 / 360000;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * millionth of a degree.
	 */
	double PREC_MICRO_DEGREE = 0.000001;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * thousandth of an arcsecond.
	 */
	double PREC_MILLI_ARCSECOND = 1.0 / 3600000;

	/**
	 * IRI of the Earth. Used frequently to specify the globe.
	 */
	String GLOBE_EARTH = "http://www.wikidata.org/entity/Q2";
	/**
	 * IRI of the the Earth's Moon.
	 */
	String GLOBE_MOON = "http://www.wikidata.org/entity/Q405";

	/**
	 * Get the latitude of this value in degrees. For Earth, the latitude value
	 * is generally the geographic latitude (as opposed to the geocentric
	 * latitude etc.). For other celestial bodies, the meaning of the latitude
	 * can vary. It is part of the semantics of the property to specify which
	 * coordinate system should be assumed for each globe (possibly depending on
	 * further information, such as qualifiers).
	 *
	 * @return latitude in degrees
	 */
	double getLatitude();

	/**
	 * Get the longitude of this value in degrees. For celestial bodies other
	 * than Earth, the meaning of the longitude can vary. It is part of the
	 * semantics of the property to specify which coordinate system should be
	 * assumed for each globe (possibly depending on further information, such
	 * as qualifiers).
	 *
	 * @return longitude in degrees
	 */
	double getLongitude();

	/**
	 * Get the precision of the value in degrees. This value indicates that both
	 * latitude and longitude might be off by that precision. Obviously, since
	 * the absolute distance of one degree may vary depending on the
	 * coordinates, this leads to a non-uniform notion of precision. For
	 * example, precision of one whole degree at coordinates 80N, 145E is a much
	 * smaller distance from the spot than the same precision at 10N, 145E.
	 *
	 * @return precision in degrees
	 */
	double getPrecision();

	/**
	 * Get the IRI of the globe that these coordinates refer to. In most cases
	 * this is {@link GlobeCoordinatesValue#GLOBE_EARTH}.
	 *
	 * @return IRI of a globe.
	 */
	String getGlobe();

	/**
	 * Get the {@link ItemIdValue} of the globe that these coordinates refer to.
	 *
	 * @throws IllegalArgumentException if the globe is not a valid item IRI
	 */
	ItemIdValue getGlobeItemId();

}
