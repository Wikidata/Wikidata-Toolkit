package org.wikidata.wdtk.datamodel.implementation;

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

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * Implementation of {@link PropertyDocument}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class PropertyDocumentImpl extends TermedDocumentImpl implements
		PropertyDocument {

	final PropertyIdValue propertyId;
	final DatatypeIdValue datatypeId;

	/**
	 * Constructor.
	 * 
	 * @param propertyId
	 *            the id of the property that data is about
	 * @param labels
	 *            the labels of this property by language code
	 * @param descriptions
	 *            the descriptions of this property by language code
	 * @param aliases
	 *            the alias lists of this property by language code
	 * @param datatypeId
	 *            the datatype of that property
	 */
	PropertyDocumentImpl(PropertyIdValue propertyId,
			Map<String, MonolingualTextValue> labels,
			Map<String, MonolingualTextValue> descriptions,
			Map<String, List<MonolingualTextValue>> aliases,
			DatatypeIdValue datatypeId) {
		super(labels, descriptions, aliases);
		Validate.notNull(propertyId, "property ID cannot be null");
		Validate.notNull(datatypeId, "datatype ID cannot be null");
		this.propertyId = propertyId;
		this.datatypeId = datatypeId;
	}

	@Override
	public EntityIdValue getEntityId() {
		return propertyId;
	}

	@Override
	public PropertyIdValue getPropertyId() {
		return propertyId;
	}

	@Override
	public DatatypeIdValue getDatatype() {
		return datatypeId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + datatypeId.hashCode();
		result = prime * result + propertyId.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof PropertyDocumentImpl)) {
			return false;
		}
		PropertyDocumentImpl other = (PropertyDocumentImpl) obj;
		return datatypeId.equals(other.datatypeId)
				&& propertyId.equals(other.propertyId);
	}

}
