package org.wikidata.wdtk.datamodel.jsonconverter;

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

public abstract class TestRessources {
	final static String ITEM_DOCUMENT_REPRES = "{\"id\":\"Q10\",\"claims\":{\"P11\":[{\"id\":\"none\",\"rank\":\"normal\",\"mainsnak\":{\"property\":\"P11\",\"snaktype\":\"novalue\"},\"type\":\"statement\"}],\"P1040\":[{\"id\":\"none2\",\"references\":[{\"snak-order\":[\"P112\"],\"snaks\":{\"P112\":[{\"property\":\"P112\",\"datatype\":\"time\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"calendarmodel\":\"http://www.wikidata.org/entity/Q1985727\",\"after\":5246,\"timezone\":22080,\"time\":\"+00000002196-07-03T04:00:00Z\",\"precision\":110,\"before\":2074},\"type\":\"time\"}}]}}],\"rank\":\"normal\",\"mainsnak\":{\"property\":\"P1040\",\"datatype\":\"time\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"calendarmodel\":\"http://www.wikidata.org/entity/Q1985727\",\"after\":731,\"timezone\":6630,\"time\":\"+00000000306-04-24T10:58:41Z\",\"precision\":51,\"before\":289},\"type\":\"time\"}},\"qualifiers\":{\"http://www.wikidata.org/ontology#Property\":[{\"property\":\"P15\",\"datatype\":\"time\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"calendarmodel\":\"http://www.wikidata.org/entity/Q1985727\",\"after\":602,\"timezone\":5460,\"time\":\"+00000000252-07-15T04:37:23Z\",\"precision\":42,\"before\":238},\"type\":\"time\"}}]},\"type\":\"statement\"},{\"id\":\"none\",\"rank\":\"normal\",\"mainsnak\":{\"property\":\"P1040\",\"datatype\":\"time\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"calendarmodel\":\"http://www.wikidata.org/entity/Q1985727\",\"after\":129,\"timezone\":1170,\"time\":\"+00000000054-10-10T06:21:18Z\",\"precision\":9,\"before\":51},\"type\":\"time\"}},\"type\":\"statement\"}]},\"title\":\"Q10\",\"labels\":{\"lc\":{\"value\":\"foo\",\"language\":\"lc\"},\"lc2\":{\"value\":\"bar\",\"language\":\"lc2\"}},\"type\":\"item\",\"aliases\":{\"lc\":[{\"value\":\"foo\",\"language\":\"lc\"},{\"value\":\"bar\",\"language\":\"lc\"}]},\"descriptions\":{\"lc\":{\"value\":\"it's foo\",\"language\":\"lc\"},\"lc2\":{\"value\":\"it's bar\",\"language\":\"lc2\"}},\"sitelinks\":{\"auwiki\":{\"site\":\"siteKey\",\"badges\":[],\"title\":\"title_au\"},\"enwiki\":{\"site\":\"siteKey\",\"badges\":[],\"title\":\"title_en\"}}}";

	
	final static String EMPTY_PROPERTY_DOCUMENT_REPRES = "{\"id\":\"P42\",\"title\":\"P42\",\"type\":\"property\"}";
	final static String CLAIM_REPRES = "{\"mainsnak\":{\"property\":\"P129\",\"datatype\":\"time\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"calendarmodel\":\"http://www.wikidata.org/entity/Q1985727\",\"after\":1806,\"timezone\":16380,\"time\":\"+00000000756-07-15T12:50:08Z\",\"precision\":126,\"before\":714},\"type\":\"time\"}}}";
	final static String ITEM_ID_VALUE_REPRES = "{\"entity-type\":\"item\",\"numeric-id\":\"Q200\"}";
	final static String PROPERTY_ID_VALUE_REPRES = "{\"entity-type\":\"property\",\"numeric-id\":\"P200\"}";
	final static String ENTITY_ID_VALUE_REPRES = "{\"value\":{\"entity-type\":\"item\",\"numeric-id\":\"Q200\"},\"type\":\"wikibase-entityid\"}";
	final static String ENTITY_ID_VALUE_REPRES_2 = "{\"value\":{\"entity-type\":\"property\",\"numeric-id\":\"P200\"},\"type\":\"wikibase-entityid\"}";
	final static String VALUE_SNAK_ITEM_ID_VALUE_REPRES = "{\"property\":\"P132\",\"datatype\":\"wikibase-item\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"entity-type\":\"item\",\"numeric-id\":\"Q233\"},\"type\":\"wikibase-entityid\"}}";
	final static String VALUE_SNAK_STRING_VALUE_REPRES = "{\"property\":\"P132\",\"datatype\":\"string\",\"snaktype\":\"value\",\"datavalue\":{\"value\":\"TestString\",\"type\":\"string\"}}";
	final static String VALUE_SNAK_GLOBE_COORDINATES_VALUE_REPRES = "{\"property\":\"P132\",\"datatype\":\"globe-coordinate\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"precision\":0,\"longitude\":21314,\"latitude\":213124,\"globe\":\"http://www.wikidata.org/entity/Q2\"},\"type\":\"globecoordinate\"}}";
	final static String VALUE_SNAK_QUANTITY_VALUE_REPRES = "{\"property\":\"P231\",\"datatype\":\"quantity\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"amount\":\"+3\",\"unit\":\"1\",\"lowerBound\":\"+3\",\"upperBound\":\"+3\"},\"type\":\"quantity\"}}";
	final static String SOME_VALUE_SNAK_REPRES = "{\"property\":\"P1231\",\"snaktype\":\"somevalue\"}";
}