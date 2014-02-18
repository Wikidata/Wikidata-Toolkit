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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;

public class GlobeCoordinatesImplTest {

	GlobeCoordinatesValue c1;
	GlobeCoordinatesValue c2;

	@Before
	public void setUp() throws Exception {
		c1 = new GlobeCoordinatesValueImpl(
				123 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				141 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.PREC_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
		c2 = new GlobeCoordinatesValueImpl(12300000000L, 14100000000L,
				GlobeCoordinatesValue.PREC_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
	}

	@Test
	public void globeCoordinatesDataIsCorrect() {
		assertEquals(c1.getLatitude(),
				123 * GlobeCoordinatesValue.PREC_DECI_DEGREE, 0);
		assertEquals(c1.getLongitude(),
				141 * GlobeCoordinatesValue.PREC_DECI_DEGREE, 0);
		assertEquals(c1.getPrecision(), GlobeCoordinatesValue.PREC_DEGREE, 0);
		assertEquals(c1.getGlobe(), GlobeCoordinatesValue.GLOBE_EARTH);
	}

	@Test
	public void globeCoordinatesValueEqualityBasedOnContent() {
		GlobeCoordinatesValue c3 = new GlobeCoordinatesValueImpl(
				121 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				141 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.PREC_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue c4 = new GlobeCoordinatesValueImpl(
				123 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				142 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.PREC_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue c5 = new GlobeCoordinatesValueImpl(
				123 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				141 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.PREC_ARCMINUTE,
				GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue c6 = new GlobeCoordinatesValueImpl(
				123 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				141 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.PREC_DEGREE,
				"http://wikidata.org/entity/Q367221");

		assertEquals(c1, c1);
		assertEquals(c1, c2);
		assertThat(c1, not(equalTo(c3)));
		assertThat(c1, not(equalTo(c4)));
		assertThat(c1, not(equalTo(c5)));
		assertThat(c1, not(equalTo(c6)));
		assertThat(c1, not(equalTo(null)));
		assertFalse(c1.equals(this));
	}

	@Test
	public void globeCoordinatesValueHashBasedOnContent() {
		assertEquals(c1.hashCode(), c2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void globeCoordinatesValueGlobeNotNull() {
		new GlobeCoordinatesValueImpl(
				123 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				141 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.PREC_DEGREE, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void globeCoordinatesValueOnlyAllowedPrecisions() {
		new GlobeCoordinatesValueImpl(
				123 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				141 * GlobeCoordinatesValue.PREC_DECI_DEGREE, 123456789,
				GlobeCoordinatesValue.GLOBE_EARTH);
	}

}
