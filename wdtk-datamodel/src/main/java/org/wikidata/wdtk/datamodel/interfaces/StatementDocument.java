package org.wikidata.wdtk.datamodel.interfaces;

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

import org.wikidata.wdtk.util.NestedIterator;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Interface for EntityDocuments that can have statements.
 *
 * @author Markus Kroetzsch
 */
public interface StatementDocument extends EntityDocument {

	/**
	 * Return the list of all StatementGroups stored for this item. The order of
	 * StatementGroups is significant.
	 *
	 * @return list of StatementGroups
	 */
	List<StatementGroup> getStatementGroups();

	/**
	 * Returns an iterator that provides access to all statements, without
	 * considering the statement groups. The order of statements is preserved.
	 *
	 * @return iterator over all statements
	 */
	default Iterator<Statement> getAllStatements() {
		return new NestedIterator<>(getStatementGroups());
	}

	/**
	 * Returns the {@link StatementGroup} for the given property, or null if
	 * there are no statements for this property. This is a convenience method
	 * for accessing the data that can be obtained via
	 * {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return {@link StatementGroup} or null
	 */
	default StatementGroup findStatementGroup(PropertyIdValue propertyIdValue) {
		for (StatementGroup sg : getStatementGroups()) {
			if (propertyIdValue.equals(sg.getProperty())) {
				return sg;
			}
		}
		return null;
	}

	/**
	 * Returns the {@link StatementGroup} for the given property, or null if
	 * there are no statements for this property. Only the string id of the
	 * property is compared, not the site id. This is useful in situations where
	 * all data is known to come from a single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return {@link StatementGroup} or null
	 */
	default StatementGroup findStatementGroup(String propertyId) {
		for (StatementGroup sg : getStatementGroups()) {
			if (propertyId.equals(sg.getProperty().getId())) {
				return sg;
			}
		}
		return null;
	}

	/**
	 * Returns true if there is a statement for the given property. This is a
	 * convenience method for accessing the data that can be obtained via
	 * {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return true if a statement for this property exists
	 */
	default boolean hasStatement(PropertyIdValue propertyIdValue) {
		return findStatementGroup(propertyIdValue) != null;
	}

	/**
	 * Returns true if there is a statement for the given property. Only the
	 * string id of the property is compared, not the site id. This is useful in
	 * situations where all data is known to come from a single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return true if a statement for this property exists
	 */
	default boolean hasStatement(String propertyId) {
		return findStatementGroup(propertyId) != null;
	}

	/**
	 * Returns true if there is a statement for the given property and value.
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @param value
	 *            the value to search
	 * @return true if a statement for this property and value exists
	 */
	default boolean hasStatementValue(PropertyIdValue propertyIdValue, Value value) {
		return hasStatementValue(propertyIdValue, Collections.singleton(value));
	}

	/**
	 * Returns true if there is a statement for the given property and value.
	 * Only the string id of the property is compared, not the site id. This is
	 * useful in situations where all data is known to come from a single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @param value
	 *            the value to search
	 * @return true if a statement for this property and value exists
	 */
	default boolean hasStatementValue(String propertyId, Value value) {
		return hasStatementValue(propertyId, Collections.singleton(value));
	}

	/**
	 * Returns true if there is a statement for the given property and one of
	 * the given values. This is a convenience method for accessing the data
	 * that can be obtained via {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @param values
	 *            the set of values to search
	 * @return true if a statement for this property and value exists
	 */
	default boolean hasStatementValue(PropertyIdValue propertyIdValue,
			Set<? extends Value> values) {
		StatementGroup statementGroup = findStatementGroup(propertyIdValue);
		if(statementGroup == null) {
			return false;
		}
		for (Statement statement : statementGroup) {
			if (values.contains(statement.getValue())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if there is a statement for the given property and one of
	 * the given values. Only the string id of the property is compared, not the
	 * site id. This is useful in situations where all data is known to come
	 * from a single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @param values
	 *            the set of values to search
	 * @return true if a statement for this property and value exists
	 */
	default boolean hasStatementValue(String propertyId, Set<? extends Value> values) {
		StatementGroup statementGroup = findStatementGroup(propertyId);
		if(statementGroup == null) {
			return false;
		}
		for (Statement statement : statementGroup) {
			if (values.contains(statement.getValue())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the unique {@link Statement} for the given property, or null if
	 * there are zero or many statements for this property. This is a
	 * convenience method for accessing the data that can be obtained via
	 * {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return {@link Statement} or null
	 */
	default Statement findStatement(PropertyIdValue propertyIdValue) {
		StatementGroup statementGroup = findStatementGroup(propertyIdValue);
		return (statementGroup != null && statementGroup.size() == 1)
				? statementGroup.getStatements().get(0)
				: null;
	}

	/**
	 * Returns the unique {@link Statement} for the given property, or null if
	 * there are zero or many statements for this property. Only the string id
	 * of the property is compared, not the site id. This is useful in
	 * situations where all data is known to come from a single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return {@link Statement} or null
	 */
	default Statement findStatement(String propertyId) {
		StatementGroup statementGroup = findStatementGroup(propertyId);
		return (statementGroup != null && statementGroup.size() == 1)
				? statementGroup.getStatements().get(0)
				: null;
	}

	/**
	 * Returns the unique {@link Value} for the given property, or null if there
	 * are zero or many values given in statements for this property.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return {@link Value} or null
	 */
	default Value findStatementValue(PropertyIdValue propertyIdValue) {
		Statement statement = findStatement(propertyIdValue);
		return (statement != null) ? statement.getValue() : null;
	}

	/**
	 * Returns the unique {@link Value} for the given property, or null if there
	 * are zero or many values given in statements for this property. Only the
	 * string id of the property is compared, not the site id. This is useful in
	 * situations where all data is known to come from a single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return {@link Value} or null
	 */
	default Value findStatementValue(String propertyId) {
		Statement statement = findStatement(propertyId);
		return (statement != null) ? statement.getValue() : null;
	}

	/**
	 * Returns the unique {@link StringValue} for the given property, or null if
	 * there are zero or many such values given in statements for this property.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return {@link StringValue} or null
	 */
	default StringValue findStatementStringValue(PropertyIdValue propertyIdValue) {
		Value value = findStatementValue(propertyIdValue);
		return value instanceof StringValue ? (StringValue) value : null;
	}

	/**
	 * Returns the unique {@link StringValue} for the given property, or null if
	 * there are zero or many such values given in statements for this property.
	 * Only the string id of the property is compared, not the site id. This is
	 * useful in situations where all data is known to come from a single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return {@link StringValue} or null
	 */
	default StringValue findStatementStringValue(String propertyId) {
		Value value = findStatementValue(propertyId);
		return value instanceof StringValue ? (StringValue) value : null;
	}

	/**
	 * Returns the unique {@link QuantityValue} for the given property, or null
	 * if there are zero or many such values given in statements for this
	 * property.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return {@link QuantityValue} or null
	 */
	default QuantityValue findStatementQuantityValue(PropertyIdValue propertyIdValue) {
		Value value = findStatementValue(propertyIdValue);
		return value instanceof QuantityValue ? (QuantityValue) value : null;
	}

	/**
	 * Returns the unique {@link QuantityValue} for the given property, or null
	 * if there are zero or many such values given in statements for this
	 * property. Only the string id of the property is compared, not the site
	 * id. This is useful in situations where all data is known to come from a
	 * single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return {@link QuantityValue} or null
	 */
	default QuantityValue findStatementQuantityValue(String propertyId) {
		Value value = findStatementValue(propertyId);
		return value instanceof QuantityValue ? (QuantityValue) value : null;
	}

	/**
	 * Returns the unique {@link GlobeCoordinatesValue} for the given property,
	 * or null if there are zero or many such values given in statements for
	 * this property.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return {@link GlobeCoordinatesValue} or null
	 */
	default GlobeCoordinatesValue findStatementGlobeCoordinatesValue(
			PropertyIdValue propertyIdValue) {
		Value value = findStatementValue(propertyIdValue);
		return value instanceof GlobeCoordinatesValue ? (GlobeCoordinatesValue) value : null;
	}

	/**
	 * Returns the unique {@link GlobeCoordinatesValue} for the given property,
	 * or null if there are zero or many such values given in statements for
	 * this property. Only the string id of the property is compared, not the
	 * site id. This is useful in situations where all data is known to come
	 * from a single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return {@link GlobeCoordinatesValue} or null
	 */
	default GlobeCoordinatesValue findStatementGlobeCoordinatesValue(String propertyId) {
		Value value = findStatementValue(propertyId);
		return value instanceof GlobeCoordinatesValue ? (GlobeCoordinatesValue) value : null;
	}

	/**
	 * Returns the unique {@link TimeValue} for the given property, or null if
	 * there are zero or many such values given in statements for this property.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return {@link TimeValue} or null
	 */
	default TimeValue findStatementTimeValue(PropertyIdValue propertyIdValue) {
		Value value = findStatementValue(propertyIdValue);
		return value instanceof TimeValue ? (TimeValue) value : null;
	}

	/**
	 * Returns the unique {@link TimeValue} for the given property, or null if
	 * there are zero or many such values given in statements for this property.
	 * Only the string id of the property is compared, not the site id. This is
	 * useful in situations where all data is known to come from a single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return {@link TimeValue} or null
	 */
	default TimeValue findStatementTimeValue(String propertyId) {
		Value value = findStatementValue(propertyId);
		return value instanceof TimeValue ? (TimeValue) value : null;
	}

	/**
	 * Returns the unique {@link MonolingualTextValue} for the given property,
	 * or null if there are zero or many such values given in statements for
	 * this property.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return {@link MonolingualTextValue} or null
	 */
	default MonolingualTextValue findStatementMonolingualTextValue(
			PropertyIdValue propertyIdValue) {
		Value value = findStatementValue(propertyIdValue);
		return value instanceof MonolingualTextValue ? (MonolingualTextValue) value : null;
	}

	/**
	 * Returns the unique {@link MonolingualTextValue} for the given property,
	 * or null if there are zero or many such values given in statements for
	 * this property. Only the string id of the property is compared, not the
	 * site id. This is useful in situations where all data is known to come
	 * from a single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return {@link MonolingualTextValue} or null
	 */
	default MonolingualTextValue findStatementMonolingualTextValue(String propertyId) {
		Value value = findStatementValue(propertyId);
		return value instanceof MonolingualTextValue ? (MonolingualTextValue) value : null;
	}

	/**
	 * Returns the unique {@link ItemIdValue} for the given property, or null if
	 * there are zero or many such values given in statements for this property.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return {@link ItemIdValue} or null
	 */
	default ItemIdValue findStatementItemIdValue(PropertyIdValue propertyIdValue) {
		Value value = findStatementValue(propertyIdValue);
		return value instanceof ItemIdValue ? (ItemIdValue) value : null;
	}

	/**
	 * Returns the unique {@link ItemIdValue} for the given property, or null if
	 * there are zero or many such values given in statements for this property.
	 * Only the string id of the property is compared, not the site id. This is
	 * useful in situations where all data is known to come from a single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return {@link ItemIdValue} or null
	 */
	default ItemIdValue findStatementItemIdValue(String propertyId) {
		Value value = findStatementValue(propertyId);
		return value instanceof ItemIdValue ? (ItemIdValue) value : null;
	}

	/**
	 * Returns the unique {@link PropertyIdValue} for the given property, or
	 * null if there are zero or many such values given in statements for this
	 * property.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return {@link PropertyIdValue} or null
	 */
	default PropertyIdValue findStatementPropertyIdValue(PropertyIdValue propertyIdValue) {
		Value value = findStatementValue(propertyIdValue);
		return value instanceof PropertyIdValue ? (PropertyIdValue) value : null;
	}

	/**
	 * Returns the unique {@link PropertyIdValue} for the given property, or
	 * null if there are zero or many such values given in statements for this
	 * property. Only the string id of the property is compared, not the site
	 * id. This is useful in situations where all data is known to come from a
	 * single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return {@link PropertyIdValue} or null
	 */
	default PropertyIdValue findStatementPropertyIdValue(String propertyId) {
		Value value = findStatementValue(propertyId);
		return value instanceof PropertyIdValue ? (PropertyIdValue) value : null;
	}

	/**
	 * Returns the unique {@link EntityIdValue} for the given property, or null
	 * if there are zero or many such values given in statements for this
	 * property.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return {@link EntityIdValue} or null
	 */
	default EntityIdValue findStatementEntityIdValue(PropertyIdValue propertyIdValue) {
		Value value = findStatementValue(propertyIdValue);
		return value instanceof EntityIdValue ? (EntityIdValue) value : null;
	}

	/**
	 * Returns the unique {@link EntityIdValue} for the given property, or null
	 * if there are zero or many such values given in statements for this
	 * property. Only the string id of the property is compared, not the site
	 * id. This is useful in situations where all data is known to come from a
	 * single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return {@link EntityIdValue} or null
	 */
	default EntityIdValue findStatementEntityIdValue(String propertyId) {
		Value value = findStatementValue(propertyId);
		return value instanceof EntityIdValue ? (EntityIdValue) value : null;
	}

	/**
	 * @deprecated {@link DatatypeIdValue} is not allowed as snak value
	 *
	 * Returns the unique {@link DatatypeIdValue} for the given property, or
	 * null if there are zero or many such values given in statements for this
	 * property.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return {@link DatatypeIdValue} or null
	 */
	@Deprecated
	default DatatypeIdValue findStatementDatatypeIdValue(PropertyIdValue propertyIdValue) {
		Value value = findStatementValue(propertyIdValue);
		return value instanceof DatatypeIdValue ? (DatatypeIdValue) value : null;
	}

	/**
	 * @deprecated {@link DatatypeIdValue} is not allowed as snak value
	 *
	 * Returns the unique {@link DatatypeIdValue} for the given property, or
	 * null if there are zero or many such values given in statements for this
	 * property. Only the string id of the property is compared, not the site
	 * id. This is useful in situations where all data is known to come from a
	 * single site.
	 * <p>
	 * This is a convenience method for accessing the data that can be obtained
	 * via {@link #getStatementGroups()}.
	 *
	 * @param propertyId
	 *            the property to search for
	 * @return {@link DatatypeIdValue} or null
	 */
	@Deprecated
	default DatatypeIdValue findStatementDatatypeIdValue(String propertyId) {
		Value value = findStatementValue(propertyId);
		return value instanceof DatatypeIdValue ? (DatatypeIdValue) value : null;
	}
	
	/**
	 * Returns a copy of this document with an updated revision id.
	 */
	@Override
	StatementDocument withRevisionId(long newRevisionId);
	
	/**
	 * Returns a new version of this document which includes the
	 * statement provided. If the identifier of this statement matches
	 * that of any other statement for the same property, then the
	 * existing statement will be replaced by the new one. Otherwise,
	 * the new statement will be added at the end of the list of statements
	 * in this group.
	 * 
	 * @param statement
	 * 		the statement to add or update in the document
	 */
	StatementDocument withStatement(Statement statement);
	
	/**
	 * Returns a new version of this document where all statements matching
	 * any of the statement ids provided have been removed. These statements
	 * can use different properties.
	 * 
	 * @param statementIds
	 *       the identifiers of the statements to remove
	 */
	StatementDocument withoutStatementIds(Set<String> statementIds);
}
