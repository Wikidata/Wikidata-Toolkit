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
package org.wikidata.wdtk.datamodel.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.implementation.DataObjectFactoryImpl;
import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

/**
 * Builder for incremental construction of {@link StatementUpdate} objects.
 * 
 * @see StatementDocumentUpdateBuilder
 */
public class StatementUpdateBuilder {

	private static DataObjectFactory factory = new DataObjectFactoryImpl();

	private final List<Statement> added = new ArrayList<>();
	private final Map<String, Statement> replaced = new HashMap<>();
	private final Set<String> removed = new HashSet<>();

	/**
	 * Adds statement to the entity. If {@code statement} has an ID (perhaps because
	 * it is a modified copy of another statement), its ID is stripped to ensure the
	 * statement is added and no other statement is modified.
	 * 
	 * @param statement
	 *            new statement to add
	 * @throws NullPointerException
	 *             if {@code statement} is {@code null}
	 */
	public void add(Statement statement) {
		Objects.requireNonNull(statement, "Statement cannot be null.");
		if (!statement.getStatementId().isEmpty()) {
			statement = statement.withStatementId("");
		}
		added.add(statement);
	}

	/**
	 * Replaces existing statement in the entity. Provided {@code statement} must
	 * have statement ID identifying statement to replace. Calling this method
	 * overrides any previous changes made to the same statement ID by this method
	 * or {@link #remove(String)}.
	 * 
	 * @param statement
	 *            replacement for existing statement
	 * @throws NullPointerException
	 *             if {@code statement} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code statement} does not have statement ID
	 */
	public void replace(Statement statement) {
		Objects.requireNonNull(statement, "Statement cannot be null.");
		Validate.notEmpty(statement.getStatementId(), "Statement must have an ID.");
		replaced.put(statement.getStatementId(), statement);
		removed.remove(statement.getStatementId());
	}

	/**
	 * Removes existing statement from the entity. Calling this method overrides any
	 * previous changes made to the same statement ID by
	 * {@link #replace(Statement)}. Removing the same statement ID twice is silently
	 * tolerated.
	 * 
	 * @param statementId
	 *            ID of the removed statement
	 * @throws IllegalArgumentException
	 *             if {@code statementId} is empty
	 */
	public void remove(String statementId) {
		Validate.notEmpty(statementId, "Statement ID must not be empty.");
		removed.add(statementId);
		replaced.remove(statementId);
	}

	public StatementUpdate build() {
		return factory.getStatementUpdate(added, replaced.values(), removed);
	}

}
