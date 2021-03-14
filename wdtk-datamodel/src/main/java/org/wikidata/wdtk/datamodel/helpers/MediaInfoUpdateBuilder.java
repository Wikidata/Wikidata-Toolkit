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

import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoUpdate;

/**
 * Builder for incremental construction of {@link MediaInfoUpdate} objects.
 */
public class MediaInfoUpdateBuilder extends LabeledStatementUpdateBuilder {

	/**
	 * Initializes new builder object for constructing update of media entity with
	 * given ID.
	 * 
	 * @param mediaInfoId
	 *            ID of the media entity that is to be updated
	 * @throws NullPointerException
	 *             if {@code mediaInfoId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code mediaInfoId} is not a valid ID
	 */
	private MediaInfoUpdateBuilder(MediaInfoIdValue mediaInfoId) {
		super(mediaInfoId);
	}

	/**
	 * Initializes new builder object for constructing update of given media entity
	 * revision.
	 * 
	 * @param document
	 *            form revision to be updated
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} does not have valid ID
	 */
	private MediaInfoUpdateBuilder(MediaInfoDocument document) {
		super(document);
	}

	/**
	 * Creates new builder object for constructing update of media entity with given
	 * ID.
	 * 
	 * @param mediaInfoId
	 *            ID of the media entity that is to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code mediaInfoId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code mediaInfoId} is not valid
	 */
	public static MediaInfoUpdateBuilder forMediaInfoId(MediaInfoIdValue mediaInfoId) {
		return new MediaInfoUpdateBuilder(mediaInfoId);
	}

	/**
	 * Creates new builder object for constructing update of given media entity
	 * revision. Provided media document might not represent the latest revision of
	 * the media entity as currently stored in Wikibase. It will be used for
	 * validation in builder methods. If the document has revision ID, it will be
	 * used to detect edit conflicts.
	 * 
	 * @param document
	 *            media entity revision to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} does not have valid ID
	 */
	public static MediaInfoUpdateBuilder forMediaInfoDocument(MediaInfoDocument document) {
		return new MediaInfoUpdateBuilder(document);
	}

	@Override
	protected MediaInfoIdValue getEntityId() {
		return (MediaInfoIdValue) super.getEntityId();
	}

	@Override
	protected MediaInfoDocument getCurrentDocument() {
		return (MediaInfoDocument) super.getCurrentDocument();
	}

	@Override
	public MediaInfoUpdate build() {
		return factory.getMediaInfoUpdate(getEntityId(), getCurrentDocument(), getLabels(),
				getAddedStatements(), getReplacedStatements(), getRemovedStatements());
	}

}
