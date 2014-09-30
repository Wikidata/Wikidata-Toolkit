package org.wikidata.wdtk.dumpfiles.oldjson;

/*
 * #%L
 * Wikidata Toolkit Dump File Handling
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

/**
 * This class is a handler for mono-lingual text values.
 *
 * @author Fredo Erxleben
 *
 */
public class MonolingualTextValueHandler {

	private final DataObjectFactory factory;

	public MonolingualTextValueHandler(DataObjectFactory factory) {
		this.factory = factory;
	}

	public List<MonolingualTextValue> convertToMltv(JSONObject container) {
		// the container object is of the form {"key", value}
		// where the value might be a JSONArray of strings
		// or a JSONObject itself
		// or simply a string associated with the key

		List<MonolingualTextValue> result = new LinkedList<MonolingualTextValue>();

		@SuppressWarnings("unchecked")
		Iterator<String> keyIterator = container.keys();

		while (keyIterator.hasNext()) {

			String key = keyIterator.next();
			List<String> values = new LinkedList<>();

			// catch null values
			if (container.isNull(key)) {
				continue;
			}

			// differentiate between objects and arrays
			JSONArray arrayEntries = container.optJSONArray(key);
			JSONObject objEntries = container.optJSONObject(key);
			String stringEntry = container.optString(key);

			if (arrayEntries != null) { // it is an array
				values = this.extractValues(arrayEntries);
			} else if (objEntries != null) { // it is an object
				values = this.extractValues(objEntries);
			} else if (stringEntry != null) { // it is a string
				values.add(stringEntry);
			}
			// else do nothing…
			// this is not in a known format

			// add the found results to the overall result list
			for (String s : values) {
				MonolingualTextValue toAdd = this.factory
						.getMonolingualTextValue(s, key);
				result.add(toAdd);
			}
		} // all keys checked

		return result;
	}

	private List<String> extractValues(JSONObject objEntries) {
		// object contains numericStrings as keys
		// and the desired Strings as values

		List<String> result = new LinkedList<>();

		@SuppressWarnings("unchecked")
		Iterator<String> keyIterator = objEntries.keys();

		while (keyIterator.hasNext()) {

			String key = keyIterator.next();

			if (objEntries.isNull(key)) { // skip null values
				continue;
			}

			String value = objEntries.optString(key);
			if (value != null) {
				result.add(value);
			}
		}

		return result;
	}

	/**
	 * Extract the entries of an JSONArray as a list of strings.
	 *
	 * @param arrayEntries
	 *            is a JSONArray containing the desired Strings straight ahead.
	 *            In case of other formats the entries which are not strings
	 *            will be skipped.
	 * @return a list containing the extracted strings. Might be empty, but not
	 *         null.
	 */
	private List<String> extractValues(JSONArray arrayEntries) {
		// the array contains the desired values
		// straight forward
		List<String> result = new LinkedList<>();

		for (int i = 0; i < arrayEntries.length(); i++) {
			String value = arrayEntries.optString(i);
			if (value != null) {
				result.add(value);
			}
		}
		return result;
	}
}
