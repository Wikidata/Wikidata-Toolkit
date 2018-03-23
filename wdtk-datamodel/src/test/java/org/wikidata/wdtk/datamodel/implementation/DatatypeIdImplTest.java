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

import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;

public class DatatypeIdImplTest {

	private final DatatypeIdImpl d1 = new DatatypeIdImpl(DatatypeIdValue.DT_ITEM);
	private final DatatypeIdImpl d2 = new DatatypeIdImpl("http://wikiba.se/ontology#WikibaseItem");
	private final DatatypeIdImpl d3 = new DatatypeIdImpl(DatatypeIdValue.DT_TIME);

	@Test(expected = NullPointerException.class)
	public void datatypeIdNotNull() {
		new DatatypeIdImpl((String) null);
	}

	@Test
	public void equalityBasedOnContent() {
		assertEquals(d1, d1);
		assertEquals(d1, d2);
		assertNotEquals(d1, d3);
		assertNotEquals(d1, null);
		assertNotEquals(d1, new StringValueImpl("foo"));
	}

	@Test
	public void hashBasedOnContent() {
		assertEquals(d1.hashCode(), d2.hashCode());
	}

	@Test
	public void testIriForItem() {
		assertEquals(
				DatatypeIdImpl.getDatatypeIriFromJsonDatatype(DatatypeIdImpl.JSON_DT_ITEM),
				DatatypeIdValue.DT_ITEM);
	}

	@Test
	public void testIriForProperty() {
		assertEquals(
				DatatypeIdImpl.getDatatypeIriFromJsonDatatype(DatatypeIdImpl.JSON_DT_PROPERTY),
				DatatypeIdValue.DT_PROPERTY);
	}

	@Test
	public void testIriForCoordinate() {
		assertEquals(
				DatatypeIdImpl.getDatatypeIriFromJsonDatatype(DatatypeIdImpl.JSON_DT_GLOBE_COORDINATES),
				DatatypeIdValue.DT_GLOBE_COORDINATES);
	}

	@Test
	public void testIriForTime() {
		assertEquals(
				DatatypeIdImpl.getDatatypeIriFromJsonDatatype(DatatypeIdImpl.JSON_DT_TIME),
				DatatypeIdValue.DT_TIME);
	}

	@Test
	public void testIriForString() {
		assertEquals(
				DatatypeIdImpl.getDatatypeIriFromJsonDatatype(DatatypeIdImpl.JSON_DT_STRING),
				DatatypeIdValue.DT_STRING);
	}

	@Test
	public void testIriForQuantity() {
		assertEquals(
				DatatypeIdImpl.getDatatypeIriFromJsonDatatype(DatatypeIdImpl.JSON_DT_QUANTITY),
				DatatypeIdValue.DT_QUANTITY);
	}

	@Test
	public void testIriForCommons() {
		assertEquals(
				DatatypeIdImpl.getDatatypeIriFromJsonDatatype(DatatypeIdImpl.JSON_DT_COMMONS_MEDIA),
				DatatypeIdValue.DT_COMMONS_MEDIA);
	}

	@Test
	public void testIriForExternalId() {
		assertEquals(
				DatatypeIdImpl.getDatatypeIriFromJsonDatatype(DatatypeIdImpl.JSON_DT_EXTERNAL_ID),
				DatatypeIdValue.DT_EXTERNAL_ID);
	}

	@Test
	public void testIriForMath() {
		assertEquals(
				DatatypeIdImpl.getDatatypeIriFromJsonDatatype(DatatypeIdImpl.JSON_DT_MATH),
				DatatypeIdValue.DT_MATH);
	}

	@Test
	public void testIriForGeoShape() {
		assertEquals(
				DatatypeIdImpl.getDatatypeIriFromJsonDatatype(DatatypeIdImpl.JSON_DT_GEO_SHAPE),
				DatatypeIdValue.DT_GEO_SHAPE);
	}

	@Test
	public void testIriForUrl() {
		assertEquals(
				DatatypeIdImpl.getDatatypeIriFromJsonDatatype(DatatypeIdImpl.JSON_DT_URL),
				DatatypeIdValue.DT_URL);
	}

	@Test
	public void testIriForMonolingualText() {
		assertEquals(
				DatatypeIdImpl.getDatatypeIriFromJsonDatatype(DatatypeIdImpl.JSON_DT_MONOLINGUAL_TEXT),
				DatatypeIdValue.DT_MONOLINGUAL_TEXT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIriForInvalidType() {
		DatatypeIdImpl.getDatatypeIriFromJsonDatatype("some wrong type");
	}

	@Test
	public void testJsonForItem() {
		assertEquals(
				DatatypeIdImpl.getJsonDatatypeFromDatatypeIri(DatatypeIdImpl.DT_ITEM),
				DatatypeIdImpl.JSON_DT_ITEM);
	}

	@Test
	public void testJsonForProperty() {
		assertEquals(
				DatatypeIdImpl.getJsonDatatypeFromDatatypeIri(DatatypeIdImpl.DT_PROPERTY),
				DatatypeIdImpl.JSON_DT_PROPERTY);
	}

	@Test
	public void testJsonForCoordinate() {
		assertEquals(
				DatatypeIdImpl.getJsonDatatypeFromDatatypeIri(DatatypeIdImpl.DT_GLOBE_COORDINATES),
				DatatypeIdImpl.JSON_DT_GLOBE_COORDINATES);
	}

	@Test
	public void testJsonForTime() {
		assertEquals(
				DatatypeIdImpl.getJsonDatatypeFromDatatypeIri(DatatypeIdImpl.DT_TIME),
				DatatypeIdImpl.JSON_DT_TIME);
	}

	@Test
	public void testJsonForString() {
		assertEquals(
				DatatypeIdImpl.getJsonDatatypeFromDatatypeIri(DatatypeIdImpl.DT_STRING),
				DatatypeIdImpl.JSON_DT_STRING);
	}

	@Test
	public void testJsonForQuantity() {
		assertEquals(
				DatatypeIdImpl.getJsonDatatypeFromDatatypeIri(DatatypeIdImpl.DT_QUANTITY),
				DatatypeIdImpl.JSON_DT_QUANTITY);
	}

	@Test
	public void testJsonForCommons() {
		assertEquals(
				DatatypeIdImpl.getJsonDatatypeFromDatatypeIri(DatatypeIdImpl.DT_COMMONS_MEDIA),
				DatatypeIdImpl.JSON_DT_COMMONS_MEDIA);
	}

	@Test
	public void testJsonForExternalId() {
		assertEquals(
				DatatypeIdImpl.getJsonDatatypeFromDatatypeIri(DatatypeIdImpl.DT_EXTERNAL_ID),
				DatatypeIdImpl.JSON_DT_EXTERNAL_ID);
	}

	@Test
	public void testJsonForMath() {
		assertEquals(
				DatatypeIdImpl.getJsonDatatypeFromDatatypeIri(DatatypeIdImpl.DT_MATH),
				DatatypeIdImpl.JSON_DT_MATH);
	}

	@Test
	public void testJsonForGeoShape() {
		assertEquals(
				DatatypeIdImpl.getJsonDatatypeFromDatatypeIri(DatatypeIdImpl.DT_GEO_SHAPE),
				DatatypeIdImpl.JSON_DT_GEO_SHAPE);
	}

	@Test
	public void testJsonForUrl() {
		assertEquals(
				DatatypeIdImpl.getJsonDatatypeFromDatatypeIri(DatatypeIdImpl.DT_URL),
				DatatypeIdImpl.JSON_DT_URL);
	}

	@Test
	public void testJsonForMonolingualText() {
		assertEquals(
				DatatypeIdImpl.getJsonDatatypeFromDatatypeIri(DatatypeIdImpl.DT_MONOLINGUAL_TEXT),
				DatatypeIdImpl.JSON_DT_MONOLINGUAL_TEXT);
	}
}
