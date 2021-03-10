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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;

/**
 * Builder for incremental construction of {@link SenseUpdate} objects.
 */
public class SenseUpdateBuilder extends StatementUpdateBuilder {

	private final Map<String, MonolingualTextValue> modifiedGlosses = new HashMap<>();
	private final Set<String> removedGlosses = new HashSet<>();

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
	 * Initializes new builder object for constructing update of given sense entity
	 * revision.
	 * 
	 * @param document
	 *            sense revision to be updated
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} does not have valid ID
	 */
	private SenseUpdateBuilder(SenseDocument document) {
		super(document);
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
	public static SenseUpdateBuilder forSenseId(SenseIdValue senseId) {
		return new SenseUpdateBuilder(senseId);
	}

	/**
	 * Creates new builder object for constructing update of given sense entity
	 * revision. Provided sense document might not represent the latest revision of
	 * the sense entity as currently stored in Wikibase. It will be used for
	 * validation in builder methods. If the document has revision ID, it will be
	 * used to detect edit conflicts.
	 * 
	 * @param document
	 *            sense entity revision to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} does not have valid ID
	 */
	public static SenseUpdateBuilder forSenseDocument(SenseDocument document) {
		return new SenseUpdateBuilder(document);
	}

	@Override
	protected SenseIdValue getEntityId() {
		return (SenseIdValue) super.getEntityId();
	}

	@Override
	protected SenseDocument getCurrentDocument() {
		return (SenseDocument) super.getCurrentDocument();
	}

	/**
	 * Adds or changes sense gloss. If there is no gloss for the language code, new
	 * gloss is added. If a gloss with this language code already exists, it is
	 * replaced. Glosses with other language codes are not touched. Calling this
	 * method overrides any previous changes made with the same language code by
	 * this method or {@link #removeGloss(String)}.
	 * 
	 * @param gloss
	 *            sense gloss to add or change
	 * @throws NullPointerException
	 *             if {@code gloss} is {@code null}
	 */
	public void setGloss(MonolingualTextValue gloss) {
		Objects.requireNonNull(gloss, "Gloss cannot be null.");
		modifiedGlosses.put(gloss.getLanguageCode(), gloss);
		removedGlosses.remove(gloss.getLanguageCode());
	}

	/**
	 * Removes sense gloss. Glosses with other language codes are not touched.
	 * Calling this method overrides any previous changes made with the same
	 * language code by this method or {@link #setGloss(MonolingualTextValue)}.
	 * 
	 * @param languageCode
	 *            language code of the removed sense gloss
	 * @throws NullPointerException
	 *             if {@code languageCode} is {@code null}
	 * @throws IllegalArgumentException
	 *             if the gloss is not present in current sense entity revision (if
	 *             available)
	 */
	public void removeGloss(String languageCode) {
		Objects.requireNonNull(languageCode, "Language code cannot be null.");
		if (getCurrentDocument() != null && !getCurrentDocument().getGlosses().containsKey(languageCode)) {
			throw new IllegalArgumentException("Gloss with this language code is not in the current revision.");
		}
		removedGlosses.add(languageCode);
		modifiedGlosses.remove(languageCode);
	}

	@Override
	public SenseUpdate build() {
		return factory.getSenseUpdate(getEntityId(), getCurrentDocument(),
				modifiedGlosses.values(), removedGlosses,
				getAddedStatements(), getReplacedStatements(), getRemovedStatements());
	}

}
