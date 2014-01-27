package org.wikidata.wdtk.datamodel.implementation;

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

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.wikidata.wdtk.datamodel.interfaces.PropertyId;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;

/**
 * Implementation of {@link ValueSnak}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ValueSnakImpl extends SnakImpl implements ValueSnak {

	final Value value;

	public ValueSnakImpl(PropertyId propertyId, Value value) {
		super(propertyId);
		Validate.notNull(value, "ValueSnak values cannot be null");
		this.value = value;
	}

	@Override
	public Value getValue() {
		return value;
	}

	public int hashCode() {
		return new HashCodeBuilder(997, 1013).append(value).append(propertyId)
				.toHashCode();
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof ValueSnak))
			return false;

		return this.propertyId.equals(((ValueSnak) obj).getPropertyId())
				&& this.value.equals(((ValueSnak) obj).getValue());
	}

}
