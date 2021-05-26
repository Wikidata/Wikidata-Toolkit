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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

/**
 * Builder for incremental construction of {@link StatementUpdate} objects.
 * 
 * @see StatementDocumentUpdateBuilder
 */
public class StatementUpdateBuilder {

	private final Map<String, Statement> base;
	private EntityIdValue subject;
	private final List<Statement> added = new ArrayList<>();
	private final Map<String, Statement> replaced = new HashMap<>();
	private final Set<String> removed = new HashSet<>();

	private StatementUpdateBuilder(EntityIdValue subject, Collection<Statement> base) {
		if (subject != null) {
			Validate.isTrue(!subject.isPlaceholder(), "Subject cannot be a placeholder ID.");
			this.subject = subject;
		}
		if (base != null) {
			for (Statement statement : base) {
				Objects.requireNonNull(statement, "Base document statements cannot be null.");
				Validate.isTrue(
						!statement.getSubject().isPlaceholder(),
						"Statement subject cannot be a placeholder ID.");
				if (this.subject != null) {
					Validate.isTrue(this.subject.equals(statement.getSubject()), "Inconsistent statement subject.");
				} else {
					this.subject = statement.getSubject();
				}
				Validate.notBlank(statement.getStatementId(), "Base document statement must have valid ID.");
			}
			Validate.isTrue(
					base.stream().map(s -> s.getSubject()).distinct().count() <= 1,
					"Base document statements must all refer to the same subject.");
			Validate.isTrue(
					base.stream().map(s -> s.getStatementId()).distinct().count() == base.size(),
					"Base document statements must have unique IDs.");
			this.base = base.stream().collect(toMap(s -> s.getStatementId(), s -> s));
		} else {
			this.base = null;
		}
	}

	/**
	 * Creates new builder object for constructing statement update.
	 * 
	 * @return update builder object
	 */
	public static StatementUpdateBuilder create() {
		return new StatementUpdateBuilder(null, null);
	}

	/**
	 * Creates new builder object for constructing statement update of given
	 * subject. All added or replaced statements must have the same subject ID.
	 * 
	 * @param subject
	 *            statement subject or {@code null} for unspecified ID
	 * @return update builder object
	 * @throws IllegalArgumentException
	 *             if subject is a placeholder ID
	 */
	public static StatementUpdateBuilder create(EntityIdValue subject) {
		return new StatementUpdateBuilder(subject, null);
	}

	/**
	 * Creates new builder object for constructing update of given base revision
	 * statements. Provided statements will be used to check correctness of changes.
	 * <p>
	 * Since all changes will be checked after the {@link StatementUpdate} is passed
	 * to {@link EntityDocumentBuilder} anyway, it is usually unnecessary to use
	 * this method. It is simpler to initialize the builder with {@link #create()}.
	 * 
	 * @param statements
	 *            statements from base revision of the document
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code statements} or any of its items is {@code null}
	 * @throws IllegalArgumentException
	 *             if any statement is missing statement ID or statement subjects
	 *             are inconsistent or placeholders
	 */
	public static StatementUpdateBuilder forStatements(Collection<Statement> statements) {
		Objects.requireNonNull(statements, "Base document statement collection cannot be null.");
		return new StatementUpdateBuilder(null, statements);
	}

	/**
	 * Creates new builder object for constructing update of given base revision
	 * statements with given subject. Provided statements will be used to check
	 * correctness of changes. All provided statements as well as added or replaced
	 * statements must have the provided subject ID.
	 * <p>
	 * Since all changes will be checked after the {@link StatementUpdate} is passed
	 * to {@link EntityDocumentBuilder} anyway, it is usually unnecessary to use
	 * this method. It is simpler to initialize the builder with {@link #create()}.
	 * 
	 * @param subject
	 *            statement subject or {@code null} for unspecified ID
	 * @param statements
	 *            statements from base revision of the document
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code statements} or any of its items is {@code null}
	 * @throws IllegalArgumentException
	 *             if any statement is missing statement ID or statement subjects
	 *             are inconsistent or placeholders
	 */
	public static StatementUpdateBuilder forStatements(EntityIdValue subject, Collection<Statement> statements) {
		Objects.requireNonNull(statements, "Base document statement collection cannot be null.");
		return new StatementUpdateBuilder(subject, statements);
	}

	/**
	 * Creates new builder object for constructing update of given base revision
	 * statement groups. Provided statements will be used to check correctness of
	 * changes.
	 * <p>
	 * Since all changes will be checked after the {@link StatementUpdate} is passed
	 * to {@link EntityDocumentBuilder} anyway, it is usually unnecessary to use
	 * this method. It is simpler to initialize the builder with {@link #create()}.
	 * 
	 * @param groups
	 *            statement groups from base revision of the document
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code groups} is {@code null}
	 * @throws IllegalArgumentException
	 *             if any group is {@code null} or any statement is missing
	 *             statement ID or statement subjects are inconsistent or
	 *             placeholders
	 */
	public static StatementUpdateBuilder forStatementGroups(Collection<StatementGroup> groups) {
		Objects.requireNonNull(groups, "Base document statement group collection cannot be null.");
		Validate.noNullElements(groups, "Base document statement groups cannot be null.");
		return new StatementUpdateBuilder(null,
				groups.stream().flatMap(g -> g.getStatements().stream()).collect(toList()));
	}

	/**
	 * Creates new builder object for constructing update of given base revision
	 * statement groups with given subject. Provided statements will be used to
	 * check correctness of changes. All provided statements as well as added or
	 * replaced statements must have the provided subject ID.
	 * <p>
	 * Since all changes will be checked after the {@link StatementUpdate} is passed
	 * to {@link EntityDocumentBuilder} anyway, it is usually unnecessary to use
	 * this method. It is simpler to initialize the builder with {@link #create()}.
	 * 
	 * @param subject
	 *            statement subject or {@code null} for unspecified ID
	 * @param groups
	 *            statement groups from base revision of the document
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code groups} is {@code null}
	 * @throws IllegalArgumentException
	 *             if any group is {@code null} or any statement is missing
	 *             statement ID or statement subjects are inconsistent or
	 *             placeholders
	 */
	public static StatementUpdateBuilder forStatementGroups(EntityIdValue subject, Collection<StatementGroup> groups) {
		Objects.requireNonNull(groups, "Base document statement group collection cannot be null.");
		Validate.noNullElements(groups, "Base document statement groups cannot be null.");
		return new StatementUpdateBuilder(subject,
				groups.stream().flatMap(g -> g.getStatements().stream()).collect(toList()));
	}

	/**
	 * Adds statement to the entity. If {@code statement} has an ID (perhaps because
	 * it is a modified copy of another statement), its ID is stripped to ensure the
	 * statement is added and no other statement is modified.
	 * 
	 * @param statement
	 *            new statement to add
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code statement} is {@code null}
	 * @throws IllegalArgumentException
	 *             if statement's subject is inconsistent with other statements or
	 *             it is a placeholder ID
	 */
	public StatementUpdateBuilder add(Statement statement) {
		Objects.requireNonNull(statement, "Statement cannot be null.");
		Validate.isTrue(
				!statement.getSubject().isPlaceholder(),
				"Statement subject cannot be a placeholder ID.");
		if (subject != null) {
			Validate.isTrue(subject.equals(statement.getSubject()), "Inconsistent statement subject.");
		}
		if (!statement.getStatementId().isEmpty()) {
			statement = statement.withStatementId("");
		}
		added.add(statement);
		if (subject == null) {
			subject = statement.getSubject();
		}
		return this;
	}

	/**
	 * Replaces existing statement in the entity. Provided {@code statement} must
	 * have statement ID identifying statement to replace. Calling this method
	 * overrides any previous changes made to the same statement ID by this method
	 * or {@link #remove(String)}.
	 * <p>
	 * If base revision statements were provided, existence of the statement is
	 * checked. Any attempt to replace some statement with identical statement is
	 * silently ignored, resulting in empty update.
	 * 
	 * @param statement
	 *            replacement for existing statement
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code statement} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code statement} does not have statement ID or it is not
	 *             among base revision statements (if available) or its subject is
	 *             inconsistent with other statements or a placeholder ID
	 */
	public StatementUpdateBuilder replace(Statement statement) {
		Objects.requireNonNull(statement, "Statement cannot be null.");
		Validate.isTrue(
				!statement.getSubject().isPlaceholder(),
				"Statement subject cannot be a placeholder ID.");
		Validate.notEmpty(statement.getStatementId(), "Statement must have an ID.");
		if (subject != null) {
			Validate.isTrue(subject.equals(statement.getSubject()), "Inconsistent statement subject.");
		}
		if (base != null) {
			Statement original = base.get(statement.getStatementId());
			Validate.isTrue(original != null, "Replaced statement is not in base revision.");
			if (statement.equals(original)) {
				replaced.remove(statement.getStatementId());
				removed.remove(statement.getStatementId());
				return this;
			}
		}
		replaced.put(statement.getStatementId(), statement);
		removed.remove(statement.getStatementId());
		if (subject == null) {
			subject = statement.getSubject();
		}
		return this;
	}

	/**
	 * Removes existing statement from the entity. Calling this method overrides any
	 * previous changes made to the same statement ID by
	 * {@link #replace(Statement)}. Removing the same statement ID twice is silently
	 * tolerated.
	 * <p>
	 * If base revision statements were provided, this method checks that statement
	 * with this ID exists in the base revision.
	 * 
	 * @param statementId
	 *            ID of the removed statement
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code statementId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code statementId} is empty or it is not among base revision
	 *             statements (if available)
	 */
	public StatementUpdateBuilder remove(String statementId) {
		Validate.notBlank(statementId, "Statement ID must not be empty.");
		if (base != null) {
			Statement original = base.get(statementId);
			Validate.isTrue(original != null, "Removed statement is not in base revision.");
		}
		removed.add(statementId);
		replaced.remove(statementId);
		return this;
	}

	/**
	 * Replays all changes in provided update into this builder object. Changes are
	 * performed as if by calling {@link #add(Statement)},
	 * {@link #replace(Statement)}, and {@link #remove(String)} methods.
	 * 
	 * @param update
	 *            statement update to replay
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if updated or removed statement is not among base revision
	 *             statements (if available)
	 */
	public StatementUpdateBuilder append(StatementUpdate update) {
		Objects.requireNonNull(update, "Statement update cannot be null.");
		for (Statement statement : update.getAdded()) {
			add(statement);
		}
		for (Statement statement : update.getReplaced().values()) {
			replace(statement);
		}
		for (String statementId : update.getRemoved()) {
			remove(statementId);
		}
		return this;
	}

	/**
	 * Creates new {@link StatementUpdate} object with contents of this builder
	 * object.
	 * 
	 * @return constructed object
	 */
	public StatementUpdate build() {
		return Datamodel.makeStatementUpdate(added, replaced.values(), removed);
	}

}
