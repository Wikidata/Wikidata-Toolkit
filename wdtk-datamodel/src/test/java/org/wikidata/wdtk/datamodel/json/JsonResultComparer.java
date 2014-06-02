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

import static org.junit.Assert.assertEquals;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

/**
 * Static helper methods for comparing JSON structures during tests. We avoid
 * the name "Comparator" here to avoid confusion with the Java class.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class JsonResultComparer {

	/**
	 * Compares obj1 with obj2. If the content is equal (same keys, same values)
	 * nothing happens. Otherwise the function will cause a Fail.
	 * 
	 * @param obj1
	 * @param obj2
	 */
	public static void compareJSONObjects(JSONObject obj1, JSONObject obj2) {
		try {
			assertEquals(obj1.keySet(), obj2.keySet());
			for (Object key : obj1.keySet()) {
				if (obj1.get((String) key) instanceof JSONObject) {
					compareJSONObjects(obj1.getJSONObject((String) key),
							obj2.getJSONObject((String) key));
				} else if (obj1.get((String) key) instanceof JSONArray) {
					JSONArray arrayObj1 = obj1.getJSONArray((String) key);
					JSONArray arrayObj2 = obj2.getJSONArray((String) key);
					compareJSONArrays(arrayObj1, arrayObj2);
				} else {
					assertEquals(comparableObject(obj1.get((String) key)),
							comparableObject(obj2.get((String) key)));
				}
			}
		} catch (JSONException e) {
			assertEquals("JSON objects do not match", obj1.toString(),
					obj2.toString());
		}
	}

	/**
	 * Compares array1 with array2. If the content is equal (same values in the
	 * same order) nothing happens. Otherwise the function will cause a Fail.
	 * 
	 * @param array1
	 * @param array2
	 */
	public static void compareJSONArrays(JSONArray array1, JSONArray array2) {
		try {
			assertEquals(array1.length(), array2.length());
			for (int index = 0; index < array1.length(); index++) {
				if (array1.get(index) instanceof JSONObject) {
					compareJSONObjects(array1.getJSONObject(index),
							array2.getJSONObject(index));
				} else if (array1.get(index) instanceof JSONArray) {
					JSONArray arrayElem1 = array1.getJSONArray(index);
					JSONArray arrayElem2 = array2.getJSONArray(index);
					compareJSONArrays(arrayElem1, arrayElem2);
				} else {
					assertEquals(comparableObject(array1.get(index)),
							comparableObject(array2.get(index)));
				}
			}
		} catch (JSONException e) {
			assertEquals("JSON arrays do not match", array1.toString(),
					array1.toString());
		}
	}

	/**
	 * Sometimes values in the key-value-pairs of a JSON file are not clearly
	 * assignable to a data type. This function converts these values in a
	 * comparable type.
	 * 
	 * @param val
	 * 
	 * @return comparable object
	 */
	public static Object comparableObject(Object val) {
		if (val instanceof Integer) {
			return ((Integer) val).longValue();
		}

		return val;
	}

	@Test
	public void testCompareJSONObjects() {
		JSONObject obj1 = new JSONObject(
				"{\"numeric-id\":\"Q200\", \"array\":[\"a\", \"b\"], \"entity-type\":\"item\"}");
		JSONObject obj2 = new JSONObject(
				"{\"array\":[\"a\", \"b\"], \"entity-type\":\"item\", \"numeric-id\":\"Q200\"}");
		JsonResultComparer.compareJSONObjects(obj1, obj2);
	}

}
