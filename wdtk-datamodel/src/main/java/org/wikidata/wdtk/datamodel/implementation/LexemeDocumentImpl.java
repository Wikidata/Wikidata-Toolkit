package org.wikidata.wdtk.datamodel.implementation;

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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Jackson implementation of {@link LexemeDocument}.
 *
 * @author Thomas Pellissier Tanon
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LexemeDocumentImpl extends StatementDocumentImpl implements LexemeDocument {

	private ItemIdValue lexicalCategory;

	private ItemIdValue language;

	private Map<String,MonolingualTextValue> lemmas;

	private List<FormDocument> forms;

	private List<SenseDocument> senses;

	/**
	 * Constructor.
	 *
	 * @param id
	 *            the id of the le that data is about
	 * @param lexicalCategory
	 *            the lexical category of the lexeme
	 * @param language
	 *            the language of the lexeme
	 * @param lemmas
	 *            the list of lemmas of this lexeme, with at most one
	 *            lemma for each language code
	 * @param statements
	 *            the list of statement groups of this lexeme; all of them must
	 *            have the given id as their subject
	 * @param forms
	 *            the list of the forms of this lexeme.
	 * @param senses
	 *            the list of the senses of this lexeme.
	 * @param revisionId
	 *            the revision ID or 0 if not known; see
	 *            {@link EntityDocument#getRevisionId()}
	 */
	LexemeDocumentImpl(
			LexemeIdValue id,
			ItemIdValue lexicalCategory,
			ItemIdValue language,
			List<MonolingualTextValue> lemmas,
			List<StatementGroup> statements,
			List<FormDocument> forms,
			List<SenseDocument> senses,
			long revisionId) {
		super(id, statements, revisionId);
		Validate.notNull(lexicalCategory, "Lexeme lexical category should not be null");
		this.lexicalCategory = lexicalCategory;
		Validate.notNull(language, "Lexeme language should not be null");
		this.language = language;
		Validate.notNull(lemmas, "Lexeme lemmas should not be null");
		if(lemmas.isEmpty()) {
			throw new IllegalArgumentException("Lexemes should have at least one lemma");
		}
		this.lemmas = constructTermMap(lemmas);
		this.forms = (forms == null) ? Collections.emptyList() : forms;
		this.senses = (senses == null) ? Collections.emptyList() : senses;
	}

	/**
	 * Constructor. Creates an object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	@JsonCreator
	LexemeDocumentImpl(
			@JsonProperty("id") String jsonId,
			@JsonProperty("lexicalCategory") String lexicalCategory,
			@JsonProperty("language") String language,
			@JsonProperty("lemmas") @JsonDeserialize(contentAs=TermImpl.class) Map<String, MonolingualTextValue> lemmas,
			@JsonProperty("claims") Map<String, List<StatementImpl.PreStatement>> claims,
			@JsonProperty("forms") @JsonDeserialize(contentAs=FormDocumentImpl.class) List<FormDocument> forms,
			@JsonProperty("senses") @JsonDeserialize(contentAs=SenseDocumentImpl.class) List<SenseDocument> senses,
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
		super(jsonId, claims, revisionId, siteIri);
		Validate.notNull(lexicalCategory, "Lexeme lexical category should not be null");
		this.lexicalCategory = new ItemIdValueImpl(lexicalCategory, siteIri);
		Validate.notNull(language, "Lexeme language should not be null");
		this.language = new ItemIdValueImpl(language, siteIri);
		Validate.notNull(lemmas, "Lexeme lemmas should not be null");
		if(lemmas.isEmpty()) {
			throw new IllegalArgumentException("Lexemes should have at least one lemma");
		}
		this.lemmas = lemmas;
		this.forms = (forms == null) ? Collections.emptyList() : forms;
		this.senses = (senses == null) ? Collections.emptyList() : senses;
	}
	
	/**
	 * Copy constructor, used when creating modified copies of lexemes.
	 */
	private LexemeDocumentImpl(
			LexemeIdValue id,
			ItemIdValue lexicalCategory,
			ItemIdValue language,
			Map<String, MonolingualTextValue> lemmas,
			Map<String, List<Statement>> statements,
			List<FormDocument> forms,
			long revisionId) {
		super(id, statements, revisionId);
		this.lexicalCategory = lexicalCategory;
		this.language = language;
		this.lemmas = lemmas;
		this.forms = forms;
	}

	private static Map<String, MonolingualTextValue> constructTermMap(List<MonolingualTextValue> terms) {
		Map<String, MonolingualTextValue> map = new HashMap<>();
		for(MonolingualTextValue term : terms) {
			String language = term.getLanguageCode();
			if(map.containsKey(language)) {
				throw new IllegalArgumentException("Multiple terms provided for the same language.");
			}
			// We need to make sure the terms are of the right type, otherwise they will not
			// be serialized correctly.
			map.put(language, (term instanceof TermImpl) ? term : new TermImpl(term.getLanguageCode(), term.getText()));
		}
		return map;
	}

	@JsonIgnore
	@Override
	public LexemeIdValue getEntityId() {
		return new LexemeIdValueImpl(this.entityId, this.siteIri);
	}

	@JsonIgnore
	@Override
	public ItemIdValue getLexicalCategory() {
		return lexicalCategory;
	}

	@JsonProperty("lexicalCategory")
	String getJsonLexicalCategory() {
		return lexicalCategory.getId();
	}

	@JsonIgnore
	@Override
	public ItemIdValue getLanguage() {
		return language;
	}

	@JsonProperty("language")
	String getJsonLanguage() {
		return language.getId();
	}

	@JsonProperty("lemmas")
	@Override
	public Map<String, MonolingualTextValue> getLemmas() {
		return lemmas;
	}

	@JsonProperty("forms")
	@Override
	public List<FormDocument> getForms() {
		return forms;
	}


	@JsonProperty("senses")
	@Override
	public List<SenseDocument> getSenses() {
		return senses;
	}

	@JsonIgnore
	@Override
	public FormDocument getForm(FormIdValue formId) {
		for(FormDocument form : forms) {
			if(form.getEntityId().equals(formId)) {
				return form;
			}
		}
		throw new IndexOutOfBoundsException("There is no " + formId + " in the lexeme.");
	}

	@JsonIgnore
	@Override
	public SenseDocument getSense(SenseIdValue senseId) {
		for(SenseDocument sense : senses) {
			if(sense.getEntityId().equals(senseId)) {
				return sense;
			}
		}
		throw new IndexOutOfBoundsException("There is no " + senseId + " in the lexeme.");
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsLexemeDocument(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
	/*
	 * LexemeIdValue id,
			ItemIdValue lexicalCategory,
			ItemIdValue language,
			Map<String, MonolingualTextValue> lemmas,
			Map<String, List<Statement>> statements,
			List<FormDocument> forms,
			long revisionId
			(non-Javadoc)
	 * @see org.wikidata.wdtk.datamodel.interfaces.StatementDocument#withStatement(org.wikidata.wdtk.datamodel.interfaces.Statement)
	 */
	
	@Override
	public LexemeDocument withStatement(Statement statement) {
		Map<String, List<Statement>> newGroups = addStatementToGroups(statement, claims);
		return new LexemeDocumentImpl(getEntityId(), lexicalCategory,
				language, lemmas, newGroups, forms, revisionId);
	}

	@Override
	public LexemeDocument withoutStatementIds(Set<String> statementIds) {
		Map<String, List<Statement>> newGroups = removeStatements(statementIds, claims);
		return new LexemeDocumentImpl(getEntityId(), lexicalCategory,
				language, lemmas, newGroups, forms, revisionId);
	}
	
	@Override
	public LexemeDocument withRevisionId(long newRevisionId) {
		return new LexemeDocumentImpl(getEntityId(), lexicalCategory,
				language, lemmas, claims, forms, newRevisionId);
	}
}
