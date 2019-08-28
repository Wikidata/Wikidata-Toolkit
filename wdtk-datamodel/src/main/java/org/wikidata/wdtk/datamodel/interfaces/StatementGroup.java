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

import java.util.Collection;
import java.util.List;

/**
 * A statement group represents an ordered list of {@link Statement} objects
 * that use the same subject and the same property in the main snak of their
 * {@link Claim}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public interface StatementGroup extends Collection<Statement> {

	/**
	 * Get the list of Statements of this group.
	 * 
	 * @return a list of Statements
	 */
	List<Statement> getStatements();

	/**
	 * Get the best statements of this group.
	 * These are the statements with rank {@link StatementRank::PREFERRED }
	 * if they exists or the one with rank {@link StatementRank::NORMAL }
	 *
	 * @return a subset of the current StatementGroup, or null if there are no best statements
	 */
	StatementGroup getBestStatements();

	/**
	 * Get the property used in the main snak of the {@link Claim} of each
	 * statement in this group.
	 * 
	 * @return a PropertyIdValue
	 */
	PropertyIdValue getProperty();

	/**
	 * Get the subject used in the {@link Claim} of each statement in this
	 * group.
	 * 
	 * @return an EntityIdValue
	 */
	EntityIdValue getSubject();
	
	/**
	 * Returns a new version of this group, where the given
	 * statement has been added. If some statement in the group
	 * has the same non-empty statement id, then the new statement
	 * will replace it. Otherwise the new statement is appended
	 * at the end of the list.
	 */
	StatementGroup withStatement(Statement statement);
}
