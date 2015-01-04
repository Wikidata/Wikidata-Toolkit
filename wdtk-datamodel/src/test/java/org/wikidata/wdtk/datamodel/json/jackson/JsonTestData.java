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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValue;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueGlobeCoordinates;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueItemId;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueMonolingualText;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValuePropertyId;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueQuantity;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueString;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueTime;

/**
 * Class that provides objects and strings for testing the conversion between
 * JSON and Java.
 *
 * @author Fredo Erxleben
 *
 */
public class JsonTestData {

	public static final DataObjectFactory JACKSON_OBJECT_FACTORY = new JacksonObjectFactory();

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
			+ JacksonValue.JSON_VALUE_TYPE_STRING + "\",\"value\":\"foobar\"}";
	public static final String JSON_ITEM_ID_VALUE = "{\"type\":\""
			+ JacksonValue.JSON_VALUE_TYPE_ENTITY_ID
			+ "\",\"value\":{\"entity-type\":\"" + JSON_ENTITY_TYPE_ITEM
			+ "\",\"numeric-id\":" + TEST_NUMERIC_ID + "}}";
	public static final String JSON_PROPERTY_ID_VALUE = "{\"type\":\""
			+ JacksonValue.JSON_VALUE_TYPE_ENTITY_ID
			+ "\",\"value\":{\"entity-type\":\"" + JSON_ENTITY_TYPE_PROPERTY
			+ "\",\"numeric-id\":" + TEST_NUMERIC_ID + "}}";
	public static final String JSON_TIME_VALUE = "{\"type\":\""
			+ JacksonValue.JSON_VALUE_TYPE_TIME
			+ "\", \"value\":{\"time\":\"+00000002013-10-28T00:00:00Z\",\"timezone\":0,\"before\":0,\"after\":0,\"precision\":11,\"calendarmodel\":\"http://www.wikidata.org/entity/Q1985727\"}}";
	public static final String JSON_GLOBE_COORDINATES_VALUE = "{\"type\":\""
			+ JacksonValue.JSON_VALUE_TYPE_GLOBE_COORDINATES
			+ "\", \"value\":{\"latitude\":-90.0,\"longitude\":0.0,\"precision\":10.0,\"globe\":\"http://www.wikidata.org/entity/Q2\"}}";
	public static final String JSON_QUANTITY_VALUE = "{\"type\":\""
			+ JacksonValue.JSON_VALUE_TYPE_QUANTITY
			+ "\",\"value\":{\"amount\":\"+1\",\"unit\":\"1\",\"upperBound\":\"+1.5\",\"lowerBound\":\"-0.5\"}}";
	public static final String JSON_MONOLINGUAL_TEXT_VALUE = "{\"type\":\""
			+ JacksonValue.JSON_VALUE_TYPE_MONOLINGUAL_TEXT
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
			+ TEST_PROPERTY_ID
			+ "\",\"datatype\":\""
			+ JacksonDatatypeId.JSON_DT_STRING
			+ "\",\"datavalue\":"
			+ JSON_STRING_VALUE + "}";

	// wrapping into item document structure for dedicated tests
	public static final String JSON_WRAPPED_LABEL = "{\"labels\":{\"en\":"
			+ JSON_TERM_MLTV + "}," + JSON_ITEM_TYPE + "}";
	public static final String JSON_WRAPPED_DESCRIPTIONS = "{\"descriptions\":{\"en\":"
			+ JSON_TERM_MLTV + "}," + JSON_ITEM_TYPE + "}";
	public static final String JSON_WRAPPED_ALIASES = "{ \"aliases\":{\"en\":["
			+ JSON_TERM_MLTV + "]}," + JSON_ITEM_TYPE + "}";
	public static final String JSON_WRAPPED_ITEMID = "{\"id\":\""
			+ TEST_ITEM_ID + "\"," + JSON_ITEM_TYPE + "}";
	public static final String JSON_WRAPPED_SITE_LINK = "{\"sitelinks\":{\"enwiki\":"
			+ JSON_SITE_LINK + "}," + JSON_ITEM_TYPE + "}";

	public static final String JSON_NOVALUE_STATEMENT = "{\"type\":\"statement\",\"id\":\""
			+ TEST_STATEMENT_ID
			+ "\",\"rank\":\""
			+ JSON_RANK_NORMAL
			+ "\",\"mainsnak\":" + JSON_NOVALUE_SNAK + "}";

	// objects to test against
	// should (of course) correspond to the JSON strings counterpart
	public static final JacksonMonolingualTextValue TEST_MLTV_TERM_VALUE = new JacksonMonolingualTextValue(
			"en", "foobar");
	public static final JacksonSiteLink TEST_SITE_LINK = (JacksonSiteLink) JACKSON_OBJECT_FACTORY
			.getSiteLink("foobar", "enwiki", Collections.<String> emptyList());

	public static final JacksonValueString TEST_STRING_VALUE = (JacksonValueString) JACKSON_OBJECT_FACTORY
			.getStringValue("foobar");
	public static final JacksonValueItemId TEST_ITEM_ID_VALUE = (JacksonValueItemId) JACKSON_OBJECT_FACTORY
			.getItemIdValue("Q1", Datamodel.SITE_WIKIDATA);
	public static final JacksonValuePropertyId TEST_PROPERTY_ID_VALUE = (JacksonValuePropertyId) JACKSON_OBJECT_FACTORY
			.getPropertyIdValue("P1", Datamodel.SITE_WIKIDATA);
	public static final JacksonValueTime TEST_TIME_VALUE = (JacksonValueTime) JACKSON_OBJECT_FACTORY
			.getTimeValue(2013, (byte) 10, (byte) 28, (byte) 0, (byte) 0,
					(byte) 0, (byte) 11, 0, 0, 0, TimeValue.CM_GREGORIAN_PRO);
	public static final JacksonValueGlobeCoordinates TEST_GLOBE_COORDINATES_VALUE = (JacksonValueGlobeCoordinates) JACKSON_OBJECT_FACTORY
			.getGlobeCoordinatesValue(-90000000000L, 0L, 10000000000L,
					GlobeCoordinatesValue.GLOBE_EARTH);
	public static final JacksonValueQuantity TEST_QUANTITY_VALUE = (JacksonValueQuantity) JACKSON_OBJECT_FACTORY
			.getQuantityValue(new BigDecimal(1), new BigDecimal(-0.5),
					new BigDecimal(1.5));
	public static final JacksonValueMonolingualText TEST_MONOLINGUAL_TEXT_VALUE = (JacksonValueMonolingualText) JACKSON_OBJECT_FACTORY
			.getMonolingualTextValue("foobar", "en");

	public static final JacksonNoValueSnak TEST_NOVALUE_SNAK = (JacksonNoValueSnak) JACKSON_OBJECT_FACTORY
			.getNoValueSnak(TEST_PROPERTY_ID_VALUE);
	public static final JacksonSomeValueSnak TEST_SOMEVALUE_SNAK = (JacksonSomeValueSnak) JACKSON_OBJECT_FACTORY
			.getSomeValueSnak(TEST_PROPERTY_ID_VALUE);
	public static final JacksonValueSnak TEST_STRING_VALUE_SNAK = (JacksonValueSnak) JACKSON_OBJECT_FACTORY
			.getValueSnak(TEST_PROPERTY_ID_VALUE, TEST_STRING_VALUE);

	// TODO continue testing using stringValueSnak, timeValueSnak,
	// globeCoordinateValueSnak

	public static Map<String, JacksonMonolingualTextValue> getTestMltvMap() {
		Map<String, JacksonMonolingualTextValue> testMltvMap = new HashMap<>();
		testMltvMap.put("en", TEST_MLTV_TERM_VALUE);
		return testMltvMap;
	}

	public static Map<String, List<JacksonMonolingualTextValue>> getTestAliases() {
		Map<String, List<JacksonMonolingualTextValue>> testAliases = new HashMap<>();

		List<JacksonMonolingualTextValue> enAliases = new ArrayList<>();
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

	public static Map<String, JacksonSiteLink> getTestSiteLinkMap() {
		Map<String, JacksonSiteLink> testSiteLinkMap = new HashMap<>();
		testSiteLinkMap.put("enwiki", TEST_SITE_LINK);
		return testSiteLinkMap;
	}

	public static JacksonStatement getTestNoValueStatement() {
		JacksonStatement result = new JacksonStatement(TEST_STATEMENT_ID,
				TEST_NOVALUE_SNAK);
		result.setParentDocument(getEmtpyTestItemDocument());
		return result;
	}

	public static JacksonItemDocument getEmtpyTestItemDocument() {
		JacksonItemDocument testItemDocument = new JacksonItemDocument();
		testItemDocument.setJsonId(getTestItemId().getId());
		testItemDocument.setSiteIri(Datamodel.SITE_WIKIDATA);
		return testItemDocument;
	}

	public static JacksonItemDocument getTestItemDocument() {
		JacksonItemDocument testItemDocument = new JacksonItemDocument();
		testItemDocument.setJsonId(getTestItemId().getId());
		testItemDocument.setSiteIri(Datamodel.SITE_WIKIDATA);
		testItemDocument.setAliases(getTestAliases());
		testItemDocument.setDescriptions(getTestMltvMap());
		testItemDocument.setLabels(getTestMltvMap());
		return testItemDocument;
	}

}
