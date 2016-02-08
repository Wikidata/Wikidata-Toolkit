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

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.util.NestedIterator;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract base class for implementations of {@link TermedDocument} and
 * {@link StatementDocument}. It does not store any data, but it implements the
 * required find methods that access the data. Specific implementations can
 * overwrite some find methods with more efficient solutions based on internal
 * data structures.
 *
 * @author Markus Kroetzsch
 *
 */
public abstract class AbstractTermedStatementDocument implements
		TermedDocument, StatementDocument {

	@Override
	public String findLabel(String languageCode) {
		MonolingualTextValue mtv = this.getLabels().get(languageCode);
		if (mtv != null) {
			return mtv.getText();
		} else {
			return null;
		}
	}

	@Override
	public String findDescription(String languageCode) {
		MonolingualTextValue mtv = this.getDescriptions().get(languageCode);
		if (mtv != null) {
			return mtv.getText();
		} else {
			return null;
		}
	}

	@Override
	@JsonIgnore
	public Iterator<Statement> getAllStatements() {
		return new NestedIterator<>(getStatementGroups());
	}

	@Override
	public StatementGroup findStatementGroup(PropertyIdValue propertyIdValue) {
		for (StatementGroup sg : getStatementGroups()) {
			if (propertyIdValue.equals(sg.getProperty())) {
				return sg;
			}
		}
		return null;
	}

	@Override
	public StatementGroup findStatementGroup(String propertyId) {
		for (StatementGroup sg : getStatementGroups()) {
			if (propertyId.equals(sg.getProperty().getId())) {
				return sg;
			}
		}
		return null;
	}

	@Override
	public boolean hasStatement(PropertyIdValue propertyIdValue) {
		return findStatementGroup(propertyIdValue) != null;
	}

	@Override
	public boolean hasStatement(String propertyId) {
		return findStatementGroup(propertyId) != null;
	}

	@Override
	public boolean hasStatementValue(PropertyIdValue propertyIdValue,
			Value value) {
		return hasStatementValue(propertyIdValue, Collections.singleton(value));
	}

	@Override
	public boolean hasStatementValue(String propertyId, Value value) {
		return hasStatementValue(propertyId, Collections.singleton(value));
	}

	@Override
	public boolean hasStatementValue(PropertyIdValue propertyIdValue,
			Set<? extends Value> values) {
		return containsStatementValue(findStatementGroup(propertyIdValue),
				values);
	}

	@Override
	public boolean hasStatementValue(String propertyId,
			Set<? extends Value> values) {
		return containsStatementValue(findStatementGroup(propertyId), values);
	}

	@Override
	public Statement findStatement(PropertyIdValue propertyIdValue) {
		return getUniqueStatementFromStatementGroup(findStatementGroup(propertyIdValue));
	}

	@Override
	public Statement findStatement(String propertyId) {
		return getUniqueStatementFromStatementGroup(findStatementGroup(propertyId));
	}

	@Override
	public Value findStatementValue(PropertyIdValue propertyIdValue) {
		return getValueFromStatement(findStatement(propertyIdValue));
	}

	@Override
	public Value findStatementValue(String propertyId) {
		return getValueFromStatement(findStatement(propertyId));
	}

	@Override
	public StringValue findStatementStringValue(PropertyIdValue propertyIdValue) {
		return castValue(findStatementValue(propertyIdValue), StringValue.class);
	}

	@Override
	public StringValue findStatementStringValue(String propertyId) {
		return castValue(findStatementValue(propertyId), StringValue.class);
	}

	@Override
	public QuantityValue findStatementQuantityValue(
			PropertyIdValue propertyIdValue) {
		return castValue(findStatementValue(propertyIdValue),
				QuantityValue.class);
	}

	@Override
	public QuantityValue findStatementQuantityValue(String propertyId) {
		return castValue(findStatementValue(propertyId), QuantityValue.class);
	}

	@Override
	public TimeValue findStatementTimeValue(PropertyIdValue propertyIdValue) {
		return castValue(findStatementValue(propertyIdValue), TimeValue.class);
	}

	@Override
	public TimeValue findStatementTimeValue(String propertyId) {
		return castValue(findStatementValue(propertyId), TimeValue.class);
	}

	@Override
	public MonolingualTextValue findStatementMonolingualTextValue(
			PropertyIdValue propertyIdValue) {
		return castValue(findStatementValue(propertyIdValue),
				MonolingualTextValue.class);
	}

	@Override
	public MonolingualTextValue findStatementMonolingualTextValue(
			String propertyId) {
		return castValue(findStatementValue(propertyId),
				MonolingualTextValue.class);
	}

	@Override
	public ItemIdValue findStatementItemIdValue(PropertyIdValue propertyIdValue) {
		return castValue(findStatementValue(propertyIdValue), ItemIdValue.class);
	}

	@Override
	public ItemIdValue findStatementItemIdValue(String propertyId) {
		return castValue(findStatementValue(propertyId), ItemIdValue.class);
	}

	@Override
	public PropertyIdValue findStatementPropertyIdValue(
			PropertyIdValue propertyIdValue) {
		return castValue(findStatementValue(propertyIdValue),
				PropertyIdValue.class);
	}

	@Override
	public PropertyIdValue findStatementPropertyIdValue(String propertyId) {
		return castValue(findStatementValue(propertyId), PropertyIdValue.class);
	}

	@Override
	public EntityIdValue findStatementEntityIdValue(
			PropertyIdValue propertyIdValue) {
		return castValue(findStatementValue(propertyIdValue),
				EntityIdValue.class);
	}

	@Override
	public EntityIdValue findStatementEntityIdValue(String propertyId) {
		return castValue(findStatementValue(propertyId), EntityIdValue.class);
	}

	@Override
	public DatatypeIdValue findStatementDatatypeIdValue(
			PropertyIdValue propertyIdValue) {
		return castValue(findStatementValue(propertyIdValue),
				DatatypeIdValue.class);
	}

	@Override
	public DatatypeIdValue findStatementDatatypeIdValue(String propertyId) {
		return castValue(findStatementValue(propertyId), DatatypeIdValue.class);
	}

	@Override
	public GlobeCoordinatesValue findStatementGlobeCoordinatesValue(
			PropertyIdValue propertyIdValue) {
		return castValue(findStatementValue(propertyIdValue),
				GlobeCoordinatesValue.class);
	}

	@Override
	public GlobeCoordinatesValue findStatementGlobeCoordinatesValue(
			String propertyId) {
		return castValue(findStatementValue(propertyId),
				GlobeCoordinatesValue.class);
	}

	/**
	 * Returns true if the given statement group contains a statement with one
	 * of the given values.
	 *
	 * @param statementGroup
	 *            the group of statements to search
	 * @param values
	 *            set of acceptable values
	 * @return true if found
	 */
	protected boolean containsStatementValue(StatementGroup statementGroup,
			Set<? extends Value> values) {
		if (statementGroup == null) {
			return false;
		}
		for (Statement statement : statementGroup.getStatements()) {
			if (!(statement.getClaim().getMainSnak() instanceof ValueSnak)) {
				continue;
			}
			ValueSnak valueSnak = (ValueSnak) statement.getClaim()
					.getMainSnak();
			if (values.contains(valueSnak.getValue())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the unique statement in a statement group, or null if there are
	 * zero or more statements. The given statement group can be null; then null
	 * will be returned, too.
	 *
	 * @param statementGroup
	 *            the statement group or null
	 * @return the unique statement or null
	 */
	protected Statement getUniqueStatementFromStatementGroup(
			StatementGroup statementGroup) {
		if (statementGroup == null) {
			return null;
		}
		if (statementGroup.getStatements().size() != 1) {
			return null;
		}
		return statementGroup.getStatements().get(0);
	}

	/**
	 * Returns the {@link Value} of the given {@link Statement}, provided that
	 * the statements actually has a value. Otherwise null is returned.
	 *
	 * @param statement
	 *            the statement to process
	 * @return the value, or null if there is none
	 */
	protected Value getValueFromStatement(Statement statement) {
		if (statement == null) {
			return null;
		}
		return statement.getClaim().getMainSnak().getValue();
	}

	/**
	 * Casts the given value to the given class if possible, or returns null if
	 * not.
	 *
	 * @param value
	 *            the {@link Value} to cast
	 * @param valueClass
	 *            the class to cast to
	 * @return casted value or null
	 */
	protected <T extends Value> T castValue(Value value, Class<T> valueClass) {
		if (valueClass.isInstance(value)) {
			return valueClass.cast(value);
		} else {
			return null;
		}
	}

}
