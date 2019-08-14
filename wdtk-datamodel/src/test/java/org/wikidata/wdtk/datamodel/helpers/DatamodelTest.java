package org.wikidata.wdtk.datamodel.helpers;

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

import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.*;

public class DatamodelTest {

	private final DataObjectFactory factory = new DataObjectFactoryImpl();

	@Test
	public final void testGetItemId() {
		ItemIdValue o1 = Datamodel.makeItemIdValue("Q42", "foo");
		ItemIdValue o2 = factory.getItemIdValue("Q42", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetWikidataItemId() {
		ItemIdValue o1 = Datamodel.makeWikidataItemIdValue("Q42");
		ItemIdValue o2 = factory.getItemIdValue("Q42",
				"http://www.wikidata.org/entity/");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetLexemeId() {
		LexemeIdValue o1 = Datamodel.makeLexemeIdValue("L42", "foo");
		LexemeIdValue o2 = factory.getLexemeIdValue("L42", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetWikidataLexemeId() {
		LexemeIdValue o1 = Datamodel.makeWikidataLexemeIdValue("L42");
		LexemeIdValue o2 = factory.getLexemeIdValue("L42",
				"http://www.wikidata.org/entity/");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetPropertyId() {
		PropertyIdValue o1 = Datamodel.makePropertyIdValue("P42", "foo");
		PropertyIdValue o2 = factory.getPropertyIdValue("P42", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetWikidataPropertyId() {
		PropertyIdValue o1 = Datamodel.makeWikidataPropertyIdValue("P42");
		PropertyIdValue o2 = factory.getPropertyIdValue("P42",
				"http://www.wikidata.org/entity/");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetFormId() {
		FormIdValue o1 = Datamodel.makeFormIdValue("L42-F1", "foo");
		FormIdValue o2 = factory.getFormIdValue("L42-F1", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetWikidataFormId() {
		FormIdValue o1 = Datamodel.makeWikidataFormIdValue("L42-F1");
		FormIdValue o2 = factory.getFormIdValue("L42-F1",
				"http://www.wikidata.org/entity/");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetSenseId() {
		SenseIdValue o1 = Datamodel.makeSenseIdValue("L42-S1", "foo");
		SenseIdValue o2 = factory.getSenseIdValue("L42-S1", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetWikidataSenseId() {
		SenseIdValue o1 = Datamodel.makeWikidataSenseIdValue("L42-S1");
		SenseIdValue o2 = factory.getSenseIdValue("L42-S1",
				"http://www.wikidata.org/entity/");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetMediaInfoId() {
		MediaInfoIdValue o1 = Datamodel.makeMediaInfoIdValue("M42", "foo");
		MediaInfoIdValue o2 = factory.getMediaInfoIdValue("M42", "foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetWikimediaCommonsMediaInfoId() {
		MediaInfoIdValue o1 = Datamodel.makeWikimediaCommonsMediaInfoIdValue("M42");
		MediaInfoIdValue o2 = factory.getMediaInfoIdValue("M42",
				"http://commons.wikimedia.org/entity/");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetDatatypeId() {
		DatatypeIdValue o1 = Datamodel
				.makeDatatypeIdValue(DatatypeIdValue.DT_TIME);
		DatatypeIdValue o2 = factory
				.getDatatypeIdValue(DatatypeIdValue.DT_TIME);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetTimeValue() {
		TimeValue o1 = Datamodel.makeTimeValue(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_DAY, 0, 0, 60,
				TimeValue.CM_GREGORIAN_PRO);
		TimeValue o2 = factory.getTimeValue(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_DAY, 0, 0, 60,
				TimeValue.CM_GREGORIAN_PRO);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetTimeValueTime() {
		TimeValue o1 = Datamodel
				.makeTimeValue(2007, (byte) 5, (byte) 12, (byte) 10, (byte) 45,
						(byte) 0, 60, TimeValue.CM_GREGORIAN_PRO);
		TimeValue o2 = factory.getTimeValue(2007, (byte) 5, (byte) 12,
				(byte) 10, (byte) 45, (byte) 0, TimeValue.PREC_SECOND, 0, 0,
				60, TimeValue.CM_GREGORIAN_PRO);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetTimeValueDate() {
		TimeValue o1 = Datamodel.makeTimeValue(2007, (byte) 5, (byte) 12,
				TimeValue.CM_GREGORIAN_PRO);
		TimeValue o2 = factory.getTimeValue(2007, (byte) 5, (byte) 12,
				(byte) 0, (byte) 0, (byte) 0, TimeValue.PREC_DAY, 0, 0, 0,
				TimeValue.CM_GREGORIAN_PRO);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetGlobeCoordinatesValue() {
		GlobeCoordinatesValue o1 = Datamodel.makeGlobeCoordinatesValue(90.0,
				190.5, GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
		GlobeCoordinatesValue o2 = factory.getGlobeCoordinatesValue(90.0,
				190.5, GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetStringValue() {
		StringValue o1 = Datamodel.makeStringValue("foo");
		StringValue o2 = factory.getStringValue("foo");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetMonolingualTextValue() {
		MonolingualTextValue o1 = Datamodel.makeMonolingualTextValue("foo",
				"en");
		MonolingualTextValue o2 = factory.getMonolingualTextValue("foo", "en");
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
		QuantityValue o1 = Datamodel.makeQuantityValue(nv, lb, ub, "unit");
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
		QuantityValue o1 = Datamodel.makeQuantityValue(nv, lb, ub);
		QuantityValue o2 = factory.getQuantityValue(nv, lb, ub);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetQuantityValueNoBounds() {
		BigDecimal nv = new BigDecimal(
				"0.123456789012345678901234567890123456789");
		QuantityValue o1 = Datamodel.makeQuantityValue(nv, "unit");
		QuantityValue o2 = factory.getQuantityValue(nv, "unit");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetQuantityValueNoBoundsAndUnits() {
		BigDecimal nv = new BigDecimal(
				"0.123456789012345678901234567890123456789");
		QuantityValue o1 = Datamodel.makeQuantityValue(nv);
		QuantityValue o2 = factory.getQuantityValue(nv);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetLongQuantityValue() {
		BigDecimal nv = new BigDecimal("1234567890123456789");
		BigDecimal lb = new BigDecimal("1234567890123456788");
		BigDecimal ub = new BigDecimal("1234567890123456790");
		QuantityValue o1 = Datamodel.makeQuantityValue(1234567890123456789L,
				1234567890123456788L, 1234567890123456790L);
		QuantityValue o2 = factory.getQuantityValue(nv, lb, ub);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetValueSnak() {
		ValueSnak o1 = Datamodel.makeValueSnak(
				factory.getPropertyIdValue("P42", "foo"),
				factory.getStringValue("foo"));
		ValueSnak o2 = factory.getValueSnak(
				factory.getPropertyIdValue("P42", "foo"),
				factory.getStringValue("foo"));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetSomeValueSnak() {
		SomeValueSnak o1 = Datamodel.makeSomeValueSnak(factory
				.getPropertyIdValue("P42", "foo"));
		SomeValueSnak o2 = factory.getSomeValueSnak(factory.getPropertyIdValue(
				"P42", "foo"));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetNoValueSnak() {
		NoValueSnak o1 = Datamodel.makeNoValueSnak(factory.getPropertyIdValue(
				"P42", "foo"));
		NoValueSnak o2 = factory.getNoValueSnak(factory.getPropertyIdValue(
				"P42", "foo"));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetSnakGroup() {
		Snak s = factory.getNoValueSnak(factory
				.getPropertyIdValue("P42", "foo"));
		SnakGroup o1 = Datamodel.makeSnakGroup(Collections
				.singletonList(s));
		SnakGroup o2 = factory
				.getSnakGroup(Collections.singletonList(s));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetClaim() {
		Claim o1 = Datamodel
				.makeClaim(factory.getItemIdValue("Q42", "foo"), factory
						.getNoValueSnak(factory
								.getPropertyIdValue("P42", "foo")), Collections
						.emptyList());
		Claim o2 = factory
				.getClaim(factory.getItemIdValue("Q42", "foo"), factory
						.getNoValueSnak(factory
								.getPropertyIdValue("P42", "foo")), Collections
						.emptyList());
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetReference() {
		Reference r1 = Datamodel.makeReference(Collections
				.emptyList());
		Reference r2 = factory
				.getReference(Collections.emptyList());
		assertEquals(r1, r2);
	}

	@Test
	public final void testGetStatement() {
		Statement o1 = Datamodel.makeStatement(
				factory.getItemIdValue("Q42", "foo"),
				factory.getNoValueSnak(factory.getPropertyIdValue("P42", "foo")),
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "MyId");
		Statement o2 = factory.getStatement(
				factory.getItemIdValue("Q42", "foo"),
				factory.getNoValueSnak(factory.getPropertyIdValue("P42", "foo")),
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "MyId");
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetStatementGroup() {
		Statement s = Datamodel.makeStatement(
				factory.getItemIdValue("Q42", "foo"),
				factory.getNoValueSnak(factory.getPropertyIdValue("P42", "foo")),
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "MyId");
		StatementGroup o1 = Datamodel.makeStatementGroup(Collections.singletonList(s));
		StatementGroup o2 = factory.getStatementGroup(Collections.singletonList(s));
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetSiteLink() {
		SiteLink o1 = Datamodel.makeSiteLink("SOLID", "enwiki",
				Collections.emptyList());
		SiteLink o2 = factory.getSiteLink("SOLID", "enwiki",
				Collections.emptyList());
		SiteLink o3 = Datamodel.makeSiteLink("SOLID", "enwiki");
		assertEquals(o1, o2);
		assertEquals(o3, o2);
	}

	@Test
	public final void testGetPropertyDocument() {
		PropertyDocument o1 = Datamodel.makePropertyDocument(
				factory.getPropertyIdValue("P42", "foo"),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				factory.getDatatypeIdValue(DatatypeIdValue.DT_TIME));
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
		ItemDocument o1 = Datamodel.makeItemDocument(
				factory.getItemIdValue("Q42", "foo"),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyMap());
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

	@Test
	public final void testGetLexemeDocument() {
		LexemeDocument o1 = Datamodel.makeLexemeDocument(
				factory.getLexemeIdValue("L42", "foo"),
				factory.getItemIdValue("Q1", "foo"),
				factory.getItemIdValue("Q2", "foo"),
				Collections.singletonList(factory.getMonolingualTextValue("foo", "en")),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList());
		LexemeDocument o2 = factory.getLexemeDocument(
				factory.getLexemeIdValue("L42", "foo"),
				factory.getItemIdValue("Q1", "foo"),
				factory.getItemIdValue("Q2", "foo"),
				Collections.singletonList(factory.getMonolingualTextValue("foo", "en")),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				0);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetFormDocument() {
		FormDocument o1 = Datamodel.makeFormDocument(
				factory.getFormIdValue("L42-F1", "foo"),
				Collections.singletonList(factory.getMonolingualTextValue("en", "foo")),
				Collections.singletonList(factory.getItemIdValue("Q1", "foo")),
				Collections.emptyList());
		FormDocument o2 = factory.getFormDocument(
				factory.getFormIdValue("L42-F1", "foo"),
				Collections.singletonList(factory.getMonolingualTextValue("en", "foo")),
				Collections.singletonList(factory.getItemIdValue("Q1", "foo")),
				Collections.emptyList(),
				0);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetSenseDocument() {
		SenseDocument o1 = Datamodel.makeSenseDocument(
				factory.getSenseIdValue("L42-S1", "foo"),
				Collections.singletonList(factory.getMonolingualTextValue("en", "foo")),
				Collections.emptyList());
		SenseDocument o2 = factory.getSenseDocument(
				factory.getSenseIdValue("L42-S1", "foo"),
				Collections.singletonList(factory.getMonolingualTextValue("en", "foo")),
				Collections.emptyList(),
				0);
		assertEquals(o1, o2);
	}

	@Test
	public final void testGetMediaInfoDocument() {
		MediaInfoDocument o1 = Datamodel.makeMediaInfoDocument(
				factory.getMediaInfoIdValue("M42", "foo"),
				Collections.emptyList(),
				Collections.emptyList());
		MediaInfoDocument o2 = factory.getMediaInfoDocument(
				factory.getMediaInfoIdValue("M42", "foo"),
				Collections.emptyList(),
				Collections.emptyList(),
				0);
		assertEquals(o1, o2);
	}
}
