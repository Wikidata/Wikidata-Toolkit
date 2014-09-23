package org.wikidata.wdtk.datamodel.json.jackson.documents;

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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonMonolingualTextValue;
import org.wikidata.wdtk.datamodel.json.jackson.serializers.AliasesDeserializer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = JacksonItemDocument.class, name = "item"),
		@Type(value = JacksonPropertyDocument.class, name = "property") })
public abstract class JacksonEntityDocument implements EntityDocument,
		TermedDocument {

	public static final String typeItem = "item";
	public static final String typeProperty = "property";

	@JsonDeserialize(using = AliasesDeserializer.class)
	protected Map<String, List<JacksonMonolingualTextValue>> aliases = new HashMap<>();
	protected Map<String, JacksonMonolingualTextValue> labels = new HashMap<>();
	protected Map<String, JacksonMonolingualTextValue> descriptions = new HashMap<>();

	// the following is not mapped directly towards JSON
	// rather split up into two JSON fields:
	// "type" and "id"
	// the type field in the JSON will be ignored.
	// for a concrete document the type is clear.
	// for writing out to external JSON there is a hard-coded solution
	@JsonIgnore
	protected EntityIdValue id;

	public JacksonEntityDocument() {
	}

	/**
	 * A constructor for generating ItemDocumentImpl-objects from other
	 * implementations that satisfy the ItemDocument-interface. This can be used
	 * for converting other implementations into this one for later export.
	 * 
	 * @param source
	 *            is the implementation to be used as a base.
	 */
	public JacksonEntityDocument(TermedDocument source) {

		// build id
		this.id = (EntityIdValue) source.getEntityId();

		// build aliases
		for (Entry<String, List<MonolingualTextValue>> mltvs : source
				.getAliases().entrySet()) {
			List<JacksonMonolingualTextValue> value = new LinkedList<>();
			for (MonolingualTextValue mltv : mltvs.getValue()) {
				value.add(new JacksonMonolingualTextValue(mltv));
			}
			this.aliases.put(mltvs.getKey(), value);
		}

		// build labels
		for (Entry<String, MonolingualTextValue> mltvs : source.getLabels()
				.entrySet()) {
			this.labels.put(mltvs.getKey(),
					new JacksonMonolingualTextValue(mltvs.getValue()));
		}
		// build descriptions
		for (Entry<String, MonolingualTextValue> mltvs : source
				.getDescriptions().entrySet()) {
			this.descriptions.put(mltvs.getKey(), new JacksonMonolingualTextValue(
					mltvs.getValue()));
		}
	}

	@JsonIgnore
	@Override
	public EntityIdValue getEntityId() {
		return this.id;
	}

	/**
	 * <b> Warning! </b> This is a hack to cope with empty aliases being
	 * represented as <code>"aliases":[]</code> despite its declaration as map
	 * and not as list or array.
	 * 
	 * @param aliases
	 */
	public void setAliases(Map<String, List<JacksonMonolingualTextValue>> aliases) {
		this.aliases = aliases;
	}

	@Override
	public Map<String, List<MonolingualTextValue>> getAliases() {

		// because of the typing provided by the interface one has to
		// re-create the map anew, simple casting is not possible
		Map<String, List<MonolingualTextValue>> returnMap = new HashMap<>();

		for (Entry<String, List<JacksonMonolingualTextValue>> entry : this.aliases
				.entrySet()) {
			List<MonolingualTextValue> mltvList = new LinkedList<>();
			mltvList.addAll(entry.getValue());
			returnMap.put(entry.getKey(), mltvList);
		}
		return returnMap;
	}

	public void setDescriptions(
			Map<String, JacksonMonolingualTextValue> descriptions) {
		this.descriptions = descriptions;
	}

	@Override
	public Map<String, MonolingualTextValue> getDescriptions() {

		// because of the typing provided by the interface one has to
		// re-create the map anew, simple casting is not possible
		Map<String, MonolingualTextValue> returnMap = new HashMap<>();
		returnMap.putAll(this.descriptions);
		return returnMap;
	}

	public void setLabels(Map<String, JacksonMonolingualTextValue> labels) {
		this.labels = labels;
	}

	@Override
	public Map<String, MonolingualTextValue> getLabels() {

		// because of the typing provided by the interface one has to
		// re-create the map anew, simple casting is not possible
		Map<String, MonolingualTextValue> returnMap = new HashMap<>();
		returnMap.putAll(this.labels);
		return returnMap;
	}

	@JsonProperty("id")
	public abstract void setId(String id);

	@JsonProperty("id")
	public String getId() {
		return this.id.getId();
	}

	/**
	 * This method is only used for handling the JSON export correctly.
	 * 
	 * @return either "item" or "property"
	 */
	@JsonProperty("type")
	public abstract String getType();

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

	@Override
	public abstract String toString();
}
