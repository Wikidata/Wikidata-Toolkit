package org.wikidata.wdtk.storage.wdtkbindings;

/*
 * #%L
 * Wikidata Toolkit Storage
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
import org.wikidata.wdtk.storage.datamodel.Sort;
import org.wikidata.wdtk.storage.datamodel.StringValue;

public class EntityValueAdaptor implements StringValue {

	final EntityIdValue entityIdValue;
	final WdtkAdaptorHelper helpers;

	public EntityValueAdaptor(EntityIdValue entityValue,
			WdtkAdaptorHelper helpers) {
		this.entityIdValue = entityValue;
		this.helpers = helpers;
	}

	@Override
	public Sort getSort() {
		return this.helpers.getEntitySort();
	}

	@Override
	public String getString() {
		return this.entityIdValue.getIri();
	}

}
