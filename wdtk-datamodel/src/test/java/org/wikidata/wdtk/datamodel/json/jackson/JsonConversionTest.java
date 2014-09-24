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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerEntityId;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerGlobeCoordinates;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerQuantity;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonInnerTime;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValue;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueEntityId;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueGlobeCoordinates;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueMonolingualText;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueQuantity;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueString;
import org.wikidata.wdtk.datamodel.json.jackson.datavalues.JacksonValueTime;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a superclass for all tests regarding the conversion of Wikidata
 * Web-API JSON into the WDTK data model and the other way around. It provides
 * mainly constants and the needed mapper objects.
 *
 * @author Fredo Erxleben
 *
 */
public abstract class JsonConversionTest {

	// TODO maybe decompose the time a bit to have less magic strings in it

	protected static ObjectMapper mapper = new ObjectMapper();
	protected static Logger logger = LoggerFactory
			.getLogger(JsonConversionTest.class);

	protected static final String entityTypeItem = "item";

	// the id's used in the tests
	protected static final String propertyId = "P1";
	protected static final String itemId = "Q1";
	protected static final int numericId = 1;
	protected static final String statementId = "statement_foobar";
	protected static final String rankNormal = "normal";
	protected static final String rankDeprecated = "deprecated";
	protected static final String rankPreferred = "preferred";

	// stand-alone descriptions of Value-parts
	protected static final String stringValueJson = "{\"type\":\""
			+ JacksonValue.JSON_VALUE_TYPE_STRING + "\",\"value\":\"foobar\"}";
	protected static final String entityIdValueJson = "{\"type\":\""
			+ JacksonValue.JSON_VALUE_TYPE_ENTITY_ID
			+ "\",\"value\":{\"entity-type\":\"" + entityTypeItem
			+ "\",\"numeric-id\":" + numericId + "}}";
	protected static final String timeValueJson = "{\"type\":\""
			+ JacksonValue.JSON_VALUE_TYPE_TIME
			+ "\", \"value\":{\"time\":\"+00000002013-10-28T00:00:00Z\",\"timezone\":0,\"before\":0,\"after\":0,\"precision\":11,\"calendarmodel\":\"http://www.wikidata.org/entity/Q1985727\"}}";
	protected static final String globeCoordinateValueJson = "{\"type\":\""
			+ JacksonValue.JSON_VALUE_TYPE_GLOBE_COORDINATES
			+ "\", \"value\":{\"latitude\":-90,\"longitude\":0,\"precision\":10,\"globe\":\"http://www.wikidata.org/entity/Q2\"}}";
	protected static final String quantityValueJson = "{\"type\":\""
			+ JacksonValue.JSON_VALUE_TYPE_QUANTITY
			+ "\",\"value\":{\"amount\":\"+1\",\"unit\":\"1\",\"upperBound\":\"+1.5\",\"lowerBound\":\"-0.5\"}}";
	protected static final String mltDatavalueJson = "{\"type\":\""
			+ JacksonValue.JSON_VALUE_TYPE_MONOLINGUAL_TEXT
			+ "\",\"value\":{\"language\":\"en\",\"text\":\"foobar\"}}";

	// stand-alone descriptions of ItemDocument-parts
	protected static final String itemTypeJson = "\"type\":\"item\"";
	protected static final String mltvJson = "{\"language\": \"en\", \"value\": \"foobar\"}";
	protected static final String siteLinkJson = "{\"site\":\"enwiki\", \"title\":\"foobar\", \"badges\":[]}";
	protected static final String noValueSnakJson = "{\"snaktype\":\"novalue\",\"property\":\""
			+ propertyId + "\"}";
	protected static final String someValueSnakJson = "{\"snaktype\":\"somevalue\",\"property\":\""
			+ propertyId + "\"}";
	protected static final String commonsValueSnakJson = "{\"snaktype\":\"value\",\"property\":\""
			+ propertyId
			+ "\",\"datatype\":\""
			+ JacksonDatatypeId.JSON_DT_COMMONS_MEDIA
			+ "\",\"datavalue\":"
			+ stringValueJson + "}";

	// wrapping into item document structure for dedicated tests
	protected static final String wrappedLabelJson = "{\"labels\":{\"en\":"
			+ mltvJson + "}," + itemTypeJson + "}";
	protected static final String wrappedDescriptionJson = "{\"descriptions\":{\"en\":"
			+ mltvJson + "}," + itemTypeJson + "}";
	protected static final String wrappedAliasJson = "{ \"aliases\":{\"en\":["
			+ mltvJson + "]}," + itemTypeJson + "}";
	protected static final String wrappedItemIdJson = "{\"id\":\"" + itemId
			+ "\"," + itemTypeJson + "}";
	protected static final String wrappedSiteLinkJson = "{\"sitelinks\":{\"enwiki\":"
			+ siteLinkJson + "}," + itemTypeJson + "}";

	protected static final String emptyStatementJson = "{\"type\":\"statement\",\"id\":\""
			+ statementId
			+ "\",\"rank\":\""
			+ rankNormal
			+ "\",\"mainsnak\":"
			+ noValueSnakJson + "}";

	// objects to test against
	// should (of course) correspond to the JSON strings counterpart
	protected static final JacksonMonolingualTextValue testMltv = new JacksonMonolingualTextValue(
			"en", "foobar");
	protected static final JacksonSiteLink testSiteLink = new JacksonSiteLink(
			"enwiki", "foobar");

	protected static final JacksonValueString testStringValue = new JacksonValueString(
			"foobar");
	protected static final JacksonValueEntityId testEntityIdValue = new JacksonValueEntityId(
			new JacksonInnerEntityId(entityTypeItem, numericId));
	protected static final JacksonValueTime testTimeValue = new JacksonValueTime(
			new JacksonInnerTime("+00000002013-10-28T00:00:00Z", 0, 0, 0, 11,
					"http://www.wikidata.org/entity/Q1985727"));
	protected static final JacksonValueGlobeCoordinates testGlobeCoordinateValue = new JacksonValueGlobeCoordinates(
			new JacksonInnerGlobeCoordinates(-90, 0, 10,
					"http://www.wikidata.org/entity/Q2"));
	protected static final JacksonValueQuantity testQuantityValue = new JacksonValueQuantity(
			new JacksonInnerQuantity(new BigDecimal(1), new BigDecimal(1.5),
					new BigDecimal(-0.5)));
	protected static final JacksonValueMonolingualText testMltDatavalue = new JacksonValueMonolingualText(
			"en", "foobar");

	protected static final JacksonNoValueSnak testNoValueSnak = new JacksonNoValueSnak(
			propertyId);
	protected static final JacksonSomeValueSnak testSomeValueSnak = new JacksonSomeValueSnak(
			propertyId);
	protected static final JacksonValueSnak testCommonsValueSnak = new JacksonValueSnak(
			propertyId, JacksonDatatypeId.JSON_DT_COMMONS_MEDIA,
			testStringValue);
	// TODO continue testing using stringValueSnak, timeValueSnak,
	// globeCoordinateValueSnak

	// puzzle pieces for creation of the test of ItemDocument and
	// PropertyDocument
	protected Map<String, JacksonMonolingualTextValue> testMltvMap;
	protected Map<String, List<JacksonMonolingualTextValue>> testAliases;
	protected ItemIdValue testItemId;
	protected PropertyIdValue testPropertyId;
	protected Map<String, JacksonSiteLink> testSiteLinkMap;
	protected JacksonStatement testEmptyStatement;
	protected ClaimFromJson testClaim;

	@Before
	public void setupTestMltv() {
		testMltvMap = new HashMap<>();
		testMltvMap.put("en", TestMonolingualTextValue.testMltv);
	}

	@Before
	public void setupTestAliases() {
		testAliases = new HashMap<>();
		List<JacksonMonolingualTextValue> aliases = new LinkedList<>();
		aliases.add(TestMonolingualTextValue.testMltv);
		testAliases.put("en", aliases);
	}

	@Before
	public void setupTestItemId() {
		testItemId = Datamodel.makeWikidataItemIdValue(itemId);
		// FIXME never do tests during setup!
		assertEquals(testItemId.getId(), itemId);
	}

	@Before
	public void setupTestPropertyId() {
		testPropertyId = Datamodel.makeWikidataPropertyIdValue(propertyId);
		// FIXME never do tests during setup!
		assertEquals(testPropertyId.getId(), propertyId);
	}

	@Before
	public void setupTestSiteLinks() {
		testSiteLinkMap = new HashMap<>();
		testSiteLinkMap.put("enwiki", TestSiteLink.testSiteLink);
	}

	@Before
	public void setupTestStatementAndClaim() {
		testEmptyStatement = new JacksonStatement(statementId, testNoValueSnak);
		testEmptyStatement.setSubject(testItemId);
	}

}
