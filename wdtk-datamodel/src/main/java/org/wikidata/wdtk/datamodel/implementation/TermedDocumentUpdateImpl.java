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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.AliasUpdate;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocumentUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson implementation of {@link TermedStatementDocumentUpdate}.
 */
public abstract class TermedDocumentUpdateImpl extends LabeledDocumentUpdateImpl
		implements TermedStatementDocumentUpdate {

	@JsonIgnore
	private final TermUpdate descriptions;
	@JsonIgnore
	private final Map<String, AliasUpdate> aliases;

	/**
	 * Initializes new entity update.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @param revisionId
	 *            base entity revision to be updated or zero if not available
	 * @param labels
	 *            changes in entity labels or {@code null} for no change
	 * @param descriptions
	 *            changes in entity descriptions, possibly empty
	 * @param aliases
	 *            changes in entity aliases, possibly empty
	 * @param statements
	 *            changes in entity statements, possibly empty
	 * @throws NullPointerException
	 *             if any required parameter or its part is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	protected TermedDocumentUpdateImpl(
			EntityIdValue entityId,
			long revisionId,
			TermUpdate labels,
			TermUpdate descriptions,
			Map<String, AliasUpdate> aliases,
			StatementUpdate statements) {
		super(entityId, revisionId, labels, statements);
		Objects.requireNonNull(descriptions, "Description update cannot be null.");
		Objects.requireNonNull(aliases, "Alias map cannot be null.");
		for (Map.Entry<String, AliasUpdate> entry : aliases.entrySet()) {
			Validate.notBlank(entry.getKey(), "Alias language code cannot be null or blank.");
			Objects.requireNonNull(entry.getValue(), "Alias update cannot be null.");
			if (entry.getValue().getLanguageCode().isPresent()) {
				Validate.isTrue(entry.getValue().getLanguageCode().get().equals(entry.getKey()),
						"Inconsistent alias language codes.");
			}
		}
		this.descriptions = descriptions;
		this.aliases = Collections.unmodifiableMap(aliases.keySet().stream()
				.filter(k -> !aliases.get(k).isEmpty())
				.collect(toMap(k -> k, k -> aliases.get(k))));
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return super.isEmpty() && descriptions.isEmpty() && aliases.isEmpty();
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
	public Map<String, AliasUpdate> getAliases() {
		return aliases;
	}

}
