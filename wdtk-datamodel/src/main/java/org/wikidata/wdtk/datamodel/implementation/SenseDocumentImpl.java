package org.wikidata.wdtk.datamodel.implementation;

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

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Jackson implementation of {@link SenseDocument}.
 *
 * @author Thomas Pellissier Tanon
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SenseDocumentImpl extends StatementDocumentImpl implements SenseDocument {

	private final Map<String,MonolingualTextValue> glosses;

	/**
	 * Constructor.
	 *
	 * @param id
	 *            the id of the le that data is about
	 * @param glosses
	 *            the list of glosses of this lexeme, with at most one
	 *            lemma for each language code
	 * @param statements
	 *            the list of statement groups of this lexeme; all of them must
	 *            have the given id as their subject
	 * @param revisionId
	 *            the revision ID or 0 if not known; see
	 *            {@link EntityDocument#getRevisionId()}
	 */
	SenseDocumentImpl(
			SenseIdValue id,
			List<MonolingualTextValue> glosses,
			List<StatementGroup> statements,
			long revisionId) {
		super(id, statements, revisionId);
		Validate.notNull(glosses, "Senses glosses should not be null");
		if(glosses.isEmpty()) {
			throw new IllegalArgumentException("Senses should have at least one gloss");
		}
		this.glosses = constructTermMap(glosses);
	}

	/**
	 * Constructor. Creates an object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	@JsonCreator
	SenseDocumentImpl(
			@JsonProperty("id") String jsonId,
			@JsonProperty("glosses") @JsonDeserialize(contentAs=TermImpl.class) Map<String, MonolingualTextValue> glosses,
			@JsonProperty("claims") Map<String, List<StatementImpl.PreStatement>> claims,
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
		super(jsonId, claims, revisionId, siteIri);
		Validate.notNull(glosses, "Senses glosses should not be null");
		if(glosses.isEmpty()) {
			throw new IllegalArgumentException("Senses should have at least one gloss");
		}
		this.glosses = glosses;
	}

	/**
	 * Copy constructor, used when creating modified copies of senses.
	 */
	private SenseDocumentImpl(
			SenseIdValue subject,
			Map<String, MonolingualTextValue> glosses,
			Map<String, List<Statement>> claims,
			long revisionId) {
		super(subject, claims, revisionId);
		this.glosses = glosses;
	}

	private static Map<String, MonolingualTextValue> constructTermMap(List<MonolingualTextValue> terms) {
		Map<String, MonolingualTextValue> map = new HashMap<>();
		for(MonolingualTextValue term : terms) {
			String language = term.getLanguageCode();
			if(map.containsKey(language)) {
				throw new IllegalArgumentException("Multiple terms provided for the same language.");
			}
			// We need to make sure the terms are of the right type, otherwise they will not
			// be serialized correctly.
			map.put(language, (term instanceof TermImpl) ? term : new TermImpl(term.getLanguageCode(), term.getText()));
		}
		return map;
	}

	@JsonIgnore
	@Override
	public SenseIdValue getEntityId() {
		return new SenseIdValueImpl(entityId, siteIri);
	}

	@JsonProperty("type")
	String getType() {
		return EntityDocumentImpl.JSON_TYPE_SENSE;
	}

	@JsonProperty("glosses")
	@Override
	public Map<String, MonolingualTextValue> getGlosses() {
		return glosses;
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsSenseDocument(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

	@Override
	public SenseDocument withRevisionId(long newRevisionId) {
		return new SenseDocumentImpl(getEntityId(),
				glosses,
				claims,
				newRevisionId);
	}

	@Override
	public SenseDocument withGloss(MonolingualTextValue gloss) {
		Map<String, MonolingualTextValue> newGlosses = new HashMap<>(glosses);
		newGlosses.put(gloss.getLanguageCode(), gloss);
		return new SenseDocumentImpl(getEntityId(), newGlosses, claims, revisionId);
	}

	@Override
	public SenseDocument withStatement(Statement statement) {
		return new SenseDocumentImpl(getEntityId(),
				glosses,
				addStatementToGroups(statement, claims),
				revisionId);
	}

	@Override
	public SenseDocument withoutStatementIds(Set<String> statementIds) {
		return new SenseDocumentImpl(getEntityId(),
				glosses,
				removeStatements(statementIds, claims),
				revisionId);
	}
}
