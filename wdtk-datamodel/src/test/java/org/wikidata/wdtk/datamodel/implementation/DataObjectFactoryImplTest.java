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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeId;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class DataObjectFactoryImplTest {

	DataObjectFactoryImpl factory;

	@Before
	public void setUp() throws Exception {
		factory = new DataObjectFactoryImpl();
	}

	@Test
	public final void testGetItemId() {
		ItemIdValue o1 = new ItemIdValueImpl("Q42", "foo");
		ItemIdValue o2 = factory.getItemId("Q42", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetPropertyId() {
		PropertyIdValue o1 = new PropertyIdValueImpl("P42", "foo");
		PropertyIdValue o2 = factory.getPropertyId("P42", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetDatatypeId() {
		DatatypeId o1 = new DatatypeIdImpl(DatatypeId.DT_TIME);
		DatatypeId o2 = factory.getDatatypeId(DatatypeId.DT_TIME);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetTimeValue() {
		TimeValue o1 = new TimeValueImpl(2007, (byte) 5, (byte) 12, (byte) 10,
				(byte) 45, (byte) 00, TimeValue.PREC_DAY, 0, 1, 60,
				TimeValue.CM_GREGORIAN_PRO);
		TimeValue o2 = factory.getTimeValue(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 00, TimeValue.PREC_DAY, 0, 1, 60,
				TimeValue.CM_GREGORIAN_PRO);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetGlobeCoordinatesValue() {
		GlobeCoordinatesValue o1 = new GlobeCoordinatesValueImpl(
				90 * GlobeCoordinatesValue.PREC_DEGREE,
				1905 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue o2 = factory.getGlobeCoordinatesValue(
				90 * GlobeCoordinatesValue.PREC_DEGREE,
				1905 * GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetStringValue() {
		StringValue o1 = new StringValueImpl("foo");
		StringValue o2 = factory.getStringValue("foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetQuantityValue() {
		BigDecimal nv = new BigDecimal(
				"0.123456789012345678901234567890123456789");
		BigDecimal lb = new BigDecimal(
				"0.123456789012345678901234567890123456788");
		BigDecimal ub = new BigDecimal(
				"0.123456789012345678901234567890123456790");
		QuantityValue o1 = new QuantityValueImpl(nv, lb, ub);
		QuantityValue o2 = factory.getQuantityValue(nv, lb, ub);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetValueSnak() {
		ValueSnak o1 = new ValueSnakImpl(factory.getPropertyId("P42", "foo"),
				factory.getStringValue("foo"));
		ValueSnak o2 = factory.getValueSnak(
				factory.getPropertyId("P42", "foo"),
				factory.getStringValue("foo"));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetSomeValueSnak() {
		SomeValueSnak o1 = new SomeValueSnakImpl(factory.getPropertyId("P42",
				"foo"));
		SomeValueSnak o2 = factory.getSomeValueSnak(factory.getPropertyId(
				"P42", "foo"));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetNoValueSnak() {
		NoValueSnak o1 = new NoValueSnakImpl(
				factory.getPropertyId("P42", "foo"));
		NoValueSnak o2 = factory.getNoValueSnak(factory.getPropertyId("P42",
				"foo"));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetStatement() {
		Statement o1 = new StatementImpl(factory.getItemId("Q42", "foo"),
				factory.getNoValueSnak(factory.getPropertyId("P42", "foo")),
				Collections.<Snak> emptyList(),
				Collections.<List<? extends Snak>> emptyList(),
				StatementRank.NORMAL);
		Statement o2 = factory.getStatement(factory.getItemId("Q42", "foo"),
				factory.getNoValueSnak(factory.getPropertyId("P42", "foo")),
				Collections.<Snak> emptyList(),
				Collections.<List<? extends Snak>> emptyList(),
				StatementRank.NORMAL);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetSiteLink() {
		SiteLink o1 = new SiteLinkImpl("SOLID", "enwiki",
				"http://en.wikipedia.org", Collections.<String> emptyList());
		SiteLink o2 = factory.getSiteLink("SOLID", "enwiki",
				"http://en.wikipedia.org", Collections.<String> emptyList());
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetPropertyDocument() {
		PropertyDocument o1 = new PropertyDocumentImpl(factory.getPropertyId(
				"P42", "foo"), Collections.<String, String> emptyMap(),
				Collections.<String, String> emptyMap(),
				Collections.<String, List<String>> emptyMap(),
				factory.getDatatypeId(DatatypeId.DT_TIME));
		PropertyDocument o2 = factory.getPropertyDocument(
				factory.getPropertyId("P42", "foo"),
				Collections.<String, String> emptyMap(),
				Collections.<String, String> emptyMap(),
				Collections.<String, List<String>> emptyMap(),
				factory.getDatatypeId(DatatypeId.DT_TIME));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetItemDocument() {
		ItemDocument o1 = new ItemDocumentImpl(factory.getItemId("Q42", "foo"),
				Collections.<String, String> emptyMap(),
				Collections.<String, String> emptyMap(),
				Collections.<String, List<String>> emptyMap(),
				Collections.<Statement> emptyList(),
				Collections.<String, SiteLink> emptyMap());
		ItemDocument o2 = factory.getItemDocument(
				factory.getItemId("Q42", "foo"),
				Collections.<String, String> emptyMap(),
				Collections.<String, String> emptyMap(),
				Collections.<String, List<String>> emptyMap(),
				Collections.<Statement> emptyList(),
				Collections.<String, SiteLink> emptyMap());
		assertEquals(o1, o2);
	}

}
