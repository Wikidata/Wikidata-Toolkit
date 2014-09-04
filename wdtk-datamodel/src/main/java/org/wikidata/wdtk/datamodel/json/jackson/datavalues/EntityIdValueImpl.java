package org.wikidata.wdtk.datamodel.json.jackson.datavalues;

import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
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

@JsonIgnoreProperties(ignoreUnknown = true)
public class EntityIdValueImpl extends ValueImpl implements EntityIdValue {

	private EntityId value;

	public EntityIdValueImpl() {
		super(typeEntity);
	}

	public EntityIdValueImpl(EntityId value) {
		super(typeEntity);
		this.value = value;
	}

	public EntityId getValue() {
		return value;
	}

	public void setValue(EntityId value) {
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

	@JsonIgnore
	@Override
	public String getEntityType() {
		return this.value.getDatamodelEntityType();
	}

}
