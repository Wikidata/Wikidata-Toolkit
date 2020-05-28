package org.wikidata.wdtk.datamodel.interfaces;

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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for datasets that describe lexemes.
 *
 * @author Thomas Pellissier Tanon
 *
 */
public interface LexemeDocument extends StatementDocument {

	/**
	 * Returns the ID of the entity that the data refers to
	 *
	 * @return lexeme id
	 */
	@Override
	LexemeIdValue getEntityId();

	/**
	 * Return the ID of the lexical category to which the lexeme belongs
	 * (noun, verb...)
	 *
	 * @return item id
	 */
	ItemIdValue getLexicalCategory();

	/**
	 * Return the ID of the language to which the lexeme belongs
	 * (French, British English...)
	 *
	 * @return item id
	 */
	ItemIdValue getLanguage();

	/**
	 * Return the human readable representations of the lexeme indexed by Wikimedia language code
	 *
	 * @return a map from Wikimedia language code to the lemma
	 */
	Map<String,MonolingualTextValue> getLemmas();

	/**
	 * Return the lexeme forms
	 *
	 * @return the list of forms
	 */
	List<FormDocument> getForms();

	/**
	 * Return the inner form having the given id
	 *
	 * @throws IndexOutOfBoundsException if there is no form with this id in the document
	 */
	FormDocument getForm(FormIdValue formId);

	/**
	 * Return the lexeme senses
	 *
	 * @return the list of senses
	 */
	List<SenseDocument> getSenses();

	/**
	 * Return the inner sense having the given id
	 *
	 * @throws IndexOutOfBoundsException if there is no sense with this id in the document
	 */
	SenseDocument getSense(SenseIdValue formId);

	/**
	 * Returns a copy of this document with an updated revision id.
	 */
	@Override
	LexemeDocument withRevisionId(long newRevisionId);

	LexemeDocument withLexicalCategory(ItemIdValue newLexicalCategory);

	LexemeDocument withLanguage(ItemIdValue newLanguage);

	LexemeDocument withLemma(MonolingualTextValue lemma);

	/**
	 * Returns a new version of this document which includes the
	 * statement provided. If the identifier of this statement matches
	 * that of any other statement for the same property, then the
	 * existing statement will be replaced by the new one. Otherwise,
	 * the new statement will be added at the end of the list of statements
	 * in this group.
	 *
	 * @param statement
	 * 		the statement to add or update in the document
	 */
	@Override
	LexemeDocument withStatement(Statement statement);

	/**
	 * Returns a new version of this document where all statements matching
	 * any of the statement ids provided have been removed. These statements
	 * can use different properties.
	 *
	 * @param statementIds
	 *       the identifiers of the statements to remove
	 */
	@Override
	LexemeDocument withoutStatementIds(Set<String> statementIds);

	/**
	 * Creates a new {@link FormDocument} for this lexeme.
	 * The form is not added to the {@link LexemeDocument} object,
	 * it should be done with {@link LexemeDocument#withForm}.
	 */
	FormDocument createForm(List<MonolingualTextValue> representations);

	/**
	 * Adds a {@link FormDocument} to this lexeme.
	 * The form id should be prefixed with the lexeme id.
	 */
	LexemeDocument withForm(FormDocument form);

	/**
	 * Creates a new {@link SenseDocument} for this Lexeme.
	 * The form is not added to the {@link LexemeDocument} object,
	 * it should be done with {@link LexemeDocument#withSense}.
	 */
	SenseDocument createSense(List<MonolingualTextValue> glosses);

	/**
	 * Adds a {@link SenseDocument} to this lexeme.
	 * The sense id should be prefixed with the lexeme id.
	 */
	LexemeDocument withSense(SenseDocument sense);

}
