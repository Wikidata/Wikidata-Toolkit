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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocumentUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Jackson implementation of {@link TermedStatementDocumentUpdate}.
 */
public abstract class TermedStatementDocumentUpdateImpl extends LabeledStatementDocumentUpdateImpl
		implements TermedStatementDocumentUpdate {

	@JsonIgnore
	private final TermUpdate descriptions;
	@JsonIgnore
	private final Map<String, List<MonolingualTextValue>> aliases;

	/**
	 * Initializes new entity update.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @param revision
	 *            base entity revision to be updated or {@code null} if not
	 *            available
	 * @param labels
	 *            changes in entity labels or {@code null} for no change
	 * @param descriptions
	 *            changes in entity descriptions, possibly empty
	 * @param aliases
	 *            changes in entity aliases, possibly empty
	 * @param statements
	 *            changes in entity statements, possibly empty
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	protected TermedStatementDocumentUpdateImpl(
			EntityIdValue entityId,
			TermedStatementDocument revision,
			TermUpdate labels,
			TermUpdate descriptions,
			Map<String, List<MonolingualTextValue>> aliases,
			StatementUpdate statements) {
		super(entityId, revision, labels, statements);
		Objects.requireNonNull(descriptions, "Description update cannot be null.");
		Objects.requireNonNull(aliases, "Alias map cannot be null.");
		this.descriptions = descriptions;
		this.aliases = Collections.unmodifiableMap(aliases.keySet().stream()
				.collect(toMap(k -> k, k -> Collections.unmodifiableList(aliases.get(k).stream()
						.map(t -> new TermImpl(k, t.getText()))
						.collect(toList())))));
	}

	@JsonIgnore
	@Override
	public TermedStatementDocument getBaseRevision() {
		return (TermedStatementDocument) super.getBaseRevision();
	}

	@JsonIgnore
	@Override
	public TermUpdate getDescriptions() {
		return descriptions;
	}

	@JsonProperty("descriptions")
	@JsonInclude(Include.NON_NULL)
	TermUpdate getJsonDescriptions() {
		return descriptions.isEmpty() ? null : descriptions;
	}

	@JsonProperty("aliases")
	@JsonInclude(Include.NON_EMPTY)
	@Override
	public Map<String, List<MonolingualTextValue>> getAliases() {
		return aliases;
	}

}
