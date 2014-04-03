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
import org.wikidata.wdtk.datamodel.interfaces.Claim;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;

/**
 * Implementation of {@link Statement}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class StatementImpl implements Statement {

	final Claim claim;
	final List<? extends Reference> references;
	final StatementRank rank;
	final String statementId;

	/**
	 * Constructor.
	 * <p>
	 * The string id is used mainly for communication with a Wikibase site, in
	 * order to refer to statements of that site. When creating new statements
	 * that are not on any site, the empty string can be used.
	 * 
	 * @param claim
	 *            the main claim the Statement refers to
	 * @param references
	 *            the references for the Statement
	 * @param rank
	 *            the rank of the Statement
	 * @param statementId
	 *            the string id of the Statement
	 */
	StatementImpl(Claim claim, List<? extends Reference> references,
			StatementRank rank, String statementId) {
		Validate.notNull(claim, "Statement main claim cannot be null");
		Validate.notNull(references, "Statement references cannot be null");
		Validate.notNull(rank, "Statement ranks cannot be null");
		Validate.notNull(statementId, "Statement ids cannot be null");

		this.claim = claim;
		this.references = references;
		this.rank = rank;
		this.statementId = statementId;
	}

	@Override
	public Claim getClaim() {
		return this.claim;
	}

	@Override
	public StatementRank getRank() {
		return this.rank;
	}

	@Override
	public List<? extends Reference> getReferences() {
		return Collections.unmodifiableList(this.references);
	}

	@Override
	public String getStatementId() {
		return this.statementId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(31, 569).append(this.claim)
				.append(this.rank).append(this.references)
				.append(this.statementId).toHashCode();
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
		if (!(obj instanceof StatementImpl)) {
			return false;
		}

		StatementImpl other = (StatementImpl) obj;

		return this.claim.equals(other.claim) && this.rank == other.rank
				&& this.references.equals(other.references)
				&& this.statementId.equals(other.statementId);
	}

	@Override
	public String toString(){
		return "Statement {id = " + this.statementId 
				+ ", rank = " + this.rank + ", " 
				+ this.references.size() + " references }";
	}
}
