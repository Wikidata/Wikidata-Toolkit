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
package org.wikidata.wdtk.datamodel.implementation;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Jackson implementation of {@link MediaInfoUpdate}.
 */
public class MediaInfoUpdateImpl extends LabeledDocumentUpdateImpl implements MediaInfoUpdate {

	/**
	 * Initializes new media update.
	 * 
	 * @param entityId
	 *            ID of the media that is to be updated
	 * @param revisionId
	 *            base media revision to be updated or zero if not available
	 * @param labels
	 *            changes in entity labels or {@code null} for no change
	 * @param statements
	 *            changes in entity statements, possibly empty
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	public MediaInfoUpdateImpl(
			MediaInfoIdValue entityId,
			long revisionId,
			TermUpdate labels,
			StatementUpdate statements) {
		super(entityId, revisionId, labels, statements);
	}

	@JsonIgnore
	@Override
	public MediaInfoIdValue getEntityId() {
		return (MediaInfoIdValue) super.getEntityId();
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsMediaInfoUpdate(this, obj);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

}
