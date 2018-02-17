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

import java.util.Iterator;
import java.util.List;

/**
 * Interface for Wikibase Statments. A Statement is the main information object
 * entered by users in Wikidata. It refers to a {@link Claim}, on which it
 * provides additional information about references and ranking.
 *
 * @author Markus Kroetzsch
 *
 */
public interface Statement {

	/**
	 * Get the Claim object that this statement refers to.
	 *
	 * @return the claim that this statement refers to
	 */
	Claim getClaim();

	/**
	 * The subject that the claim refers to, e.g., the id of "Berlin".
	 *
	 * @return EntityId of the subject
	 */
	EntityIdValue getSubject();

	/**
	 * Main Snak of the statement. This Snak refers directly to the subject,
	 * e.g., the {@link ValueSnak} "Population: 3000000".
	 *
	 * @return the main snak
	 */
	Snak getMainSnak();

	/**
	 * Groups of auxiliary Snaks, also known as qualifiers, that provide
	 * additional context information for this claim. For example, "as of: 2014"
	 * might be a temporal context given for a claim that provides a population
	 * number. The snaks are grouped by the property that they use.
	 *
	 * @return list of snak groups
	 */
	List<SnakGroup> getQualifiers();

	/**
	 * Returns an iterator over all qualifiers, without considering qualifier
	 * groups. The relative order of qualifiers is preserved.
	 *
	 * @return iterator over all qualifier snaks
	 */
	Iterator<Snak> getAllQualifiers();

	/**
	 * @see StatementRank
	 * @return the rank of the Statement
	 */
	StatementRank getRank();

	/**
	 * Get a list of references for this Statement. Each reference is
	 * represented by a list of Snaks, which provide information about the
	 * reference.
	 *
	 * @return the list of references
	 */
	List<Reference> getReferences();

	/**
	 * Return the id used to identify this statement.
	 * <p>
	 * Statement ids are used by Wikibase to allow certain interactions though
	 * the API, especially the atomic modification of Statements (modifications
	 * of statements can be viewed as deletions followed by insertions, but
	 * doing this in several steps though the API is not practical). In the
	 * current Wikibase implementation, the id is a string that begins with the
	 * (sometimes lowercased) local ID of the subject of the statement, followed
	 * by a dollar sign and a randomly generated UUID. Thus statements of
	 * different subjects can never have the same id, and it is extremely
	 * unlikely that two statements of the one subject ever have the same id.
	 * However, it is possible that two statements with the same content differ
	 * in their id, since the id is not based on the content.
	 * <p>
	 * Wikidata Toolkit generally requires ids to be specified but you can use
	 * the empty string to indicate that a statement has no id. This will also
	 * be respected when serializing data as JSON, i.e., rather than setting the
	 * statement id to an empty string in JSON, the key will simply be omitted.
	 * This is useful for creating new statements through the API.
	 * <p>
	 * Callers should not make any assumptions about the stability of statement
	 * ids over time, or about the internal format of the ids.
	 *
	 * @return the statement string id
	 */
	String getStatementId();

	/**
	 * Convenience method to get the value of the statement's claim's main snak,
	 * or null if there is none.
	 *
	 * @return main value of the statement, or null
	 */
	Value getValue();
	
	/**
	 * Returns the same statement, but with a different identifier.
	 * This is useful when the existing identifier was empty and we need to 
	 * attribute one before creating the statement in a remote Wikibase instance.
	 */
	Statement withStatementId(String id);
}
