package org.wikidata.wdtk.dumpfiles;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

/**
 * MLTV is the abbreviation for mono-lingual text value.
 * 
 * @author fredo
 * 
 */
public class MltvHandler {

	private DataObjectFactory factory;

	public MltvHandler(DataObjectFactory factory) {
		this.factory = factory;
	}

	public List<MonolingualTextValue> convertToMltv(JSONObject container) {
		// the container object is of the form {"key", value}
		// where the value might be a JSONArray of strings
		// or a JSONObject itself

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

			if (arrayEntries != null) { // it is an array
				values = this.extractValues(arrayEntries);
			} else if (objEntries != null) { // it is an object
				values = this.extractValues(objEntries);
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
