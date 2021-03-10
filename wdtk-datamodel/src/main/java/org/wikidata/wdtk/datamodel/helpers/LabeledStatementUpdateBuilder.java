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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LabeledStatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.LabeledStatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoDocument;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;

/**
 * Builder for incremental construction of {@link LabeledStatementUpdate}
 * objects.
 */
public abstract class LabeledStatementUpdateBuilder extends StatementUpdateBuilder {

	private final Map<String, MonolingualTextValue> modifiedLabels = new HashMap<>();
	private final Set<String> removedLabels = new HashSet<>();

	/**
	 * Initializes new builder object for constructing update of entity with given
	 * ID.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @throws NullPointerException
	 *             if {@code entityId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code entityId} is not a valid ID
	 */
	protected LabeledStatementUpdateBuilder(EntityIdValue entityId) {
		super(entityId);
	}

	/**
	 * Initializes new builder object for constructing update of given entity
	 * revision.
	 * 
	 * @param document
	 *            entity revision to be updated
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} does not have valid ID
	 */
	protected LabeledStatementUpdateBuilder(LabeledStatementDocument document) {
		super(document);
	}

	/**
	 * Creates new builder object for constructing update of entity with given ID.
	 * <p>
	 * Supported entity IDs include {@link ItemIdValue}, {@link PropertyIdValue},
	 * and {@link MediaInfoIdValue}.
	 * 
	 * @param entityId
	 *            ID of the entity that is to be updated
	 * @return builder object matching entity type
	 * @throws NullPointerException
	 *             if {@code entityId} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code entityId} is of unrecognized type or it is not valid
	 */
	public static LabeledStatementUpdateBuilder forEntityId(EntityIdValue entityId) {
		Objects.requireNonNull(entityId, "Entity ID cannot be null.");
		if (entityId instanceof MediaInfoIdValue) {
			return MediaInfoUpdateBuilder.forMediaInfoId((MediaInfoIdValue) entityId);
		}
		return TermedStatementUpdateBuilder.forEntityId(entityId);
	}

	/**
	 * Creates new builder object for constructing update of given entity revision.
	 * Provided entity document might not represent the latest revision of the
	 * entity as currently stored in Wikibase. It will be used for validation in
	 * builder methods. If the document has revision ID, it will be used to detect
	 * edit conflicts.
	 * <p>
	 * Supported entity types include {@link ItemDocument},
	 * {@link PropertyDocument}, and {@link MediaInfoDocument}.
	 * 
	 * @param document
	 *            entity revision to be updated
	 * @return builder object matching entity type
	 * @throws NullPointerException
	 *             if {@code document} is {@code null}
	 * @throws IllegalArgumentException
	 *             if {@code document} is of unrecognized type or it does not have
	 *             valid ID
	 */
	public static LabeledStatementUpdateBuilder forLabeledStatementDocument(LabeledStatementDocument document) {
		Objects.requireNonNull(document, "Entity document cannot be null.");
		if (document instanceof MediaInfoDocument) {
			return MediaInfoUpdateBuilder.forMediaInfoDocument((MediaInfoDocument) document);
		}
		if (document instanceof TermedStatementDocument) {
			return TermedStatementUpdateBuilder.forTermedStatementDocument((TermedStatementDocument) document);
		}
		throw new IllegalArgumentException("Unrecognized entity document type.");
	}

	@Override
	protected LabeledStatementDocument getCurrentDocument() {
		return (LabeledStatementDocument) super.getCurrentDocument();
	}

	protected Map<String, MonolingualTextValue> getModifiedLabels() {
		return modifiedLabels;
	}

	protected Set<String> getRemovedLabels() {
		return removedLabels;
	}

	/**
	 * Adds or changes entity label. If there is no label for the language code, new
	 * label is added. If a label with this language code already exists, it is
	 * replaced. Labels with other language codes are not touched. Calling this
	 * method overrides any previous changes made with the same language code by
	 * this method or {@link #removeLabel(String)}.
	 * 
	 * @param label
	 *            entity label to add or change
	 * @throws NullPointerException
	 *             if {@code label} is {@code null}
	 */
	public void setLabel(MonolingualTextValue label) {
		Objects.requireNonNull(label, "Label cannot be null.");
		modifiedLabels.put(label.getLanguageCode(), label);
		removedLabels.remove(label.getLanguageCode());
	}

	/**
	 * Removes entity label. Labels with other language codes are not touched.
	 * Calling this method overrides any previous changes made with the same
	 * language code by this method or {@link #setLabel(MonolingualTextValue)}.
	 * 
	 * @param languageCode
	 *            language code of the removed entity label
	 * @throws NullPointerException
	 *             if {@code languageCode} is {@code null}
	 * @throws IllegalArgumentException
	 *             if the label is not present in current entity revision (if
	 *             available)
	 */
	public void removeLabel(String languageCode) {
		Objects.requireNonNull(languageCode, "Language code cannot be null.");
		if (getCurrentDocument() != null && !getCurrentDocument().getLabels().containsKey(languageCode)) {
			throw new IllegalArgumentException("Label with this language code is not in the current revision.");
		}
		removedLabels.add(languageCode);
		modifiedLabels.remove(languageCode);
	}

	/**
	 * Creates new {@link LabeledStatementUpdate} object with contents of this
	 * builder object.
	 * 
	 * @return constructed object
	 */
	@Override
	public abstract LabeledStatementUpdate build();

}
