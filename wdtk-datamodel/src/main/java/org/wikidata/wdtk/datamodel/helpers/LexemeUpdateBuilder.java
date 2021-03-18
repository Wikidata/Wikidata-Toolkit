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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MultilingualTextUpdate;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

/**
 * Builder for incremental construction of {@link LexemeUpdate} objects.
 */
public class LexemeUpdateBuilder extends StatementDocumentUpdateBuilder {

	private ItemIdValue language;
	private ItemIdValue lexicalCategory;
	private MultilingualTextUpdate lemmas = MultilingualTextUpdate.NULL;
	private final List<SenseDocument> addedSenses = new ArrayList<>();
	private final Map<SenseIdValue, SenseUpdate> updatedSenses = new HashMap<>();
	private final Set<SenseIdValue> removedSenses = new HashSet<>();
	private final List<FormDocument> addedForms = new ArrayList<>();
	private final Map<FormIdValue, FormUpdate> updatedForms = new HashMap<>();
	private final Set<FormIdValue> removedForms = new HashSet<>();

	/**
	 * Initializes new builder object for constructing update of lexeme entity with
	 * given ID.
	 * 
	 * @param lexemeId
	 *            ID of the lexeme entity that is to be updated
	 * @throws NullPointerException
	 *             if {@code lexemeId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code lexemeId} is not a valid ID
	 */
	private LexemeUpdateBuilder(LexemeIdValue lexemeId) {
		super(lexemeId);
	}

	/**
	 * Initializes new builder object for constructing update of given base lexeme
	 * entity revision.
	 * 
	 * @param revision
	 *            base lexeme revision to be updated
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} does not have valid ID
	 */
	private LexemeUpdateBuilder(LexemeDocument revision) {
		super(revision);
	}

	/**
	 * Creates new builder object for constructing update of lexeme entity with
	 * given ID.
	 * 
	 * @param lexemeId
	 *            ID of the lexeme that is to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code lexemeId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code lexemeId} is not valid
	 */
	public static LexemeUpdateBuilder forEntityId(LexemeIdValue lexemeId) {
		return new LexemeUpdateBuilder(lexemeId);
	}

	/**
	 * Creates new builder object for constructing update of given base lexeme
	 * entity revision. Provided lexeme document might not represent the latest
	 * revision of the lexeme entity as currently stored in Wikibase. It will be
	 * used for validation in builder methods. If the document has revision ID, it
	 * will be used to detect edit conflicts.
	 * 
	 * @param revision
	 *            base lexeme entity revision to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code revision} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code revision} does not have valid ID
	 */
	public static LexemeUpdateBuilder forBaseRevision(LexemeDocument revision) {
		return new LexemeUpdateBuilder(revision);
	}

	@Override
	LexemeIdValue getEntityId() {
		return (LexemeIdValue) super.getEntityId();
	}

	@Override
	LexemeDocument getBaseRevision() {
		return (LexemeDocument) super.getBaseRevision();
	}

	@Override
	public LexemeUpdateBuilder updateStatements(StatementUpdate update) {
		super.updateStatements(update);
		return this;
	}

	/**
	 * Sets lexeme language.
	 * 
	 * @param language
	 *            new lexeme language
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code language} is {@code null}
	 */
	public LexemeUpdateBuilder setLanguage(ItemIdValue language) {
		Objects.requireNonNull(language, "Language cannot be null.");
		Validate.isTrue(language.isValid(), "Language ID is not valid.");
		this.language = language;
		return this;
	}

	/**
	 * Sets lexical category of the lexeme.
	 * 
	 * @param category
	 *            new lexical category
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code category} is {@code null}
	 */
	public LexemeUpdateBuilder setLexicalCategory(ItemIdValue category) {
		Objects.requireNonNull(category, "Lexical category cannot be null.");
		Validate.isTrue(category.isValid(), "Lexical category ID is not valid.");
		lexicalCategory = category;
		return this;
	}

	/**
	 * Updates lemmas. Any previous changes to lemmas are discarded.
	 * 
	 * @param update
	 *            changes to lemmas
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if removed lemma is not present in current lexeme revision (if
	 *             available)
	 */
	public LexemeUpdateBuilder updateLemmas(MultilingualTextUpdate update) {
		Objects.requireNonNull(update, "Update cannot be null.");
		if (getBaseRevision() != null) {
			for (String removed : update.getRemovedValues()) {
				if (!getBaseRevision().getLemmas().containsKey(removed)) {
					throw new IllegalArgumentException("Removed lemma is not in the current revision.");
				}
			}
		}
		lemmas = update;
		return this;
	}

	/**
	 * Adds sense to the lexeme. If {@code sense} has an ID (perhaps because it is a
	 * modified copy of another sense), its ID is stripped to ensure the sense is
	 * added and no other sense is modified.
	 * 
	 * @param sense
	 *            new sense to add
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code sense} is {@code null}
	 */
	public LexemeUpdateBuilder addSense(SenseDocument sense) {
		Objects.requireNonNull(sense, "Sense cannot be null.");
		if (sense.getEntityId().isValid())
			sense = sense.withEntityId(SenseIdValue.NULL);
		addedSenses.add(sense);
		return this;
	}

	/**
	 * Updates existing sense in the lexeme. Calling this method overrides any
	 * previous changes made to the same sense ID by this method or
	 * {@link #removeSense(SenseIdValue)}.
	 * 
	 * @param update
	 *            update of existing sense
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code update} refers to sense ID that does not exist in
	 *             current version of the lexeme document (if available)
	 */
	public LexemeUpdateBuilder updateSense(SenseUpdate update) {
		Objects.requireNonNull(update, "Sense update cannot be null.");
		if (getBaseRevision() != null && getBaseRevision().getSenses().stream()
				.noneMatch(s -> s.getEntityId().equals(update.getEntityId()))) {
			throw new IllegalArgumentException("Cannot update sense that is not in the current revision.");
		}
		updatedSenses.put(update.getEntityId(), update);
		removedSenses.remove(update.getEntityId());
		return this;
	}

	/**
	 * Removes existing sense from the lexeme. Calling this method overrides any
	 * previous changes made to the same sense ID by
	 * {@link #updateSense(SenseUpdate)}. Removing the same sense ID twice is
	 * silently tolerated.
	 * 
	 * @param senseId
	 *            ID of the removed sense
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code senseId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code senseId} is not valid or if such ID does not exist in
	 *             current version of the lexeme document (if available)
	 */
	public LexemeUpdateBuilder removeSense(SenseIdValue senseId) {
		Objects.requireNonNull(senseId, "Sense ID cannot be null.");
		Validate.isTrue(senseId.isValid(), "ID of removed sense must be valid.");
		if (getBaseRevision() != null
				&& getBaseRevision().getSenses().stream().noneMatch(s -> s.getEntityId().equals(senseId))) {
			throw new IllegalArgumentException("Cannot remove sense that is not in the current revision.");
		}
		removedSenses.add(senseId);
		updatedSenses.remove(senseId);
		return this;
	}

	/**
	 * Adds form to the lexeme. If {@code form} has an ID (perhaps because it is a
	 * modified copy of another form), its ID is stripped to ensure the form is
	 * added and no other form is modified.
	 * 
	 * @param form
	 *            new form to add
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code form} is {@code null}
	 */
	public LexemeUpdateBuilder addForm(FormDocument form) {
		Objects.requireNonNull(form, "Form cannot be null.");
		if (form.getEntityId().isValid())
			form = form.withEntityId(FormIdValue.NULL);
		addedForms.add(form);
		return this;
	}

	/**
	 * Updates existing form in the lexeme. Calling this method overrides any
	 * previous changes made to the same form ID by this method or
	 * {@link #removeForm(SenseIdValue)}.
	 * 
	 * @param update
	 *            update of existing form
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code update} refers to form ID that does not exist in
	 *             current version of the lexeme document (if available)
	 */
	public LexemeUpdateBuilder updateForm(FormUpdate update) {
		Objects.requireNonNull(update, "Form update cannot be null.");
		if (getBaseRevision() != null && getBaseRevision().getForms().stream()
				.noneMatch(f -> f.getEntityId().equals(update.getEntityId()))) {
			throw new IllegalArgumentException("Cannot update form that is not in the current revision.");
		}
		updatedForms.put(update.getEntityId(), update);
		removedForms.remove(update.getEntityId());
		return this;
	}

	/**
	 * Removes existing form from the lexeme. Calling this method overrides any
	 * previous changes made to the same form ID by {@link #updateForm(FormUpdate)}.
	 * Removing the same form ID twice is silently tolerated.
	 * 
	 * @param formId
	 *            ID of the removed form
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code formId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code formId} is not valid or if such ID does not exist in
	 *             current version of the lexeme document (if available)
	 */
	public LexemeUpdateBuilder removeForm(FormIdValue formId) {
		Objects.requireNonNull(formId, "Form ID cannot be null.");
		Validate.isTrue(formId.isValid(), "ID of removed form must be valid.");
		if (getBaseRevision() != null
				&& getBaseRevision().getForms().stream().noneMatch(f -> f.getEntityId().equals(formId))) {
			throw new IllegalArgumentException("Cannot remove form that is not in the current revision.");
		}
		removedForms.add(formId);
		updatedForms.remove(formId);
		return this;
	}

	@Override
	public LexemeUpdate build() {
		return factory.getLexemeUpdate(getEntityId(), getBaseRevision(),
				language, lexicalCategory, lemmas, statements,
				addedSenses, updatedSenses.values(), removedSenses,
				addedForms, updatedForms.values(), removedForms);
	}

}
