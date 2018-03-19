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
import org.wikidata.wdtk.datamodel.implementation.ItemDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.TermImpl;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.implementation.SiteLinkImpl;
import org.wikidata.wdtk.datamodel.interfaces.*;

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

	// the id's used in the tests
	public static final String TEST_PROPERTY_ID = "P1";
	public static final String TEST_ITEM_ID = "Q1";
	public static final String TEST_STATEMENT_ID = "statement_foobar";

	public static final String JSON_RANK_NORMAL = "normal";

	// stand-alone descriptions of ItemDocument-parts
	public static final String JSON_ITEM_TYPE = "\"type\":\"item\"";
	public static final String JSON_TERM_MLTV = "{\"language\": \"en\", \"value\": \"foobar\"}";
	public static final String JSON_SITE_LINK = "{\"site\":\"enwiki\", \"title\":\"foobar\", \"badges\":[]}";
	public static final String JSON_NOVALUE_SNAK = "{\"snaktype\":\"novalue\",\"property\":\""
			+ TEST_PROPERTY_ID + "\"}";

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

	// objects to test against
	// should (of course) correspond to the JSON strings counterpart
	public static final TermImpl TEST_MLTV_TERM_VALUE = new TermImpl(
			"en", "foobar");
	public static final SiteLinkImpl TEST_SITE_LINK = (SiteLinkImpl) JACKSON_OBJECT_FACTORY
			.getSiteLink("foobar", "enwiki", Collections. emptyList());

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

	public static ItemDocumentImpl getEmptyTestItemDocument() {
		return new ItemDocumentImpl(
				getTestItemId(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				Collections.emptyList(),
				0);
	}

}
