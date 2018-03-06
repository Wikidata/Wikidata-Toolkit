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
 * Interface for Wikidata claims. Claims consist of those parts of Wikibase
 * Statements that express a claim about a subject entity, such as the claim
 * that Berlin has 3 million inhabitants. Additional information, such as
 * references and ranks, are not part of the claim.
 *
 * @author Markus Kroetzsch
 *
 */
public interface Claim {

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
	 * Convenience method to get the value of the claim's main snak, or null if
	 * there is none.
	 *
	 * @return main value of the claim, or null
	 */
	Value getValue();
}
