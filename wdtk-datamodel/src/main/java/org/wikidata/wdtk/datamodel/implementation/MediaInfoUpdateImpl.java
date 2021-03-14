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

import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MultilingualTextUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

/**
 * Jackson implementation of {@link MediaInfoUpdate}.
 */
public class MediaInfoUpdateImpl extends LabeledStatementDocumentUpdateImpl implements MediaInfoUpdate {

	/**
	 * Initializes new media update.
	 * 
	 * @param entityId
	 *            ID of the media that is to be updated
	 * @param document
	 *            media revision to be updated or {@code null} if not available
	 * @param labels
	 *            changes in entity labels or {@code null} for no change
	 * @param statements
	 *            changes in entity statements, possibly empty
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	protected MediaInfoUpdateImpl(
			MediaInfoIdValue entityId,
			MediaInfoDocument document,
			MultilingualTextUpdate labels,
			StatementUpdate statements) {
		super(entityId, document, labels, statements);
	}

	@Override
	public MediaInfoIdValue getEntityId() {
		return (MediaInfoIdValue) super.getEntityId();
	}

	@Override
	public MediaInfoDocument getCurrentDocument() {
		return (MediaInfoDocument) super.getCurrentDocument();
	}

}
