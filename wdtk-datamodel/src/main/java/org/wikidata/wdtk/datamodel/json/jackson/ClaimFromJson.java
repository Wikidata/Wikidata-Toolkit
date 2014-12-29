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

import java.util.Iterator;
import java.util.List;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.util.NestedIterator;

/**
 * Helper class to represent a {@link Claim} deserialized from JSON. The actual
 * data is part of {@link JacksonStatement}. This is merely a facade that
 * provides a suitable view.
 *
 * @author Fredo Erxleben
 */
public class ClaimFromJson implements Claim {

	private final JacksonStatement statement;

	private List<SnakGroup> qualifiers = null;

	public ClaimFromJson(JacksonStatement statement) {
		this.statement = statement;
	}

	@Override
	public EntityIdValue getSubject() {
		EntityDocument parentDocument = this.statement.getParentDocument();
		if (parentDocument != null) {
			return parentDocument.getEntityId();
		} else {
			return null;
		}
	}

	@Override
	public Snak getMainSnak() {
		return this.statement.getMainsnak();
	}

	@Override
	public List<SnakGroup> getQualifiers() {
		// Note: caching this is not 100% safe since the data is not immutable
		// and we won't know of changes. But when this is called, no further
		// changes should happen.
		if (this.qualifiers == null) {
			this.qualifiers = SnakGroupFromJson.makeSnakGroups(
					this.statement.getQualifiers(),
					this.statement.getPropertyOrder());
		}
		return this.qualifiers;
	}

	@Override
	public Iterator<Snak> getAllQualifiers() {
		return new NestedIterator<>(getQualifiers());
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsClaim(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
}
