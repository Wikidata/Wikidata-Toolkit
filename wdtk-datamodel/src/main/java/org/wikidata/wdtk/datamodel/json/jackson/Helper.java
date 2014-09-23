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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.JacksonEntityId;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.JacksonItemId;
import org.wikidata.wdtk.datamodel.json.jackson.documents.ids.JacksonPropertyId;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.JacksonSnakGroup;
import org.wikidata.wdtk.datamodel.json.jackson.snaks.JacksonSnak;

/**
 * A class that provides several helper methods.
 *
 * @author Fredo Erxleben
 *
 */
public class Helper {

	/**
	 * The given parameter holds a mapping from the propertyId as String to the
	 * snak for this propertyId.
	 *
	 * @param snaks
	 * @return
	 */
	public static List<SnakGroup> buildSnakGroups(
			Map<String, List<JacksonSnak>> snaks) {

		List<SnakGroup> result = new ArrayList<>();

		for (Entry<String, List<JacksonSnak>> entry : snaks.entrySet()) {
			result.add(new JacksonSnakGroup(new JacksonPropertyId(entry.getKey()),
					entry.getValue()));
		}
		return result;
	}

	/**
	 * The given parameter holds a mapping from the propertyId as String to the
	 * statement for this propertyId.
	 *
	 * @param statements
	 * @return
	 */
	public static List<JacksonStatementGroup> buildStatementGroups(
			Map<String, List<JacksonStatement>> statements) {
		List<JacksonStatementGroup> result = new ArrayList<>();

		for (Entry<String, List<JacksonStatement>> entry : statements.entrySet()) {
			result.add(new JacksonStatementGroup(
					new JacksonPropertyId(entry.getKey()), entry.getValue()));
		}

		return result;
	}

	/**
	 * This is used to construct an EntityId from a given String. The string
	 * might either denote a property or an item.
	 *
	 * @param source
	 *            is either of the form "Q..." for an item or "P..." for a
	 *            property
	 * @return appropriately either a PropertyIdImpl or an ItemIdImpl
	 */
	public static JacksonEntityId constructEntityId(String source) {
		// TODO maybe match via regex to assure the value is formatted
		// correctly?
		if (source.startsWith("Q")) { // is an item
			return new JacksonItemId(source);
		} else if (source.startsWith("P")) { // is a property
			return new JacksonPropertyId("P");
		}
		throw new IllegalArgumentException(source
				+ " could not be matched to be an item- or property id.");
	}
}
