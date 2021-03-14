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
package org.wikidata.wdtk.datamodel.implementation;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

/**
 * Jackson implementation of {@link StatementUpdate}.
 * 
 * @see StatementDocumentUpdateImpl
 */
public class StatementUpdateImpl implements StatementUpdate {

	private final List<Statement> added;
	private final Map<String, Statement> replaced;
	private final Set<String> removed;

	/**
	 * Initializes new statement update.
	 * 
	 * @param added
	 *            added statements
	 * @param replaced
	 *            replaced statements
	 * @param removed
	 *            IDs of removed statements
	 * @throws NullPointerException
	 *             if any parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	public StatementUpdateImpl(Collection<Statement> added,
			Collection<Statement> replaced,
			Collection<String> removed) {
		this.added = Collections.unmodifiableList(new ArrayList<>(added));
		this.replaced = Collections.unmodifiableMap(replaced.stream().collect(toMap(s -> s.getStatementId(), s -> s)));
		this.removed = Collections.unmodifiableSet(new HashSet<>(removed));
	}

	@Override
	public List<Statement> getAddedStatements() {
		return added;
	}

	@Override
	public Map<String, Statement> getReplacedStatements() {
		return replaced;
	}

	@Override
	public Set<String> getRemovedStatements() {
		return removed;
	}

}
