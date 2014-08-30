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
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

/**
 * Generic implementation of {@link EntityIdValue} that works with arbitrary
 * Wikibase instances: it requires a baseIri that identifies the site globally.
 *
 * TODO It would be cleaner to have an object that manages the site context
 * instead of passing a base IRI string that is simply concatenated.
 *
 * TODO For our common use case that Wikidata entities are processed, it might
 * be useful to have a more lightweight object that does not store this known
 * base IRI.
 *
 * @author Markus Kroetzsch
 *
 */
public abstract class EntityIdValueImpl implements EntityIdValue {

	final String id;
	final String siteIri;

	/**
	 * Constructor.
	 *
	 * @param id
	 *            the ID string, e.g., "Q1234". The required format depends on
	 *            the specific type of entity
	 * @param siteIri
	 *            IRI to identify the site, usually the first part of the entity
	 *            IRI of the site this belongs to, e.g.,
	 *            "http://www.wikidata.org/entity/"
	 */
	EntityIdValueImpl(String id, String siteIri) {
		Validate.notNull(id, "Entity ids cannot be null");
		Validate.notNull(siteIri, "Entity site IRIs cannot be null");
		this.id = id;
		this.siteIri = siteIri;
	}

	@Override
	public String getIri() {
		return siteIri.concat(id);
	}

	@Override
	public String getId() {
		return id;
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
