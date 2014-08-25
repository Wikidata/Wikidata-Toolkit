package org.wikidata.wdtk.storage.dbtowdtk;

/*
 * #%L
 * Wikidata Toolkit Storage
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
import java.util.Iterator;
import java.util.List;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.storage.datamodel.ObjectValue;
import org.wikidata.wdtk.storage.datamodel.PropertyValuePair;
import org.wikidata.wdtk.storage.wdtkbindings.WdtkSorts;
import org.wikidata.wdtk.util.NestedIterator;

public class ReferenceFromObjectValue implements Reference {

	final List<SnakGroup> snakGroups;

	public ReferenceFromObjectValue(ObjectValue rov) {
		this.snakGroups = new ArrayList<>(rov.size());

		List<Snak> snaks = null;
		String property = null;

		// Assume that qualifiers list is grouped by property:
		for (PropertyValuePair pvp : rov) {
			String curProperty = pvp.getProperty();
			if (!curProperty.equals(property)) {
				if (property != null
						&& !WdtkSorts.PROP_SOMEVALUE.equals(property)
						&& !WdtkSorts.PROP_NOVALUE.equals(property)) {
					this.snakGroups.add(Datamodel.makeSnakGroup(snaks));
				}

				property = curProperty;
				snaks = new ArrayList<>(1);
			}

			snaks.add(new ValueSnakFromValue(curProperty, pvp.getValue()));
		}

		if (property != null && !WdtkSorts.PROP_SOMEVALUE.equals(property)
				&& !WdtkSorts.PROP_NOVALUE.equals(property)) {
			this.snakGroups.add(Datamodel.makeSnakGroup(snaks));
		}

	}

	@Override
	public List<SnakGroup> getSnakGroups() {
		return this.snakGroups;
	}

	@Override
	public Iterator<Snak> getAllSnaks() {
		return new NestedIterator<>(getSnakGroups());
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsReference(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

}
