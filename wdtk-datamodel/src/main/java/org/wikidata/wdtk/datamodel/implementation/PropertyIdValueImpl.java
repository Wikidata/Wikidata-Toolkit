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

import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * Generic implementation of {@link PropertyIdValue} that works with arbitrary
 * Wikibase instances: it requires a baseIri that identifies the site globally.
 *
 * @author Markus Kroetzsch
 *
 */
public class PropertyIdValueImpl extends NumericEntityIdValueImpl implements
		PropertyIdValue {

	private static final long serialVersionUID = 3427673190538556373L;

	/**
	 * Creates a new object of this type.
	 *
	 * @param id
	 *            a string of the form Pn... where n... is the string
	 *            representation of a positive integer number
	 * @param baseIri
	 *            the first part of the entity IRI of the site this belongs to,
	 *            e.g., "http://www.wikidata.org/entity/"
	 */
	public static PropertyIdValueImpl create(String id,
			String baseIri) {
		if (id == null || id.length() <= 1 || id.charAt(0) != 'P') {
			throw new IllegalArgumentException(
					"Wikibase property ids must have the form \"P<positive integer>\". Given id was \""
							+ id + "\"");
		}

		try {
			int numId = new Integer(id.substring(1));
			return new PropertyIdValueImpl(numId, baseIri);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
					"Wikibase property ids must have the form \"P<positive integer>\". Given id was \""
							+ id + "\"");
		}
	}

	/**
	 * Constructor. Use {@link #create(String, String)} to
	 * create objects of this type.
	 *
	 * @see NumericEntityIdValueImpl#EntityIdImpl(int, String)
	 * @param id
	 *            the numeric id of this property (the number after "P")
	 * @param baseIri
	 *            the first part of the entity IRI of the site this belongs to,
	 *            e.g., "http://www.wikidata.org/entity/"
	 */
	private PropertyIdValueImpl(int id, String baseIri) {
		super(id, baseIri);
	}

	@Override
	public String getEntityType() {
		return EntityIdValue.ET_PROPERTY;
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

	@Override
	public String getId() {
		return "P" + this.id;
	}

}
