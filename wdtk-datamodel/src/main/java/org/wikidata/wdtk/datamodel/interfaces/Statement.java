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
	List<? extends Reference> getReferences();
}
