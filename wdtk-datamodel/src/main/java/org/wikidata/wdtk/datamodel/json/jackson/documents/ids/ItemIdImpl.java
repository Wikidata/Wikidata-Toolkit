package org.wikidata.wdtk.datamodel.json.jackson.documents.ids;

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
 * Note that you have to differentiate between <b>ItemIdImpl</b> (this) and
 * <b>ItemIdValueImpl</b>. This class identifies an items id on the level of an
 * item document. The other one identifies an items id on the level of snak
 * values. They are different due to the resulting JSON being syntactically
 * different for these cases. Since the document ID's are only Strings in the
 * JSON, instances of this class will never appear in the exported JSON.
 * 
 * @author Fredo Erxleben
 *
 */
public class ItemIdImpl 
extends EntityIdImpl 
implements ItemIdValue {

	public ItemIdImpl() {}
	public ItemIdImpl(String id) {
		this.id = id;
	}

	@Override
	public String getEntityType() {
		return EntityIdValue.ET_ITEM;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof ItemIdImpl)) {
			return false;
		}

		return ((ItemIdImpl) o).getId().equalsIgnoreCase(this.id);
	}
	@Override
	public String getSiteIri() {
		// TODO Auto-generated method stub
		return null;
	}

}
