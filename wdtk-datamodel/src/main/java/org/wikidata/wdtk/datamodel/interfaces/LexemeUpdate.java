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
package org.wikidata.wdtk.datamodel.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Collection of changes that can be applied to lexeme entity.
 */
public interface LexemeUpdate extends StatementDocumentUpdate {

	@Override
	LexemeIdValue getEntityId();

	/**
	 * Returns new lexeme language assigned in this update. If language code is not
	 * changing, this method returns {@link Optional#empty()}.
	 * 
	 * @return new lexeme language or {@link Optional#empty()} if it is not changing
	 */
	Optional<ItemIdValue> getLanguage();

	/**
	 * Returns new lexical category assigned to the lexeme in this update. If
	 * lexical category is not changing, this method returns
	 * {@link Optional#empty()}.
	 * 
	 * @return new lexical category or {@link Optional#empty()} if it is not
	 *         changing
	 */
	Optional<ItemIdValue> getLexicalCategory();

	/**
	 * Returns changes in lemmas.
	 * 
	 * @return update of lemmas, possibly empty
	 */
	TermUpdate getLemmas();

	/**
	 * Returns new forms added to the lexeme in this update. Existing forms are
	 * preserved by default.
	 * 
	 * @return list of new forms
	 */
	List<FormDocument> getAddedForms();

	/**
	 * Returns lexeme forms modified in this update. Forms not listed here are
	 * preserved by default.
	 * 
	 * @return modified forms indexed by ID
	 */
	Map<FormIdValue, FormUpdate> getUpdatedForms();

	/**
	 * Returns IDs of forms removed from the lexeme in this update.
	 * 
	 * @return IDs of removed lexeme forms
	 */
	Set<FormIdValue> getRemovedForms();

	/**
	 * Returns new senses added to the lexeme in this update. Existing senses are
	 * preserved by default.
	 * 
	 * @return list of new senses
	 */
	List<SenseDocument> getAddedSenses();

	/**
	 * Returns lexeme senses modified in this update. Senses not listed here are
	 * preserved by default.
	 * 
	 * @return modified senses indexed by ID
	 */
	Map<SenseIdValue, SenseUpdate> getUpdatedSenses();

	/**
	 * Returns IDs of senses removed from the lexeme in this update.
	 * 
	 * @return IDs of removed lexeme senses
	 */
	Set<SenseIdValue> getRemovedSenses();

}
