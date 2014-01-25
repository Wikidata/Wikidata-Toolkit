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

import org.wikidata.wdtk.datamodel.interfaces.EntityId;

/**
 * Generic implementation of {@link EntityId} that works with arbitrary Wikibase
 * instances: it requires a baseIri that identifies the site globally.
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
public abstract class EntityIdImpl implements EntityId {

	final String id;
	final String baseIri;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            the ID string, e.g., "Q1234"
	 * @param baseIri
	 *            the first part of the IRI of the site this belongs to, e.g.,
	 *            "http://www.wikidata.org/entity/"
	 */
	public EntityIdImpl(String id, String baseIri) {
		this.id = id;
		this.baseIri = baseIri;
	}

	@Override
	public String getIri() {
		return baseIri.concat(id);
	}

	@Override
	public String getId() {
		return id;
	}

	public int hashCode() {
		// TODO not the most efficient approach; better use some hash builder
		return this.getIri().hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof EntityId))
			return false;

		return this.getIri().equals(((EntityId) obj).getIri());
	}

}
