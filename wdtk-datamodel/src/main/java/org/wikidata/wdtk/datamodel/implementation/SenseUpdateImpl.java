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

import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

/**
 * Jackson implementation of {@link SenseUpdate}.
 */
public class SenseUpdateImpl extends StatementUpdateImpl implements SenseUpdate {

	private final Map<String, MonolingualTextValue> modifiedGlosses;
	private final Set<String> removedGlosses;

	/**
	 * Initializes new sense update.
	 * 
	 * @param entityId
	 *            ID of the sense that is to be updated
	 * @param document
	 *            sense revision to be updated or {@code null} if not available
	 * @param modifiedGlosses
	 *            added or changed sense glosses
	 * @param removedGlosses
	 *            language codes of removed sense glosses
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
	protected SenseUpdateImpl(
			SenseIdValue entityId,
			SenseDocument document,
			Collection<MonolingualTextValue> modifiedGlosses,
			Collection<String> removedGlosses,
			Collection<Statement> addedStatements,
			Collection<Statement> replacedStatements,
			Collection<String> removedStatements) {
		super(entityId, document, addedStatements, replacedStatements, removedStatements);
		this.modifiedGlosses = Collections.unmodifiableMap(
				modifiedGlosses.stream().collect(toMap(r -> r.getLanguageCode(), r -> r)));
		this.removedGlosses = Collections.unmodifiableSet(new HashSet<>(removedGlosses));
	}

	@Override
	public SenseIdValue getEntityId() {
		return (SenseIdValue) super.getEntityId();
	}

	@Override
	public SenseDocument getCurrentDocument() {
		return (SenseDocument) super.getCurrentDocument();
	}

	@Override
	public Map<String, MonolingualTextValue> getModifiedGlosses() {
		return modifiedGlosses;
	}

	@Override
	public Set<String> getRemovedGlosses() {
		return removedGlosses;
	}

}
