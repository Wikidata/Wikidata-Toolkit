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

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeUpdate;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson implementation of {@link LexemeUpdate}.
 */
public class LexemeUpdateImpl extends StatementDocumentUpdateImpl implements LexemeUpdate {

	@JsonIgnore
	private final ItemIdValue language;
	@JsonIgnore
	private final ItemIdValue lexicalCategory;
	@JsonIgnore
	private final TermUpdate lemmas;
	@JsonIgnore
	private final List<SenseDocument> addedSenses;
	@JsonIgnore
	private final Map<SenseIdValue, SenseUpdate> updatedSenses;
	@JsonIgnore
	private final Set<SenseIdValue> removedSenses;
	@JsonIgnore
	private final List<FormDocument> addedForms;
	@JsonIgnore
	private final Map<FormIdValue, FormUpdate> updatedForms;
	@JsonIgnore
	private final Set<FormIdValue> removedForms;

	/**
	 * Initializes new entity update.
	 * 
	 * @param entityId
	 *            ID of the lexeme that is to be updated
	 * @param revisionId
	 *            base lexeme revision to be updated or zero if not available
	 * @param language
	 *            new lexeme language or {@code null} for no change
	 * @param lexicalCategory
	 *            new lexical category of the lexeme or {@code null} for no change
	 * @param lemmas
	 *            changes in lemmas, possibly empty
	 * @param statements
	 *            changes in entity statements, possibly empty
	 * @param addedSenses
	 *            added senses
	 * @param updatedSenses
	 *            updated senses
	 * @param removedSenses
	 *            IDs of removed senses
	 * @param addedForms
	 *            added forms
	 * @param updatedForms
	 *            updated forms
	 * @param removedForms
	 *            IDs of removed forms
	 * @throws NullPointerException
	 *             if any required parameter or its item is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	public LexemeUpdateImpl(
			LexemeIdValue entityId,
			long revisionId,
			ItemIdValue language,
			ItemIdValue lexicalCategory,
			TermUpdate lemmas,
			StatementUpdate statements,
			Collection<SenseDocument> addedSenses,
			Collection<SenseUpdate> updatedSenses,
			Collection<SenseIdValue> removedSenses,
			Collection<FormDocument> addedForms,
			Collection<FormUpdate> updatedForms,
			Collection<FormIdValue> removedForms) {
		super(entityId, revisionId, statements);
		Validate.isTrue(language == null || !language.isPlaceholder(), "Language cannot be a placeholder ID.");
		this.language = language;
		Validate.isTrue(
				lexicalCategory == null || !lexicalCategory.isPlaceholder(),
				"Lexical category cannot be a placeholder ID.");
		this.lexicalCategory = lexicalCategory;
		Objects.requireNonNull(lemmas, "Lemma update cannot be null.");
		this.lemmas = lemmas;
		Objects.requireNonNull(addedSenses, "List of added senses cannot be null.");
		for (SenseDocument sense : addedSenses) {
			Objects.requireNonNull(sense, "Added sense cannot be null.");
			Validate.isTrue(sense.getEntityId().isPlaceholder(), "Added sense must have placeholder ID.");
		}
		this.addedSenses = Collections.unmodifiableList(new ArrayList<>(addedSenses));
		Objects.requireNonNull(updatedSenses, "List of sense updates cannot be null.");
		for (SenseUpdate update : updatedSenses) {
			Objects.requireNonNull(update, "Sense update cannot be null.");
			Validate.isTrue(update.getBaseRevisionId() == revisionId,
					"Nested sense update must have the same revision ID as lexeme update.");
		}
		Validate.isTrue(
				updatedSenses.stream().map(s -> s.getEntityId()).distinct().count() == updatedSenses.size(),
				"Cannot apply two updates to the same sense.");
		this.updatedSenses = Collections.unmodifiableMap(updatedSenses.stream()
				.filter(s -> !s.isEmpty())
				.collect(toMap(s -> s.getEntityId(), s -> s)));
		Objects.requireNonNull(removedSenses, "List of removed sense IDs cannot be null.");
		for (SenseIdValue senseId : removedSenses) {
			Objects.requireNonNull(senseId, "Removed sense cannot have null ID.");
			Validate.isTrue(!senseId.isPlaceholder(), "Removed sense cannot have placeholder ID.");
		}
		Validate.isTrue(
				removedSenses.stream().distinct().count() == removedSenses.size(),
				"Cannot remove the same sense twice.");
		this.removedSenses = Collections.unmodifiableSet(new HashSet<>(removedSenses));
		Validate.isTrue(
				updatedSenses.stream().noneMatch(s -> this.removedSenses.contains(s.getEntityId())),
				"Cannot remove sense that is being updated.");
		Objects.requireNonNull(addedForms, "List of added forms cannot be null.");
		for (FormDocument form : addedForms) {
			Objects.requireNonNull(form, "Added form cannot be null.");
			Validate.isTrue(form.getEntityId().isPlaceholder(), "Added form must have placeholder ID.");
		}
		this.addedForms = Collections.unmodifiableList(new ArrayList<>(addedForms));
		Objects.requireNonNull(updatedForms, "List of form updates cannot be null.");
		for (FormUpdate update : updatedForms) {
			Objects.requireNonNull(update, "Form update cannot be null.");
			Validate.isTrue(update.getBaseRevisionId() == revisionId,
					"Nested form update must have the same revision ID as lexeme update.");
		}
		Validate.isTrue(
				updatedForms.stream().map(s -> s.getEntityId()).distinct().count() == updatedForms.size(),
				"Cannot apply two updates to the same form.");
		this.updatedForms = Collections.unmodifiableMap(updatedForms.stream()
				.filter(f -> !f.isEmpty())
				.collect(toMap(f -> f.getEntityId(), f -> f)));
		Objects.requireNonNull(removedForms, "List of removed form IDs cannot be null.");
		for (FormIdValue formId : removedForms) {
			Objects.requireNonNull(formId, "Removed form cannot have null ID.");
			Validate.isTrue(!formId.isPlaceholder(), "Removed form cannot have placeholder ID.");
		}
		Validate.isTrue(
				removedForms.stream().distinct().count() == removedForms.size(),
				"Cannot remove the same form twice.");
		this.removedForms = Collections.unmodifiableSet(new HashSet<>(removedForms));
		Validate.isTrue(
				updatedForms.stream().noneMatch(s -> this.removedForms.contains(s.getEntityId())),
				"Cannot remove form that is being updated.");
	}

	@JsonIgnore
	@Override
	public LexemeIdValue getEntityId() {
		return (LexemeIdValue) super.getEntityId();
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return super.isEmpty() && language == null && lexicalCategory == null && lemmas.isEmpty()
				&& addedSenses.isEmpty() && updatedSenses.isEmpty() && removedSenses.isEmpty()
				&& addedForms.isEmpty() && updatedForms.isEmpty() && removedForms.isEmpty();
	}

	@JsonIgnore
	@Override
	public Optional<ItemIdValue> getLanguage() {
		return Optional.ofNullable(language);
	}

	@JsonProperty("language")
	@JsonInclude(Include.NON_NULL)
	String getJsonLanguage() {
		return language != null ? language.getId() : null;
	}

	@JsonIgnore
	@Override
	public Optional<ItemIdValue> getLexicalCategory() {
		return Optional.ofNullable(lexicalCategory);
	}

	@JsonProperty("lexicalCategory")
	@JsonInclude(Include.NON_NULL)
	String getJsonLexicalCategory() {
		return lexicalCategory != null ? lexicalCategory.getId() : null;
	}

	@JsonIgnore
	@Override
	public TermUpdate getLemmas() {
		return lemmas;
	}

	@JsonProperty("lemmas")
	@JsonInclude(Include.NON_NULL)
	TermUpdate getJsonLemmas() {
		return lemmas.isEmpty() ? null : lemmas;
	}

	@JsonIgnore
	@Override
	public List<SenseDocument> getAddedSenses() {
		return addedSenses;
	}

	@JsonIgnore
	@Override
	public Map<SenseIdValue, SenseUpdate> getUpdatedSenses() {
		return updatedSenses;
	}

	@JsonIgnore
	@Override
	public Set<SenseIdValue> getRemovedSenses() {
		return removedSenses;
	}

	@JsonIgnore
	@Override
	public List<FormDocument> getAddedForms() {
		return addedForms;
	}

	@JsonIgnore
	@Override
	public Map<FormIdValue, FormUpdate> getUpdatedForms() {
		return updatedForms;
	}

	@JsonIgnore
	@Override
	public Set<FormIdValue> getRemovedForms() {
		return removedForms;
	}

	static class AddedSense extends SenseDocumentImpl {

		AddedSense(SenseDocument sense) {
			super(SenseIdValue.NULL, new ArrayList<>(sense.getGlosses().values()),
					sense.getStatementGroups(), sense.getRevisionId());
		}

		@JsonProperty("add")
		public String getAddCommand() {
			return "";
		}

	}

	static class RemovedSense {

		@JsonIgnore
		private final SenseIdValue id;

		RemovedSense(SenseIdValue id) {
			this.id = id;
		}

		@JsonProperty
		String getId() {
			return id.getId();
		}

		@JsonProperty("remove")
		String getRemoveCommand() {
			return "";
		}

	}

	@JsonProperty
	@JsonInclude(Include.NON_EMPTY)
	List<Object> getSenses() {
		List<Object> list = new ArrayList<>();
		for (SenseDocument sense : addedSenses) {
			list.add(new AddedSense(sense));
		}
		list.addAll(updatedSenses.values());
		for (SenseIdValue id : removedSenses) {
			list.add(new RemovedSense(id));
		}
		return list;
	}

	static class AddedForm extends FormDocumentImpl {

		AddedForm(FormDocument form) {
			super(FormIdValue.NULL, new ArrayList<>(form.getRepresentations().values()), form.getGrammaticalFeatures(),
					form.getStatementGroups(), form.getRevisionId());
		}

		@JsonProperty("add")
		public String getAddCommand() {
			return "";
		}

	}

	static class RemovedForm {

		@JsonIgnore
		private final FormIdValue id;

		RemovedForm(FormIdValue id) {
			this.id = id;
		}

		@JsonProperty
		String getId() {
			return id.getId();
		}

		@JsonProperty("remove")
		String getRemoveCommand() {
			return "";
		}

	}

	@JsonProperty
	@JsonInclude(Include.NON_EMPTY)
	List<Object> getForms() {
		List<Object> list = new ArrayList<>();
		for (FormDocument form : addedForms) {
			list.add(new AddedForm(form));
		}
		list.addAll(updatedForms.values());
		for (FormIdValue id : removedForms) {
			list.add(new RemovedForm(id));
		}
		return list;
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsLexemeUpdate(this, obj);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

}
