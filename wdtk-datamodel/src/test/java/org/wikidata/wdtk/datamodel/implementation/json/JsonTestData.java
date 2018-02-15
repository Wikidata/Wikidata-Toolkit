package org.wikidata.wdtk.datamodel.implementation.json;

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

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.implementation.ItemIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.ItemDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.TermImpl;
import org.wikidata.wdtk.datamodel.implementation.NoValueSnakImpl;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.implementation.SiteLinkImpl;
import org.wikidata.wdtk.datamodel.implementation.SomeValueSnakImpl;
import org.wikidata.wdtk.datamodel.implementation.StatementImpl;
import org.wikidata.wdtk.datamodel.implementation.ValueSnakImpl;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonPreStatement;
import org.wikidata.wdtk.datamodel.implementation.ValueImpl;
import org.wikidata.wdtk.datamodel.implementation.GlobeCoordinatesValueImpl;
import org.wikidata.wdtk.datamodel.implementation.MonolingualTextValueImpl;
import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.QuantityValueImpl;
import org.wikidata.wdtk.datamodel.implementation.StringValueImpl;
import org.wikidata.wdtk.datamodel.implementation.TimeValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Class that provides objects and strings for testing the conversion between
 * JSON and Java.
 *
 * @author Fredo Erxleben
 *
 */
public class JsonTestData {

	public static final DataObjectFactory JACKSON_OBJECT_FACTORY = new DataObjectFactoryImpl();

	// TODO maybe decompose the time a bit to have fewer magic strings in it

	public static final String JSON_ENTITY_TYPE_ITEM = "item";
	public static final String JSON_ENTITY_TYPE_PROPERTY = "property";

	// the id's used in the tests
	public static final String TEST_PROPERTY_ID = "P1";
	public static final String TEST_ITEM_ID = "Q1";
	public static final int TEST_NUMERIC_ID = 1;
	public static final String TEST_STATEMENT_ID = "statement_foobar";

	public static final String JSON_RANK_NORMAL = "normal";
	public static final String JSON_RANK_DEPRECATED = "deprecated";
	public static final String JSON_RANK_PREFERRED = "preferred";

	// stand-alone descriptions of Value-parts
	public static final String JSON_STRING_VALUE = "{\"type\":\""
			+ ValueImpl.JSON_VALUE_TYPE_STRING + "\",\"value\":\"foobar\"}";
	public static final String JSON_ITEM_ID_VALUE = "{\"type\":\""
			+ ValueImpl.JSON_VALUE_TYPE_ENTITY_ID
			+ "\",\"value\":{\"entity-type\":\"" + JSON_ENTITY_TYPE_ITEM
			+ "\",\"numeric-id\":" + TEST_NUMERIC_ID + ",\"id\":\"" + TEST_ITEM_ID + "\"}}";
    public static final String JSON_ITEM_ID_VALUE_WITHOUT_ID = "{\"type\":\""
            + ValueImpl.JSON_VALUE_TYPE_ENTITY_ID
            + "\",\"value\":{\"entity-type\":\"" + JSON_ENTITY_TYPE_ITEM
            + "\",\"numeric-id\":\"" + TEST_NUMERIC_ID + "\"}}";
    public static final String JSON_ITEM_ID_VALUE_WITHOUT_NUMERICAL_ID = "{\"type\":\""
            + ValueImpl.JSON_VALUE_TYPE_ENTITY_ID
            + "\",\"value\":{\"id\":\"" + TEST_ITEM_ID + "\"}}";
	public static final String JSON_PROPERTY_ID_VALUE = "{\"type\":\""
			+ ValueImpl.JSON_VALUE_TYPE_ENTITY_ID
			+ "\",\"value\":{\"entity-type\":\"" + JSON_ENTITY_TYPE_PROPERTY
			+ "\",\"numeric-id\":" + TEST_NUMERIC_ID + ",\"id\":\"" + TEST_PROPERTY_ID + "\"}}";
	public static final String JSON_TIME_VALUE = "{\"type\":\""
			+ ValueImpl.JSON_VALUE_TYPE_TIME
			+ "\", \"value\":{\"time\":\"+00000002013-10-28T00:00:00Z\",\"timezone\":0,\"before\":0,\"after\":0,\"precision\":11,\"calendarmodel\":\"http://www.wikidata.org/entity/Q1985727\"}}";
	public static final String JSON_GLOBE_COORDINATES_VALUE = "{\"type\":\""
			+ ValueImpl.JSON_VALUE_TYPE_GLOBE_COORDINATES
			+ "\", \"value\":{\"latitude\":-90.0,\"longitude\":0.0,\"precision\":10.0,\"globe\":\"http://www.wikidata.org/entity/Q2\"}}";
	public static final String JSON_QUANTITY_VALUE = "{\"type\":\""
			+ ValueImpl.JSON_VALUE_TYPE_QUANTITY
			+ "\",\"value\":{\"amount\":\"+1\",\"unit\":\"1\",\"upperBound\":\"+1.5\",\"lowerBound\":\"-0.5\"}}";
	public static final String JSON_UNBOUNDED_QUANTITY_VALUE = "{\"type\":\""
			+ ValueImpl.JSON_VALUE_TYPE_QUANTITY
			+ "\",\"value\":{\"amount\":\"+1\",\"unit\":\"1\"}}";
	public static final String JSON_MONOLINGUAL_TEXT_VALUE = "{\"type\":\""
			+ ValueImpl.JSON_VALUE_TYPE_MONOLINGUAL_TEXT
			+ "\",\"value\":{\"language\":\"en\",\"text\":\"foobar\"}}";

	// stand-alone descriptions of ItemDocument-parts
	public static final String JSON_ITEM_TYPE = "\"type\":\"item\"";
	public static final String JSON_TERM_MLTV = "{\"language\": \"en\", \"value\": \"foobar\"}";
	public static final String JSON_SITE_LINK = "{\"site\":\"enwiki\", \"title\":\"foobar\", \"badges\":[]}";
	public static final String JSON_NOVALUE_SNAK = "{\"snaktype\":\"novalue\",\"property\":\""
			+ TEST_PROPERTY_ID + "\"}";
	public static final String JSON_SOMEVALUE_SNAK = "{\"snaktype\":\"somevalue\",\"property\":\""
			+ TEST_PROPERTY_ID + "\"}";
	public static final String JSON_VALUE_SNAK_STRING = "{\"snaktype\":\"value\",\"property\":\""
			+ TEST_PROPERTY_ID + "\",\"datavalue\":" + JSON_STRING_VALUE + "}";
	public static final String JSON_VALUE_SNAK_STRING_HASH = "{\"snaktype\":\"value\",\"property\":\""
			+ TEST_PROPERTY_ID + "\",\"datavalue\":" + JSON_STRING_VALUE + ",\"hash\":\"foobar\"}";

	// wrapping into item document structure for dedicated tests
	public static final String JSON_WRAPPED_LABEL = "{\"id\":\""
			+ TEST_ITEM_ID
			+ "\",\"aliases\":{},\"descriptions\":{},\"claims\":{},\"sitelinks\":{},\"labels\":{\"en\":"
			+ JSON_TERM_MLTV + "}," + JSON_ITEM_TYPE + "}";
	public static final String JSON_WRAPPED_DESCRIPTIONS = "{\"id\":\""
			+ TEST_ITEM_ID
			+ "\",\"aliases\":{},\"labels\":{},\"claims\":{},\"sitelinks\":{},\"descriptions\":{\"en\":"
			+ JSON_TERM_MLTV + "}," + JSON_ITEM_TYPE + "}";
	public static final String JSON_WRAPPED_ALIASES = "{\"id\":\""
			+ TEST_ITEM_ID
			+ "\",\"labels\":{},\"descriptions\":{},\"claims\":{},\"sitelinks\":{},\"aliases\":{\"en\":["
			+ JSON_TERM_MLTV + "]}," + JSON_ITEM_TYPE + "}";
	public static final String JSON_WRAPPED_ITEMID = "{\"id\":\""
			+ TEST_ITEM_ID
			+ "\",\"aliases\":{},\"labels\":{},\"descriptions\":{},\"claims\":{},\"sitelinks\":{},"
			+ JSON_ITEM_TYPE + "}";
	public static final String JSON_WRAPPED_NOITEMID = "{\"aliases\":{},\"labels\":{},\"descriptions\":{},\"claims\":{},\"sitelinks\":{},"
			+ JSON_ITEM_TYPE + "}";
	public static final String JSON_WRAPPED_SITE_LINK = "{\"id\":\""
			+ TEST_ITEM_ID
			+ "\",\"aliases\":{},\"labels\":{},\"descriptions\":{},\"claims\":{},\"sitelinks\":{\"enwiki\":"
			+ JSON_SITE_LINK + "}," + JSON_ITEM_TYPE + "}";
	public static final String JSON_EMPTY_ARRAY_AS_CONTAINER = "{\"id\":\""
			+ TEST_ITEM_ID
			+ "\",\"aliases\":[],\"labels\":[],\"descriptions\":[],\"claims\":[],\"sitelinks\":[],"
			+ JSON_ITEM_TYPE + "}";

	public static final String JSON_NOVALUE_STATEMENT = "{\"type\":\"statement\",\"id\":\""
			+ TEST_STATEMENT_ID
			+ "\",\"rank\":\""
			+ JSON_RANK_NORMAL
			+ "\",\"mainsnak\":" + JSON_NOVALUE_SNAK + "}";

	public static final String JSON_NOVALUE_NOID_STATEMENT = "{\"type\":\"statement\",\"rank\":\""
			+ JSON_RANK_NORMAL + "\",\"mainsnak\":" + JSON_NOVALUE_SNAK + "}";

	// objects to test against
	// should (of course) correspond to the JSON strings counterpart
	public static final TermImpl TEST_MLTV_TERM_VALUE = new TermImpl(
			"en", "foobar");
	public static final SiteLinkImpl TEST_SITE_LINK = (SiteLinkImpl) JACKSON_OBJECT_FACTORY
			.getSiteLink("foobar", "enwiki", Collections.<String> emptyList());

	public static final StringValueImpl TEST_STRING_VALUE = (StringValueImpl) JACKSON_OBJECT_FACTORY
			.getStringValue("foobar");
	public static final ItemIdValueImpl TEST_ITEM_ID_VALUE = (ItemIdValueImpl) JACKSON_OBJECT_FACTORY
			.getItemIdValue("Q1", Datamodel.SITE_WIKIDATA);
	public static final PropertyIdValueImpl TEST_PROPERTY_ID_VALUE = (PropertyIdValueImpl) JACKSON_OBJECT_FACTORY
			.getPropertyIdValue("P1", Datamodel.SITE_WIKIDATA);
	public static final TimeValueImpl TEST_TIME_VALUE = (TimeValueImpl) JACKSON_OBJECT_FACTORY
			.getTimeValue(2013, (byte) 10, (byte) 28, (byte) 0, (byte) 0,
					(byte) 0, (byte) 11, 0, 0, 0, TimeValue.CM_GREGORIAN_PRO);
	public static final GlobeCoordinatesValueImpl TEST_GLOBE_COORDINATES_VALUE = (GlobeCoordinatesValueImpl) JACKSON_OBJECT_FACTORY
			.getGlobeCoordinatesValue(-90.0, 0.0, 10.0,
					GlobeCoordinatesValue.GLOBE_EARTH);
	public static final QuantityValueImpl TEST_QUANTITY_VALUE = (QuantityValueImpl) JACKSON_OBJECT_FACTORY
			.getQuantityValue(new BigDecimal(1), new BigDecimal(-0.5),
					new BigDecimal(1.5));
	public static final QuantityValueImpl TEST_UNBOUNDED_QUANTITY_VALUE = (QuantityValueImpl) JACKSON_OBJECT_FACTORY
			.getQuantityValue(new BigDecimal(1));
	public static final MonolingualTextValueImpl TEST_MONOLINGUAL_TEXT_VALUE = (MonolingualTextValueImpl) JACKSON_OBJECT_FACTORY
			.getMonolingualTextValue("foobar", "en");

	public static final NoValueSnakImpl TEST_NOVALUE_SNAK = (NoValueSnakImpl) JACKSON_OBJECT_FACTORY
			.getNoValueSnak(TEST_PROPERTY_ID_VALUE);
	public static final SomeValueSnakImpl TEST_SOMEVALUE_SNAK = (SomeValueSnakImpl) JACKSON_OBJECT_FACTORY
			.getSomeValueSnak(TEST_PROPERTY_ID_VALUE);
	public static final ValueSnakImpl TEST_STRING_VALUE_SNAK = (ValueSnakImpl) JACKSON_OBJECT_FACTORY
			.getValueSnak(TEST_PROPERTY_ID_VALUE, TEST_STRING_VALUE);

	// TODO continue testing using stringValueSnak, timeValueSnak,
	// globeCoordinateValueSnak

	public static Map<String, MonolingualTextValue> getTestMltvMap() {
		Map<String, MonolingualTextValue> testMltvMap = new HashMap<>();
		testMltvMap.put("en", TEST_MLTV_TERM_VALUE);
		return testMltvMap;
	}

	public static Map<String, List<MonolingualTextValue>> getTestAliases() {
		Map<String, List<MonolingualTextValue>> testAliases = new HashMap<>();

		List<MonolingualTextValue> enAliases = new ArrayList<>();
		enAliases.add(TEST_MLTV_TERM_VALUE);
		testAliases.put("en", enAliases);

		return testAliases;
	}

	public static ItemIdValue getTestItemId() {
		return Datamodel.makeWikidataItemIdValue(TEST_ITEM_ID);
	}

	public static PropertyIdValue getTestPropertyId() {
		return Datamodel.makeWikidataPropertyIdValue(TEST_PROPERTY_ID);
	}

	public static Map<String, SiteLink> getTestSiteLinkMap() {
		Map<String, SiteLink> testSiteLinkMap = new HashMap<>();
		testSiteLinkMap.put("enwiki", TEST_SITE_LINK);
		return testSiteLinkMap;
	}

	public static StatementImpl getTestNoValueStatement() {
		StatementImpl result = new StatementImpl(TEST_STATEMENT_ID,
				TEST_NOVALUE_SNAK,
				getEmptyTestItemDocument().getEntityId());
		return result;
	}

	public static StatementImpl getTestNoValueNoIdStatement() {
		StatementImpl result = new StatementImpl("",
				TEST_NOVALUE_SNAK,getEmptyTestItemDocument().getEntityId());
		return result;
	}

	public static ItemDocumentImpl getEmptyTestItemDocument() {
		ItemDocumentImpl testItemDocument = new ItemDocumentImpl(
				getTestItemId(),
				Collections.<MonolingualTextValue>emptyList(),
				Collections.<MonolingualTextValue>emptyList(),
				Collections.<MonolingualTextValue>emptyList(),
				Collections.<StatementGroup>emptyList(),
				Collections.<SiteLink>emptyList(),
				0);
		return testItemDocument;
	}

	public static ItemDocumentImpl getTestItemDocument() {
		ItemDocumentImpl testItemDocument = new ItemDocumentImpl(
				getTestItemId().getId(),
				getTestMltvMap(),
				getTestMltvMap(),
				getTestAliases(),
				Collections.<String, List<JacksonPreStatement>>emptyMap(),
				Collections.<String, SiteLink>emptyMap(),
				0, getTestItemId().getSiteIri());
		return testItemDocument;
	}

}
