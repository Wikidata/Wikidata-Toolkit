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
	Iterator<Statement> getAllStatements();

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
	StatementGroup findStatementGroup(PropertyIdValue propertyIdValue);

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
	StatementGroup findStatementGroup(String propertyId);

	/**
	 * Returns true if there is a statement for the given property. This is a
	 * convenience method for accessing the data that can be obtained via
	 * {@link #getStatementGroups()}.
	 *
	 * @param propertyIdValue
	 *            the property to search for
	 * @return true if a statement for this property exists
	 */
	boolean hasStatement(PropertyIdValue propertyIdValue);

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
	boolean hasStatement(String propertyId);

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
	boolean hasStatementValue(PropertyIdValue propertyIdValue, Value value);

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
	boolean hasStatementValue(String propertyId, Value value);

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
	boolean hasStatementValue(PropertyIdValue propertyIdValue,
			Set<? extends Value> values);

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
	boolean hasStatementValue(String propertyId, Set<? extends Value> values);

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
	Statement findStatement(PropertyIdValue propertyIdValue);

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
	Statement findStatement(String propertyId);

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
	Value findStatementValue(PropertyIdValue propertyIdValue);

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
	Value findStatementValue(String propertyId);

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
	StringValue findStatementStringValue(PropertyIdValue propertyIdValue);

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
	StringValue findStatementStringValue(String propertyId);

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
	QuantityValue findStatementQuantityValue(PropertyIdValue propertyIdValue);

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
	QuantityValue findStatementQuantityValue(String propertyId);

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
	GlobeCoordinatesValue findStatementGlobeCoordinatesValue(
			PropertyIdValue propertyIdValue);

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
	GlobeCoordinatesValue findStatementGlobeCoordinatesValue(String propertyId);

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
	TimeValue findStatementTimeValue(PropertyIdValue propertyIdValue);

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
	TimeValue findStatementTimeValue(String propertyId);

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
	MonolingualTextValue findStatementMonolingualTextValue(
			PropertyIdValue propertyIdValue);

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
	MonolingualTextValue findStatementMonolingualTextValue(String propertyId);

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
	ItemIdValue findStatementItemIdValue(PropertyIdValue propertyIdValue);

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
	ItemIdValue findStatementItemIdValue(String propertyId);

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
	PropertyIdValue findStatementPropertyIdValue(PropertyIdValue propertyIdValue);

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
	PropertyIdValue findStatementPropertyIdValue(String propertyId);

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
	EntityIdValue findStatementEntityIdValue(PropertyIdValue propertyIdValue);

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
	EntityIdValue findStatementEntityIdValue(String propertyId);

	/**
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
	DatatypeIdValue findStatementDatatypeIdValue(PropertyIdValue propertyIdValue);

	/**
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
	DatatypeIdValue findStatementDatatypeIdValue(String propertyId);
}
