package org.wikidata.wdtk.datamodel.json.jackson;

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

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.util.NestedIterator;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@Type(value = JacksonItemDocument.class, name = JacksonTermedStatementDocument.JSON_TYPE_ITEM),
		@Type(value = JacksonPropertyDocument.class, name = JacksonTermedStatementDocument.JSON_TYPE_PROPERTY) })
public abstract class JacksonTermedStatementDocument implements TermedDocument,
		StatementDocument {

	/**
	 * String used to refer to items in JSON.
	 */
	public static final String JSON_TYPE_ITEM = "item";
	/**
	 * String used to refer to properties in JSON.
	 */
	public static final String JSON_TYPE_PROPERTY = "property";

	@JsonDeserialize(using = AliasesDeserializer.class)
	protected Map<String, List<JacksonMonolingualTextValue>> aliases = new HashMap<>();
	protected Map<String, JacksonMonolingualTextValue> labels = new HashMap<>();
	protected Map<String, JacksonMonolingualTextValue> descriptions = new HashMap<>();

	/**
	 * This is what is called <i>claim</i> in the JSON model. It corresponds to
	 * the statement group in the WDTK model.
	 */
	private Map<String, List<JacksonStatement>> claims = new HashMap<>();

	/**
	 * Statement groups. This member is initialized when statements are
	 * accessed.
	 */
	private List<StatementGroup> statementGroups = null;

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
	protected String entityId = "";

	/**
	 * The site IRI that this document refers to, or null if not specified. In
	 * the latter case, we assume Wikidata as the default.
	 *
	 * @see EntityIdValue#getSiteIri()
	 */
	@JsonIgnore
	protected String siteIri = null;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonTermedStatementDocument() {
	}

	/**
	 * Sets the aliases to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param aliases
	 *            new value
	 */
	public void setAliases(
			Map<String, List<JacksonMonolingualTextValue>> aliases) {
		this.aliases = aliases;
	}

	@Override
	public Map<String, List<MonolingualTextValue>> getAliases() {
		// because of the typing provided by the interface one has to
		// re-create the map anew, simple casting is not possible
		Map<String, List<MonolingualTextValue>> returnMap = new HashMap<>();

		for (Entry<String, List<JacksonMonolingualTextValue>> entry : this.aliases
				.entrySet()) {
			returnMap.put(entry.getKey(), Collections
					.<MonolingualTextValue> unmodifiableList(entry.getValue()));
		}

		return Collections.unmodifiableMap(returnMap);
	}

	/**
	 * Sets the descriptions to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param descriptions
	 *            new value
	 */
	public void setDescriptions(
			Map<String, JacksonMonolingualTextValue> descriptions) {
		this.descriptions = descriptions;
	}

	@Override
	public Map<String, MonolingualTextValue> getDescriptions() {
		return Collections
				.<String, MonolingualTextValue> unmodifiableMap(this.descriptions);
	}

	/**
	 * Sets the labels to the given value. Only for use by Jackson during
	 * deserialization.
	 *
	 * @param labels
	 *            new value
	 */
	public void setLabels(Map<String, JacksonMonolingualTextValue> labels) {
		this.labels = labels;
	}

	@Override
	public Map<String, MonolingualTextValue> getLabels() {
		return Collections
				.<String, MonolingualTextValue> unmodifiableMap(this.labels);
	}

	/**
	 * Sets the string id of the entity that this document refers to. Only for
	 * use by Jackson during deserialization.
	 *
	 * @param id
	 *            new value
	 */
	@JsonProperty("id")
	public void setJsonId(String id) {
		this.entityId = id;
	}

	/**
	 * Returns the string id of the entity that this document refers to. Only
	 * for use by Jackson during serialization.
	 *
	 * @return string id
	 */
	@JsonProperty("id")
	public String getJsonId() {
		return this.entityId;
	}

	/**
	 * Sets the site iri to the given value. This can be used to inject
	 * information about the site the object belongs to after the object is
	 * constructed. This is needed since this information is not part of the
	 * JSON serialization.
	 *
	 * @see EntityIdValue#getSiteIri()
	 * @param siteIri
	 *            the site IRI
	 */
	@JsonIgnore
	public void setSiteIri(String siteIri) {
		this.siteIri = siteIri;

		EntityIdValue subject = this.getEntityId();

		for (Entry<String, List<JacksonStatement>> entry : this.claims
				.entrySet()) {
			for (JacksonStatement statement : entry.getValue()) {
				statement.setSubject(subject);
			}
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
			for (List<JacksonStatement> statements : this.claims.values()) {
				this.statementGroups
						.add(new StatementGroupFromJson(statements));
			}
		}
		return this.statementGroups;
	}

	/**
	 * Sets the "claims" to the given value. Only for use by Jackson during
	 * deserialization.
	 * <p>
	 * The name refers to the JSON model, where claims are similar to statement
	 * groups. This should not be confused with claims as used in the WDTK data
	 * model. This will probably only be used by the Jacksons' ObjectMapper.
	 *
	 * @param claims
	 */
	@JsonProperty("claims")
	public void setJsonClaims(Map<String, List<JacksonStatement>> claims) {
		this.claims = claims;
		this.statementGroups = null; // clear cache
	}

	/**
	 * Returns the "claims". Only used by Jackson.
	 * <p>
	 * JSON "claims" correspond to statement groups in the WDTK model. You
	 * should use {@link JacksonItemDocument#getStatementGroups()} to obtain
	 * this data.
	 *
	 * @return map of statement groups
	 */
	@JsonProperty("claims")
	public Map<String, List<JacksonStatement>> getJsonClaims() {
		return this.claims;
	}

	@Override
	@JsonIgnore
	public Iterator<Statement> getAllStatements() {
		return new NestedIterator<>(this.getStatementGroups());
	}

}
