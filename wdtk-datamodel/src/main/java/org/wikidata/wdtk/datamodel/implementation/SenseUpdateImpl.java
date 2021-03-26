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

import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;
import org.wikidata.wdtk.datamodel.interfaces.SenseDocument;
import org.wikidata.wdtk.datamodel.interfaces.SenseIdValue;
import org.wikidata.wdtk.datamodel.interfaces.SenseUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Jackson implementation of {@link SenseUpdate}.
 */
public class SenseUpdateImpl extends StatementDocumentUpdateImpl implements SenseUpdate {

	@JsonIgnore
	private final TermUpdate glosses;

	/**
	 * Initializes new sense update.
	 * 
	 * @param entityId
	 *            ID of the sense that is to be updated
	 * @param revision
	 *            base sense revision to be updated or {@code null} if not available
	 * @param glosses
	 *            changes in sense glosses, possibly empty
	 * @param statements
	 *            changes in entity statements, possibly empty
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	public SenseUpdateImpl(
			SenseIdValue entityId,
			SenseDocument revision,
			TermUpdate glosses,
			StatementUpdate statements) {
		super(entityId, revision, statements);
		Objects.requireNonNull(glosses, "Gloss update cannot be null.");
		this.glosses = glosses;
	}

	@JsonIgnore
	@Override
	public SenseIdValue getEntityId() {
		return (SenseIdValue) super.getEntityId();
	}

	@JsonIgnore
	@Override
	public SenseDocument getBaseRevision() {
		return (SenseDocument) super.getBaseRevision();
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return glosses.isEmpty() && getStatements().isEmpty();
	}

	@JsonIgnore
	@Override
	public TermUpdate getGlosses() {
		return glosses;
	}

	@JsonProperty("glosses")
	@JsonInclude(Include.NON_EMPTY)
	TermUpdate getJsonGlosses() {
		return glosses.isEmpty() ? null : glosses;
	}

}
