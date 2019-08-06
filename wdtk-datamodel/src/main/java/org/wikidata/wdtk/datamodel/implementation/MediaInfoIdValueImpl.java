package org.wikidata.wdtk.datamodel.implementation;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.wikidata.wdtk.datamodel.helpers.Equality;
import org.wikidata.wdtk.datamodel.helpers.Hash;
import org.wikidata.wdtk.datamodel.helpers.ToString;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MediaInfoIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

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
 * Jackson implementation of {@link MediaInfoIdValue}.
 *
 * @author Thomas Pellissier Tanon
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize()
public class MediaInfoIdValueImpl extends EntityIdValueImpl implements MediaInfoIdValue {

	/**
	 * Constructor.
	 *
	 * @param id
	 * 		the identifier of the entity, such as "M42"
	 * @param siteIri
	 *      the siteIRI that this value refers to
	 */
	MediaInfoIdValueImpl(
			String id,
			String siteIri) {
		super(id, siteIri);
		assertHasJsonEntityType(JSON_ENTITY_TYPE_MEDIA_INFO);
	}
	/**
	 * Constructor used for deserialization with Jackson.
	 *
	 * @param value
	 *     the inner JSON object deserialized as a {@link JacksonInnerEntityId}
	 * @param siteIri
	 *     the siteIRI that this value refers to.
	 */
	@JsonCreator
	MediaInfoIdValueImpl(
			@JsonProperty("value") JacksonInnerEntityId value,
			@JacksonInject("siteIri") String siteIri) {
		super(value, siteIri);
		assertHasJsonEntityType(JSON_ENTITY_TYPE_MEDIA_INFO);
	}

	@JsonIgnore
	@Override
	public String getEntityType() {
		return EntityIdValue.ET_MEDIA_INFO;
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
