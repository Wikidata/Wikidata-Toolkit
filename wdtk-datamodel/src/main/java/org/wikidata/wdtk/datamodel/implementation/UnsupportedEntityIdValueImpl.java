package org.wikidata.wdtk.datamodel.implementation;

/*-
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2019 Wikidata Toolkit Developers
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
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.UnsupportedEntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Represents a entity id value of an unsupported type.
 * We can still "deserialize" it by just storing its
 * JSON representation, so that it can be serialized
 * back to its original representation.
 * This avoids parsing failures on documents containing
 * these values.
 * 
 * @author Antonin Delpeuch
 */
@JsonDeserialize()
public class UnsupportedEntityIdValueImpl extends ValueImpl implements UnsupportedEntityIdValue {
	
	private final JacksonIdValue value;
	private final String siteIri;

	@JsonCreator
	private UnsupportedEntityIdValueImpl(
			@JsonProperty("value")
			JacksonIdValue value,
			@JacksonInject("siteIri")
			String siteIri) {
		super(JSON_VALUE_TYPE_ENTITY_ID);
		this.value = value;
		this.siteIri = siteIri;
	}

	private static class JacksonIdValue {
		private final String entityType;
		private final String id;
		private final Map<String, JsonNode> contents;
		
		@JsonCreator
		private JacksonIdValue(
				@JsonProperty("entity-type")
				String entityType,
				@JsonProperty("id")
				String id) {
			Validate.notNull(id);
			this.entityType = entityType;
			this.id = id;
			contents = new HashMap<>();
		}
		
		@JsonProperty("entity-type")
		@JsonInclude(Include.NON_NULL)
		public String getEntityTypeString() {
			return entityType;
		}
		
		@JsonProperty("id")
		public String getId() {
			return id;
		}
		
		@JsonAnyGetter
		protected Map<String, JsonNode> getContents() {
			return contents;
		}
		
		@JsonAnySetter
		protected void loadContents(String key, JsonNode value) {
			this.contents.put(key, value);
		}
	}
	
	@Override
	public String toString() {
		return ToString.toString(this);
	}

	@Override
	@JsonIgnore
	public String getEntityType() {
		if (value.entityType == null) {
			return ET_UNSUPPORTED;
		}
		String[] parts = value.entityType.split("-");
		for(int i = 0; i < parts.length; i++) {
			parts[i] = StringUtils.capitalize(parts[i]);
		}
		return "http://www.wikidata.org/ontology#" + StringUtils.join(parts);
	}

	@Override
	@JsonIgnore
	public String getId() {
		return value.getId();
	}
	
	@JsonProperty("value")
	protected JacksonIdValue getInnerValue() {
		return value;
	}

	@Override
	@JsonIgnore
	public String getSiteIri() {
		return siteIri;
	}

	@Override
	@JsonIgnore
	public String getIri() {
		return siteIri.concat(value.getId());
	}
	
	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}
	
	@Override
	public boolean equals(Object other) {
		return Equality.equalsEntityIdValue(this, other);
	}
	
	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	@JsonIgnore
	public String getEntityTypeJsonString() {
		return value.getEntityTypeString();
	}
}
