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
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.util.*;


/**
 * Jackson implementation of {@link MediaInfoDocument}.
 *
 * @author Thomas Pellissier Tanon
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MediaInfoDocumentImpl extends LabeledStatementDocumentImpl implements MediaInfoDocument {

	/**
	 * Constructor.
	 *
	 * @param id
	 *            the id of the media that data is about
	 * @param labels
	 *            the list of captions of this media, with at most one label for
	 *            each language code
	 * @param statements
	 *            the list of statement groups of this media info; all of them must
	 *            have the given itemIdValue as their subject
	 * @param revisionId
	 *            the revision ID or 0 if not known; see
	 *            {@link EntityDocument#getRevisionId()}
	 */
	public MediaInfoDocumentImpl(
			MediaInfoIdValue id,
			List<MonolingualTextValue> labels,
			List<StatementGroup> statements,
			long revisionId) {
		super(id, labels, statements, revisionId);
	}

	/**
	 * Constructor. Creates an object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 *
	 * The claims parameter is here in case the Structured Data on Commons dev team
	 * moves back from "statements" to "claims" or someone wrongly use the "claims" key.
	 */
	@JsonCreator
	public MediaInfoDocumentImpl(
			@JsonProperty("id") String jsonId,
			@JsonProperty("labels") @JsonDeserialize(contentAs=TermImpl.class) Map<String, MonolingualTextValue> labels,
			@JsonProperty("claims") Map<String, List<StatementImpl.PreStatement>> claims,
			@JsonProperty("statements") Map<String, List<StatementImpl.PreStatement>> statements,
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
		super(jsonId, labels, (statements == null) ? claims : statements, revisionId, siteIri);
	}

	/**
	 * Protected constructor, meant to be used to create modified copies
	 * of instances.
	 */
	protected MediaInfoDocumentImpl(
			MediaInfoIdValue subject,
			Map<String, MonolingualTextValue> labels,
			Map<String, List<Statement>> claims,
			long revisionId) {
		super(subject, labels, claims, revisionId);
	}

	@JsonIgnore
	@Override
	public MediaInfoIdValue getEntityId() {
		return new MediaInfoIdValueImpl(this.entityId, this.siteIri);
	}

	@JsonProperty("type")
	String getType() {
		return EntityDocumentImpl.JSON_TYPE_MEDIA_INFO;
	}

	@JsonProperty("statements")
	@Override
	public Map<String, List<Statement>> getJsonClaims() {
		return super.getJsonClaims();
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsMediaInfoDocument(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

	@Override
	public MediaInfoDocument withRevisionId(long newRevisionId) {
		return new MediaInfoDocumentImpl(getEntityId(), labels, claims, newRevisionId);
	}

	@Override
	public MediaInfoDocument withLabel(MonolingualTextValue newLabel) {
		Map<String, MonolingualTextValue> newLabels = new HashMap<>(labels);
		newLabels.put(newLabel.getLanguageCode(), newLabel);
		return new MediaInfoDocumentImpl(getEntityId(), newLabels, claims, revisionId);
	}

	@Override
	public MediaInfoDocument withStatement(Statement statement) {
		Map<String, List<Statement>> newGroups = addStatementToGroups(statement, claims);
		return new MediaInfoDocumentImpl(getEntityId(), labels, newGroups, revisionId);
	}

	@Override
	public MediaInfoDocument withoutStatementIds(Set<String> statementIds) {
		Map<String, List<Statement>> newGroups = removeStatements(statementIds, claims);
		return new MediaInfoDocumentImpl(getEntityId(), labels, newGroups, revisionId);
	}
}
