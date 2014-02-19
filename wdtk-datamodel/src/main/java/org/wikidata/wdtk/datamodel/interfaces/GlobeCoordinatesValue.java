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
 * All numeric data in coordinates is represented by limited-precision decimal
 * numbers: they are given as long integers that should be divided by 10^9 to
 * obtain the actual value in degrees. In other words, all numbers here are
 * measured in nanodegrees (degrees * 10^-9). The precision constants
 * {@link GlobeCoordinatesValue#PREC_DEGREE} etc. should be used as suitable
 * factors to transform numbers to this format rather than writing long
 * constants in code. For example, the value of 23.567 degrees in nanodegrees is
 * obtained by multiplying 23567 with
 * {@link GlobeCoordinatesValue#PREC_MILLI_DEGREE}.
 * <p>
 * Limited precision is acceptable here, because (1) coordinates are naturally
 * of limited precision, already because the whole coordinate system makes
 * simplifying assumptions about the shape of the body it refers to; (2) exact
 * calculations with coordinates are not needed in most applications; (4) exact
 * comparisons of coordinate objects, although rare in practice, should still
 * work as expected.
 * <p>
 * The one general problem with any underlying number format is the conversion
 * between degrees (with fraction) and a degrees-minute-second view, which will
 * always lead to a loss in precision that one has to live with. All values
 * should be rounded to 9 significant digits after the dot.
 * <p>
 * Precision is measured in degrees, for convenience of use, but only some
 * values are allowed there. The constants defined in this interface are based
 * on what is supported by the Wikibase user interface.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface GlobeCoordinatesValue extends Value {

	/**
	 * Precision constant for globe coordinates that are precise to ten degrees.
	 */
	static final long PREC_TEN_DEGREE = 10000000000L;
	/**
	 * Precision constant for globe coordinates that are precise to the degree.
	 */
	static final long PREC_DEGREE = 1000000000L;
	/**
	 * Precision constant for globe coordinates that are precise to the tenth of
	 * a degree.
	 */
	static final long PREC_DECI_DEGREE = 100000000L;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * arcminute.
	 */
	static final long PREC_ARCMINUTE = 16666667L;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * hundredth of a degree.
	 */
	static final long PREC_CENTI_DEGREE = 10000000L;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * thousandth of a degree.
	 */
	static final long PREC_MILLI_DEGREE = 1000000L;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * arcsecond.
	 */
	static final long PREC_ARCSECOND = 277778L;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * ten-thousandth of a degree.
	 */
	static final long PREC_HUNDRED_MICRO_DEGREE = 100000L;
	/**
	 * Precision constant for globe coordinates that are precise to the tenth of
	 * an arcsecond.
	 */
	static final long PREC_DECI_ARCSECOND = 2777778L;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * hundred-thousandth of a degree.
	 */
	static final long PREC_TEN_MICRO_DEGREE = 10000L;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * hundredth of an arcsecond.
	 */
	static final long PREC_CENTI_ARCSECOND = 277778L;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * millionth of a degree.
	 */
	static final long PREC_MICRO_DEGREE = 1000L;
	/**
	 * Precision constant for globe coordinates that are precise to the
	 * thousandth of an arcsecond.
	 */
	static final long PREC_MILLI_ARCSECOND = 278L;

	/**
	 * IRI of the earth. Used frequently to specify the globe.
	 */
	static final String GLOBE_EARTH = "http://www.wikidata.org/entity/Q2";

	/**
	 * Get the latitude of this value in nanodegrees. For Earth, the latitude
	 * value is generally the geographic latitude (as opposed to the geocentric
	 * latitude etc.). For other celestial bodies, the meaning of the latitude
	 * can vary. It is part of the semantics of the property to specify which
	 * coordinate system should be assumed for each globe (possibly depending on
	 * further information, such as qualifiers).
	 * 
	 * @return latitude in nanodegrees
	 */
	long getLatitude();

	/**
	 * Get the longitude of this value in nanodegrees. For celestial bodies
	 * other than Earth, the meaning of the longitude can vary. It is part of
	 * the semantics of the property to specify which coordinate system should
	 * be assumed for each globe (possibly depending on further information,
	 * such as qualifiers).
	 * 
	 * @return longitude in nanodegrees
	 */
	long getLongitude();

	/**
	 * Get the precision of the value in nanodegrees. This value indicates that
	 * both latitude and longitude might be off by that precision. Obviously,
	 * since the absolute distance of one degree may vary depending on the
	 * coordinates, this leads to a non-uniform notion of precision. For
	 * example, precision of one whole degree at coordinates 80N, 145E is a much
	 * smaller distance from the spot than the same precision at 10N, 145E.
	 * 
	 * @return precision in nanodegrees
	 */
	long getPrecision();

	/**
	 * Get the IRI of the globe that these coordinates refer to. In most cases
	 * this is {@link GlobeCoordinatesValue#GLOBE_EARTH}.
	 * 
	 * @return IRI of a globe.
	 */
	String getGlobe();

}
