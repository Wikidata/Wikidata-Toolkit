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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.apache.commons.lang3.Validate;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Jackson implementation of {@link ItemDocument}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemDocumentImpl extends TermedStatementDocumentImpl
		implements ItemDocument {

	/**
	 * Map to store site links.
	 */
	@JsonDeserialize(contentAs=SiteLinkImpl.class)
	private final Map<String, SiteLink> sitelinks;
	
	/**
	 * Constructor.
	 *
	 * @param id
	 *            the id of the item that data is about
	 * @param labels
	 *            the list of labels of this item, with at most one label for
	 *            each language code
	 * @param descriptions
	 *            the list of descriptions of this item, with at most one
	 *            description for each language code
	 * @param aliases
	 *            the list of aliases of this item
	 * @param statements
	 *            the list of statement groups of this item; all of them must
	 *            have the given itemIdValue as their subject
	 * @param siteLinks
	 *            the sitelinks of this item
	 * @param revisionId
	 *            the revision ID or 0 if not known; see
	 *            {@link EntityDocument#getRevisionId()}
	 */
	public ItemDocumentImpl(
			ItemIdValue id,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statements,
			List<SiteLink> siteLinks,
			long revisionId) {
		super(id, labels, descriptions, aliases, statements, revisionId);
		this.sitelinks = new HashMap<>();
		for(SiteLink sitelink : siteLinks) {
			if(this.sitelinks.containsKey(sitelink.getSiteKey())) {
				throw new IllegalArgumentException("Multiple site links provided for the same site.");
			} else {
				this.sitelinks.put(sitelink.getSiteKey(), sitelink);
			}
		}
	}

	/**
	 * Constructor. Creates an object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	@JsonCreator
	public ItemDocumentImpl(
			@JsonProperty("id") String jsonId,
			@JsonProperty("labels") @JsonDeserialize(contentAs=TermImpl.class) Map<String, MonolingualTextValue> labels,
			@JsonProperty("descriptions") @JsonDeserialize(contentAs=TermImpl.class) Map<String, MonolingualTextValue> descriptions,
			@JsonProperty("aliases") @JsonDeserialize(using = AliasesDeserializer.class) Map<String, List<MonolingualTextValue>> aliases,
			@JsonProperty("claims") Map<String, List<StatementImpl.PreStatement>> claims,
			@JsonProperty("sitelinks") Map<String, SiteLink> sitelinks,
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
		super(jsonId, labels, descriptions, aliases, claims, revisionId, siteIri);
		if (sitelinks != null) {
			this.sitelinks = sitelinks;
		} else {
			this.sitelinks = Collections.emptyMap();
		}
	}

	/**
	 * Protected constructor, meant to be used to create modified copies
	 * of instances.
	 */
	protected ItemDocumentImpl(
			ItemIdValue subject,
			Map<String, MonolingualTextValue> labels,
			Map<String, MonolingualTextValue> descriptions,
			Map<String, List<MonolingualTextValue>> aliases,
			Map<String, List<Statement>> claims, 
			Map<String, SiteLink> siteLinks,
			long revisionId) {
		super(subject, labels, descriptions, aliases, claims, revisionId);
		this.sitelinks = siteLinks;
	}

	@JsonIgnore
	@Override
	public ItemIdValue getItemId() {
		return getEntityId();
	}

	@JsonIgnore
	@Override
	public ItemIdValue getEntityId() {
		return new ItemIdValueImpl(this.entityId, this.siteIri);
	}

	@JsonProperty("sitelinks")
	@Override
	public Map<String, SiteLink> getSiteLinks() {
		return Collections. unmodifiableMap(this.sitelinks);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsItemDocument(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

	@Override
	public ItemDocument withRevisionId(long newRevisionId) {
		return new ItemDocumentImpl(getItemId(),
				labels,	descriptions,
				aliases, claims,
				sitelinks, newRevisionId);
	}

	@Override
	public ItemDocument withLabel(MonolingualTextValue newLabel) {
		Map<String, MonolingualTextValue> newLabels = new HashMap<>(labels);
		newLabels.put(newLabel.getLanguageCode(), newLabel);
		return new ItemDocumentImpl(getItemId(),
				newLabels, descriptions,
				aliases, claims,
				sitelinks, revisionId);
	}

	@Override
	public ItemDocument withDescription(MonolingualTextValue newDescription) {
		Map<String, MonolingualTextValue> newDescriptions = new HashMap<>(descriptions);
		newDescriptions.put(newDescription.getLanguageCode(), newDescription);
		return new ItemDocumentImpl(getItemId(),
				labels, newDescriptions,
				aliases, claims,
				sitelinks, revisionId);
	}

	@Override
	public ItemDocument withAliases(String language, List<MonolingualTextValue> aliases) {
		Map<String, List<MonolingualTextValue>> newAliases = new HashMap<>(this.aliases);
		for(MonolingualTextValue alias : aliases) {
			Validate.isTrue(alias.getLanguageCode().equals(language));
		}
		newAliases.put(language, aliases);
		return new ItemDocumentImpl(getItemId(),
				labels, descriptions,
				newAliases, claims,
				sitelinks, revisionId);
	}

	@Override
	public ItemDocument withStatement(Statement statement) {
		Map<String, List<Statement>> newGroups = addStatementToGroups(statement, claims);
		return new ItemDocumentImpl(getItemId(),
				labels, descriptions,
				aliases, newGroups,
				sitelinks, revisionId);
	}

	@Override
	public ItemDocument withoutStatementIds(Set<String> statementIds) {
		Map<String, List<Statement>> newGroups = removeStatements(statementIds, claims);
		return new ItemDocumentImpl(getItemId(),
				labels, descriptions,
				aliases, newGroups,
				sitelinks, revisionId);
	}
}
