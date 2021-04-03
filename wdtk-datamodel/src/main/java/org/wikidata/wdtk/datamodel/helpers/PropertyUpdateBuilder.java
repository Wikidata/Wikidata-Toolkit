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

import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

/**
 * Builder for incremental construction of {@link PropertyUpdate} objects.
 */
public class PropertyUpdateBuilder extends TermedStatementDocumentUpdateBuilder {

	/**
	 * Initializes new builder object for constructing update of property entity
	 * with given ID.
	 * 
	 * @param propertyId
	 *            ID of the property entity that is to be updated
	 * @throws NullPointerException
	 *             if {@code propertyId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code propertyId} is not a valid ID
	 */
	private PropertyUpdateBuilder(PropertyIdValue propertyId) {
		super(propertyId);
	}

	/**
	 * Initializes new builder object for constructing update of given base property
	 * entity revision.
	 * 
	 * @param revision
	 *            base property entity revision to be updated
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} does not have valid ID
	 */
	private PropertyUpdateBuilder(PropertyDocument revision) {
		super(revision);
	}

	/**
	 * Creates new builder object for constructing update of property entity with
	 * given ID.
	 * 
	 * @param propertyId
	 *            ID of the property entity that is to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code propertyId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code propertyId} is not valid
	 */
	public static PropertyUpdateBuilder forEntityId(PropertyIdValue propertyId) {
		return new PropertyUpdateBuilder(propertyId);
	}

	/**
	 * Creates new builder object for constructing update of given base property
	 * entity revision. Provided property document might not represent the latest
	 * revision of the property entity as currently stored in Wikibase. It will be
	 * used for validation in builder methods. If the document has revision ID, it
	 * will be used to detect edit conflicts.
	 * 
	 * @param revision
	 *            base property entity revision to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} does not have valid ID
	 */
	public static PropertyUpdateBuilder forBaseRevision(PropertyDocument revision) {
		return new PropertyUpdateBuilder(revision);
	}

	@Override
	PropertyIdValue getEntityId() {
		return (PropertyIdValue) super.getEntityId();
	}

	@Override
	PropertyDocument getBaseRevision() {
		return (PropertyDocument) super.getBaseRevision();
	}

	@Override
	public PropertyUpdateBuilder updateStatements(StatementUpdate update) {
		super.updateStatements(update);
		return this;
	}

	@Override
	public PropertyUpdateBuilder updateLabels(TermUpdate update) {
		super.updateLabels(update);
		return this;
	}

	@Override
	public PropertyUpdateBuilder updateDescriptions(TermUpdate update) {
		super.updateDescriptions(update);
		return this;
	}

	@Override
	public PropertyUpdateBuilder setAliases(String language, List<String> aliases) {
		super.setAliases(language, aliases);
		return this;
	}

	@Override
	public PropertyUpdate build() {
		return factory.getPropertyUpdate(getEntityId(), getBaseRevision(), labels, descriptions, aliases, statements);
	}

}