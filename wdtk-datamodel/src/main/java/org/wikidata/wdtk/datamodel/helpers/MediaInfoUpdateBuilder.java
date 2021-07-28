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
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

/**
 * Builder for incremental construction of {@link MediaInfoUpdate} objects.
 */
public class MediaInfoUpdateBuilder extends LabeledDocumentUpdateBuilder {

	private MediaInfoUpdateBuilder(MediaInfoIdValue mediaInfoId, long revisionId) {
		super(mediaInfoId, revisionId);
	}

	private MediaInfoUpdateBuilder(MediaInfoDocument revision) {
		super(revision);
	}

	/**
	 * Creates new builder object for constructing update of media entity with given
	 * revision ID.
	 * 
	 * @param mediaInfoId
	 *            ID of the media entity that is to be updated
	 * @param revisionId
	 *            ID of the base media entity revision to be updated or zero if not
	 *            available
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code mediaInfoId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code mediaInfoId} is a placeholder ID
	 */
	public static MediaInfoUpdateBuilder forBaseRevisionId(MediaInfoIdValue mediaInfoId, long revisionId) {
		return new MediaInfoUpdateBuilder(mediaInfoId, revisionId);
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
	 *             if {@code mediaInfoId} is a placeholder ID
	 */
	public static MediaInfoUpdateBuilder forEntityId(MediaInfoIdValue mediaInfoId) {
		return new MediaInfoUpdateBuilder(mediaInfoId, 0);
	}

	/**
	 * Creates new builder object for constructing update of given base media entity
	 * revision. Provided media document might not represent the latest revision of
	 * the media entity as currently stored in Wikibase. It will be used for
	 * validation in builder methods. If the document has revision ID, it will be
	 * used to detect edit conflicts.
	 * 
	 * @param revision
	 *            base media entity revision to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} has placeholder ID
	 */
	public static MediaInfoUpdateBuilder forBaseRevision(MediaInfoDocument revision) {
		return new MediaInfoUpdateBuilder(revision);
	}

	@Override
	MediaInfoIdValue getEntityId() {
		return (MediaInfoIdValue) super.getEntityId();
	}

	@Override
	MediaInfoDocument getBaseRevision() {
		return (MediaInfoDocument) super.getBaseRevision();
	}

	@Override
	public MediaInfoUpdateBuilder updateStatements(StatementUpdate update) {
		super.updateStatements(update);
		return this;
	}

	@Override
	public MediaInfoUpdateBuilder updateLabels(TermUpdate update) {
		super.updateLabels(update);
		return this;
	}

	/**
	 * Replays all changes in provided update into this builder object. Changes from
	 * the update are added on top of changes already present in this builder
	 * object.
	 * 
	 * @param update
	 *            media update to replay
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code update} cannot be applied to base entity revision (if
	 *             available)
	 */
	public MediaInfoUpdateBuilder apply(MediaInfoUpdate update) {
		super.append(update);
		return this;
	}

	@Override
	public MediaInfoUpdate build() {
		return Datamodel.makeMediaInfoUpdate(getEntityId(), getBaseRevisionId(), labels, statements);
	}

}
