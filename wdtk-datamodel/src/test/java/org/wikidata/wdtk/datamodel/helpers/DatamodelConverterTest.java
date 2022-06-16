/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

package org.wikidata.wdtk.datamodel.helpers;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.implementation.GlobeCoordinatesValueImpl;
import org.wikidata.wdtk.datamodel.implementation.ItemIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.MonolingualTextValueImpl;
import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.QuantityValueImpl;
import org.wikidata.wdtk.datamodel.implementation.SnakGroupImpl;
import org.wikidata.wdtk.datamodel.implementation.StatementGroupImpl;
import org.wikidata.wdtk.datamodel.implementation.StatementImpl;
import org.wikidata.wdtk.datamodel.implementation.StringValueImpl;
import org.wikidata.wdtk.datamodel.implementation.TimeValueImpl;
import org.wikidata.wdtk.datamodel.implementation.ValueSnakImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

/**
 * Test for special aspects of {@link DatamodelConverter}. Regular operation is
 * tested elsewhere already.
 *
 * @author Markus Kroetzsch
 *
 */
public class DatamodelConverterTest {

	static class BrokenItemIdValue implements ItemIdValue {

		@Override
		public String getEntityType() {
			return ItemIdValue.ET_ITEM;
		}

		@Override
		public String getId() {
			return null; // illegal; should cause errors elsewhere
		}

		@Override
		public String getSiteIri() {
			return Datamodel.SITE_WIKIDATA;
		}

		@Override
		public String getIri() {
			return null;
		}

		@Override
		public <T> T accept(ValueVisitor<T> valueVisitor) {
			return valueVisitor.visit(this);
		}

		@Override
		public boolean isPlaceholder() {
			return false;
		}

	}

	private Statement getBrokenStatement() {
		Snak brokenSnak = Datamodel.makeValueSnak(
				getTestPropertyIdValue(5),
				new BrokenItemIdValue());
		return Datamodel.makeStatement(
				getTestItemIdValue(2), brokenSnak,
				Collections.emptyList(), Collections.emptyList(),
				StatementRank.NORMAL, "id");
	}

	/**
	 * Tests that statement groups that contain a single statement which cannot
	 * be copied are removed.
	 */
	@Test
	public void testSingleBrokenStatement() {

		StatementGroup sg1 = Datamodel.makeStatementGroup(Collections
				.singletonList(getBrokenStatement()));
		StatementGroup sg2 = getTestStatementGroup(2,
				5, 1, EntityIdValue.ET_ITEM);

		List<StatementGroup> brokenSgs = new ArrayList<>();
		brokenSgs.add(sg1);
		brokenSgs.add(sg2);
		List<StatementGroup> fixedSgs = new ArrayList<>();
		fixedSgs.add(sg2);

		ItemDocument brokenId = Datamodel.makeItemDocument(
				getTestItemIdValue(2),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(), brokenSgs,
				Collections.emptyMap());

		ItemDocument fixedId = Datamodel.makeItemDocument(
				getTestItemIdValue(2),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(), fixedSgs,
				Collections.emptyMap());

		DatamodelConverter dmc = new DatamodelConverter(
				new DataObjectFactoryImpl());

		assertEquals(fixedId, dmc.copy(brokenId));
	}

	/**
	 * Tests that statement groups that contain several statements, one of which
	 * cannot be copied, are reduced to the working statements.
	 */
	@Test
	public void testBrokenStatement() {

		List<Statement> brokenSg1Statements = new ArrayList<>();
		brokenSg1Statements.add(getTestStatement(2,
				5, 1, EntityIdValue.ET_ITEM));
		brokenSg1Statements.add(getBrokenStatement());
		brokenSg1Statements.add(getTestStatement(2,
				5, 2, EntityIdValue.ET_ITEM));
		StatementGroup brokenSg1 = Datamodel
				.makeStatementGroup(brokenSg1Statements);

		List<Statement> fixedSg1Statements = new ArrayList<>();
		fixedSg1Statements.add(getTestStatement(2, 5,
				1, EntityIdValue.ET_ITEM));
		fixedSg1Statements.add(getTestStatement(2, 5,
				2, EntityIdValue.ET_ITEM));
		StatementGroup fixedSg1 = Datamodel
				.makeStatementGroup(fixedSg1Statements);

		StatementGroup sg2 = getTestStatementGroup(2,
				5, 1, EntityIdValue.ET_ITEM);

		List<StatementGroup> brokenSgs = new ArrayList<>();
		brokenSgs.add(brokenSg1);
		brokenSgs.add(sg2);
		List<StatementGroup> fixedSgs = new ArrayList<>();
		fixedSgs.add(fixedSg1);
		fixedSgs.add(sg2);

		ItemDocument brokenId = Datamodel.makeItemDocument(
				getTestItemIdValue(2),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(), brokenSgs,
				Collections.emptyMap());

		ItemDocument fixedId = Datamodel.makeItemDocument(
				getTestItemIdValue(2),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(), fixedSgs,
				Collections.emptyMap());

		DatamodelConverter dmc = new DatamodelConverter(
				new DataObjectFactoryImpl());

		assertEquals(fixedId, dmc.copy(brokenId));
	}

	@Test
	public void testGenerationFromOtherItemDocument() {
		ItemDocument item = Datamodel.makeItemDocument(
				Datamodel.makeWikidataItemIdValue("Q42"),
				Collections.singletonList(Datamodel.makeMonolingualTextValue("en", "label")),
				Collections.singletonList(Datamodel.makeMonolingualTextValue("en", "desc")),
				Collections.singletonList(Datamodel.makeMonolingualTextValue("en", "alias")),
				Collections.emptyList(),
				Collections.singletonMap("enwiki", Datamodel.makeSiteLink("foo", "enwiki", Collections.emptyList()))
		);

		DatamodelConverter converter = new DatamodelConverter(new DataObjectFactoryImpl());
		assertEquals(item, converter.copy(item));
	}
	
	
    @Test
    public void testCopyMediaInfoIdValue() {
        DatamodelConverter converter = new DatamodelConverter(new DataObjectFactoryImpl());
        MediaInfoIdValue mediaInfo = getTestMediaInfoIdValue(34);
        assertEquals(mediaInfo, converter.copy(mediaInfo));
    }
    
    @Test
    public void testCopyLexemeIdValue() {
        DatamodelConverter converter = new DatamodelConverter(new DataObjectFactoryImpl());
        LexemeIdValue lexeme = getTestLexemeIdValue(45);
        assertEquals(lexeme, converter.copy(lexeme));
    }
    
    @Test
    public void testCopyFormIdValue() {
        DatamodelConverter converter = new DatamodelConverter(new DataObjectFactoryImpl());
        FormIdValue form = getTestFormIdValue(56);
        assertEquals(form, converter.copy(form));
    }
    
    @Test
    public void testCopySenseIdValue() {
        DatamodelConverter converter = new DatamodelConverter(new DataObjectFactoryImpl());
        SenseIdValue sense = getTestSenseIdValue(56);
        assertEquals(sense, converter.copy(sense));
    }
    
    @Test
    public void testMediaInfoDocument() {
        DatamodelConverter converter = new DatamodelConverter(new DataObjectFactoryImpl());
        MediaInfoDocument document = Datamodel.makeMediaInfoDocument(
                getTestMediaInfoIdValue(78),
                Collections.singletonList(Datamodel.makeMonolingualTextValue("en", "label")),
                Collections.emptyList());
        assertEquals(document, converter.copy(document));
    }
    
    @Test
    public void testLexemeDocument() {
        DatamodelConverter converter = new DatamodelConverter(new DataObjectFactoryImpl());
        LexemeDocument document = Datamodel.makeLexemeDocument(
                getTestLexemeIdValue(90),
                getTestItemIdValue(38),
                getTestItemIdValue(39),
                Collections.singletonList(Datamodel.makeMonolingualTextValue("en", "lemma")));
        assertEquals(document, converter.copy(document));
    }
    
    @Test
    public void testFormDocument() {
        DatamodelConverter converter = new DatamodelConverter(new DataObjectFactoryImpl());
        FormDocument document = Datamodel.makeFormDocument(
                getTestFormIdValue(92),
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList());
        assertEquals(document, converter.copy(document));
    }
    
    @Test
    public void testSenseDocument() {
        DatamodelConverter converter = new DatamodelConverter(new DataObjectFactoryImpl());
        SenseDocument document = Datamodel.makeSenseDocument(
                getTestSenseIdValue(738),
                Collections.singletonList(Datamodel.makeMonolingualTextValue("en", "gloss")),
                Collections.emptyList());
        assertEquals(document, converter.copy(document));
    }
	
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
	
	private ItemIdValue getTestItemIdValue(int seed) {
		return new ItemIdValueImpl("Q4" + seed, "foo:");
	}

	private PropertyIdValue getTestPropertyIdValue(int seed) {
		return new PropertyIdValueImpl("P4" + seed, "foo:");
	}
	
	private MediaInfoIdValue getTestMediaInfoIdValue(int seed) {
        return Datamodel.makeMediaInfoIdValue("M4" + seed, "foo:");
    }

    private LexemeIdValue getTestLexemeIdValue(int seed) {
        return Datamodel.makeLexemeIdValue("L4" + seed, "foo:");
    }
    
    private FormIdValue getTestFormIdValue(int seed) {
        return Datamodel.makeFormIdValue("L4" + seed + "-F1", "foo:");
    }
    
    private SenseIdValue getTestSenseIdValue(int seed) {
        return Datamodel.makeSenseIdValue("L4" + seed + "-S1", "foo:");
    }

	private EntityIdValue getTestEntityIdValue(int seed, String entityType) {
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

	private TimeValue getTestTimeValue(int seed) {
		return new TimeValueImpl(2007 + seed, (byte) 5, (byte) 12, (byte) 10,
				(byte) 45, (byte) 0, TimeValue.PREC_DAY, 0, 1, 60,
				TimeValue.CM_GREGORIAN_PRO);
	}

	private GlobeCoordinatesValue getTestGlobeCoordinatesValue(int seed) {
		return new GlobeCoordinatesValueImpl((10 + seed)
				* GlobeCoordinatesValue.PREC_DEGREE, (1905 + seed)
				* GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.PREC_DECI_DEGREE,
				GlobeCoordinatesValue.GLOBE_EARTH);
	}

	private StringValue getTestStringValue(int seed) {
		return new StringValueImpl("foo" + seed);
	}

	private MonolingualTextValue getTestMonolingualTextValue(int seed, String language) {
		return new MonolingualTextValueImpl("foo" + seed, language);
	}

	private QuantityValue getTestQuantityValue(int seed) {
		BigDecimal nv = new BigDecimal(seed
				+ ".123456789012345678901234567890123456789");
		BigDecimal lb = new BigDecimal(seed
				+ ".123456789012345678901234567890123456788");
		BigDecimal ub = new BigDecimal(seed
				+ ".123456789012345678901234567890123456790");
		return new QuantityValueImpl(nv, lb, ub,
				Datamodel.makeWikidataItemIdValue("Q11573"));
	}

	private Value getTestValue(ValueType valueType, int seed) {
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

	private ValueSnak getTestValueSnak(ValueType valueType, int pseed,
											  int vseed) {
		PropertyIdValue property = getTestPropertyIdValue(pseed);
		Value value =  getTestValue(valueType, vseed);
		return new ValueSnakImpl(property, value);
	}

	private SnakGroup getTestValueSnakGroup(ValueType valueType,
												   int pseed, int size) {
		List<Snak> snaks = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			snaks.add(getTestValueSnak(valueType, pseed, i));
		}
		return new SnakGroupImpl(snaks);
	}

	private List<SnakGroup> getTestValueSnakGroups(int seed, int size) {
		List<SnakGroup> snakGroups = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			SnakGroup group = getTestValueSnakGroup(ValueType.fromInt(i + seed), i
					+ seed, i + 1);
			snakGroups.add(group);
		}
		return snakGroups;
	}

	private Statement getTestStatement(int subjectSeed, int seed,
											 int size, String entityType) {
		List<SnakGroup> qualifiers = getTestValueSnakGroups(seed * 100, size);
		return new StatementImpl("",
				StatementRank.NORMAL,
				getTestValueSnak(ValueType.fromInt(seed), seed, seed),
				qualifiers, null,
				getTestEntityIdValue(subjectSeed, entityType));
	}

	private StatementGroup getTestStatementGroup(int subjectSeed, int seed, int size, String entityType) {
		List<Statement> statements = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			statements.add(getTestStatement(subjectSeed, seed, i, entityType));
		}
		return new StatementGroupImpl(statements);
	}

}
