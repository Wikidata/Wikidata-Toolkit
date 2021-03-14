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
import java.util.Optional;
import java.util.Set;

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
 * Jackson implementation of {@link LexemeUpdate}.
 */
public class LexemeUpdateImpl extends StatementDocumentUpdateImpl implements LexemeUpdate {

	private final ItemIdValue language;
	private final ItemIdValue lexicalCategory;
	private final MultilingualTextUpdate lemmas;
	private final List<SenseDocument> addedSenses;
	private final Map<SenseIdValue, SenseUpdate> updatedSenses;
	private final Set<SenseIdValue> removedSenses;
	private final List<FormDocument> addedForms;
	private final Map<FormIdValue, FormUpdate> updatedForms;
	private final Set<FormIdValue> removedForms;

	/**
	 * Initializes new entity update.
	 * 
	 * @param entityId
	 *            ID of the lexeme that is to be updated
	 * @param document
	 *            lexeme revision to be updated or {@code null} if not available
	 * @param language
	 *            new lexeme language or {@code null} for no change
	 * @param lexicalCategory
	 *            new lexical category of the lexeme or {@code null} for no change
	 * @param lemmas
	 *            changes in lemmas or {@code null} for no change
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
	protected LexemeUpdateImpl(
			LexemeIdValue entityId,
			LexemeDocument document,
			ItemIdValue language,
			ItemIdValue lexicalCategory,
			MultilingualTextUpdate lemmas,
			StatementUpdate statements,
			Collection<SenseDocument> addedSenses,
			Collection<SenseUpdate> updatedSenses,
			Collection<SenseIdValue> removedSenses,
			Collection<FormDocument> addedForms,
			Collection<FormUpdate> updatedForms,
			Collection<FormIdValue> removedForms) {
		super(entityId, document, statements);
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

	@Override
	public LexemeIdValue getEntityId() {
		return (LexemeIdValue) super.getEntityId();
	}

	@Override
	public LexemeDocument getCurrentDocument() {
		return (LexemeDocument) super.getCurrentDocument();
	}

	@Override
	public Optional<ItemIdValue> getLanguage() {
		return Optional.ofNullable(language);
	}

	@Override
	public Optional<ItemIdValue> getLexicalCategory() {
		return Optional.ofNullable(lexicalCategory);
	}

	@Override
	public Optional<MultilingualTextUpdate> getLemmas() {
		return Optional.ofNullable(lemmas);
	}

	@Override
	public List<SenseDocument> getAddedSenses() {
		return addedSenses;
	}

	@Override
	public Map<SenseIdValue, SenseUpdate> getUpdatedSenses() {
		return updatedSenses;
	}

	@Override
	public Set<SenseIdValue> getRemovedSenses() {
		return removedSenses;
	}

	@Override
	public List<FormDocument> getAddedForms() {
		return addedForms;
	}

	@Override
	public Map<FormIdValue, FormUpdate> getUpdatedForms() {
		return updatedForms;
	}

	@Override
	public Set<FormIdValue> getRemovedForms() {
		return removedForms;
	}

}
