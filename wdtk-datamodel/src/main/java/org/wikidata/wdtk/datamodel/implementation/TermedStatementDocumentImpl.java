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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract Jackson implementation of {@link TermedDocument} and {@link StatementDocument}.
 * You should not rely on it directly but build instances with the Datamodel helper and
 * use {@link EntityDocumentImpl} for deserialization.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 * @author Thomas Pellissier Tanon
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ //TODO: drop in future release
		@Type(value = ItemDocumentImpl.class, name = EntityDocumentImpl.JSON_TYPE_ITEM),
		@Type(value = PropertyDocumentImpl.class, name = EntityDocumentImpl.JSON_TYPE_PROPERTY) })
public abstract class TermedStatementDocumentImpl extends StatementDocumentImpl implements TermedStatementDocument {

	protected final Map<String, List<MonolingualTextValue>> aliases;
	
	@JsonDeserialize(contentAs=TermImpl.class)
	protected final Map<String, MonolingualTextValue> labels;
	@JsonDeserialize(contentAs=TermImpl.class)
	protected final Map<String, MonolingualTextValue> descriptions;
	
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
		super(id, claims, revisionId);
		if (labels != null) {
			this.labels = constructTermMap(labels);
		} else {
			this.labels = Collections.emptyMap();
		}
		if (descriptions != null) {
			this.descriptions = constructTermMap(descriptions);
		} else {
			this.descriptions = Collections.emptyMap();
		}
		if (aliases != null) {
			this.aliases = constructTermListMap(aliases);
		} else {
			this.aliases = Collections.emptyMap();
		}
	}

	/**
	 * Constructor used for JSON deserialization with Jackson.
	 */
	TermedStatementDocumentImpl(
			@JsonProperty("id") String jsonId,
			@JsonProperty("labels") Map<String, MonolingualTextValue> labels,
			@JsonProperty("descriptions") Map<String, MonolingualTextValue> descriptions,
			@JsonProperty("aliases") Map<String, List<MonolingualTextValue>> aliases,
			@JsonProperty("claims") Map<String, List<StatementImpl.PreStatement>> claims,
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
		super(jsonId, claims, revisionId, siteIri);
		if (labels != null) {
			this.labels = labels;
		} else {
			this.labels = Collections.emptyMap();
		}
		if (descriptions != null) {
			this.descriptions = descriptions;
		} else {
			this.descriptions = Collections.emptyMap();
		}
		if (aliases != null) {
			this.aliases = aliases;
		} else {
			this.aliases = Collections.emptyMap();
		}
	}
	
	/**
	 * Protected constructor provided to ease the creation
	 * of copies. No check is made and each field is reused without
	 * copying.
	 * 
	 * @param labels
	 * 		a map from language codes to monolingual values with
	 * 	    the same language codes
	 * @param descriptions
	 * 		a map from language codes to monolingual values with
	 * 	    the same language codes 	    
	 * @param aliases
	 * 		a map from language codes to lists of monolingual values
	 *      with the same language codes
	 * @param statementGroups
	 * @param revisionId
	 */
	protected TermedStatementDocumentImpl(
			EntityIdValue subject,
			Map<String, MonolingualTextValue> labels,
			Map<String, MonolingualTextValue> descriptions,
			Map<String, List<MonolingualTextValue>> aliases,
			Map<String, List<Statement>> claims,
			long revisionId) {
		super(subject, claims, revisionId);
		this.labels = labels;
		this.descriptions = descriptions;
		this.aliases = aliases;
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
					. unmodifiableList(entry.getValue()));
		}

		return Collections.unmodifiableMap(returnMap);
	}

	@JsonProperty("descriptions")
	@Override
	public Map<String, MonolingualTextValue> getDescriptions() {
		return Collections.unmodifiableMap(this.descriptions);
	}

	@JsonProperty("labels")
	@Override
	public Map<String, MonolingualTextValue> getLabels() {
		return Collections.unmodifiableMap(this.labels);
	}

	@JsonIgnore
	public String getSiteIri() {
		return this.siteIri;
	}
	
	/**
	 * More efficient implementation of findStatementGroup than the
	 * default one provided in {@link AbstractTermedStatementDocument}
	 */
	@Override
	public StatementGroup findStatementGroup(PropertyIdValue propertyIdValue) {
		StatementGroup group = findStatementGroup(propertyIdValue.getId());
		if (group != null && group.getProperty().equals(propertyIdValue)) {
			return group;
		}
		return null;
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
			map.put(language, toTerm(term));
		}
		return map;
	}
	
	private static Map<String, List<MonolingualTextValue>> constructTermListMap(List<MonolingualTextValue> terms) {
		Map<String, List<MonolingualTextValue>> map = new HashMap<>();
		for(MonolingualTextValue term : terms) {
			String language = term.getLanguageCode();
			// We need to make sure the terms are of the right type, otherwise they will not
			// be serialized correctly.
			List<MonolingualTextValue> aliases = map.computeIfAbsent(language, (l) -> new ArrayList<>());
			aliases.add(toTerm(term));
		}
		return map;
	}

	/**
	 * We need to make sure the terms are of the right type, otherwise they will not be serialized correctly.
	 */
	private static MonolingualTextValue toTerm(MonolingualTextValue term) {
		return term instanceof TermImpl ? term : new TermImpl(term.getLanguageCode(), term.getText());
	}

	/**
	 * A deserializer implementation for the aliases in an
	 * {@link TermedStatementDocumentImpl}.
	 * <p>
	 * It implements a workaround to cope with empty aliases being represented as
	 * <code>"aliases":[]</code> despite its declaration as map and not as list or
	 * array. This is neither nice nor fast, and should be obsolete as soon as
	 * possible.
	 *
	 */
	static class AliasesDeserializer extends JsonDeserializer<Map<String, List<MonolingualTextValue>>> {

		@Override
		public Map<String, List<MonolingualTextValue>> deserialize(
				JsonParser jp, DeserializationContext ctxt) throws JsonMappingException {

			Map<String, List<MonolingualTextValue>> contents = new HashMap<>();

			try {
				JsonNode node = jp.getCodec().readTree(jp);
				if (!node.isArray()) {
					Iterator<Entry<String, JsonNode>> nodeIterator = node.fields();
					while (nodeIterator.hasNext()) {
						List<MonolingualTextValue> mltvList = new ArrayList<>();
						Entry<String, JsonNode> currentNode = nodeIterator.next();
						// get the list of MLTVs
						for (JsonNode mltvEntry : currentNode.getValue()) {
							String language = mltvEntry.get("language").asText();
							String value = mltvEntry.get("value").asText();
							mltvList.add(new TermImpl(language,value));
						}

						contents.put(currentNode.getKey(), mltvList);
					}
				}
			} catch (Exception e) {
				throw new JsonMappingException(jp, "Unexpected alias list serialization", e);
			}

			return contents;

		}
	}
	
	@Override
	abstract public TermedStatementDocument withRevisionId(long newRevisionId);
}
