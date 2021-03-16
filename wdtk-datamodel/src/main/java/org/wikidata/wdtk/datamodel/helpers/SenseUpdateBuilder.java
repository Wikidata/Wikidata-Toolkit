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

import org.wikidata.wdtk.datamodel.interfaces.MultilingualTextUpdate;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

/**
 * Builder for incremental construction of {@link SenseUpdate} objects.
 */
public class SenseUpdateBuilder extends StatementDocumentUpdateBuilder {

	private MultilingualTextUpdate glosses = MultilingualTextUpdate.NULL;

	/**
	 * Initializes new builder object for constructing update of sense entity with
	 * given ID.
	 * 
	 * @param senseId
	 *            ID of the sense entity that is to be updated
	 * @throws NullPointerException
	 *             if {@code senseId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code senseId} is not a valid ID
	 */
	private SenseUpdateBuilder(SenseIdValue senseId) {
		super(senseId);
	}

	/**
	 * Initializes new builder object for constructing update of given base sense
	 * entity revision.
	 * 
	 * @param revision
	 *            base sense revision to be updated
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} does not have valid ID
	 */
	private SenseUpdateBuilder(SenseDocument revision) {
		super(revision);
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
	 *             if {@code senseId} is not valid
	 */
	public static SenseUpdateBuilder forEntityId(SenseIdValue senseId) {
		return new SenseUpdateBuilder(senseId);
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
	 *             if {@code revision} does not have valid ID
	 */
	public static SenseUpdateBuilder forBaseRevision(SenseDocument revision) {
		return new SenseUpdateBuilder(revision);
	}

	@Override
	protected SenseIdValue getEntityId() {
		return (SenseIdValue) super.getEntityId();
	}

	@Override
	protected SenseDocument getBaseRevision() {
		return (SenseDocument) super.getBaseRevision();
	}

	@Override
	public SenseUpdateBuilder updateStatements(StatementUpdate update) {
		super.updateStatements(update);
		return this;
	}

	/**
	 * Updates sense glosses. Any previous changes to sense glosses are discarded.
	 * 
	 * @param update
	 *            changes to sense glosses
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if removed gloss is not present in current sense revision (if
	 *             available)
	 */
	public SenseUpdateBuilder updateGlosses(MultilingualTextUpdate update) {
		Objects.requireNonNull(update, "Update cannot be null.");
		if (getBaseRevision() != null) {
			for (String removed : update.getRemovedValues()) {
				if (!getBaseRevision().getGlosses().containsKey(removed)) {
					throw new IllegalArgumentException("Removed gloss is not in the current revision.");
				}
			}
		}
		glosses = update;
		return this;
	}

	@Override
	public SenseUpdate build() {
		return factory.getSenseUpdate(getEntityId(), getBaseRevision(), glosses, getStatements());
	}

}
