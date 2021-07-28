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

import java.util.Objects;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LabeledStatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson implementation of {@link LabeledStatementDocumentUpdate}.
 */
public abstract class LabeledDocumentUpdateImpl extends StatementDocumentUpdateImpl
		implements LabeledStatementDocumentUpdate {

	@JsonIgnore
	private final TermUpdate labels;

	/**
	 * Initializes new entity update.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @param revisionId
	 *            base entity revision to be updated or zero if not available
	 * @param labels
	 *            changes in entity labels, possibly empty
	 * @param statements
	 *            changes in entity statements, possibly empty
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	protected LabeledDocumentUpdateImpl(
			EntityIdValue entityId,
			long revisionId,
			TermUpdate labels,
			StatementUpdate statements) {
		super(entityId, revisionId, statements);
		Objects.requireNonNull(labels, "Label update cannot be null.");
		this.labels = labels;
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return super.isEmpty() && labels.isEmpty();
	}

	@JsonIgnore
	@Override
	public TermUpdate getLabels() {
		return labels;
	}

	@JsonProperty("labels")
	@JsonInclude(Include.NON_NULL)
	TermUpdate getJsonLabels() {
		return labels.isEmpty() ? null : labels;
	}

}
