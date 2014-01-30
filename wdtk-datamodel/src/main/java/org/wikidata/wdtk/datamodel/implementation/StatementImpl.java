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

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityId;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

/**
 * Implementation of {@link Statement}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class StatementImpl implements Statement {

	final EntityId subject;
	final Snak mainSnak;
	final List<? extends Snak> qualifiers;
	final List<List<? extends Snak>> references;
	final StatementRank rank;

	/**
	 * Constructor.
	 * 
	 * @param subject
	 *            the subject the Statement refers to
	 * @param mainSnak
	 *            the main Snak of the Statement
	 * @param qualifiers
	 *            the qualifiers of the Statement
	 * @param references
	 *            the references for the Statement
	 * @param rank
	 *            the rank of the Statement
	 */
	StatementImpl(EntityId subject, Snak mainSnak,
			List<? extends Snak> qualifiers,
			List<List<? extends Snak>> references, StatementRank rank) {
		Validate.notNull(subject, "Statement subjects cannot be null");
		Validate.notNull(mainSnak, "Statement main Snaks cannot be null");
		Validate.notNull(qualifiers, "Statement qualifiers cannot be null");
		Validate.notNull(references, "Statement references cannot be null");
		Validate.notNull(rank, "Statement ranks cannot be null");

		this.subject = subject;
		this.mainSnak = mainSnak;
		this.qualifiers = qualifiers;
		this.references = references;
		this.rank = rank;
	}

	@Override
	public EntityId getSubject() {
		return subject;
	}

	@Override
	public Snak getMainSnak() {
		return mainSnak;
	}

	@Override
	public List<? extends Snak> getQualifiers() {
		return Collections.unmodifiableList(qualifiers);
	}

	@Override
	public StatementRank getRank() {
		return rank;
	}

	@Override
	public List<List<? extends Snak>> getReferences() {
		// TODO This still allows inner lists of Snaks to be modified. Do
		// we have to protect against this?
		return Collections.unmodifiableList(references);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(31, 569).append(subject).append(mainSnak)
				.append(rank).append(qualifiers).append(references)
				.toHashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof StatementImpl)) {
			return false;
		}

		StatementImpl other = (StatementImpl) obj;

		if (!subject.equals(other.subject) || !mainSnak.equals(other.mainSnak)
				|| rank != other.rank || !qualifiers.equals(other.qualifiers)
				|| !references.equals(other.references)) {
			return false;
		} else {
			return true;
		}
	}

}
