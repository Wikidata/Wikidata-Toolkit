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

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import java.util.regex.Pattern;

/**
 * Generic implementation of {@link ItemIdValue} that works with arbitrary
 * Wikibase instances: it requires a baseIri that identifies the site globally.
 *
 * @author Markus Kroetzsch
 *
 */
public class ItemIdValueImpl implements ItemIdValue {

	private static final long serialVersionUID = -2430982177464510496L;
	private static final Pattern ITEM_ID_PATTERN = Pattern.compile("^Q\\d+$");

	private final String id;
	private final String siteIri;

	/**
	 * Creates a new object of this type.
	 *
	 * @param id
	 *            a string of the form Qn... where n... is the string
	 *            representation of a positive integer number
	 * @param baseIri
	 *            the first part of the entity IRI of the site this belongs to,
	 *            e.g., "http://www.wikidata.org/entity/"
	 */
	@Deprecated
	public static ItemIdValueImpl create(String id, String baseIri) {
		return new ItemIdValueImpl(id, baseIri);
	}

	/**
	 * Constructor.

	 * @param id
	 *            a string of the form Qn... where n... is the string
	 *            representation of a positive integer number
	 * @param siteIri
	 *            the first part of the entity IRI of the site this belongs to,
	 *            e.g., "http://www.wikidata.org/entity/"
	 */
	ItemIdValueImpl(String id, String siteIri) {
		Validate.notNull(siteIri, "Entity site IRIs cannot be null");
		if (id == null || !ITEM_ID_PATTERN.matcher(id).matches()) {
			throw new IllegalArgumentException(
					"Wikibase item ids must have the form \"Q<positive integer>\". Given id was \""
							+ id + "\"");
		}
		this.id = id;
		this.siteIri = siteIri;
	}

	@Override
	public String getEntityType() {
		return EntityIdValue.ET_ITEM;
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getIri() {
		return siteIri + id;
	}

	@Override
	public String getSiteIri() {
		return this.siteIri;
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsEntityIdValue(this, obj);
	}
}
