package org.wikidata.wdtk.datamodel.helpers;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementRank;
import org.wikidata.wdtk.datamodel.interfaces.Value;

public class StatementBuilder extends
		AbstractDataObjectBuilder<StatementBuilder, Statement> {

	private final EntityIdValue subject;
	private final PropertyIdValue mainProperty;
	private Value mainValue = null;
	private boolean noMainValue = false;

	private final HashMap<PropertyIdValue, ArrayList<Snak>> qualifiers = new HashMap<>();
	private String statementId = "";
	private StatementRank rank = StatementRank.NORMAL;
	private final ArrayList<Reference> references = new ArrayList<>();

	/**
	 * Constructor.
	 *
	 * @param subject
	 *            id of the entity that the constructed statement refers to
	 * @param property
	 *            the id of the main property of the constructed statement
	 */
	protected StatementBuilder(EntityIdValue subject, PropertyIdValue property) {
		this.subject = subject;
		this.mainProperty = property;
	}

	/**
	 * Starts the construction of a {@link Statement} with the given subject.
	 *
	 * @param subject
	 *            id of the entity that the constructed statement refers to
	 * @param property
	 *            the id of the main property of the constructed statement
	 * @return builder object to continue construction
	 */
	public static StatementBuilder forSubjectAndProperty(EntityIdValue subject,
			PropertyIdValue property) {
		return new StatementBuilder(subject, property);
	}

	@Override
	public Statement build() {
		prepareBuild();
		return factory.getStatement(subject, getMainSnak(), getQualifierGroups(),
				references, rank, statementId);
	}

	/**
	 * Sets the rank for the constructed statement.
	 *
	 * @param rank
	 *            the rank of the statement
	 * @return builder object to continue construction
	 */
	public StatementBuilder withRank(StatementRank rank) {
		this.rank = rank;
		return getThis();
	}

	/**
	 * Sets the id for the constructed statement.
	 *
	 * @param statementId
	 *            the id of the statement
	 * @return builder object to continue construction
	 */
	public StatementBuilder withId(String statementId) {
		this.statementId = statementId;
		return getThis();
	}

	/**
	 * Sets the main value for the constructed statement.
	 *
	 * @param value
	 *            the main value of the statement
	 * @return builder object to continue construction
	 */
	public StatementBuilder withValue(Value value) {
		this.mainValue = value;
		return getThis();
	}

	/**
	 * Sets the main snak of the statement to be a {{@link SomeValueSnak}.
	 *
	 * @return builder object to continue construction
	 */
	public StatementBuilder withSomeValue() {
		this.mainValue = null;
		this.noMainValue = false;
		return getThis();
	}

	/**
	 * Sets the main snak of the statement to be a {{@link NoValueSnak}.
	 *
	 * @return builder object to continue construction
	 */
	public StatementBuilder withNoValue() {
		this.mainValue = null;
		this.noMainValue = true;
		return getThis();
	}

	/**
	 * Adds a qualifier with the given property and value to the constructed
	 * statement.
	 *
	 * @param propertyIdValue
	 *            the property of the qualifier
	 * @param value
	 *            the value of the qualifier
	 * @return builder object to continue construction
	 */
	public StatementBuilder withQualifierValue(PropertyIdValue propertyIdValue,
			Value value) {
		withQualifier(factory.getValueSnak(propertyIdValue, value));
		return getThis();
	}

	/**
	 * Adds a {@link SomeValueSnak} qualifier with the given property to the
	 * constructed statement.
	 *
	 * @param propertyIdValue
	 *            the property of the qualifier
	 * @return builder object to continue construction
	 */
	public StatementBuilder withQualifierSomeValue(
			PropertyIdValue propertyIdValue) {
		withQualifier(factory.getSomeValueSnak(propertyIdValue));
		return getThis();
	}

	/**
	 * Adds a {@link NoValueSnak} qualifier with the given property to the
	 * constructed statement.
	 * <p>
	 * Note that it might not be meaningful to use {@link NoValueSnak} in a
	 * qualifier. It is usually implicitly assumed that all qualifiers that are
	 * not given have no value for a particular statement. Otherwise one would
	 * need large numbers of {@link NoValueSnak} qualifiers for every statement!
	 *
	 * @param propertyIdValue
	 *            the property of the qualifier
	 * @return builder object to continue construction
	 */
	public StatementBuilder withQualifierNoValue(PropertyIdValue propertyIdValue) {
		withQualifier(factory.getNoValueSnak(propertyIdValue));
		return getThis();
	}

	/**
	 * Adds a qualifier {@link Snak} to the constructed statement.
	 *
	 * @param qualifier
	 *            the qualifier to add
	 * @return builder object to continue construction
	 */
	public StatementBuilder withQualifier(Snak qualifier) {
		getQualifierList(qualifier.getPropertyId()).add(qualifier);
		return getThis();
	}

	/**
	 * Adds all qualifiers from the given {@link SnakGroup} to the constructed
	 * statement.
	 *
	 * @param qualifiers
	 *            the group of qualifiers to add
	 * @return builder object to continue construction
	 */
	public StatementBuilder withQualifiers(SnakGroup qualifiers) {
		getQualifierList(qualifiers.getProperty()).addAll(qualifiers);
		return getThis();
	}

	/**
	 * Adds all qualifiers from the given list of {@link SnakGroup} to the
	 * constructed statement. This is handy to copy all qualifiers from a given
	 * statement.
	 *
	 * @param qualifiers
	 *            the list of groups of qualifiers to add
	 * @return builder object to continue construction
	 */
	public StatementBuilder withQualifiers(List<SnakGroup> qualifiers) {
		for (SnakGroup sg : qualifiers) {
			withQualifiers(sg);
		}
		return getThis();
	}

	/**
	 * Adds a reference to the constructed statement.
	 *
	 * @param reference
	 *            the reference to be added
	 * @return builder object to continue construction
	 */
	public StatementBuilder withReference(Reference reference) {
		this.references.add(reference);
		return getThis();
	}

	/**
	 * Adds a list of references to the constructed statement.
	 *
	 * @param references
	 *            the references to be added
	 * @return builder object to continue construction
	 */
	public StatementBuilder withReferences(List<? extends Reference> references) {
		this.references.addAll(references);
		return getThis();
	}

	@Override
	protected StatementBuilder getThis() {
		return this;
	}

	/**
	 * Returns a list of {@link SnakGroup} objects for the currently stored
	 * qualifiers.
	 *
	 * @return
	 */
	protected List<SnakGroup> getQualifierGroups() {
		ArrayList<SnakGroup> result = new ArrayList<>(this.qualifiers.size());
		for (ArrayList<Snak> statementList : this.qualifiers.values()) {
			result.add(factory.getSnakGroup(statementList));
		}
		return result;
	}

	/**
	 * Returns the list of {@link Snak} objects for a given qualifier property.
	 *
	 * @param propertyIdValue
	 * @return
	 */
	protected ArrayList<Snak> getQualifierList(PropertyIdValue propertyIdValue) {
		return this.qualifiers.computeIfAbsent(propertyIdValue, k -> new ArrayList<>());
	}

	/**
	 * Returns the main {@link Snak} object for the constructed statement.
	 *
	 * @return
	 */
	protected Snak getMainSnak() {
		if (this.mainValue != null) {
			return factory.getValueSnak(this.mainProperty, this.mainValue);
		} else if (this.noMainValue) {
			return factory.getNoValueSnak(this.mainProperty);
		} else {
			return factory.getSomeValueSnak(this.mainProperty);
		}
	}

}
