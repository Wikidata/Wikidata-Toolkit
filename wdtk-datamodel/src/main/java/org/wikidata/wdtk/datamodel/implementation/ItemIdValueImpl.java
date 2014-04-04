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

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;

/**
 * Generic implementation of {@link ItemIdValue} that works with arbitrary
 * Wikibase instances: it requires a baseIri that identifies the site globally.
 * 
 * @author Markus Kroetzsch
 * 
 */
public class ItemIdValueImpl extends EntityIdValueImpl implements ItemIdValue {

	/**
	 * Constructor.
	 * 
	 * @see EntityIdValueImpl#EntityIdImpl(String, String)
	 * @param id
	 *            a string of the form Qn... where n... is the string
	 *            representation of a positive integer number
	 * @param baseIri
	 *            the first part of the entity IRI of the site this belongs to,
	 *            e.g., "http://www.wikidata.org/entity/"
	 */
	ItemIdValueImpl(String id, String baseIri) {
		super(id, baseIri);

		if (!id.matches("^Q[1-9][0-9]*$")) {
			throw new IllegalArgumentException(
					"Wikibase item ids must have the form \"Q<positive integer>\"");
		}
	}

	@Override
	public String getEntityType() {
		return EntityIdValue.ET_ITEM;
	}
	
	@Override
	public String toString(){
		return "(ItemId)" + this.baseIri +"/"+ this.id;
	}
}
