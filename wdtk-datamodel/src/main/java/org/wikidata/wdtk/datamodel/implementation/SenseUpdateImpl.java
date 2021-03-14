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

import java.util.Collection;
import java.util.Optional;

import org.wikidata.wdtk.datamodel.interfaces.MultilingualTextUpdate;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

/**
 * Jackson implementation of {@link SenseUpdate}.
 */
public class SenseUpdateImpl extends StatementUpdateImpl implements SenseUpdate {

	private final MultilingualTextUpdate glosses;

	/**
	 * Initializes new sense update.
	 * 
	 * @param entityId
	 *            ID of the sense that is to be updated
	 * @param document
	 *            sense revision to be updated or {@code null} if not available
	 * @param glosses
	 *            changes in sense glosses or {@code null} for no change
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
			MultilingualTextUpdate glosses,
			Collection<Statement> addedStatements,
			Collection<Statement> replacedStatements,
			Collection<String> removedStatements) {
		super(entityId, document, addedStatements, replacedStatements, removedStatements);
		this.glosses = glosses;
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
	public Optional<MultilingualTextUpdate> getGlosses() {
		return Optional.ofNullable(glosses);
	}

}
