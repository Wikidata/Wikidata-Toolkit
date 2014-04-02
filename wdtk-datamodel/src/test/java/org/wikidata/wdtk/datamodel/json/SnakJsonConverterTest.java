package org.wikidata.wdtk.datamodel.json;

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

import org.json.JSONObject;
import org.junit.Test;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.json.SnakJsonConverter;
import org.wikidata.wdtk.datamodel.json.ValueJsonConverter;

public class SnakJsonConverterTest {

	final static String JSON_VALUE_SNAK_ITEM_ID_VALUE = "{\"property\":\"P132\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"entity-type\":\"item\",\"numeric-id\":\"Q233\"},\"type\":\"wikibase-entityid\"}}";
	final static String JSON_VALUE_SNAK_STRING_VALUE = "{\"property\":\"P132\",\"snaktype\":\"value\",\"datavalue\":{\"value\":\"TestString\",\"type\":\"string\"}}";
	final static String JSON_VALUE_SNAK_GLOBE_COORDINATES_VALUE = "{\"property\":\"P132\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"precision\":0.016666667,\"longitude\":2.1314E-5,\"latitude\":2.13124E-4,\"globe\":\"http://www.wikidata.org/entity/Q2\"},\"type\":\"globecoordinate\"}}";
	final static String JSON_VALUE_SNAK_QUANTITY_VALUE = "{\"property\":\"P231\",\"snaktype\":\"value\",\"datavalue\":{\"value\":{\"amount\":\"+3\",\"unit\":\"1\",\"lowerBound\":\"+3\",\"upperBound\":\"+3\"},\"type\":\"quantity\"}}";
	final static String JSON_SOME_VALUE_SNAK = "{\"property\":\"P1231\",\"snaktype\":\"somevalue\"}";
	final static String JSON_NO_VALUE_SNAK = "{\"property\":\"P10\",\"snaktype\":\"novalue\"}";

	final DataObjectFactory dataObjectFactory = new DataObjectFactoryImpl();
	final TestObjectFactory testObjectFactory = new TestObjectFactory();
	final ValueJsonConverter valueJsonConverter = new ValueJsonConverter();
	final SnakJsonConverter snakJsonConverter = new SnakJsonConverter(
			this.valueJsonConverter);

	@Test
	public void testVisitValueSnakStringValue() {
		ValueSnak snak = testObjectFactory.createValueSnakStringValue("P132");
		JsonResultComparer.compareJSONObjects(snak.accept(snakJsonConverter),
				new JSONObject(JSON_VALUE_SNAK_STRING_VALUE));
	}

	@Test
	public void testVisitValueSnakGlobeCoordinatesValue() {
		ValueSnak snak = testObjectFactory
				.createValueSnakGlobeCoordinatesValue("P132");
		JsonResultComparer.compareJSONObjects(snak.accept(snakJsonConverter),
				new JSONObject(JSON_VALUE_SNAK_GLOBE_COORDINATES_VALUE));
	}

	@Test
	public void testVisitValueSnakQuantityValue() {
		ValueSnak snak = testObjectFactory.createValueSnakQuantityValue("P231");
		JsonResultComparer.compareJSONObjects(snak.accept(snakJsonConverter),
				new JSONObject(JSON_VALUE_SNAK_QUANTITY_VALUE));
	}

	@Test
	public void testVisitValueSnakItemIdValue() {
		ValueSnak snak = testObjectFactory.createValueSnakItemIdValue("P132",
				"Q233");
		JsonResultComparer.compareJSONObjects(snak.accept(snakJsonConverter),
				new JSONObject(JSON_VALUE_SNAK_ITEM_ID_VALUE));
	}

	@Test
	public void testVisitConvertSomeValueSnak() {
		SomeValueSnak snak = testObjectFactory.createSomeValueSnak("P1231");
		JsonResultComparer.compareJSONObjects(snak.accept(snakJsonConverter),
				new JSONObject(JSON_SOME_VALUE_SNAK));

	}

	@Test
	public void testVisitNoValueSnak() {
		NoValueSnak snak = dataObjectFactory.getNoValueSnak(dataObjectFactory
				.getPropertyIdValue("P10", "test/"));
		JsonResultComparer.compareJSONObjects(snak.accept(snakJsonConverter),
				new JSONObject(JSON_NO_VALUE_SNAK));
	}

}
