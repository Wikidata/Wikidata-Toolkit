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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LabeledStatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.LabeledStatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

/**
 * Jackson implementation of {@link LabeledStatementUpdate}.
 */
public abstract class LabeledStatementUpdateImpl extends StatementUpdateImpl implements LabeledStatementUpdate {

	private final Map<String, MonolingualTextValue> modifiedLabels;
	private final Set<String> removedLabels;

	/**
	 * Initializes new entity update.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @param document
	 *            entity revision to be updated or {@code null} if not available
	 * @param modifiedLabels
	 *            added or changed entity labels
	 * @param removedLabels
	 *            language codes of removed entity labels
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
	protected LabeledStatementUpdateImpl(
			EntityIdValue entityId,
			LabeledStatementDocument document,
			Collection<MonolingualTextValue> modifiedLabels,
			Collection<String> removedLabels,
			Collection<Statement> addedStatements,
			Collection<Statement> replacedStatements,
			Collection<String> removedStatements) {
		super(entityId, document, addedStatements, replacedStatements, removedStatements);
		this.modifiedLabels = Collections.unmodifiableMap(
				modifiedLabels.stream().collect(toMap(r -> r.getLanguageCode(), r -> r)));
		this.removedLabels = Collections.unmodifiableSet(new HashSet<>(removedLabels));
	}

	@Override
	public LabeledStatementDocument getCurrentDocument() {
		return (LabeledStatementDocument) super.getCurrentDocument();
	}

	@Override
	public Map<String, MonolingualTextValue> getModifiedLabels() {
		return modifiedLabels;
	}

	@Override
	public Set<String> getRemovedLabels() {
		return removedLabels;
	}

}
