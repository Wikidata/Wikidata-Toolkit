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

import java.util.AbstractCollection;
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
 * Helper class to represent a {@link StatementGroup}.
 *
 * @author Markus Kroetzsch
 * @author Antonin Delpeuch
 */
public class StatementGroupImpl extends AbstractCollection<Statement> implements StatementGroup {

	private final List<Statement> statements;

	/**
	 * Constructor.
	 *
	 * @param statements
	 *            a non-empty list of statements that use the same subject and
	 *            main-snak property in their claim
	 */
	public StatementGroupImpl(List<Statement> statements) {
		Validate.notNull(statements,
				"A non-null list of statements must be provided to create a statement group.");
		Validate.isTrue(!statements.isEmpty(),
				"A non-empty list of statements must be provided to create a statement group.");
		EntityIdValue subject = statements.get(0).getSubject();
		PropertyIdValue property = statements.get(0).getMainSnak().getPropertyId();
		for(Statement statement : statements) {
			Validate.isTrue(statement.getSubject().equals(subject),
					"All statements of a statement group must have the same subject.");
			Validate.isTrue(statement.getMainSnak().getPropertyId().equals(property),
			"All statements of a statement group must have the same subject.");
		}
		this.statements = Collections.unmodifiableList(statements);
	}

	@Override
	public Iterator<Statement> iterator() {
		return statements.iterator();
	}

	@Override
	public int size() {
		return statements.size();
	}

	@Override
	public List<Statement> getStatements() {
		return Collections.unmodifiableList(statements);
	}

	@Override
	public PropertyIdValue getProperty() {
		return statements.get(0).getMainSnak().getPropertyId();
	}

	@Override
	public EntityIdValue getSubject() {
		return statements.get(0).getSubject();
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

}
