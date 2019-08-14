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

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.wikidata.wdtk.datamodel.interfaces.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract Jackson implementation of {@link LabeledDocument} and {@link StatementDocument}.
 * You should not rely on it directly but build instances with the Datamodel helper and
 * use {@link EntityDocumentImpl} for deserialization.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 * @author Thomas Pellissier Tanon
 *
 */
abstract class LabeledStatementDocumentImpl extends StatementDocumentImpl implements LabeledStatementDocument {

	protected final Map<String, MonolingualTextValue> labels;

	/**
	 * Constructor.
	 *
	 * @param id
	 * 		the identifier of the subject of this document
	 * @param labels
	 * 		the labels for this entity, at most one per language
	 * @param claims
	 * 		the statement groups contained in this document
	 * @param revisionId
	 * 		the id of the last revision of this document
	 */
	public LabeledStatementDocumentImpl(
			EntityIdValue id,
			List<MonolingualTextValue> labels,
			List<StatementGroup> claims,
			long revisionId) {
		super(id, claims, revisionId);
		this.labels = (labels == null) ? Collections.emptyMap() : constructTermMap(labels);
	}

	/**
	 * Constructor used for JSON deserialization with Jackson.
	 */
	LabeledStatementDocumentImpl(
			@JsonProperty("id") String jsonId,
			@JsonProperty("labels") Map<String, MonolingualTextValue> labels,
			@JsonProperty("claims") Map<String, List<StatementImpl.PreStatement>> claims,
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
		super(jsonId, claims, revisionId, siteIri);
		this.labels = (labels == null) ? Collections.emptyMap() : labels;
	}

	/**
	 * Protected constructor provided to ease the creation
	 * of copies. No check is made and each field is reused without
	 * copying.
	 *
	 * @param labels
	 * 		a map from language codes to monolingual values with
	 * 	    the same language codes
	 * @param claims
	 * @param revisionId
	 */
	protected LabeledStatementDocumentImpl(
			EntityIdValue subject,
			Map<String, MonolingualTextValue> labels,
			Map<String, List<Statement>> claims,
			long revisionId) {
		super(subject, claims, revisionId);
		this.labels = labels;
	}

	@JsonProperty("labels")
	@Override
	public Map<String, MonolingualTextValue> getLabels() {
		return Collections.unmodifiableMap(this.labels);
	}

	protected static Map<String, MonolingualTextValue> constructTermMap(List<MonolingualTextValue> terms) {
		Map<String, MonolingualTextValue> map = new HashMap<>();
		for(MonolingualTextValue term : terms) {
			String language = term.getLanguageCode();
			if(map.containsKey(language)) {
				throw new IllegalArgumentException("Multiple terms provided for the same language.");
			}
			map.put(language, toTerm(term));
		}
		return map;
	}

	/**
	 * We need to make sure the terms are of the right type, otherwise they will not be serialized correctly.
	 */
	private static MonolingualTextValue toTerm(MonolingualTextValue term) {
		return term instanceof TermImpl ? term : new TermImpl(term.getLanguageCode(), term.getText());
	}
}
