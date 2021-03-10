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

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

/**
 * Jackson implementation of {@link ItemUpdate}.
 */
public class ItemUpdateImpl extends TermedStatementUpdateImpl implements ItemUpdate {

	/**
	 * Initializes new item update.
	 * 
	 * @param entityId
	 *            ID of the item that is to be updated
	 * @param document
	 *            item revision to be updated or {@code null} if not available
	 * @param modifiedLabels
	 *            added or changed entity labels
	 * @param removedLabels
	 *            language codes of removed entity labels
	 * @param modifiedDescriptions
	 *            added or changed entity descriptions
	 * @param removedDescriptions
	 *            language codes of removed entity descriptions
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
	protected ItemUpdateImpl(
			ItemIdValue entityId,
			ItemDocument document,
			Collection<MonolingualTextValue> modifiedLabels,
			Collection<String> removedLabels,
			Collection<MonolingualTextValue> modifiedDescriptions,
			Collection<String> removedDescriptions,
			Collection<Statement> addedStatements,
			Collection<Statement> replacedStatements,
			Collection<String> removedStatements) {
		super(entityId, document, modifiedLabels, removedLabels, modifiedDescriptions, removedDescriptions,
				addedStatements, replacedStatements, removedStatements);
	}

	@Override
	public ItemIdValue getEntityId() {
		return (ItemIdValue) super.getEntityId();
	}

	@Override
	public ItemDocument getCurrentDocument() {
		return (ItemDocument) super.getCurrentDocument();
	}

}
