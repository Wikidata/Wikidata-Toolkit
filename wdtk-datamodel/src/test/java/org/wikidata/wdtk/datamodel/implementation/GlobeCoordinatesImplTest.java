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
		c1 = new GlobeCoordinatesValueImpl(12.3, 14.1, 1.0,
				GlobeCoordinatesValue.GLOBE_EARTH);
		c2 = new GlobeCoordinatesValueImpl(12.3, 14.1, 1.0,
				GlobeCoordinatesValue.GLOBE_EARTH);
	}

	@Test
	public void globeCoordinatesDataIsCorrect() {
		assertEquals(c1.getLatitude(), 12.3, 0);
		assertEquals(c1.getLongitude(), 14.1, 0);
		assertEquals(c1.getPrecision(), 1.0, 0);
		assertEquals(c1.getGlobe(), GlobeCoordinatesValue.GLOBE_EARTH);
	}

	@Test
	public void globeCoordinatesValueEqualityBasedOnContent() {
		GlobeCoordinatesValue c3 = new GlobeCoordinatesValueImpl(12.4, 14.1,
				1.0, GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue c4 = new GlobeCoordinatesValueImpl(12.3, 14.2,
				1.0, GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue c5 = new GlobeCoordinatesValueImpl(12.3, 14.1,
				0.1, GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue c6 = new GlobeCoordinatesValueImpl(12.3, 14.1,
				1.0, "http://wikidata.org/entity/Q367221");

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
	public void negativeZeroHandling() {
		GlobeCoordinatesValue cpos = new GlobeCoordinatesValueImpl(0.0, 14.1,
				1.0, GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue cneg = new GlobeCoordinatesValueImpl(-0.0, 14.1,
				1.0, GlobeCoordinatesValue.GLOBE_EARTH);

		assertEquals(cpos, cneg);
		assertEquals(cpos.hashCode(), cneg.hashCode());
	}

	@Test
	public void globeCoordinatesValueHashBasedOnContent() {
		assertEquals(c1.hashCode(), c2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void globeCoordinatesValueGlobeNotNull() {
		new GlobeCoordinatesValueImpl(12.4, 14.1, 1.0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void globeCoordinatesValueLatNotNan() {
		new GlobeCoordinatesValueImpl(Double.NaN, 14.1, 1.0,
				GlobeCoordinatesValue.GLOBE_EARTH);
	}

	@Test(expected = IllegalArgumentException.class)
	public void globeCoordinatesValueLonNotNan() {
		new GlobeCoordinatesValueImpl(12.1, Double.NaN, 1.0,
				GlobeCoordinatesValue.GLOBE_EARTH);
	}

	@Test(expected = IllegalArgumentException.class)
	public void globeCoordinatesValuePrecNotNan() {
		new GlobeCoordinatesValueImpl(12.1, 14.1, Double.NaN,
				GlobeCoordinatesValue.GLOBE_EARTH);
	}
}
