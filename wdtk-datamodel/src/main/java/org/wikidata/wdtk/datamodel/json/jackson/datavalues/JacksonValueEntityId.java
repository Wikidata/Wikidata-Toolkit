package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

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
 * Jackson implementation of {@link EntityIdValue}.
 * <p>
 * TODO The deserialization should create instances of {@link ItemIdValue} (an
 * maybe others if ever supported). Maybe everything should use only this type
 * of object for now.
 * <p>
 * TODO Implementation of toString() is missing ({@link ToString} method not
 * supported for entity id values).
 *
 * @author Fredo Erxleben
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JacksonValueEntityId extends JacksonValue implements EntityIdValue {

	/**
	 * Inner helper object to store the actual data. Used to get the nested JSON
	 * structure that is required here.
	 */
	private JacksonInnerEntityId value;

	/**
	 * Constructor. Creates an empty object that can be populated during JSON
	 * deserialization. Should only be used by Jackson for this very purpose.
	 */
	public JacksonValueEntityId() {
		super(JSON_VALUE_TYPE_ENTITY_ID);
	}

	/**
	 * Creates a new object from the given data.
	 *
	 * TODO Review the utility of this constructor. A copy constructor would
	 * seem more useful.
	 *
	 * @param value
	 */
	public JacksonValueEntityId(JacksonInnerEntityId value) {
		super(JSON_VALUE_TYPE_ENTITY_ID);
		this.value = value;
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
		return value.getStringId();
	}

	@JsonIgnore
	@Override
	public String getSiteIri() {
		// FIXME returns fixed site IRI for now
		return "http://www.wikidata.org/entity/";
	}

	@JsonIgnore
	@Override
	public String getEntityType() {
		return this.value.getDatamodelEntityType();
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

}
