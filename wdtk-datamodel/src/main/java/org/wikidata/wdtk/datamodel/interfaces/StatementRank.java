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

/**
 * Enum for the possible ranks of Wikibase Statements. Ranks are used to compare
 * Statements that have the same subject and main-snak property.
 * <p>
 * By default, Statements are of "normal" rank. The rank "preferred" can be
 * given to Statements that should be preferred when using the data without more
 * specific selection criteria (for example, there can be many population
 * numbers for one city, but only the most current/accurate one should be shown
 * by default, hence it should be preferred). The rank "deprecated" is used for
 * Statements that should not normally be considered, but which are still stored
 * for some reason (maybe because their status is disputed or because they
 * record a known wrong claim of a respected source).
 * 
 * @author Markus Kroetzsch
 * 
 */
public enum StatementRank {
	PREFERRED, NORMAL, DEPRECATED
}
