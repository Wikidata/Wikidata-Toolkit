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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.helpers.DatamodelConverter;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

public class DataObjectFactoryImplTest {

	public enum ValueType {
		STRING, ITEM, GLOBE_COORDINATES, TIME, QUANTITY, MONOLINGUAL_TEXT;

		protected static ValueType fromInt(int seed) {
			switch (seed % 6) {
			case 0:
				return STRING;
			case 1:
				return ITEM;
			case 2:
				return GLOBE_COORDINATES;
			case 3:
				return TIME;
			case 4:
				return QUANTITY;
			default:
			case 5:
				return MONOLINGUAL_TEXT;
			}
		}
	}

	protected DataObjectFactory factory;
	protected DatamodelConverter converter;

	public DataObjectFactoryImplTest() {
		factory = new DataObjectFactoryImpl();
		converter = new DatamodelConverter(factory);
	}

	@Test
	public void deepCopyOptionValue() {
		assertTrue(this.converter.hasOptionDeepCopy());
	}

	@Test
	public final void testGetItemId() {
		ItemIdValue o1 = getTestItemIdValue(2);
		ItemIdValue o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static ItemIdValue getTestItemIdValue(int seed) {
		return ItemIdValueImpl.create("Q4" + seed, "foo:");
	}

	@Test
	public final void testGetPropertyId() {
		PropertyIdValue o1 = getTestPropertyIdValue(2);
		PropertyIdValue o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static PropertyIdValue getTestPropertyIdValue(int seed) {
		return PropertyIdValueImpl.create("P4" + seed, "foo:");
	}

	public static EntityIdValue getTestEntityIdValue(int seed, String entityType) {
		switch (entityType) {
		case EntityIdValue.ET_ITEM:
			return getTestItemIdValue(seed);
		case EntityIdValue.ET_PROPERTY:
			return getTestPropertyIdValue(seed);
		default:
			throw new IllegalArgumentException("Unsupported entity type "
					+ entityType);
		}
	}

	@Test
	public final void testGetDatatypeId() {
		DatatypeIdValue o1 = new DatatypeIdImpl(DatatypeIdValue.DT_TIME);
		DatatypeIdValue o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	@Test
	public final void testGetTimeValue() {
		TimeValue o1 = getTestTimeValue(0);
		TimeValue o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static TimeValue getTestTimeValue(int seed) {
		return new TimeValueImpl(2007 + seed, (byte) 5, (byte) 12, (byte) 10,
				(byte) 45, (byte) 00, TimeValue.PREC_DAY, 0, 1, 60,
				TimeValue.CM_GREGORIAN_PRO);
	}

	@Test
	public final void testGetGlobeCoordinatesValue() {
		GlobeCoordinatesValue o1 = getTestGlobeCoordinatesValue(0);
		GlobeCoordinatesValue o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static GlobeCoordinatesValue getTestGlobeCoordinatesValue(int seed) {
		return new GlobeCoordinatesValueImpl((10 + seed)
				* GlobeCoordinatesValue.PREC_DEGREE, (1905 + seed)
				* GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
	}

	@Test
	public final void testGetStringValue() {
		StringValue o1 = getTestStringValue(0);
		StringValue o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static StringValue getTestStringValue(int seed) {
		return new StringValueImpl("foo" + seed);
	}

	@Test
	public final void testGetMonolingualTextValue() {
		MonolingualTextValue o1 = getTestMonolingualTextValue(0, "en");
		MonolingualTextValue o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static MonolingualTextValue getTestMonolingualTextValue(int seed,
			String language) {
		return new MonolingualTextValueImpl("foo" + seed, language);
	}

	@Test
	public final void testGetQuantityValue() {
		QuantityValue o1 = getTestQuantityValue(0);
		QuantityValue o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static QuantityValue getTestQuantityValue(int seed) {
		BigDecimal nv = new BigDecimal(seed
				+ ".123456789012345678901234567890123456789");
		BigDecimal lb = new BigDecimal(seed
				+ ".123456789012345678901234567890123456788");
		BigDecimal ub = new BigDecimal(seed
				+ ".123456789012345678901234567890123456790");
		return new QuantityValueImpl(nv, lb, ub);
	}

	public static Value getTestValue(ValueType valueType, int seed) {
		switch (valueType) {
		case GLOBE_COORDINATES:
			return getTestGlobeCoordinatesValue(seed);
		case ITEM:
			return getTestItemIdValue(seed);
		case MONOLINGUAL_TEXT:
			return getTestMonolingualTextValue(seed, "de");
		case QUANTITY:
			return getTestQuantityValue(seed);
		case STRING:
			return getTestStringValue(seed);
		case TIME:
			return getTestTimeValue(seed);
		default:
			throw new RuntimeException("Unsupported value type.");
		}
	}

	@Test
	public final void testGetValueSnak() {
		ValueSnak o1 = getTestValueSnak(ValueType.STRING, 0, 0);
		ValueSnak o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static ValueSnak getTestValueSnak(ValueType valueType, int pseed,
			int vseed) {
		return new ValueSnakImpl(getTestPropertyIdValue(pseed), getTestValue(
				valueType, vseed));
	}

	@Test
	public final void testGetSomeValueSnak() {
		SomeValueSnak o1 = new SomeValueSnakImpl(getTestPropertyIdValue(0));
		SomeValueSnak o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	@Test
	public final void testGetNoValueSnak() {
		NoValueSnak o1 = new NoValueSnakImpl(getTestPropertyIdValue(0));
		NoValueSnak o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	@Test
	public final void testGetSnakGroup() {
		SnakGroup o1 = getTestValueSnakGroup(ValueType.STRING, 0, 2);
		SnakGroup o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static SnakGroup getTestValueSnakGroup(ValueType valueType,
			int pseed, int size) {
		List<Snak> snaks = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			snaks.add(getTestValueSnak(valueType, pseed, i));
		}
		return new SnakGroupImpl(snaks);
	}

	public static List<SnakGroup> getTestValueSnakGroups(int seed, int size) {
		List<SnakGroup> snakGroups = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			snakGroups.add(getTestValueSnakGroup(ValueType.fromInt(i + seed), i
					+ seed, i + 1));
		}
		return snakGroups;
	}

	@Test
	public final void testGetClaim() {
		Claim o1 = getTestClaim(0, 0, 2, EntityIdValue.ET_ITEM);
		Claim o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static Claim getTestClaim(int subjectSeed, int seed, int size,
			String entityType) {
		return new ClaimImpl(getTestEntityIdValue(subjectSeed, entityType),
				getTestValueSnak(ValueType.fromInt(seed), seed, seed),
				getTestValueSnakGroups(seed * 100, size));
	}

	@Test
	public final void testGetReference() {
		Reference o1 = new ReferenceImpl(getTestValueSnakGroups(10, 3));
		Reference o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static List<Reference> getReferenceList(int seed, int size) {
		List<Reference> references = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			references.add(new ReferenceImpl(getTestValueSnakGroups(seed,
					(seed + i) % 4 + 1)));
		}
		return references;
	}

	@Test
	public final void testGetStatement() {
		Statement o1 = getTestStatement(0, 42, 3, EntityIdValue.ET_ITEM);
		Statement o2 = converter.copy(o1);
		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static Statement getTestStatement(int subjectSeed, int seed,
			int size, String entityType) {
		return new StatementImpl(getTestClaim(subjectSeed, seed, size,
				entityType), getReferenceList(seed, size),
				StatementRank.NORMAL, "MyId" + seed);
	}

	@Test
	public final void testGetStatementGroup() {
		StatementGroup o1 = getTestStatementGroup(0, 17, 10,
				EntityIdValue.ET_ITEM);
		StatementGroup o2 = converter.copy(o1);

		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	public static StatementGroup getTestStatementGroup(int subjectSeed,
			int seed, int size, String entityType) {
		List<Statement> statements = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			statements.add(getTestStatement(subjectSeed, seed, i, entityType));
		}
		return new StatementGroupImpl(statements);
	}

	public static List<StatementGroup> getTestStatementGroups(int subjectSeed,
			int seed, int size, String entityType) {
		List<StatementGroup> statementGroups = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			statementGroups.add(getTestStatementGroup(subjectSeed, i + seed,
					i * 2 + 1, entityType));
		}
		return statementGroups;
	}

	@Test
	public final void testGetSiteLink() {
		SiteLink o1 = new SiteLinkImpl("SOLID", "enwiki",
				Collections.<String> emptyList());
		SiteLink o2 = converter.copy(o1);
		assertEquals(o2, o1);
	}

	@Test
	public final void testGetPropertyDocument() {
		PropertyDocument o1 = new PropertyDocumentImpl(
				getTestPropertyIdValue(2),
				getTestMtvList(1, 0), // labels
				getTestMtvList(4, 13), // descriptions
				getTestMtvList(0, 0), // aliases
				getTestStatementGroups(2, 17, 1, EntityIdValue.ET_PROPERTY),
				new DatatypeIdImpl(DatatypeIdValue.DT_TIME));
		PropertyDocument o2 = converter.copy(o1);

		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	@Test
	public final void testGetItemDocument() {
		ItemDocument o1 = new ItemDocumentImpl(
				getTestItemIdValue(2),
				getTestMtvList(5, 0), // labels
				getTestMtvList(0, 0), // descriptions
				getTestMtvList(15, 12), // aliases
				getTestStatementGroups(2, 17, 1, EntityIdValue.ET_ITEM),
				getTestSiteLinks(20));
		ItemDocument o2 = converter.copy(o1);

		assertEquals(o1.toString(), o2.toString());
		assertEquals(o1.hashCode(), o2.hashCode());
		assertEquals(o2, o1);
	}

	/**
	 * Creates a test list of {@link MonolingualTextValue} objects.
	 *
	 * @param size
	 *            if bigger than 5, some language keys will occur multiple times
	 * @param seed
	 * @return
	 */
	public static List<MonolingualTextValue> getTestMtvList(int size, int seed) {
		List<MonolingualTextValue> result = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			result.add(getTestMonolingualTextValue(i + seed, "lang" + (i % 6)));
		}
		return result;
	}

	public static Map<String, SiteLink> getTestSiteLinks(int size) {
		Map<String, SiteLink> result = new HashMap<>(size);
		List<String> someBadges = new ArrayList<>(2);
		someBadges.add("badge1");
		someBadges.add("badge2");
		for (int i = 0; i < size; i++) {
			if (i % 3 == 0) {
				result.put("site" + i, new SiteLinkImpl("Badged article" + i,
						"site" + i, someBadges));
			} else {
				result.put("site" + i, new SiteLinkImpl("Article" + i, "site"
						+ i, Collections.<String> emptyList()));
			}
		}
		return result;

	}
}
