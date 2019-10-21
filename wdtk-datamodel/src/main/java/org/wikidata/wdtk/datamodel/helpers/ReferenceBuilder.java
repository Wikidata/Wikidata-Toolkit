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

import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Reference;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.Value;

public class ReferenceBuilder extends
		AbstractDataObjectBuilder<ReferenceBuilder, Reference> {

	final HashMap<PropertyIdValue, ArrayList<Snak>> snaks = new HashMap<>();

	/**
	 * Constructor.
	 */
	protected ReferenceBuilder() {
	}

	/**
	 * Starts the construction of a {@link Reference}.
	 *
	 * @return builder object to continue construction
	 */
	public static ReferenceBuilder newInstance() {
		return new ReferenceBuilder();
	}

	@Override
	public Reference build() {
		prepareBuild();
		return factory.getReference(getSnakGroups());
	}

	/**
	 * Adds the given property and value to the constructed reference.
	 *
	 * @param propertyIdValue
	 *            the property to add
	 * @param value
	 *            the value to add
	 * @return builder object to continue construction
	 */
	public ReferenceBuilder withPropertyValue(PropertyIdValue propertyIdValue,
			Value value) {
		getSnakList(propertyIdValue).add(
				factory.getValueSnak(propertyIdValue, value));
		return getThis();
	}

	/**
	 * Adds a {@link SomeValueSnak} with the given property to the constructed
	 * reference.
	 * <p>
	 * Note that it might not be meaningful to use {@link SomeValueSnak} in a
	 * reference, depending on the policies of the wiki.
	 *
	 * @param propertyIdValue
	 *            the property of the snak
	 * @return builder object to continue construction
	 */
	public ReferenceBuilder withSomeValue(PropertyIdValue propertyIdValue) {
		getSnakList(propertyIdValue).add(
				factory.getSomeValueSnak(propertyIdValue));
		return getThis();
	}

	/**
	 * Adds a {@link NoValueSnak} with the given property to the constructed
	 * reference.
	 * <p>
	 * Note that it might not be meaningful to use {@link NoValueSnak} in a
	 * reference. It is usually implicitly assumed that all snaks that are not
	 * given have no value for a particular reference. Otherwise one would need
	 * large numbers of {@link NoValueSnak} entries for every reference!
	 *
	 * @param propertyIdValue
	 *            the property of the snak
	 * @return builder object to continue construction
	 */
	public ReferenceBuilder withNoValue(PropertyIdValue propertyIdValue) {
		getSnakList(propertyIdValue).add(
				factory.getNoValueSnak(propertyIdValue));
		return getThis();
	}

	@Override
	protected ReferenceBuilder getThis() {
		return this;
	}

	/**
	 * Returns a list of {@link SnakGroup} objects for the currently stored
	 * snaks.
	 *
	 * @return
	 */
	protected List<SnakGroup> getSnakGroups() {
		ArrayList<SnakGroup> result = new ArrayList<>(this.snaks.size());
		for (ArrayList<Snak> statementList : this.snaks.values()) {
			result.add(factory.getSnakGroup(statementList));
		}
		return result;
	}

	/**
	 * Returns the list of {@link Snak} objects for a given property.
	 *
	 * @param propertyIdValue
	 * @return
	 */
	protected ArrayList<Snak> getSnakList(PropertyIdValue propertyIdValue) {
		return this.snaks.computeIfAbsent(propertyIdValue, k -> new ArrayList<>());
	}

}
