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

import java.util.Objects;

import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

/**
 * Builder for incremental construction of {@link SenseUpdate} objects.
 */
public class SenseUpdateBuilder extends StatementDocumentUpdateBuilder {

	private TermUpdate glosses = TermUpdate.EMPTY;

	private SenseUpdateBuilder(SenseIdValue senseId, long revisionId) {
		super(senseId, revisionId);
	}

	private SenseUpdateBuilder(SenseDocument revision) {
		super(revision);
	}

	/**
	 * Creates new builder object for constructing update of sense entity with given
	 * revision ID.
	 * 
	 * @param senseId
	 *            ID of the sense that is to be updated
	 * @param revisionId
	 *            ID of the base sense revision to be updated or zero if not
	 *            available
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code senseId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code senseId} is a placeholder ID
	 */
	public static SenseUpdateBuilder forBaseRevisionId(SenseIdValue senseId, long revisionId) {
		return new SenseUpdateBuilder(senseId, revisionId);
	}

	/**
	 * Creates new builder object for constructing update of sense entity with given
	 * ID.
	 * 
	 * @param senseId
	 *            ID of the sense that is to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code senseId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code senseId} is a placeholder ID
	 */
	public static SenseUpdateBuilder forEntityId(SenseIdValue senseId) {
		return new SenseUpdateBuilder(senseId, 0);
	}

	/**
	 * Creates new builder object for constructing update of given base sense entity
	 * revision. Provided sense document might not represent the latest revision of
	 * the sense entity as currently stored in Wikibase. It will be used for
	 * validation in builder methods. If the document has revision ID, it will be
	 * used to detect edit conflicts.
	 * 
	 * @param revision
	 *            base sense entity revision to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} has placeholder ID
	 */
	public static SenseUpdateBuilder forBaseRevision(SenseDocument revision) {
		return new SenseUpdateBuilder(revision);
	}

	@Override
	SenseIdValue getEntityId() {
		return (SenseIdValue) super.getEntityId();
	}

	@Override
	SenseDocument getBaseRevision() {
		return (SenseDocument) super.getBaseRevision();
	}

	@Override
	public SenseUpdateBuilder updateStatements(StatementUpdate update) {
		super.updateStatements(update);
		return this;
	}

	/**
	 * Updates sense glosses. If this method is called multiple times, changes are
	 * accumulated. If base entity revision was provided, redundant changes are
	 * silently ignored, resulting in empty update.
	 * 
	 * @param update
	 *            changes in sense glosses
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 */
	public SenseUpdateBuilder updateGlosses(TermUpdate update) {
		Objects.requireNonNull(update, "Update cannot be null.");
		TermUpdateBuilder combined = getBaseRevision() != null
				? TermUpdateBuilder.forTerms(getBaseRevision().getGlosses().values())
				: TermUpdateBuilder.create();
		combined.append(glosses);
		combined.append(update);
		glosses = combined.build();
		return this;
	}

	/**
	 * Replays all changes in provided update into this builder object. Changes from
	 * the update are added on top of changes already present in this builder
	 * object.
	 * 
	 * @param update
	 *            sense update to replay
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code update} cannot be applied to base entity revision (if
	 *             available)
	 */
	public SenseUpdateBuilder append(SenseUpdate update) {
		super.append(update);
		updateGlosses(update.getGlosses());
		return this;
	}

	@Override
	public SenseUpdate build() {
		return Datamodel.makeSenseUpdate(getEntityId(), getBaseRevisionId(), glosses, statements);
	}

}
