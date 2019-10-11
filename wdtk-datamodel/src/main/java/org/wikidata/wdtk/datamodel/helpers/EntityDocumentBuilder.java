package org.wikidata.wdtk.datamodel.helpers;

/*
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2015 Wikidata Toolkit Developers
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;

/**
 * Abstract base class for builders that construct {@link EntityDocument}
 * objects.
 *
 * @author Markus Kroetzsch
 *
 * @param <T>
 *            the type of the eventual concrete builder implementation
 * @param <O>
 *            the type of the object that is being built
 */
public abstract class EntityDocumentBuilder<T extends EntityDocumentBuilder<T, O>, O extends TermedStatementDocument>
		extends AbstractDataObjectBuilder<T, O> {

	EntityIdValue entityIdValue;
	final ArrayList<MonolingualTextValue> labels = new ArrayList<>();
	final ArrayList<MonolingualTextValue> descriptions = new ArrayList<>();
	final ArrayList<MonolingualTextValue> aliases = new ArrayList<>();
	final HashMap<PropertyIdValue, ArrayList<Statement>> statements = new HashMap<>();

	long revisionId = 0;

	protected EntityDocumentBuilder(EntityIdValue entityIdValue) {
		this.entityIdValue = entityIdValue;
	}
	
	/**
	 * Starts constructing an EntityDocument from an initial version
	 * of this document.
	 * 
	 * @param initialDocument
	 * 			the initial version of the document to use
	 */
	protected EntityDocumentBuilder(O initialDocument) {
		this.entityIdValue = initialDocument.getEntityId();
		this.revisionId = initialDocument.getRevisionId();
		for(MonolingualTextValue label : initialDocument.getLabels().values()) {
			withLabel(label);
		}
		for(MonolingualTextValue description : initialDocument.getDescriptions().values()) {
			withDescription(description);
		}
		for(List<MonolingualTextValue> aliases : initialDocument.getAliases().values()) {
			for(MonolingualTextValue alias : aliases) {
				withAlias(alias);
			}
		}
		Iterator<Statement> iterator = initialDocument.getAllStatements();
		while(iterator.hasNext()) {
			withStatement(iterator.next());		
		}
	}

	/**
	 * Sets the revision id for the constructed document. See
	 * {@link EntityDocument#getRevisionId()}.
	 *
	 * @param revisionId
	 *            the revision id
	 * @return builder object to continue construction
	 */
	public T withRevisionId(long revisionId) {
		this.revisionId = revisionId;
		return getThis();
	}
	
	/**
	 * Changes the entity value id for the constructed document.
	 * See {@link EntityDocument#getEntityId()}.
	 * 
	 * @param entityId
	 *          the entity id
	 * @return builder object to continue construction
	 */
	public T withEntityId(EntityIdValue entityId) {
		this.entityIdValue = entityId;
		return getThis();
	}

	/**
	 * Adds an additional label to the constructed document.
	 *
	 * @param mtv
	 *            the additional label
	 * @return builder object to continue construction
	 */
	public T withLabel(MonolingualTextValue mtv) {
		this.labels.add(mtv);
		return getThis();
	}

	/**
	 * Adds an additional label to the constructed document.
	 *
	 * @param text
	 *            the text of the label
	 * @param languageCode
	 *            the language code of the label
	 * @return builder object to continue construction
	 */
	public T withLabel(String text, String languageCode) {
		withLabel(factory.getMonolingualTextValue(text, languageCode));
		return getThis();
	}

	/**
	 * Adds an additional description to the constructed document.
	 *
	 * @param mtv
	 *            the additional description
	 * @return builder object to continue construction
	 */
	public T withDescription(MonolingualTextValue mtv) {
		this.descriptions.add(mtv);
		return getThis();
	}

	/**
	 * Adds an additional description to the constructed document.
	 *
	 * @param text
	 *            the text of the description
	 * @param languageCode
	 *            the language code of the description
	 * @return builder object to continue construction
	 */
	public T withDescription(String text, String languageCode) {
		withDescription(factory.getMonolingualTextValue(text, languageCode));
		return getThis();
	}

	/**
	 * Adds an additional alias to the constructed document.
	 *
	 * @param mtv
	 *            the additional alias
	 * @return builder object to continue construction
	 */
	public T withAlias(MonolingualTextValue mtv) {
		this.aliases.add(mtv);
		return getThis();
	}

	/**
	 * Adds an additional alias to the constructed document.
	 *
	 * @param text
	 *            the text of the alias
	 * @param languageCode
	 *            the language code of the alias
	 * @return builder object to continue construction
	 */
	public T withAlias(String text, String languageCode) {
		withAlias(factory.getMonolingualTextValue(text, languageCode));
		return getThis();
	}

	/**
	 * Adds an additional statement to the constructed document.
	 *
	 * @param statement
	 *            the additional statement
	 * @return builder object to continue construction
	 */
	public T withStatement(Statement statement) {
		PropertyIdValue pid = statement.getMainSnak()
				.getPropertyId();
		ArrayList<Statement> pidStatements = this.statements.get(pid);
		if (pidStatements == null) {
			pidStatements = new ArrayList<Statement>();
			this.statements.put(pid, pidStatements);
		}

		pidStatements.add(statement);
		return getThis();
	}

	/**
	 * Returns a list of {@link StatementGroup} objects for the currently stored
	 * statements.
	 *
	 * @return
	 */
	protected List<StatementGroup> getStatementGroups() {
		ArrayList<StatementGroup> result = new ArrayList<>(
				this.statements.size());
		for (ArrayList<Statement> statementList : this.statements.values()) {
			result.add(factory.getStatementGroup(statementList));
		}
		return result;
	}

}
