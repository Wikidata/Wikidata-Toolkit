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

import java.util.Map;
import java.util.Set;

/**
 * Interface for lexemes senses.
 *
 * @author Thomas Pellissier Tanon
 *
 */
public interface SenseDocument extends StatementDocument {

	/**
	 * Returns the ID of the entity that the data refers to
	 *
	 * @return sense id
	 */
	@Override
	SenseIdValue getEntityId();

	/**
	 * Return the human readable description of the sense indexed by Wikimedia language code
	 *
	 * @return a map from Wikimedia language code to the representations
	 */
	Map<String,MonolingualTextValue> getGlosses();
	
	/**
	 * Returns a copy of this document with an updated revision id.
	 */
	@Override
	SenseDocument withRevisionId(long newRevisionId);

	SenseDocument withGloss(MonolingualTextValue gloss);
	
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
	SenseDocument withStatement(Statement statement);
	
	/**
	 * Returns a new version of this document where all statements matching
	 * any of the statement ids provided have been removed. These statements
	 * can use different properties.
	 * 
	 * @param statementIds
	 *       the identifiers of the statements to remove
	 */
	@Override
	SenseDocument withoutStatementIds(Set<String> statementIds);
}
