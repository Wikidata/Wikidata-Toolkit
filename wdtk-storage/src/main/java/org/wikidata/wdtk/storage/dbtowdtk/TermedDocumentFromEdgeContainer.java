package org.wikidata.wdtk.storage.dbtowdtk;

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

import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.DataObjectFactory;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedDocument;
import org.wikidata.wdtk.storage.datamodel.EdgeContainer.PropertyTargets;

public abstract class TermedDocumentFromEdgeContainer implements TermedDocument {

	final DataObjectFactory dataObjectFactory;

	final PropertyTargets labels;
	final PropertyTargets descriptions;
	final PropertyTargets aliases;

	public TermedDocumentFromEdgeContainer(PropertyTargets labels,
			PropertyTargets descriptions, PropertyTargets aliases,
			DataObjectFactory dataObjectFactory) {
		this.labels = labels;
		this.descriptions = descriptions;
		this.aliases = aliases;
		this.dataObjectFactory = dataObjectFactory;
	}

	@Override
	public Map<String, MonolingualTextValue> getLabels() {
		return WdtkFromDb.getMtvMapFromPropertyTargets(this.labels,
				this.dataObjectFactory);
	}

	@Override
	public Map<String, MonolingualTextValue> getDescriptions() {
		return WdtkFromDb.getMtvMapFromPropertyTargets(this.descriptions,
				this.dataObjectFactory);
	}

	@Override
	public Map<String, List<MonolingualTextValue>> getAliases() {
		return WdtkFromDb.getMtvListMapFromPropertyTargets(this.aliases,
				this.dataObjectFactory);
	}

}
