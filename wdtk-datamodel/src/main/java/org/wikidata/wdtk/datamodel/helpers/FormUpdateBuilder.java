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

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

/**
 * Builder for incremental construction of {@link FormUpdate} objects.
 */
public class FormUpdateBuilder extends StatementDocumentUpdateBuilder {

	private TermUpdate representations = TermUpdate.EMPTY;
	private Set<ItemIdValue> grammaticalFeatures;

	private FormUpdateBuilder(FormIdValue formId, long revisionId) {
		super(formId, revisionId);
	}

	private FormUpdateBuilder(FormDocument revision) {
		super(revision);
	}

	/**
	 * Creates new builder object for constructing update of form entity with given
	 * revision ID.
	 * 
	 * @param formId
	 *            ID of the form that is to be updated
	 * @param revisionId
	 *            ID of the base form revision to be updated or zero if not
	 *            available
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code formId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code formId} is a placeholder ID
	 */
	public static FormUpdateBuilder forBaseRevisionId(FormIdValue formId, long revisionId) {
		return new FormUpdateBuilder(formId, revisionId);
	}

	/**
	 * Creates new builder object for constructing update of form entity with given
	 * ID.
	 * 
	 * @param formId
	 *            ID of the form that is to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code formId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code formId} is a placeholder ID
	 */
	public static FormUpdateBuilder forEntityId(FormIdValue formId) {
		return new FormUpdateBuilder(formId, 0);
	}

	/**
	 * Creates new builder object for constructing update of given base form entity
	 * revision. Provided form document might not represent the latest revision of
	 * the form entity as currently stored in Wikibase. It will be used for
	 * validation in builder methods. If the document has revision ID, it will be
	 * used to detect edit conflicts.
	 * 
	 * @param revision
	 *            base form entity revision to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} has placeholder ID
	 */
	public static FormUpdateBuilder forBaseRevision(FormDocument revision) {
		return new FormUpdateBuilder(revision);
	}

	@Override
	FormIdValue getEntityId() {
		return (FormIdValue) super.getEntityId();
	}

	@Override
	FormDocument getBaseRevision() {
		return (FormDocument) super.getBaseRevision();
	}

	@Override
	public FormUpdateBuilder updateStatements(StatementUpdate update) {
		super.updateStatements(update);
		return this;
	}

	/**
	 * Updates form representations. If this method is called multiple times,
	 * changes are accumulated. If base entity revision was provided, redundant
	 * changes are silently ignored, resulting in empty update.
	 * 
	 * @param update
	 *            changes in form representations
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 */
	public FormUpdateBuilder updateRepresentations(TermUpdate update) {
		Objects.requireNonNull(update, "Update cannot be null.");
		TermUpdateBuilder combined = getBaseRevision() != null
				? TermUpdateBuilder.forTerms(getBaseRevision().getRepresentations().values())
				: TermUpdateBuilder.create();
		combined.append(representations);
		combined.append(update);
		representations = combined.build();
		return this;
	}

	/**
	 * Sets grammatical features of the form. Any previously assigned grammatical
	 * features are removed. To remove all grammatical features without replacement,
	 * call this method with empty collection. If base entity revision was provided,
	 * attempt to replace grammatical features with identical set is silently
	 * ignored, resulting in empty update.
	 * 
	 * @param features
	 *            new grammatical features of the form
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code features} or any of its items is {@code null}
	 * @throws IllegalArgumentException
	 *             if any item ID in {@code features} is a placeholder ID or if
	 *             there are duplicate features
	 */
	public FormUpdateBuilder setGrammaticalFeatures(Collection<ItemIdValue> features) {
		Objects.requireNonNull(features, "Collection of grammatical features cannot be null.");
		for (ItemIdValue id : features) {
			Objects.requireNonNull(id, "Grammatical feature IDs must not be null.");
			Validate.isTrue(!id.isPlaceholder(), "Grammatical feature ID cannot be a placeholder ID.");
		}
		Set<ItemIdValue> set = new HashSet<>(features);
		Validate.isTrue(set.size() == features.size(), "Every grammatical feature must be unique.");
		if (getBaseRevision() != null && set.equals(new HashSet<>(getBaseRevision().getGrammaticalFeatures()))) {
			grammaticalFeatures = null;
			return this;
		}
		grammaticalFeatures = new HashSet<>(features);
		return this;
	}

	/**
	 * Replays all changes in provided update into this builder object. Changes from
	 * the update are added on top of changes already present in this builder
	 * object.
	 * 
	 * @param update
	 *            form update to replay
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code update} cannot be applied to base entity revision (if
	 *             available)
	 */
	public FormUpdateBuilder append(FormUpdate update) {
		super.append(update);
		updateRepresentations(update.getRepresentations());
		if (update.getGrammaticalFeatures().isPresent()) {
			setGrammaticalFeatures(update.getGrammaticalFeatures().get());
		}
		return this;
	}

	@Override
	public FormUpdate build() {
		return Datamodel.makeFormUpdate(getEntityId(), getBaseRevisionId(),
				representations, grammaticalFeatures, statements);
	}

}
