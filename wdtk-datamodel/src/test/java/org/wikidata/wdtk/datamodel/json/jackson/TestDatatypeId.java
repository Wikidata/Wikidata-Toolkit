package org.wikidata.wdtk.datamodel.json.jackson;

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

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;

import static org.junit.Assert.assertEquals;

public class TestDatatypeId extends JsonTestData {

	@Test
	public void testIriForItem() {
		JacksonDatatypeId uut = new JacksonDatatypeId(
				JacksonDatatypeId.JSON_DT_ITEM);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_ITEM);
	}

	@Test
	public void testIriForProperty() {
		JacksonDatatypeId uut = new JacksonDatatypeId(
				JacksonDatatypeId.JSON_DT_PROPERTY);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_PROPERTY);
	}

	@Test
	public void testIriForCoordinate() {
		JacksonDatatypeId uut = new JacksonDatatypeId(
				JacksonDatatypeId.JSON_DT_GLOBE_COORDINATES);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_GLOBE_COORDINATES);
	}

	@Test
	public void testIriForTime() {
		JacksonDatatypeId uut = new JacksonDatatypeId(
				JacksonDatatypeId.JSON_DT_TIME);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_TIME);
	}

	@Test
	public void testIriForString() {
		JacksonDatatypeId uut = new JacksonDatatypeId(
				JacksonDatatypeId.JSON_DT_STRING);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_STRING);
	}

	@Test
	public void testIriForQuantity() {
		JacksonDatatypeId uut = new JacksonDatatypeId(
				JacksonDatatypeId.JSON_DT_QUANTITY);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_QUANTITY);
	}

	@Test
	public void testIriForCommons() {
		JacksonDatatypeId uut = new JacksonDatatypeId(
				JacksonDatatypeId.JSON_DT_COMMONS_MEDIA);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_COMMONS_MEDIA);
	}

	@Test
	public void testIriForExternalId() {
		JacksonDatatypeId uut = new JacksonDatatypeId(
				JacksonDatatypeId.JSON_DT_EXTERNAL_ID);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_EXTERNAL_ID);
	}

	@Test
	public void testIriForMath() {
		JacksonDatatypeId uut = new JacksonDatatypeId(
				JacksonDatatypeId.JSON_DT_MATH);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_MATH);
	}

	@Test
	public void testIriForGeoShape() {
		JacksonDatatypeId uut = new JacksonDatatypeId(
				JacksonDatatypeId.JSON_DT_GEO_SHAPE);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_GEO_SHAPE);
	}

	@Test
	public void testIriForUrl() {
		JacksonDatatypeId uut = new JacksonDatatypeId(
				JacksonDatatypeId.JSON_DT_URL);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_URL);
	}

	@Test
	public void testIriForMonolingualText() {
		JacksonDatatypeId uut = new JacksonDatatypeId(
				JacksonDatatypeId.JSON_DT_MONOLINGUAL_TEXT);
		assertEquals(uut.getIri(), DatatypeIdValue.DT_MONOLINGUAL_TEXT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIriForInvalidType() {
		new JacksonDatatypeId("some wrong type");
	}
}
