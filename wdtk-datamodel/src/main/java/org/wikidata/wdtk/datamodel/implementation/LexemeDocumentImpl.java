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
			long revisionId) {
		super(id, statements, revisionId);
		Validate.notNull(lexicalCategory, "Lexeme lexical category should not be null");
		this.lexicalCategory = lexicalCategory;
		Validate.notNull(language, "Lexeme language should not be null");
		this.language = language;
		this.lemmas = (lemmas == null) ? Collections.emptyMap() : constructTermMap(lemmas);
		this.forms = (forms == null) ? Collections.emptyList() : forms;
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
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
		super(jsonId, claims, revisionId, siteIri);
		Validate.notNull(lexicalCategory, "Lexeme lexical category should not be null");
		this.lexicalCategory = new ItemIdValueImpl(lexicalCategory, siteIri);
		Validate.notNull(language, "Lexeme language should not be null");
		this.language = new ItemIdValueImpl(language, siteIri);
		this.lemmas = (lemmas == null) ? Collections.emptyMap() : lemmas;
		this.forms = (forms == null) ? Collections.emptyList() : forms;
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
}
