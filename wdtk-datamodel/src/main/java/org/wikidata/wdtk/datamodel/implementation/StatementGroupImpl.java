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
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;

/**
 * Implementation of {@link StatementGroup}.
 *
 * @author Markus Kroetzsch
 *
 */
public class StatementGroupImpl implements StatementGroup, Serializable {

	private static final long serialVersionUID = -7455251135036540828L;
	
	final List<Statement> statements;

	/**
	 * Constructor.
	 *
	 * @param statements
	 *            a non-empty list of statements that use the same subject and
	 *            main-snak property in their claim
	 */
	public StatementGroupImpl(List<Statement> statements) {
		Validate.notNull(statements, "List of statements cannot be null");
		Validate.notEmpty(statements, "List of statements cannot be empty");

		EntityIdValue subject = statements.get(0).getClaim().getSubject();
		PropertyIdValue property = statements.get(0).getClaim().getMainSnak()
				.getPropertyId();

		for (Statement s : statements) {
			if (!subject.equals(s.getClaim().getSubject())) {
				throw new IllegalArgumentException(
						"All statements in a statement group must use the same subject");
			}
			if (!property.equals(s.getClaim().getMainSnak().getPropertyId())) {
				throw new IllegalArgumentException(
						"All statements in a statement group must use the same main property");
			}
		}

		this.statements = statements;

	}

	@Override
	public List<Statement> getStatements() {
		return Collections.unmodifiableList(this.statements);
	}

	@Override
	public PropertyIdValue getProperty() {
		return this.statements.get(0).getClaim().getMainSnak().getPropertyId();
	}

	@Override
	public EntityIdValue getSubject() {
		return this.statements.get(0).getClaim().getSubject();
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsStatementGroup(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

	@Override
	public Iterator<Statement> iterator() {
		return this.statements.iterator();
	}
}
