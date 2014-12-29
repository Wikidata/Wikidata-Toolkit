package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonItemDocument;
import org.wikidata.wdtk.datamodel.json.jackson.JacksonTermedStatementDocument;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
 * Jackson implementation of {@link ItemIdValue}. So far this is the only kind
 * of {@link EntityIdValue} that can occur as a value of properties.
 *
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonValueItemId extends JacksonValue implements ItemIdValue {

	/**
	 * The parent document that this value is part of. This is needed since the
	 * site that this value refers to is not part of the JSON serialization of
	 * value, but is needed in WDTK to build {@link ItemIdValue} objects. Thus,
	 * it is necessary to set this information after each deserialization using
	 * {@link JacksonValueItemId#setParentDocument(JacksonItemDocument)}.
	 */
	@JsonIgnore
	JacksonTermedStatementDocument parentDocument;

	/**
	 * Inner helper object to store the actual data. Used to get the nested JSON
	 * structure that is required here.
	 */
	private JacksonInnerEntityId value;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonValueItemId() {
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

	@JsonIgnore
	@Override
	public String getEntityType() {
		return EntityIdValue.ET_ITEM;
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
}
