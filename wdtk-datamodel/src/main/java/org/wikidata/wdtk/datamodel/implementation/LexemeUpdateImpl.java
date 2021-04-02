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

import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeDocument;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

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
	 * @param revision
	 *            base lexeme revision to be updated or {@code null} if not
	 *            available
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
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	public LexemeUpdateImpl(
			LexemeIdValue entityId,
			LexemeDocument revision,
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
		super(entityId, revision, statements);
		Objects.requireNonNull(lemmas, "Lemma update cannot be null.");
		this.language = language;
		this.lexicalCategory = lexicalCategory;
		this.lemmas = lemmas;
		this.addedSenses = Collections.unmodifiableList(new ArrayList<>(addedSenses));
		this.updatedSenses = Collections.unmodifiableMap(
				updatedSenses.stream().collect(toMap(s -> s.getEntityId(), s -> s)));
		this.removedSenses = Collections.unmodifiableSet(new HashSet<>(removedSenses));
		this.addedForms = Collections.unmodifiableList(new ArrayList<>(addedForms));
		this.updatedForms = Collections.unmodifiableMap(
				updatedForms.stream().collect(toMap(s -> s.getEntityId(), s -> s)));
		this.removedForms = Collections.unmodifiableSet(new HashSet<>(removedForms));
	}

	@JsonIgnore
	@Override
	public LexemeIdValue getEntityId() {
		return (LexemeIdValue) super.getEntityId();
	}

	@JsonIgnore
	@Override
	public LexemeDocument getBaseRevision() {
		return (LexemeDocument) super.getBaseRevision();
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return language == null && lexicalCategory == null && lemmas.isEmpty() && getStatements().isEmpty()
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

}
