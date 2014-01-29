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

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeId;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemId;
import org.wikidata.wdtk.datamodel.interfaces.ItemRecord;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyId;
import org.wikidata.wdtk.datamodel.interfaces.PropertyRecord;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.UrlValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class DataObjectFactoryImplTest {

	DataObjectFactoryImpl factory;

	@Before
	public void setUp() throws Exception {
		factory = new DataObjectFactoryImpl();
	}

	@Test
	public final void testGetItemId() {
		ItemId o1 = new ItemIdImpl("Q42", "foo");
		ItemId o2 = factory.getItemId("Q42", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetPropertyId() {
		PropertyId o1 = new PropertyIdImpl("P42", "foo");
		PropertyId o2 = factory.getPropertyId("P42", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetDatatypeId() {
		DatatypeId o1 = new DatatypeIdImpl(DatatypeId.DT_TIME);
		DatatypeId o2 = factory.getDatatypeId(DatatypeId.DT_TIME);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetUrlValue() {
		UrlValue o1 = new UrlValueImpl("http://example.org/");
		UrlValue o2 = factory.getUrlValue("http://example.org/");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetTimeValue() {
		TimeValue o1 = new TimeValueImpl(2007, (byte) 5, (byte) 12, (byte) 1,
				TimeValue.CM_GREGORIAN_PRO);
		TimeValue o2 = factory.getTimeValue(2007, (byte) 5, (byte) 12,
				(byte) 1, TimeValue.CM_GREGORIAN_PRO);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetGlobeCoordinatesValue() {
		GlobeCoordinatesValue o1 = new GlobeCoordinatesValueImpl(90.0, 190.5,
				1, GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue o2 = factory.getGlobeCoordinatesValue(90.0,
				190.5, 1, GlobeCoordinatesValue.GLOBE_EARTH);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetStringValue() {
		StringValue o1 = new StringValueImpl("foo");
		StringValue o2 = factory.getStringValue("foo");
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
	public final void testGetPropertyRecord() {
		PropertyRecord o1 = new PropertyRecordImpl(factory.getPropertyId("P42",
				"foo"), Collections.<String, String> emptyMap(),
				Collections.<String, String> emptyMap(),
				Collections.<String, List<String>> emptyMap(),
				factory.getDatatypeId(DatatypeId.DT_TIME));
		PropertyRecord o2 = factory.getPropertyRecord(
				factory.getPropertyId("P42", "foo"),
				Collections.<String, String> emptyMap(),
				Collections.<String, String> emptyMap(),
				Collections.<String, List<String>> emptyMap(),
				factory.getDatatypeId(DatatypeId.DT_TIME));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetItemRecord() {
		ItemRecord o1 = new ItemRecordImpl(factory.getItemId("Q42", "foo"),
				Collections.<String, String> emptyMap(),
				Collections.<String, String> emptyMap(),
				Collections.<String, List<String>> emptyMap(),
				Collections.<Statement> emptyList(),
				Collections.<String, SiteLink> emptyMap());
		ItemRecord o2 = factory.getItemRecord(factory.getItemId("Q42", "foo"),
				Collections.<String, String> emptyMap(),
				Collections.<String, String> emptyMap(),
				Collections.<String, List<String>> emptyMap(),
				Collections.<Statement> emptyList(),
				Collections.<String, SiteLink> emptyMap());
		assertEquals(o1, o2);
	}

}
