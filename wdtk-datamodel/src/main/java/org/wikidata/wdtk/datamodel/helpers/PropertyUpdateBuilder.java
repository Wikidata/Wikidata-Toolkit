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

import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyUpdate;

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
	 * Initializes new builder object for constructing update of given property
	 * entity revision.
	 * 
	 * @param document
	 *            property entity revision to be updated
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} does not have valid ID
	 */
	private PropertyUpdateBuilder(PropertyDocument document) {
		super(document);
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
	public static PropertyUpdateBuilder forPropertyId(PropertyIdValue propertyId) {
		return new PropertyUpdateBuilder(propertyId);
	}

	/**
	 * Creates new builder object for constructing update of given property entity
	 * revision. Provided property document might not represent the latest revision
	 * of the property entity as currently stored in Wikibase. It will be used for
	 * validation in builder methods. If the document has revision ID, it will be
	 * used to detect edit conflicts.
	 * 
	 * @param document
	 *            property entity revision to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} does not have valid ID
	 */
	public static PropertyUpdateBuilder forPropertyDocument(PropertyDocument document) {
		return new PropertyUpdateBuilder(document);
	}

	@Override
	protected PropertyIdValue getEntityId() {
		return (PropertyIdValue) super.getEntityId();
	}

	@Override
	protected PropertyDocument getCurrentDocument() {
		return (PropertyDocument) super.getCurrentDocument();
	}

	@Override
	public PropertyUpdate build() {
		return factory.getPropertyUpdate(getEntityId(), getCurrentDocument(), getLabels(), getDescriptions(),
				getAddedStatements(), getReplacedStatements(), getRemovedStatements());
	}

}
