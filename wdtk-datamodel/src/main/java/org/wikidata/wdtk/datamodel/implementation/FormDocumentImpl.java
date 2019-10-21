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

import java.util.*;


/**
 * Jackson implementation of {@link FormDocument}.
 *
 * @author Thomas Pellissier Tanon
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FormDocumentImpl extends StatementDocumentImpl implements FormDocument {

	private final List<ItemIdValue> grammaticalFeatures;

	private final Map<String,MonolingualTextValue> representations;

	/**
	 * Constructor.
	 *
	 * @param id
	 *            the id of the le that data is about
	 * @param representations
	 *            the list of representations of this lexeme, with at most one
	 *            lemma for each language code
	 * @param grammaticalFeatures
	 *            the grammatical features of the lexeme
	 * @param statements
	 *            the list of statement groups of this lexeme; all of them must
	 *            have the given id as their subject
	 * @param revisionId
	 *            the revision ID or 0 if not known; see
	 *            {@link EntityDocument#getRevisionId()}
	 */
	FormDocumentImpl(
			FormIdValue id,
			List<MonolingualTextValue> representations,
			List<ItemIdValue> grammaticalFeatures,
			List<StatementGroup> statements,
			long revisionId) {
		super(id, statements, revisionId);
		Validate.notNull(representations, "Forms representations should not be null");
		if(representations.isEmpty()) {
			throw new IllegalArgumentException("Forms should have at least one representation");
		}
		this.representations = constructTermMap(representations);
		this.grammaticalFeatures = (grammaticalFeatures == null) ? Collections.emptyList() : grammaticalFeatures;
		this.grammaticalFeatures.sort(Comparator.comparing(EntityIdValue::getId));
	}

	/**
	 * Constructor. Creates an object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	@JsonCreator
	FormDocumentImpl(
			@JsonProperty("id") String jsonId,
			@JsonProperty("representations") @JsonDeserialize(contentAs=TermImpl.class) Map<String, MonolingualTextValue> representations,
			@JsonProperty("grammaticalFeatures") List<String> grammaticalFeatures,
			@JsonProperty("claims") Map<String, List<StatementImpl.PreStatement>> claims,
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
		super(jsonId, claims, revisionId, siteIri);
		Validate.notNull(representations, "Forms representations should not be null");
		if(representations.isEmpty()) {
			throw new IllegalArgumentException("Forms should have at least one representation");
		}
		this.representations = representations;
		this.grammaticalFeatures = (grammaticalFeatures == null || grammaticalFeatures.isEmpty())
				? Collections.emptyList()
				: constructGrammaticalFeatures(grammaticalFeatures, siteIri);
	}

	/**
	 * Copy constructor, used when creating modified copies of forms.
	 */
	private FormDocumentImpl(
			FormIdValue id,
			Map<String,MonolingualTextValue> representations,
			List<ItemIdValue> grammaticalFeatures,
			Map<String, List<Statement>> statements,
			long revisionId) {
		super(id, statements, revisionId);
		this.representations = representations;
		this.grammaticalFeatures = grammaticalFeatures;
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

	private List<ItemIdValue> constructGrammaticalFeatures(List<String> grammaticalFeatures, String siteIri) {
		List<ItemIdValue> output = new ArrayList<>(grammaticalFeatures.size());
		for(String grammaticalFeature : grammaticalFeatures) {
			output.add(new ItemIdValueImpl(grammaticalFeature, siteIri));
		}
		return output;
	}

	@JsonIgnore
	@Override
	public FormIdValue getEntityId() {
		return new FormIdValueImpl(entityId, siteIri);
	}

	@JsonIgnore
	@Override
	public List<ItemIdValue> getGrammaticalFeatures() {
		return grammaticalFeatures;
	}

	@JsonProperty("grammaticalFeatures")
	List<String> getJsonGrammaticalFeatures() {
		if (grammaticalFeatures.isEmpty()) {
			return Collections.emptyList();
		}
		List<String> output = new ArrayList<>(grammaticalFeatures.size());
		for(ItemIdValue feature : grammaticalFeatures) {
			output.add(feature.getId());
		}
		return output;
	}

	@JsonProperty("type")
	String getType() {
		return EntityDocumentImpl.JSON_TYPE_FORM;
	}

	@JsonProperty("representations")
	@Override
	public Map<String, MonolingualTextValue> getRepresentations() {
		return representations;
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsFormDocument(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
	@Override
	public FormDocument withRevisionId(long newRevisionId) {
		return new FormDocumentImpl(getEntityId(),
				representations, grammaticalFeatures,
				claims, newRevisionId);
	}

	@Override
	public FormDocument withRepresentation(MonolingualTextValue representation) {
		Map<String, MonolingualTextValue> newRepresentations = new HashMap<>(representations);
		newRepresentations.put(representation.getLanguageCode(), representation);
		return new FormDocumentImpl(getEntityId(), newRepresentations, grammaticalFeatures, claims, revisionId);
	}

	@Override
	public FormDocument withGrammaticalFeature(ItemIdValue grammaticalFeature) {
		if (grammaticalFeatures.contains(grammaticalFeature)) {
			return this;
		}
		List<ItemIdValue> newGrammaticalFeatures = new ArrayList<>(grammaticalFeatures);
		newGrammaticalFeatures.add(grammaticalFeature);
		return new FormDocumentImpl(getEntityId(), representations, newGrammaticalFeatures, claims, revisionId);
	}

	@Override
	public FormDocument withStatement(Statement statement) {
		Map<String, List<Statement>> newGroups = addStatementToGroups(statement, claims);
		return new FormDocumentImpl(getEntityId(),
				representations, grammaticalFeatures,
				newGroups, revisionId);
	}

	@Override
	public FormDocument withoutStatementIds(Set<String> statementIds) {
		Map<String, List<Statement>> newGroups = removeStatements(statementIds, claims);
		return new FormDocumentImpl(getEntityId(),
				representations, grammaticalFeatures,
				newGroups, revisionId);
	}
}
