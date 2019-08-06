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

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.DatamodelConverterTest.ValueType;
import org.wikidata.wdtk.datamodel.interfaces.*;

public class DataObjectFactoryImplTest {

	private final DataObjectFactory factory = new DataObjectFactoryImpl();

	@Test
	public final void testGetItemId() {
		ItemIdValue o1 = new ItemIdValueImpl("Q42", "foo");
		ItemIdValue o2 = factory.getItemIdValue("Q42", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetLexemeId() {
		LexemeIdValue o1 = new LexemeIdValueImpl("L42", "foo");
		LexemeIdValue o2 = factory.getLexemeIdValue("L42", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetFormId() {
		FormIdValue o1 = new FormIdValueImpl("L42-F1", "foo");
		FormIdValue o2 = factory.getFormIdValue("L42-F1", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetSenseId() {
		SenseIdValue o1 = new SenseIdValueImpl("L42-S1", "foo");
		SenseIdValue o2 = factory.getSenseIdValue("L42-S1", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetPropertyId() {
		PropertyIdValue o1 = new PropertyIdValueImpl("P42", "foo");
		PropertyIdValue o2 = factory.getPropertyIdValue("P42", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetMediaInfoId() {
		MediaInfoIdValue o1 = new MediaInfoIdValueImpl("M22", "foo");
		MediaInfoIdValue o2 = factory.getMediaInfoIdValue("M22", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetDatatypeId() {
		DatatypeIdValue o1 = new DatatypeIdImpl(DatatypeIdValue.DT_TIME);
		DatatypeIdValue o2 = factory.getDatatypeIdValue(DatatypeIdValue.DT_TIME);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetTimeValue() {
		TimeValue o1 = new TimeValueImpl(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_DAY, 0, 1, 60,
				TimeValue.CM_GREGORIAN_PRO);
		TimeValue o2 = factory.getTimeValue(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_DAY, 0, 1, 60,
				TimeValue.CM_GREGORIAN_PRO);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetGlobeCoordinatesValue() {
		GlobeCoordinatesValue o1 = new GlobeCoordinatesValueImpl(90.0,
				190.5, GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue o2 = factory.getGlobeCoordinatesValue(90.0,
				190.5, GlobeCoordinatesValue.PREC_DECI_DEGREE,
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
	public final void testGetMonolingualTextValue() {
		MonolingualTextValue o1 = new MonolingualTextValueImpl("foo", "en");
		MonolingualTextValue o2 = factory.getMonolingualTextValue("foo", "en");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetQuantityValue() {
		BigDecimal nv = new BigDecimal("0.123456789012345678901234567890123456789");
		BigDecimal lb = new BigDecimal("0.123456789012345678901234567890123456788");
		BigDecimal ub = new BigDecimal("0.123456789012345678901234567890123456790");
		QuantityValue o1 = new QuantityValueImpl(nv, lb, ub, "unit");
		QuantityValue o2 = factory.getQuantityValue(nv, lb, ub, "unit");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetQuantityValueNoUnit() {
		BigDecimal nv = new BigDecimal(
				"0.123456789012345678901234567890123456789");
		BigDecimal lb = new BigDecimal(
				"0.123456789012345678901234567890123456788");
		BigDecimal ub = new BigDecimal(
				"0.123456789012345678901234567890123456790");
		QuantityValue o1 = new QuantityValueImpl(nv, lb, ub, "1");
		QuantityValue o2 = factory.getQuantityValue(nv, lb, ub);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetQuantityValueNoBounds() {
		BigDecimal nv = new BigDecimal(
				"0.123456789012345678901234567890123456789");
		QuantityValue o1 = new QuantityValueImpl(nv, null, null, "unit");
		QuantityValue o2 = factory.getQuantityValue(nv, "unit");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetQuantityValueNoBoundsAndUnits() {
		BigDecimal nv = new BigDecimal(
				"0.123456789012345678901234567890123456789");
		QuantityValue o1 = new QuantityValueImpl(nv, null, null, "1");
		QuantityValue o2 = factory.getQuantityValue(nv);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetValueSnak() {
		ValueSnak o1 = new ValueSnakImpl(
				factory.getPropertyIdValue("P42", "foo"),
				factory.getStringValue("foo"));
		ValueSnak o2 = factory.getValueSnak(
				factory.getPropertyIdValue("P42", "foo"),
				factory.getStringValue("foo"));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetSomeValueSnak() {
		SomeValueSnak o1 = new SomeValueSnakImpl(factory.getPropertyIdValue("P42", "foo"));
		SomeValueSnak o2 = factory.getSomeValueSnak(factory.getPropertyIdValue("P42", "foo"));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetNoValueSnak() {
		NoValueSnak o1 = new NoValueSnakImpl(factory.getPropertyIdValue("P42", "foo"));
		NoValueSnak o2 = factory.getNoValueSnak(factory.getPropertyIdValue("P42", "foo"));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetSnakGroup() {
		Snak s = factory.getNoValueSnak(factory.getPropertyIdValue("P42", "foo"));
		SnakGroup o1 = new SnakGroupImpl(Collections.singletonList(s));
		SnakGroup o2 = factory.getSnakGroup(Collections.singletonList(s));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetClaim() {
		Claim o1 = new ClaimImpl(
				factory.getItemIdValue("Q42", "foo"),
				factory.getNoValueSnak(factory.getPropertyIdValue("P42", "foo")),
				Collections.emptyList()
		);
		Claim o2 = factory.getClaim(
				factory.getItemIdValue("Q42", "foo"),
				factory.getNoValueSnak(factory.getPropertyIdValue("P42", "foo")),
				Collections.emptyList()
		);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetReference() {
		Reference r1 = new ReferenceImpl(Collections.emptyList());
		Reference r2 = factory.getReference(Collections.emptyList());
		assertEquals(r1, r2);
	}

	@Test
	public final void testGetStatementGroup() {
		Statement s = factory.getStatement(
				factory.getItemIdValue("Q42", "foo"),
				factory.getNoValueSnak(factory.getPropertyIdValue("P42", "foo")),
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "MyId");
		StatementGroup o1 = new StatementGroupImpl(Collections.singletonList(s));
		StatementGroup o2 = factory.getStatementGroup(Collections.singletonList(s));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetSiteLink() {
		SiteLink o1 = new SiteLinkImpl("SOLID", "enwiki", Collections.emptyList());
		SiteLink o2 = factory.getSiteLink("SOLID", "enwiki", Collections.emptyList());
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetPropertyDocument() {
		PropertyDocument o1 = new PropertyDocumentImpl(
				factory.getPropertyIdValue("P42", "foo"),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				factory.getDatatypeIdValue(DatatypeIdValue.DT_TIME),
				0);
		PropertyDocument o2 = factory.getPropertyDocument(
				factory.getPropertyIdValue("P42", "foo"),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				factory.getDatatypeIdValue(DatatypeIdValue.DT_TIME),
				0);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetItemDocument() {
		ItemDocument o1 = new ItemDocumentImpl(
				factory.getItemIdValue("Q42", "foo"),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				0);
		ItemDocument o2 = factory.getItemDocument(
				factory.getItemIdValue("Q42", "foo"),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyMap(),
				0);
		assertEquals(o1, o2);
	}
}
