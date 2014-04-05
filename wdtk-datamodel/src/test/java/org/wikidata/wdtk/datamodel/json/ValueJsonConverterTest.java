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
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.json.ValueJsonConverter;

public class ValueJsonConverterTest {

	final static String JSON_ITEM_ID_VALUE = "{\"value\":{\"entity-type\":\"item\",\"numeric-id\":\"Q200\"},\"type\":\"wikibase-entityid\"}";
	final static String JSON_PROPERTY_ID_VALUE = "{\"value\":{\"entity-type\":\"property\",\"numeric-id\":\"P200\"},\"type\":\"wikibase-entityid\"}";
	final static String JSON_MONOLINGUAL_TEXT_VALUE = "{\"value\":\"some text in a language lc\",\"language\":\"lc\"}";

	final DataObjectFactory dataObjectFactory = new DataObjectFactoryImpl();
	final TestObjectFactory testObjectFactory = new TestObjectFactory();
	final ValueJsonConverter valueJsonConverter = new ValueJsonConverter();

	@Test
	public void testVisitPropertyIdValue() {
		PropertyIdValue value = testObjectFactory.createPropertyIdValue("P200");
		JsonResultComparer.compareJSONObjects(value.accept(valueJsonConverter),
				new JSONObject(JSON_PROPERTY_ID_VALUE));
	}

	@Test
	public void testVisitItemIdValue() {
		ItemIdValue value = testObjectFactory.createItemIdValue("Q200");
		JsonResultComparer.compareJSONObjects(value.accept(valueJsonConverter),
				new JSONObject(JSON_ITEM_ID_VALUE));
	}

	@Test
	public void testVisitMonolingualTextValue() {
		MonolingualTextValue value = dataObjectFactory.getMonolingualTextValue(
				"some text in a language lc", "lc");
		JsonResultComparer.compareJSONObjects(value.accept(valueJsonConverter),
				new JSONObject(JSON_MONOLINGUAL_TEXT_VALUE));

	}

}
