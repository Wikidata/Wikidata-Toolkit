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

import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * Builder class to construct {@link PropertyDocument} objects.
 *
 * @author Markus Kroetzsch
 *
 */
public class PropertyDocumentBuilder extends
		EntityDocumentBuilder<PropertyDocumentBuilder, PropertyDocument> {

	private final DatatypeIdValue datatype;

	/**
	 * Constructor when building the property document from scratch.
	 *
	 * @param propertyIdValue
	 *            id of the newly constructed property document
	 * @param datatype
	 *            the datatype of the newly constructed property document
	 */
	protected PropertyDocumentBuilder(PropertyIdValue propertyIdValue,
			DatatypeIdValue datatype) {
		super(propertyIdValue);
		this.datatype = datatype;
	}
	
	/**
	 * Constructor when building the property document from an existing one.
	 * 
	 * @param initialDocument
	 * 			the initial property document to start the build from
	 */
	protected PropertyDocumentBuilder(PropertyDocument initialDocument) {
		super(initialDocument);
		this.datatype = initialDocument.getDatatype();
	}

	/**
	 * Starts the construction of an {@link PropertyDocument} with the given id.
	 *
	 * @param propertyIdValue
	 *            id of the newly constructed property document
	 * @param datatype
	 *            the datatype of the newly constructed property document
	 * @return builder object to continue construction
	 */
	public static PropertyDocumentBuilder forPropertyIdAndDatatype(
			PropertyIdValue propertyIdValue, DatatypeIdValue datatype) {
		return new PropertyDocumentBuilder(propertyIdValue, datatype);
	}
	
	/**
	 * Starts the construction of an {@link PropertyDocument} from the existing
	 * document.
	 * 
	 * @param initialDocument
	 *           the existing document to start the build from
	 * @return builder object to continue construction
	 */
	public static PropertyDocumentBuilder fromPropertyDocument(
			PropertyDocument initialDocument) {
		return new PropertyDocumentBuilder(initialDocument);
	}

	/**
	 * Starts the construction of an {@link PropertyDocument} with the given id.
	 *
	 * @param propertyIdValue
	 *            id of the newly constructed property document
	 * @param datatypeId
	 *            the datatype id of the newly constructed property document,
	 *            e.g., {@link DatatypeIdValue#DT_ITEM}.
	 * @return builder object to continue construction
	 */
	public static PropertyDocumentBuilder forPropertyIdAndDatatype(
			PropertyIdValue propertyIdValue, String datatypeId) {
		return forPropertyIdAndDatatype(propertyIdValue,
				factory.getDatatypeIdValue(datatypeId));
	}
	
	/**
	 * Changes the entity value id for the constructed document.
	 * See {@link EntityDocument#getEntityId()}.
	 * 
	 * @param entityId
	 *          the entity id, which must be an ItemIdValue
	 * @return builder object to continue construction
	 */
	@Override
	public PropertyDocumentBuilder withEntityId(EntityIdValue entityId) {
		if (!(entityId instanceof PropertyIdValue)) {
			throw new IllegalArgumentException("The entity id of a PropertyDocument must be an PropertyIdValue.");
		}
		return super.withEntityId(entityId);
	}

	/**
	 * Returns the {@link ItemDocument} that has been built.
	 *
	 * @return constructed item document
	 * @throws IllegalStateException
	 *             if the object was built already
	 */
	@Override
	public PropertyDocument build() {
		prepareBuild();
		return factory.getPropertyDocument(
				(PropertyIdValue) this.entityIdValue, this.labels,
				this.descriptions, this.aliases, getStatementGroups(),
				this.datatype, this.revisionId);
	}

	@Override
	protected PropertyDocumentBuilder getThis() {
		return this;
	}
}
