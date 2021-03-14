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

import java.util.Optional;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MultilingualTextUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocumentUpdate;

/**
 * Jackson implementation of {@link TermedStatementDocumentUpdate}.
 */
public abstract class TermedStatementDocumentUpdateImpl extends LabeledStatementDocumentUpdateImpl
		implements TermedStatementDocumentUpdate {

	private final MultilingualTextUpdate descriptions;

	/**
	 * Initializes new entity update.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @param document
	 *            entity revision to be updated or {@code null} if not available
	 * @param labels
	 *            changes in entity labels or {@code null} for no change
	 * @param descriptions
	 *            changes in entity descriptions or {@code null} for no change
	 * @param statements
	 *            changes in entity statements, possibly empty
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	protected TermedStatementDocumentUpdateImpl(
			EntityIdValue entityId,
			TermedStatementDocument document,
			MultilingualTextUpdate labels,
			MultilingualTextUpdate descriptions,
			StatementUpdate statements) {
		super(entityId, document, labels, statements);
		this.descriptions = descriptions;
	}

	@Override
	public TermedStatementDocument getCurrentDocument() {
		return (TermedStatementDocument) super.getCurrentDocument();
	}

	@Override
	public Optional<MultilingualTextUpdate> getDescriptions() {
		return Optional.ofNullable(descriptions);
	}

}
