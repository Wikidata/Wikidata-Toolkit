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

import static org.junit.Assert.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;

import java.io.IOException;

public class GlobeCoordinatesValueImplTest {

	private final ObjectMapper mapper = new ObjectMapper();

	private final GlobeCoordinatesValue c1 = new GlobeCoordinatesValueImpl(12.3, 14.1,
			GlobeCoordinatesValue.PREC_DEGREE,
			GlobeCoordinatesValue.GLOBE_EARTH);
	private final GlobeCoordinatesValue c2 = new GlobeCoordinatesValueImpl(12.3, 14.1,
			GlobeCoordinatesValue.PREC_DEGREE,
			GlobeCoordinatesValue.GLOBE_EARTH);
	private final GlobeCoordinatesValue c3 = new GlobeCoordinatesValueImpl(12.3, 14.1,
			GlobeCoordinatesValue.PREC_DEGREE,
			"earth");
	private final String JSON_GLOBE_COORDINATES_VALUE = "{\"type\":\""
			+ ValueImpl.JSON_VALUE_TYPE_GLOBE_COORDINATES
			+ "\", \"value\":{\"latitude\":12.3,\"longitude\":14.1,\"precision\":1.0,\"globe\":\"http://www.wikidata.org/entity/Q2\"}}";

	@Test
	public void dataIsCorrect() {
		assertEquals(c1.getLatitude(), 12.3, 0);
		assertEquals(c1.getLongitude(), 14.1, 0);
		assertEquals(c1.getPrecision(), GlobeCoordinatesValue.PREC_DEGREE, 0);
		assertEquals(c1.getGlobe(), GlobeCoordinatesValue.GLOBE_EARTH);
	}

	@Test
	public void getGlobeItemId() {
		assertEquals(new ItemIdValueImpl("Q2", "http://www.wikidata.org/entity/"), c1.getGlobeItemId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void getGlobeItemIdInvalidIri() {
		c3.getGlobeItemId();
	}

	@Test
	public void equalityBasedOnContent() {
		GlobeCoordinatesValue gcDiffLatitude = new GlobeCoordinatesValueImpl(
				12.1, 14.1, GlobeCoordinatesValue.PREC_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue gcDiffLongitude = new GlobeCoordinatesValueImpl(
				12.3, 14.2, GlobeCoordinatesValue.PREC_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue gcDiffPrecision = new GlobeCoordinatesValueImpl(
				12.3, 14.1, GlobeCoordinatesValue.PREC_MILLI_ARCSECOND,
				GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue gcDiffGlobe = new GlobeCoordinatesValueImpl(12.3,
				14.1, GlobeCoordinatesValue.PREC_DEGREE,
				"http://wikidata.org/entity/Q367221");

		assertEquals(c1, c1);
		assertEquals(c1, c2);
		assertNotEquals(c1, gcDiffLatitude);
		assertNotEquals(c1, gcDiffLongitude);
		assertNotEquals(c1, gcDiffPrecision);
		assertNotEquals(c1, gcDiffGlobe);
		assertNotEquals(c1, null);
		assertNotEquals(c1, this);
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(c1.hashCode(), c2.hashCode());
	}

	@Test(expected = NullPointerException.class)
	public void globeNotNull() {
		new GlobeCoordinatesValueImpl(12.3, 14.1,
				GlobeCoordinatesValue.PREC_DEGREE, null);
	}

	@Test
	public void onlyAllowedPrecisions() {
		GlobeCoordinatesValue v = new GlobeCoordinatesValueImpl(12.3, 14.1, 0.0,
				GlobeCoordinatesValue.GLOBE_EARTH);
		assertTrue(v.getPrecision() > 0.);
	}

	@Test(expected = IllegalArgumentException.class)
	public void latitudeWithinUpperRange() {
		new GlobeCoordinatesValueImpl(91.0, 270.0,
				GlobeCoordinatesValue.PREC_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
	}

	@Test(expected = IllegalArgumentException.class)
	public void latitudeWithinLowerRange() {
		new GlobeCoordinatesValueImpl(-91.0, 270.0,
				GlobeCoordinatesValue.PREC_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
	}

	@Test(expected = IllegalArgumentException.class)
	public void longitudeWithinUpperRange() {
		new GlobeCoordinatesValueImpl(45.0, 500.0,
				GlobeCoordinatesValue.PREC_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
	}

	@Test(expected = IllegalArgumentException.class)
	public void longitudeWithinLowerRange() {
		new GlobeCoordinatesValueImpl(45.0, -500.0,
				GlobeCoordinatesValue.PREC_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
	}

	@Test
	public void testToJson() throws JsonProcessingException {
		JsonComparator.compareJsonStrings(JSON_GLOBE_COORDINATES_VALUE, mapper.writeValueAsString(c1));
	}

	@Test
	public void testToJava() throws IOException {
		assertEquals(c1, mapper.readValue(JSON_GLOBE_COORDINATES_VALUE, ValueImpl.class));
	}

}
