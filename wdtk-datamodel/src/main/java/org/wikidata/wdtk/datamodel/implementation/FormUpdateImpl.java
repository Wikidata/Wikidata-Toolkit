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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson implementation of {@link FormUpdate}.
 */
public class FormUpdateImpl extends StatementDocumentUpdateImpl implements FormUpdate {

	@JsonIgnore
	private final TermUpdate representations;
	@JsonIgnore
	private final Set<ItemIdValue> grammaticalFeatures;

	/**
	 * Initializes new form update.
	 * 
	 * @param entityId
	 *            ID of the form that is to be updated
	 * @param revisionId
	 *            base form revision to be updated or zero if not available
	 * @param representations
	 *            changes in form representations, possibly empty
	 * @param grammaticalFeatures
	 *            new grammatical features of the form or {@code null} for no change
	 * @param statements
	 *            changes in entity statements, possibly empty
	 * @throws NullPointerException
	 *             if any required parameter is {@code null}
	 * @throws IllegalArgumentException
	 *             if any parameters or their combination is invalid
	 */
	public FormUpdateImpl(
			FormIdValue entityId,
			long revisionId,
			TermUpdate representations,
			Collection<ItemIdValue> grammaticalFeatures,
			StatementUpdate statements) {
		super(entityId, revisionId, statements);
		Objects.requireNonNull(representations, "Representation update cannot be null.");
		if (grammaticalFeatures != null) {
			for (ItemIdValue feature : grammaticalFeatures) {
				Objects.requireNonNull(feature, "Grammatical feature cannot be null.");
				Validate.isTrue(!feature.isPlaceholder(), "Grammatical feature cannot be a placeholder ID.");
			}
			Validate.isTrue(
					grammaticalFeatures.stream().distinct().count() == grammaticalFeatures.size(),
					"Grammatical features must be unique.");
		}
		this.representations = representations;
		this.grammaticalFeatures = grammaticalFeatures != null
				? Collections.unmodifiableSet(new HashSet<>(grammaticalFeatures))
				: null;
	}

	@JsonIgnore
	@Override
	public FormIdValue getEntityId() {
		return (FormIdValue) super.getEntityId();
	}

	@JsonIgnore
	@Override
	public boolean isEmpty() {
		return super.isEmpty() && representations.isEmpty() && grammaticalFeatures == null;
	}

	@JsonIgnore
	@Override
	public TermUpdate getRepresentations() {
		return representations;
	}

	@JsonProperty("representations")
	@JsonInclude(Include.NON_NULL)
	TermUpdate getJsonRepresentations() {
		return representations.isEmpty() ? null : representations;
	}

	@JsonIgnore
	@Override
	public Optional<Set<ItemIdValue>> getGrammaticalFeatures() {
		return Optional.ofNullable(grammaticalFeatures);
	}

	@JsonProperty("grammaticalFeatures")
	@JsonInclude(Include.NON_NULL)
	List<String> getJsonGrammaticalFeatures() {
		if (grammaticalFeatures == null)
			return null;
		return grammaticalFeatures.stream().map(f -> f.getId()).collect(toList());
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsFormUpdate(this, obj);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

}
