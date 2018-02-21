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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.AbstractTermedStatementDocument;
import org.wikidata.wdtk.datamodel.implementation.json.AliasesDeserializer;
import org.wikidata.wdtk.datamodel.implementation.json.JacksonPreStatement;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Abstract Jackson implementation of {@link TermedDocument}. Like all Jackson
 * objects, it is not technically immutable, but it is strongly recommended to
 * treat it as such in all contexts: the setters are for Jackson; never call
 * them in your code.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@Type(value = ItemDocumentImpl.class, name = TermedStatementDocumentImpl.JSON_TYPE_ITEM),
		@Type(value = PropertyDocumentImpl.class, name = TermedStatementDocumentImpl.JSON_TYPE_PROPERTY) })
public abstract class TermedStatementDocumentImpl extends
		AbstractTermedStatementDocument {

	/**
	 * String used to refer to items in JSON.
	 */
	public static final String JSON_TYPE_ITEM = "item";
	/**
	 * String used to refer to properties in JSON.
	 */
	public static final String JSON_TYPE_PROPERTY = "property";

	@JsonDeserialize(using = AliasesDeserializer.class)
	protected final Map<String, List<MonolingualTextValue>> aliases;
	
	@JsonDeserialize(contentAs=TermImpl.class)
	protected final Map<String, MonolingualTextValue> labels;
	@JsonDeserialize(contentAs=TermImpl.class)
	protected final Map<String, MonolingualTextValue> descriptions;

	/**
	 * This is what is called <i>claim</i> in the JSON model. It corresponds to
	 * the statement group in the WDTK model.
	 */
	private final Map<String, List<Statement>> claims;

	/**
	 * Statement groups. This member is initialized when statements are
	 * accessed.
	 */
	private List<StatementGroup> statementGroups;

	/**
	 * The id of the entity that the document refers to. This is not mapped to
	 * JSON directly by Jackson but split into two fields, "type" and "id". The
	 * type field is ignored during deserialization since the type is clear for
	 * a concrete document. For serialization, the type is hard-coded.
	 * <p>
	 * The site IRI, which would also be required to create a complete
	 * {@link EntityIdValue}, is not encoded in JSON. It needs to be injected
	 * from the outside (if not, we default to Wikidata).
	 */
	protected final String entityId;

	/**
	 * The site IRI that this document refers to, or null if not specified. In
	 * the latter case, we assume Wikidata as the default.
	 *
	 * @see EntityIdValue#getSiteIri()
	 */
	@JsonIgnore
	protected final String siteIri;

	/**
	 * The revision id of this document.
	 *
	 * @see EntityDocument#getRevisionId()
	 */
	@JsonProperty("lastrevid")
	protected long revisionId = 0;
	
	/**
	 * Constructor.
	 * 
	 * @param id
	 * 		the identifier of the subject of this document
	 * @param labels
	 * 		the labels for this entity, at most one per language
	 * @param descriptions
	 * 		the descriptions for this entity, at most one per language
	 * @param aliases
	 * 		the aliases for this language. Their relative order in a
	 * 		given language will be preserved.
	 * @param claims
	 * 		the statement groups contained in this document
	 * @param revisionId
	 * 		the id of the last revision of this document
	 */
	public TermedStatementDocumentImpl(
			EntityIdValue id,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> claims,
			long revisionId) {
		Validate.notNull(id);
		this.entityId = id.getId();
		this.siteIri = id.getSiteIri();
		if (labels != null) {
			this.labels = constructTermMap(labels);
		} else {
			this.labels = Collections.<String, MonolingualTextValue>emptyMap();
		}
		if (descriptions != null) {
			this.descriptions = constructTermMap(descriptions);
		} else {
			this.descriptions = Collections.<String, MonolingualTextValue>emptyMap();
		}
		if (aliases != null) {
			this.aliases = constructTermListMap(aliases);
		} else {
			this.aliases = Collections.<String, List<MonolingualTextValue>>emptyMap();
		}
		this.claims = new HashMap<>();
		if(claims != null) {
			for(StatementGroup group : claims) {
				EntityIdValue otherId = group.getSubject();
				otherId.getIri();
				Validate.isTrue(group.getSubject().equals(id), "Subject for the statement group and the document are different: "+otherId.toString()+" vs "+id.toString());
				this.claims.put(group.getProperty().getId(), group.getStatements());
			}
		}
		this.revisionId = revisionId;
	}

	/**
	 * Constructor used for JSON deserialization with Jackson.
	 */
	protected TermedStatementDocumentImpl(
			@JsonProperty("id") String jsonId,
			@JsonProperty("labels") Map<String, MonolingualTextValue> labels,
			@JsonProperty("descriptions") Map<String, MonolingualTextValue> descriptions,
			@JsonProperty("aliases") Map<String, List<MonolingualTextValue>> aliases,
			@JsonProperty("claims") Map<String, List<JacksonPreStatement>> claims,
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
		Validate.notNull(jsonId);
		this.entityId = jsonId;
		Validate.notNull(siteIri);
		this.siteIri = siteIri;
		if (labels != null) {
			this.labels = labels;
		} else {
			this.labels = Collections.<String, MonolingualTextValue>emptyMap();
		}
		if (descriptions != null) {
			this.descriptions = descriptions;
		} else {
			this.descriptions = Collections.<String, MonolingualTextValue>emptyMap();
		}
		if (aliases != null) {
			this.aliases = aliases;
		} else {
			this.aliases = Collections.<String, List<MonolingualTextValue>>emptyMap();
		}
		if (claims != null) {
			this.claims = new HashMap<String, List<Statement>>();
			EntityIdValue subject = this.getEntityId();
			for (Entry<String, List<JacksonPreStatement>> entry : claims
					.entrySet()) {
				List<Statement> statements = new ArrayList<>(entry.getValue().size());
				for (JacksonPreStatement statement : entry.getValue()) {
					statements.add(statement.withSubject(subject));
				}
				this.claims.put(entry.getKey(), statements);
			}
		} else {
			this.claims = Collections.<String,List<Statement>>emptyMap();
		}
		this.revisionId = revisionId;
	}


	@JsonProperty("aliases")
	@Override
	public Map<String, List<MonolingualTextValue>> getAliases() {
		// because of the typing provided by the interface one has to
		// re-create the map anew, simple casting is not possible
		Map<String, List<MonolingualTextValue>> returnMap = new HashMap<>();

		for (Entry<String, List<MonolingualTextValue>> entry : this.aliases
				.entrySet()) {
			returnMap.put(entry.getKey(), Collections
					.<MonolingualTextValue> unmodifiableList(entry.getValue()));
		}

		return Collections.unmodifiableMap(returnMap);
	}

	@JsonProperty("descriptions")
	@Override
	public Map<String, MonolingualTextValue> getDescriptions() {
		return Collections
				.<String, MonolingualTextValue> unmodifiableMap(this.descriptions);
	}

	@JsonProperty("labels")
	@Override
	public Map<String, MonolingualTextValue> getLabels() {
		return Collections
				.<String, MonolingualTextValue> unmodifiableMap(this.labels);
	}

	/**
	 * Returns the string id of the entity that this document refers to. Only
	 * for use by Jackson during serialization.
	 *
	 * @return string id
	 */
	@JsonInclude(Include.NON_EMPTY)
	@JsonProperty("id")
	public String getJsonId() {
		if (!EntityIdValue.SITE_LOCAL.equals(this.siteIri)) {
			return this.entityId;
		} else {
			return null;
		}
	}

	@JsonIgnore
	public String getSiteIri() {
		return this.siteIri;
	}

	@JsonIgnore
	@Override
	public List<StatementGroup> getStatementGroups() {
		if (this.statementGroups == null) {
			this.statementGroups = new ArrayList<>(this.claims.size());
			for (List<Statement> statements : this.claims.values()) {
				this.statementGroups
						.add(new StatementGroupImpl(statements));
			}
		}
		return this.statementGroups;
	}

	/**
	 * Returns the "claims". Only used by Jackson.
	 * <p>
	 * JSON "claims" correspond to statement groups in the WDTK model. You
	 * should use {@link ItemDocumentImpl#getStatementGroups()} to obtain
	 * this data.
	 *
	 * @return map of statement groups
	 */
	@JsonProperty("claims")
	public Map<String, List<Statement>> getJsonClaims() {
		return this.claims;
	}
	
	private static class NonZeroFilter {
		@Override
		public boolean equals(Object other) {
			return (other instanceof Long) && (long)other == 0;
		}
	}

	@Override
	@JsonInclude(value=Include.CUSTOM, valueFilter=NonZeroFilter.class)
	@JsonProperty("lastrevid")
	public long getRevisionId() {
		return this.revisionId;

	}
	
	protected static Map<String, MonolingualTextValue> constructTermMap(List<MonolingualTextValue> terms) {
		Map<String, MonolingualTextValue> map = new HashMap<>();
		for(MonolingualTextValue term : terms) {
			String language = term.getLanguageCode();
			if(map.containsKey(language)) {
				throw new IllegalArgumentException("Multiple terms provided for the same language.");
			}
			// We need to make sure the terms are of the right type, otherwise they will not
			// be serialized correctly.
			map.put(language, new TermImpl(term));
		}
		return map;
	}
	
	protected static Map<String, List<MonolingualTextValue>> constructTermListMap(List<MonolingualTextValue> terms) {
		Map<String, List<MonolingualTextValue>> map = new HashMap<>();
		for(MonolingualTextValue term : terms) {
			String language = term.getLanguageCode();
			// We need to make sure the terms are of the right type, otherwise they will not
			// be serialized correctly.
			TermImpl castTerm = new TermImpl(term);
			List<MonolingualTextValue> aliases = map.get(language);
			if(aliases == null) {
				aliases = new ArrayList<>();
				aliases.add(castTerm);
				map.put(language, aliases);
			} else {
				aliases.add(castTerm);
			}
		}
		return map;
	}

}
