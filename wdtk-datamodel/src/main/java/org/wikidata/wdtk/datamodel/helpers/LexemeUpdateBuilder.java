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
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

/**
 * Builder for incremental construction of {@link LexemeUpdate} objects.
 */
public class LexemeUpdateBuilder extends StatementDocumentUpdateBuilder {

	private ItemIdValue language;
	private ItemIdValue lexicalCategory;
	private TermUpdate lemmas = TermUpdate.EMPTY;
	private final List<SenseDocument> addedSenses = new ArrayList<>();
	private final Map<SenseIdValue, SenseUpdate> updatedSenses = new HashMap<>();
	private final Set<SenseIdValue> removedSenses = new HashSet<>();
	private final List<FormDocument> addedForms = new ArrayList<>();
	private final Map<FormIdValue, FormUpdate> updatedForms = new HashMap<>();
	private final Set<FormIdValue> removedForms = new HashSet<>();

	private LexemeUpdateBuilder(LexemeIdValue lexemeId, long revisionId) {
		super(lexemeId, revisionId);
	}

	private LexemeUpdateBuilder(LexemeDocument revision) {
		super(revision);
	}

	/**
	 * Creates new builder object for constructing update of lexeme entity with
	 * given revision ID.
	 * 
	 * @param lexemeId
	 *            ID of the lexeme that is to be updated
	 * @param revisionId
	 *            ID of the base lexeme revision to be updated or zero if not
	 *            available
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code lexemeId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code lexemeId} is a placeholder ID
	 */
	public static LexemeUpdateBuilder forBaseRevisionId(LexemeIdValue lexemeId, long revisionId) {
		return new LexemeUpdateBuilder(lexemeId, revisionId);
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
	 *             if {@code lexemeId} is a placeholder ID
	 */
	public static LexemeUpdateBuilder forEntityId(LexemeIdValue lexemeId) {
		return new LexemeUpdateBuilder(lexemeId, 0);
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
	 *             if {@code revision} has placeholder ID
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
	 * Sets lexeme language. If base entity revision was provided, attempt to
	 * replace lexeme language with the same value is silently ignored, resulting in
	 * empty update.
	 * 
	 * @param language
	 *            new lexeme language
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code language} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code language} is an invalid ID
	 */
	public LexemeUpdateBuilder setLanguage(ItemIdValue language) {
		Objects.requireNonNull(language, "Language cannot be null.");
		Validate.isTrue(!language.isPlaceholder(), "Language ID cannot be a placeholder ID.");
		if (getBaseRevision() != null && getBaseRevision().getLanguage().equals(language)) {
			this.language = null;
			return this;
		}
		this.language = language;
		return this;
	}

	/**
	 * Sets lexical category of the lexeme. If base entity revision was provided,
	 * attempt to replace lexical category with the same value is silently ignored,
	 * resulting in empty update.
	 * 
	 * @param category
	 *            new lexical category
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code category} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code category} is an invalid ID
	 */
	public LexemeUpdateBuilder setLexicalCategory(ItemIdValue category) {
		Objects.requireNonNull(category, "Lexical category cannot be null.");
		Validate.isTrue(!category.isPlaceholder(), "Lexical category ID cannot be a placeholder ID.");
		if (getBaseRevision() != null && getBaseRevision().getLexicalCategory().equals(category)) {
			lexicalCategory = null;
			return this;
		}
		lexicalCategory = category;
		return this;
	}

	/**
	 * Updates lemmas. If this method is called multiple times, changes are
	 * accumulated. If base entity revision was provided, redundant changes are
	 * silently ignored, resulting in empty update.
	 * 
	 * @param update
	 *            changes in lemmas
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 */
	public LexemeUpdateBuilder updateLemmas(TermUpdate update) {
		Objects.requireNonNull(update, "Update cannot be null.");
		TermUpdateBuilder combined = getBaseRevision() != null
				? TermUpdateBuilder.forTerms(getBaseRevision().getLemmas().values())
				: TermUpdateBuilder.create();
		combined.append(lemmas);
		combined.append(update);
		lemmas = combined.build();
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
		if (!sense.getEntityId().isPlaceholder()) {
			sense = sense.withEntityId(SenseIdValue.NULL);
		}
		if (sense.getRevisionId() != 0) {
			sense = sense.withRevisionId(0);
		}
		addedSenses.add(sense);
		return this;
	}

	/**
	 * Updates existing sense in the lexeme. If this method is called multiple
	 * times, changes are accumulated. If base entity revision was provided, the
	 * update is checked against it and redundant changes are silently ignored,
	 * resulting in empty update.
	 * 
	 * @param update
	 *            update of existing sense
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if the sense does not exist in base revision (if available) or
	 *             the update cannot be applied to it
	 * @throws IllegalStateException
	 *             if the sense was removed by calling
	 *             {@link #removeSense(SenseIdValue)}
	 */
	public LexemeUpdateBuilder updateSense(SenseUpdate update) {
		Objects.requireNonNull(update, "Sense update cannot be null.");
		SenseIdValue id = update.getEntityId();
		Validate.validState(!removedSenses.contains(id), "Cannot update removed sense.");
		SenseUpdateBuilder builder;
		if (getBaseRevision() != null) {
			SenseDocument original = getBaseRevision().getSenses().stream()
					.filter(s -> s.getEntityId().equals(id))
					.findFirst().orElse(null);
			Validate.isTrue(original != null, "Cannot update sense that is not in the base revision.");
			builder = SenseUpdateBuilder.forBaseRevision(original.withRevisionId(getBaseRevisionId()));
		} else {
			builder = SenseUpdateBuilder.forBaseRevisionId(id, getBaseRevisionId());
		}
		SenseUpdate prior = updatedSenses.get(id);
		if (prior != null) {
			builder.append(prior);
		}
		builder.append(update);
		SenseUpdate combined = builder.build();
		if (!combined.isEmpty()) {
			updatedSenses.put(id, combined);
		} else {
			updatedSenses.remove(id);
		}
		return this;
	}

	/**
	 * Removes existing sense from the lexeme. Removing the same sense ID twice is
	 * silently tolerated. Any prior changes made by calling
	 * {@link #updateSense(SenseUpdate)} are discarded.
	 * 
	 * @param senseId
	 *            ID of the removed sense
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code senseId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code senseId} is not valid or if such ID does not exist in
	 *             base revision (if available)
	 */
	public LexemeUpdateBuilder removeSense(SenseIdValue senseId) {
		Objects.requireNonNull(senseId, "Sense ID cannot be null.");
		Validate.isTrue(!senseId.isPlaceholder(), "ID of removed sense cannot be a placeholder ID.");
		if (getBaseRevision() != null) {
			Validate.isTrue(getBaseRevision().getSenses().stream().anyMatch(s -> s.getEntityId().equals(senseId)),
					"Cannot remove sense that is not in the base revision.");
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
		if (!form.getEntityId().isPlaceholder()) {
			form = form.withEntityId(FormIdValue.NULL);
		}
		if (form.getRevisionId() != 0) {
			form = form.withRevisionId(0);
		}
		addedForms.add(form);
		return this;
	}

	/**
	 * Updates existing form in the lexeme. If this method is called multiple times,
	 * changes are accumulated. If base entity revision was provided, the update is
	 * checked against it and redundant changes are silently ignored, resulting in
	 * empty update.
	 * 
	 * @param update
	 *            update of existing form
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if the form does not exist in base revision (if available) or the
	 *             update cannot be applied to it
	 * @throws IllegalStateException
	 *             if the form was removed by calling
	 *             {@link #removeForm(FormIdValue)}
	 */
	public LexemeUpdateBuilder updateForm(FormUpdate update) {
		Objects.requireNonNull(update, "Form update cannot be null.");
		FormIdValue id = update.getEntityId();
		Validate.validState(!removedForms.contains(id), "Cannot update removed form.");
		FormUpdateBuilder builder;
		if (getBaseRevision() != null) {
			FormDocument original = getBaseRevision().getForms().stream()
					.filter(s -> s.getEntityId().equals(id))
					.findFirst().orElse(null);
			Validate.isTrue(original != null, "Cannot update form that is not in the base revision.");
			builder = FormUpdateBuilder.forBaseRevision(original.withRevisionId(getBaseRevisionId()));
		} else {
			builder = FormUpdateBuilder.forBaseRevisionId(id, getBaseRevisionId());
		}
		FormUpdate prior = updatedForms.get(id);
		if (prior != null) {
			builder.append(prior);
		}
		builder.append(update);
		FormUpdate combined = builder.build();
		if (!combined.isEmpty()) {
			updatedForms.put(id, combined);
		} else {
			updatedForms.remove(id);
		}
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
		Validate.isTrue(!formId.isPlaceholder(), "ID of removed form cannot be a placeholder ID.");
		if (getBaseRevision() != null) {
			Validate.isTrue(getBaseRevision().getForms().stream().anyMatch(s -> s.getEntityId().equals(formId)),
					"Cannot remove form that is not in the base revision.");
		}
		removedForms.add(formId);
		updatedForms.remove(formId);
		return this;
	}

	/**
	 * Replays all changes in provided update into this builder object. Changes from
	 * the update are added on top of changes already present in this builder
	 * object.
	 * 
	 * @param update
	 *            lexeme update to replay
	 * @return {@code this} (fluent method)
	 * @throws NullPointerException
	 *             if {@code update} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code update} cannot be applied to base entity revision (if
	 *             available)
	 */
	public LexemeUpdateBuilder append(LexemeUpdate update) {
		super.append(update);
		if (update.getLanguage().isPresent()) {
			setLanguage(update.getLanguage().get());
		}
		if (update.getLexicalCategory().isPresent()) {
			setLexicalCategory(update.getLexicalCategory().get());
		}
		updateLemmas(update.getLemmas());
		for (SenseDocument sense : update.getAddedSenses()) {
			addSense(sense);
		}
		for (SenseUpdate sense : update.getUpdatedSenses().values()) {
			updateSense(sense);
		}
		for (SenseIdValue senseId : update.getRemovedSenses()) {
			removeSense(senseId);
		}
		for (FormDocument form : update.getAddedForms()) {
			addForm(form);
		}
		for (FormUpdate form : update.getUpdatedForms().values()) {
			updateForm(form);
		}
		for (FormIdValue formId : update.getRemovedForms()) {
			removeForm(formId);
		}
		return this;
	}

	@Override
	public LexemeUpdate build() {
		return Datamodel.makeLexemeUpdate(getEntityId(), getBaseRevisionId(),
				language, lexicalCategory, lemmas, statements,
				addedSenses, updatedSenses.values(), removedSenses,
				addedForms, updatedForms.values(), removedForms);
	}

}
