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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;

/**
 * Helper class to represent a {@link SnakGroup} deserialized from JSON. The
 * actual data is part of a map of lists of {@link JacksonSnak} objects in JSON,
 * so there is no corresponding JSON object.
 *
 * @author Markus Kroetzsch
 *
 */
public class SnakGroupFromJson implements SnakGroup {

	private final List<Snak> snaks;

	public SnakGroupFromJson(List<JacksonSnak> snaks) {
		this.snaks = Collections.<Snak> unmodifiableList(snaks);
	}

	@Override
	public List<Snak> getSnaks() {
		return this.snaks;
	}

	@Override
	public PropertyIdValue getProperty() {
		return this.snaks.get(0).getPropertyId();
	}

	@Override
	public Iterator<Snak> iterator() {
		return this.snaks.iterator();
	}

	/**
	 * Construct a list of {@link SnakGroup} objects from a map from property
	 * ids to snak lists as found in JSON.
	 *
	 * @param snaks
	 *            the map with the data
	 * @return the result list
	 */
	public static List<SnakGroup> makeSnakGroups(
			Map<String, List<JacksonSnak>> snaks, List<String> propertyOrder) {

		List<SnakGroup> result = new ArrayList<>(snaks.size());

		for (String propertyName : propertyOrder) {
			result.add(new SnakGroupFromJson(snaks.get(propertyName)));
		}

		return result;
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsSnakGroup(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

}
