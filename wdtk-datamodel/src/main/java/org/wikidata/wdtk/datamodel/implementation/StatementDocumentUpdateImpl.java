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
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson implementation of {@link StatementDocumentUpdate}.
 * 
 * @see StatementUpdateImpl
 */
public abstract class StatementDocumentUpdateImpl extends EntityUpdateImpl implements StatementDocumentUpdate {

	@JsonIgnore
	private final StatementUpdate statements;

	/**
	 * Initializes new entity update.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @param revision
	 *            base entity revision to be updated or {@code null} if not
	 *            available
	 * @param statements
	 *            changes in entity statements, possibly empty
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	protected StatementDocumentUpdateImpl(
			EntityIdValue entityId,
			StatementDocument revision,
			StatementUpdate statements) {
		super(entityId, revision);
		Objects.requireNonNull(statements, "Statement update cannot be null.");
		this.statements = statements;
	}

	@JsonIgnore
	@Override
	public StatementDocument getBaseRevision() {
		return (StatementDocument) super.getBaseRevision();
	}

	@JsonIgnore
	@Override
	public StatementUpdate getStatements() {
		return statements;
	}

	@JsonProperty
	@JsonInclude(Include.NON_NULL)
	StatementUpdate getClaims() {
		return statements.isEmpty() ? null : statements;
	}
}
