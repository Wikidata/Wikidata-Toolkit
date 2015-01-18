package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

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

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonTermedStatementDocument;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract base implementation of {@link EntityIdValue} for Jackson.
 *
 * @author Markus Kroetzsch
 *
 */
public abstract class JacksonValueEntityId extends JacksonValue implements
		EntityIdValue {

	/**
	 * The parent document that this value is part of. This is needed since the
	 * site that this value refers to is not part of the JSON serialization of
	 * value, but is needed in WDTK to build all current types of
	 * {@link EntityIdValue} objects. Thus, it is necessary to set this
	 * information after each deserialization using
	 * {@link JacksonValueEntityId#setParentDocument(JacksonTermedStatementDocument)}
	 * .
	 */
	@JsonIgnore
	JacksonTermedStatementDocument parentDocument;

	/**
	 * Inner helper object to store the actual data. Used to get the nested JSON
	 * structure that is required here.
	 */
	protected JacksonInnerEntityId value;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonValueEntityId() {
		super(JSON_VALUE_TYPE_ENTITY_ID);
	}

	/**
	 * Returns the inner value helper object. Only for use by Jackson during
	 * serialization.
	 *
	 * @return the inner entity id value
	 */
	public JacksonInnerEntityId getValue() {
		return value;
	}

	/**
	 * Sets the inner value helper object to the given value. Only for use by
	 * Jackson during deserialization.
	 *
	 * @param value
	 *            new value
	 */
	public void setValue(JacksonInnerEntityId value) {
		this.value = value;
	}

	@JsonIgnore
	@Override
	public String getIri() {
		return this.getSiteIri().concat(this.getId());
	}

	@JsonIgnore
	@Override
	public String getId() {
		return this.value.getStringId();
	}

	@JsonIgnore
	@Override
	public String getSiteIri() {
		if (this.parentDocument != null
				&& this.parentDocument.getSiteIri() != null) {
			return this.parentDocument.getSiteIri();
		} else {
			throw new RuntimeException(
					"Cannot access the site IRI id of an insufficiently initialised Jackson value.");
		}
	}

	/**
	 * Sets the parent document of this value to the given value. This document
	 * provides the value with information about its site IRI, which is not part
	 * of the JSON serialization of values. This method should only be used
	 * during deserialization.
	 *
	 * @param parentDocument
	 *            new value
	 */
	@JsonIgnore
	public void setParentDocument(JacksonTermedStatementDocument parentDocument) {
		this.parentDocument = parentDocument;
	}
}
