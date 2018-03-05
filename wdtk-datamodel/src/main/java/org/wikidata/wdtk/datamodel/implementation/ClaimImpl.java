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

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.util.Iterator;
import java.util.List;

/**
 * Helper class to represent a {@link Claim}.
 * This is  a ffacade for a {@link Statement}
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 */
public class ClaimImpl implements Claim {

	private final Statement statement;

	/**
	 * Constructor to create a claim. This internally creates
	 * a new statement, so if you want to create a statement later
	 * on just use {@link StatementImpl} directly.
	 * 
	 * @param subject
	 *            the subject the Claim refers to
	 * @param mainSnak
	 *            the main Snak of the Claim
	 * @param qualifiers
	 *            the qualifiers of the Claim, grouped in SnakGroups
	 */
	public ClaimImpl(
			EntityIdValue subject,
			Snak mainSnak,
			List<SnakGroup> qualifiers) {
		this.statement = new StatementImpl(null, StatementRank.NORMAL, mainSnak, qualifiers, null, subject);
	}
	
	/**
	 * Constructor used to initialize a claim from a JacksonStatement,
	 * should only be used internally.
	 * 
	 * @param statement
	 * 		the statement which contains this claim
	 */
	public ClaimImpl(StatementImpl statement) {
		this.statement = statement;
	}

	@Override
	public EntityIdValue getSubject() {
		return statement.getSubject();
	}

	@Override
	public Snak getMainSnak() {
		return statement.getMainSnak();
	}

	@Override
	public List<SnakGroup> getQualifiers() {
		return statement.getQualifiers();
	}

	@Override
	public Iterator<Snak> getAllQualifiers() {
		return statement.getAllQualifiers();
	}

	@Override
	public Value getValue() {
		return statement.getValue();
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
