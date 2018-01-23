package org.wikidata.wdtk.datamodel.helpers;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2018 Wikidata Toolkit Developers
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

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;

public abstract class EntityDocumentBuilder<T extends EntityDocumentBuilder<T, O>, O extends EntityDocument>
extends AbstractDataObjectBuilder<T, O>  {
	
	final EntityIdValue entityIdValue;
	long revisionId = 0;

	
	/**
	 * Starts constructing an EntityDocument for the given entity id.
	 * 
	 * @param entityIdValue
	 * 			the id of the document to build
	 */
	protected EntityDocumentBuilder(EntityIdValue entityIdValue) {
		this.entityIdValue = entityIdValue;
	}
	
	/**
	 * Starts constructing an EntityDocument from an initial version
	 * of this document.
	 * 
	 * @param initialDocument
	 * 			the initial version of the document to use
	 */
	protected EntityDocumentBuilder(O initialDocument) {
		this.entityIdValue = initialDocument.getEntityId();
		this.revisionId = initialDocument.getRevisionId();
	}
	
	/**
	 * Sets the revision id for the constructed document. See
	 * {@link EntityDocument#getRevisionId()}.
	 *
	 * @param revisionId
	 *            the revision id
	 * @return builder object to continue construction
	 */
	public T withRevisionId(long revisionId) {
		this.revisionId = revisionId;
		return getThis();
	}
}
