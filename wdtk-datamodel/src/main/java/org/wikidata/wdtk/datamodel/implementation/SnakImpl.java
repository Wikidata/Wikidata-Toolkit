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

import java.io.Serializable;

import org.apache.commons.lang3.Validate;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;

/**
 * Implementation of {@link Snak}.
 * 
 * @author Markus Kroetzsch
 * 
 */
public abstract class SnakImpl implements Snak, Serializable {

	private static final long serialVersionUID = 7513457794344946061L;
	
	final PropertyIdValue propertyId;

	SnakImpl(PropertyIdValue propertyId) {
		Validate.notNull(propertyId, "Snak property ids cannot be null");
		this.propertyId = propertyId;
	}

	@Override
	public PropertyIdValue getPropertyId() {
		return propertyId;
	}

}
