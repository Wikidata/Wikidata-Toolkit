package org.wikidata.wdtk.datamodel.implementation;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.FormIdValue;
import org.wikidata.wdtk.datamodel.interfaces.LexemeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

import java.util.regex.Pattern;

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

/**
 * Jackson implementation of {@link FormIdValue}.
 * TODO: It is not possible to use it as statement value yet.
 *
 * @author Thomas Pellissier Tanon
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize()
public class FormIdValueImpl extends ValueImpl implements FormIdValue {

	private static final Pattern PATTERN = Pattern.compile("^L[1-9]\\d*-F[1-9]\\d*$");

	private final String id;
	private final String siteIri;

	/**
	 * Constructor.
	 *
	 * @param id
	 * 		the identifier of the entity, such as "L42-F43"
	 * @param siteIri
	 *      the siteIRI that this value refers to
	 */
	FormIdValueImpl(
			String id,
			String siteIri) {
		super(JSON_VALUE_TYPE_ENTITY_ID);
		if(id == null || !PATTERN.matcher(id).matches()) {
			throw new IllegalArgumentException("The string " + id + " is not a valid form id");
		}
		this.id = id;
		Validate.notNull(siteIri);
		this.siteIri = siteIri;
	}

	/**
	 * Constructor used for deserialization with Jackson.
	 */
	@JsonCreator
	FormIdValueImpl(
			@JsonProperty("value") JacksonInnerEntityId value,
			@JacksonInject("siteIri") String siteIri) {
		this(value.getStringId(), siteIri);
	}

	@JsonIgnore
	@Override
	public String getEntityType() {
		return EntityIdValue.ET_FORM;
	}

	@JsonIgnore
	@Override
	public String getId() {
		return id;
	}

	@JsonIgnore
	@Override
	public String getSiteIri() {
		return siteIri;
	}

	@JsonIgnore
	@Override
	public String getIri() {
		return siteIri + id;
	}

	/**
	 * Returns the inner value helper object. Only for use by Jackson during
	 * serialization.
	 *
	 * @return the inner entity id value
	 */
	@JsonProperty("value")
	JacksonInnerEntityId getValue() {
		return new JacksonInnerEntityId(id);
	}

	@JsonIgnore
	@Override
	public LexemeIdValue getLexemeId() {
		return new LexemeIdValueImpl(id.substring(0, id.indexOf("-")), siteIri);
	}

	@Override
	public <T> T accept(ValueVisitor<T> valueVisitor) {
		return valueVisitor.visit(this);
	}

	@Override
	public int hashCode() {
		return Hash.hashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return Equality.equalsEntityIdValue(this, obj);
	}

	@Override
	public String toString() {
		return ToString.toString(this);
	}

	/**
	 * Helper object that represents the JSON object structure of the value.
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	static class JacksonInnerEntityId {

		private final String id;

		@JsonCreator
		JacksonInnerEntityId(
				@JsonProperty("id") String id
		) {
			this.id = id;
		}

		/**
		 * Returns the entity type string as used in JSON. Only for use by Jackson
		 * during serialization.
		 *
		 * @return the entity type string
		 */
		@JsonProperty("entity-type")
		String getJsonEntityType() {
			return "form";
		}

		/**
		 * Returns the standard string version of the entity id encoded in this
		 * value. For example, an id with entityType "item" and numericId "42" is
		 * normally identified as "Q42".
		 *
		 * @return the string id
		 */
		@JsonProperty("id")
		String getStringId() {
			return id;
		}
	}
}
