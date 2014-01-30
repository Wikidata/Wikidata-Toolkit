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
 * 
 * Altitude is not currently supported by Wikibase.
 * 
 * All numeric data in coordinates is represented by double-precision floating
 * point numbers. This results in limited precision, but this is acceptable
 * here, because (1) coordinates are naturally of limited precision today,
 * already because the whole coordinate system makes simplifying assumptions
 * about the shape of the body it refers to; (2) exact calculations with
 * coordinates are not needed in most applications; (3) Wikibase uses
 * double-precision floats (based on PHP) internally for calculating with
 * coordinates as well; (4) exact comparisons of coordinate objects, although
 * rare in practice, should still work as expected. The one general problem with
 * any underlying number format is the conversion between degrees (with
 * fraction) and a degrees-minute-second view, which will always lead to a loss
 * in precision that one has to live with.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface GlobeCoordinatesValue extends Value {

	/**
	 * IRI of the earth. Used frequently to specify the globe.
	 */
	static final String GLOBE_EARTH = "http://www.wikidata.org/entity/Q2";

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
	public double getLatitude();

	/**
	 * Get the longitude of this value in degrees. For celestial bodies other
	 * than Earth, the meaning of the longitude can vary. It is part of the
	 * semantics of the property to specify which coordinate system should be
	 * assumed for each globe (possibly depending on further information, such
	 * as qualifiers).
	 * 
	 * @return longitude in degrees
	 */
	public double getLongitude();

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
	public double getPrecision();

	/**
	 * Get the IRI of the globe that these coordinates refer to. In most cases
	 * this is {@link GlobeCoordinatesValue#GLOBE_EARTH}.
	 * 
	 * @return IRI of a globe.
	 */
	public String getGlobe();

}
