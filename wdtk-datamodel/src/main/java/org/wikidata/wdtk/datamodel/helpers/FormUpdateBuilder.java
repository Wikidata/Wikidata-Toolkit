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
package org.wikidata.wdtk.datamodel.helpers;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.FormDocument;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormUpdate;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;

/**
 * Builder for incremental construction of {@link FormUpdate} objects.
 */
public class FormUpdateBuilder extends StatementUpdateBuilder {

	private final Map<String, MonolingualTextValue> modifiedRepresentations = new HashMap<>();
	private final Set<String> removedRepresentations = new HashSet<>();
	private Set<ItemIdValue> grammaticalFeatures;

	/**
	 * Initializes new builder object for constructing update of form entity with
	 * given ID.
	 * 
	 * @param formId
	 *            ID of the form entity that is to be updated
	 * @throws NullPointerException
	 *             if {@code formId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code formId} is not a valid ID
	 */
	private FormUpdateBuilder(FormIdValue formId) {
		super(formId);
	}

	/**
	 * Initializes new builder object for constructing update of given form entity
	 * revision.
	 * 
	 * @param document
	 *            form revision to be updated
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} does not have valid ID
	 */
	private FormUpdateBuilder(FormDocument document) {
		super(document);
	}

	/**
	 * Creates new builder object for constructing update of form entity with given
	 * ID.
	 * 
	 * @param formId
	 *            ID of the form that is to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code formId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code formId} is not valid
	 */
	public static FormUpdateBuilder forFormId(FormIdValue formId) {
		return new FormUpdateBuilder(formId);
	}

	/**
	 * Creates new builder object for constructing update of given form entity
	 * revision. Provided form document might not represent the latest revision of
	 * the form entity as currently stored in Wikibase. It will be used for
	 * validation in builder methods. If the document has revision ID, it will be
	 * used to detect edit conflicts.
	 * 
	 * @param document
	 *            form entity revision to be updated
	 * @return update builder object
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} does not have valid ID
	 */
	public static FormUpdateBuilder forFormDocument(FormDocument document) {
		return new FormUpdateBuilder(document);
	}

	@Override
	protected FormIdValue getEntityId() {
		return (FormIdValue) super.getEntityId();
	}

	@Override
	protected FormDocument getCurrentDocument() {
		return (FormDocument) super.getCurrentDocument();
	}

	/**
	 * Adds or changes form representation. If there is no representation for the
	 * language code, new representation is added. If a representation with this
	 * language code already exists, it is replaced. Representations with other
	 * language codes are not touched. Calling this method overrides any previous
	 * changes made with the same language code by this method or
	 * {@link #removeRepresentation(String)}.
	 * 
	 * @param representation
	 *            form representation to add or change
	 * @throws NullPointerException
	 *             if {@code representation} is {@code null}
	 */
	public void setRepresentation(MonolingualTextValue representation) {
		Objects.requireNonNull(representation, "Representation cannot be null.");
		modifiedRepresentations.put(representation.getLanguageCode(), representation);
		removedRepresentations.remove(representation.getLanguageCode());
	}

	/**
	 * Removes form representation. Representations with other language codes are
	 * not touched. Calling this method overrides any previous changes made with the
	 * same language code by this method or
	 * {@link #setRepresentation(MonolingualTextValue)}.
	 * 
	 * @param languageCode
	 *            language code of the removed form representation
	 * @throws NullPointerException
	 *             if {@code languageCode} is {@code null}
	 * @throws IllegalArgumentException
	 *             if the representation is not present in current form entity
	 *             revision (if available)
	 */
	public void removeRepresentation(String languageCode) {
		Objects.requireNonNull(languageCode, "Language code cannot be null.");
		if (getCurrentDocument() != null && !getCurrentDocument().getRepresentations().containsKey(languageCode)) {
			throw new IllegalArgumentException(
					"Representation with this language code is not in the current revision.");
		}
		removedRepresentations.add(languageCode);
		modifiedRepresentations.remove(languageCode);
	}

	/**
	 * Sets grammatical features of the form. Any previously assigned grammatical
	 * features are removed. Duplicate grammatical features are ignored. To remove
	 * all grammatical features without replacement, call this method with empty
	 * collection.
	 * 
	 * @param features
	 *            new grammatical features of the form
	 * @throws NullPointerException
	 *             if {@code features} or any of its items is {@code null}
	 * @throws IllegalArgumentException
	 *             if any item ID in {@code features} is invalid
	 */
	public void setGrammaticalFeatures(Collection<ItemIdValue> features) {
		Objects.requireNonNull(features, "Collection of grammatical features cannot be null.");
		for (ItemIdValue id : features) {
			Objects.requireNonNull(id, "Grammatical feature IDs must not be null.");
			Validate.isTrue(id.isValid(), "Grammatical feature ID must be valid.");
		}
		this.grammaticalFeatures = new HashSet<>(features);
	}

	@Override
	public FormUpdate build() {
		return factory.getFormUpdate(getEntityId(), getCurrentDocument(),
				modifiedRepresentations.values(), removedRepresentations, grammaticalFeatures,
				getAddedStatements(), getReplacedStatements(), getRemovedStatements());
	}

}
