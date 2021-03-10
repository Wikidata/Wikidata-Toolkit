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

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

/**
 * Jackson implementation of {@link StatementUpdate}.
 */
public abstract class StatementUpdateImpl extends EntityUpdateImpl implements StatementUpdate {

	private final List<Statement> addedStatements;
	private final Map<String, Statement> replacedStatements;
	private final Set<String> removedStatements;

	/**
	 * Initializes new entity update.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @param document
	 *            entity revision to be updated or {@code null} if not available
	 * @param addedStatements
	 *            added statements
	 * @param replacedStatements
	 *            replaced statements
	 * @param removedStatements
	 *            IDs of removed statements
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	protected StatementUpdateImpl(
			EntityIdValue entityId,
			StatementDocument document,
			Collection<Statement> addedStatements,
			Collection<Statement> replacedStatements,
			Collection<String> removedStatements) {
		super(entityId, document);
		this.addedStatements = Collections.unmodifiableList(new ArrayList<>(addedStatements));
		this.replacedStatements = Collections.unmodifiableMap(
				replacedStatements.stream().collect(toMap(s -> s.getStatementId(), s -> s)));
		this.removedStatements = Collections.unmodifiableSet(new HashSet<>(removedStatements));
	}

	@Override
	public StatementDocument getCurrentDocument() {
		return (StatementDocument) super.getCurrentDocument();
	}

	@Override
	public List<Statement> getAddedStatements() {
		return addedStatements;
	}

	@Override
	public Map<String, Statement> getReplacedStatements() {
		return replacedStatements;
	}

	@Override
	public Set<String> getRemovedStatements() {
		return removedStatements;
	}

}
