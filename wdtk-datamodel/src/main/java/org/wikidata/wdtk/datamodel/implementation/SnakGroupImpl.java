package org.wikidata.wdtk.datamodel.implementation;

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

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;

/**
 * Implementation of {@link SnakGroup}.
 *
 * @author Markus Kroetzsch
 *
 */
public class SnakGroupImpl implements SnakGroup, Serializable {

	private static final long serialVersionUID = -6497504020405854760L;
	
	final List<? extends Snak> snaks;

	/**
	 * Constructor.
	 *
	 * @param snaks
	 *            a non-empty list of snaks that use the same property
	 */
	SnakGroupImpl(List<? extends Snak> snaks) {
		Validate.notNull(snaks, "List of statements cannot be null");
		Validate.notEmpty(snaks, "List of statements cannot be empty");

		PropertyIdValue property = snaks.get(0).getPropertyId();

		for (Snak s : snaks) {
			if (!property.equals(s.getPropertyId())) {
				throw new IllegalArgumentException(
						"All snaks in a snak group must use the same property");
			}
		}

		this.snaks = snaks;

	}

	@Override
	public List<Snak> getSnaks() {
		return Collections.unmodifiableList(this.snaks);
	}

	@Override
	public PropertyIdValue getProperty() {
		return this.snaks.get(0).getPropertyId();
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

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Snak> iterator() {
		return (Iterator<Snak>) this.snaks.iterator();
	}
}
