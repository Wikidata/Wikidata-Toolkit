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

/**
 * Builder for incremental construction of {@link LexemeUpdate} objects.
 */
public class LexemeUpdateBuilder extends StatementDocumentUpdateBuilder {

	private ItemIdValue language;
	private ItemIdValue lexicalCategory;
	private MultilingualTextUpdate lemmas;
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
	 * Initializes new builder object for constructing update of given lexeme entity
	 * revision.
	 * 
	 * @param document
	 *            lexeme revision to be updated
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} does not have valid ID
	 */
	private LexemeUpdateBuilder(LexemeDocument document) {
		super(document);
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
	public static LexemeUpdateBuilder forLexemeId(LexemeIdValue lexemeId) {
		return new LexemeUpdateBuilder(lexemeId);
	}

	/**
	 * Creates new builder object for constructing update of given lexeme entity
	 * revision. Provided lexeme document might not represent the latest revision of
	 * the lexeme entity as currently stored in Wikibase. It will be used for
	 * validation in builder methods. If the document has revision ID, it will be
	 * used to detect edit conflicts.
	 * 
	 * @param document
	 *            lexeme entity revision to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} does not have valid ID
	 */
	public static LexemeUpdateBuilder forLexemeDocument(LexemeDocument document) {
		return new LexemeUpdateBuilder(document);
	}

	@Override
	protected LexemeIdValue getEntityId() {
		return (LexemeIdValue) super.getEntityId();
	}

	@Override
	protected LexemeDocument getCurrentDocument() {
		return (LexemeDocument) super.getCurrentDocument();
	}

	public void setLanguage(ItemIdValue language) {
		Objects.requireNonNull(language, "Language cannot be null.");
		Validate.isTrue(language.isValid(), "Language ID is not valid.");
		this.language = language;
	}

	public void setLexicalCategory(ItemIdValue category) {
		Objects.requireNonNull(category, "Lexical category cannot be null.");
		Validate.isTrue(category.isValid(), "Lexical category ID is not valid.");
		lexicalCategory = category;
	}

	/**
	 * Updates lemmas.
	 * 
	 * @param update
	 *            changes to lemmas
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if removed lemma is not present in current lexeme revision (if
	 *             available)
	 */
	public void updateLemmas(MultilingualTextUpdate update) {
		Objects.requireNonNull(update, "Update cannot be null.");
		if (getCurrentDocument() != null) {
			for (String removed : update.getRemovedValues()) {
				if (!getCurrentDocument().getLemmas().containsKey(removed)) {
					throw new IllegalArgumentException("Removed lemma is not in the current revision.");
				}
			}
		}
		lemmas = update;
	}

	/**
	 * Adds sense to the lexeme. If {@code sense} has an ID (perhaps because it is a
	 * modified copy of another sense), its ID is stripped to ensure the sense is
	 * added and no other sense is modified.
	 * 
	 * @param sense
	 *            new sense to add
	 * @throws NullPointerException
	 *             if {@code sense} is {@code null}
	 */
	public void addSense(SenseDocument sense) {
		Objects.requireNonNull(sense, "Sense cannot be null.");
		if (sense.getEntityId().isValid())
			sense = sense.withEntityId(SenseIdValue.NULL);
		addedSenses.add(sense);
	}

	/**
	 * Updates existing sense in the lexeme. Calling this method overrides any
	 * previous changes made to the same sense ID by this method or
	 * {@link #removeSense(SenseIdValue)}.
	 * 
	 * @param update
	 *            update of existing sense
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code update} refers to sense ID that does not exist in
	 *             current version of the lexeme document (if available)
	 */
	public void updateSense(SenseUpdate update) {
		Objects.requireNonNull(update, "Sense update cannot be null.");
		if (getCurrentDocument() != null && getCurrentDocument().getSenses().stream()
				.noneMatch(s -> s.getEntityId().equals(update.getEntityId()))) {
			throw new IllegalArgumentException("Cannot update sense that is not in the current revision.");
		}
		updatedSenses.put(update.getEntityId(), update);
		removedSenses.remove(update.getEntityId());
	}

	/**
	 * Removes existing sense from the lexeme. Calling this method overrides any
	 * previous changes made to the same sense ID by
	 * {@link #updateSense(SenseUpdate)}. Removing the same sense ID twice is
	 * silently tolerated.
	 * 
	 * @param senseId
	 *            ID of the removed sense
	 * @throws NullPointerException
	 *             if {@code senseId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code senseId} is not valid or if such ID does not exist in
	 *             current version of the lexeme document (if available)
	 */
	public void removeSense(SenseIdValue senseId) {
		Objects.requireNonNull(senseId, "Sense ID cannot be null.");
		Validate.isTrue(senseId.isValid(), "ID of removed sense must be valid.");
		if (getCurrentDocument() != null
				&& getCurrentDocument().getSenses().stream().noneMatch(s -> s.getEntityId().equals(senseId))) {
			throw new IllegalArgumentException("Cannot remove sense that is not in the current revision.");
		}
		removedSenses.add(senseId);
		updatedSenses.remove(senseId);
	}

	/**
	 * Adds form to the lexeme. If {@code form} has an ID (perhaps because it is a
	 * modified copy of another form), its ID is stripped to ensure the form is
	 * added and no other form is modified.
	 * 
	 * @param form
	 *            new form to add
	 * @throws NullPointerException
	 *             if {@code form} is {@code null}
	 */
	public void addForm(FormDocument form) {
		Objects.requireNonNull(form, "Form cannot be null.");
		if (form.getEntityId().isValid())
			form = form.withEntityId(FormIdValue.NULL);
		addedForms.add(form);
	}

	/**
	 * Updates existing form in the lexeme. Calling this method overrides any
	 * previous changes made to the same form ID by this method or
	 * {@link #removeForm(SenseIdValue)}.
	 * 
	 * @param update
	 *            update of existing form
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code update} refers to form ID that does not exist in
	 *             current version of the lexeme document (if available)
	 */
	public void updateForm(FormUpdate update) {
		Objects.requireNonNull(update, "Form update cannot be null.");
		if (getCurrentDocument() != null && getCurrentDocument().getForms().stream()
				.noneMatch(f -> f.getEntityId().equals(update.getEntityId()))) {
			throw new IllegalArgumentException("Cannot update form that is not in the current revision.");
		}
		updatedForms.put(update.getEntityId(), update);
		removedForms.remove(update.getEntityId());
	}

	/**
	 * Removes existing form from the lexeme. Calling this method overrides any
	 * previous changes made to the same form ID by {@link #updateForm(FormUpdate)}.
	 * Removing the same form ID twice is silently tolerated.
	 * 
	 * @param formId
	 *            ID of the removed form
	 * @throws NullPointerException
	 *             if {@code formId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code formId} is not valid or if such ID does not exist in
	 *             current version of the lexeme document (if available)
	 */
	public void removeForm(FormIdValue formId) {
		Objects.requireNonNull(formId, "Form ID cannot be null.");
		Validate.isTrue(formId.isValid(), "ID of removed form must be valid.");
		if (getCurrentDocument() != null
				&& getCurrentDocument().getForms().stream().noneMatch(f -> f.getEntityId().equals(formId))) {
			throw new IllegalArgumentException("Cannot remove form that is not in the current revision.");
		}
		removedForms.add(formId);
		updatedForms.remove(formId);
	}

	@Override
	public LexemeUpdate build() {
		return factory.getLexemeUpdate(getEntityId(), getCurrentDocument(), language, lexicalCategory, lemmas,
				getAddedStatements(), getReplacedStatements(), getRemovedStatements(),
				addedSenses, updatedSenses.values(), removedSenses,
				addedForms, updatedForms.values(), removedForms);
	}

}
