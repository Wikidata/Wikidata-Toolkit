package org.wikidata.wdtk.datamodel.implementation;

import java.util.List;
import java.util.Map;

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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Jackson implementation of {@link PropertyDocument}.
 *
 * @author Fredo Erxleben
 * @author Antonin Delpeuch
 * @author Markus Kroetzsch
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PropertyDocumentImpl extends TermedStatementDocumentImpl
		implements PropertyDocument {

	/**
	 * Datatype of the property. This is internally stored as 
	 * a Jackson object because we need to be able to serialize
	 * it directly to JSON as a field.
	 */
	private final DatatypeIdImpl datatype;
	
	/**
	 * Constructor for instances that are built manually, rather than from JSON.
	 * 
	 * @param id
	 *            the id of the property that data is about
	 * @param labels
	 *            the list of labels of this property, with at most one label
	 *            for each language code
	 * @param descriptions
	 *            the list of descriptions of this property, with at most one
	 *            description for each language code
	 * @param aliases
	 *            the list of aliases of this property
	 * @param statements
	 *            the list of statement groups of this item; all of them must
	 *            have the given itemIdValue as their subject
	 * @param datatype
	 *            the datatype of that property
	 * @param revisionId
	 *            the revision ID or 0 if not known; see
	 *            {@link EntityDocument#getRevisionId()}
	 */
	public PropertyDocumentImpl(
			PropertyIdValue id,
			List<MonolingualTextValue> labels,
			List<MonolingualTextValue> descriptions,
			List<MonolingualTextValue> aliases,
			List<StatementGroup> statements,
			DatatypeIdValue datatype,
			long revisionId) {
		super(id, labels, descriptions, aliases, statements, revisionId);
		this.datatype = new DatatypeIdImpl(datatype);
	}

	/**
	 * Constructor. Creates an instance by deserializing from JSON.
	 */
	@JsonCreator
	public PropertyDocumentImpl(
			@JsonProperty("id") String jsonId,
			@JsonProperty("labels") @JsonDeserialize(contentAs=TermImpl.class) Map<String, MonolingualTextValue> labels,
			@JsonProperty("descriptions") @JsonDeserialize(contentAs=TermImpl.class) Map<String, MonolingualTextValue> descriptions,
			@JsonProperty("aliases") @JsonDeserialize(using = AliasesDeserializer.class) Map<String, List<MonolingualTextValue>> aliases,
			@JsonProperty("claims") Map<String, List<StatementImpl.PreStatement>> claims,
			@JsonProperty("datatype") String datatype,
			@JsonProperty("lastrevid") long revisionId,
			@JacksonInject("siteIri") String siteIri) {
        super(jsonId, labels, descriptions, aliases, claims, revisionId, siteIri);
        this.datatype = new DatatypeIdImpl(DatatypeIdImpl.getDatatypeIriFromJsonDatatype(datatype));
    }

    /**
	 * Private constructor, meant to be used to create modified copies
	 * of instances.
	 */
	private PropertyDocumentImpl(
			PropertyIdValue id,
			Map<String, MonolingualTextValue> labels,
			Map<String, MonolingualTextValue> descriptions,
			Map<String, List<MonolingualTextValue>> aliases,
			Map<String, List<Statement>> claims,
			DatatypeIdValue datatypeId, 
			long revisionId) {
		super(id, labels, descriptions, aliases, claims, revisionId);
		this.datatype = new DatatypeIdImpl(datatypeId);
	}

	/**
	 * Returns the JSON string version of the property's datatype. Note that
	 * {@link #getDatatype()} is already used for another function of the
	 * interface.
	 *
	 * @return string datatype
	 */
	@JsonProperty("datatype")
	public String getJsonDatatype() {
		return this.datatype.getJsonString();
    }

	@JsonIgnore
	@Override
	public PropertyIdValue getPropertyId() {
		return getEntityId();
	}

	@JsonIgnore
	@Override
	public PropertyIdValue getEntityId() {
		return new PropertyIdValueImpl(this.entityId, this.siteIri);
	}

	@JsonIgnore
	@Override
	public DatatypeIdValue getDatatype() {
		return new DatatypeIdImpl(this.datatype);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsPropertyDocument(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}
	
	@Override
	public PropertyDocument withRevisionId(long newRevisionId) {
		return new PropertyDocumentImpl(getEntityId(),
				labels,	descriptions,
				aliases, claims,
				datatype, newRevisionId);
	}

	@Override
	public PropertyDocument withLabel(MonolingualTextValue newLabel) {
		Map<String, MonolingualTextValue> newLabels = new HashMap<>(labels);
		newLabels.put(newLabel.getLanguageCode(), newLabel);
		return new PropertyDocumentImpl(getEntityId(),
				newLabels, descriptions,
				aliases, claims,
				datatype, revisionId);
	}

	@Override
	public PropertyDocument withDescription(MonolingualTextValue newDescription) {
		Map<String, MonolingualTextValue> newDescriptions = new HashMap<>(descriptions);
		newDescriptions.put(newDescription.getLanguageCode(), newDescription);
		return new PropertyDocumentImpl(getEntityId(),
				labels, newDescriptions,
				aliases, claims,
				datatype, revisionId);
	}

	@Override
	public PropertyDocument withAliases(String language, List<MonolingualTextValue> aliases) {
		Map<String, List<MonolingualTextValue>> newAliases = new HashMap<>(this.aliases);
		for(MonolingualTextValue alias : aliases) {
			Validate.isTrue(alias.getLanguageCode().equals(language));
		}
		newAliases.put(language, aliases);
		return new PropertyDocumentImpl(getEntityId(),
				labels, descriptions,
				newAliases, claims,
				datatype, revisionId);
	}

	@Override
	public PropertyDocument withStatement(Statement statement) {
		Map<String, List<Statement>> newGroups = addStatementToGroups(statement, claims);
		return new PropertyDocumentImpl(getEntityId(),
				labels, descriptions,
				aliases, newGroups,
				datatype, revisionId);
	}

	@Override
	public PropertyDocument withoutStatementIds(Set<String> statementIds) {
		Map<String, List<Statement>> newGroups = removeStatements(statementIds, claims);
		return new PropertyDocumentImpl(getEntityId(),
				labels, descriptions,
				aliases, newGroups,
				datatype, revisionId);
	}
}
