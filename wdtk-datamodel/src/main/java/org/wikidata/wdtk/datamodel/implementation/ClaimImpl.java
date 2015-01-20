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
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.util.NestedIterator;

/**
 * Implementation of {@link Claim}.
 *
 * @author Markus Kroetzsch
 *
 */
public class ClaimImpl implements Claim, Serializable {

	private static final long serialVersionUID = -2991778567647082844L;
	
	final EntityIdValue subject;
	final Snak mainSnak;
	final List<SnakGroup> qualifiers;

	/**
	 * Constructor.
	 *
	 * @param subject
	 *            the subject the Claim refers to
	 * @param mainSnak
	 *            the main Snak of the Claim
	 * @param qualifiers
	 *            the qualifiers of the Claim, groupd in SnakGroups
	 */
	ClaimImpl(EntityIdValue subject, Snak mainSnak, List<SnakGroup> qualifiers) {
		Validate.notNull(subject, "Statement subjects cannot be null");
		Validate.notNull(mainSnak, "Statement main Snaks cannot be null");
		Validate.notNull(qualifiers,
				"Statement qualifier groups cannot be null");

		this.subject = subject;
		this.mainSnak = mainSnak;
		this.qualifiers = qualifiers;
	}

	@Override
	public EntityIdValue getSubject() {
		return subject;
	}

	@Override
	public Snak getMainSnak() {
		return mainSnak;
	}

	@Override
	public List<SnakGroup> getQualifiers() {
		return Collections.unmodifiableList(this.qualifiers);
	}

	@Override
	public Iterator<Snak> getAllQualifiers() {
		return new NestedIterator<>(this.qualifiers);
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
